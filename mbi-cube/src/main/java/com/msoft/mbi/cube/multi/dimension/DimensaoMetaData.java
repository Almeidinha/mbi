package com.msoft.mbi.cube.multi.dimension;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.column.ColumnMetaData;
import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.column.TipoData;
import com.msoft.mbi.cube.multi.column.TipoDecimal;
import com.msoft.mbi.cube.multi.column.TipoDimensaoInteiro;
import com.msoft.mbi.cube.multi.column.TipoHora;
import com.msoft.mbi.cube.multi.column.TextType;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsDimensao;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsValorDimensao;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoComparator;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoPadraoComparator;
import com.msoft.mbi.cube.multi.generation.Impressor;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimensaoMetaData extends ColumnMetaData {

    private DimensaoMetaData parent = null;
    @Setter(AccessLevel.NONE)
    private String coluna;
    private DimensaoMetaData dimensaoOrdenacao = null;
    @Setter(AccessLevel.NONE)
    private int tipoOrdenacao = 1;
    private int sequenciaOrdenacao;
    private boolean totalizacaoParcial = false;
    private boolean mediaParcial = false;
    private boolean expressaoParcial = false;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean isDrillDown = false;
    @Setter(AccessLevel.NONE)
    private DimensaoMetaData filho = null;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient Cube cube = null;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final DataType<?> tipo;
    private int qtdNiveisAbaixo = 0;
    private int qtdNiveisAbaixoComSequencia = 0;
    private final List<ColorAlertConditionsDimensao> alertasCoresLinha;
    @Setter(AccessLevel.NONE)
    private final List<ColorAlertConditionsDimensao> colorAlertCells;
    private DimensaoComparator comparator;
    private transient DimensaoMetaData nivelAcimaTotalizado = null;
    private int eixoReferencia = LINHA;
    public static final int LINHA = 1;
    public static final int COLUNA = 2;

    private MetaDataField metadataField = null;

    protected DimensaoMetaData(String titulo, String coluna, DataType<?> tipo) {
        super(titulo, MetaDataField.DIMENSION);
        this.coluna = coluna;
        this.tipo = tipo;
        this.colorAlertCells = new ArrayList<>();
        this.alertasCoresLinha = new ArrayList<>();
        this.comparator = DimensaoPadraoComparator.getInstance();
    }

    public Cube getCubo() {
        return cube;
    }

    public MetaDataField getCampoMetadadata() {
        return metadataField;
    }

    public void setCampoMetadadata(MetaDataField campo) {
        this.metadataField = campo;
    }

    public void setAscendente(boolean asc) {
        this.tipoOrdenacao = ((asc) ? 1 : -1);
    }

    public boolean isUltima() {
        return this.filho == null;
    }

    public void setFilho(DimensaoMetaData filho) {
        this.filho = filho;
        if (filho.hasSequenceFields()) {
            incrementaNiveis(2);
        } else {
            incrementaNiveis(1);
        }
    }

    public void setCubo(Cube cube) {
        this.cube = cube;
    }

    public boolean isLinha() {
        return this.eixoReferencia == LINHA;
    }

    @SuppressWarnings("rawtypes")
    public DataType getTipo() {
        return tipo;
    }

    public boolean isDrillDown() {
        return isDrillDown;
    }

    public void setDrillDown(boolean isDrillDown) {
        this.isDrillDown = isDrillDown;
    }

    public void setNivelAcimaTotalizado() {
        this.setNivelAcimaTotalizado(this.buscaNivelAcimaTotalizado());
    }

    private void addAlertaCorLinha(ColorAlertConditionsDimensao alertaCor) {
        this.alertasCoresLinha.add(alertaCor);
    }

    private void addAlertaCorCelula(ColorAlertConditionsDimensao alertaCor) {
        this.colorAlertCells.add(alertaCor);
    }

    private void addAlertaCor(ColorAlertConditionsDimensao alertaCor) {
        if (ColorAlertMetadata.PAINT_LINE_ACTION == alertaCor.getAction()) {
            this.addAlertaCorLinha(alertaCor);
        } else {
            this.addAlertaCorCelula(alertaCor);
        }
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public static DimensaoMetaData factory(MetaDataField metaDataField) {
        DimensaoMetaData dimensaoMetaData;
        String titulo = metaDataField.getTitle();
        String coluna;
        if (metaDataField.getFieldNickname() != null) {
            coluna = metaDataField.getFieldNickname();
        } else {
            coluna = metaDataField.getName();
        }
        DataType<?> dataType = null;
        if (MetaDataField.TEST_TYPE.equals(metaDataField.getDataType())) {
            dataType = new TextType();
        } else if (MetaDataField.DATA_TYPE.equals(metaDataField.getDataType())) {
            dataType = new TipoData();
        } else if (MetaDataField.HOUR_TYPE.equals(metaDataField.getDataType())) {
            dataType = new TipoHora();
        } else if (MetaDataField.INT_TYPE.equals(metaDataField.getDataType())) {
            dataType = new TipoDimensaoInteiro();
        } else if (MetaDataField.DECIMAL_TYPE.equals(metaDataField.getDataType())) {
            dataType = new TipoDecimal();
        }

        dimensaoMetaData = new DimensaoMetaData(titulo, coluna, dataType);

        dimensaoMetaData.setCampoMetadadata(metaDataField);

        if (metaDataField.getOrderingField() != null) {
            DimensaoMetaData dimensaoOrdenacao = factory(metaDataField.getOrderingField());
            dimensaoMetaData.setDimensaoOrdenacao(dimensaoOrdenacao);
        }
        ColumnMetaData.factory(dimensaoMetaData, metaDataField);
        dimensaoMetaData.setAscendente(!("DESC".equals(metaDataField.getOrderDirection()) && metaDataField.getOrder() > 0));

        ColorAlertProperties propriedadeAlerta = null;
        ColorAlertConditionsValorDimensao condicaoAlertaCores = null;
        List<ColorAlertMetadata> alertas = metaDataField.getColorAlertMetadata();
        for (ColorAlertMetadata alertaCor : alertas) {
            propriedadeAlerta = ColorAlertProperties.factory(alertaCor.getFontColor(), alertaCor.getBackGroundColor(), alertaCor.getFontStyle(),
                    alertaCor.isBold(), alertaCor.isItalic(), alertaCor.getFontSize());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALINHAMENTO_ESQUERDA);
            condicaoAlertaCores = new ColorAlertConditionsValorDimensao(alertaCor.getSequence(), propriedadeAlerta, alertaCor.getFunction(),
                    alertaCor.getAction(), alertaCor.getOperator(), dimensaoMetaData, alertaCor.getValues());
            dimensaoMetaData.addAlertaCor(condicaoAlertaCores);
        }

        dimensaoMetaData.setDrillDown(metaDataField.isDrillDown());
        dimensaoMetaData.setSequenciaOrdenacao(metaDataField.getOrder());
        dimensaoMetaData.setTotalizacaoParcial(metaDataField.isTotalPartial());
        dimensaoMetaData.setMediaParcial(metaDataField.isMediaPartial());
        dimensaoMetaData.setExpressaoParcial(metaDataField.isExpressionInPartial());
        dimensaoMetaData.setPartialTotalExpression(metaDataField.isExpressionInTotalPartial());
        return dimensaoMetaData;
    }

    private DimensaoMetaData buscaNivelAcimaTotalizado() {
        if (this.getParent() == null) {
            return this;
        }
        if (!this.getParent().isTotalizacaoParcial() && !this.getParent().isMediaParcial() && !this.getParent().isExpressaoParcial()) {
            return this.getParent().buscaNivelAcimaTotalizado();
        }
        return this.getParent();
    }

    private void incrementaNiveis(int niveisAbaixoComSequencia) {
        this.qtdNiveisAbaixo++;
        this.qtdNiveisAbaixoComSequencia += niveisAbaixoComSequencia;
        if (this.parent != null) {
            parent.incrementaNiveis(niveisAbaixoComSequencia);
        }
    }

    @Override
    public String getTextNullValue() {
        return "";
    }

    @Override
    public void printFieldTypeValue(Object valor, String cellProperty, Impressor impressor) {
        impressor.imprimeValorColuna(cellProperty, valor, this);
    }

    public String getEstiloPadrao() {
        return CellProperty.PROPRIEDADE_CELULA_VALOR_DIMENSAO;
    }

}
