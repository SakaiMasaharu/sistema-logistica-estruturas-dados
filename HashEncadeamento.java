/**
 * Implementação de Tabela Hash utilizando Encadeamento (Listas Encadeadas
 * Manuais)
 * para resolução de colisões.
 * Módulo de Aprofundamento: Suporta os métodos da Divisão e Multiplicação.
 */
public class HashEncadeamento implements TabelaHash {

    /**
     * Classe interna estática representando o nó da lista encadeada manual.
     * Encapsulada para não expor a manipulação de ponteiros ao sistema principal.
     */
    private static class NoHash {
        final Pedido pedido;
        NoHash proximo;

        NoHash(Pedido pedido) {
            this.pedido = pedido;
            this.proximo = null;
        }
    }

    private final NoHash[] tabela;
    private final int M; // Tamanho do array base
    private int quantidadeElementos;
    private final boolean usarMetodoDivisao;

    // Variáveis para rastreamento das métricas exigidas
    private int elementosExaminadosUltimaBusca;
    private int maiorCadeiaRegistrada;
    private final int[] tamanhoCadeias; // Cache O(1) para monitorar o tamanho de cada bucket

    /**
     * Construtor da Tabela Hash com Encadeamento.
     * 
     * @param tamanho           O tamanho do array base (M).
     * @param usarMetodoDivisao Se true, usa divisão. Se false, usa multiplicação.
     */
    public HashEncadeamento(int tamanho, boolean usarMetodoDivisao) {
        this.M = tamanho;
        this.tabela = new NoHash[M];
        this.tamanhoCadeias = new int[M];
        this.quantidadeElementos = 0;
        this.elementosExaminadosUltimaBusca = 0;
        this.maiorCadeiaRegistrada = 0;
        this.usarMetodoDivisao = usarMetodoDivisao;
    }

    /**
     * Função Hash Central que roteia para o método escolhido.
     */
    private int calcularHash(int chave) {
        if (usarMetodoDivisao) {
            return chave % M;
        } else {
            // Constante A = (sqrt(5) - 1) / 2 (Razão Áurea)
            final double A = 0.6180339887;
            double parteFracionaria = (chave * A) % 1; // Extrai apenas a parte decimal
            return (int) (M * parteFracionaria);
        }
    }

    @Override
    public void inserir(Pedido p) {
        int indice = calcularHash(p.getIdPedido());
        NoHash novoNo = new NoHash(p);

        // Inserção em O(1): Adiciona o novo nó como a nova "cabeça" da lista
        novoNo.proximo = tabela[indice];
        tabela[indice] = novoNo;

        quantidadeElementos++;

        // Atualiza métricas de aprofundamento empírico
        tamanhoCadeias[indice]++;
        if (tamanhoCadeias[indice] > maiorCadeiaRegistrada) {
            maiorCadeiaRegistrada = tamanhoCadeias[indice];
        }
    }

    @Override
    public Pedido buscar(int idPedido) {
        this.elementosExaminadosUltimaBusca = 0; // Reseta a métrica para a nova operação
        int indice = calcularHash(idPedido);
        NoHash atual = tabela[indice];

        while (atual != null) {
            this.elementosExaminadosUltimaBusca++; // Coleta do custo real da busca

            if (atual.pedido.getIdPedido() == idPedido) {
                return atual.pedido;
            }
            atual = atual.proximo;
        }
        return null; // Retorna null se esgotar a cadeia sem encontrar
    }

    @Override
    public boolean remover(int idPedido) {
        int indice = calcularHash(idPedido);
        NoHash atual = tabela[indice];
        NoHash anterior = null;

        while (atual != null) {
            if (atual.pedido.getIdPedido() == idPedido) {
                // Caso 1: O elemento a ser removido é o primeiro da cadeia
                if (anterior == null) {
                    tabela[indice] = atual.proximo;
                }
                // Caso 2: O elemento está no meio ou no fim da cadeia
                else {
                    anterior.proximo = atual.proximo;
                }

                quantidadeElementos--;
                tamanhoCadeias[indice]--;
                return true;
            }
            anterior = atual;
            atual = atual.proximo;
        }
        return false;
    }

    // ========================================================================
    // IMPLEMENTAÇÃO DAS MÉTRICAS EXIGIDAS
    // ========================================================================

    @Override
    public double getFatorDeCarga() {
        return (double) quantidadeElementos / M;
    }

    @Override
    public int getTamanhoMaiorCadeiaOuSondagem() {
        return maiorCadeiaRegistrada;
    }

    @Override
    public int getElementosExaminadosUltimaBusca() {
        return elementosExaminadosUltimaBusca;
    }
}