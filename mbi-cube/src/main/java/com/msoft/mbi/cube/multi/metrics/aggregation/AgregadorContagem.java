package com.msoft.mbi.cube.multi.metrics.aggregation;

import java.io.Serial;

public class AgregadorContagem extends AgregadorTipo {

    @Serial
    private static final long serialVersionUID = -8252970413515223432L;

    @Override
    public void agregaValor(Double novoValor) {
        if (this.valor == null) {
            this.valor = (double) 0;
        }
        this.valor++;
    }

}
