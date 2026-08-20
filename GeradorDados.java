import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Classe responsável pela geração determinística de todos os dados do sistema.
 * Utiliza a semente pessoal para garantir reprodutibilidade e gera
 * dinamicamente
 * entre 10 e 25 Centros de Distribuição.
 */
public class GeradorDados {

    // Semente pessoal calculada: Charles Masaharu Sakai = 859L
    private static final long SEMENTE = 859L;
    private static final int MAX_ID_PEDIDO = 1000000;

    private static boolean[] idsGerados;
    private static String[] centrosDinamicos;

    public static void main(String[] args) {
        System.out.println("Iniciando geração de dados (Semente: " + SEMENTE + ")...");

        Random random = new Random(SEMENTE);

        // 1. Determina a topologia da Malha (10 a 25 centros)
        gerarMalhaCsvDinamica(random);

        // 2. Gera as cargas normais
        gerarPedidosCsv("pedidos_1k.csv", 1000, random);
        gerarPedidosCsv("pedidos_10k.csv", 10000, random);
        gerarPedidosCsv("pedidos_50k.csv", 50000, random);

        // 3. Gera o cenário desafio (Hash: múltiplos de 17)
        gerarPedidosDesafioCsv("pedidos_desafio.csv", 50000, random, 17);

        System.out.println("Geração concluída com sucesso! Todos os arquivos estão prontos.");
    }

    /**
     * Gera dinamicamente o número de CDs (10 a 25) e as conexões da malha.
     */
    private static void gerarMalhaCsvDinamica(Random random) {
        String arquivo = "malha.csv";

        // Define o número de centros estritamente entre 10 e 25
        int numCentros = 10 + random.nextInt(16);
        centrosDinamicos = new String[numCentros];

        for (int i = 0; i < numCentros; i++) {
            centrosDinamicos[i] = String.format("CD_%02d", i);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            writer.println("centro_origem,centro_destino,custo");

            int totalArestas = 0;

            // Passo A: Garantir conectividade (Árvore Geradora Linear)
            for (int i = 0; i < numCentros - 1; i++) {
                int custo = 50 + random.nextInt(450);
                writer.printf("%s,%s,%d\n", centrosDinamicos[i], centrosDinamicos[i + 1], custo);
                totalArestas++;
            }

            // Passo B: Adicionar densidade (arestas extras)
            for (int i = 0; i < numCentros; i++) {
                for (int j = i + 2; j < numCentros; j++) {
                    if (random.nextDouble() < 0.40) {
                        int custo = 50 + random.nextInt(450);
                        writer.printf("%s,%s,%d\n", centrosDinamicos[i], centrosDinamicos[j], custo);
                        totalArestas++;
                    }
                }
            }
            System.out
                    .println("-> " + arquivo + " gerado (" + numCentros + " Centros, " + totalArestas + " Conexões).");
        } catch (IOException e) {
            System.err.println("Erro ao gerar " + arquivo + ": " + e.getMessage());
        }
    }

    /**
     * Gera um arquivo de pedidos aleatório, referenciando os CDs dinâmicos.
     */
    private static void gerarPedidosCsv(String nomeArquivo, int quantidade, Random random) {
        idsGerados = new boolean[MAX_ID_PEDIDO];

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("id_pedido,cliente,prazo_entrega,centro_origem,centro_destino");

            for (int i = 0; i < quantidade; i++) {
                int idPedido = gerarIdUnico(random);
                String cliente = "Cliente_" + idPedido;
                int prazoEntrega = random.nextInt(30) + 1;

                int idxOrigem = random.nextInt(centrosDinamicos.length);
                int idxDestino = random.nextInt(centrosDinamicos.length);
                while (idxOrigem == idxDestino) {
                    idxDestino = random.nextInt(centrosDinamicos.length);
                }

                writer.printf("%d,%s,%d,%s,%s\n",
                        idPedido, cliente, prazoEntrega, centrosDinamicos[idxOrigem], centrosDinamicos[idxDestino]);
            }
            System.out.println("-> " + nomeArquivo + " gerado (" + quantidade + " registros).");
        } catch (IOException e) {
            System.err.println("Erro ao gerar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    /**
     * Cenário desafio: Força colisões gerando múltiplos da função Hash (M=17).
     */
    private static void gerarPedidosDesafioCsv(String nomeArquivo, int quantidade, Random random, int fatorColisao) {
        idsGerados = new boolean[MAX_ID_PEDIDO];

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("id_pedido,cliente,prazo_entrega,centro_origem,centro_destino");

            int multiplicadorBase = 1;
            for (int i = 0; i < quantidade; i++) {
                int idPedido = multiplicadorBase * fatorColisao;
                if (idPedido >= MAX_ID_PEDIDO) {
                    idPedido = gerarIdUnico(random);
                } else {
                    idsGerados[idPedido] = true;
                    multiplicadorBase++;
                }

                String cliente = "ClienteDesafio_" + idPedido;
                // No desafio, geramos prazos crescentes para estressar a inserção na Árvore
                int prazoEntrega = (i / 1000) + 1;

                int idxOrigem = random.nextInt(centrosDinamicos.length);
                int idxDestino = random.nextInt(centrosDinamicos.length);
                while (idxOrigem == idxDestino) {
                    idxDestino = random.nextInt(centrosDinamicos.length);
                }

                writer.printf("%d,%s,%d,%s,%s\n",
                        idPedido, cliente, prazoEntrega, centrosDinamicos[idxOrigem], centrosDinamicos[idxDestino]);
            }
            System.out.println("-> " + nomeArquivo + " gerado (" + quantidade + " registros com colisões).");
        } catch (IOException e) {
            System.err.println("Erro ao gerar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    private static int gerarIdUnico(Random random) {
        int idCandidato;
        do {
            idCandidato = random.nextInt(MAX_ID_PEDIDO);
        } while (idsGerados[idCandidato]);

        idsGerados[idCandidato] = true;
        return idCandidato;
    }
}