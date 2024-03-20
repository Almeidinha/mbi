package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class LinkHTMLSVGColumn extends LinkHTML {

    private String id;
    private Integer width;
    private Integer height;
    private String hint = "";
    private String cssClass;

    public LinkHTMLSVGColumn() {
        super();
    }

    public LinkHTMLSVGColumn(String id, String cssClass) {
        super();
        this.id = id;
        this.cssClass = cssClass;
    }

    public LinkHTMLSVGColumn(String id, String cssClass, String hint, Integer height, Integer width) {
        this(id, cssClass);
        this.height = height;
        this.width = width;
        this.hint = hint;
    }

    public LinkHTMLSVGColumn(String cssClass, String hint, Integer height, Integer width) {
        this.cssClass = cssClass;
        this.hint = hint;
        this.height = height;
        this.width = width;
    }

    @Override
    public String toString() {
        StringBuilder image = new StringBuilder();
        String height = this.height != null ? " height:" + this.height + "px;" : "";
        String width = this.width != null ? " width:" + this.width + "px;" : "";
        String id = this.id != null ? " id='" + this.id + "'" : " ";
        image.append("<div")
				.append(id).append(" class='").append(this.cssClass)
				.append("' style='cursor: pointer;").append(height).append(width).append("'")
				.append(" title='").append(this.hint).append("'");

        for (String param : this.getParametros().keySet()) {
            image.append(" ").append(param).append("='").append(this.getParametros().get(param)).append("'");
        }

        image.append("></div>");
        return image.toString();
    }
}
