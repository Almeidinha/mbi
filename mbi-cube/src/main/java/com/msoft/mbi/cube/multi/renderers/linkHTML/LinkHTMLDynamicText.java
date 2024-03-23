package com.msoft.mbi.cube.multi.renderers.linkHTML;

import lombok.Getter;

@Getter
public class LinkHTMLDynamicText extends LinkHTMLText {

    private final String dynamicParameterName;

    public LinkHTMLDynamicText(String dynamicParameterName) {
        super(null);
        this.addParameter(dynamicParameterName);
        this.dynamicParameterName = dynamicParameterName;
    }

}
