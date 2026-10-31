package ordenacao;

/**
 * CountingSort. Estavel (na versao com vetor de saida), O(n + k) tempo e
 * O(n + k) memoria auxiliar, onde k = max - min + 1 (aqui k <= 100.000).
 *
 * Usa a versao classica com somas acumuladas e preenchimento de tras para frente,
 * que e a que preserva a estabilidade.
 */
public class CountingSort {

    public static void sort(int[] a) {
        if (a.length < 2) {
            return;
        }
        int min = a[0];
        int max = a[0];
        for (int v : a) {
            if (v < min) min = v;
            if (v > max) max = v;
        }
        int[] cont = new int[max - min + 1];
        for (int v : a) {
            cont[v - min]++;
        }
        for (int i = 1; i < cont.length; i++) {
            cont[i] += cont[i - 1];
        }
        int[] saida = new int[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            saida[--cont[a[i] - min]] = a[i];
        }
        System.arraycopy(saida, 0, a, 0, a.length);
    }
}
