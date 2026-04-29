import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Boletim {
    public void gravar(int alunoId, String indicadores, String avaliacao) throws IOException {
        File pasta = prepararPastaBoletins();
        File ficheiro = new File(pasta, "aluno_" + alunoId + ".json");

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ficheiro), "UTF-8"));
        try {
            DadosBoletim dados = extrairDados(alunoId, indicadores, avaliacao);

            writer.write("{\n");
            writer.write("  \"alunoId\": " + alunoId + ",\n");
            writer.write("  \"indicadores\": {\n");
            writer.write("    \"participacoesForum\": " + dados.participacoesForum + ",\n");
            writer.write("    \"mediaTarefas\": " + dados.mediaTarefas + ",\n");
            writer.write("    \"mediaQuizzes\": " + dados.mediaQuizzes + ",\n");
            writer.write("    \"mediaConjunta\": " + dados.mediaConjunta + ",\n");
            writer.write("    \"assiduidade\": \"" + escaparJson(dados.assiduidade) + "\",\n");
            writer.write("    \"autoavaliacao\": \"" + escaparJson(dados.autoavaliacao) + "\"\n");
            writer.write("  },\n");
            writer.write("  \"avaliacao\": {\n");
            writer.write("    \"R1\": " + dados.r1 + ",\n");
            writer.write("    \"R2\": " + dados.r2 + ",\n");
            writer.write("    \"R3\": " + dados.r3 + ",\n");
            writer.write("    \"R4\": " + dados.r4 + ",\n");
            writer.write("    \"estadoFinal\": \"" + escaparJson(dados.estadoFinal) + "\"\n");
            writer.write("  }\n");
            writer.write("}\n");
        } finally {
            writer.close();
        }
    }

    public void gravarCSV(int alunoId, String indicadores, String avaliacao) throws IOException {
        File pasta = prepararPastaBoletins();
        File ficheiro = new File(pasta, "aluno_" + alunoId + ".csv");

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ficheiro), "UTF-8"));
        try {
            DadosBoletim dados = extrairDados(alunoId, indicadores, avaliacao);

            writer.write("alunoId,participacoesForum,mediaTarefas,mediaQuizzes,mediaConjunta,assiduidade,autoavaliacao,R1,R2,R3,R4,estadoFinal\n");
            writer.write(alunoId + ","
                + dados.participacoesForum + ","
                + dados.mediaTarefas + ","
                + dados.mediaQuizzes + ","
                + dados.mediaConjunta + ","
                + escaparCsv(dados.assiduidade) + ","
                + escaparCsv(dados.autoavaliacao) + ","
                + dados.r1 + ","
                + dados.r2 + ","
                + dados.r3 + ","
                + dados.r4 + ","
                + escaparCsv(dados.estadoFinal) + "\n");
        } finally {
            writer.close();
        }
    }

    private File prepararPastaBoletins() throws IOException {
        File pasta = new File("boletins");
        if (!pasta.exists() && !pasta.mkdirs()) {
            throw new IOException("Nao foi possivel criar a pasta de boletins.");
        }
        return pasta;
    }

    private DadosBoletim extrairDados(int alunoId, String indicadores, String avaliacao) throws IOException {
        String linhaIndicadores = encontrarLinhaDoAluno(alunoId, indicadores);
        String[] partes = linhaIndicadores.split(";");

        if (partes.length < 7) {
            throw new IOException("Formato de indicadores invalido para o aluno " + alunoId + ".");
        }

        DadosBoletim dados = new DadosBoletim();
        dados.participacoesForum = Integer.parseInt(partes[1].trim());
        dados.mediaTarefas = Double.parseDouble(partes[2].trim());
        dados.mediaQuizzes = Double.parseDouble(partes[3].trim());
        dados.mediaConjunta = Double.parseDouble(partes[4].trim());
        dados.assiduidade = partes[5].trim();
        dados.autoavaliacao = partes[6].trim();

        dados.r1 = avaliacao.contains("R1") && avaliacao.contains("R1 (>=3 forum): true");
        dados.r2 = avaliacao.contains("R2") && avaliacao.contains("R2 (media >=10): true");
        dados.r3 = avaliacao.contains("R3") && avaliacao.contains("R3 (assid. >=75%): true");
        dados.r4 = avaliacao.contains("R4") && avaliacao.contains("R4 (autoav. coerente): true");
        dados.estadoFinal = extrairEstadoFinal(avaliacao);

        return dados;
    }

    private String encontrarLinhaDoAluno(int alunoId, String texto) throws IOException {
        String prefixo = alunoId + ";";
        for (String linha : texto.split("\\n")) {
            String linhaLimpa = linha.trim();
            if (linhaLimpa.startsWith(prefixo)) {
                return linhaLimpa;
            }
        }
        throw new IOException("Nao foi encontrada a linha de indicadores do aluno " + alunoId + ".");
    }

    private String extrairEstadoFinal(String avaliacao) {
        if (avaliacao.contains("Estado final: Aprovado")) {
            return "Aprovado";
        }
        if (avaliacao.contains("Estado final: Condicionado")) {
            return "Condicionado";
        }
        if (avaliacao.contains("Estado final: Em Observacao")) {
            return "Em Observacao";
        }
        if (avaliacao.contains("Estado final: Em Risco")) {
            return "Em Risco";
        }
        if (avaliacao.contains("Estado final: Retido")) {
            return "Retido";
        }
        return "Desconhecido";
    }

    private String escaparJson(String texto) {
        return texto
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "")
            .replace("\n", "\\n");
    }

    private String escaparCsv(String texto) {
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    private static class DadosBoletim {
        int participacoesForum;
        double mediaTarefas;
        double mediaQuizzes;
        double mediaConjunta;
        String assiduidade;
        String autoavaliacao;
        boolean r1;
        boolean r2;
        boolean r3;
        boolean r4;
        String estadoFinal;
    }
}
