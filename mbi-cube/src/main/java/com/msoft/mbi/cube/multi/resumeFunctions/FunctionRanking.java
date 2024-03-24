package com.msoft.mbi.cube.multi.resumeFunctions;

import java.io.Serializable;
import java.util.StringTokenizer;

import com.msoft.mbi.cube.exception.CubeMathParserException;
import com.msoft.mbi.cube.multi.calculation.Calculation;

public class FunctionRanking implements Serializable {

    private String rankingCondition;
    protected transient Calculation calculation;

    protected FunctionRanking(String rankingCondition) {
        this.rankingCondition = rankingCondition;
        this.calculation = new Calculation(this.rankingCondition);
    }

    public FunctionRanking(String operador, Integer posicoes) {
        this("IF([sequencia]" + operador + posicoes + ", 1, 0)");
    }

    public FunctionRanking(String operador, String posicoes) {
        this(criaExpressao(operador, posicoes));
    }

    public static FunctionRanking factory(String expressao) {
        int tamanhoStringCampoSequencia = 13;
        expressao = expressao.substring(tamanhoStringCampoSequencia).trim();
        int indexPosicoes = expressao.indexOf(" ");
        String operador = expressao.substring(0, indexPosicoes);
        String posicoes = expressao.substring(indexPosicoes).trim();
        if ("primeiros(n)".equals(operador)) {
            return new FunctionRankingFirst((int) Double.parseDouble(posicoes));
        } else if ("ultimos(n)".equals(operador)) {
            return new FunctionRankingLast((int) Double.parseDouble(posicoes));
        } else {
            return new FunctionRanking(operador, posicoes);
        }
    }

    private static String criaExpressao(String operador, String posicoes) {
        StringTokenizer tokenizer = new StringTokenizer(posicoes, ";");
        StringBuilder expressao = new StringBuilder("IF((");
        while (tokenizer.hasMoreTokens()) {
            String posicao = tokenizer.nextToken();
            expressao.append("[sequencia]").append(operador).append(posicao).append("|");
        }
        expressao.deleteCharAt(expressao.length() - 1);
        expressao.append("), 1, 0)");
        return expressao.toString();
    }

    public boolean testCondicao(double valor, int qtdRegistros) {
        boolean retorno;
        try {
            this.calculation.setVariableValue("sequencia", valor);
            retorno = (valor == -1 || this.calculation.calculateValue() == 1);
        } catch (Exception e) {
            throw new CubeMathParserException("Não foi possível aplicar o ranking.", e);
        }
        return retorno;
    }

}
