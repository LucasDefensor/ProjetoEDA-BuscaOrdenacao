#!/usr/bin/env bash
# Benchmark reduzido (~30 s) para validar o ambiente e medir sua maquina.
set -e
java -cp out benchmark.Main --sizes 2000,5000,10000 --out resultados_teste
python3 gerar_graficos.py resultados_teste graficos_teste
