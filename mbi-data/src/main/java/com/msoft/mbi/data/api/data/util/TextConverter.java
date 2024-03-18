package com.msoft.mbi.data.api.data.util;

public class TextConverter {

    protected String text;
    protected String[] HTML = {"&quot;", "&amp;", "&lt;", "&gt;", "&nbsp;", "<br>"};
    protected String[] UNICODE = {"\"", "&", "<", ">", " ", "\n"};

    public TextConverter(String text) {
        this.text = text;
    }

    public String getTextToHTML() {
        StringBuilder newText = new StringBuilder();
        newText.append(this.text);
        for (int i = 0; i < this.HTML.length; i++) {
            this.replace(newText, this.UNICODE[i], this.HTML[i]);
        }
        return newText.toString();
    }

    public String getHTMLToText() {
        StringBuilder newText = new StringBuilder();
        newText.append(this.text);
        for (int i = 0; i < this.HTML.length; i++) {
            this.replace(newText, this.HTML[i], this.UNICODE[i]);
        }
        return newText.toString();
    }

    protected void replace(StringBuilder src, String ant, String nov) {
        int i = -1;
        while ((i = src.indexOf(ant, i + 1)) != -1) {
            src.replace(i, i + ant.length(), nov);
        }
    }

    public static void main(String[] args) {
        String text = "1\n2\n3\n4";
        System.out.println(text);
        System.out.println("-");
        TextConverter converter = new TextConverter(text);
        System.out.println(converter.getTextToHTML());
    }

}
