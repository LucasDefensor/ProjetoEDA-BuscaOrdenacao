#!/usr/bin/env python3
"""Gera tabelas (Markdown) e graficos (PNG) a partir dos CSVs do benchmark.

Uso: python3 gerar_graficos.py [pasta_resultados] [pasta_saida]
Requer: pandas, matplotlib
"""
import sys
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import pandas as pd

res = Path(sys.argv[1] if len(sys.argv) > 1 else "resultados")
out = Path(sys.argv[2] if len(sys.argv) > 2 else "graficos")
out.mkdir(parents=True, exist_ok=True)

CENARIOS = ["aleatorio", "ordenado", "inverso", "parcial15"]
NOMES = {"aleatorio": "Aleatório", "ordenado": "Ordenado",
         "inverso": "Inverso", "parcial15": "Parcialmente ordenado (15%)"}


def fmt_tempo(ns, unidade):
    div = {"ns": 1, "us": 1e3, "ms": 1e6}[unidade]
    return f"{ns / div:,.3f}"


def tabela_md(df, unidade):
    """Tabela: linhas = algoritmo, colunas = tamanho; valor = media ± desvio."""
    linhas = []
    tamanhos = sorted(df["tamanho"].unique())
    linhas.append("| Algoritmo | " + " | ".join(f"n={t:,}" for t in tamanhos) + " |")
    linhas.append("|---|" + "---|" * len(tamanhos))
    for alg in df["algoritmo"].drop_duplicates():
        cel = []
        for t in tamanhos:
            r = df[(df.algoritmo == alg) & (df.tamanho == t)]
            if r.empty:
                cel.append("—")
            else:
                r = r.iloc[0]
                cel.append(f"{fmt_tempo(r.media_ns, unidade)} ± {fmt_tempo(r.desvio_ns, unidade)}")
        linhas.append(f"| {alg} | " + " | ".join(cel) + " |")
    return "\n".join(linhas)


def grafico_linhas(df, titulo, arquivo, unidade_y="ms"):
    """Um grafico por cenario: tempo medio x tamanho (escala log-log)."""
    div = {"ns": 1, "us": 1e3, "ms": 1e6}[unidade_y]
    cens = [c for c in CENARIOS if c in set(df["cenario"])]
    fig, axes = plt.subplots(1, len(cens), figsize=(5.2 * len(cens), 4.6), squeeze=False)
    for ax, cen in zip(axes[0], cens):
        sub = df[df.cenario == cen]
        for alg in sub["algoritmo"].drop_duplicates():
            s = sub[sub.algoritmo == alg].sort_values("tamanho")
            ax.errorbar(s["tamanho"], s["media_ns"] / div, yerr=s["desvio_ns"] / div,
                        marker="o", capsize=2, label=alg)
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.set_title(NOMES.get(cen, cen))
        ax.set_xlabel("Tamanho do vetor (n)")
        ax.set_ylabel(f"Tempo médio ({unidade_y})")
        ax.grid(True, which="both", alpha=0.3)
    axes[0][-1].legend(fontsize=7, loc="upper left", bbox_to_anchor=(1.02, 1))
    fig.suptitle(titulo)
    fig.tight_layout()
    fig.savefig(out / arquivo, dpi=150, bbox_inches="tight")
    plt.close(fig)


def grafico_barras(df, n, arquivo):
    """Barras agrupadas por cenario para um tamanho fixo (escala log)."""
    sub = df[df.tamanho == n]
    algs = list(sub["algoritmo"].drop_duplicates())
    cens = [c for c in CENARIOS if c in set(sub["cenario"])]
    fig, ax = plt.subplots(figsize=(11, 4.8))
    w = 0.8 / len(cens)
    for i, cen in enumerate(cens):
        vals = []
        for a in algs:
            r = sub[(sub.algoritmo == a) & (sub.cenario == cen)]
            vals.append(r.iloc[0].media_ns / 1e6 if not r.empty else float("nan"))
        ax.bar([x + i * w for x in range(len(algs))], vals, w, label=NOMES.get(cen, cen))
    ax.set_xticks([x + 0.4 - w / 2 for x in range(len(algs))])
    ax.set_xticklabels(algs, rotation=30, ha="right")
    ax.set_yscale("log")
    ax.set_ylabel("Tempo médio (ms, escala log)")
    ax.set_title(f"Ordenação — comparação por cenário (n = {n:,})")
    ax.legend()
    ax.grid(True, axis="y", which="both", alpha=0.3)
    fig.tight_layout()
    fig.savefig(out / arquivo, dpi=150)
    plt.close(fig)


md = []

f = res / "ordenacao.csv"
if f.exists():
    df = pd.read_csv(f)
    grafico_linhas(df, "Algoritmos de ordenação — tempo médio por tamanho", "ordenacao_por_cenario.png")
    grafico_barras(df, int(df["tamanho"].max()), "ordenacao_barras_maior_tamanho.png")
    md.append("# Tempos de ordenação (média ± desvio padrão, em ms)\n")
    for cen in CENARIOS:
        sub = df[df.cenario == cen]
        if sub.empty:
            continue
        md.append(f"## {NOMES[cen]}\n")
        md.append(tabela_md(sub, "ms") + "\n")

f = res / "busca.csv"
if f.exists():
    dfb = pd.read_csv(f)
    grafico_linhas(dfb, "Algoritmos de busca (elemento -1, ausente) — tempo médio",
                   "busca.png", unidade_y="us")
    md.append("# Tempos de busca por -1 (média ± desvio padrão, em µs)\n")
    md.append(tabela_md(dfb, "us") + "\n")

(out / "tabelas.md").write_text("\n".join(md), encoding="utf-8")
print(f"Gráficos e tabelas gerados em {out}/")
