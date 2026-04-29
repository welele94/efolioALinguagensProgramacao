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
                    if (id == -1) continue;
                    imprimir(integrador.executar("indicadores", String.valueOf(id)));
                } else if ("3".equals(opcao)) {
                    int id = lerId();
                    if (id == -1) continue;
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
                System.out.println("Erro ao executar operacao. Verifique o ID ou tente novamente.");
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

        try {
            int id = Integer.parseInt(texto);
            if (id <= 0) {
                System.out.println("ID invalido.");
                return -1;
            }
            return id;
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
            return -1;
        }
    }

    private String[] obterDadosAluno(int id) throws IOException, InterruptedException {
        String indicadores = integrador.executar("indicadores", String.valueOf(id));
        String avaliacao = integrador.executar("avaliar", String.valueOf(id));
        return new String[]{indicadores, avaliacao};
    }

    private void emitirBoletim() throws IOException, InterruptedException {
        int id = lerId();
        if (id == -1) return;

        String[] dados = obterDadosAluno(id);
        String indicadores = dados[0];
        String avaliacao = dados[1];

        boletim.gravar(id, indicadores, avaliacao);
        System.out.println("Boletim JSON gravado em boletins/aluno_" + id + ".json");

        System.out.print("Exportar tambem para CSV? (s/n): ");
        String resposta = scanner.nextLine().trim();

        if (resposta.equalsIgnoreCase("s")) {
            boletim.gravarCSV(id, indicadores, avaliacao);
            System.out.println("Boletim CSV gravado em boletins/aluno_" + id + ".csv");
        }
    }

    private void imprimir(String texto) {
        System.out.println();
        System.out.print(texto);
    }
}
