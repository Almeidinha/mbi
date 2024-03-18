package com.msoft.mbi.cube.multi.metaData;

import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTML;
import lombok.Getter;
import lombok.Setter;

@Getter
public class MascaraLinkHTMLMetaData {

    private final String idMascara;
    private final int tipo;
    @Setter
    private LinkHTML linkHTML;
    public static final int TIPO_DEPOIS_VALOR = 1;
    public static final int TIPO_VALOR = 2;
    public static final int TIPO_DINAMICO = 3;

    public MascaraLinkHTMLMetaData(String idMascara, int tipo, LinkHTML linkHTML) {
        this.idMascara = idMascara;
        this.linkHTML = linkHTML;
        this.tipo = tipo;
    }

}