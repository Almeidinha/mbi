package com.msoft.mbi.cube.multi.column;

import java.util.List;

import com.msoft.mbi.cube.multi.resumefunctions.FunctionRanking;
import com.msoft.mbi.cube.multi.generation.Printer;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metadata.HTMLLineMask;
import com.msoft.mbi.cube.multi.renderers.MaskBeforeRenderer;
import com.msoft.mbi.cube.multi.renderers.MascaraDataRenderer;
import com.msoft.mbi.cube.multi.renderers.MaskAfterRenderer;
import com.msoft.mbi.cube.multi.renderers.MaskMonth;
import com.msoft.mbi.cube.multi.renderers.MaskMonthYear;
import com.msoft.mbi.cube.multi.renderers.MaskDefaultRenderer;
import com.msoft.mbi.cube.multi.renderers.MaskPeriod;
import com.msoft.mbi.cube.multi.renderers.MaskRenderer;
import com.msoft.mbi.cube.multi.renderers.MaskWeek;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLDynamicText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MascLinkHTMLTextRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDefaultDynamicValueRenderer;
import com.msoft.mbi.cube.multi.renderers.linkHTML.MaskLinkHTMLDynamicValueRenderer;
import lombok.Getter;
import lombok.Setter;

public abstract class ColumnMetaData {

    @Getter
    @Setter
    private String title;
    private MaskRenderer maskRenderer;
    private MaskRenderer hTMLEffectRenderer;
    private MaskLinkHTMLDynamicValueRenderer hTMLDynamicEffectRenderer;
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
        this.maskRenderer = new MaskDefaultRenderer();
        this.hTMLEffectRenderer = new MaskDefaultRenderer();
        this.hTMLDynamicEffectRenderer = new MaskLinkHTMLDefaultDynamicValueRenderer();
        this.cellProperty = new CellProperty();
    }

    public void setDecorator(MaskRenderer decorator) {
        this.maskRenderer = decorator;
    }

    public void setHTMLEffectRenderer(MaskRenderer decorator) {
        this.hTMLEffectRenderer = decorator;
    }

    public void addDecorator(MaskColumnMetaData mascara) {
        this.maskRenderer = montaDecorator(mascara, this.maskRenderer);
    }

    public void addHTMLEffectRenderer(MaskColumnMetaData mascara) {
        this.hTMLEffectRenderer = montaDecorator(mascara, this.hTMLEffectRenderer);
    }

    public void setCellProperty(int columnWidth, String alignment) {
        this.cellProperty.setWidth(columnWidth);
        // TODO Validate is this will work with excel export
        this.cellProperty.addExtraAttributes("minWidth", columnWidth + "px");
        this.cellProperty.setAlignment(alignment);
    }

    public boolean hasSequenceFields() {
        return sequenceField;
    }

    public void setHasSequenceFields(boolean sequenceField) {
        this.sequenceField = sequenceField;
    }

    public MaskRenderer getHTMLEffectRenderer() {
        return hTMLEffectRenderer;
    }

    public MaskLinkHTMLDynamicValueRenderer getHTMLDynamicEffectRenderer() {
        return this.hTMLDynamicEffectRenderer;
    }

    public void setHTMLDynamicEffectRenderer(MaskLinkHTMLDynamicValueRenderer hTMLDynamicEffectRenderer) {
        this.hTMLDynamicEffectRenderer = hTMLDynamicEffectRenderer;
    }

    public String getFormattedValue(Object valor) {
        if (valor != null) {
            return this.maskRenderer.apply(valor).toString();
        } else {
            return this.getTextNullValue();
        }
    }

    protected static MaskRenderer montaDecorator(MaskColumnMetaData mascara, MaskRenderer decorator) {
        int maskType = mascara.getType();
        String mascaraValue = mascara.getMascara();

        switch (maskType) {
            case MaskColumnMetaData.TYPE_BEFORE:
                decorator = new MaskBeforeRenderer(decorator, mascaraValue);
                break;
            case MaskColumnMetaData.TYPE_AFTER:
                decorator = new MaskAfterRenderer(decorator, mascaraValue);
                break;
            case MaskColumnMetaData.DATA_TYPE:
                decorator = new MascaraDataRenderer(decorator, mascaraValue);
                break;
            case MaskColumnMetaData.TYPE_EIS_DIMENSION_MONTH:
                decorator = new MaskMonth(mascaraValue);
                break;
            case MaskColumnMetaData.TYPE_EIS_DIMENSION_MONTH_YEAR:
                decorator = new MaskMonthYear(mascaraValue);
                break;
            case MaskColumnMetaData.TYPE_EIS_DIMENSION_WEEK:
                decorator = new MaskWeek(mascaraValue);
                break;
            case MaskColumnMetaData.TYPE_EIS_DIMENSION_PERIOD:
                decorator = new MaskPeriod(mascaraValue);
                break;
            default:
                throw new IllegalArgumentException("Invalid mascara type: " + maskType);
        }
        return decorator;
    }

    protected static void buildDecoratorHTMLLink(List<HTMLLineMask> mascarasHTML,
                                                 ColumnMetaData columnMetaData) {
        for (HTMLLineMask mascara : mascarasHTML) {
            switch (mascara.getType()) {
                case HTMLLineMask.VALUE_TYPE_AFTER:
                    columnMetaData.setHTMLEffectRenderer(new com.msoft.mbi.cube.multi.renderers.linkHTML.MaskAfterRenderer(columnMetaData.getHTMLEffectRenderer(), mascara.getLinkHTML()));
                    break;
                case HTMLLineMask.VALUE_TYPE:
                    MascLinkHTMLTextRenderer mascaraHTML = new MascLinkHTMLTextRenderer((LinkHTMLText) mascara.getLinkHTML());
                    columnMetaData.setHTMLEffectRenderer(mascaraHTML);
                    break;
                case HTMLLineMask.DYNAMIC_TYPE:
                    MaskLinkHTMLDynamicValueRenderer mascaraHTMLValor = new MaskLinkHTMLDynamicValueRenderer((LinkHTMLDynamicText) mascara.getLinkHTML());
                    columnMetaData.setHTMLDynamicEffectRenderer(mascaraHTMLValor);
                    break;
            }
        }
    }

    protected static void factory(ColumnMetaData columnMetaData, MetaDataField metaDataField) {
        List<MaskColumnMetaData> mascarasCampo = metaDataField.getFieldMask();
        MaskRenderer decorator = new MaskDefaultRenderer();
        for (MaskColumnMetaData mascara : mascarasCampo) {
            decorator = montaDecorator(mascara, decorator);
        }
        columnMetaData.setDecorator(decorator);

        buildDecoratorHTMLLink(metaDataField.getHtmlLineMasks(), columnMetaData);
        columnMetaData.setCellProperty(metaDataField.getColumnWidth(), metaDataField.getColumnAlignmentPosition());
        columnMetaData.setHasSequenceFields(metaDataField.isShowSequence());
        if (metaDataField.getRankingExpression() != null) {
            columnMetaData.setFunctionRanking(FunctionRanking.factory(metaDataField.getRankingExpression()));
        }
    }

    public abstract void printFieldTypeValue(Object valor, String cellProperty, Printer printer);

    public abstract List<?> getColorAlertCells();

    public abstract String getTextNullValue();

}
