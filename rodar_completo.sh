#!/usr/bin/env bash
# Benchmark completo. Tamanhos padrao do enunciado: 50.000, 150.000, 500.000.
# Para reduzir: ./rodar_completo.sh 20000,50000,100000
# Atencao: com os tamanhos do enunciado, os quadraticos levam HORAS.
set -e
TAMANHOS="${1:-50000,150000,500000}"
java -cp out benchmark.Main --sizes "$TAMANHOS" --out resultados
python3 gerar_graficos.py resultados graficos
