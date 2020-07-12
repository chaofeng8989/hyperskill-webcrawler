import java.io.BufferedReader;
import java.io.InputStreamReader;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here

        String in = reader.readLine().trim();
        if (in.isEmpty()) {
            System.out.print(0);
            return;
        }
        in = in.replaceAll("\\s+", " ");
        String[] inArray = in.split(" ");
        System.out.print(inArray.length);
        reader.close();
    }
}