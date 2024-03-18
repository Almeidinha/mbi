package com.msoft.mbi.cube.multi.dimension;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.column.ColunaMetaData;
import com.msoft.mbi.cube.multi.column.DataType;
import com.msoft.mbi.cube.multi.column.TipoData;
import com.msoft.mbi.cube.multi.column.TipoDecimal;
import com.msoft.mbi.cube.multi.column.TipoDimensaoInteiro;
import com.msoft.mbi.cube.multi.column.TipoHora;
import com.msoft.mbi.cube.multi.column.TipoTexto;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsDimensao;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertConditionsValorDimensao;
import com.msoft.mbi.cube.multi.colorAlertCondition.ColorAlertProperties;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoComparator;
import com.msoft.mbi.cube.multi.dimension.comparator.DimensaoPadraoComparator;
import com.msoft.mbi.cube.multi.generation.Impressor;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.renderers.CellProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimensaoMetaData extends ColunaMetaData {

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
    private transient Cubo cube = null;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final DataType<?> tipo;
    private int qtdNiveisAbaixo = 0;
    private int qtdNiveisAbaixoComSequencia = 0;
    private final List<ColorAlertConditionsDimensao> alertasCoresLinha;
    @Setter(AccessLevel.NONE)
    private final List<ColorAlertConditionsDimensao> alertasCoresCelula;
    private DimensaoComparator comparator;
    private transient DimensaoMetaData nivelAcimaTotalizado = null;
    private int eixoReferencia = LINHA;
    public static final int LINHA = 1;
    public static final int COLUNA = 2;

    private CampoMetaData metadataField = null;

    protected DimensaoMetaData(String titulo, String coluna, DataType<?> tipo) {
        super(titulo, CampoMetaData.DIMENSAO);
        this.coluna = coluna;
        this.tipo = tipo;
        this.alertasCoresCelula = new ArrayList<>();
        this.alertasCoresLinha = new ArrayList<>();
        this.comparator = DimensaoPadraoComparator.getInstance();
    }

    public Cubo getCubo() {
        return cube;
    }

    public CampoMetaData getCampoMetadadata() {
        return metadataField;
    }

    public void setCampoMetadadata(CampoMetaData campo) {
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
        if (filho.hasCampoSequencia()) {
            incrementaNiveis(2);
        } else {
            incrementaNiveis(1);
        }
    }

    public void setCubo(Cubo cube) {
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
        this.alertasCoresCelula.add(alertaCor);
    }

    private void addAlertaCor(ColorAlertConditionsDimensao alertaCor) {
        if (AlertaCorMetaData.ACAO_PINTAR_LINHA == alertaCor.getAction()) {
            this.addAlertaCorLinha(alertaCor);
        } else {
            this.addAlertaCorCelula(alertaCor);
        }
    }

    @Override
    public String toString() {
        return this.getTitulo();
    }

    public static DimensaoMetaData factory(CampoMetaData campoMetaData) {
        DimensaoMetaData dimensaoMetaData;
        String titulo = campoMetaData.getTituloCampo();
        String coluna;
        if (campoMetaData.getApelidoCampo() != null) {
            coluna = campoMetaData.getApelidoCampo();
        } else {
            coluna = campoMetaData.getNomeCampo();
        }
        DataType<?> dataType = null;
        if (CampoMetaData.TIPO_TEXTO.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoTexto();
        } else if (CampoMetaData.TIPO_DATA.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoData();
        } else if (CampoMetaData.TIPO_HORA.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoHora();
        } else if (CampoMetaData.TIPO_INTEIRO.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoDimensaoInteiro();
        } else if (CampoMetaData.TIPO_DECIMAL.equals(campoMetaData.getTipoDado())) {
            dataType = new TipoDecimal();
        }

        dimensaoMetaData = new DimensaoMetaData(titulo, coluna, dataType);

        dimensaoMetaData.setCampoMetadadata(campoMetaData);

        if (campoMetaData.getCampoOrdenacao() != null) {
            DimensaoMetaData dimensaoOrdenacao = factory(campoMetaData.getCampoOrdenacao());
            dimensaoMetaData.setDimensaoOrdenacao(dimensaoOrdenacao);
        }
        ColunaMetaData.factory(dimensaoMetaData, campoMetaData);
        dimensaoMetaData.setAscendente(!("DESC".equals(campoMetaData.getSentidoOrdem()) && campoMetaData.getOrdem() > 0));

        ColorAlertProperties propriedadeAlerta = null;
        ColorAlertConditionsValorDimensao condicaoAlertaCores = null;
        List<AlertaCorMetaData> alertas = campoMetaData.getAlertasCoresValor();
        for (AlertaCorMetaData alertaCor : alertas) {
            propriedadeAlerta = ColorAlertProperties.factory(alertaCor.getCorFonte(), alertaCor.getCorFundo(), alertaCor.getEstiloFonte(),
                    alertaCor.isNegrito(), alertaCor.isItalico(), alertaCor.getTamanhoFonte());
            propriedadeAlerta.setAlignment(ColorAlertProperties.ALINHAMENTO_ESQUERDA);
            condicaoAlertaCores = new ColorAlertConditionsValorDimensao(alertaCor.getSequencia(), propriedadeAlerta, alertaCor.getFuncao(),
                    alertaCor.getAcao(), alertaCor.getOperador(), dimensaoMetaData, alertaCor.getValores());
            dimensaoMetaData.addAlertaCor(condicaoAlertaCores);
        }

        dimensaoMetaData.setDrillDown(campoMetaData.isDrillDown());
        dimensaoMetaData.setSequenciaOrdenacao(campoMetaData.getOrdem());
        dimensaoMetaData.setTotalizacaoParcial(campoMetaData.isTotalizacaoParcial());
        dimensaoMetaData.setMediaParcial(campoMetaData.isMediaParcial());
        dimensaoMetaData.setExpressaoParcial(campoMetaData.isExpressaoNaParcial());
        dimensaoMetaData.setExpressaoParcialTotal(campoMetaData.isExpressaoNaParcialTotal());
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
    public String getTextoValorNulo() {
        return "";
    }

    @Override
    public void imprimeValorTipoCampo(Object valor, String propriedadeCelula, Impressor impressor) {
        impressor.imprimeValorColuna(propriedadeCelula, valor, this);
    }

    public String getEstiloPadrao() {
        return CellProperty.PROPRIEDADE_CELULA_VALOR_DIMENSAO;
    }

}
