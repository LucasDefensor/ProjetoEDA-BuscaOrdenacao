package ordenacao;

/**
 * InsertionSort. Estavel, in-place, O(n^2) no pior/medio caso e O(n) no melhor.
 */
public class InsertionSort {

    public static void sort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int chave = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > chave) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = chave;
        }
    }
}
