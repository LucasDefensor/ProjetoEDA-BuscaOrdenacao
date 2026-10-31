package ordenacao;

/**
 * BubbleSort. Estavel, O(n^2) no pior caso e no caso medio.
 */
public class BubbleSort {

    /** Versao "do slide": dois lacos aninhados, sem nenhuma parada antecipada. */
    public static void sort(int[] a) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (a[j] > a[j + 1]) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                }
            }
        }
    }

    /**
     * Versao otimizada: para quando uma passada nao troca nada (melhor caso O(n)
     * em vetor ordenado) e reduz o intervalo ate a posicao da ultima troca.
     */
    public static void sortOtimizado(int[] a) {
        int fim = a.length - 1;
        while (fim > 0) {
            int ultimaTroca = 0;
            for (int j = 0; j < fim; j++) {
                if (a[j] > a[j + 1]) {
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                    ultimaTroca = j;
                }
            }
            fim = ultimaTroca;
        }
    }
}
