/**
 * Implementação de Tabela Hash utilizando Endereçamento Aberto (Sondagem
 * Linear)
 * para resolução de colisões. Inclui suporte a Tombstones (Lápides) para
 * remoção segura,
 * garantindo integridade das cadeias de sondagem O(1) médio.
 */
public class HashEnderecamentoAberto implements TabelaHash {

    private final Pedido[] tabela;
    private final int M; // Capacidade total do array
    private int quantidadeElementos;
    private final boolean usarMetodoDivisao;

    // Objeto Sentinela (Tombstone) usado para marcar posições deletadas.
    // Impede o rompimento da corrente de sondagem linear.
    private static final Pedido TOMBSTONE = new Pedido(-1, "DELETADO", -1, "NONE", "NONE");

    // Variáveis de estado para extração de métricas de complexidade empírica
    private int elementosExaminadosUltimaBusca;
    private int tamanhoMaiorSondagem;

    /**
     * Construtor da Tabela Hash com Endereçamento Aberto.
     * 
     * @param tamanho           O tamanho fixo do array base (M).
     * @param usarMetodoDivisao Se true, usa divisão. Se false, usa multiplicação.
     */
    public HashEnderecamentoAberto(int tamanho, boolean usarMetodoDivisao) {
        this.M = tamanho;
        this.tabela = new Pedido[M];
        this.quantidadeElementos = 0;
        this.usarMetodoDivisao = usarMetodoDivisao;
        this.tamanhoMaiorSondagem = 0;
        this.elementosExaminadosUltimaBusca = 0;
    }

    /**
     * Função Hash Central O(1).
     */
    private int calcularHash(int chave) {
        if (usarMetodoDivisao) {
            return chave % M;
        } else {
            // Método da Multiplicação: h(k) = piso( M * ( (k * A) mod 1 ) )
            final double A = 0.6180339887;
            double parteFracionaria = (chave * A) % 1;
            return (int) (M * parteFracionaria);
        }
    }

    @Override
    public void inserir(Pedido p) {
        // Prevenção de loop infinito: Tabela atingiu carga máxima (α = 1)
        if (quantidadeElementos >= M) {
            System.err.println("HashEnderecamentoAberto: Overflow! Não é possível inserir Pedido " + p.getIdPedido());
            return;
        }

        int indiceInicial = calcularHash(p.getIdPedido());
        int saltos = 0;
        int indiceSondagem = indiceInicial;

        // Sondagem Linear: Procura o próximo slot que seja null ou uma Tombstone
        while (tabela[indiceSondagem] != null && tabela[indiceSondagem] != TOMBSTONE) {
            saltos++;
            indiceSondagem = (indiceInicial + saltos) % M; // Aritmética modular para comportamento circular
        }

        tabela[indiceSondagem] = p;
        quantidadeElementos++;

        // Coleta de métrica: rastreia o pior caso global de colisão
        if (saltos > tamanhoMaiorSondagem) {
            tamanhoMaiorSondagem = saltos;
        }
    }

    @Override
    public Pedido buscar(int idPedido) {
        this.elementosExaminadosUltimaBusca = 0; // Reset para o experimento atual
        int indiceInicial = calcularHash(idPedido);
        int saltos = 0;
        int indiceSondagem = indiceInicial;

        // A sondagem só para ao encontrar um 'null' verdadeiro
        while (tabela[indiceSondagem] != null) {
            this.elementosExaminadosUltimaBusca++;

            // Verifica o elemento apenas se não for uma Lápide
            if (tabela[indiceSondagem] != TOMBSTONE && tabela[indiceSondagem].getIdPedido() == idPedido) {
                return tabela[indiceSondagem];
            }

            saltos++;
            indiceSondagem = (indiceInicial + saltos) % M;

            // Condição de parada de segurança: O(N) atingido, varreu todo o array sem
            // encontrar
            if (saltos >= M) {
                break;
            }
        }

        return null; // Elemento não encontrado
    }

    @Override
    public boolean remover(int idPedido) {
        int indiceInicial = calcularHash(idPedido);
        int saltos = 0;
        int indiceSondagem = indiceInicial;

        while (tabela[indiceSondagem] != null) {
            if (tabela[indiceSondagem] != TOMBSTONE && tabela[indiceSondagem].getIdPedido() == idPedido) {

                // Marcação arquitetural: Substitui pela Lápide para preservar a busca
                tabela[indiceSondagem] = TOMBSTONE;
                quantidadeElementos--;
                return true; // Removido com sucesso
            }

            saltos++;
            indiceSondagem = (indiceInicial + saltos) % M;

            if (saltos >= M) {
                break;
            }
        }
        return false; // Não estava na tabela
    }

    // ========================================================================
    // MÉTRICAS DE APROFUNDAMENTO OBRIGATÓRIAS
    // ========================================================================

    @Override
    public double getFatorDeCarga() {
        return (double) quantidadeElementos / M;
    }

    @Override
    public int getTamanhoMaiorCadeiaOuSondagem() {
        return tamanhoMaiorSondagem;
    }

    @Override
    public int getElementosExaminadosUltimaBusca() {
        return elementosExaminadosUltimaBusca;
    }
}