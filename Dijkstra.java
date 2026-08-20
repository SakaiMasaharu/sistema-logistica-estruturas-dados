/**
 * Implementação do Algoritmo de Dijkstra para cálculo de rotas de menor custo.
 * Utiliza busca linear O(|V|²) projetada especificamente para operar sobre
 * matrizes de adjacência densas e restritas (|V| <= 25).
 */
public class Dijkstra {

    private final GrafoMatriz grafo;

    // Variáveis de estado para métricas de observabilidade (Big-O empírico)
    private int verticesVisitados;
    private int arestasVisitadas;

    public Dijkstra(GrafoMatriz grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("Grafo não pode ser nulo.");
        }
        this.grafo = grafo;
    }

    /**
     * Encontra a rota de menor custo entre a origem e o destino.
     * 
     * @param indiceOrigem  Índice numérico do CD de origem
     * @param indiceDestino Índice numérico do CD de destino
     */
    public void calcularCaminhoMinimo(int indiceOrigem, int indiceDestino) {
        int V = grafo.getV();
        this.verticesVisitados = 0;
        this.arestasVisitadas = 0;

        // Arrays de controle do algoritmo
        int[] distancias = new int[V];
        boolean[] processado = new boolean[V];
        int[] pai = new int[V]; // Mantém o rastro do caminho para reconstrução

        // Inicialização do estado basal O(|V|)
        for (int i = 0; i < V; i++) {
            distancias[i] = Integer.MAX_VALUE;
            processado[i] = false;
            pai[i] = -1;
        }

        distancias[indiceOrigem] = 0;

        // Loop principal O(|V|²)
        for (int count = 0; count < V - 1; count++) {
            // 1. Extração do mínimo via Busca Linear
            int u = encontrarMenorDistancia(distancias, processado, V);

            // Grafo desconexo ou destino inalcançável
            if (u == -1)
                break;

            processado[u] = true;
            this.verticesVisitados++;

            // Early-exit: Se já consolidamos o destino, não precisamos varrer o resto do
            // grafo
            if (u == indiceDestino) {
                break;
            }

            // 2. Relaxamento das arestas adjacentes
            for (int v = 0; v < V; v++) {
                int pesoAresta = grafo.getCusto(u, v);

                // Se existe aresta (-1 é a nossa lápide para ausência de aresta)
                if (pesoAresta != -1 && pesoAresta != 0) {
                    this.arestasVisitadas++;

                    // Condição de relaxamento de Dijkstra
                    if (!processado[v] && distancias[u] != Integer.MAX_VALUE
                            && distancias[u] + pesoAresta < distancias[v]) {

                        distancias[v] = distancias[u] + pesoAresta;
                        pai[v] = u; // Guarda o vértice de onde viemos
                    }
                }
            }
        }

        imprimirRelatorioRota(indiceOrigem, indiceDestino, distancias, pai);
    }

    /**
     * Função auxiliar para encontrar o vértice não processado de menor distância.
     * Custo estrito: O(|V|).
     */
    private int encontrarMenorDistancia(int[] distancias, boolean[] processado, int V) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 0; v < V; v++) {
            if (!processado[v] && distancias[v] <= min) {
                min = distancias[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    /**
     * Reconstrói o caminho de trás para frente usando o array de pais
     * e formata a saída de console para o usuário.
     */
    private void imprimirRelatorioRota(int origem, int destino, int[] distancias, int[] pai) {
        System.out.println("--- RELATÓRIO DE ROTA (DIJKSTRA) ---");
        System.out.println("Origem:  " + grafo.getNomeVertice(origem));
        System.out.println("Destino: " + grafo.getNomeVertice(destino));

        if (distancias[destino] == Integer.MAX_VALUE) {
            System.out.println("Status: ERRO - Rota Inalcançável.");
            return;
        }

        System.out.println("Custo Total: " + distancias[destino]);

        // Reconstrução do caminho O(|V|)
        // Como não podemos usar ArrayList, usamos um array estático invertido
        int[] caminhoInverso = new int[grafo.getV()];
        int passos = 0;
        int atual = destino;

        while (atual != -1) {
            caminhoInverso[passos] = atual;
            atual = pai[atual];
            passos++;
        }

        System.out.print("Caminho: ");
        for (int i = passos - 1; i >= 0; i--) {
            System.out.print(grafo.getNomeVertice(caminhoInverso[i]));
            if (i > 0)
                System.out.print(" -> ");
        }
        System.out.println("\n[Métricas de Complexidade O(|V|²)]");
        System.out.println("Vértices Processados: " + this.verticesVisitados);
        System.out.println("Arestas Inspecionadas: " + this.arestasVisitadas);
        System.out.println("------------------------------------");
    }

    // Getters de métricas para a fase de integração
    public int getVerticesVisitados() {
        return verticesVisitados;
    }

    public int getArestasVisitadas() {
        return arestasVisitadas;
    }
}