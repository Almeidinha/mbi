package com.msoft.mbi.cube.multi.resumeFunctions;

import com.msoft.mbi.cube.exception.CuboMathParserException;

import java.io.Serial;

public class FuncaoRankingUltimos extends FuncaoRanking {

    @Serial
    private static final long serialVersionUID = -6819639180540031258L;

    public FuncaoRankingUltimos(Integer posicoes) {
        super("IF([sequencia] > ([qtdRegistros]-[posicoes]), 1, 0)");
        try {
            this.calculo.setValorVariable("posicoes", Double.valueOf(posicoes));
        } catch (Exception e) {
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível criar o ranking do tipo últimos(n).", e);
            throw parserException;
        }
    }

    @Override
    public boolean testaCondicao(double valorAtual, int qtdRegistros) {
        boolean retorno;
        try {
            this.calculo.setValorVariable("qtdRegistros", (double) qtdRegistros);
            retorno = super.testaCondicao(valorAtual, (qtdRegistros - (int) valorAtual));
        } catch (Exception e) {
            CuboMathParserException parserException = new CuboMathParserException("Não foi possível aplicar o ranking do tipo últimos(n).", e);
            throw parserException;
        }
        return retorno;
    }
}
