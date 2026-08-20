/**
 * Classe de Transferência de Dados (DTO) que representa uma linha do arquivo
 * malha.csv.
 * Modela uma conexão física bidirecional (não direcionada) entre dois Centros
 * de Distribuição.
 */
public class Conexao {

    private final String centroOrigem;
    private final String centroDestino;
    private final int custo;

    /**
     * Construtor da Conexão.
     * 
     * @param centroOrigem  Identificador textual do CD de origem (ex: "CD_CTBA")
     * @param centroDestino Identificador textual do CD de destino (ex: "CD_LOND")
     * @param custo         Distância rodoviária ou custo do trecho em km
     */
    public Conexao(String centroOrigem, String centroDestino, int custo) {
        if (centroOrigem == null || centroDestino == null) {
            throw new IllegalArgumentException("Os centros de distribuição não podem ser nulos.");
        }
        if (custo < 0) {
            throw new IllegalArgumentException("O custo de transporte não pode ser negativo.");
        }
        this.centroOrigem = centroOrigem;
        this.centroDestino = centroDestino;
        this.custo = custo;
    }

    public String getCentroOrigem() {
        return centroOrigem;
    }

    public String getCentroDestino() {
        return centroDestino;
    }

    public int getCusto() {
        return custo;
    }

    /**
     * Verifica se a conexão contém um determinado Centro de Distribuição.
     * 
     * @param centro Identificador do CD a ser verificado
     * @return true se o CD for origem ou destino desta conexão
     */
    public boolean conecta(String centro) {
        return this.centroOrigem.equals(centro) || this.centroDestino.equals(centro);
    }

    @Override
    public String toString() {
        return String.format("Conexao[%s <---> %s | Custo: %d km]", centroOrigem, centroDestino, custo);
    }
}