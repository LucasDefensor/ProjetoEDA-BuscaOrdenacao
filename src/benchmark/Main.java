package benchmark;

import busca.Busca;
import ordenacao.BubbleSort;
import ordenacao.CountingSort;
import ordenacao.InsertionSort;
import ordenacao.MergeSort;
import ordenacao.QuickSort;
import ordenacao.SelectionSort;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToIntBiFunction;

/**
 * Harness de benchmark do projeto da Unidade 1.
 *
 * Regras seguidas (conforme o enunciado):
 *  - arrays de int com valores em [1, 100000];
 *  - 4 cenarios: aleatorio, ordenado, inverso, parcialmente ordenado (15%);
 *  - para cada (tamanho, cenario) existe UMA entrada original e todos os
 *    algoritmos recebem copias dela (a copia fica fora da medicao);
 *  - 25 execucoes por combinacao, as 5 primeiras descartadas (warm-up);
 *  - tempo medido com System.nanoTime() apenas em volta do algoritmo;
 *  - a correcao e verificada apos cada execucao, fora da medicao;
 *  - busca pelo elemento -1 (ausente) em vetor ordenado.
 *
 * Uso: veja README.md.
 */
public class Main {

    static final Map<String, Consumer<int[]>> ORDENACOES = new LinkedHashMap<>();
    static final Map<String, ToIntBiFunction<int[], Integer>> BUSCAS = new LinkedHashMap<>();

    static {
        ORDENACOES.put("BubbleSort", BubbleSort::sort);
        ORDENACOES.put("BubbleSortOtimizado", BubbleSort::sortOtimizado);
        ORDENACOES.put("SelectionSort", SelectionSort::sort);
        ORDENACOES.put("SelectionSortEstavel", SelectionSort::sortEstavel);
        ORDENACOES.put("InsertionSort", InsertionSort::sort);
        ORDENACOES.put("MergeSort", MergeSort::sort);
        ORDENACOES.put("QuickSort", QuickSort::sort);
        ORDENACOES.put("QuickSortShuffle", QuickSort::sortShuffle);
        ORDENACOES.put("CountingSort", CountingSort::sort);
        ORDENACOES.put("Arrays.sort", Arrays::sort);

        BUSCAS.put("LinearIterativa", Busca::linearIterativa);
        BUSCAS.put("LinearRecursiva", Busca::linearRecursiva);
        BUSCAS.put("BinariaIterativa", Busca::binariaIterativa);
        BUSCAS.put("BinariaRecursiva", Busca::binariaRecursiva);
        BUSCAS.put("LinearDuasPontas", Busca::linearDuasPontas);
    }

    // ---- configuracao (padroes = enunciado) ----
    static int[] tamanhos = {50_000, 150_000, 500_000};
    static int execucoes = 25;
    static int warmup = 5;
    static long seed = 42L;
    static String modo = "tudo"; // ordenacao | busca | tudo
    static String saida = "resultados";
    static List<String> filtroAlgs = null;
    static List<String> filtroCenarios = null;

