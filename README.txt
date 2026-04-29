# e-Fólio A — Linguagens de Programação (OCaml + Java)

## Descrição

Este projeto implementa um sistema de acompanhamento de alunos, que analisa a
participação, desempenho e assiduidade ao longo do semestre.

Os dados são lidos a partir de um ficheiro em formato Prolog (`database.pl`) e
processados em OCaml. A aplicação Java funciona como interface, permitindo
executar os comandos de forma interativa.

---

## Estrutura do Projeto

database/database.pl
Base de dados com informação dos alunos.

ocaml/main.ml
Programa principal em OCaml (responsável pelo processamento).

java/src/
Código da interface Java:

* Aplicacao.java — ponto de entrada
* Menu.java — menu interativo
* IntegradorOCaml.java — ligação ao executável OCaml
* Boletim.java — geração de ficheiros JSON e CSV

relatorio/
Contém o relatório do trabalho.

---

## Requisitos

* OCaml instalado
* Java (JDK 8 ou superior)

---

## Compilação

1. Compilar o programa OCaml:
   ocamlc str.cma -o main.exe ocaml/main.ml

2. Compilar a aplicação Java:
   mkdir -p java/bin
   javac -encoding UTF-8 -d java/bin java/src/*.java

---

## Execução

Executar comandos diretamente em OCaml:

./main.exe listar_alunos
./main.exe indicadores 1
./main.exe avaliar 2
./main.exe listar_estados
./main.exe listar_auto_coerente

Executar a interface Java:

java -cp java/bin Aplicacao ./main.exe

---

## Boletins

A opção "Emitir boletim" permite gerar ficheiros com os resultados do aluno:

* JSON:
  boletins/aluno_1.json

* CSV (funcionalidade extra):
  boletins/aluno_1.csv

---

## Funcionalidade Extra

Para além do formato JSON pedido no enunciado, foi adicionada a possibilidade de
exportar os dados em formato CSV, permitindo abrir e analisar facilmente os
resultados em ferramentas como Excel.

---

## Notas

* Os comandos devem ser executados a partir da raiz do projeto.
* O ficheiro `database/database.pl` deve existir no caminho definido.
* A pasta `boletins/` é criada automaticamente quando necessário.
