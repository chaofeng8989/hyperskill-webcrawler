import java.io.*;

class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // start coding here
        StringBuilder sb = new StringBuilder(reader.readLine());
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
        sb.reverse();
        reader.close();
        writer.write(sb.toString());
        writer.close();
    }
}