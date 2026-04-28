import java.io.IOException;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final IntegradorOCaml integrador;
    private final Boletim boletim;

    public Menu(Scanner scanner, IntegradorOCaml integrador, Boletim boletim) {
        this.scanner = scanner;
        this.integrador = integrador;
        this.boletim = boletim;
    }

    public void iniciar() {
        boolean ativo = true;
        while (ativo) {
            imprimirOpcoes();
            String opcao = scanner.nextLine().trim();

            try {
                if ("1".equals(opcao)) {
                    imprimir(integrador.executar("listar_alunos"));
                } else if ("2".equals(opcao)) {
                    int id = lerId();
                    imprimir(integrador.executar("indicadores", String.valueOf(id)));
                } else if ("3".equals(opcao)) {
                    int id = lerId();
                    imprimir(integrador.executar("avaliar", String.valueOf(id)));
                } else if ("4".equals(opcao)) {
                    imprimir(integrador.executar("listar_estados"));
                } else if ("5".equals(opcao)) {
                    emitirBoletim();
                } else if ("6".equals(opcao)) {
                    imprimir(integrador.executar("listar_auto_coerente"));
                } else if ("0".equals(opcao)) {
                    ativo = false;
                } else {
                    System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void imprimirOpcoes() {
        System.out.println();
        System.out.println("=== Sistema de Acompanhamento de Turmas ===");
        System.out.println("1 - Listar alunos");
        System.out.println("2 - Ver indicadores de um aluno");
        System.out.println("3 - Avaliar aluno");
        System.out.println("4 - Listar estados finais");
        System.out.println("5 - Emitir boletim JSON");
        System.out.println("6 - Listar autoavaliacoes coerentes");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
    }

    private int lerId() {
        System.out.print("ID do aluno: ");
        String texto = scanner.nextLine().trim();
        return Integer.parseInt(texto);
    }

    private void emitirBoletim() throws IOException, InterruptedException {
        int id = lerId();
        String indicadores = integrador.executar("indicadores", String.valueOf(id));
        String avaliacao = integrador.executar("avaliar", String.valueOf(id));

        boletim.gravar(id, indicadores, avaliacao);
        System.out.println("--- Indicadores ---");
        System.out.print(indicadores);
        System.out.println("--- Avaliacao ---");
        System.out.print(avaliacao);
        System.out.println("Boletim gravado em boletins/aluno_" + id + ".json");
    }

    private void imprimir(String texto) {
        System.out.println();
        System.out.print(texto);
    }
}
