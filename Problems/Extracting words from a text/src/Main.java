import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String text = scanner.nextLine();
        Pattern pattern = Pattern.compile("\\w*program\\w*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        // write your code here
        while (matcher.find()) {
            int start = matcher.start();
            String word = matcher.group();
            System.out.println((start) + " " + word);
        }
    }
}