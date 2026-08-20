import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Motor de Benchmarking automatizado para os três módulos do sistema.
 * Extrai as métricas de complexidade (Big-O empírico) exigidas para a
 * fundamentação teórica e prática do README.
 */
public class AnalisadorEmpirico {

    // M = 51017. Estrategicamente escolhido por ser maior que 50.000
    // e múltiplo de 17 (Cenário Desafio).
    private static final int TAMANHO_TABELA = 51017;

    // Arrays para mapeamento manual de vértices do Grafo (Sem uso de HashMap)
    private static String[] mapaCentros;
    private static int quantidadeCentros;

    public static void main(String[] args) {
        System.out.println("==================================================================================");
        System.out.println(" BENCHMARKING DE COMPLEXIDADE (MÓDULOS INTEGRADOS)  ");
        System.out.println("==================================================================================\n");

        String[] cenariosPedidos = {
                "pedidos_1k.csv",
                "pedidos_10k.csv",
                "pedidos_50k.csv",
                "pedidos_desafio.csv"
        };

        // 1. Auditoria dos Módulos de Hash (Aprofundado) e Árvore
        for (String arquivo : cenariosPedidos) {
            executarBateriaHashEArvore(arquivo);
        }

        // 2. Auditoria do Módulo de Grafos
        executarAuditoriaGrafos("malha.csv");
    }

    /**
     * Injeta a carga de pedidos nas 4 variantes de Hash e na Árvore BST.
     */
    private static void executarBateriaHashEArvore(String nomeArquivo) {
        System.out.println(">>> INJETANDO CARGA: [" + nomeArquivo + "] <<<");

        // Instancia as estruturas
        TabelaHash encadDivisao = new HashEncadeamento(TAMANHO_TABELA, true);
        TabelaHash encadMultip = new HashEncadeamento(TAMANHO_TABELA, false);
        TabelaHash abertoDivisao = new HashEnderecamentoAberto(TAMANHO_TABELA, true);
        TabelaHash abertoMultip = new HashEnderecamentoAberto(TAMANHO_TABELA, false);
        ArvoreBST arvorePrazos = new ArvoreBST();

        int ultimoIdProcessado = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            br.readLine(); // Pula o cabeçalho

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length < 5)
                    continue;

                int id = Integer.parseInt(dados[0]);
                ultimoIdProcessado = id;

                Pedido p = new Pedido(id, dados[1], Integer.parseInt(dados[2]), dados[3], dados[4]);

                // Inserção O(1) médio
                encadDivisao.inserir(p);
                encadMultip.inserir(p);
                abertoDivisao.inserir(p);
                abertoMultip.inserir(p);

