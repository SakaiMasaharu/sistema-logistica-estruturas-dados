# Trabalho Final: Sistema de Logística e Entregas

**Autor:** Charles Masaharu Sakai  
**Instituição:** Universidade Estadual de Londrina (UEL)  
**Programa:** Mestrado em Ciência da Computação  
**Disciplina:** 2COP212 - Algoritmos e Estrutura de Dados  
**Semente Determinística:** `859L` (Matrícula: 202612560003)

---

## 📹 1. Demonstração (Vídeo)

O vídeo com a demonstração do executável rodando (fluxo integrado dos três módulos) e explicação das decisões de projeto e dos resultados obtidos está disponível no YouTube:

**Link para o Vídeo:** [https://www.youtube.com/watch?v=????????????]

---

## ⚙️ 2. Instruções de Compilação e Execução

O sistema foi desenvolvido em Java. Para reproduzir o ambiente, gerar os dados determinísticos e extrair as métricas, siga o fluxo de execução abaixo em um terminal:

### Passo A: Compilação
Faça o download dos arquivos deste repositório do GitHub.
Em seguida, utilizando um terminal, navegue até o diretório raiz do projeto e compile todos os arquivos fonte utilizando o comando:

*javac *.java*

### Passo B: Geração da Infraestrutura e Carga (Determinística)
Execute o gerador para criar os arquivos malha.csv e pedidos_*.csv. A topologia da malha e as colisões são baseadas exclusivamente na semente 859L. Execute o comando:

*java GeradorDados*

### Passo C: O Fluxo Integrado (A Aplicação em Produção)
Para simular o ecossistema logístico em tempo real (Inserção -> Rota de Entrega -> Baixa do Pedido), utlize:

*java SistemaLogistica*

### Passo D: Auditoria e Extração de Métricas (Benchmarking)
Para submeter as estruturas de dados a testes de estresse computacional e reproduzir exatamente as contagens tabuladas na Seção 4 deste relatório:

*java AnalisadorEmpirico*

---

## 🔍 3. Módulo de Aprofundamento e Justificativa Arquitetural

**Módulo Escolhido:** Hash.

**Justificativa Técnica:**
A escolha do módulo Hash para aprofundamento fundamenta-se na sua criticidade para a resiliência e escalabilidade de sistemas corporativos. Em ambientes de administração de sistemas e infraestruturas de alta concorrência, a latência na recuperação de registros é o gargalo mais comum e a tabela hash garante complexidade de tempo assintótica O(1) no caso médio para operações de busca, inserção e remoção.

Para comprovar o comportamento da estrutura sob níveis variados de estresse e densidade, este trabalho implementou e comparou as seguintes abordagens:
*   **Tratamento de Colisões:** Encadeamento (Listas Dinâmicas) vs. Endereçamento Aberto (Sondagem Linear com manipulação de *Tombstones*).
*   **Funções de Espalhamento (Hash):** Método da Divisão h(k) = k (mod M) vs. Método da Multiplicação (empregando a constante da Razão Áurea 0.6180339887 para garantir a distribuição mais uniforme possível das chaves, evitando repetições e minimizando colisões na tabela).

Além de submeter o sistema a distribuições uniformes de carga (1.000 a 50.000 registros), o trabalho inclui um **Cenário Desafio** no qual foram gerados 50.000 identificadores (id_pedido) que colidem sistematicamente sob o método da divisão (ex.: múltiplos de 17), para forçar o pior caso da função hash.

---

## 📊 4. Métricas de Complexidade

Conforme exigido pela metodologia de avaliação empírica, o tempo de execução (sujeito ao estado da máquina) foi descartado em favor de contagens determinísticas de operações para fundamentar a análise do Big-O.

### 4.1. Módulo Aprofundado: Tabela Hash

A infraestrutura foi alocada com um array estático de tamanho M = 51017. Este valor é um número primo (vital para otimizar o Método da Divisão) e foi propositalmente escolhido para ser ligeiramente superior à carga máxima esperada (50.000) e também múltiplo de 17, viabilizando o teste de estresse por colisão sistemática exigido pelo projeto.

##### A. Nível de Ocupação da Tabela (Fator de Carga α = n/M)
O Fator de Carga evidencia o grau de saturação da memória. A tabela abaixo demonstra o comportamento estrutural sob as três densidades normais de dados:

| Cenário Analisado | Total de Pedidos (n) | Tamanho do Array (M) | Fator de Carga Final |  
| `pedidos_1k.csv` | 1.000 | 51.017 | α = 1000 / 51017 ≈ 0.0196 |  
| `pedidos_10k.csv` | 10.000 | 51.017 | α = 10000 / 51017 ≈ 0.1960 |  
| `pedidos_50k.csv` | 50.000 | 51.017 | α = 50000 / 51017 ≈ 0.9800 |

##### B. Pior Caso Observado na Prática

Para evidenciar a resiliência das estratégias de colisão, o sistema foi submetido ao **Cenário Desafio** (`pedidos_desafio.csv`), que injeta 50.000 identificadores (múltiplos de 17) projetados intencionalmente para colidir sistematicamente no Método da Divisão. 

A tabela abaixo reporta o tamanho da maior cadeia (para Encadeamento) e a maior sequência de sondagem (para Endereçamento Aberto), revelando o quão longe a estrutura degenerou do ideal O(1):

| Estrutura Analisada | Função Hash | Fator de Carga (α) | Custo Medido | Diagnóstico Assintótico |  
| **Encadeamento** | Divisão | 0.9800 | 17 nós | O(1) - O custo médio de busca permanece constante. |  
| **Encadeamento** | Multiplicação | 0.9800 | ~ 6 nós | O(1) - A multiplicação espalhou os dados perfeitamente. |  
| **End. Aberto** | Divisão | 0.9800 | ~ 50.000 saltos | O(n) - Colapso causado pela concentração de chaves. |  
| **End. Aberto** | Multiplicação | 0.9800 | ~ 2.500 saltos | O(n) - Degradação severa devido à saturação de memória. |

### 4.2. Módulo Árvores (Ordenação por Prazo)

Para a manutenção dos pedidos ordenados cronologicamente, foi adotada a implementação de uma **Árvore de Pesquisa Binária (BST) sem balanceamento**. Os registros foram indexados utilizando a regra de desempate exigida: `prazo_entrega * 1000000 + id_pedido`. 

Análise de Complexidade: A busca por um ID específico exigiu uma varredura completa (Full Tree Scan). Os dados refletem o custo linear O(n) inerente à ausência de balanceamento e à busca sobre um atributo não indexado.

### 4.3. Módulo Grafos (Roteamento e Infraestrutura)

A infraestrutura física (Centros de Distribuição e rotas) foi modelada como um Grafo Não Direcionado. 
Para a representação em memória, adotou-se a Matriz de Adjacência. 
O Módulo de Grafos opera exclusivamente sobre a infraestrutura (`malha.csv`). 
Com a geração determinística limitando a malha entre 10 e 25 centros, o custo de espaço |V|² consome no máximo 625 posições em um array.
Assim, as métricas topológicas (espaço de representação, custo da Árvore Geradora Mínima via Prim e o custo de rota do Dijkstra) mantêm-se constantes, independentemente da carga de pedidos injetada no sistema.

---

## 🧠 5. Análise Crítica de Trade-offs

Segue análise crítica dos trade-offs observados empiricamente e a comparação teórica das estruturas alternativas exigidas pelo projeto.

### 5.1. Módulo Aprofundado (Hash)

A análise empírica do Módulo Hash revelou o comportamento assintótico prático das estratégias de resolução de colisão quando submetidas a cargas críticas (α ≈ 0.98).

*   **Encadeamento vs. Endereçamento Aberto:** O Encadeamento exige o trade-off de alocar memória extra (ponteiros de nós) fora do vetor principal. No entanto, provou-se extremamente resiliente no cenário de desafio. A maior cadeia conteve apenas 17 elementos, mantendo a degradação de busca muito próxima a O(1). Em contrapartida, o Endereçamento Aberto (Sondagem Linear) economiza memória de ponteiros, mas sofreu um colapso (agrupamento primário) no método da Divisão, resultando em uma varredura de quase 50.000 saltos (comportamento linear O(n)) para resolver uma única colisão.
*   **Divisão vs. Multiplicação:** A função da Divisão é computacionalmente mais barata (uma operação de módulo), mas é altamente suscetível a padrões nos dados de entrada. A função da Multiplicação (usando a Razão Áurea) tem um custo de CPU ligeiramente maior, mas evitou o colapso estrutural espalhando os múltiplos de 17 uniformemente pela tabela.

### 5.2. Módulo Árvores

O sistema implementou a Árvore de Pesquisa Binária (BST) sem balanceamento. A comparação teórica contra a alternativa Rubro-Negra representa o trade-off entre simplicidade de inserção e garantia de performance na busca.

*   **BST sem balanceamento (Implementada):** A vantagem reside na ausência de custo computacional auxiliar durante a inserção, pois não há rotações. O trade-off é a ausência de garantias de limite superior. Se os prazos de entrega chegarem em ordem estritamente crescente ou decrescente, a árvore degenera em uma lista ligada, resultando em complexidade O(n) para buscas, inserções e deleções.
*   **Árvore Rubro-Negra (Teórica):** Paga-se um custo computacional contínuo (pequeno, mas constante) para realizar rotações de nós durante cada operação de troca. O benefício arquitetural é garantir matematicamente que a altura da árvore nunca excederá o custo de busca de O(log n).

### 5.3. Módulo Grafos

A representação do grafo logístico adotou a Matriz de Adjacência. O trade-off em relação à Lista de Adjacência envolve o gerenciamento de espaço de memória versus tempo de acesso.

*   **Matriz de Adjacência (Implementada):** Ocupa um espaço fixo e quadrático |V|², independentemente de o grafo ser denso ou esparso. O benefício é o tempo de consulta constante O(1) para verificar se existe uma estrada entre dois centros. Dado que a nossa rede é pequena (limite máximo de 25 instalações geradas deterministicamente), existe o desperdício de memória, porém é irrelevante para a RAM moderna.
*   **Lista de Adjacência (Teórica):** É a estrutura ideal para grafos esparsos, pois consume apenas o espaço estritamente necessário para os vértices e arestas reais |V| + 2|A|. O trade-off é que para descobrir se há uma rota direta entre dois centros é necessário percorrer a lista de vizinhos, aumentando a complexidade de tempo e de gerenciamento de ponteiros.

### 5.4. Recomendação Arquitetural para Produção

Com base nas métricas extraídas e na teoria fundamentada, se este sistema fosse promovido para um ambiente de produção empresarial real, a arquitetura recomendada seria:

1.  **Hash:** Manteria o Encadeamento associado ao Método da Multiplicação. O custo de memória extra dos ponteiros é irrisório perante a segurança operacional contra colapsos causados por agrupamento primário em momentos de pico de acessos.
2.  **Árvore:** Substituiria a BST simples pela Árvore Rubro-Negra. Em sistemas de produção, é perigoso assumir que os prazos de entrega sempre chegarão de forma bem distribuída. Se entrarem de forma sequencial (crescente ou decrescente), a árvore simples degenera, prejudicando o desempenho. A Árvore Rubro-Negra resolve isso reequilibrando seus ramos automaticamente, o que garante matematicamente que as buscas ocorram sempre de maneira ágil, protegendo a escalabilidade da aplicação.
3.  **Grafos:** Manteria a Matriz de Adjacência, desde que a malha de centros de distribuição da empresa ficasse abaixo da ordem de alguns milhares de vértices. A simplicidade de implementação previne bugs e reduz o custo de manutenção do código, sendo perfeitamente viável para a escala física restrita do domínio do problema.

---

## ⚠️ 6. Limitações Conhecidas da Implementação

Embora o sistema seja funcional e atenda aos requisitos de complexidade exigidos pela avaliação, a restrição arquitetural de não utilizar bibliotecas nativas impôs algumas limitações estruturais inerentes à implementação adotada:

1.  **Ausência de Redimensionamento Dinâmico (Rehashing):** 
    A Tabela Hash foi construída sobre um array estático rigidamente dimensionado (M = 51017). O sistema atual não implementa uma rotina de resize. Consequentemente, se o volume de pedidos ultrapassar a capacidade máxima projetada, a estratégia de Endereçamento Aberto entrará em loop infinito (gerando falha por falta de slots livres), e a de Encadeamento degradará severamente para O(n) contínuo.
2.  **Degradação Linear da Árvore BST:** 
    O Módulo Árvore implementou a BST sem balanceamento automático. Portanto, a estrutura é vulnerável à ordem de chegada dos dados. A inserção sequencial de prazos de entrega transformará a árvore em uma lista encadeada, degradando as operações de O(log n) para O(n).
3.  **Resolução de Nomes de Vértices:** 
    Para converter as Strings (nomes dos centros de distribuição) em índices inteiros da Matriz de Adjacência sem o uso de dicionários nativos (como `HashMap`), foi implementada uma busca linear sobre um array estático. Para o cenário desta malha restrita (máximo de 25 centros), o custo de tempo é imperceptível. Contudo, em uma topologia em escala nacional (milhares de centros logísticos), essa resolução linear se tornaria um gargalo.

---

## 🤖 7. Uso de Inteligência Artificial

Declaro o uso de Inteligência Artificial Generativa (Google Gemini) durante o desenvolvimento deste trabalho, utilizada da seguinte forma:

*   **Como ferramenta de apoio:** 
    *   Apoiou na estruturação dos algoritmos das classes base (Tabela Hash, Árvore BST e Matriz de Adjacência).
    *   Esclareceu dúvidas, tanto de programação quanto de fundamentação teórica das estruturas de dados.
*   **Como ferramenta de geração:** 
    *   Forneceu o código-fonte para a geração dos arquivos de dados (.csv).
    *   Estruturou a classe que implementa um fluxo de execução único para simular as operações reais.
    *   Gerou o template com a formatação dos tópicos deste arquivo Markdown para facilitar o preenchimento.
    *   Gerou a imagem de capa (thumbnail) utilizada no vídeo.
