#!/usr/bin/env bash
# Compila o projeto para a pasta out/ e roda os testes de corretude.
set -e
rm -rf out && mkdir out
javac -d out $(find src -name '*.java')
java -cp out testes.TestesCorretude
