/**
 * Classe de domínio que representa uma Aresta no Grafo da malha logística.
 * É utilizada para mapear as distâncias entre os Centros de Distribuição (CDs)
 * e para compor a Árvore Geradora Mínima (AGM) no algoritmo de Prim.
 */
public class Aresta {

    // Representamos os vértices como inteiros para acesso O(1) em arrays estáticos
    private final int origem;
    private final int destino;
    private final int custo;

    /**
     * Construtor da Aresta.
     * 
     * @param origem  Índice numérico do vértice de origem (ex: 0 para CD_CTBA)
     * @param destino Índice numérico do vértice de destino (ex: 1 para CD_LOND)
     * @param custo   O peso da aresta (distância em km, tempo ou frete)
     */
    public Aresta(int origem, int destino, int custo) {
        this.origem = origem;
        this.destino = destino;
        this.custo = custo;
    }

    public int getOrigem() {
        return origem;
    }

    public int getDestino() {
        return destino;
    }

    public int getCusto() {
        return custo;
    }

    /**
     * Retorna o vértice oposto dado um dos vértices da aresta.
     * Útil para travessias em grafos não direcionados.
     */
    public int getVeredaOposta(int verticeConhecido) {
        return (verticeConhecido == origem) ? destino : origem;
    }

    @Override
    public String toString() {
        return String.format("Aresta(v%d -- v%d : peso %d)", origem, destino, custo);
    }
}