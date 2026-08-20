/**
 * Classe utilitária responsável por extrair e demonstrar a Instrumentação de
 * Métricas
 * das Tabelas Hash (Tarefa 2.4). Valida empiricamente o custo computacional
 * e o nível de colisão de cada abordagem implementada.
 */
public class MonitorMetricasHash {

    public static void main(String[] args) {
        System.out.println("=== RELATÓRIO EMPÍRICO: MÓDULO APROFUNDADO (HASH) ===\n");

        // Tamanho M arbitrário para demonstração (idealmente um número primo para
        // divisão,
        // mas usaremos potência de 2 para estressar a diferença entre Divisão e
        // Multiplicação).
        int tamanhoTabela = 1000;

        // Instanciação das 4 combinações exigidas pelo trabalho
        TabelaHash encadeamentoDivisao = new HashEncadeamento(tamanhoTabela, true);
        TabelaHash encadeamentoMultiplicacao = new HashEncadeamento(tamanhoTabela, false);

        TabelaHash abertoDivisao = new HashEnderecamentoAberto(tamanhoTabela, true);
        TabelaHash abertoMultiplicacao = new HashEnderecamentoAberto(tamanhoTabela, false);

        System.out.println("[*] Injetando carga de teste (800 pedidos - Fator de Carga Alvo: 0.8)...");

        // Simulação de injeção de dados. Em produção (Fase 5), isso virá do CSV gerado
        // pela sua semente.
        for (int i = 0; i < 800; i++) {
            // Geramos IDs sequenciais (que podem causar clustering na divisão)
            int id = 100000 + i;
            Pedido p = new Pedido(id, "Cliente_" + i, (i % 30) + 1, "CD_CTBA", "CD_LOND");

            encadeamentoDivisao.inserir(p);
            encadeamentoMultiplicacao.inserir(p);
            abertoDivisao.inserir(p);
            abertoMultiplicacao.inserir(p);
        }

        System.out.println("[*] Executando buscas para captura de complexidade...\n");
        // Buscando o pior caso possível (o último elemento inserido)
        int idBusca = 100799;
        encadeamentoDivisao.buscar(idBusca);
        encadeamentoMultiplicacao.buscar(idBusca);
        abertoDivisao.buscar(idBusca);
        abertoMultiplicacao.buscar(idBusca);

        // Impressão tabular comparativa das métricas instrumentadas
        imprimirMetricas("Encadeamento (Divisão)", encadeamentoDivisao);
        imprimirMetricas("Encadeamento (Multiplicação)", encadeamentoMultiplicacao);
        imprimirMetricas("Endereçamento Aberto (Divisão)", abertoDivisao);
        imprimirMetricas("Endereçamento Aberto (Multiplicação)", abertoMultiplicacao);
    }

    /**
     * Consome as métricas encapsuladas na interface TabelaHash e formata a saída.
     */
    private static void imprimirMetricas(String estrategia, TabelaHash tabela) {
        System.out.println("--------------------------------------------------");
        System.out.println("Estratégia: " + estrategia);
        System.out.printf("Fator de Carga (α):           %.2f\n", tabela.getFatorDeCarga());
        System.out.println("Pior Colisão (Maior Cadeia):  " + tabela.getTamanhoMaiorCadeiaOuSondagem() + " elementos");
        System.out.println(
                "Custo da Busca (O(x)):        " + tabela.getElementosExaminadosUltimaBusca() + " nós inspecionados");
    }
}