/**
 * Classe utilitária responsável por converter os identificadores textuais
 * dos Centros de Distribuição em índices numéricos para uso nos arrays e
 * matrizes.
 */
public class MapeadorCentros {

    // Array estático mantendo a ordem exata dos 10 vértices do sistema
    private static final String[] CENTROS = {
            "CD_CTBA", "CD_LOND", "CD_MRGA", "CD_CSVL", "CD_PTGR",
            "CD_FZIG", "CD_GPVA", "CD_SJPI", "CD_UMUA", "CD_PATO"
    };

    /**
     * Converte o nome do Centro de Distribuição em um índice numérico (0 a 9).
     * Utiliza Busca Linear (O(V)). Como V=10, a performance é virtualmente O(1).
     * 
     * @param nomeCentro Identificador textual (ex: "CD_LOND")
     * @return Índice inteiro correspondente (ex: 1)
     */
    public static int paraIndice(String nomeCentro) {
        for (int i = 0; i < CENTROS.length; i++) {
            if (CENTROS[i].equals(nomeCentro)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Erro Crítico: Centro de Distribuição desconhecido na malha: " + nomeCentro);
    }

    /**
     * Converte o índice numérico de volta para o identificador textual.
     * Operação estritamente O(1).
     * 
     * @param indice Índice numérico no grafo (0 a 9)
     * @return Identificador textual (ex: "CD_LOND")
     */
    public static String paraNome(int indice) {
        if (indice < 0 || indice >= CENTROS.length) {
            throw new IllegalArgumentException("Índice de vértice fora dos limites permitidos: " + indice);
        }
        return CENTROS[indice];
    }

    /**
     * Retorna a quantidade total de vértices no grafo.
     */
    public static int getQuantidadeVértices() {
        return CENTROS.length;
    }
}