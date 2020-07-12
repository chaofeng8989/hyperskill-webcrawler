package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebCrawler extends JFrame {
    Object[][] linkTitle = null;
    JLabel urlLabel;
    JTextField urlTextField;
    JButton runButton;
    JScrollPane scrollPane;
    JTable links;
    DefaultTableModel defaultTableModel;
    JLabel titleLabel;
    JLabel title;
    JLabel exportLabel;
    JTextField exportField;
    JButton exportButton;
    public WebCrawler() {
        componentInit();
    }
    private void componentInit() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("WebCrawler");
        urlLabel = new JLabel();
        urlTextField = new JTextField();
        runButton = new JButton();
        titleLabel = new JLabel();
        title = new JLabel();
        links = new JTable();
        exportButton = new JButton();
        exportField = new JTextField();
        exportLabel = new JLabel();
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

        urlLabel.setName("URLLabel");
        exportField.setName("ExportUrlTextField");
        exportButton.setName("ExportButton");
        jpanel.setName("HtmlTextArea");
        //jpanel.setLayout(new GridBagLayout());
        urlTextField.setName("UrlTextField");
        runButton.setName("RunButton");
        titleLabel.setName("TitleLabel");
        title.setName("Title");

        setSize(800, 1000);
        urlLabel.setBounds(20, 20, 70, 30);
        urlTextField.setBounds(100, 20, 500, 30);
        runButton.setBounds(650,20,100,30);
        title.setBounds(30, 60, 100, 30);
        titleLabel.setBounds(150, 60, 400, 30);
        jpanel.setBounds(00,90,800,800);
        scrollPane.setBounds(10,10,750,750);
        exportLabel.setBounds(30, 900, 80, 30);
        exportField.setBounds(130, 900,500,30);
        exportButton.setBounds(650, 900, 100, 30);
        //scrollPane.setSize(750,1000);
        //jpanel.setBackground(Color.BLACK);
        jpanel.setLayout(new BorderLayout());
        add(urlLabel);
        add(exportButton);
        add(exportField);
        add(exportLabel);
        add(urlTextField);
        add(runButton);
        add(jpanel);
        add(titleLabel);
        add(title);
        //scrollPane.disable();
        links.disable();
        setLayout(null);
        urlLabel.setText("URL");
        title.setText("Title : ");
        exportButton.setText("Save");
        exportLabel.setText("Export: ");
        runButton.setText("Parse!");
//        title.setText("Title : ");
        //setLayout(urlTextField,runButton,scrollPane);
        runButton.addActionListener(new ButtonClickListener());
        exportButton.addActionListener(new SaveListener());
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
            linkTitle = getLinkAndTitle(initURL, topDomain);
            for (int count = 0; count < linkTitle.length; count++) {
                defaultTableModel.addRow(new Object[] { linkTitle[count][0], linkTitle[count][1]});
            }
        }
    }
    private class SaveListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (linkTitle == null) return;
            String savePath = exportField.getText();
            System.out.println("save to :"+savePath);
            Path file = Paths.get(savePath);
            List<String> list = new LinkedList<>();
            for (Object[] line : linkTitle) {
                list.add((String)line[0]);
                list.add((String)line[1]);

            }
            try {
                Files.write(file, list, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
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
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
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