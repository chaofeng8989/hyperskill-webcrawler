import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String text = scanner.nextLine();
        Pattern pattern = Pattern.compile("password[\\s|:]*(\\w*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        boolean found = false;
        while (matcher.find()) {
            found = true;
            String pw = matcher.group(1);
            System.out.println(pw);
        }
        if (!found) System.out.println("No passwords found.");
        // write your code here
    }
}