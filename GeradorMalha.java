import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Classe utilitária responsável pela geração determinística do arquivo
 * malha.csv.
 * Representa a infraestrutura física (Grafo Não Direcionado).
 */
public class GeradorMalha {

    // Semente pessoal calculada: Charles Masaharu Sakai = 859L
    private static final long SEMENTE = 859L;

    /**
     * Executa a geração do arquivo estruturando os vértices e arestas
     * de forma pseudoaleatória, porém 100% reprodutível via semente.
     */
    public static void gerarMalhaCsv() {
        String nomeArquivo = "malha.csv";

        // A instância Random carrega a semente determinística
        Random random = new Random(SEMENTE);

        // Define o número de centros estritamente entre 10 e 25
        int numCentros = 10 + random.nextInt(16);

        // Alocação em Array estático respeitando as restrições do edital
        String[] centros = new String[numCentros];
        for (int i = 0; i < numCentros; i++) {
            centros[i] = String.format("CD_%02d", i + 1);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("centro_origem,centro_destino,custo");

            int totalArestas = 0;

            // Passo A: Garantir conectividade mínima (Caminho Hamiltoniano simples)
            for (int i = 0; i < numCentros - 1; i++) {
                int custo = 50 + random.nextInt(450); // Custos entre 50 e 499
                writer.printf("%s,%s,%d\n", centros[i], centros[i + 1], custo);
                totalArestas++;
            }

            // Passo B: Adicionar densidade estocástica (arestas extras)
            for (int i = 0; i < numCentros; i++) {
                for (int j = i + 2; j < numCentros; j++) {
                    // 35% de chance de criar uma conexão direta entre dois CDs distantes
                    if (random.nextDouble() < 0.35) {
                        int custo = 50 + random.nextInt(450);
                        writer.printf("%s,%s,%d\n", centros[i], centros[j], custo);
                        totalArestas++;
                    }
                }
            }

            System.out.println("Sucesso: Arquivo '" + nomeArquivo + "' gerado de forma determinística.");
            System.out.println("-> " + numCentros + " Centros de Distribuição.");
            System.out.println("-> " + totalArestas + " Conexões criadas.");

        } catch (IOException e) {
            System.err.println("Falha crítica de I/O ao gerar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Inicializando motor de geração topológica (Semente: " + SEMENTE + ")...");
        gerarMalhaCsv();
    }
}