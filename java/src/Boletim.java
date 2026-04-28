import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Boletim {
    public void gravar(int alunoId, String indicadores, String avaliacao) throws IOException {
        File pasta = new File("boletins");
        if (!pasta.exists() && !pasta.mkdirs()) {
            throw new IOException("Nao foi possivel criar a pasta de boletins.");
        }

        File ficheiro = new File(pasta, "aluno_" + alunoId + ".json");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ficheiro), "UTF-8"));
        try {
            writer.write("{\n");
            writer.write("  \"alunoId\": " + alunoId + ",\n");
            writer.write("  \"indicadores\": \"" + escaparJson(indicadores) + "\",\n");
            writer.write("  \"avaliacao\": \"" + escaparJson(avaliacao) + "\"\n");
            writer.write("}\n");
        } finally {
            writer.close();
        }
    }

    private String escaparJson(String texto) {
        return texto
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "")
            .replace("\n", "\\n");
    }
}
