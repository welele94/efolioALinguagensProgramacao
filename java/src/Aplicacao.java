import java.util.Scanner;

public class Aplicacao {
    public static void main(String[] args) {
        // O executavel OCaml pode ser indicado por argumento.
        // Se nao for indicado, assume-se que esta na raiz do projeto.
        String executavel = args.length > 0 ? args[0] : "./main.exe";

        // A aplicacao Java fica apenas com a interface e delega os calculos ao OCaml.
        IntegradorOCaml integrador = new IntegradorOCaml(executavel);
        Boletim boletim = new Boletim();
        Scanner scanner = new Scanner(System.in);

        try {
            Menu menu = new Menu(scanner, integrador, boletim);
            menu.iniciar();
        } finally {
            // Fecha o Scanner no fim da aplicacao.
            scanner.close();
        }
    }
}
