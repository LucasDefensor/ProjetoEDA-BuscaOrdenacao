package ordenacao;

import java.util.Random;

/**
 * QuickSort. NAO estavel. Caso medio O(n log n); pior caso O(n^2).
 *
 * Versao "do slide": pivo = ultimo elemento, particao de Lomuto.
 * Em vetor ordenado ou inverso o pivo e sempre o maior/menor, a recursao degenera
 * (profundidade n) e o custo vira O(n^2). Por isso o benchmark roda em uma thread
 * com pilha grande.
 *
 * Versao "slide + shuffle": embaralha o vetor (Fisher-Yates) antes de ordenar,
 * tornando improvavel o pior caso. O custo do embaralhamento faz parte do tempo.
 */
public class QuickSort {

    private static final Random RANDOM = new Random(12345);

    public static void sort(int[] a) {
        quickSort(a, 0, a.length - 1);
    }

    public static void sortShuffle(int[] a) {
        embaralhar(a);
        quickSort(a, 0, a.length - 1);
    }

    private static void quickSort(int[] a, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int p = particionar(a, lo, hi);
        quickSort(a, lo, p - 1);
        quickSort(a, p + 1, hi);
    }

    /** Particao de Lomuto com pivo no ultimo elemento. */
    private static int particionar(int[] a, int lo, int hi) {
        int pivo = a[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j] <= pivo) {
                i++;
                int tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
        }
        int tmp = a[i + 1];
        a[i + 1] = a[hi];
        a[hi] = tmp;
        return i + 1;
    }

    private static void embaralhar(int[] a) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
    }
}
