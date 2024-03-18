package com.msoft.mbi.data.api.data.oldindicator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartialTotalization {


    private Field field;
    private Object[][] values;

    private double partialTotalization;
    private int sequence;


    public String toString() {
        return "Campo: " + this.field + "   Valor: " + this.partialTotalization + "  Sequência: " + this.sequence;
    }

}
