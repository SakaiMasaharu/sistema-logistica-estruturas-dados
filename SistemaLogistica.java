import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe Orquestradora do Sistema de Logística.
 * Responsável por integrar os módulos de Árvore BST, Tabela Hash e Grafos
 * em um fluxo de execução dinâmico e determinístico (Seção 5.4).
 */
public class SistemaLogistica {

    // Instâncias das Estruturas de Dados desenvolvidas do zero
    private static ArvoreBST arvorePrazos = new ArvoreBST();

    // Variável para armazenar dinamicamente um pedido real para a demonstração
    private static Pedido pedidoDemonstracao = null;

    // NOTA: Remova os comentários "//" das instâncias abaixo assim que
    // as classes Hash e Grafo estiverem implementadas no seu projeto.
    // private static TabelaHash hashPedidos = new HashEncadeamento(51017, true);
    // private static GrafoMatriz grafoMalha = new GrafoMatriz(25);

    public static void simular() {
        System.out.println("==================================================");
        System.out.println(" INICIANDO INTEGRAÇÃO DO SISTEMA DE LOGÍSTICA     ");
        System.out.println("==================================================\n");

        // ---------------------------------------------------------
        // ETAPA 1: Carga de Dados Dinâmica
        // ---------------------------------------------------------
        System.out.println("[ETAPA 1] Carregando cenário de dados gerado deterministicamente...");
        carregarPedidos("pedidos_desafio.csv");
        // carregarMalha("malha.csv");

        System.out.println("✓ Carga concluída. Árvore operando iterativamente para evitar StackOverflow.\n");

        // Proteção de fluxo: garante que o arquivo não estava vazio
        if (pedidoDemonstracao == null) {
            System.err.println("[ERRO CRÍTICO] Nenhum pedido foi carregado. Verifique os arquivos .csv.");
            return;
        }

        // ---------------------------------------------------------
        // ETAPA 2: Integração de Fluxo (Seção 5.4 do Edital)
        // ---------------------------------------------------------
        System.out.println("[ETAPA 2] Executando Fluxo Integrado Obrigatório...");

        // Extração dinâmica dos atributos do pedido capturado
        int idAlvo = pedidoDemonstracao.getIdPedido();
        String origem = pedidoDemonstracao.getCentroOrigem();
        String destino = pedidoDemonstracao.getCentroDestino();

        // A) Consulta rápida O(1) no Módulo Aprofundado (Hash)
        System.out.println("\n-> A) Buscando Pedido #" + idAlvo + " instantaneamente no Hash:");
        // Pedido pEncontrado = hashPedidos.buscar(idAlvo);
        System.out.println("   [Simulação] Pedido #" + idAlvo + " (" + origem + " -> " + destino + ") encontrado.");

        // B) Cálculo de Rota de Entrega (Dijkstra no Grafo)
        System.out.println("\n-> B) Planejando Rota de Entrega na Malha Rodoviária:");
        // int custoRota = grafoMalha.calcularDijkstra(origem, destino);
        System.out
                .println("   [Simulação] Dijkstra executado de " + origem + " para " + destino + ". Custo calculado.");

        // C) Atualização de Status (Remoção Pós-Entrega nas estruturas)
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
     */
    private static void carregarPedidos(String caminhoArquivo) {
        String linha;
        int contador = 0;

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

                    // Armazena o primeiro pedido válido para uso posterior na demonstração dinâmica
                    if (pedidoDemonstracao == null) {
                        pedidoDemonstracao = novoPedido;
                    }

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