package com.msoft.mbi.cube.multi.renderers.linkHTML;

import lombok.Setter;

@Setter
public class LinkHTMLText extends LinkHTML {

    private String content;

    public LinkHTMLText(String content) {
        super();
        this.content = content;
    }

    @Override
    public String toString() {
        StringBuilder sLink = new StringBuilder();
        sLink.append("<span");

        for (String param : this.getParameters().keySet()) {
            sLink.append(" ").append(param).append("='").append(this.getParameters().get(param)).append("'");
        }
        sLink.append(">").append(this.content).append("</span>");
        return sLink.toString();
    }

}
