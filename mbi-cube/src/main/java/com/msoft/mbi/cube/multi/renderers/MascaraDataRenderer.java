/*
 * @(#)DataDecorator.java 10/08/2009 - 14:32:35 Copyright (c) 2002-2003
 * Logocenter S/A. R XV de Novembro, 3950, Bairro Gl�ria, Joinville, SC, Brasil.
 * All rights reserved. This software is the confidential and proprietary
 * information of Logocenter S/A ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with Logocenter.
 */
package com.msoft.mbi.cube.multi.renderers;

import java.io.Serial;
import java.text.SimpleDateFormat;

public class MascaraDataRenderer implements MascaraRenderer {

    @Serial
    private static final long serialVersionUID = -5791637447947728543L;

    private MascaraRenderer renderer;
    private SimpleDateFormat formatador;

    public MascaraDataRenderer(MascaraRenderer decorator) {
        this(decorator, "dd/MM/yyyy");
    }

    public MascaraDataRenderer(MascaraRenderer decorator, String formato) {
        this.renderer = decorator;
        this.formatador = new SimpleDateFormat(formato);
    }

    @Override
    public Object aplica(Object valor) {
        return this.renderer.aplica(this.formatador.format(valor));
    }
}
