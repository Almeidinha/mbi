package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;

public class MascaraLinkHTMLColunaPadraoRenderer implements MascaraRenderer {

    @Override
    public Object aplica(Object valor) {
        return "<td width='100%'>" + valor + "</td>";
    }
}
