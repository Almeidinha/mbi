package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class LinkHTMLColumnImage extends LinkHTML {

    private final String path;
    private final String image;
    private Integer width;
    private Integer height;
    private String hint = "";
    private final String cssClass;

    public LinkHTMLColumnImage(String path, String image, String cssClass) {
        super();
        this.path = path;
        this.image = image;
        this.cssClass = cssClass;
    }

    private LinkHTMLColumnImage(String path, String id, String cssClass, Integer height, Integer width) {
        this(path, id, cssClass);
        this.height = height;
        this.width = width;
    }

    public LinkHTMLColumnImage(String path, String id, String cssClass, String hint, Integer height, Integer width) {
        this(path, id, cssClass, height, width);
        this.hint = hint;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String textHeight = this.height != null ? " height:" + this.height + "px;" : "";
        String textWidth = this.width != null ? " width:" + this.width + "px;" : "";
        builder.append("<img id='").append(this.image).append("' style='padding: 3px; ").append(textHeight).append(textWidth).append("' src='").append(this.path).append("'")
                .append(" title='").append(this.hint).append("' class='").append(this.cssClass).append("'");

        for (String param : this.getParameters().keySet()) {
            builder.append(" ").append(param).append("='").append(this.getParameters().get(param)).append("'");
        }

        builder.append(">");
        return builder.toString();
    }
}
