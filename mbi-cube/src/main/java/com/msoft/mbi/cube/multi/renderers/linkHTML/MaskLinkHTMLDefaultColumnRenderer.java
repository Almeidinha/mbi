package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;

public class MaskLinkHTMLDefaultColumnRenderer implements MaskRenderer {

    @Override
    public Object apply(Object valor) {
        return "<td width='100%'>" + valor + "</td>";
    }
}
