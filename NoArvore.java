/**
 * Classe de domínio estrutural que representa um Nó da Árvore de Pesquisa
 * Binária (BST).
 * Construída estritamente com referências manuais, atuando como um contêiner
 * para os objetos da classe Pedido.
 */
public class NoArvore {

    // O "payload" ou carga útil do nó
    private final Pedido pedido;

    // Chave efetiva de ordenação cacheada para otimização de CPU nas comparações
    // O(h)
    private final long chaveOrdenacao;

    // Ponteiros manuais para as subárvores (filhos)
    private NoArvore esquerda;
    private NoArvore direita;

    /**
     * Construtor do Nó da Árvore.
     * Ao ser instanciado, um nó é sempre uma folha (esquerda e direita nulas).
     * 
     * @param pedido O pedido a ser encapsulado neste vértice.
     */
    public NoArvore(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("O pedido encapsulado no nó não pode ser nulo.");
        }
        this.pedido = pedido;

        // Aplicação da regra estrita de desempate exigida pelo escopo do trabalho:
        // (prazo_entrega * 1.000.000) + id_pedido
        this.chaveOrdenacao = ((long) pedido.getPrazoEntrega() * 1000000L) + pedido.getIdPedido();

        this.esquerda = null;
        this.direita = null;
    }

    // Getters para a travessia e manipulação algorítmica

    public Pedido getPedido() {
        return pedido;
    }

    public long getChaveOrdenacao() {
        return chaveOrdenacao;
    }

    public NoArvore getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(NoArvore esquerda) {
        this.esquerda = esquerda;
    }

    public NoArvore getDireita() {
        return direita;
    }

    public void setDireita(NoArvore direita) {
        this.direita = direita;
    }
}