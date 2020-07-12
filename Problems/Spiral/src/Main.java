import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] array = new int[n][n];
        int rowL = 0, rowR = n-1, colL = 0, colR = n-1;
        int x = 1;
        while (rowL <= rowR && colL <= colR) {
            for (int j = colL; j <= colR; j++) {
                array[rowL][j] = x++;
            }
            rowL++;
            for (int i = rowL; i <= rowR; i++) {
                array[i][colR] = x++;
            }
            colR--;
            for (int j = colR; j >= colL; j--) {
                array[rowR][j] = x++;
            }
            rowR--;
            for (int i = rowR; i >= rowL; i--) {
                array[i][colL] = x++;
            }
            colL++;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }
}