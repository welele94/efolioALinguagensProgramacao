OCAMLC=ocamlc
JAVAC=javac

.PHONY: all ocaml java clean

all: ocaml java

ocaml:
	$(OCAMLC) str.cma -o main.exe ocaml/main.ml

java:
	mkdir -p java/bin
	$(JAVAC) -encoding UTF-8 -d java/bin java/src/*.java

clean:
	rm -f main.exe ocaml/*.cmi ocaml/*.cmo
	rm -rf java/bin boletins
