e-Folio A - Linguagens de Programacao
=====================================

Estrutura
---------
database/database.pl       Base de dados em formato Prolog.
ocaml/main.ml              Programa principal OCaml.
java/src/                  Interface Java organizada por classes.
relatorio/relatorio.md     Base para o relatorio final.

Compilacao
----------
1. Compilar o programa OCaml:
   ocamlc str.cma -o main.exe ocaml/main.ml

2. Compilar a interface Java:
   mkdir -p java/bin
   javac -encoding UTF-8 -d java/bin java/src/*.java

Tambem pode usar:
   make

Execucao OCaml
--------------
Listar alunos:
   ./main.exe listar_alunos

Calcular indicadores de um aluno:
   ./main.exe indicadores 1

Avaliar um aluno:
   ./main.exe avaliar 2

Listar estados finais:
   ./main.exe listar_estados

Opcao bonus:
   ./main.exe listar_auto_coerente

Execucao Java
------------
Depois de compilar OCaml e Java:
   java -cp java/bin Aplicacao

Se o executavel OCaml estiver noutro caminho:
   java -cp java/bin Aplicacao /caminho/para/main.exe

Boletins
--------
A opcao "Emitir boletim JSON" cria ficheiros na pasta boletins, por exemplo:
   boletins/aluno_1.json

Notas
-----
O ficheiro database/database.pl deve existir quando o programa e executado, porque o caminho
esta definido no programa OCaml como database/database.pl.
