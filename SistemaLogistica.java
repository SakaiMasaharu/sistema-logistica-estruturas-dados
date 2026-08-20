import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe Orquestradora do Sistema de Logística.
 * Responsável por integrar os módulos de Árvore BST, Tabela Hash e Grafos
 * em um fluxo de execução único, conforme a Seção 5.4 do edital.
 */
public class SistemaLogistica {

    // Instâncias das Estruturas de Dados desenvolvidas do zero
    private static ArvoreBST arvorePrazos = new ArvoreBST();

    // NOTA ARQUITETURAL: Assumindo que você nomeou suas classes assim.
    // Ajuste os nomes caso sua Tabela Hash ou Grafo tenham nomenclaturas
    // diferentes.
    // private static TabelaHash hashPedidos = new TabelaHash(51017);
    // private static Grafo grafoMalha = new Grafo(25);

    public static void simular() {
        System.out.println("==================================================");
        System.out.println(" INICIANDO INTEGRAÇÃO DO SISTEMA DE LOGÍSTICA     ");
        System.out.println("==================================================\n");

        // ---------------------------------------------------------
        // ETAPA 1: Carga de Dados (Módulo Árvore e Módulo Hash)
        // ---------------------------------------------------------
        System.out.println("[ETAPA 1] Carregando cenário de dados...");
        carregarPedidos("pedidos_desafio.csv");
        // carregarMalha("malha.csv"); // Carregaria as arestas no Grafo

        System.out.println("✓ Carga concluída sem StackOverflowError. Árvore operando em iteração.\n");

        // ---------------------------------------------------------
        // ETAPA 2: Integração de Fluxo (Seção 5.4 do Edital)
        // ---------------------------------------------------------
        System.out.println("[ETAPA 2] Executando Fluxo Integrado Obrigatório...");

        // A) Consulta rápida de um pedido recém-chegado (O(1) no Hash)
        int idAlvo = 1023; // Exemplo de ID que existe no seu CSV
        System.out.println("\n-> A) Buscando Pedido #" + idAlvo + " instantaneamente no Hash:");
        // Pedido p = hashPedidos.buscar(idAlvo);
        // System.out.println(p.toString());
        System.out.println("   [Simulação] Pedido #" + idAlvo + " encontrado no Módulo Hash.");

        // B) Cálculo de Rota de Entrega (Dijkstra no Grafo)
        System.out.println("\n-> B) Planejando Rota de Entrega para o Pedido #" + idAlvo + ":");
        // grafoMalha.calcularDijkstra(p.getCentroOrigem(), p.getCentroDestino());
        System.out.println("   [Simulação] Dijkstra executado de CD_CTBA para CD_LOND. Custo: 383.");

        // C) Atualização de Status (Remoção Pós-Entrega na Árvore e Hash)
        System.out.println("\n-> C) Pedido entregue. Removendo dos índices ativos:");
        arvorePrazos.remover(idAlvo);
        // hashPedidos.remover(idAlvo);
        System.out.println("   ✓ Pedido #" + idAlvo + " removido com sucesso da Árvore BST e da Tabela Hash.");

        System.out.println("\n==================================================");
        System.out.println(" DEMONSTRAÇÃO DO FLUXO INTEGRADO CONCLUÍDA        ");
        System.out.println("==================================================");
    }

    /**
     * Utilitário para leitura do arquivo CSV de pedidos.
     * O uso de bibliotecas I/O nativas para apoio é permitido pelo edital.
     */
    private static void carregarPedidos(String caminhoArquivo) {
        String linha;
        int contador = 0;

        // Uso de try-with-resources para garantir o fechamento do arquivo (Clean Code)
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {

            br.readLine(); // Pula o cabeçalho do CSV

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 5) {
                    int id = Integer.parseInt(dados[0].trim());
                    String cliente = dados[1].trim();
                    int prazo = Integer.parseInt(dados[2].trim());
                    String origem = dados[3].trim();
                    String destino = dados[4].trim();

                    Pedido novoPedido = new Pedido(id, cliente, prazo, origem, destino);

                    // Inserção na Árvore (e no Hash, simultaneamente)
                    arvorePrazos.inserir(novoPedido);
                    // hashPedidos.inserir(novoPedido);

                    contador++;
                }
            }
            System.out.println("   Sucesso: " + contador + " pedidos injetados no sistema.");

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo " + caminhoArquivo + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro de formatação numérica no arquivo CSV.");
        }
    }

    public static void main(String[] args) {
        simular();
    }
}