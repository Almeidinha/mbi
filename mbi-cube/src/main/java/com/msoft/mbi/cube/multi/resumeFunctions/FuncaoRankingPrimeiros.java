package com.msoft.mbi.cube.multi.resumeFunctions;

import java.io.Serial;

public class FuncaoRankingPrimeiros extends FuncaoRanking {

    @Serial
    private static final long serialVersionUID = -1179697465456778565L;

    public FuncaoRankingPrimeiros(Integer posicoes) {
        super("<=", posicoes);
    }

    public static final String FWJ_VERSAO = "$Revision: 1.1 $";

}
