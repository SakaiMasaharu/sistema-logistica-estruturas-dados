/**
 * Interface que define o contrato para as implementações de Tabela Hash.
 * Garante o polimorfismo no Módulo Integrado e padroniza a extração
 * das métricas de complexidade exigidas na avaliação empírica.
 */
public interface TabelaHash {

    /**
     * Insere um novo pedido na tabela de dispersão.
     * 
     * @param p O objeto Pedido a ser inserido (a chave será p.getIdPedido()).
     */
    void inserir(Pedido p);

    /**
     * Busca um pedido na tabela em complexidade O(1) no caso médio.
     * Deve atualizar a métrica interna de elementos examinados.
     * 
     * @param idPedido O identificador numérico único do pedido.
     * @return O objeto Pedido caso encontrado, ou null caso não exista.
     */
    Pedido buscar(int idPedido);

    /**
     * Remove um pedido da tabela.
     * 
     * @param idPedido O identificador numérico único do pedido.
     * @return true se o pedido foi encontrado e removido, false caso contrário.
     */
    boolean remover(int idPedido);

    // ========================================================================
    // MÉTRICAS OBRIGATÓRIAS (Extração para Análise Empírica e Relatório)
    // ========================================================================

    /**
     * Retorna o fator de carga da tabela (α = n/m), onde 'n' é o número de
     * elementos armazenados e 'm' é o tamanho total da estrutura.
     * Indica o nível de ocupação da tabela.
     * 
     * @return Fator de carga atual.
     */
    double getFatorDeCarga();

    /**
     * Retorna o tamanho do pior caso observado na estrutura.
     * No Encadeamento: representa o tamanho da maior lista ligada (cadeia).
     * No Endereçamento Aberto: representa o maior número de saltos (sondagens)
     * necessários durante uma inserção.
     * 
     * @return O valor absoluto do pior caso registrado.
     */
    int getTamanhoMaiorCadeiaOuSondagem();

    /**
     * Retorna a quantidade exata de nós ou posições inspecionadas durante
     * a última operação de busca (buscar()).
     * Evidencia o custo real da operação, permitindo validar a degradação para O(n)
     * no Cenário Desafio.
     * 
     * @return Número de elementos examinados.
     */
    int getElementosExaminadosUltimaBusca();
}