                // Inserção O(h)
                arvorePrazos.inserir(p);
            }

            // Simula a busca do pior caso para auditoria de custo
            if (ultimoIdProcessado != -1) {
                encadDivisao.buscar(ultimoIdProcessado);
                encadMultip.buscar(ultimoIdProcessado);
                abertoDivisao.buscar(ultimoIdProcessado);
                abertoMultip.buscar(ultimoIdProcessado);

                // CORREÇÃO: Chamada padronizada para buscar o ID e forçar a varredura O(n)
                arvorePrazos.buscar(ultimoIdProcessado);
            }

            // Tabulação das Métricas do Hash (Módulo Aprofundado)
            System.out.println("[MÉTRICAS DO HASH]");
            System.out.println(String.format("%-32s | %-12s | %-15s | %-15s", "Estratégia Adotada", "Fator(α)",
                    "Pior Colisão", "Custo da Busca"));
            System.out.println("----------------------------------------------------------------------------------");
            imprimirLinhaHash("Encadeamento + Divisão", encadDivisao);
            imprimirLinhaHash("Encadeamento + Multiplicação", encadMultip);
            imprimirLinhaHash("End. Aberto + Divisão", abertoDivisao);
            imprimirLinhaHash("End. Aberto + Multiplicação", abertoMultip);

            // Tabulação das Métricas da Árvore
            System.out.println("\n[MÉTRICAS DA ÁRVORE (BST Desbalanceada)]");
            System.out.println(String.format("%-40s | %-20s", "Métrica Obrigatória", "Resultado"));
            System.out.println("----------------------------------------------------------------------------------");

            // Chamada para getAltura() para obter a altura da árvore
            System.out.println(String.format("%-40s | %-20d", "Altura Final da Árvore (Degradação)",
                    arvorePrazos.getAltura()));

            // Chamada para getNosVisitadosUltimaBusca() para obter a quantidade de nós
            // visitados
            System.out.println(String.format("%-40s | %-20d", "Nós Visitados na Busca por ID O(n)",
                    arvorePrazos.getNosVisitadosUltimaBusca()));
            System.out.println("----------------------------------------------------------------------------------\n");

        } catch (IOException e) {
            System.err.println("[ERRO] Arquivo " + nomeArquivo + " ausente. Rode o GeradorDados.");
        }
    }

    private static void imprimirLinhaHash(String nomeEstrategia, TabelaHash tabela) {
        System.out.println(String.format("%-32s | %.4f       | %-15d | %-15d",
                nomeEstrategia, tabela.getFatorDeCarga(), tabela.getTamanhoMaiorCadeiaOuSondagem(),
                tabela.getElementosExaminadosUltimaBusca()));
    }

    /**
     * Injeta a carga da malha viária e audita Dijkstra e Prim.
     */
    private static void executarAuditoriaGrafos(String arquivoMalha) {
        System.out.println(">>> AUDITORIA DE GRAFOS: [" + arquivoMalha + "] <<<");

        mapaCentros = new String[30]; // Buffer defensivo para a malha dinâmica
        quantidadeCentros = 0;
        GrafoMatriz malhaLogistica = null;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoMalha))) {
            String linha;
            br.readLine();

            String[][] conexoesTemp = new String[1000][3];
            int numArestas = 0;

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                conexoesTemp[numArestas][0] = dados[0];
                conexoesTemp[numArestas][1] = dados[1];
                conexoesTemp[numArestas][2] = dados[2];

                registrarVerticeUnico(dados[0]);
                registrarVerticeUnico(dados[1]);
                numArestas++;
            }

            malhaLogistica = new GrafoMatriz(quantidadeCentros);

            for (int i = 0; i < quantidadeCentros; i++) {
                malhaLogistica.registrarVertice(i, mapaCentros[i]);
            }
            for (int i = 0; i < numArestas; i++) {
                int u = obterIndiceVertice(conexoesTemp[i][0]);
                int v = obterIndiceVertice(conexoesTemp[i][1]);
                int custo = Integer.parseInt(conexoesTemp[i][2]);
                malhaLogistica.adicionarAresta(u, v, custo);
            }

            // Simulação de Caminho Mínimo (Vértices 0 a N-1 arbitrários para teste)
            Dijkstra dijkstra = new Dijkstra(malhaLogistica);
            dijkstra.calcularCaminhoMinimo(0, quantidadeCentros - 1);

            // Simulação de Infraestrutura Mínima
            Prim prim = new Prim(malhaLogistica);
            prim.calcularAGM();

            // Tabulação das Métricas de Grafos
            System.out.println("\n[MÉTRICAS DE GRAFOS (Matriz de Adjacência)]");
            System.out.println(String.format("%-55s | %-20s", "Métrica Obrigatória", "Resultado"));
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println(String.format("%-55s | %s", "Espaço da Representação (Trade-off de Matriz)",
                    malhaLogistica.getCustoDeEspaco()));
            System.out.println(
                    String.format("%-55s | %d", "Vértices Processados no Dijkstra", dijkstra.getVerticesVisitados()));
            System.out.println(
                    String.format("%-55s | %d", "Arestas Inspecionadas no Dijkstra", dijkstra.getArestasVisitadas()));
            System.out.println(String.format("%-55s | %d", "Custo Total da Árvore Geradora Mínima (AGM - Prim)",
                    prim.getCustoTotalAGM()));
            System.out.println("----------------------------------------------------------------------------------\n");

        } catch (IOException e) {
            System.err.println("[ERRO] Arquivo " + arquivoMalha + " ausente. Rode o GeradorDados.");
        }
    }

    // Utilitários de Mapeamento O(V)
    private static void registrarVerticeUnico(String nome) {
        for (int i = 0; i < quantidadeCentros; i++) {
            if (mapaCentros[i].equals(nome))
                return;
        }
        mapaCentros[quantidadeCentros++] = nome;
    }

    private static int obterIndiceVertice(String nome) {
        for (int i = 0; i < quantidadeCentros; i++) {
            if (mapaCentros[i].equals(nome))
                return i;
        }
        return -1;
    }
}