package busca;

/**
 * Algoritmos de busca em int[] previamente ordenado (crescente).
 * Todos retornam o indice do elemento ou -1 se ausente.
 */
public class Busca {

    /** Busca linear iterativa: O(n). */
    public static int linearIterativa(int[] a, int x) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == x) {
                return i;
            }
        }
        return -1;
    }

    /** Busca linear recursiva: O(n) tempo e O(n) de pilha. */
    public static int linearRecursiva(int[] a, int x) {
        return linearRecursiva(a, x, 0);
    }

    private static int linearRecursiva(int[] a, int x, int i) {
        if (i >= a.length) {
            return -1;
        }
        if (a[i] == x) {
            return i;
        }
        return linearRecursiva(a, x, i + 1);
    }

    /** Busca binaria iterativa: O(log n). Exige vetor ordenado. */
    public static int binariaIterativa(int[] a, int x) {
        int lo = 0;
        int hi = a.length - 1;
        while (lo <= hi) {
            int meio = lo + (hi - lo) / 2;
            if (a[meio] == x) {
                return meio;
            } else if (a[meio] < x) {
                lo = meio + 1;
            } else {
                hi = meio - 1;
            }
        }
        return -1;
    }

    /** Busca binaria recursiva: O(log n) tempo e O(log n) de pilha. */
    public static int binariaRecursiva(int[] a, int x) {
        return binariaRecursiva(a, x, 0, a.length - 1);
    }

    private static int binariaRecursiva(int[] a, int x, int lo, int hi) {
        if (lo > hi) {
            return -1;
        }
        int meio = lo + (hi - lo) / 2;
        if (a[meio] == x) {
            return meio;
        } else if (a[meio] < x) {
            return binariaRecursiva(a, x, meio + 1, hi);
        } else {
            return binariaRecursiva(a, x, lo, meio - 1);
        }
    }

    /**
     * Busca linear iterativa de duas pontas: examina o inicio e o fim ao mesmo
     * tempo, avancando um indice de cada lado. Continua O(n), com metade das
     * iteracoes.
     */
    public static int linearDuasPontas(int[] a, int x) {
        int i = 0;
        int j = a.length - 1;
        while (i <= j) {
            if (a[i] == x) {
                return i;
            }
            if (a[j] == x) {
                return j;
            }
            i++;
            j--;
        }
        return -1;
    }
}
