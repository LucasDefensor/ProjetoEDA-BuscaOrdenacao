package ordenacao;

/**
 * MergeSort classico (top-down). Estavel, O(n log n) sempre, usa O(n) de memoria
 * auxiliar. O vetor auxiliar e alocado uma vez por chamada de sort().
 */
public class MergeSort {

    public static void sort(int[] a) {
        if (a.length < 2) {
            return;
        }
        int[] aux = new int[a.length];
        mergeSort(a, aux, 0, a.length - 1);
    }

    private static void mergeSort(int[] a, int[] aux, int esq, int dir) {
        if (esq >= dir) {
            return;
        }
        int meio = esq + (dir - esq) / 2;
        mergeSort(a, aux, esq, meio);
        mergeSort(a, aux, meio + 1, dir);
        merge(a, aux, esq, meio, dir);
    }

    private static void merge(int[] a, int[] aux, int esq, int meio, int dir) {
        System.arraycopy(a, esq, aux, esq, dir - esq + 1);
        int i = esq;
        int j = meio + 1;
        for (int k = esq; k <= dir; k++) {
            if (i > meio) {
                a[k] = aux[j++];
            } else if (j > dir) {
                a[k] = aux[i++];
            } else if (aux[j] < aux[i]) { // estrito: em empate pega da esquerda (estavel)
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }
}
