package com.msoft.mbi.cube.multi.metadata;

import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTML;
import lombok.Getter;
import lombok.Setter;

@Getter
public class HTMLLineMask {

    private final String id;
    private final int type;
    @Setter
    private LinkHTML linkHTML;
    public static final int VALUE_TYPE_AFTER = 1;
    public static final int VALUE_TYPE = 2;
    public static final int DYNAMIC_TYPE = 3;

    public HTMLLineMask(String id, int type, LinkHTML linkHTML) {
        this.id = id;
        this.linkHTML = linkHTML;
        this.type = type;
    }

}