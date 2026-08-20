/**
 * Representação estrutural do Grafo utilizando Matriz de Adjacência.
 * Escolha arquitetural ideal para grafos densos de pequeno porte (|V| <= 25),
 * garantindo acesso O(1) ao custo das arestas e alinhando-se à restrição
 * de uso exclusivo de arrays estáticos.
 */
public class GrafoMatriz {

    // Matriz de adjacência onde a interseção [u][v] guarda o custo da aresta.
    // O valor 0 (ou infinito) indica ausência de conexão.
    private final int[][] matrizAdjacencia;

    // Array auxiliar para mapear os índices numéricos de volta aos nomes dos
    // Centros
    private final String[] nomesVertices;

    // A constante V representa o número total de vértices no grafo
    private final int V;

    // Variáveis para rastreio das métricas de espaço exigidas na avaliação
    private int quantidadeArestas;

    /**
     * Construtor da estrutura do Grafo.
     * 
     * @param numVertices A quantidade de Centros de Distribuição (10 a 25).
     */
    public GrafoMatriz(int numVertices) {
        this.V = numVertices;
        this.matrizAdjacencia = new int[V][V];
        this.nomesVertices = new String[V];
        this.quantidadeArestas = 0;

        // Inicializa a matriz com "infinito" simulado (-1) para representar
        // a ausência de aresta, evitando colisão com arestas de custo 0.
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i != j) {
                    matrizAdjacencia[i][j] = -1;
                } else {
                    matrizAdjacencia[i][j] = 0; // Custo de um vértice para ele mesmo é sempre 0
                }
            }
        }
    }

    /**
     * Associa um identificador textual a um índice numérico O(1).
     */
    public void registrarVertice(int indice, String nomeCentro) {
        if (indice >= 0 && indice < V) {
            this.nomesVertices[indice] = nomeCentro;
        }
    }

    /**
     * Insere uma aresta não direcionada no grafo. Custo: O(1).
     * 
     * @param u     Índice do vértice de origem
     * @param v     Índice do vértice de destino
     * @param custo Custo de transporte/distância
     */
    public void adicionarAresta(int u, int v, int custo) {
        // Grafo não direcionado: a matriz deve ser simétrica
        if (matrizAdjacencia[u][v] == -1) {
            this.quantidadeArestas++; // Conta apenas uma vez por par não direcionado
        }
        this.matrizAdjacencia[u][v] = custo;
        this.matrizAdjacencia[v][u] = custo;
    }

    // ========================================================================
    // GETTERS PARA OS ALGORITMOS DE DIJKSTRA E PRIM
    // ========================================================================

    public int getV() {
        return V;
    }

    public int getCusto(int u, int v) {
        return matrizAdjacencia[u][v];
    }

    public String getNomeVertice(int indice) {
        return nomesVertices[indice];
    }

    // ========================================================================
    // MÉTRICAS OBRIGATÓRIAS (Análise Empírica e Relatório)
    // ========================================================================

    /**
     * Calcula o custo de espaço analítico ocupado pela representação da matriz,
     * evidenciando o trade-off central: O(|V|²).
     * 
     * @return Uma string contendo a complexidade de memória.
     */
    public String getCustoDeEspaco() {
        int espacoMatriz = V * V;
        return String.format("Matriz de Adjacência ocupando |V|² = %d posições de memória. (Arestas reais: %d)",
                espacoMatriz, quantidadeArestas);
    }
}