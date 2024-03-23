package com.msoft.mbi.cube.multi.renderers.linkHTML;

import com.msoft.mbi.cube.multi.renderers.MaskRenderer;

public class MaskAfterRenderer extends com.msoft.mbi.cube.multi.renderers.MaskAfterRenderer {

    public MaskAfterRenderer(MaskRenderer celulaDecorator, LinkHTML linkHTML) {
        super(celulaDecorator, linkHTML.toString());
    }

}
