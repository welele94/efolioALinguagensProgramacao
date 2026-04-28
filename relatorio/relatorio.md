# Relatorio - e-Folio A

## Objetivo

Este trabalho implementa um sistema de acompanhamento automatico de turmas. O processamento
dos dados e das regras de decisao e realizado em OCaml, enquanto a aplicacao Java funciona como
interface de apresentacao e invoca o executavel OCaml atraves de `ProcessBuilder`.

## Organizacao da solucao

- `database/database.pl`: factos Prolog fornecidos no enunciado.
- `ocaml/main.ml`: leitura da base de dados, calculo de indicadores e classificacao final.
- `java/src/Aplicacao.java`: ponto de entrada da aplicacao Java.
- `java/src/Menu.java`: menu interativo.
- `java/src/IntegradorOCaml.java`: classe responsavel por executar o programa OCaml.
- `java/src/Boletim.java`: criacao do boletim JSON de um aluno.

## Regras implementadas

R1 verifica se o aluno tem pelo menos 3 participacoes no forum.

R2 calcula a media conjunta de tarefas e quizzes e verifica se e maior ou igual a 10.

R3 calcula a percentagem de assiduidade com base nas aulas assistidas e verifica se e maior ou
igual a 75%.

R4 compara a autoavaliacao com a media conjunta e considera coerente quando a diferenca absoluta
e menor ou igual a 2 valores.

Depois da classificacao base, a autoavaliacao coerente melhora o estado final em um nivel, exceto
quando o aluno esta Retido. O estado Aprovado mantem-se Aprovado.

## Testes efetuados

Comandos OCaml a testar:

```sh
./main.exe listar_alunos
./main.exe indicadores 1
./main.exe avaliar 2
./main.exe listar_estados
./main.exe listar_auto_coerente
```

Comando Java a testar:

```sh
java -cp java/bin Aplicacao
```

## Opcao bonus

Foi adicionada a opcao `listar_auto_coerente`, disponivel em OCaml e tambem no menu Java. Esta
opcao lista os alunos cuja autoavaliacao esta coerente com a media conjunta de tarefas e quizzes.
