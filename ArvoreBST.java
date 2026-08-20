/**
 * Implementação de uma Árvore de Pesquisa Binária (BST) Desbalanceada.
 * A estrutura é indexada pela composição matemática (prazo_entrega * 1.000.000
 * + id_pedido)
 * para garantir O(log n) nas operações de prazos e evitar perda de nós capazes
 * de colidir.
 */
public class ArvoreBST {

    private NoArvore raiz;

    // Variáveis de estado para coleta das métricas obrigatórias da avaliação
    // empírica
    private int nosVisitadosUltimaBusca;
    private int quantidadeNos;

    public ArvoreBST() {
        this.raiz = null;
        this.nosVisitadosUltimaBusca = 0;
        this.quantidadeNos = 0;
    }

    // ========================================================================
    // OPERAÇÕES PRINCIPAIS (CRUD E ORDENAÇÃO)
    // ========================================================================

    /**
     * Insere um pedido na BST. Custo médio: O(log n). Pior caso: O(n).
     */
    public void inserir(Pedido p) {
        if (p == null)
            return;
        this.raiz = inserirRecursivo(this.raiz, p);
    }

    private NoArvore inserirRecursivo(NoArvore atual, Pedido p) {
        if (atual == null) {
            this.quantidadeNos++;
            return new NoArvore(p);
        }

        // Chave efetiva: (prazo * 1.000.000) + ID
        long chaveNova = ((long) p.getPrazoEntrega() * 1000000L) + p.getIdPedido();

        if (chaveNova < atual.getChaveOrdenacao()) {
            atual.setEsquerda(inserirRecursivo(atual.getEsquerda(), p));
        } else if (chaveNova > atual.getChaveOrdenacao()) {
            atual.setDireita(inserirRecursivo(atual.getDireita(), p));
        }
        // Se as chaves forem rigorosamente iguais, ignora (embora matematicamente
        // impossível no nosso domínio graças à composição com o ID único).

        return atual;
    }

    /**
     * Realiza a consulta de pedidos dentro de uma faixa de prazos.
     * Utiliza travessia in-order otimizada (Branch Pruning).
     * Como não podemos usar ArrayList, retornamos um array estático exato.
     * 
     * @param prazoMin Prazo de entrega mínimo (inclusivo)
     * @param prazoMax Prazo de entrega máximo (inclusivo)
     * @return Array contendo os pedidos daquela faixa, ordenados pelo prazo.
     */
    public Pedido[] consultarFaixa(int prazoMin, int prazoMax) {
        // Aloca array no pior caso (todos os nós), rastreia os válidos e trunca no
        // final
        Pedido[] buffer = new Pedido[this.quantidadeNos];
        int[] contador = { 0 }; // Array de 1 posição para simular passagem por referência em Java

        consultarFaixaRecursivo(this.raiz, prazoMin, prazoMax, buffer, contador);

        // Truncamento manual do array para Clean Code (sem usar Arrays.copyOf)
        Pedido[] resultadoExato = new Pedido[contador[0]];
        for (int i = 0; i < contador[0]; i++) {
            resultadoExato[i] = buffer[i];
        }
        return resultadoExato;
    }

    private void consultarFaixaRecursivo(NoArvore atual, int min, int max, Pedido[] buffer, int[] contador) {
        if (atual == null)
            return;

        int prazoAtual = atual.getPedido().getPrazoEntrega();

        // Branch Pruning: Só visita a esquerda se ela puder conter um prazo útil
        if (prazoAtual > min) {
            consultarFaixaRecursivo(atual.getEsquerda(), min, max, buffer, contador);
        }

        // Processa o nó atual (Travessia In-Order)
        if (prazoAtual >= min && prazoAtual <= max) {
            buffer[contador[0]] = atual.getPedido();
            contador[0]++;
        }

        // Branch Pruning: Só visita a direita se ela puder conter um prazo útil
        if (prazoAtual < max) {
            consultarFaixaRecursivo(atual.getDireita(), min, max, buffer, contador);
        }
    }

