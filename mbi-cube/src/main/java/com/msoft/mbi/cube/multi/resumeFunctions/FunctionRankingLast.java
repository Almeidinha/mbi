package com.msoft.mbi.cube.multi.resumeFunctions;

import com.msoft.mbi.cube.exception.CubeMathParserException;

import java.io.Serial;

public class FunctionRankingLast extends FunctionRanking {

    @Serial
    private static final long serialVersionUID = -6819639180540031258L;

    public FunctionRankingLast(Integer posicoes) {
        super("IF([sequencia] > ([qtdRegistros]-[posicoes]), 1, 0)");
        try {
            this.calculo.setValorVariable("posicoes", Double.valueOf(posicoes));
        } catch (Exception e) {
            throw new CubeMathParserException("Não foi possível criar o ranking do tipo últimos(n).", e);
        }
    }

    @Override
    public boolean testCondicao(double currentValue, int amount) {
        boolean result;
        try {
            this.calculo.setValorVariable("qtdRegistros", (double) amount);
            result = super.testCondicao(currentValue, (amount - (int) currentValue));
        } catch (Exception e) {
            throw new CubeMathParserException("Não foi possível aplicar o ranking do tipo últimos(n).", e);
        }
        return result;
    }
}
