package com.msoft.mbi.cube.multi.column;

import java.util.List;

import com.msoft.mbi.cube.multi.resumeFunctions.FunctionRanking;
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

public abstract class ColumnMetaData {

    @Getter
    @Setter
    private String title;
    private MascaraRenderer mascaraRenderer;
    private MascaraRenderer hTMLEffectRenderer;
    private MascaraLinkHTMLValorDinamicoRenderer hTMLDynamicEffectRenderer;
    @Getter
    @Setter
    private CellProperty cellProperty;
    private boolean sequenceField = false;
    @Getter
    @Setter
    private FunctionRanking functionRanking;
    @Getter
    @Setter
    private boolean partialTotalExpression = false;

    public ColumnMetaData(String title, String fieldType) {
        this.title = title;
        this.mascaraRenderer = new MascaraPadraoRenderer();
        this.hTMLEffectRenderer = new MascaraPadraoRenderer();
        this.hTMLDynamicEffectRenderer = new MascaraLinkHTMLValorDinamicoPadraoRenderer();
        this.cellProperty = new CellProperty();
    }

    public void setDecorator(MascaraRenderer decorator) {
        this.mascaraRenderer = decorator;
    }

    public void setHTMLEffectRenderer(MascaraRenderer decorator) {
        this.hTMLEffectRenderer = decorator;
    }

    public void addDecorator(MascaraColunaMetaData mascara) {
        this.mascaraRenderer = montaDecorator(mascara, this.mascaraRenderer);
    }

    public void addHTMLEffectRenderer(MascaraColunaMetaData mascara) {
        this.hTMLEffectRenderer = montaDecorator(mascara, this.hTMLEffectRenderer);
    }

    public void setCellProperty(int columnWidth, String alignment) {
        this.cellProperty.setWidth(columnWidth);
        this.cellProperty.addExtraAttributes("min-width", columnWidth + "px");
        this.cellProperty.setAlignment(alignment);
    }

    public boolean hasSequenceFields() {
        return sequenceField;
    }

    public void setHasSequenceFields(boolean sequenceField) {
        this.sequenceField = sequenceField;
    }

    public MascaraRenderer getHTMLEffectRenderer() {
        return hTMLEffectRenderer;
    }

    public MascaraLinkHTMLValorDinamicoRenderer getHTMLDynamicEffectRenderer() {
        return this.hTMLDynamicEffectRenderer;
    }

    public void setHTMLDynamicEffectRenderer(MascaraLinkHTMLValorDinamicoRenderer hTMLDynamicEffectRenderer) {
        this.hTMLDynamicEffectRenderer = hTMLDynamicEffectRenderer;
    }

    public String getFormattedValue(Object valor) {
        if (valor != null) {
            return this.mascaraRenderer.aplica(valor).toString();
        } else {
            return this.getTextNullValue();
        }
    }

    protected static MascaraRenderer montaDecorator(MascaraColunaMetaData mascara, MascaraRenderer decorator) {
        int maskType = mascara.getTipo();
        String mascaraValue = mascara.getMascara();

        switch (maskType) {
            case MascaraColunaMetaData.TIPO_ANTES:
                decorator = new MascaraAntesRenderer(decorator, mascaraValue);
                break;
            case MascaraColunaMetaData.TIPO_DEPOIS:
                decorator = new MascaraDepoisRenderer(decorator, mascaraValue);
                break;
            case MascaraColunaMetaData.TIPO_DATA:
                decorator = new MascaraDataRenderer(decorator, mascaraValue);
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_MES:
                decorator = new MascaraMes(mascaraValue);
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_ANO_MES:
                decorator = new MascaraMesAno(mascaraValue);
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_SEMANA:
                decorator = new MascaraSemana(mascaraValue);
                break;
            case MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_PERIODO:
                decorator = new MascaraPeriodo(mascaraValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid mascara type: " + maskType);
        }
        return decorator;
    }

    protected static void buildDecoratorHTMLLink(List<MascaraLinkHTMLMetaData> mascarasHTML,
                                                 ColumnMetaData columnMetaData) {
        for (MascaraLinkHTMLMetaData mascara : mascarasHTML) {
            switch (mascara.getTipo()) {
                case MascaraLinkHTMLMetaData.TIPO_DEPOIS_VALOR:
                    columnMetaData.setHTMLEffectRenderer(new MascaraLinkHTMLDepoisRenderer(columnMetaData.getHTMLEffectRenderer(), mascara.getLinkHTML()));
                    break;
                case MascaraLinkHTMLMetaData.TIPO_VALOR:
                    MascaraLinkHTMLTextoRenderer mascaraHTML = new MascaraLinkHTMLTextoRenderer((LinkHTMLTexto) mascara.getLinkHTML());
                    columnMetaData.setHTMLEffectRenderer(mascaraHTML);
                    break;
                case MascaraLinkHTMLMetaData.TIPO_DINAMICO:
                    MascaraLinkHTMLValorDinamicoRenderer mascaraHTMLValor = new MascaraLinkHTMLValorDinamicoRenderer((LinkHTMLTextoDinamico) mascara.getLinkHTML());
                    columnMetaData.setHTMLDynamicEffectRenderer(mascaraHTMLValor);
                    break;
            }
        }
    }

    protected static void factory(ColumnMetaData columnMetaData, CampoMetaData campoMetaData) {
        List<MascaraColunaMetaData> mascarasCampo = campoMetaData.getMascarasCampo();
        MascaraRenderer decorator = new MascaraPadraoRenderer();
        for (MascaraColunaMetaData mascara : mascarasCampo) {
            decorator = montaDecorator(mascara, decorator);
        }
        columnMetaData.setDecorator(decorator);

        buildDecoratorHTMLLink(campoMetaData.getMascarasLinkHTML(), columnMetaData);
        columnMetaData.setCellProperty(campoMetaData.getLarguraColuna(), campoMetaData.getPosicaoAlinhamentoColuna());
        columnMetaData.setHasSequenceFields(campoMetaData.isMostraSequencia());
        if (campoMetaData.getExpressaoRanking() != null) {
            columnMetaData.setFunctionRanking(FunctionRanking.factory(campoMetaData.getExpressaoRanking()));
        }
    }

    public abstract void printFieldTypeValue(Object valor, String cellProperty, Impressor impressor);

    public abstract List<?> getColorAlertCells();

    public abstract String getTextNullValue();

}
