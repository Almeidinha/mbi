package com.msoft.mbi.cube.multi.resumefunctions;

import com.msoft.mbi.cube.exception.CubeMathParserException;

public class FunctionRankingLast extends FunctionRanking {

    public FunctionRankingLast(Integer posicoes) {
        super("IF([sequencia] > ([qtdRegistros]-[posicoes]), 1, 0)");
        try {
            this.calculation.setVariableValue("posicoes", Double.valueOf(posicoes));
        } catch (Exception e) {
            throw new CubeMathParserException("Não foi possível criar o ranking do tipo últimos(n).", e);
        }
    }

    @Override
    public boolean testCondicao(double currentValue, int amount) {
        boolean result;
        try {
            this.calculation.setVariableValue("qtdRegistros", (double) amount);
            result = super.testCondicao(currentValue, (amount - (int) currentValue));
        } catch (Exception e) {
            throw new CubeMathParserException("Não foi possível aplicar o ranking do tipo últimos(n).", e);
        }
        return result;
    }
}
