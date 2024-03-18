package com.msoft.mbi.cube.multi.column;

import java.util.List;

import com.msoft.mbi.cube.multi.resumeFunctions.FuncaoRanking;
import com.msoft.mbi.cube.multi.generation.Impressor;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metaData.MascaraLinkHTMLMetaData;
import com.msoft.mbi.cube.multi.renderers.MascaraAntesRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraDataRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraDepoisRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraMes;
import com.msoft.mbi.cube.multi.renderers.MascaraMesAno;
import com.msoft.mbi.cube.multi.renderers.MascaraPadraoRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraPeriodo;
import com.msoft.mbi.cube.multi.renderers.MascaraRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraSemana;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTexto;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoDinamico;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLDepoisRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLTextoRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLValorDinamicoPadraoRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascaraLinkHTMLValorDinamicoRenderer;
import lombok.Getter;
import lombok.Setter;

public abstract class ColunaMetaData {

    @Getter
    @Setter
    private String titulo;
    private MascaraRenderer mascaraRenderer;
    private MascaraRenderer efeitosHTMLRenderer;
    private MascaraLinkHTMLValorDinamicoRenderer efeitosHTMLDinamicoRenderer;
    @Getter
    @Setter
    private CellProperty cellProperty;
    private boolean campoSequencia = false;
    @Getter
    @Setter
    private FuncaoRanking funcaoRanking;
    @Getter
    @Setter
    private boolean expressaoParcialTotal = false;

    public ColunaMetaData(String titulo, String tipoCampo) {
        this.titulo = titulo;
        this.mascaraRenderer = new MascaraPadraoRenderer();
        this.efeitosHTMLRenderer = new MascaraPadraoRenderer();
        this.efeitosHTMLDinamicoRenderer = new MascaraLinkHTMLValorDinamicoPadraoRenderer();
        this.cellProperty = new CellProperty();
    }

    public void setDecorator(MascaraRenderer decorator) {
        this.mascaraRenderer = decorator;
    }

    public void setEfeitosHTMLDecorator(MascaraRenderer decorator) {
        this.efeitosHTMLRenderer = decorator;
    }

    public void addDecorator(MascaraColunaMetaData mascara) {
        this.mascaraRenderer = montaDecorator(mascara, this.mascaraRenderer);
    }

    public void addEfeitoHTMLDecorator(MascaraColunaMetaData mascara) {
        this.efeitosHTMLRenderer = montaDecorator(mascara, this.efeitosHTMLRenderer);
    }

    public void setCellProperty(int larguraColuna, String alinhamento) {
        this.cellProperty.setWidth(larguraColuna);
        this.cellProperty.addExtraAttributes("min-width", larguraColuna + "px");
        this.cellProperty.setAlignment(alinhamento);
    }

    public boolean hasCampoSequencia() {
        return campoSequencia;
    }

    public void setHasCampoSequencia(boolean campoSequencia) {
        this.campoSequencia = campoSequencia;
    }

    public MascaraRenderer getEfeitoHTMLDecorator() {
        return efeitosHTMLRenderer;
    }

    public MascaraLinkHTMLValorDinamicoRenderer getEfeitosHTMLValorDecorator() {
        return this.efeitosHTMLDinamicoRenderer;
    }

    public void setEfeitosHTMLValorDecorator(MascaraLinkHTMLValorDinamicoRenderer efeitosHTMLValorDecorator) {
        this.efeitosHTMLDinamicoRenderer = efeitosHTMLValorDecorator;
    }

    public String getFormattedValue(Object valor) {
        if (valor != null) {
            return this.mascaraRenderer.aplica(valor).toString();
        } else {
            return this.getTextoValorNulo();
        }
    }

    protected static MascaraRenderer montaDecorator(MascaraColunaMetaData mascara, MascaraRenderer decorator) {
        switch (mascara.getTipo()) {
            case MascaraColunaMetaData.TIPO_ANTES:
                decorator = new MascaraAntesRenderer(decorator, mascara.getMascara());
                break;
            case MascaraColunaMetaData.TIPO_DEPOIS:
                decorator = new MascaraDepoisRenderer(decorator, mascara.getMascara());
                break;
            case MascaraColunaMetaData.TIPO_DATA:
                decorator = new MascaraDataRenderer(decorator, mascara.getMascara());
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_MES:
                decorator = new MascaraMes(mascara.getMascara());
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_ANO_MES:
                decorator = new MascaraMesAno(mascara.getMascara());
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_SEMANA:
                decorator = new MascaraSemana(mascara.getMascara());
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_PERIODO:
                decorator = new MascaraPeriodo(mascara.getMascara());
                break;
        }
        return decorator;
    }

    protected static void montaDecoratorLinkHTML(List<MascaraLinkHTMLMetaData> mascarasHTML,
                                                 ColunaMetaData colunaMetaData) {
        for (MascaraLinkHTMLMetaData mascara : mascarasHTML) {
            switch (mascara.getTipo()) {
                case MascaraLinkHTMLMetaData.TIPO_DEPOIS_VALOR:
                    colunaMetaData.setEfeitosHTMLDecorator(new MascaraLinkHTMLDepoisRenderer(colunaMetaData.getEfeitoHTMLDecorator(), mascara.getLinkHTML()));
                    break;
                case MascaraLinkHTMLMetaData.TIPO_VALOR:
                    MascaraLinkHTMLTextoRenderer mascaraHTML = new MascaraLinkHTMLTextoRenderer((LinkHTMLTexto) mascara.getLinkHTML());
                    colunaMetaData.setEfeitosHTMLDecorator(mascaraHTML);
                    break;
                case MascaraLinkHTMLMetaData.TIPO_DINAMICO:
                    MascaraLinkHTMLValorDinamicoRenderer mascaraHTMLValor = new MascaraLinkHTMLValorDinamicoRenderer((LinkHTMLTextoDinamico) mascara.getLinkHTML());
                    colunaMetaData.setEfeitosHTMLValorDecorator(mascaraHTMLValor);
                    break;
            }
        }
    }

    protected static void factory(ColunaMetaData colunaMetaData, CampoMetaData campoMetaData) {
        List<MascaraColunaMetaData> mascarasCampo = campoMetaData.getMascarasCampo();
        MascaraRenderer decorator = new MascaraPadraoRenderer();
        for (MascaraColunaMetaData mascara : mascarasCampo) {
            decorator = montaDecorator(mascara, decorator);
        }
        colunaMetaData.setDecorator(decorator);

        montaDecoratorLinkHTML(campoMetaData.getMascarasLinkHTML(), colunaMetaData);
        colunaMetaData.setCellProperty(campoMetaData.getLarguraColuna(), campoMetaData.getPosicaoAlinhamentoColuna());
        colunaMetaData.setHasCampoSequencia(campoMetaData.isMostraSequencia());
        if (campoMetaData.getExpressaoRanking() != null) {
            colunaMetaData.setFuncaoRanking(FuncaoRanking.factory(campoMetaData.getExpressaoRanking()));
        }
    }

    public abstract void imprimeValorTipoCampo(Object valor, String propriedadeCelula, Impressor impressor);

    public abstract List<?> getAlertasCoresCelula();

    public abstract String getTextoValorNulo();

}
