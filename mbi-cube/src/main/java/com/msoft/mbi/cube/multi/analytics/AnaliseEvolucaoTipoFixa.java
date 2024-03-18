/*
 * @(#)AnaliseEvolucaoTipoFixa.java 29/09/2009 - 16:48:15 Copyright (c)
 * 2002-2003 Logocenter S/A. R XV de Novembro, 3950, Bairro Gl�ria, Joinville,
 * SC, Brasil. All rights reserved. This software is the confidential and
 * proprietary information of Logocenter S/A ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with
 * Logocenter.
 */
package com.msoft.mbi.cube.multi.analytics;

import com.msoft.mbi.cube.multi.dimension.Dimension;

import java.io.Serial;

public class AnaliseEvolucaoTipoFixa implements AnaliseEvolucaoTipo {

    private static AnaliseEvolucaoTipoFixa analiseEvolucaoTipoFixa;

    private AnaliseEvolucaoTipoFixa() {
        super();
    }

    public static AnaliseEvolucaoTipoFixa getInstance() {
        if (analiseEvolucaoTipoFixa == null) {
            analiseEvolucaoTipoFixa = new AnaliseEvolucaoTipoFixa();
        }
        return analiseEvolucaoTipoFixa;
    }

    @Override
    public Dimension getDimensaoAnterior(Dimension dimensionAtual) {
        return dimensionAtual.getSameLevelFirstDimensionColumn();
    }

}
