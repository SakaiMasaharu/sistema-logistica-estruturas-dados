import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe principal do Sistema de Logística e Entregas.
 * Orquestra o fluxo de execução único integrando Hash, Árvores e Grafos,
 * conforme exigência da Fase 5 do projeto.
 */
public class SistemaLogistica {

    // Estruturas de Dados Centrais
    private static GrafoMatriz malhaLogistica;
    private static TabelaHash indicePedidosHash;
    private static ArvoreBST indicePedidosArvore;

    // Dicionário rudimentar (Array Estático) para mapear nomes de CDs para índices
    // inteiros
    private static String[] mapaCentros;
    private static int quantidadeCentros = 0;

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println(" INICIANDO SISTEMA DE LOGÍSTICA E ENTREGAS (INTEGRAÇÃO)  ");
        System.out.println("=========================================================\n");

        // Passo 1: Inicialização e Carga da Infraestrutura (Grafos)
        carregarMalha("malha.csv");

        // Instanciação das estruturas de busca com tamanho coerente para 50k registros
        // Usamos Endereçamento Aberto (Multiplicação) como exemplo para a integração
        indicePedidosHash = new HashEnderecamentoAberto(100000, false);
        indicePedidosArvore = new ArvoreBST();

        // Passo 2: Carga Operacional (Novos pedidos chegam)
        carregarPedidos("pedidos_10k.csv"); // Utilizando o arquivo de 10k como demonstração de volume

        // Passo 3: Fluxo Integrado de Operação (O Ciclo de Vida)
        simularFluxoOperacional(10050, 10055); // Intervalo de IDs arbitrários para simular despachos
    }

    /**
     * Realiza o parser do arquivo da malha, descobre o número de vértices
     * dinamicamente
     * e popula a Matriz de Adjacência.
     */
    private static void carregarMalha(String arquivo) {
        System.out.println("[SISTEMA] Lendo infraestrutura física: " + arquivo);

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            br.readLine(); // Pula o cabeçalho

            // Como o número de CDs gerados pela semente varia de 10 a 25,
            // alocamos o limite máximo para o mapa textual
            mapaCentros = new String[30];

            // Usaremos uma matriz temporária para segurar os dados antes de instanciar o
            // Grafo
            String[][] conexoesTemp = new String[1000][3];
            int numConexoes = 0;

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                conexoesTemp[numConexoes][0] = dados[0];
                conexoesTemp[numConexoes][1] = dados[1];
                conexoesTemp[numConexoes][2] = dados[2];

                registrarCentroNoMapa(dados[0]);
                registrarCentroNoMapa(dados[1]);
                numConexoes++;
            }

            // Inicializa a Matriz de Adjacência exata com o número de vértices descobertos
            malhaLogistica = new GrafoMatriz(quantidadeCentros);

            // Popula os nomes para o relatório
            for (int i = 0; i < quantidadeCentros; i++) {
                malhaLogistica.registrarVertice(i, mapaCentros[i]);
            }

            // Popula as arestas
            for (int i = 0; i < numConexoes; i++) {
                int u = obterIndiceCentro(conexoesTemp[i][0]);
                int v = obterIndiceCentro(conexoesTemp[i][1]);
                int custo = Integer.parseInt(conexoesTemp[i][2]);
                malhaLogistica.adicionarAresta(u, v, custo);
            }

            System.out.println("[SISTEMA] Malha carregada: " + quantidadeCentros + " CDs ativos.");
            System.out.println("[MÉTRICA] " + malhaLogistica.getCustoDeEspaco() + "\n");

        } catch (IOException e) {
            System.err.println("Erro ao carregar a malha: " + e.getMessage());
        }
    }

    /**
     * Lê os pedidos do CSV e os insere simultaneamente nas duas estruturas de
     * busca.
     */
    private static void carregarPedidos(String arquivo) {
        System.out.println("[SISTEMA] Recebendo lote de pedidos: " + arquivo);
        int inseridos = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            br.readLine(); // Pula cabeçalho

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                int id = Integer.parseInt(dados[0]);
                String cliente = dados[1];
                int prazo = Integer.parseInt(dados[2]);
                String origem = dados[3];
                String destino = dados[4];

                Pedido p = new Pedido(id, cliente, prazo, origem, destino);

                // Inserção Dual obrigatória (Hash para IDs, Árvore para Prazos)
                indicePedidosHash.inserir(p);
                indicePedidosArvore.inserir(p);
                inseridos++;
            }

            System.out.println("[SISTEMA] " + inseridos + " pedidos indexados nas estruturas de dados.\n");

        } catch (IOException e) {
            System.err.println("Erro ao carregar pedidos: " + e.getMessage());
        }
    }

    /**
     * Executa o fluxo contínuo obrigatório: Consulta -> Roteamento -> Baixa.
     */
    private static void simularFluxoOperacional(int idInicial, int idFinal) {
        System.out.println("=========================================================");
        System.out.println(" INICIANDO OPERAÇÃO DE DESPACHO E BAIXA                  ");
        System.out.println("=========================================================\n");

        for (int idAlvo = idInicial; idAlvo <= idFinal; idAlvo++) {
            System.out.println(">>> Processando Pedido ID: " + idAlvo);

            // 1. Consulta Rápida O(1) no Hash
            Pedido pedido = indicePedidosHash.buscar(idAlvo);

            if (pedido != null) {
                System.out.println("    [1/3] Pedido Localizado (Hash): " + pedido.getCliente() + " | Prazo: "
                        + pedido.getPrazoEntrega() + " dias.");

                int u = obterIndiceCentro(pedido.getCentroOrigem());
                int v = obterIndiceCentro(pedido.getCentroDestino());

                // 2. Roteamento Logístico (Dijkstra)
                System.out.println("    [2/3] Calculando rota de entrega...");
                Dijkstra roteirizador = new Dijkstra(malhaLogistica);
                roteirizador.calcularCaminhoMinimo(u, v);

                // 3. Baixa no Sistema (Remoção Dual)
                System.out.println("    [3/3] Confirmando entrega e efetuando baixa no sistema...");
                indicePedidosHash.remover(pedido.getIdPedido());
                indicePedidosArvore.removerPorId(pedido.getIdPedido());
                System.out.println("    [OK] Pedido " + idAlvo + " removido dos índices.\n");

            } else {
                System.out.println("    [ERRO] Pedido " + idAlvo + " não encontrado no Hash (O(1)).\n");
            }
        }

        System.out.println("=========================================================");
        System.out.println(" PLANEJAMENTO DE INFRAESTRUTURA (PRIM)                   ");
        System.out.println("=========================================================\n");

        // 4. Execução do Prim para planejamento de malha
        Prim planejador = new Prim(malhaLogistica);
        planejador.calcularAGM();
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS (Mapeamento String -> Int O(V))
    // ========================================================================

    private static void registrarCentroNoMapa(String nomeCentro) {
        for (int i = 0; i < quantidadeCentros; i++) {
            if (mapaCentros[i].equals(nomeCentro))
                return; // Já existe
        }
        mapaCentros[quantidadeCentros] = nomeCentro;
        quantidadeCentros++;
    }

    private static int obterIndiceCentro(String nomeCentro) {
        for (int i = 0; i < quantidadeCentros; i++) {
            if (mapaCentros[i].equals(nomeCentro))
                return i;
        }
        return -1;
    }
}