    public static void main(String[] args) throws Exception {
        parseArgs(args);
        // Pilha grande: QuickSort do slide em vetor ordenado/inverso e a busca linear
        // recursiva chegam a profundidade n.
        Thread t = new Thread(null, () -> {
            try {
                executar();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }, "benchmark", 2L * 1024 * 1024 * 1024);
        t.start();
        t.join();
    }

    static void executar() throws IOException {
        Files.createDirectories(Paths.get(saida));
        System.out.printf("Tamanhos=%s execucoes=%d (warm-up descartado=%d) seed=%d modo=%s%n",
                Arrays.toString(tamanhos), execucoes, warmup, seed, modo);
        long inicio = System.nanoTime();
        if (modo.equals("ordenacao") || modo.equals("tudo")) {
            benchmarkOrdenacao(Paths.get(saida, "ordenacao.csv"));
        }
        if (modo.equals("busca") || modo.equals("tudo")) {
            benchmarkBusca(Paths.get(saida, "busca.csv"));
        }
        System.out.printf("Concluido em %.1f s%n", (System.nanoTime() - inicio) / 1e9);
    }

    // ------------------------------------------------------------------
    static void benchmarkOrdenacao(Path csv) throws IOException {
        iniciarCsv(csv);
        String[] cenarios = filtroCenarios == null ? Cenarios.TODOS
                : filtroCenarios.toArray(new String[0]);
        for (int n : tamanhos) {
            for (String cenario : cenarios) {
                int[] original = Cenarios.gerar(cenario, n, seed + n);
                int[] esperado = original.clone();
                Arrays.sort(esperado);
                for (Map.Entry<String, Consumer<int[]>> e : ORDENACOES.entrySet()) {
                    if (filtroAlgs != null && !filtroAlgs.contains(e.getKey())) {
                        continue;
                    }
                    long[] tempos = new long[execucoes - warmup];
                    long t0Combo = System.nanoTime();
                    for (int r = 0; r < execucoes; r++) {
                        int[] copia = original.clone();
                        long ini = System.nanoTime();
                        e.getValue().accept(copia);
                        long fim = System.nanoTime();
                        if (!Arrays.equals(copia, esperado)) {
                            throw new IllegalStateException("Resultado incorreto: "
                                    + e.getKey() + " n=" + n + " cenario=" + cenario);
                        }
                        if (r >= warmup) {
                            tempos[r - warmup] = fim - ini;
                        }
                    }
                    escreverLinha(csv, "ordenacao", e.getKey(), n, cenario, tempos);
                    System.out.printf(Locale.ROOT, "[ord] n=%-7d %-10s %-22s media=%12.3f ms  (combo: %.1f s)%n",
                            n, cenario, e.getKey(), media(tempos) / 1e6,
                            (System.nanoTime() - t0Combo) / 1e9);
                }
            }
        }
    }

    // ------------------------------------------------------------------
    static void benchmarkBusca(Path csv) throws IOException {
        iniciarCsv(csv);
        final int alvo = -1; // ausente: todos os algoritmos percorrem o pior caso
        for (int n : tamanhos) {
            // vetor aleatorio e depois ordenado (pre-condicao das buscas). Gerado a
            // partir do mesmo cenario aleatorio, sem usar saida de algoritmo avaliado.
            int[] ordenado = Cenarios.gerar(Cenarios.ORDENADO, n, seed + n);
            for (Map.Entry<String, ToIntBiFunction<int[], Integer>> e : BUSCAS.entrySet()) {
                if (filtroAlgs != null && !filtroAlgs.contains(e.getKey())) {
                    continue;
                }
                long[] tempos = new long[execucoes - warmup];
                for (int r = 0; r < execucoes; r++) {
                    long ini = System.nanoTime();
                    int resultado = e.getValue().applyAsInt(ordenado, alvo);
                    long fim = System.nanoTime();
                    if (resultado != -1) {                          // fora da medicao
                        throw new IllegalStateException("Busca incorreta: " + e.getKey());
                    }
                    if (r >= warmup) {
                        tempos[r - warmup] = fim - ini;
                    }
                }
                escreverLinha(csv, "busca", e.getKey(), n, "ordenado", tempos);
                System.out.printf(Locale.ROOT, "[busca] n=%-7d %-18s media=%12.1f ns%n",
                        n, e.getKey(), media(tempos));
            }
        }
    }

    // ------------------------------------------------------------------
    static double media(long[] v) {
        double s = 0;
        for (long x : v) s += x;
        return s / v.length;
    }

    static double desvio(long[] v) {
        double m = media(v);
        double s = 0;
        for (long x : v) s += (x - m) * (x - m);
        return v.length > 1 ? Math.sqrt(s / (v.length - 1)) : 0;
    }

    static void iniciarCsv(Path csv) throws IOException {
        if (!Files.exists(csv)) {
            try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(csv, StandardCharsets.UTF_8))) {
                w.println("tipo,algoritmo,tamanho,cenario,execucoes_medidas,media_ns,desvio_ns,min_ns,mediana_ns,max_ns");
            }
        }
    }

    static void escreverLinha(Path csv, String tipo, String alg, int n, String cenario, long[] tempos)
            throws IOException {
        long[] ord = tempos.clone();
        Arrays.sort(ord);
        double mediana = ord.length % 2 == 1 ? ord[ord.length / 2]
                : (ord[ord.length / 2 - 1] + ord[ord.length / 2]) / 2.0;
        String linha = String.format(Locale.ROOT, "%s,%s,%d,%s,%d,%.1f,%.1f,%d,%.1f,%d%n",
                tipo, alg, n, cenario, tempos.length, media(tempos), desvio(tempos),
                ord[0], mediana, ord[ord.length - 1]);
        Files.write(csv, linha.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    static void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--sizes":
                    String[] p = args[++i].split(",");
                    tamanhos = new int[p.length];
                    for (int k = 0; k < p.length; k++) tamanhos[k] = Integer.parseInt(p[k].trim());
                    break;
                case "--runs": execucoes = Integer.parseInt(args[++i]); break;
                case "--warmup": warmup = Integer.parseInt(args[++i]); break;
                case "--seed": seed = Long.parseLong(args[++i]); break;
                case "--mode": modo = args[++i]; break;
                case "--out": saida = args[++i]; break;
                case "--algs": filtroAlgs = Arrays.asList(args[++i].split(",")); break;
                case "--scenarios": filtroCenarios = Arrays.asList(args[++i].split(",")); break;
                default:
                    throw new IllegalArgumentException("Argumento desconhecido: " + args[i]);
            }
        }
        if (execucoes <= warmup) {
            throw new IllegalArgumentException("--runs deve ser maior que --warmup");
        }
    }
}
