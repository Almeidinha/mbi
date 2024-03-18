package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MascaraDepoisRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;

import java.io.Serial;

public class MascaraLinkHTMLDepoisRenderer extends MascaraDepoisRenderer {
    @Serial
    private static final long serialVersionUID = -3125354511997552061L;

    public MascaraLinkHTMLDepoisRenderer(MascaraRenderer celulaDecorator, LinkHTML linkHTML) {
        super(celulaDecorator, linkHTML.toString());
    }

}
