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
        // Guarda o caminho para o programa OCaml compilado
        this.executavel = executavel;
    }

    public String executar(String... argumentos) throws IOException, InterruptedException {
        // Construcao do comando a executar (ex: ./main.exe avaliar 1)
        List<String> comando = new ArrayList<String>();
        comando.add(executavel);
        comando.addAll(Arrays.asList(argumentos));

        ProcessBuilder processBuilder = new ProcessBuilder(comando);
        processBuilder.directory(new File("."));
        processBuilder.redirectErrorStream(true);

        // Arranque do processo OCaml
        Process processo = processBuilder.start();
        StringBuilder saida = new StringBuilder();

        // Leitura da saida do programa OCaml linha a linha
        BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream(), "UTF-8"));
        String linha;
        while ((linha = reader.readLine()) != null) {
            saida.append(linha).append(System.lineSeparator());
        }

        int exitCode = processo.waitFor();
        if (exitCode != 0) {
            // Caso o OCaml falhe, devolve erro com o output capturado
            throw new IOException("O programa OCaml terminou com erro: " + exitCode + System.lineSeparator() + saida.toString());
        }

        return saida.toString();
    }
}