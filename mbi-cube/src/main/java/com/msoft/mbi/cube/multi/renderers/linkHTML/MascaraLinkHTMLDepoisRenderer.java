package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MascaraDepoisRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;

public class MascaraLinkHTMLDepoisRenderer extends MascaraDepoisRenderer {

    public MascaraLinkHTMLDepoisRenderer(MascaraRenderer celulaDecorator, LinkHTML linkHTML) {
        super(celulaDecorator, linkHTML.toString());
    }

}
