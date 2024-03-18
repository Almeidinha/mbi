package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;

import java.io.Serial;

public class MascaraLinkHTMLColunaPadraoRenderer implements MascaraRenderer {
    @Serial
    private static final long serialVersionUID = 1188149666621880231L;

    @Override
    public Object aplica(Object valor) {
        return "<td width='100%'>" + valor + "</td>";
    }
}
