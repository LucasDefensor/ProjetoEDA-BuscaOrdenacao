# Projeto Unidade 1 — Análise de Algoritmos de Busca e Ordenação (EDA/LEDA 2026.2)

Implementação em Java (somente `int[]`) e benchmark de:

- **Ordenação:** BubbleSort (slide e otimizado), SelectionSort (slide e estável), InsertionSort,
  MergeSort, QuickSort (slide e slide+shuffle), CountingSort e `Arrays.sort(int[])` (referência).
- **Busca (vetor ordenado, alvo -1):** linear iterativa, linear recursiva, binária iterativa,
  binária recursiva e linear iterativa de duas pontas.

## Estrutura

```
src/ordenacao/   algoritmos de ordenação
src/busca/       algoritmos de busca
src/benchmark/   Cenarios.java (entradas) e Main.java (medição, CSV)
src/testes/      testes de corretude (sem dependências)
gerar_graficos.py  CSV -> gráficos PNG + tabelas Markdown
```

## Como executar (Java 11+; Python 3 com pandas e matplotlib para os gráficos)

```bash
./compilar.sh             # compila e roda os testes
./rodar_teste_rapido.sh   # ~30 s, tamanhos pequenos
./rodar_completo.sh       # tamanhos do enunciado (horas!)
./rodar_completo.sh 20000,50000,100000   # tamanhos reduzidos (~1 h)
```

Opções do `benchmark.Main`: `--sizes`, `--runs` (25), `--warmup` (5), `--seed` (42),
`--mode ordenacao|busca|tudo`, `--algs A,B`, `--scenarios aleatorio,ordenado,inverso,parcial15`,
`--out pasta`. O CSV é gravado a cada combinação concluída; para rodar em partes (ou em
máquinas diferentes) use `--algs`/`--sizes` e `--out` distintos e junte os CSVs.

Os resultados saem em `resultados/ordenacao.csv` e `resultados/busca.csv`
(média, desvio padrão, mínimo, mediana e máximo, em ns) e, após o script Python,
em `graficos/` (PNG e `tabelas.md`).

## Metodologia (conforme o enunciado)

- Valores inteiros em [1, 100.000], com repetições.
- 4 cenários: aleatório, ordenado, inverso e parcialmente ordenado (parte de um vetor ordenado,
  sorteia ~15% das posições e embaralha só os valores dessas posições).
- Para cada (tamanho, cenário) há **uma única entrada original** (semente fixa); todos os
  algoritmos recebem **cópias** dela. A cópia fica fora da medição.
- 25 execuções por combinação; as 5 primeiras (warm-up) são descartadas, então as estatísticas
  usam 20 medições.
- Tempo com `System.nanoTime()` somente em volta da chamada do algoritmo.
- Após cada execução o resultado é comparado com a referência ordenada (fora da medição);
  se estiver incorreto, o programa aborta.
- Buscas: vetor ordenado, alvo `-1` (ausente), uma chamada medida por execução.
- O benchmark roda em uma thread com pilha de 2 GB, pois o QuickSort do slide em vetor
  ordenado/inverso e a busca linear recursiva chegam a profundidade n.

## Decisões e pontos a conferir

- **"Versão do slide":** os slides não estavam disponíveis, então foram usadas as versões
  didáticas padrão (ver comentários nas classes): Bubble sem parada antecipada; Selection com
  troca; QuickSort com pivô no último elemento e partição de Lomuto. Se os slides diferirem,
  ajuste `BubbleSort.sort`, `SelectionSort.sort` e `QuickSort.particionar`.
- O custo do embaralhamento do `QuickSortShuffle` está **incluído** no tempo medido.
- O vetor da busca vem do mesmo gerador (cenário ordenado), não da saída de um algoritmo avaliado.
- Buscas binárias levam centenas de ns; com 1 chamada por execução o ruído é alto. Isso deve ser
  comentado no relatório.
- Prazo no PDF diz 31/10/**2025**; confirmar a data correta.
