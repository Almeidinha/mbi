package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;

public class MascLinkHTMLTextRenderer implements MaskRenderer {

    protected LinkHTMLText link;

    public MascLinkHTMLTextRenderer(LinkHTMLText link) {
        this.link = link;
    }

    @Override
    public Object apply(Object valor) {
        this.link.setContent(valor.toString());
        return this.link.toString();
    }
}
