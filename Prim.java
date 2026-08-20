/**
 * Implementação do Algoritmo de Prim para descoberta da Árvore Geradora Mínima
 * (AGM).
 * Utiliza busca linear O(|V|²) projetada especificamente para operar sobre
 * a Matriz de Adjacência, respeitando a restrição de ausência de PriorityQueue.
 */
public class Prim {

    private final GrafoMatriz grafo;

    // Métrica obrigatória: Custo total da infraestrutura
    private int custoTotalAGM;

    public Prim(GrafoMatriz grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("Grafo não pode ser nulo.");
        }
        this.grafo = grafo;
        this.custoTotalAGM = 0;
    }

    /**
     * Executa o algoritmo para interligar todos os vértices com o menor custo
     * global.
     */
    public void calcularAGM() {
        int V = grafo.getV();

        // Arrays de controle do estado algorítmico
        int[] pai = new int[V]; // Armazena a aresta que conecta o vértice à AGM
        int[] chave = new int[V]; // Armazena o menor peso encontrado para conectar o vértice
        boolean[] incluidoNaAGM = new boolean[V]; // Rastreia os vértices já consolidados

        // Inicialização O(|V|)
        for (int i = 0; i < V; i++) {
            chave[i] = Integer.MAX_VALUE;
            incluidoNaAGM[i] = false;
        }

        // O vértice 0 (arbitrário) é a raiz da Árvore Geradora
        chave[0] = 0;
        pai[0] = -1; // A raiz não tem pai

        // A AGM de um grafo conexo terá exatamente V - 1 arestas
        for (int count = 0; count < V - 1; count++) {

            // 1. Seleciona o vértice com a menor chave que ainda não está na AGM
            int u = encontrarMenorChave(chave, incluidoNaAGM, V);

            // Proteção contra grafos desconexos (evita NullPointerException)
            if (u == -1)
                break;

            incluidoNaAGM[u] = true;

            // 2. Atualiza a chave e o índice do pai dos vértices adjacentes a 'u'
            for (int v = 0; v < V; v++) {
                int pesoAresta = grafo.getCusto(u, v);

                // Atualiza se: existe aresta (peso != -1 e != 0), o destino não está na AGM,
                // e o custo da aresta é menor que a chave atual registrada para o destino.
                if (pesoAresta != -1 && pesoAresta != 0
                        && !incluidoNaAGM[v]
                        && pesoAresta < chave[v]) {

                    pai[v] = u;
                    chave[v] = pesoAresta;
                }
            }
        }

        gerarRelatorioAGM(pai, V);
    }

    /**
     * Função auxiliar de extração do mínimo em tempo O(|V|).
     */
    private int encontrarMenorChave(int[] chave, boolean[] incluidoNaAGM, int V) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 0; v < V; v++) {
            if (!incluidoNaAGM[v] && chave[v] < min) {
                min = chave[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    /**
     * Consolida e formata a extração da métrica obrigatória.
     */
    private void gerarRelatorioAGM(int[] pai, int V) {
        this.custoTotalAGM = 0;

        System.out.println("--- RELATÓRIO ESTRATÉGICO: ÁRVORE GERADORA MÍNIMA (PRIM) ---");
        System.out.println("Aresta \t\t\t Custo");
        System.out.println("--------------------------------------------------");

        // Começa de 1 porque o vértice 0 é a raiz (pai == -1)
        for (int i = 1; i < V; i++) {
            if (pai[i] != -1) {
                int custoAresta = grafo.getCusto(pai[i], i);
                this.custoTotalAGM += custoAresta;

                String origem = grafo.getNomeVertice(pai[i]);
                String destino = grafo.getNomeVertice(i);

                System.out.printf("%s <-> %s \t %d\n", origem, destino, custoAresta);
            }
        }

        System.out.println("--------------------------------------------------");
        System.out.println("[Métrica de Corretude] Custo Total da Malha Otimizada: " + this.custoTotalAGM);
        System.out.println("--------------------------------------------------\n");
    }

    // Getter para a métrica exigida no relatório final
    public int getCustoTotalAGM() {
        return this.custoTotalAGM;
    }
}