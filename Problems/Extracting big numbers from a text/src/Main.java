import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        String stringWithNumbers = scanner.nextLine();
        Pattern pattern = Pattern.compile("\\d{10,}");
        Matcher matcher = pattern.matcher(stringWithNumbers);
        while (matcher.find()) {
            String x = matcher.group();
            System.out.println(x + ":" + x.length());
        }
        // write your code here
    }
}
