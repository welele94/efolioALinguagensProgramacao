import java.util.Scanner;

public class Aplicacao {
    public static void main(String[] args) {
        String executavel = args.length > 0 ? args[0] : "./main.exe";

        IntegradorOCaml integrador = new IntegradorOCaml(executavel);
        Boletim boletim = new Boletim();
        Scanner scanner = new Scanner(System.in);

        try {
            Menu menu = new Menu(scanner, integrador, boletim);
            menu.iniciar();
        } finally {
            scanner.close();
        }
    }
}
