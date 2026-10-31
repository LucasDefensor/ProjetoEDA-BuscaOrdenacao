package testes;

import busca.Busca;
import ordenacao.BubbleSort;
import ordenacao.CountingSort;
import ordenacao.InsertionSort;
import ordenacao.MergeSort;
import ordenacao.QuickSort;
import ordenacao.SelectionSort;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/** Testes de corretude sem dependencias externas. Execute: java -cp out testes.TestesCorretude */
public class TestesCorretude {

    static int falhas = 0;

    public static void main(String[] args) {
        Map<String, Consumer<int[]>> algs = new LinkedHashMap<>();
        algs.put("BubbleSort", BubbleSort::sort);
        algs.put("BubbleSortOtimizado", BubbleSort::sortOtimizado);
        algs.put("SelectionSort", SelectionSort::sort);
        algs.put("SelectionSortEstavel", SelectionSort::sortEstavel);
        algs.put("InsertionSort", InsertionSort::sort);
        algs.put("MergeSort", MergeSort::sort);
        algs.put("QuickSort", QuickSort::sort);
        algs.put("QuickSortShuffle", QuickSort::sortShuffle);
        algs.put("CountingSort", CountingSort::sort);

        Random rnd = new Random(7);
        for (Map.Entry<String, Consumer<int[]>> e : algs.entrySet()) {
            int ok = 0;
            // bordas
            int[][] fixos = {{}, {5}, {2, 1}, {1, 2}, {3, 3, 3, 3}, {5, 4, 3, 2, 1}, {1, 1, 2, 2, 1, 1}};
            for (int[] f : fixos) ok += testar(e, f.clone());
            // aleatorios com muitos repetidos (valores em faixa pequena) e faixa cheia
            for (int t = 0; t < 300; t++) {
                int n = rnd.nextInt(200);
                int faixa = t % 2 == 0 ? 5 : 100_000;
                int[] a = new int[n];
                for (int i = 0; i < n; i++) a[i] = 1 + rnd.nextInt(faixa);
                ok += testar(e, a);
            }
            System.out.println((falhas == 0 ? "OK   " : "????") + " " + e.getKey() + " (" + ok + " casos)");
        }

        // buscas
        int[] v = new int[1000];
        for (int i = 0; i < v.length; i++) v[i] = 1 + rnd.nextInt(5000);
        Arrays.sort(v);
        for (int t = 0; t < 2000; t++) {
            int x = t < 1000 ? v[t] : rnd.nextInt(6000) - 500;
            boolean existe = Arrays.binarySearch(v, x) >= 0;
            checarBusca("linearIterativa", Busca.linearIterativa(v, x), v, x, existe);
            checarBusca("linearRecursiva", Busca.linearRecursiva(v, x), v, x, existe);
            checarBusca("binariaIterativa", Busca.binariaIterativa(v, x), v, x, existe);
            checarBusca("binariaRecursiva", Busca.binariaRecursiva(v, x), v, x, existe);
            checarBusca("linearDuasPontas", Busca.linearDuasPontas(v, x), v, x, existe);
        }
        checarBusca("vazio", Busca.binariaIterativa(new int[0], -1), new int[0], -1, false);
        System.out.println((falhas == 0 ? "OK   " : "????") + " buscas");

        if (falhas > 0) {
            System.out.println("FALHAS: " + falhas);
            System.exit(1);
        }
        System.out.println("Todos os testes passaram.");
    }

    static int testar(Map.Entry<String, Consumer<int[]>> e, int[] a) {
        int[] esperado = a.clone();
        Arrays.sort(esperado);
        e.getValue().accept(a);
        if (!Arrays.equals(a, esperado)) {
            falhas++;
            System.out.println("FALHA em " + e.getKey() + ": " + Arrays.toString(a));
        }
        return 1;
    }

    static void checarBusca(String nome, int r, int[] v, int x, boolean existe) {
        boolean certo = existe ? (r >= 0 && v[r] == x) : r == -1;
        if (!certo) {
            falhas++;
            System.out.println("FALHA busca " + nome + " x=" + x + " r=" + r);
        }
    }
}
