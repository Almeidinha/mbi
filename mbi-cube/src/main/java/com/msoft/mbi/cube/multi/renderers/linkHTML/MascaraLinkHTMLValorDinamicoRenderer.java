package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class MascaraLinkHTMLValorDinamicoRenderer extends MascaraLinkHTMLTextoRenderer {
    private static final long serialVersionUID = 3712478038224216253L;

    public MascaraLinkHTMLValorDinamicoRenderer(LinkHTMLTextoDinamico linkaplicar) {
        super(linkaplicar);
    }

    public Object aplica(Object valor, String parametro1Inserir) {
        this.linkAplicar.getParametros().put(((LinkHTMLTextoDinamico) this.linkAplicar).getNomeParametroDinamico(), parametro1Inserir);
        this.linkAplicar.setConteudo(valor.toString());
        return super.aplica(valor);
    }

}
