import java.util.*;
import java.util.stream.Collectors;

public class Main {

    static void changeList(List<String> list) {
        // write your code here
        String longest = list.stream().reduce((a,b) -> {
            return a.length() > b.length()? a : b;
        }).orElse(null);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, longest);
        }

    }


    /* Do not change code below */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        List<String> lst = Arrays.asList(s.split(" "));
        changeList(lst);
        lst.forEach(e -> System.out.print(e + " "));
    }
}