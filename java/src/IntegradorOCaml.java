import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntegradorOCaml {
    private final String executavel;

    public IntegradorOCaml(String executavel) {
        this.executavel = executavel;
    }

    public String executar(String... argumentos) throws IOException, InterruptedException {
        List<String> comando = new ArrayList<String>();
        comando.add(executavel);
        comando.addAll(Arrays.asList(argumentos));

        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.directory(new File("."));
        processBuilder.redirectErrorStream(true);

        Process processo = processBuilder.start();
        StringBuilder saida = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream(), "UTF-8"));
        String linha;
        while ((linha = reader.readLine()) != null) {
            saida.append(linha).append(System.lineSeparator());
        }

        int exitCode = processo.waitFor();
        if (exitCode != 0) {
            throw new IOException("O programa OCaml terminou com erro: " + exitCode + System.lineSeparator() + saida.toString());
        }

        return saida.toString();
    }
}
