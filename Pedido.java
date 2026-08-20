/**
 * Classe de domínio que representa um Pedido no Sistema de Logística.
 * Objetos desta classe são imutáveis após a criação (uso de campos final),
 * garantindo thread-safety e previsibilidade em estruturas de dados complexas.
 */
public class Pedido {

    private final int idPedido;
    private final String cliente;
    private final int prazoEntrega;
    private final String centroOrigem;
    private final String centroDestino;

    /**
     * Construtor da classe Pedido.
     */
    public Pedido(int idPedido, String cliente, int prazoEntrega, String centroOrigem, String centroDestino) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.prazoEntrega = prazoEntrega;
        this.centroOrigem = centroOrigem;
        this.centroDestino = centroDestino;
    }

    // Getters

    public int getIdPedido() {
        return idPedido;
    }

    public String getCliente() {
        return cliente;
    }

    public int getPrazoEntrega() {
        return prazoEntrega;
    }

    public String getCentroOrigem() {
        return centroOrigem;
    }

    public String getCentroDestino() {
        return centroDestino;
    }

    /**
     * Gera a chave composta para ordenação na Árvore de Busca (BST).
     * O uso do tipo 'long' evita Integer Overflow caso o prazo de entrega seja
     * elevado.
     * 
     * @return chave primária de ordenação: (prazo_entrega * 1000000) + id_pedido
     */
    public long getChaveArvore() {
        return ((long) this.prazoEntrega * 1000000L) + this.idPedido;
    }

    @Override
    public String toString() {
        return String.format("Pedido[ID: %06d | Prazo: %3d | Rota: %s -> %s | Cliente: %s]",
                idPedido, prazoEntrega, centroOrigem, centroDestino, cliente);
    }
}