    /**
     * Busca um pedido pelo ID.
     * ATENÇÃO ARQUITETURAL: Como a árvore está indexada pelo prazo, a busca por ID
     * obriga o algoritmo a fazer um Full Tree Scan, degradando para O(n).
     */
    public Pedido buscarPorId(int idPedido) {
        this.nosVisitadosUltimaBusca = 0; // Reset para nova medição
        return buscarPorIdRecursivo(this.raiz, idPedido);
    }

    private Pedido buscarPorIdRecursivo(NoArvore atual, int idPedido) {
        if (atual == null)
            return null;

        this.nosVisitadosUltimaBusca++; // Incrementa a contagem a cada nó visitado

        if (atual.getPedido().getIdPedido() == idPedido) {
            return atual.getPedido();
        }

        // Busca linear forçada na subárvore esquerda
        Pedido encontradoEsquerda = buscarPorIdRecursivo(atual.getEsquerda(), idPedido);
        if (encontradoEsquerda != null)
            return encontradoEsquerda;

        // Busca linear forçada na subárvore direita
        return buscarPorIdRecursivo(atual.getDireita(), idPedido);
    }

    /**
     * Remove um pedido da árvore informando o ID.
     * Novamente, exige O(n) por depender de um Full Tree Scan prévio.
     */
    public boolean removerPorId(int idPedido) {
        Pedido alvo = buscarPorId(idPedido);
        if (alvo == null)
            return false; // Não existe

        this.raiz = removerRecursivo(this.raiz, alvo.getChaveArvore());
        this.quantidadeNos--;
        return true;
    }

    private NoArvore removerRecursivo(NoArvore atual, long chaveAlvo) {
        if (atual == null)
            return null;

        if (chaveAlvo < atual.getChaveOrdenacao()) {
            atual.setEsquerda(removerRecursivo(atual.getEsquerda(), chaveAlvo));
        } else if (chaveAlvo > atual.getChaveOrdenacao()) {
            atual.setDireita(removerRecursivo(atual.getDireita(), chaveAlvo));
        } else {
            // Nó encontrado. Caso 1 e 2: Zero ou Um filho
            if (atual.getEsquerda() == null)
                return atual.getDireita();
            if (atual.getDireita() == null)
                return atual.getEsquerda();

            // Caso 3: Dois filhos (Encontra o sucessor in-order)
            NoArvore sucessor = encontrarMinimo(atual.getDireita());

            // Reconstrução manual dos ponteiros (Substituição de payload é má prática)
            atual = new NoArvore(sucessor.getPedido());
            atual.setEsquerda(atual.getEsquerda()); // Mantém a esquerda original
            atual.setDireita(removerRecursivo(atual.getDireita(), sucessor.getChaveOrdenacao()));
        }
        return atual;
    }

    private NoArvore encontrarMinimo(NoArvore atual) {
        while (atual.getEsquerda() != null) {
            atual = atual.getEsquerda();
        }
        return atual;
    }

    // ========================================================================
    // MÉTRICAS OBRIGATÓRIAS (Análise Empírica e Relatório)
    // ========================================================================

    /**
     * Retorna o número de nós visitados na última operação de busca por ID.
     * Comprovará no seu relatório o custo O(n) da estrutura quando
     * submetida a uma busca por um atributo não indexado.
     */
    public int getNosVisitadosUltimaBusca() {
        return this.nosVisitadosUltimaBusca;
    }

    /**
     * Calcula e retorna a altura final da árvore.
     * Evidencia o grau de degeneração da BST desbalanceada.
     * Custo computacional: O(n).
     */
    public int getAlturaTotal() {
        return calcularAltura(this.raiz);
    }

    private int calcularAltura(NoArvore atual) {
        if (atual == null) {
            return -1; // Altura de folha é 0, de nó nulo é -1
        }
        int alturaEsq = calcularAltura(atual.getEsquerda());
        int alturaDir = calcularAltura(atual.getDireita());

        return Math.max(alturaEsq, alturaDir) + 1;
    }
}