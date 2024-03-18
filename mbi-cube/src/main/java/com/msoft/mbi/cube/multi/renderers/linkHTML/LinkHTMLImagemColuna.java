package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class LinkHTMLImagemColuna extends LinkHTML {

    public static final String FWJ_VERSAO = "$Revision: 1.2 $";

    private String caminhoImagem;
    private String idImagem;
    private Integer largura;
    private Integer altura;
    private String hint = "";
    private String classe;

    public LinkHTMLImagemColuna(String caminhoImagem, String idImagem, String classe) {
        super();
        this.caminhoImagem = caminhoImagem;
        this.idImagem = idImagem;
        this.classe = classe;
    }

    private LinkHTMLImagemColuna(String caminhoImagem, String id, String classe, Integer altura, Integer largura) {
        this(caminhoImagem, id, classe);
        this.altura = altura;
        this.largura = largura;
    }

    public LinkHTMLImagemColuna(String caminhoImagem, String id, String classe, String hint, Integer altura, Integer largura) {
        this(caminhoImagem, id, classe, altura, largura);
        this.hint = hint;
    }

    @Override
    public String toString() {
        StringBuilder sbImagem = new StringBuilder();
        String textoAltura = this.altura != null ? " height:" + this.altura.intValue() + "px;" : "";
        String textoLargura = this.largura != null ? " width:" + this.largura.intValue() + "px;" : "";
        sbImagem.append("<img id='").append(this.idImagem).append("' style='padding: 3px; ").append(textoAltura).append(textoLargura).append("' src='").append(this.caminhoImagem).append("'")
                .append(" title='").append(this.hint).append("' class='").append(this.classe).append("'");

        for (String param : this.getParametros().keySet()) {
            sbImagem.append(" ").append(param).append("='").append(this.getParametros().get(param)).append("'");
        }

        sbImagem.append(">");
        return sbImagem.toString();
    }
}
