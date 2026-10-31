package benchmark;

import java.util.Arrays;
import java.util.Random;

/**
 * Geracao das entradas originais. Valores sempre em [1, 100.000], com repeticoes.
 * Cada (tamanho, cenario) e gerado UMA vez; todos os algoritmos recebem copias
 * dessa mesma entrada.
 */
public class Cenarios {

    public static final int VALOR_MIN = 1;
    public static final int VALOR_MAX = 100_000;
    public static final double FRACAO_PARCIAL = 0.15;

    public static final String ALEATORIO = "aleatorio";
    public static final String ORDENADO = "ordenado";
    public static final String INVERSO = "inverso";
    public static final String PARCIAL = "parcial15";

    public static final String[] TODOS = {ALEATORIO, ORDENADO, INVERSO, PARCIAL};

    public static int[] gerar(String cenario, int n, long seed) {
        Random rnd = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = VALOR_MIN + rnd.nextInt(VALOR_MAX - VALOR_MIN + 1);
        }
        switch (cenario) {
            case ALEATORIO:
                return a;
            case ORDENADO:
                Arrays.sort(a);
                return a;
            case INVERSO:
                Arrays.sort(a);
                inverter(a);
                return a;
            case PARCIAL:
                Arrays.sort(a);
                embaralharParcial(a, rnd);
                return a;
            default:
                throw new IllegalArgumentException("Cenario desconhecido: " + cenario);
        }
    }

    private static void inverter(int[] a) {
        for (int i = 0, j = a.length - 1; i < j; i++, j--) {
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
    }

    /**
     * Parte de um vetor ordenado, sorteia ~15% das posicoes (sem repeticao) e
     * embaralha apenas os valores dessas posicoes entre si.
     */
    private static void embaralharParcial(int[] a, Random rnd) {
        int n = a.length;
        int k = (int) Math.round(n * FRACAO_PARCIAL);
        // sorteia k posicoes distintas com Fisher-Yates parcial sobre os indices
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
        }
        for (int i = 0; i < k; i++) {
            int j = i + rnd.nextInt(n - i);
            int tmp = idx[i];
            idx[i] = idx[j];
            idx[j] = tmp;
        }
        // embaralha os valores apenas nas k posicoes escolhidas
        for (int i = k - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int pi = idx[i];
            int pj = idx[j];
            int tmp = a[pi];
            a[pi] = a[pj];
            a[pj] = tmp;
        }
    }
}
