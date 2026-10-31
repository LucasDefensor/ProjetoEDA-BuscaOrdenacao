package ordenacao;

/**
 * SelectionSort. Sempre O(n^2) comparacoes, inclusive em vetor ordenado.
 */
public class SelectionSort {

    /** Versao "do slide": troca o minimo com a posicao i (NAO estavel). */
    public static void sort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[min]) {
                    min = j;
                }
            }
            if (min != i) {
                int tmp = a[i];
                a[i] = a[min];
                a[min] = tmp;
            }
        }
    }

    /**
     * Versao estavel: em vez de trocar, desloca os elementos entre i e min uma
     * posicao a direita e insere o minimo em i. Como o minimo escolhido e sempre
     * a PRIMEIRA ocorrencia do menor valor (comparacao estrita), a ordem relativa
     * dos iguais e preservada.
     */
    public static void sortEstavel(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (a[j] < a[min]) {
                    min = j;
                }
            }
            if (min != i) {
                int valor = a[min];
                System.arraycopy(a, i, a, i + 1, min - i);
                a[i] = valor;
            }
        }
    }
}
