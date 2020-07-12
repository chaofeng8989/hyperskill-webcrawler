package crawler;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {

    JTextField urlTextField;
    JButton runButton;
    JScrollPane scrollPane;
    JTable links;
    DefaultTableModel defaultTableModel;
    JLabel titleLabel;
    JLabel title;
    public WebCrawler() {
        componentInit();
    }
    private void componentInit() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("WebCrawler");
        urlTextField = new JTextField();
        runButton = new JButton();
        titleLabel = new JLabel();
        title = new JLabel();
        links = new JTable();
        scrollPane = new JScrollPane(links);
        JPanel jpanel = new JPanel();
        jpanel.add(scrollPane);

        defaultTableModel = new DefaultTableModel(0,0);
        String[] header = new String[]{"URL", "Title"};
        links.setName("TitlesTable");
        defaultTableModel.setColumnIdentifiers(header);
        links.getTableHeader().setPreferredSize(new Dimension(scrollPane.getWidth(), 25));
        links.setRowHeight(25);
        links.setModel(defaultTableModel);
        links.setGridColor(Color.BLACK);

        jpanel.setName("HtmlTextArea");
        //jpanel.setLayout(new GridBagLayout());
        urlTextField.setName("UrlTextField");
        runButton.setName("RunButton");
        titleLabel.setName("TitleLabel");
        title.setName("Title");
        title.setText("Title : ");
        setSize(800, 1200);
        urlTextField.setBounds(20, 20, 600, 30);
        runButton.setBounds(650,20,100,30);
        title.setBounds(30, 60, 100, 30);
        titleLabel.setBounds(150, 60, 400, 30);
        jpanel.setBounds(00,90,800,1150);
        scrollPane.setBounds(10,10,750,1120);
        //scrollPane.setSize(750,1000);
        //jpanel.setBackground(Color.BLACK);
        jpanel.setLayout(new BorderLayout());
        add(urlTextField);
        add(runButton);
        add(jpanel);
        add(titleLabel);
        add(title);
        //scrollPane.disable();
        links.disable();
        setLayout(null);

        runButton.setText("Get text!");
//        title.setText("Title : ");
        //setLayout(urlTextField,runButton,scrollPane);
        runButton.addActionListener(new ButtonClickListener());
        //scrollPane.setText("HTML code?");
        setVisible(true);
    }
    class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(ApplicationRunner.DEBUG) System.out.println(urlTextField.getText());
            URL initURL = null, topDomain = null;
            try {
                String top = null;
                String firstPage = urlTextField.getText();
                Pattern pattern = Pattern.compile("\\/\\/([^\\/]+)");
                Matcher matcher = pattern.matcher(firstPage);
                
                if (matcher.find()) top =  firstPage.substring(0, matcher.end());
                initURL = new URL(firstPage);
                topDomain = new URL(top);
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            }
            String siteContent = getSiteContent(initURL);
            String mainTitle = getTitle(siteContent);
            titleLabel.setText(mainTitle);
            defaultTableModel.setRowCount(0);
            Object[][] linkTitle = getLinkAndTitle(initURL, topDomain);
            for (int count = 0; count < linkTitle.length; count++) {
                defaultTableModel.addRow(new Object[] { linkTitle[count][0], linkTitle[count][1]});
            }
        }
    }

    private Object[][] getLinkAndTitle(URL firstPageURL, URL topDomain) {

        Map<String, String> map = new HashMap<>();

        Pattern pattern = Pattern.compile("<a.*href=['\"]([^'\"]*)['\"]");
        dfs(firstPageURL, map, pattern, 1, topDomain);
        Object[][] data = new Object[map.size()][2];
        if(ApplicationRunner.DEBUG) System.out.println(map);
        int i = 0;
        for (Map.Entry<String, String> e : map.entrySet()) {
            data[i][0] = e.getKey();
            data[i++][1] = e.getValue();
        }
        return data;
    }

    private void dfs(URL url, Map<String, String> map, Pattern pattern, int depth, URL topDomain) {
        if (depth < 0) return;
        if(ApplicationRunner.DEBUG) System.out.println("URL =>>>>>>" +url);
        String siteString = getSiteContent(url);
//        if(ApplicationRunner.DEBUG) System.out.println(siteString);
        if (siteString == null) return;
        String title = getTitle(siteString);
        if(ApplicationRunner.DEBUG) System.out.println("TITLE====>>>>" + title);
        map.put(url.toString(), title);
        Matcher matcher = pattern.matcher(siteString);
        Set<URL> children = new HashSet<>();
        while (matcher.find()) {
            String nextUrl = matcher.group(1);
            if(ApplicationRunner.DEBUG) System.out.print("find href : " + nextUrl +"*****");
            if (nextUrl.isEmpty()) continue;

            if (nextUrl.startsWith("//")) {
                nextUrl = "http:" + nextUrl;
            } else if (nextUrl.startsWith("/")){
                nextUrl = topDomain + nextUrl;
            } else if (!nextUrl.startsWith("http")) {
                nextUrl = topDomain + "/"+ nextUrl;
            }
            try {
                URL childURL = new URL(nextUrl);
                children.add(childURL);
                if(ApplicationRunner.DEBUG) System.out.println("change to >>>>>" +childURL.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if(ApplicationRunner.DEBUG) System.out.println(children);
        for (URL childURL : children) {
            dfs(childURL, map, pattern, depth - 1, topDomain);
        }
    }
    private String getTitle(String text) {
        Pattern pattern = Pattern.compile("<title.*>(.*)<\\/title>");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group(1);
        return null;
    }
    private String getSiteContent(URL siteURL) {
        try {
            URLConnection connection = siteURL.openConnection();
            String contentType = connection.getContentType();

            if(ApplicationRunner.DEBUG) System.out.println("TYPE>>>>>>>"+contentType);
            if (contentType!= null && contentType.contains("text/html")) {
                InputStream inputStream = connection.getInputStream();
                String siteText = read(inputStream);
                if(ApplicationRunner.DEBUG) System.out.println(siteText);

                return siteText;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception allE) {
            allE.printStackTrace();
            return null;
        }
        return null;
    }
    private String read(InputStream stream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        InputStreamReader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        int charsRead;
        while((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
            out.append(buffer, 0, charsRead);
        }
        return out.toString();
    }
}