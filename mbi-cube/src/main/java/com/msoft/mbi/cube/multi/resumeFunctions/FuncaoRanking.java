package com.msoft.mbi.cube.multi.resumeFunctions;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringTokenizer;

import com.msoft.mbi.cube.exception.CuboMathParserException;
import com.msoft.mbi.cube.multi.calculation.Calculo;

public class FuncaoRanking implements Serializable {

    @Serial
    private static final long serialVersionUID = -3980171578731105435L;

    private String condicaoRanking;
    protected transient Calculo calculo;

    protected FuncaoRanking(String condicaoRanking) {
        this.condicaoRanking = condicaoRanking;
        this.calculo = new Calculo(this.condicaoRanking);
    }

    public FuncaoRanking(String operador, Integer posicoes) {
        this("IF([sequencia]" + operador + posicoes + ", 1, 0)");
    }

    public FuncaoRanking(String operador, String posicoes) {
        this(criaExpressao(operador, posicoes));
    }

    public static FuncaoRanking factory(String expressao) {
        int tamanhoStringCampoSequencia = 13;
        expressao = expressao.substring(tamanhoStringCampoSequencia).trim();
        int indexPosicoes = expressao.indexOf(" ");
        String operador = expressao.substring(0, indexPosicoes);
        String posicoes = expressao.substring(indexPosicoes).trim();
        if ("primeiros(n)".equals(operador)) {
            return new FuncaoRankingPrimeiros((int) Double.parseDouble(posicoes));
        } else if ("ultimos(n)".equals(operador)) {
            return new FuncaoRankingUltimos((int) Double.parseDouble(posicoes));
        } else {
            return new FuncaoRanking(operador, posicoes);
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

    public boolean testaCondicao(double valor, int qtdRegistros) {
        boolean retorno = false;
        try {
            this.calculo.setValorVariable("sequencia", valor);
            retorno = (valor == -1 || this.calculo.calculaValor() == 1);
        } catch (Exception e) {
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível aplicar o ranking.", e);
            throw parserException;
        }
        return retorno;
    }

}
