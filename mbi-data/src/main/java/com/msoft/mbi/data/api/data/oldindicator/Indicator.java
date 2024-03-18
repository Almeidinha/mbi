package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.cube.multi.Cubo;
import com.msoft.mbi.cube.multi.column.MascaraColunaMetaData;
import com.msoft.mbi.cube.multi.generation.DefaultTableBuilder;
import com.msoft.mbi.cube.multi.generation.GeradorTabela;
import com.msoft.mbi.cube.multi.generation.ImpressorHTML;
import com.msoft.mbi.cube.multi.generation.MultiDimensionalDefaultTableBuilder;
import com.msoft.mbi.cube.multi.metaData.AlertaCorMetaData;
import com.msoft.mbi.cube.multi.metaData.CampoMetaData;
import com.msoft.mbi.cube.multi.metaData.CuboMetaData;
import com.msoft.mbi.cube.multi.metaData.MascaraLinkHTMLMetaData;
import com.msoft.mbi.cube.multi.renderers.MascaraMes;
import com.msoft.mbi.cube.multi.renderers.MascaraMesAno;
import com.msoft.mbi.cube.multi.renderers.MascaraPeriodo;
import com.msoft.mbi.cube.multi.renderers.MascaraSemana;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLSVGColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTexto;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoColuna;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLTextoDinamico;
import com.msoft.mbi.cube.util.CuboListener;
import com.msoft.mbi.cube.util.DefaultCuboListener;
import com.msoft.mbi.cube.util.logicOperators.OperadoresLogicos;
import com.msoft.mbi.data.api.data.consult.*;
import com.msoft.mbi.data.api.data.exception.*;
import com.msoft.mbi.data.api.data.filters.*;
import com.msoft.mbi.data.api.data.htmlbuilder.CelulaHTML;
import com.msoft.mbi.data.api.data.htmlbuilder.EstiloHTML;
import com.msoft.mbi.data.api.data.htmlbuilder.LinhaHTML;
import com.msoft.mbi.data.api.data.htmlbuilder.TabelaHTML;
import com.msoft.mbi.data.api.data.util.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Stream;

@Getter
@Setter
@Data
@Slf4j
@SuppressWarnings("unused")
public class Indicator {

    public Indicator() throws BIException {
        temporaryFrozenStatus = false;
        frozenStatus = false;
        this.analysisGroupPermissions = new ArrayList<>();
        this.analysisUserPermissions = new ArrayList<>();
        filters = new Filters();
        this.analysisComments = new AnalysisComments();
    }


    public Indicator(int indicatorCode) {
        this.originalCode = indicatorCode;
        this.code = indicatorCode;
        this.filters = new Filters();
    }

    public Indicator(int code, String userId) throws BIException {
        this();
        this.code = code;
        this.userId = Integer.parseInt(userId);
    }

    public Indicator(int code, int userId) throws BIException {
        this();
        this.code = code;
        this.userId = userId;
        this.setFields();
    }

    private int code;
    private String name;
    private int areaCode;
    private Filters filters;
    private FiltersFunction filtersFunction;
    private List<Field> fields;
    public ArrayList<Field> deletedFields;
    private AnalysisComments analysisComments;
    private List<analysisUserPermission> analysisUserPermissions;
    private List<com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions> analysisGroupPermissions;
    private String fileName;
    private String searchClause;
    private ClausulaCondicaoFixa fixedConditionClause;
    private ClausulaTabela fromClause;
    private ClausulaLigacoesTabela whereClause;
    private String groupClause;
    private String orderClause;
    private boolean scheduled = false;
    private Integer scheduledCode = null;
    private String filterTable;
    private boolean temporaryFrozenStatus;
    private boolean frozenStatus;
    private int panelCode;
    private Integer userId;
    private CachedResults tableRecords;
    private String connectionId;
    private UUID tenantId;
    private boolean isMultidimensional;
    private String currentView = "T";

    transient private CuboListener cuboListener = new DefaultCuboListener();

    private int leftCoordinates = 10;
    private int topCoordinates = 60;
    private int height = 0;
    private int width = 0;
    private boolean isMaximized = true;
    private boolean isOpen = true;
    private Restrictions restrictions;
    private PartialTotalizations partialTotalizations;
    private int originalCode;
    private boolean usesSequence = false;
    private int tableType;
    private ValuesRepository sequeceValuesRepository;
    private ArrayList<ValuesRepository> accumulatedValuesRepository;
    private MetricDimensionRestrictions metricDimensionRestrictions;
    private ColorsAlert colorAlerts;
    private Integer originalIndicator;
    private boolean inheritsRestrictions = false;
    private boolean inheritsFields = false;
    private boolean replicateChanges;

    private Indicator childAnalysis;
    private List<String> indicadoresFilhosNaoReplicouFields;
    transient private AnalysisParameters analysisParameters;

    transient private Cubo cubo;
    transient private GeradorTabela cubeTable;

    private List<Field> dimensionColumn;

    transient private Map<Field, CampoMetaData> BICubeMapedFields;
    private int panelIndex;
    private boolean hasData;

    public static final int DEFAULT_TABLE = 1;
    public static final int MULTIDIMENSIONAL_TABLE = 2;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public String getVisualizacaoAtual(String vizualizacao_atual) {
        String visualizacao = "Tables";
        if (vizualizacao_atual != null) {
            visualizacao = switch (vizualizacao_atual) {
                case "G" -> "Graphics";
                case "C" -> "Comments";
                case "A" -> "Attachments";
                default -> visualizacao;
            };
        }
        return visualizacao;
    }

    public void setVisualizacaoAtual(String vizualizacao_atual) throws BIException {
        if (vizualizacao_atual != null) {
            this.currentView = geraVisualizacaoAtual(vizualizacao_atual);
            if (this.getAnalysisParameters() != null) {
                if (!this.currentView.equals(
                        this.analysisParameters.getParametroAnaliseUsuario(this.userId, AnalysisParameters.VIZUALIZACAO_PADRAO_ANALISE))) {
                    this.analysisParameters.removeParametroValor(AnalysisParameters.VIZUALIZACAO_PADRAO_ANALISE, this.analysisParameters
                            .getParametroAnaliseUsuario(userId, AnalysisParameters.VIZUALIZACAO_PADRAO_ANALISE));
                    this.analysisParameters.criaParametro(AnalysisParameters.VIZUALIZACAO_PADRAO_ANALISE, this.currentView, userId);
                }
            }
        }
    }

    public static String geraVisualizacaoAtual(String vizualizacaoAtual) {
        if (vizualizacaoAtual.trim().equals("Graphics") || vizualizacaoAtual.trim().equals("G")) {
            vizualizacaoAtual = "G";
        } else if (vizualizacaoAtual.equals("Comments") || vizualizacaoAtual.trim().equals("C")) {
            vizualizacaoAtual = "C";
        } else if (vizualizacaoAtual.equals("Attachments") || vizualizacaoAtual.trim().equals("A")) {
            vizualizacaoAtual = "A";
        } else {
            vizualizacaoAtual = "T";
        }
        return vizualizacaoAtual;

    }

    public void setColumnsWidth(String columnsWidth) {

        if (Optional.ofNullable(columnsWidth).isEmpty()) {
            return;
        }

        String[] widths = columnsWidth.split("\\|");

        if (widths.length != this.fields.size()) {
            log.trace("Indicator.setColumnsWidth(): Mismatch between number of widths and fields");
            return;
        }

        int position = 0;
        for (String width : widths) {
            Field field = this.fields.get(position);
            if (field != null && ("S".equals(field.getDefaultField()) || "T".equals(field.getDefaultField()))) {
                field.setColumnWidth(width);
            }
            position++;
        }
    }

    public DimensionFilter getDimensionFilter() {
        return filters.getDimensionFilter();
    }

    public String getVisualizacaoAtual() {
        return this.getVisualizacaoAtual(this.currentView);
    }

    public MetricFilters getMetricFilters() {
        return filters.getMetricFilters();
    }

    public void setName(String name) throws BIException {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        } else {
            throw new BIGeneralException("Erro ao atribuir nome da análise, parámetro sem valor.");
        }
    }

    public void setComentario(String newComentario, String dataExpiracao, String envia_email) throws BIException {
        Comment comentario = new Comment();
        comentario.setUserId(this.userId);
        comentario.setDateHour(new Timestamp((new Date()).getTime()));
        comentario.setCommentText(newComentario);

        BIDate dataExp = null;
        try {
            dataExp = new BIDate(dataExpiracao);
        } catch (DateException e) {
            log.error("Error: " + e.getMessage());
        }
        comentario.getExpirationDate().setTimeInMillis(dataExp.getTimeInMillis());
        comentario.setSendEmail(envia_email);
        comentario.setIndicatorCode(this.originalCode);
        int codigoTemp = Integer.parseInt(BIUtil.getNewData("coment", "bi_coment", "where ind = " + this.originalCode));
        comentario.setCode(codigoTemp);
        ConnectionBean conexao = new ConnectionBean();
        try {
            comentario.save(conexao);
        } finally {
            conexao.closeConnection();
        }
        analysisComments.addComment(comentario);
    }

    public void setPermissaoGrupo(String cod_grupo, String permissao) {
        com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions permissaoGrupoUsuario = this.getPermissaoGrupoUsuario(Integer.parseInt(cod_grupo));
        if (permissaoGrupoUsuario != null) {
            permissaoGrupoUsuario.setAllowChange(permissao);
        } else {
            permissaoGrupoUsuario = new analysisGroupPermissions();
            permissaoGrupoUsuario.setUserGroup(Integer.parseInt(cod_grupo));
            permissaoGrupoUsuario.setAllowChange(permissao);
            this.addPermissaoGrupoUsuario(permissaoGrupoUsuario);
        }
    }

    public void addPermissaoUsuario(int codUsuario, String permissao, String favorita) {
        analysisUserPermission permissaoUsuario = this.getPermissaoUsuario(codUsuario);
        if (permissaoUsuario != null) {
            permissaoUsuario.setAllowChange(permissao);
            permissaoUsuario.setFavorite(favorita);
        } else {
            permissaoUsuario = new analysisUserPermission();
            permissaoUsuario.setUserId(codUsuario);
            permissaoUsuario.setAllowChange(permissao);
            permissaoUsuario.setFavorite(favorita);
            this.addPermissaoUsuario(permissaoUsuario);
        }
    }

    public void addPermissaoGrupoUsuario(com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions permissao) {
        this.analysisGroupPermissions.add(permissao);
    }

    public void addPermissaoUsuario(analysisUserPermission permissao) {
        this.analysisUserPermissions.add(permissao);
    }

    public boolean getPermissaoLeituraGrupo(String cod_grupo) {
        int codGrupo = Integer.parseInt(cod_grupo);
        for (com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions permissaoGrupoUsuario : this.analysisGroupPermissions) {
            if (permissaoGrupoUsuario.getUserGroup() == codGrupo) {
                return true;
            }
        }
        return false;
    }

    public analysisUserPermission getPermissaoUsuario(int codUsuario) {
        for (analysisUserPermission permissaoUsuario : this.analysisUserPermissions) {
            if (permissaoUsuario.getUserId() == codUsuario) {
                return permissaoUsuario;
            }
        }
        return null;
    }

    public com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions getPermissaoGrupoUsuario(int codGrupoUsuario) {
        for (com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions permissaoGrupoUsuario : this.analysisGroupPermissions) {
            if (permissaoGrupoUsuario.getUserGroup() == codGrupoUsuario) {
                return permissaoGrupoUsuario;
            }
        }
        return null;
    }

    public List<analysisUserPermission> getPermissaoUsuarioAnalise() {
        return this.analysisUserPermissions;
    }

    public List<com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions> getPermissaoGrupoUsuarioAnalise() {
        return this.analysisGroupPermissions;
    }

    public boolean hasPermissaoEscrita() {
        // TODO Load user from db
        if (this.getPermissaoEscritaGrupo(String.valueOf(1))) {
            return true;
        }
        analysisUserPermission permissaoUsuario = this.getPermissaoUsuario(this.userId);
        return permissaoUsuario != null && analysisUserPermission.PERMITE_MODIFICAR.equals(permissaoUsuario.getAllowChange());
    }

    public boolean hasPermissaoGrupoAnalise(int codGrupo) {
        return this.getPermissaoEscritaGrupo(String.valueOf(codGrupo)) || this.getPermissaoLeituraGrupo(String.valueOf(codGrupo));
    }

    public boolean hasPermissaoUsuarioAnalise(int codUsuario) {
        return this.getPermissaoUsuario(codUsuario) != null;
    }

    public boolean getPermissaoEscritaGrupo(String cod_grupo) {
        int codGrupo = Integer.parseInt(cod_grupo);
        for (com.msoft.mbi.data.api.data.oldindicator.analysisGroupPermissions permissaoGrupoUsuario : this.analysisGroupPermissions) {
            if (permissaoGrupoUsuario.getUserGroup() == codGrupo && "S".equalsIgnoreCase(permissaoGrupoUsuario.getAllowChange())) {
                return true;
            }
        }
        return false;
    }

    public void montaSaida() throws Exception {
        try {
            montaSaida(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private void createFiltroseRestricoesMetricasCuboMetaData(CuboMetaData cuboMetaData) throws BIException {
        MetricFilters filtrosMetrica = this.getMetricFilters();
        String expressaoFiltro = "";
        if (filtrosMetrica != null) {
            expressaoFiltro += filtrosMetrica.toStringWithCode();
        }
        cuboMetaData.setExpressaoFiltrosMetrica(expressaoFiltro);
        expressaoFiltro = "";
        FilterAccumulated filtroAcumulado = this.getFiltersFunction().getFilterAccumulated();
        if (filtroAcumulado != null) {
            expressaoFiltro += filtroAcumulado.toString();
        }
        cuboMetaData.setExpressaoFiltrosAcumulado(expressaoFiltro);
    }

    private CampoMetaData createFieldCuboMetaData(Field campoBI) {
        CampoMetaData campo = new CampoMetaData();
        boolean campoVisualizado = !"N".equalsIgnoreCase(campoBI.getDefaultField());
        campo.setAcumulaCampoLinha("S".equals(campoBI.getAccumulatedLine()));
        campo.setAgregacaoTipo(campoBI.getAggregationType());
        campo.setAnaliseHorizontalTipo(campoBI.getHorizontalAnalysisType());
        campo.setAnaliseVerticalTipo(campoBI.getVerticalAnalysisType());
        campo.setApelidoCampo(campoBI.getNickName());
        campo.setCampo(campoBI.getCode());
        campo.setDrillDown(campoBI.isDrillDown());
        campo.setExpressao(campoBI.isExpression());
        campo.setLarguraColuna(campoBI.getColumnWidth());
        campo.setLocalApresentacao(campoBI.getDisplayLocation() == Constants.LINE ? Constants.COLUMN : Constants.LINE);
        campo.setMascaraValorNulo("-");
        campo.setMostraSequencia(false);
        campo.setNomeCampo(campoBI.getName());
        campo.setNumPosDecimais(campoBI.getNumDecimalPositions());
        campo.setOrdem(campoBI.getOrder());
        campo.setOrdem(campoBI.getAccumulatedOrder());
        campo.setPadrao(campoBI.getDefaultField());

        if (campoBI.getDelegateOrder() == null) {
            campo.setCampoOrdenacao(null);
        } else {
            Field campoIndicator = this.getFieldPorCodigo(campoBI.getDelegateOrder());
            CampoMetaData campoOrdenacao = null;
            if (campoIndicator != null) {
                campoOrdenacao = this.createFieldCuboMetaData(campoIndicator);
            }
            campo.setCampoOrdenacao(campoOrdenacao);
        }
        campo.setParticipacaoAcumulada(campoBI.isAccumulatedParticipation());
        campo.setParticipacaoAcumuladaHorizontal(campoBI.isHorizontalParticipationAccumulated());
        campo.setParticipacaoHorizontal(campoBI.isHorizontalParticipation());
        campo.setPosicaoAlinhamentoColuna(campoBI.getColumnAlignmentPosition());

        if (!"".equalsIgnoreCase(campoBI.getDateMask()) && campoBI.getDateMask() != null && !"".equalsIgnoreCase(campoBI.getDataType())) {
            if (Constants.DATE.equalsIgnoreCase(campoBI.getDataType())) {
                campo.addMascara(new MascaraColunaMetaData(campoBI.getDateMask(), MascaraColunaMetaData.TIPO_DATA));
            } else if (Constants.DIMENSION.equals(campoBI.getFieldType())) {
                if (Constants.NUMBER.equals(campoBI.getDataType()) || Constants.STRING.equals(campoBI.getDataType())) {
                    if (campoBI.getName().equalsIgnoreCase("num_mes")) {
                        if (campoBI.getDateMask().equalsIgnoreCase(MascaraMes.ABREVIADO)
                                || campoBI.getDateMask().equalsIgnoreCase(MascaraMes.NAO_ABREVIADO)) {
                            campo.addMascara(new MascaraColunaMetaData(campoBI.getDateMask(), MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_MES));
                        }
                    }
                    if (campoBI.getName().equalsIgnoreCase("num_dia_semana")) {
                        if (campoBI.getDateMask().equalsIgnoreCase(MascaraSemana.ABREVIADO)
                                || campoBI.getDateMask().equalsIgnoreCase(MascaraSemana.NAO_ABREVIADO)) {
                            campo.addMascara(new MascaraColunaMetaData(campoBI.getDateMask(), MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_SEMANA));
                        }
                    }
                    if (campoBI.getName().equalsIgnoreCase("num_bimestre") || campoBI.getName().equalsIgnoreCase("num_trimestre")
                            || campoBI.getName().equalsIgnoreCase("num_semestre")) {
                        if (Constants.NUMBER.equals(campoBI.getDataType()) && (MascaraPeriodo.validaMascara(campoBI.getDateMask()))) {
                            campo.addMascara(new MascaraColunaMetaData(campoBI.getDateMask(), MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_PERIODO));
                        }
                    }
                    if (campoBI.getName().equalsIgnoreCase("ano_mes_dat")) {
                        if (Constants.STRING.equals(campoBI.getDataType()) && (MascaraMesAno.validaMascara(campoBI.getDateMask()))) {
                            campo.addMascara(new MascaraColunaMetaData(campoBI.getDateMask(), MascaraColunaMetaData.TIPO_EIS_DIMENSAO_DAT_ANO_MES));
                        }
                    }
                }
            }
        } else if (Constants.DATE.equals(campoBI.getDataType())) {
            campo.addMascara(new MascaraColunaMetaData(BIUtil.DEFAULT_DATE_FORMAT, MascaraColunaMetaData.TIPO_DATA));
        }

        campo.setSentidoOrdem(campoBI.getOrderDirection().trim().toUpperCase());
        campo.setSentidoOrdemAcumulado(campoBI.getAccumulatedOrderDirection() != null ? campoBI.getAccumulatedOrderDirection().trim().toUpperCase() : "ASC");
        if (campo.isExpressao()) {
            if (campoBI.isApplyTotalizationExpression() || campoBI.isPartialExpression()) {
                campo.setTipoTotalizacaoLinhas(CampoMetaData.TOTALIZAR_APLICAR_EXPRESSAO);
            } else {
                campo.setTipoTotalizacaoLinhas(CampoMetaData.TOTALIZAR_APLICAR_SOMA);
            }
            if (campoBI.getName().toUpperCase().trim().indexOf("SE(") == 0 || campoBI.getName().toUpperCase().trim().indexOf("IF(") == 0) {
                campo.setAgregacaoAplicarOrdem(CampoMetaData.AGREGACAO_APLICAR_DEPOIS);
            }
        }
        campo.setTituloCampo(campoBI.getTitle());
        campo.setTotalizaCampo(campoBI.isTotalizingField());
        campo.setTotalizaCampoLinha(campoBI.isSumLine());
        campo.setTotalizacaoParcial(campoBI.isPartialTotalization() && campoVisualizado);
        campo.setMediaParcial(campoBI.isPartialMedia() && campoVisualizado);
        campo.setExpressaoNaParcial(campoBI.isPartialExpression() && campoVisualizado);
        campo.setExpressaoNaParcialTotal(campoBI.isPartialTotalExpression() && campoVisualizado);
        campo.setUtilizaMediaLinha(campoBI.isMediaLine());
        campo.setValorAcumulado(campoBI.isAccumulatedValue());
        String tipoDado = campoBI.getDataType();

        // TODO Rever este conceito
        if (Constants.DIMENSION.equals(campoBI.getFieldType())) {
            if (Constants.NUMBER.equals(campoBI.getDataType())) {
                tipoDado = CampoMetaData.TIPO_DECIMAL;
            }
        }
        campo.setTipoDado(tipoDado);
        LinkHTMLTexto linkTexto = new LinkHTMLTextoColuna(campo.getTituloCampo(), campo.getLarguraColuna());
        linkTexto.addParametro("data-code-col", String.valueOf(campoBI.getCode()));
        MascaraLinkHTMLMetaData mascaraLinkHTML = new MascaraLinkHTMLMetaData("manutField", MascaraLinkHTMLMetaData.TIPO_VALOR, linkTexto);
        campo.addMascaraLinkHTML(mascaraLinkHTML);
        return campo;
    }

    public boolean verificaFiltros(DimensionFilter fd, Field campo) {
        boolean retorno = false;
        if (fd.getCondition() == null) {
            for (DimensionFilter filter : fd.getFilters()) {
                if (filter == null)
                    return false;

                if (filter.getCondition() == null) {
                    retorno = verificaFiltros(filter, campo);
                } else {
                    if (filter.getField().getNickName().equalsIgnoreCase(campo.getNickName()) && "=".equalsIgnoreCase(filter.getOperator().getSymbol())) {
                        retorno = true;
                    }
                }
            }
        } else {
            if (fd.getField().getNickName().equalsIgnoreCase(campo.getNickName()) && "=".equalsIgnoreCase(fd.getOperator().getSymbol())) {
                retorno = true;
            }
        }
        return retorno;
    }

    private void createMascarasHTMLDimensaoLinha(Field dimensaoBI, CampoMetaData dimensaoCubo) {
        if (dimensaoBI.isNavigable()) {
            LinkHTMLSVGColuna svgHTML = new LinkHTMLSVGColuna("mais", "btMais vect-plus-sign", "Drill Down", 14, 14);
            svgHTML.addParametro("data-code-col", String.valueOf(dimensaoBI.getCode()));
            MascaraLinkHTMLMetaData mascaraLinkHTML = new MascaraLinkHTMLMetaData("drilldown", MascaraLinkHTMLMetaData.TIPO_DEPOIS_VALOR, svgHTML);
            dimensaoCubo.addMascaraLinkHTML(mascaraLinkHTML);
        } else if (dimensaoBI.isNavigableUpwards()) {
            LinkHTMLSVGColuna svgHTML = new LinkHTMLSVGColuna("menos", "btMenos vect-minus-sign", "Drill Up", 14, 14);
            svgHTML.addParametro("data-code-col", String.valueOf(dimensaoBI.getCode()));
            MascaraLinkHTMLMetaData mascaraLinkHTML = new MascaraLinkHTMLMetaData("drilldown", MascaraLinkHTMLMetaData.TIPO_DEPOIS_VALOR, svgHTML);
            dimensaoCubo.addMascaraLinkHTML(mascaraLinkHTML);
        }
        LinkHTMLSVGColuna svgHTML;
        String idImagem = dimensaoBI.getNickName() + dimensaoBI.getCode();
        if (this.getFilters().getDimensionFilter() != null && this.verificaFiltros(this.getFilters().getDimensionFilter(), dimensaoBI)) {
            svgHTML = new LinkHTMLSVGColuna(idImagem, "btFiltro vect-filter-yellow", "Filtrar Dimens�o", 18, 18);
        } else {
            svgHTML = new LinkHTMLSVGColuna(idImagem, "btFiltro vect-filter-silver", "Filtrar Dimens�o", 18, 18);
        }
        svgHTML.addParametro("data-code-col", String.valueOf(dimensaoBI.getCode()));
        MascaraLinkHTMLMetaData mascaraLinkHTML = new MascaraLinkHTMLMetaData("filtro", MascaraLinkHTMLMetaData.TIPO_DEPOIS_VALOR, svgHTML);
        dimensaoCubo.addMascaraLinkHTML(mascaraLinkHTML);

        if (dimensaoBI.isNavigableUpwards()) {
            this.createMascaraHTMLDrillDownDimensao(dimensaoBI, dimensaoCubo);
        }
    }

    private void createMascaraHTMLDrillDownDimensao(Field dimensaoBI, CampoMetaData dimensaoCubo) {
        LinkHTMLTextoDinamico linkTextoDinamico = new LinkHTMLTextoDinamico("data-dimension-value");
        linkTextoDinamico.addParametro("data-code-col", String.valueOf(dimensaoBI.getCode()));
        linkTextoDinamico.addParametro("data-code-indicador", String.valueOf(this.getCode()));
        MascaraLinkHTMLMetaData mascaraLinkHTML = new MascaraLinkHTMLMetaData("drilldownFiltro", MascaraLinkHTMLMetaData.TIPO_DINAMICO, linkTextoDinamico);
        dimensaoCubo.addMascaraLinkHTML(mascaraLinkHTML);
    }

    private CuboMetaData createCuboMetaData() throws BIException, DateException {
        BICubeMapedFields = new HashMap<>();
        CuboMetaData cuboMetaData = new CuboMetaData();
        CampoMetaData campo;
        int sequenciaDrillDown = Integer.MAX_VALUE;
        int sequencia = Integer.MAX_VALUE;
        CampoMetaData primeiraDimensaoLinhaDrillDown = null;
        CampoMetaData primeiraDimensaoLinhaSequencia = null;
        this.carregaFieldsRestritosMulti();
        int verificaAtividade = 0;
        this.verificaNecessidadeTotalizacaoParcial();
        for (Field campoBI : this.fields) {
            verificaAtividade++;
            if (verificaAtividade % 100 == 0) {
                ;
                if (this.stopProcess())
                    return null;
            }
            if (campoBI != null && !(campoBI.getTitle().equalsIgnoreCase("Não visualizado") && campoBI.isFixedValue())) {
                campo = this.createFieldCuboMetaData(campoBI);
                String tipoField = campoBI.getFieldType();
                if (Constants.DIMENSION.equals(tipoField)) {
                    campo.setSequencia(campoBI.getDrillDownSequence());
                    if (campoBI.getDisplayLocation() == Constants.COLUMN && "S".equals(campoBI.getDefaultField())) {
                        if (campoBI.getDrillDownSequence() < sequenciaDrillDown) {
                            primeiraDimensaoLinhaDrillDown = campo;
                            sequenciaDrillDown = campoBI.getDrillDownSequence();
                        }
                        if (campoBI.getVisualizationSequence() < sequencia) {
                            sequencia = campoBI.getVisualizationSequence();
                        }
                        this.createMascarasHTMLDimensaoLinha(campoBI, campo);
                    }
                } else {
                    campo.setSequencia(campoBI.getVisualizationSequence());
                }
                cuboMetaData.addCampo(campo, tipoField);
                BICubeMapedFields.put(campoBI, campo);
            }
        }
        if ("S".equals(this.usesSequence)) {
            primeiraDimensaoLinhaDrillDown.setMostraSequencia(true);
            FilterSequence filtroSequencia = this.getFiltersFunction().getFilterSequence();
            if (filtroSequencia != null) {
                primeiraDimensaoLinhaDrillDown.setExpressaoRanking(filtroSequencia.toString());
            }
        }
        List<ColorAlert> alertasBI = this.colorAlerts.getColorAlertList();
        for (ColorAlert alertaBI : alertasBI) {
            CampoMetaData campoCubo = BICubeMapedFields.get(alertaBI.getFirstField());

            if (campoCubo == null) {
                continue;
            }
            AlertaCorMetaData alertaCubo;
            int acao = ColorAlert.LINHA.equals(alertaBI.getAction()) ? AlertaCorMetaData.ACAO_PINTAR_LINHA : AlertaCorMetaData.ACAO_PINTAR_CELULA;
            AlertProperty props = alertaBI.getAlertProperty();
            String corFundo = props.getCellBackgroundColor().startsWith("#") ? props.getCellBackgroundColor().substring(1) : props.getCellBackgroundColor();
            String corFonte = props.getFontColor().startsWith("#") ? props.getFontColor().substring(1) : props.getFontColor();
            if (!alertaBI.isCompareToAnotherField()) {
                alertaCubo = this.createAlertaCuboMetaData(alertaBI, acao, corFonte, corFundo);
                campoCubo.addAlertaCor(alertaCubo, AlertaCorMetaData.TIPO_ALERTA_VALOR);
            } else {
                String valor = alertaBI.getFirstDoubleValue();

                if ("".equals(valor)) {
                    valor = "0.00";
                }
                alertaCubo = new AlertaCorMetaData(alertaBI.getSequence(), alertaBI.getOperator().getSymbol(), Double.parseDouble(valor), acao,
                        alertaBI.getFirstFieldFunction(), corFonte, corFundo, props.getFontName(), props.hasBold(), props.hasItalic(),
                        props.getFontSize(), alertaBI.getValueType(), alertaBI.getSecondField().getTitle(), alertaBI.getSecondFieldFunction());
                campoCubo.addAlertaCor(alertaCubo, AlertaCorMetaData.TIPO_ALERTA_OUTRO_CAMPO);
            }
        }
        this.createFiltroseRestricoesMetricasCuboMetaData(cuboMetaData);
        return cuboMetaData;
    }

    private void verificaNecessidadeTotalizacaoParcial() {
        boolean mediaParcial = false;
        boolean totalizacaoParcial = false;
        boolean expressao = false;
        boolean expressaoNoTotal = false;
        for (Field campo : this.getFields(Constants.METRIC)) {
            if (campo != null && !"N".equalsIgnoreCase(campo.getDefaultField())) {
                if (campo.isPartialExpression()) {
                    expressao = true;
                }
                if (campo.isPartialTotalExpression()) {
                    expressaoNoTotal = true;
                }
                if (campo.isPartialTotalization()) {
                    totalizacaoParcial = true;
                }
                if (campo.isPartialMedia()) {
                    mediaParcial = true;
                }
            }
        }

        for (Field campo : this.getFields(Constants.DIMENSION)) {
            if (campo != null) {
                if (!mediaParcial) {
                    campo.setPartialMedia(false);
                }
                if (!totalizacaoParcial) {
                    campo.setPartialTotalization(false);
                }
                if (!expressao) {
                    campo.setPartialExpression(false);
                }
                if (!expressaoNoTotal) {
                    campo.setPartialTotalExpression(false);
                }
            }
        }
    }

    private CuboMetaData createCuboMetaDataFormatoPadrao() throws BIException {
        CuboMetaData cuboMetaData = new CuboMetaData();
        CampoMetaData campo;
        int sequencia = Integer.MAX_VALUE;
        CampoMetaData primeiraColuna = null;
        Map<Integer, CampoMetaData> camposCubo = new HashMap<>();
        int sequenciaAlerta = 1;
        this.carregaFieldsRestritosPadrao();
        int ultimaSequenciaDrillDown = this.getMaxSequenciaDrillDown();
        for (Field campoBI : this.fields) {
            if (campoBI != null && !(campoBI.getTitle().equalsIgnoreCase("N�o visualizado") && campoBI.isFixedValue())) {
                campo = this.createFieldCuboMetaData(campoBI);
                campo.setSequencia(campoBI.getVisualizationSequence());
                String tipoField = campoBI.getFieldType();
                if ("S".equals(campoBI.getDefaultField()) && campoBI.getVisualizationSequence() < sequencia) {
                    primeiraColuna = campo;
                    sequencia = campoBI.getVisualizationSequence();
                }
                LinkHTMLSVGColuna svgHTML = new LinkHTMLSVGColuna("desc", "btOrdena vect-sort-down", "Ordenar de forma decrescente", 14, 14);
                svgHTML.addParametro("data-code-col", String.valueOf(campoBI.getCode()));
                svgHTML.addParametro("data-order", "DESC");
                MascaraLinkHTMLMetaData mascaraLinkHTML = new MascaraLinkHTMLMetaData("ordenacao", MascaraLinkHTMLMetaData.TIPO_DEPOIS_VALOR, svgHTML);
                campo.addMascaraLinkHTML(mascaraLinkHTML);

                svgHTML = new LinkHTMLSVGColuna("asc", "btOrdena vect-sort-up", "Ordenar de forma crescente", 14, 14);
                svgHTML.addParametro("data-code-col", String.valueOf(campoBI.getCode()));
                svgHTML.addParametro("data-order", "ASC");
                mascaraLinkHTML = new MascaraLinkHTMLMetaData("ordenacao", MascaraLinkHTMLMetaData.TIPO_DEPOIS_VALOR, svgHTML);
                campo.addMascaraLinkHTML(mascaraLinkHTML);
                if (campoBI.isDrillDown() && !(campoBI.getDrillDownSequence() == ultimaSequenciaDrillDown)) {
                    this.createMascaraHTMLDrillDownDimensao(campoBI, campo);
                }

                for (LineColor corLinha : campoBI.getLineColors()) {
                    if (corLinha != null) {
                        String corFundo = corLinha.getBackGroundColor().startsWith("#") ? corLinha.getBackGroundColor().substring(1) : corLinha.getBackGroundColor();
                        String corFonte = corLinha.getFontColor().startsWith("#") ? corLinha.getFontColor().substring(1) : corLinha.getFontColor();
                        AlertaCorMetaData alertaCor = new AlertaCorMetaData(sequenciaAlerta++, OperadoresLogicos.ENTRE_INCLUSIVO,
                                Double.parseDouble(corLinha.getInitialValue()), Double.parseDouble(corLinha.getInitialValue()), corFonte, corFundo,
                                "Verdana", false, false, 10);
                        campo.addAlertaCor(alertaCor, AlertaCorMetaData.TIPO_ALERTA_VALOR);
                    }
                }
                cuboMetaData.addCampo(campo, tipoField);
                camposCubo.put(campoBI.getCode(), campo);
            }
        }
        if ("S".equals(this.usesSequence)) {
            primeiraColuna.setMostraSequencia(true);
            FilterSequence filtroSequencia = this.getFiltersFunction().getFilterSequence();
            if (filtroSequencia != null) {
                primeiraColuna.setExpressaoRanking(filtroSequencia.toString());
            }
        }
        this.createFiltroseRestricoesMetricasCuboMetaData(cuboMetaData);
        return cuboMetaData;
    }

    private AlertaCorMetaData createAlertaCuboMetaData(ColorAlert alertaBI, int acao, String corFonte, String corFundo) throws BIException, DateException {
        Field campo = alertaBI.getFirstField();
        AlertProperty props = alertaBI.getAlertProperty();

        if (campo == null || campo.getFieldType().equals(Constants.METRIC)) {
            double primeiroValor = Double.parseDouble(alertaBI.getFirstDoubleValue());
            double segundoValor = 0;
            if (alertaBI.getSecondValue() != null) {
                segundoValor = Double.parseDouble(alertaBI.getSegundoValorDouble());
            }
            return new AlertaCorMetaData(alertaBI.getSequence(), alertaBI.getOperator().getSymbol(), primeiroValor, segundoValor, acao,
                    alertaBI.getFirstFieldFunction(), corFonte, corFundo, props.getFontName(), props.hasBold(), props.hasItalic(),
                    props.getFontSize());
        } else {
            if (!campo.getDataType().equals(Constants.DATE)) {
                String valorComp = alertaBI.getFirstValue();
                if (valorComp.trim().startsWith("@|") && valorComp.trim().endsWith("|")) {
                    DimensionFilter filtroDimensao = FilterFactory.createDimensionFilter(campo, alertaBI.getOperator().getSymbol(), valorComp);
                    Condition condicao = filtroDimensao.getCondition();
                    valorComp = condicao.getValue();
                }
                Object valor1 = valorComp;
                if (campo.getDataType().equals(Constants.NUMBER)) {
                    valor1 = Integer.parseInt(valorComp);
                }
                return new AlertaCorMetaData(alertaBI.getSequence(), alertaBI.getOperator().getSymbol(), valor1, null, acao,
                        alertaBI.getFirstFieldFunction(), corFonte, corFundo, props.getFontName(), props.hasBold(), props.hasItalic(),
                        props.getFontSize());
            } else {
                String dataString = alertaBI.getFirstValue();
                String dataString2 = alertaBI.getSecondValue();
                if (dataString.trim().startsWith("@|") && dataString.trim().endsWith("|")) {
                    DimensionFilter filtroDimensao = FilterFactory.createDimensionFilter(campo, alertaBI.getOperator().getSymbol(), dataString);
                    if (filtroDimensao.getFilters() != null && !filtroDimensao.getFilters().isEmpty()) {

                        Date primeiroValor = null;
                        Date segundoValor = null;
                        for (DimensionFilter filtro : filtroDimensao.getFilters()) {
                            if (filtro != null && filtro.getCondition() != null) {
                                Condition condicao = new TextCondition(filtro.getCondition());
                                Date dataComparacao = BIUtil.formatSQLDate(condicao.getFormattedValue(), BIData.FORMATO_DIA_MES_ANO_TELA);
                                if (condicao.getOperator().getSymbol().equals(Operators.GREATER_TAN_OR_EQUAL)) {
                                    primeiroValor = dataComparacao;
                                } else {
                                    segundoValor = dataComparacao;
                                }
                            }
                        }

                        return new AlertaCorMetaData(alertaBI.getSequence(), OperadoresLogicos.ENTRE_INCLUSIVO, primeiroValor, segundoValor, acao,
                                alertaBI.getFirstFieldFunction(), corFonte, corFundo, props.getFontName(), props.hasBold(), props.hasItalic(),
                                props.getFontSize());
                    } else {
                        Condition condicao = new TextCondition(filtroDimensao.getCondition());
                        BIData dataComparacao = new BIData(condicao.getFormattedValue(), BIUtil.DEFAULT_DATE_FORMAT); // TODO Load from configuration
                        return new AlertaCorMetaData(alertaBI.getSequence(), alertaBI.getOperator().getSymbol(), dataComparacao.getSqlDate(), null,
                                acao, alertaBI.getFirstFieldFunction(), corFonte, corFundo, props.getFontName(), props.hasBold(),
                                props.hasItalic(), props.getFontSize());
                    }
                } else {
                    Date primeiroValor = BIUtil.formatSQLDate(dataString, BIUtil.DEFAULT_DATE_FORMAT);
                    Date segundoValor = null;
                    if (alertaBI.getOperator().getSymbol().equals(Operators.BETWEEN)) {
                        segundoValor = BIUtil.formatSQLDate(dataString2, BIUtil.DEFAULT_DATE_FORMAT);
                    }
                    return new AlertaCorMetaData(alertaBI.getSequence(), alertaBI.getOperator().getSymbol(), primeiroValor, segundoValor, acao,
                            alertaBI.getFirstFieldFunction(), corFonte, corFundo, props.getFontName(), props.hasBold(), props.hasItalic(),
                            props.getFontSize());
                }
            }
        }
    }


    public ResultSet carregaRegistros() throws Exception {
        if (!this.frozenStatus && !this.temporaryFrozenStatus) {
            boolean isCached = false; // TODO load from db config
            CachedRowSet cachedrowset;
            ResultSet results;
            PreparedStatement stmtResults;
            ConnectionBean conexao = this.getNovaConexaoIndicator();


            String sql = this.getExpressaoSQLIndicator();

            try {

                stmtResults = conexao.prepareStatement(sql);

                String console = this.applyValues(stmtResults, sql);
                this.printSQL(console);

                results = stmtResults.executeQuery();
            } catch (SQLException sqle) {
                BISQLException biex = new BISQLException(sqle, "Erro ocorrido no indicador " + this.originalCode + "\n" + sql);
                biex.setAction("buscar indicadores");
                biex.setLocal(getClass(), "geraCachedRowSet(String)");
                throw biex;
            }
            try {
                if (isCached) {
                    cachedrowset =  RowSetProvider.newFactory().createCachedRowSet();
                    cachedrowset.populate(results);
                    results = cachedrowset;
                }
            } catch (SQLException sqle) {
                BIDatabaseException biex = new BIDatabaseException(sqle);
                biex.setAction("obter objeto CachedRowSet.");
                biex.setLocal(getClass(), "geraCachedRowSet(String)");
                throw biex;
            }
            return results;
        } else {
            try {
                CachedRowSet registros = BIObject.getIndicator(this.originalCode);
                if (registros != null) {
                    this.printSQL("Carregando análise congelada: bi_ind_" + this.originalCode + ".dat");
                    registros.beforeFirst();
                    return registros;
                } else {
                    this.frozenStatus = false;
                    this.temporaryFrozenStatus = false;
                    return this.carregaRegistros();
                }
            } catch (SQLException e) {
                throw new BISQLException(e, "Erro carregar os registros para a análise.");
            }
        }
    }

    private void printSQL(String console) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String print = "(" + sdf.format(date) + ") " + this.getOriginalCode() + " - " + this.getName() + "\nSQL Cubo: " + console + "\n";
        System.out.println("\n" + print.trim() + "\n");
    }

    public String applyValues(PreparedStatement stmtResults, String sql) throws BIException, SQLException {
        int posicao = 1;
        if (stmtResults != null) {
            posicao = this.filters.applyValuesInDimension(stmtResults, posicao);
            posicao = this.filters.applyMetricFilterValuesInMetric(stmtResults, posicao);

            if (this.restrictions != null) {
                posicao = this.restrictions.aplicaValores(stmtResults, posicao);
            }
            this.filters.applyValuesInMetric(stmtResults, posicao);
        }
        if (sql != null) {
            sql = (String) (new DimensionTextFilter(this.filters.getDimensionFilter())).applyValues(sql, 0);
            sql = this.aplicaValoresMetricaSQL(sql);

            if (this.restrictions != null) {
                sql = this.restrictions.aplicaValorNaSQL(sql);
            }
            sql = this.aplicaValoreMetrica(sql);
        }
        return sql;
    }

    private String aplicaValoreMetrica(String sql) throws BIException {
        if (this.filters != null && this.filters.getMetricFilters() != null) {
            for (MetricFilter filtro : this.filters.getMetricFilters()) {
                MetricTextFilter filtroMetricaTexto = new MetricTextFilter(filtro);
                sql = (String) filtroMetricaTexto.applyValues(sql, 0);
            }
        }
        return sql;
    }

    private String aplicaValoresMetricaSQL(String sql) throws BIException {
        if (this.filters != null && this.filters.getMetricSqlFilter() != null) {
            for (MetricFilter filtro : this.filters.getMetricSqlFilter()) {
                MetricTextFilter filtroMetricaTexto = new MetricTextFilter(filtro);
                sql = (String) filtroMetricaTexto.applyValues(sql, 0);
            }
        }
        return sql;
    }

    private String getNovoNomeArquivo() {
        Random random = new Random();
        StringBuilder s = new StringBuilder();
        for (int i = 1; i <= 4; i++) {
            s.append((int) (1.0D + (double) 9 * random.nextDouble()));
        }
        return this.name.replaceAll("[^.A-Za-zÀ-ÿ ]", " ").trim().replaceAll(" +", "_") + "(" + s + ").xls";
    }

    public void montaSaida(boolean consultar) throws BIException, Exception {
        if (consultar) {
            this.cuboListener.start();
            ResultSet cachedRowSet = null;
            try {
                this.cuboListener.start();
                cachedRowSet = this.carregaRegistros();
                this.montaTabela(cachedRowSet);
                if (this.stopProcess())
                    return;

                System.gc();

                this.cuboListener.finish();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            } finally {
                if (!this.frozenStatus && !this.temporaryFrozenStatus) {
                    BIUtil.closeResultSet(cachedRowSet);
                }
            }
            this.cuboListener.finish();
        }
    }

    public CachedResults calculaExpressoes(CachedResults registros_tab) throws BIException {
        boolean temExpressao = false;
        int quant_Fields = registros_tab.getConsultResults().length;
        int quant_Expressoes = 0;
        List<Field> camposExpressao;
        Field campo_aux;
        for (int x = 0; x < quant_Fields; x++) {
            campo_aux = registros_tab.getConsultResult(x).getField();
            if (campo_aux != null && campo_aux.isExpression()) {
                temExpressao = true;
                quant_Expressoes++;
            }
        }

        if (temExpressao) {
            int indice_aux = 0;
            camposExpressao = new ArrayList<>();
            for (int x = 0; x < quant_Fields; x++) {
                campo_aux = registros_tab.getConsultResult(x).getField();
                if (campo_aux != null && campo_aux.isExpression()) {
                    camposExpressao.set(indice_aux, campo_aux);
                    indice_aux++;
                }
            }
            registros_tab.beforeFirst();
            ConditionalExpression expressaoCondicional_temp;
            while (registros_tab.next()) {
                for (Field field : camposExpressao) {
                    if (field.getName().toUpperCase().trim().indexOf("SE(") == 0
                            || field.getName().toUpperCase().trim().indexOf("IF(") == 0) {
                        expressaoCondicional_temp = this.getExpressaoCondicional(field.getName());
                        registros_tab.setDouble(this.calculaExpressaoCondicional(expressaoCondicional_temp, registros_tab),
                                field.getCode());
                    } else {
                        String exp = field.getName();
                        Expression expAux = new Expression(this.convertePartesExpressaoDeStringParaArray(exp));
                        double valor = expAux.calculaExpressao(registros_tab);
                        registros_tab.setDouble(valor, field.getCode());
                    }
                }
            }
            registros_tab.beforeFirst();
        }
        return registros_tab;
    }

    public double calculaExpressaoCondicional(ConditionalExpression expressao, CachedResults registros_tabela) throws BIException { // O indicie do CachedResultados j� est� setado no m�todo que chama ele.
        boolean condicaoValida = false;
        double resultadoFinal = 0;
        double primeiroAtributo = 0;
        double segundoAtributo = 0;
        Operator operadorCondicionalEncontrado = null;
        ExpressionCondition expParteCondicional;
        Expression expParteTrue;
        Expression expParteFalse;
        if (expressao != null) {
            expParteCondicional = expressao.getExpConditionalPart();
            expParteTrue = expressao.getExpPartTrue();
            expParteFalse = expressao.getExpPartFalse();
            Expression expAux = new Expression();
            primeiroAtributo = expAux.calculaExpressao(expParteCondicional.getFirstAttribute(), registros_tabela);
            segundoAtributo = expAux.calculaExpressao(expParteCondicional.getSecondAttribute(), registros_tabela);
            operadorCondicionalEncontrado = expParteCondicional.getOperator();

            if (operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase(">")) {
                condicaoValida = (primeiroAtributo > segundoAtributo);
            } else if (operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase(">=")) {
                condicaoValida = (primeiroAtributo >= segundoAtributo);
            } else if (operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase("<")) {
                condicaoValida = (primeiroAtributo < segundoAtributo);
            } else if (operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase("<=")) {
                condicaoValida = (primeiroAtributo <= segundoAtributo);
            } else if (operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase("=")
                    || operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase("==")) {
                condicaoValida = (primeiroAtributo == segundoAtributo);
            } else if (operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase("<>")
                    || operadorCondicionalEncontrado.getSymbol().equalsIgnoreCase("!=")) {
                condicaoValida = (primeiroAtributo != segundoAtributo);
            }

            if (condicaoValida) {
                resultadoFinal = expAux.calculaExpressao(expParteTrue, registros_tabela);
            } else {
                resultadoFinal = expAux.calculaExpressao(expParteFalse, registros_tabela);
            }
        }
        return resultadoFinal;
    }

    public CachedResults ordenaRegistros(CachedResults registros_tab) throws BIException {
        int codigoDimensaoUltimoNivel = this.getLastLevelDimensionCode();
        ArrayList<Object> camposDimensaoLinha = new ArrayList<>();
        Field campoDimensaoUltimoNivel = null;
        int posUltimoNivel = -1;
        int[] posicoesDimensaoLinha = null;
        List<Object> valores;
        ArrayList<Object[]> valoresDimensaoLinha = new ArrayList<>();
        Hashtable<Object, ArrayList<ArrayList<Object>>> valoresDimensaoUltimoNivel = new Hashtable<>();

        if (registros_tab.getConsultResults().length == 0) {
            return registros_tab;
        }
        ArrayList<String> array_codigo_campo = new ArrayList<>();
        ArrayList<String> array_sentido_ordem = new ArrayList<>();

        for (int cont = 0; cont < this.fields.size(); cont++) {
            for (int i = 0; i < this.fields.size(); i++) {
                if (this.fields.get(i) != null && this.fields.get(i).getOrder() == cont + 1 && this.fields.get(i).getDefaultField().equalsIgnoreCase("S")) {
                    array_codigo_campo.add(String.valueOf(this.fields.get(i).getCode()));
                    array_sentido_ordem.add(this.fields.get(i).getOrderDirection());
                }
            }
            if (this.fields.get(cont) != null) {
                if (this.fields.get(cont).getDisplayLocation() == Constants.LINE && this.fields.get(cont).getDefaultField().equals("S")
                        && this.fields.get(cont).getFieldType().equals(Constants.DIMENSION)) {
                    camposDimensaoLinha.add(fields.get(cont));
                }
                if (campoDimensaoUltimoNivel == null && fields.get(cont).getCode() == codigoDimensaoUltimoNivel) {
                    campoDimensaoUltimoNivel = fields.get(cont);
                }
            }
        }
        List<ConsultRegister> listaRegistrosAux = new ArrayList<>();
        registros_tab.beforeFirst();
        int indiceDimensaoLinha = 0;
        if (!camposDimensaoLinha.isEmpty()) {
            posicoesDimensaoLinha = new int[camposDimensaoLinha.size()];
            for (int x = 0; x < registros_tab.getConsultResults().length; x++) {
                ConsultResult resultadoConsulta = registros_tab.getConsultResult(x);
                if (camposDimensaoLinha.contains(resultadoConsulta.getField())) {
                    posicoesDimensaoLinha[indiceDimensaoLinha] = x;
                    indiceDimensaoLinha++;
                } else if (resultadoConsulta.getField().equals(campoDimensaoUltimoNivel)) {
                    posUltimoNivel = x;
                    valores = resultadoConsulta.getValues();
                    for (Object valore : valores) {
                        if (!valoresDimensaoUltimoNivel.containsKey(valore)) {
                            valoresDimensaoUltimoNivel.put(valore, new ArrayList<>());
                        }
                    }
                }
            }
        }
        registros_tab.beforeFirst();
        while (registros_tab.next()) {
            ConsultRegister registroConsulta = registros_tab.getRegistroAtual();
            listaRegistrosAux.add(registroConsulta);
            if (posicoesDimensaoLinha != null && posUltimoNivel != -1) {
                ArrayList<Object> valoresRegistro = registroConsulta.getValues();
                Object valor = valoresRegistro.get(posUltimoNivel);
                valoresDimensaoUltimoNivel.get(valor).add(valoresRegistro);
                Object[] valorLinhas = new Object[posicoesDimensaoLinha.length];
                for (int x = 0; x < posicoesDimensaoLinha.length; x++) {
                    int posDimensaoLinha = posicoesDimensaoLinha[x];
                    valorLinhas[x] = valoresRegistro.get(posDimensaoLinha);
                }
                boolean jaTemValor = false;
                for (Object[] objects : valoresDimensaoLinha) {
                    if (Arrays.deepEquals(objects, valorLinhas)) {
                        jaTemValor = true;
                        break;
                    }
                }
                if (!jaTemValor)
                    valoresDimensaoLinha.add(valorLinhas);
            }
        }
        if (!array_codigo_campo.isEmpty() && !listaRegistrosAux.isEmpty()) {
            RegistroComparator comparador = new RegistroComparator(array_codigo_campo, array_sentido_ordem);

            if (posicoesDimensaoLinha != null && posUltimoNivel != -1) {
                listaRegistrosAux = this.getListaTotalRegistros(registros_tab, listaRegistrosAux, valoresDimensaoLinha, valoresDimensaoUltimoNivel,
                        posicoesDimensaoLinha, posUltimoNivel);
            }
            Object[] registros2 = listaRegistrosAux.toArray();
            Arrays.sort(registros2, comparador);
            ArrayList<Field> array_campos = null;

            for (Object o : registros2) {
                if (o != null) {
                    array_campos = ((ConsultRegister) o).getFields();
                    break;
                }
            }

            if (array_campos != null) {
                ConsultResult[] resultadosConsulta = new ConsultResult[array_campos.size()];
                ConsultResult resultadosConsultaAux;
                Field campoAux;
                int codigo_campoAux;
                int c, r;
                for (c = 0; c < array_campos.size(); c++) {
                    campoAux = array_campos.get(c);
                    resultadosConsultaAux = ConsultResultFactory.factory(campoAux);
                    codigo_campoAux = campoAux.getCode();
                    resultadosConsultaAux.setField(campoAux);
                    for (r = 0; r < registros2.length; r++) {
                        resultadosConsultaAux.addValor(((ConsultRegister) registros2[r]).getValor(codigo_campoAux));
                    }
                    resultadosConsulta[c] = resultadosConsultaAux;
                }
                return new CachedResults(resultadosConsulta);
            }
            return registros_tab;
        }
        return registros_tab;
    }

    public List<ConsultRegister> getListaTotalRegistros(CachedResults registrosTabela, List<ConsultRegister> registros,
                                                        ArrayList<Object[]> valoresDimensaoLinhaSemRepeticao, Hashtable<Object, ArrayList<ArrayList<Object>>> valoresDimensaoUltimoNivel,
                                                        int[] posicoesDimensaoLinha, int posicaoUltimoNivel) {
        Enumeration<Object> enumDimensao = valoresDimensaoUltimoNivel.keys();
        while (enumDimensao.hasMoreElements()) {
            Object dimensao = enumDimensao.nextElement();
            ArrayList<ArrayList<Object>> listasValores = valoresDimensaoUltimoNivel.get(dimensao);
            ArrayList<Integer> posicoesComValor = this.getDimensoesComValor(listasValores, posicoesDimensaoLinha, valoresDimensaoLinhaSemRepeticao,
                    null, null);
            if (!posicoesComValor.isEmpty()) {
                for (int x = 0; x < valoresDimensaoLinhaSemRepeticao.size(); x++) {
                    if (!posicoesComValor.contains(x)) {
                        ArrayList<Object> novosValores = this.criaNovaLista(listasValores.get(0), registros.get(0).getFields(), posicoesDimensaoLinha,
                                posicaoUltimoNivel, valoresDimensaoLinhaSemRepeticao.get(x), dimensao);
                        ConsultRegister registroConsulta = new ConsultRegister();
                        registroConsulta.setFieldList(registros.get(0).getFields());
                        registroConsulta.setValueList(novosValores);
                        registros.add(registroConsulta);
                    }
                }
            }
        }
        return registros;
    }

    private ArrayList<Integer> getDimensoesComValor(ArrayList<ArrayList<Object>> listasValores, int[] indicesDimensaoLinha,
                                                    ArrayList<Object[]> valoresDimensaoLinhaSemRepeticao, ArrayList<?> listaPadrao, ArrayList<Field> campos) {
        ArrayList<Integer> indicesValoresAchados = new ArrayList<>();
        if (listasValores.size() != valoresDimensaoLinhaSemRepeticao.size()) {
            Object[] valorLinhas;
            for (List<Object> valores : listasValores) {
                for (int i = 0; i < valoresDimensaoLinhaSemRepeticao.size(); i++) {
                    int qdeValidadas = 0;
                    Integer indiceAtual = i;
                    valorLinhas = valoresDimensaoLinhaSemRepeticao.get(i);
                    for (int a = 0; a < valorLinhas.length; a++) {
                        if (valores.get(indicesDimensaoLinha[a]).equals(valorLinhas[a])) {
                            qdeValidadas++;
                        }
                    }
                    if (qdeValidadas == valorLinhas.length && !indicesValoresAchados.contains(indiceAtual)) {
                        indicesValoresAchados.add(indiceAtual);
                    }
                }
            }
        }
        return indicesValoresAchados;
    }

    private ArrayList<Object> criaNovaLista(ArrayList<Object> listaPadrao, ArrayList<Field> campos, int[] posicoesDimensaoLinha,
                                            int posicaoUltimaDimensao, Object[] valoresDimensaoLinha, Object valorUltimaDimensao) {
        ArrayList<Object> novosValores = new ArrayList<>();

        for (int x = 0; x < listaPadrao.size(); x++) {
            Object valor;
            int posicao = this.contemPosicao(posicoesDimensaoLinha, x);
            if (posicao != -1) {
                valor = valoresDimensaoLinha[posicao];
            } else if (x == posicaoUltimaDimensao) {
                valor = valorUltimaDimensao;
            } else {
                if (campos.get(x).getFieldType().equals("D") && campos.get(x).getDisplayLocation() == Constants.COLUMN) {
                    valor = listaPadrao.get(x);
                } else {
                    valor = 0D;
                }
            }
            novosValores.add(valor);
        }
        return novosValores;
    }

    private int contemPosicao(int[] vetor, int valor) {
        for (int i = 0; i < vetor.length; i++) {
            if (valor == vetor[i])
                return i;
        }
        return -1;
    }

    public CachedResults agrupaRegistros(CachedResults registros_tab) throws BIException {
        ConsultResultGrouping registros_agrupados = new ConsultResultGrouping();
        ConsultRegister registro_atual;
        registros_tab.beforeFirst();

        while (registros_tab.next()) {
            registro_atual = registros_tab.getRegistroAtual();
            if (this.conferirFiltrosMetricaSemAgregacao(registro_atual, this.getMetricFilters())) {
                registros_agrupados.addRegister(registro_atual);
            }
        }
        registros_tab.beforeFirst();
        return registros_agrupados.getCachedResultados();
    }

    public boolean conferirFiltrosMetricaSemAgregacao(ConsultRegister registro, MetricFilters filtrosMetricas) throws BIException {
        if (filtrosMetricas != null && !filtrosMetricas.isEmpty()) {
            Field campo_aux;
            int quant_filtros = filtrosMetricas.size();
            boolean confere;
            for (MetricFilter filtrosMetrica : filtrosMetricas) {
                campo_aux = filtrosMetrica.getField();
                if (campo_aux != null && campo_aux.getFieldType().equalsIgnoreCase("V")
                        && (campo_aux.getAggregationType().equals("VAZIO") || campo_aux.getAggregationType().isEmpty())
                        && (!campo_aux.getName().toUpperCase().contains("SE(") && !campo_aux.getName().toUpperCase().contains("IF("))) {

                    confere = this.confereMetricFilterSemAgregacao(registro, filtrosMetrica);
                    if (!confere) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean confereMetricFilterSemAgregacao(ConsultRegister registro, MetricFilter filtro) throws BIException {
        Object valor_temp = registro.getValor(filtro.getField().getCode());
        if (valor_temp != null) {
            double valor_registro = Double.parseDouble(String.valueOf(valor_temp));
            MetricTextFilter filtroMetricaTexto = new MetricTextFilter(filtro);
            String valor = (String) filtroMetricaTexto.applyValues(filtroMetricaTexto.getSQLValue(), 0);
            double valor_filtro = Double.parseDouble(valor);
            if (filtro.getOperator().getSymbol().equals(">")) {
                return (valor_registro > valor_filtro);
            } else if (filtro.getOperator().getSymbol().equals(">=")) {
                return (valor_registro >= valor_filtro);
            } else if (filtro.getOperator().getSymbol().equals("<")) {
                return (valor_registro < valor_filtro);
            } else if (filtro.getOperator().getSymbol().equals("<=")) {
                return (valor_registro <= valor_filtro);
            } else if (filtro.getOperator().getSymbol().equals("=")) {
                return (valor_registro == valor_filtro);
            } else if (filtro.getOperator().getSymbol().equals("<>")) {
                return (valor_registro != valor_filtro);
            }
        }
        return true;
    }

    public CachedResults aplicarFiltrosMetrica(CachedResults registros_tabela, MetricFilters filtrosMetricas) throws BIException {
        if (filtrosMetricas != null && !filtrosMetricas.isEmpty()) {
            Field campo_aux;
            int quant_filtros = filtrosMetricas.size();
            for (MetricFilter filtrosMetrica : filtrosMetricas) {
                campo_aux = filtrosMetrica.getField();
                if (campo_aux != null && campo_aux.getFieldType().equalsIgnoreCase("V")
                        && (!campo_aux.getAggregationType().equals("VAZIO")
                        || (campo_aux.getName().toUpperCase().contains("SE(") || campo_aux.getName().toUpperCase().contains("IF(")))
                        && !campo_aux.getAggregationType().isEmpty()) {
                    MetricTextFilter filtroMetrica = new MetricTextFilter(filtrosMetrica);
                    registros_tabela = this.aplicaMetricFilter(filtroMetrica, registros_tabela);
                }
            }
        }
        return registros_tabela;
    }

    public CachedResults aplicaMetricFilter(MetricFilter filtro, CachedResults registros_tabela) throws BIException {
        int quant_Fields = registros_tabela.getConsultResults().length;

        int indice_campo = -1;
        for (int x = 0; x < quant_Fields; x++) {
            if (registros_tabela.getConsultResult(x).getField().getCode() == filtro.getField().getCode()) {
                indice_campo = x;
                break;
            }
        }

        if (indice_campo >= 0) {
            double valorAux;
            boolean registroValido = false;
            registros_tabela.beforeFirst();
            while (registros_tabela.next()) {
                valorAux = registros_tabela.getDouble(registros_tabela.getConsultResult(indice_campo).getField().getCode());
                String valor = (String) filtro.applyValues(filtro.getSQLValue(), 0);
                if (filtro.getOperator().getSymbol().equals(">")) {
                    registroValido = (valorAux > Double.parseDouble(valor));
                } else if (filtro.getOperator().getSymbol().equals(">=")) {
                    registroValido = (valorAux >= Double.parseDouble(valor));
                } else if (filtro.getOperator().getSymbol().equals("<")) {
                    registroValido = (valorAux < Double.parseDouble(valor));
                } else if (filtro.getOperator().getSymbol().equals("<=")) {
                    registroValido = (valorAux <= Double.parseDouble(valor));
                } else if (filtro.getOperator().getSymbol().equals("=")) {
                    List<String> valores = BIUtil.stringtoList(valor, ",");
                    for (String valore : valores) {
                        if (valorAux == Double.parseDouble(valore)) {
                            registroValido = true;
                            break;
                        }
                    }
                } else if (filtro.getOperator().getSymbol().equals("<>")) {
                    List<String> valores = BIUtil.stringtoList(valor, ",");
                    int nDiferentes = 0;
                    for (String valore : valores) {
                        if (valorAux != Double.parseDouble(valore)) {
                            nDiferentes++;
                        }
                    }
                    registroValido = nDiferentes == valores.size();
                }

                if (!registroValido) {
                    registros_tabela.delete();
                }
            }
            registros_tabela.beforeFirst();
        }
        return registros_tabela;
    }

    public ConnectionBean getNovaConexaoIndicator() throws BIException {
        ConnectionBean conexao;
        try {
            if (this.connectionId != null) {
                if (false) {
                    // TODO get conn info using this.connectionId
                    //conexao = new ConnectionBean(this.connectionId, this.usuarioConexao, this.senhaConexao);
                } else {
                    conexao = new ConnectionBean(this.connectionId);
                }
            } else {
                conexao = new ConnectionBean();
            }
        } catch (Exception e) {
            throw new BIException(e);
        }
        return conexao;
    }

    public String converteExpressaoDeTituloParaCodigo(String expressao) throws BIException {
        String retorno = "";
        String str_titulo_campo_aux = "";
        Field campo_aux = null;
        boolean temMaisField = false;

        if (expressao.contains("{") && expressao.contains("}")) {
            temMaisField = true;
        }

        while (temMaisField) {
            temMaisField = false;
            str_titulo_campo_aux = expressao.substring(expressao.indexOf("{") + 1, expressao.indexOf("}"));
            campo_aux = this.getFieldPorTitulo(str_titulo_campo_aux);

            if (campo_aux != null) {
                expressao = BIUtil.replaceString(expressao, "{" + str_titulo_campo_aux + "}", "#$" + campo_aux.getCode() + "$!");
            } else {
                return "O campo " + str_titulo_campo_aux + " é inexistente.";
            }

            if (expressao.contains("{") && expressao.contains("}")) {
                temMaisField = true;
            }
        }
        retorno = expressao;
        return retorno;
    }

    public String converteExpressaoDeCodigoParaTitulo(String expressao) throws BIException {
        String retorno;
        String str_codigo_campo_aux;
        Field campo_aux;
        boolean temMaisField = false;
        if (expressao.contains("#$") && expressao.contains("$!")) {
            temMaisField = true;
        }

        while (temMaisField) {
            temMaisField = false;
            str_codigo_campo_aux = expressao.substring(expressao.indexOf("#$") + 2, expressao.indexOf("$!"));
            campo_aux = this.getFieldPorCodigo(str_codigo_campo_aux);

            if (campo_aux != null) {
                expressao = BIUtil.replaceString(expressao, "#$" + str_codigo_campo_aux + "$!", "{" + campo_aux.getTitle() + "}");
            } else {
                return "O campo com código " + str_codigo_campo_aux + " é inexistente.";
            }

            if (expressao.contains("#$") && expressao.contains("$!")) {
                temMaisField = true;
            }
        }
        retorno = expressao;
        return retorno;
    }

    public String converteExpressaoDeCodigoParaApelidoNome(String expressao, boolean todosFieldsSemAgregacao) throws BIException {
        String retorno;
        String str_codigo_campo_aux;
        Field campo_aux;
        boolean temMaisField = expressao.contains("#$") && expressao.contains("$!");

        while (temMaisField) {
            temMaisField = false;
            str_codigo_campo_aux = expressao.substring(expressao.indexOf("#$") + 2, expressao.indexOf("$!"));
            campo_aux = this.getFieldPorCodigo(str_codigo_campo_aux);

            if (campo_aux != null) {
                if (campo_aux.isExpression()) {
                    if (todosFieldsSemAgregacao) {
                        expressao = BIUtil.replaceString(expressao, "#$" + str_codigo_campo_aux + "$!",
                                "(" + converteExpressaoDeCodigoParaApelidoNome(campo_aux.getName(), true) + ")");
                    } else {
                        if (campo_aux.getAggregationType().equals("VAZIO")) {
                            expressao = BIUtil.replaceString(expressao, "#$" + str_codigo_campo_aux + "$!",
                                    "(" + converteExpressaoDeCodigoParaApelidoNome(campo_aux.getName(), false) + ")");
                        } else {
                            expressao = BIUtil.replaceString(expressao, "#$" + str_codigo_campo_aux + "$!",
                                    "(" + converteExpressaoDeCodigoParaApelidoNome(campo_aux.getName(), true) + ")");
                        }
                    }
                } else {
                    if (todosFieldsSemAgregacao || campo_aux.getAggregationType().equals("VAZIO")) {
                        expressao = BIUtil.replaceString(expressao, "#$" + str_codigo_campo_aux + "$!", campo_aux.getSqlExpressionWithoutNickName());
                    } else {
                        if (campo_aux.getAggregationType().equalsIgnoreCase("COUNT_DIST")) {
                            expressao = BIUtil.replaceString(expressao, "#$" + str_codigo_campo_aux + "$!",
                                    "COUNT(DISTINCT " + campo_aux.getSqlExpressionWithoutNickName() + ")");

                        } else {
                            expressao = BIUtil.replaceString(expressao, "#$" + str_codigo_campo_aux + "$!",
                                    campo_aux.getAggregationType() + "(" + campo_aux.getSqlExpressionWithoutNickName() + ")");
                        }
                    }
                }
            } else {
                return "O campo com código " + str_codigo_campo_aux + " é inexistente.";
            }

            if (expressao.contains("#$") && expressao.contains("$!")) {
                temMaisField = true;
            }
        }
        retorno = expressao;
        return retorno;
    }

    private Field getFieldPorTitulo(String titulo) {
        Field campo = null;
        for (Field field : this.fields) {
            if (field != null && field.getTitle().equalsIgnoreCase(titulo)) {
                campo = field;
                break;
            }
        }
        return campo;
    }

    public Field getFieldPorCodigo(String codigo) {
        if (codigo != null && !codigo.trim().isEmpty()) {
            return this.getFieldPorCodigo(Integer.parseInt(codigo));
        }
        return null;
    }

    public Field getFieldPorCodigo(int codigo) {
        for (Field campo : this.fields) {
            if (campo != null && campo.getCode() == codigo)
                return campo;
        }
        return null;
    }

    public void setFiltrosDimensao(String newClausulaFiltrosDimensao) throws BIException {
        if (newClausulaFiltrosDimensao != null && !newClausulaFiltrosDimensao.trim().isEmpty()) {
            newClausulaFiltrosDimensao = newClausulaFiltrosDimensao.substring(4);
            DimensionFilterInterpreter i = new DimensionFilterInterpreter(newClausulaFiltrosDimensao, this);
            filters.setDimensionFilter(i.getFiltro());
        }
    }

    public void setFiltrosMetrica(String newClausulaHaving) throws BIException {
        if (newClausulaHaving != null && !newClausulaHaving.trim().isEmpty()) {
            MetricFilterInterpreter i = new MetricFilterInterpreter(newClausulaHaving, this);
            filters.setMetricFilters(i.getFilters());
            if (i.hasSequenceFilters()) {
                if (this.filtersFunction == null) {
                    this.filtersFunction = new FiltersFunction();
                }
                this.filtersFunction.addFilter(i.getFilterSequence());
            }
            if (i.hasAccumulatedFilters()) {
                if (this.filtersFunction == null) {
                    this.filtersFunction = new FiltersFunction();
                }
                this.filtersFunction.addFilter(i.getFilterAccumulated());
            }
        }
    }

    public void setFiltrosMetricaSql(String newClausulaHaving) throws BIException {
        if (newClausulaHaving != null && !newClausulaHaving.trim().isEmpty()) {
            MetricFilterInterpreter i = new MetricFilterInterpreter(newClausulaHaving, this);
            filters.setMetricSqlFilter(i.getFilters());
        }
    }

    public void addField(Hashtable<String, String> hash_campo, Hashtable<String, String> hash_campoIndicator, NamedParameterJdbcTemplate jdbcTemplate)
            throws BIException {
        Field campo = new Field(Integer.parseInt(hash_campo.get("campo")), this, jdbcTemplate);
        campo.setName(hash_campo.get("nom_campo"));
        campo.setTitle(hash_campo.get("titulo_campo"));
        campo.setFieldType(hash_campo.get("tip_campo"));
        campo.setDataType(hash_campo.get("tip_dado"));
        campo.setNickName(hash_campo.get("apelido_campo"));
        campo.setExpression(hash_campo.get("eh_expressao").equals("S"));
        campo.setDrillDownSequence(Integer.parseInt(hash_campoIndicator.get("sequencia_filtro")));
        campo.setVisualizationSequence(Integer.parseInt(hash_campoIndicator.get("sequencia_visualiz")));
        campo.setDefaultField(hash_campoIndicator.get("padrao"));
        campo.setOrder(Integer.parseInt(hash_campoIndicator.get("ordem")));
        campo.setTableNickName(hash_campoIndicator.get("apelido_tabela"));
        campo.setOrderDirection(hash_campoIndicator.get("sentido_ordem"));
        campo.setNumDecimalPositions(Integer.parseInt(hash_campoIndicator.get("num_pos_decimais")));
        campo.setTotalizingField(hash_campoIndicator.get("totaliz_campo"));
        campo.setVerticalAnalysisType(hash_campoIndicator.get("analise_vertical"));
        campo.setAggregationType(hash_campoIndicator.get("tip_agregacao"));
        campo.setAccumulatedParticipation(hash_campoIndicator.get("particip_acumulada").equals("S"));
        this.addField(campo);
    }

    public void addField(Field campo) {
        FieldFactory.addField(this.fields, campo);
    }

    public void changeField(Hashtable<String, String> hash_Field) throws BIException {
        int posicao = Indicator.getFieldIndex(this.fields, hash_Field.get(("campo")));
        fields.get(posicao).setName(hash_Field.get("nom_campo"));
        fields.get(posicao).setTitle(hash_Field.get("titulo_campo"));
        fields.get(posicao).setFieldType(hash_Field.get("tip_campo"));
        fields.get(posicao).setDataType(hash_Field.get("tip_dado"));
        fields.get(posicao).setNickName(hash_Field.get("apelido_campo"));
        fields.get(posicao).setExpression(hash_Field.get("eh_expressao").equals("S"));
        fields.get(posicao).setAggregationType(hash_Field.get("tip_agregacao"));
    }

    private void setFields() {
        this.fields = this.pesquisaFieldsFromIndicator(this.code);
    }

    public List<Field> pesquisaFieldsFromIndicator(int codigoIndicator) {
        List<Field> campos;
        String sql_consulta_campo_indicador = "SELECT * FROM bi_campo_analise WHERE ind = " + codigoIndicator;
        ConnectionBean conexao = null;

        campos = this.jdbcTemplate.query(
                sql_consulta_campo_indicador, rs -> {
                    try {
                        return FieldFactory.criaFields(rs, this, this.jdbcTemplate);
                    } catch (BIException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        return campos;
    }

    public List<Field> pesquisaFieldsFromIndicatorDAO(int codigoIndicator) throws RegraNegocioException {
        // TODO Get fields from DB
        return null;
    }

    private int getQtdeFields(String tipoField) {
        int cont = 0;
        for (Field field : this.fields) {
            if (field != null && field.getFieldType().equals(tipoField)) {
                cont++;
            }
        }
        return cont;
    }

    public int getQtdeFieldsExibidosEmTela(int localExibicao) {
        int retorno = 0;
        for (Field field : this.fields) {
            if (field != null && field.getDefaultField().equals("S") && field.getDisplayLocation() == localExibicao) {
                retorno++;
            }
        }
        return retorno;
    }

    private int getQtdeFieldsExibidos(String tipoField, int localExibicao) {
        int cont = 0;
        for (Field field : this.fields) {
            if (field != null && !field.getDefaultField().equals("N") && field.getFieldType().equals(tipoField)
                    && field.getDisplayLocation() == localExibicao) {
                cont++;
                if (localExibicao == Constants.LINE && tipoField.equals("V")) {
                    if (field.isTotalizingField()) {
                        cont++;
                    }
                    if (field.isAccumulatedParticipation()) {
                        cont++;
                    }
                    if (field.isVerticalAnalysis()) {
                        cont++;
                    }
                    if (field.isAccumulatedValue()) {
                        cont++;
                    }
                    if (field.isHorizontalAnalysis()) {
                        cont++;
                    }
                    if (field.isHorizontalParticipation()) {
                        cont++;
                    }
                    if (field.isHorizontalParticipationAccumulated()) {
                        cont++;
                    }
                }
            }
        }
        return cont;
    }

    private void carregaFieldsRestritosMulti() {
        this.carregaFieldsRestritos(this.getLastLevelDimensionCode());
    }

    private void carregaFieldsRestritosPadrao() {
        this.carregaFieldsRestritos(-1);
    }

    public void carregaFieldsRestritos(int codDimensaoVerificar) {
        startFieldsCalculationByRestriction();
        for (Field field : this.fields) {
            if (field != null && !field.getDefaultField().equals("N") && field.getFieldType().equals(Constants.METRIC)) {
                MetricDimensionRestriction restMetDim = this.getMetricDimensionRestrictions().getRestMetricaDimensao(field.getCode());
                if (restMetDim != null && restMetDim.isMetricaRestrita(this, codDimensaoVerificar)) {
                    field.setDefaultField("T");
                    field.setCalculatorPerRestriction(true);
                }
            }
        }
    }

    public List<Field> loadFields(String fieldType, int displayLocation) {
        startFieldsCalculationByRestriction();

        int lastLevelCode = this.getLastLevelDimensionCode();

        return fields.stream()
                .filter(Objects::nonNull)
                .filter(field -> !field.getDefaultField().equals("N"))
                .filter(field -> field.getFieldType().equals(fieldType))
                .filter(field -> field.getDisplayLocation() == displayLocation)
                .peek(field -> {
                    if (fieldType.equals(Constants.METRIC)) {
                        // TODO Check this when doing metric restrictions
                        /*MetricDimensionRestriction restMetDim = this.getMetricDimensionRestrictions().getRestMetricaDimensao(field.getCode());
                        if (restMetDim != null && restMetDim.isMetricaRestrita(this, lastLevelCode)) {
                            field.setDefaultField("T");
                            field.setCalculatorPerRestriction(true);
                        }*/
                    }
                })
                .flatMap(field -> {
                    Stream.Builder<Field> builder = Stream.builder();
                    builder.add(field);

                    if (displayLocation == Constants.LINE && fieldType.equals("V")) {
                        if (field.isTotalizingField()) {
                            builder.add(createField(field.getCode(), "", "total", "S", String.valueOf(field.getColumnWidth()), field.getColumnAlignmentPosition()));
                        }
                        if (field.isVerticalAnalysis()) {
                            builder.add(createField(field.getCode(), "", "%", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignmentPosition()));
                        }
                        if (field.isAccumulatedParticipation()) {
                            builder.add(createField(field.getCode(), "", "% Acumulada", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignmentPosition()));
                        }
                        if (field.isAccumulatedValue()) {
                            builder.add(createField(field.getCode(), "", field.getTitle() + " Acum.", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignmentPosition()));
                        }
                        if (field.isHorizontalAnalysis()) {
                            builder.add(createField(field.getCode(), "", "AH%", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignmentPosition()));
                        }
                        if (field.isHorizontalParticipation()) {
                            builder.add(createField(field.getCode(), "", "AH Participação", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignmentPosition()));
                        }
                        if (field.isHorizontalParticipationAccumulated()) {
                            builder.add(createField(field.getCode(), "", "AH Participação Acumulada", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignmentPosition()));
                        }
                    }
                    return builder.build();
                })
                .toList();
    }

    private Field createField(int code, String name, String title, String defaultField, String columnWidth, String columnAlignmentPosition) {
        Field field = new Field();
        field.setCode(code);
        field.setName(name);
        field.setTitle(title);
        field.setDefaultField(defaultField);
        field.setColumnWidth(columnWidth);
        field.setColumnAlignmentPosition(columnAlignmentPosition);
        return field;
    }

    private Field createField(int code, String name, String title, boolean verticalAnalysis, boolean childField, String defaultField, String columnWidth, String columnAlignmentPosition) {
        Field field = createField(code, name, title, defaultField, columnWidth, columnAlignmentPosition);
        field.setVerticalAnalysis(verticalAnalysis);
        field.setChildField(childField);
        return field;
    }

    private void processarCubo(ResultSet cachedrowset) throws BIException {
        try {
            this.cubo.processar(cachedrowset);
        } catch (SQLException sqle) {
            BIDatabaseException biex = new BIDatabaseException(sqle);
            biex.setAction("Processamento do Cubo.");
            biex.setLocal(getClass(), "processarCubo(CachedRowSet)");
            throw biex;
        }
    }

    private void montaTabela(ResultSet cachedrowset) throws BIException {
        try {
            if (this.getTableType() == 0 || this.getTableType() == 2) {
                this.validaTabelaMultiDimensao();
                this.isMultidimensional = true;
            } else {
                this.isMultidimensional = false;
            }
        } catch (Exception e) {
            this.isMultidimensional = false;
        }
        try {
            if (this.isMultidimensional) {
                this.ordenaFieldsPorSequenciaDrillDown();
                this.dimensionColumn = this.loadFields(Constants.DIMENSION, Constants.COLUMN);
                if (this.stopProcess())
                    return;
                this.configureFieldsNavigation(this.dimensionColumn);
                this.cubo = Cubo.factoryCuboFormatoMultiDimensional(this.createCuboMetaData());
                if (this.cuboListener != null) {
                    this.cubo.setCuboListener(new CubeProcessor(this));
                }
                this.processarCubo(cachedrowset);
                this.cubeTable = new MultiDimensionalDefaultTableBuilder(this.cubo);
            } else {
                this.cubo = Cubo.factoryCuboFormatoPadrao(this.createCuboMetaDataFormatoPadrao());
                if (this.cuboListener != null) {
                    this.cubo.setCuboListener(new CubeProcessor(this));
                }
                this.processarCubo(cachedrowset);
                this.cubeTable = new DefaultTableBuilder(this.cubo);
            }
        } catch (BIException e) {
            throw new BIGeneralException(e);
        } catch (DateException e) {
            throw new RuntimeException(e);
        }
    }

    private void configureFieldsNavigation(List<Field> columnFields) {
        for (Field field : columnFields) {
            int index = findPreviousDrillDown(field);

            field.setDrillUp(index >= 0 && fields.get(index).isDrillDown());

            index = findNextDrillDown(field);
            Field indexField = fields.get(index);

            if (indexField.isDrillDown()) {
                field.setNavigableUpwards(true);
                field.setNavigable(!indexField.getDefaultField().equals("S"));
            } else {
                field.setNavigableUpwards(false);
                field.setNavigable(false);
            }
        }
    }

    private int findPreviousDrillDown(Field field) {
        for (int currentIndex = fields.size() - 1; currentIndex >= 0; currentIndex--) {
            if (isDrillDownField(currentIndex, field)) {
                return currentIndex;
            }
        }
        return -1;
    }

    private int findNextDrillDown(Field field) {
        for (int currentIndex = 0; currentIndex < fields.size(); currentIndex++) {
            if (isDrillDownField(currentIndex, field)) {
                return currentIndex;
            }
        }
        return fields.size();
    }

    private boolean isDrillDownField(int index, Field field) {
        Field auxField = fields.get(index);

        if (auxField == null) {
            return false;
        }

        boolean isNotDimension = !auxField.getFieldType().equals(Constants.DIMENSION);
        boolean isNotMetric = !auxField.getFieldType().equals(Constants.METRIC);
        boolean isNotInLine = auxField.getDisplayLocation() != Constants.LINE;
        boolean isDrillDown = auxField.isDrillDown();
        boolean isDrillDownSequenceLess = auxField.getDrillDownSequence() < field.getDrillDownSequence();

        return isNotDimension && isNotMetric && isNotInLine && isDrillDown && isDrillDownSequenceLess;
    }

    public TabelaHTML montarTitulo() throws BIException {
        TabelaHTML tabTitulo = new TabelaHTML();
        tabTitulo.setId("nome_indicador");
        tabTitulo.setLargura("100");
        tabTitulo.addLinha(new LinhaHTML());
        tabTitulo.getLinhaAtual().setEditable("yes");
        tabTitulo.getLinhaAtual().addCelula(new CelulaHTML());
        tabTitulo.getLinhaAtual().getCelulaAtual().setAlinhamento("center");
        tabTitulo.getLinhaAtual().getCelulaAtual().setId("nome_titulo");
        tabTitulo.getLinhaAtual().getCelulaAtual().setNowrap(true);
        EstiloHTML estiloTitulo = new EstiloHTML();
        estiloTitulo.setBackgroundColor("#FFFFFF");
        estiloTitulo.setFontSize(14);
        estiloTitulo.setFontFamily("verdana");
        estiloTitulo.setFontColor("#000080");
        estiloTitulo.setFontWeight("bold");
        estiloTitulo.setFontStyle("normal");
        estiloTitulo.setTextDecoration("none");
        estiloTitulo.setAdditionalParameters("cursor: pointer;");
        tabTitulo.getLinhaAtual().getCelulaAtual().setEstilo(estiloTitulo);
        tabTitulo.getLinhaAtual().getCelulaAtual().setEditable("yes");
        tabTitulo.getLinhaAtual().getCelulaAtual().setConteudo(this.name);
        return tabTitulo;
    }

    public TabelaHTML montaDrillUp() {
        TabelaHTML tab_drillup = new TabelaHTML();
        tab_drillup.setId("opcao_drill_up");
        tab_drillup.addLinha(new LinhaHTML());
        tab_drillup.getLinhaAtual().addCelula(new CelulaHTML());
        LinkHTMLSVGColuna imagem;
        if (this.dimensionColumn.get(0).isDrillUp()) {
            imagem = new LinkHTMLSVGColuna("vect-arrow-square-up-left", "Drill Up", 18, 18);
            imagem.addParametro("onclick", "addDrillUp(0)");
        } else {
            imagem = new LinkHTMLSVGColuna();
        }
        tab_drillup.getLinhaAtual().getCelulaAtual().setConteudo(imagem);
        return tab_drillup;
    }

    public String montaDrillUpFormatoPadrao() {
        StringBuilder sTabelaDrillUp = new StringBuilder();
        if (verificaColunasDimensao()) {
            sTabelaDrillUp.append("<table>");
            BIDriller bi = new BIDriller();
            Field campoAux = bi.buscaFieldAtual(this);
            Field ant = new Field();
            Field prox = null;
            if (campoAux != null) {
                prox = bi.buscaProximaSequenciaDrillDown(campoAux, this.fields);
                ant = bi.buscaFieldAnterior(campoAux.getDrillDownSequence(), this.fields);
            }
            if (prox == null) {
                sTabelaDrillUp.append("<tr editable='no'>")
                        .append("<th style='display:flex; text-align:left' colspan='2'>")
                        .append("<div onclick='addDrillUp(").append(this.getCode())
                        .append(")' class='vect-arrow-square-up-left' style='width: 18px; height: 18px; margin: auto;' title='Drill-Up'></div>")
                        .append("</th>")
                        .append("</tr>");
            } else if (ant == null) {
                sTabelaDrillUp.append("<tr editable ='no'>")
                        .append("<th style='display:flex; text-align:left' colspan = '2'>");
                BIDriller di = new BIDriller();
                Field aux = di.buscaFieldAtual(this);
                sTabelaDrillUp.append("<div onclick='DrillDownSemFiltro(").append(aux.getCode()).append(",").append(this.getCode())
                        .append(")' class='vect-arrow-square-down-right' style='width: 18px; height: 18px; margin: auto;' title='Descer um n�vel sem utilizar Drill-Down'></div>")
                        .append("</th>")
                        .append("</tr>");
            } else {
                sTabelaDrillUp.append("<tr editable ='no'>")
                        .append("<th style='display:flex; text-align:left' colspan = '2' >")
                        .append("<div onclick='addDrillUp(").append(this.getCode())
                        .append(")' class='vect-arrow-square-up-left' style='width: 18px; height: 18px; margin: auto;' title='Drill-Up'></div>");
                BIDriller di = new BIDriller();
                Field aux = di.buscaFieldAtual(this);
                sTabelaDrillUp.append("<div onclick='DrillDownSemFiltro(").append(aux.getCode()).append(",").append(this.getCode())
                        .append(")' class='vect-arrow-square-down-right' style='width: 18px; height: 18px; margin: auto;' title='Descer um n�vel sem utilizar Drill-Down'></div>")
                        .append("</th>")
                        .append("</tr>");
            }
            sTabelaDrillUp.append("</table>");
        }
        return sTabelaDrillUp.toString();
    }

    private void montaTituloEDrillUp(Writer out, boolean montaSemLink, Object tabelaDrillUp) throws BIException {
        TabelaHTML tabTitulo = montarTitulo();
        TabelaHTML tabela = new TabelaHTML();
        tabela.addLinha(new LinhaHTML());
        tabela.getLinhaAtual().addCelula(new CelulaHTML());
        tabela.getLinhaAtual().getCelulaAtual().setConteudo(tabTitulo);
        tabela.addLinha(new LinhaHTML());
        tabela.getLinhaAtual().addCelula(new CelulaHTML());
        tabela.setLargura("100%");
        tabela.getLinhaAtual().getCelulaAtual().setConteudo(tabelaDrillUp);
        tabela.toString(out);
    }

    public void getTabela(Writer out, boolean montaSemLink) throws Exception {
        try {
            if (this.cubeTable == null) {
                this.montaSaida();
            }

            Object tabelaDrillUp = "&nbsp;";
            if (!montaSemLink) {
                if (this.isMultidimensional) {
                    tabelaDrillUp = this.montaDrillUp();
                } else {
                    tabelaDrillUp = this.montaDrillUpFormatoPadrao();
                }
            }
            this.montaTituloEDrillUp(out, montaSemLink, tabelaDrillUp);
            this.cubeTable.processar(new ImpressorHTML(out, (!montaSemLink)));

            // TODO:Comentado para teste da obtenção dos dados da tabela para o Mapa Google
            //this.limpa();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String getTabela(boolean montaSemLink) throws Exception {
        StringWriter out = new StringWriter();
        this.getTabela(out, montaSemLink);
        return out.toString();
    }


    public void setClausulas() throws BIException {
        StringBuilder select = new StringBuilder("SELECT ");
        StringBuilder groupBy = new StringBuilder();
        String orderBy = "";
        String agregacao;
        Field campoAux;
        ordenaFieldsPorSequenciaVisualizacao();

        for (Field field : fields) {
            campoAux = field;
            if (campoAux != null) {
                agregacao = campoAux.getAggregationType();

                if ("N".equals(campoAux.getDefaultField())) {
                    if (field.getFieldType().equals("D") && !campoAux.getAggregationType().equalsIgnoreCase("COUNT")
                            && !campoAux.getAggregationType().equalsIgnoreCase("COUNT_DIST")) {
                        if (!this.isFieldUtilizadoParaOrdenacao(field))
                            continue;
                    }
                }

                if (Constants.DIMENSION.equals(campoAux.getFieldType()) && !campoAux.getAggregationType().equalsIgnoreCase("COUNT")
                        && !campoAux.getAggregationType().equalsIgnoreCase("COUNT_DIST")) {
                    if (campoAux.isFixedValue()) {
                        select.append(campoAux.getName()).append(" ").append(campoAux.getNickName().trim()).append(",");
                    } else {
                        select = new StringBuilder(select + campoAux.getSqlExpressionWithNickName());
                        if (!campoAux.isExpression() || agregacao.equalsIgnoreCase("VAZIO") && !campoAux.getTableNickName().isEmpty()) {
                            groupBy.append(campoAux.getSqlExpressionWithoutNickName()).append(",");
                        }
                        continue;
                    }
                }
                if (!campoAux.isFixedValue()) {
                    if (campoAux.isExpression()) {

                        if (campoAux.getName().toUpperCase().trim().indexOf("SE(") == 0
                                || campoAux.getName().toUpperCase().trim().indexOf("IF(") == 0) {
                        } else {
                            String exp;
                            if (agregacao.equals("VAZIO")) {
                                exp = converteExpressaoDeCodigoParaApelidoNome(campoAux.getName(), false);
                                if (!exp.toUpperCase().trim().contains("SE(") && !exp.toUpperCase().trim().contains("IF(")) {
                                    select.append(" ").append(exp).append(" ").append(campoAux.getNickName().trim()).append(",");
                                }
                            } else {
                                exp = converteExpressaoDeCodigoParaApelidoNome(campoAux.getName(), true);
                                if (!exp.toUpperCase().trim().contains("SE(") && !exp.toUpperCase().trim().contains("IF(")) {
                                    if (agregacao.equalsIgnoreCase("COUNT_DIST")) {
                                        select.append(" ").append("COUNT(DISTINCT ").append(exp).append(") ").append(campoAux.getNickName().trim()).append(",");
                                    } else {
                                        select.append(" ").append(agregacao).append("(").append(exp).append(") ").append(campoAux.getNickName().trim()).append(",");
                                    }
                                }
                            }
                        }
                    } else if (agregacao.equals("VAZIO")) {
                        select.append(" ").append(campoAux.getSqlExpressionWithNickName());
                        groupBy.append(campoAux.getSqlExpressionWithoutNickName()).append(",");
                    } else {
                        if (agregacao.equalsIgnoreCase("COUNT_DIST")) {
                            select.append(" ").append("COUNT(DISTINCT ").append(campoAux.getSqlExpressionWithoutNickName()).append(") ").append(campoAux.getNickName().trim()).append(",");
                        } else {
                            select.append(" ").append(agregacao).append("(").append(campoAux.getSqlExpressionWithoutNickName()).append(") ").append(campoAux.getNickName().trim()).append(",");
                        }
                    }
                }
            }
        }

        if (!select.isEmpty()) {
            select = new StringBuilder(select.substring(0, select.length() - 1));
        }

        if (!groupBy.isEmpty()) {
            groupBy = new StringBuilder(" GROUP BY " + groupBy.substring(0, groupBy.length() - 1));
        }

        this.searchClause = select.toString();
        this.groupClause = groupBy.toString();
        this.orderClause = orderBy;
    }

    public boolean isFieldUtilizadoParaOrdenacao(Field campo) {
        for (Field c : this.fields) {
            if (c == null || campo == null)
                continue;
            if (c.getDelegateOrder() == null)
                continue;
            if (c.getDelegateOrder() == campo.getCode()) {
                return true;
            }
        }
        return false;
    }

    public int buscaIndiceField(int cod) {
        for (int i = 0; i < fields.size(); i++)
            if (fields.get(i) != null) {
                if (fields.get(i).getCode() == cod)
                    return i;
            }
        return -1;
    }

    public static String getOrderByOrdem(Field[] campos) {
        StringBuilder orderBy = new StringBuilder();
        for (int cont = 0; cont < campos.length; cont++) {
            for (Field campo : campos) {
                if (campo != null && campo.getOrder() == cont + 1 && campo.getDefaultField().equals("S")) {
                    orderBy.append(campo.getNickName().trim()).append(" ").append(campo.getOrderDirection()).append(",");
                }
            }
        }
        return orderBy.toString();
    }

    public void atualizaFieldVisualizDimensao(String listFields) {
        for (Field value : fields)
            if (value != null && value.getFieldType().equals("D"))
                value.setDefaultField("N");

        if (!listFields.isEmpty()) {
            List<String> vct_campos = BIUtil.stringtoList(listFields);
            for (Field field : fields)
                if (field != null && vct_campos.contains(String.valueOf(field.getCode())))
                    field.setDefaultField("S");
        }
    }

    public void atualizaFieldVisualizValores(String listFields, String listCores) {
        List<String> list_campos = BIUtil.stringtoList(listFields);
        for (Field value : fields)
            if (value != null && value.getFieldType().equals("V"))
                value.setDefaultField("N");

        for (Field field : fields) {
            if (field == null || !list_campos.contains(String.valueOf(field.getCode())))
                continue;
            field.setDefaultField("S");
        }

    }

    public void atualizaSequenciaVisualizDimensao(String listFields) throws BIException {
        Field[] campos_valores = new Field[fields.size()];
        BIComparator.classificacao(fields, Constants.VISUALIZATION_SEQUENCE);

        int cont = 0;
        int i;

        for (i = 0; i < fields.size(); i++)
            if (fields.get(i) != null && fields.get(i).getDefaultField().equals("S") && fields.get(i).getFieldType().equals("V"))
                campos_valores[cont++] = fields.get(i);

        for (i = 0; i < fields.size(); i++)
            if (fields.get(i) != null)
                fields.get(i).setVisualizationSequence(0);

        cont = 1;

        if (!listFields.isEmpty()) {
            List<String> list_campos = BIUtil.stringtoList(listFields);
            for (i = 0; i < list_campos.size(); i++)
                fields.get(getFieldIndex(list_campos.get(i))).setVisualizationSequence(cont++);

        }
        for (i = 0; i < campos_valores.length; i++)
            if (campos_valores[i] != null)
                fields.get(getFieldIndex(String.valueOf(campos_valores[i].getCode()))).setVisualizationSequence(cont++);
    }

    public void atualizaSequenciaVisualizValores(String listFields) throws BIException {
        for (Field field : fields)
            if (field != null && field.getFieldType().equals("V"))
                field.setVisualizationSequence(0);

        if (!listFields.isEmpty()) {
            List<String> list_campos = BIUtil.stringtoList(listFields);
            int cont = getMaxSequenciaVisualizDimensao();
            for (String listCampo : list_campos)
                fields.get(getFieldIndex(listCampo)).setVisualizationSequence(cont++);

        }

        BIComparator.classificacao(fields, Constants.VISUALIZATION_SEQUENCE);

        int cont;
        int i;

        for (i = 0; i < fields.size(); i++)
            if (fields.get(i) != null)
                fields.get(i).setVisualizationSequence(0);

        cont = 1;
        for (i = 0; i < fields.size(); i++)
            if (fields.get(i) != null && fields.get(i).getDefaultField().equals("S") && fields.get(i).getFieldType().equals("D"))
                fields.get(i).setVisualizationSequence(cont++);

        if (!listFields.isEmpty()) {
            List<String> list_campos = BIUtil.stringtoList(listFields);
            for (i = 0; i < list_campos.size(); i++)
                fields.get(getFieldIndex(list_campos.get(i))).setVisualizationSequence(cont++);
        }
    }

    public void atualizaSequenciaVisualizacao(String listFields) throws BIException {
        this.atualizaSequencia(listFields, false);
    }

    public void atualizaSequencia(String listFields, boolean setarPadrao) throws BIException {
        List<Integer> sequenciasUtilizadas = new ArrayList<>();
        for (Field field : fields) {
            if (field == null)
                continue;
            if ("S".equals(field.getDefaultField())) {
                field.setVisualizationSequence(0);
            } else {
                sequenciasUtilizadas.add(field.getVisualizationSequence());
            }
        }

        if (!listFields.isEmpty()) {
            List<String> list_campos = BIUtil.stringtoList(listFields);
            int cont = getMaxSequenciaVisualizDimensao();
            for (String listCampo : list_campos) {
                int indice = Indicator.getFieldIndex(this.fields, listCampo);
                do {
                    cont++;
                } while (this.contemFieldNaPosicao(cont, sequenciasUtilizadas));
                fields.get(indice).setVisualizationSequence(cont);
                if (setarPadrao) {
                    fields.get(indice).setDefaultField("S");
                }
                continue;
            }
        }
    }

    public void atualizaSequenciaDrillDown(String sFieldsDrillDown, String sFieldsNaoDrillDown) {
        if (!sFieldsDrillDown.isEmpty()) {
            List<String> list_campos = BIUtil.stringtoList(sFieldsDrillDown);
            int cont_filtro = 1;
            for (String listCampo : list_campos) {
                Field campo = this.getFieldPorCodigo(listCampo);
                campo.setDrillDown(true);
                campo.setDrillDownSequence(cont_filtro++);
            }
        }
        if (!sFieldsNaoDrillDown.isEmpty()) {
            List<String> list_campos = BIUtil.stringtoList(sFieldsNaoDrillDown);
            for (String listCampo : list_campos) {
                Field campo = this.getFieldPorCodigo(listCampo);
                campo.setDrillDown(false);
            }
        }
    }

    private boolean contemFieldNaPosicao(int posicao, List<Integer> sequenciasUtilizadas) {
        for (Integer x : sequenciasUtilizadas) {
            if (x == posicao) {
                return true;
            }
        }
        return false;
    }

    public void atualizaCalculos(String camposCalculados) throws BIException {
        if (!camposCalculados.isEmpty()) {
            List<String> list_campos = BIUtil.stringtoList(camposCalculados);
            for (String listCampo : list_campos) {
                int indice = Indicator.getFieldIndex(this.fields, listCampo);
                fields.get(indice).setDefaultField("T");
            }
        }
    }

    public int getFieldIndex(String codigo) throws BIException {
        return Indicator.getFieldIndex(this.fields, codigo);
    }

    public static int getFieldIndex(List<Field> campos, String codigo) throws BIException {
        for (int i = 0; i < campos.size(); i++)
            if (campos.get(i) != null && campos.get(i).getCode() == Integer.parseInt(codigo))
                return i;
        BIGeneralException biex = new BIGeneralException("Nao foi possivel encontrar o campo de codigo " + codigo + " no indicador atual.");
        biex.setAction("buscar indice de um campo");
        biex.setLocal("com.logocenter.logixbi.analysis.Indicator", "getIndiceField(String)");
        throw biex;
    }

    private int getMaxSequenciaVisualizDimensao() {
        int maior = 0;
        for (Field field : fields)
            if (field != null && field.isDrillDown() && field.getFieldType().equals("D") && maior < field.getVisualizationSequence())
                maior = field.getVisualizationSequence();
        return maior;
    }

    public int getMaxSequenciaDrillDown() throws BIException {
        int maior = 0;
        for (Field field : fields)
            if (field != null && field.isDrillDown() && maior < field.getDrillDownSequence())
                maior = field.getDrillDownSequence();
        return maior;
    }

    public int getMaxSequenciaVisualiz() {
        int maior = 0;
        for (Field field : fields)
            if (field != null && maior < field.getVisualizationSequence())
                maior = field.getVisualizationSequence();
        return maior;
    }

    public void ordenaFieldsPorSequenciaVisualizacao() {
        this.ordenaFieldsPorSequenciaVisualizacao(this.fields);
    }

    public void ordenaFieldsPorSequenciaVisualizacao(List<Field> campos) {
        List<Field> cmpAux = new ArrayList<>();
        int cont = 0;
        for (Field campo : campos)
            if (campo != null && campo.getVisualizationSequence() == 0)
                cmpAux.set(cont++, campo);
        List<Field> cmp1 = new ArrayList<>();
        for (int i = 0; i < cmpAux.size() && cmpAux.get(i) != null; i++)
            cmp1.set(i, cmpAux.get(i));
        cont = 0;
        cmpAux = new ArrayList<>();
        for (Field campo : campos)
            if (campo != null && campo.getVisualizationSequence() != 0)
                cmpAux.set(cont++, campo);
        List<Field> cmp2 = new ArrayList<>();
        for (int i = 0; i < cmpAux.size() && cmpAux.get(i) != null; i++)
            cmp2.set(i, cmpAux.get(i));
        sortCodigo(cmp1);
        sortSequenciaVisualizacao(cmp2);
        System.arraycopy(cmp1, 0, campos, 0, cmp1.size());
        cont = 0;
        for (int i = cmp1.size(); i < cmp1.size() + cmp2.size(); i++)
            campos.set(i, cmp2.get(cont++));
    }

    private void sortSequenciaVisualizacao(List<Field> a) {
        if (a.isEmpty())
            return;
        for (int i = a.size(); true; ) {
            boolean swapped = false;
            for (int j = 0; j < i; j++)
                if (a.get(j).getVisualizationSequence() > a.get(j + 1).getVisualizationSequence()) {
                    Field T = a.get(j);
                    a.set(j, a.get(j + 1));
                    a.set(j + 1, T);
                    swapped = true;
                }
            if (!swapped)
                return;
        }
    }

    private void sortCodigo(List<Field> a) {
        if (a.size() <= 1)
            return;
        for (int i = a.size(); true; ) {
            boolean swapped = false;
            for (int j = 0; j < i; j++)
                if (a.get(j).getCode() > a.get(j+1).getCode()) {
                    Field T = a.get(j);
                    a.set(j, a.get(j+1));
                    a.set(j + 1, T);
                    swapped = true;
                }
            if (!swapped)
                return;
        }
    }

    public List<Field> getFields(String tipoField) {
        List<Field> cmpAux = new ArrayList<>();
        int cont = 0;

        for (Field field : this.fields) {
            if (field != null && field.getFieldType().equals(tipoField)) {
                cmpAux.set(cont++, field);
            }
        }
        return cmpAux;
    }

    private List<Field> getTodosFieldsDimensaoDrillDown() {
        List<Field> retorno = new ArrayList<Field>();
        for (Field field : this.fields) {
            if (field != null && field.getFieldType().equals(Constants.DIMENSION) && field.isDrillDown()) {
                retorno.add(field);
            }
        }
        return retorno;
    }

    private List<Field> getTodosFieldsDimensaoNaoDrillDown() {
        List<Field> retorno = new ArrayList<>();
        for (Field field : this.fields) {
            if (field != null && field.getFieldType().equals(Constants.DIMENSION) && !field.isDrillDown()) {
                retorno.add(field);
            }
        }
        return retorno;
    }

    public void ordenaFieldsPorSequenciaDrillDown() {
        List<Field> cmpMetrica = this.getFields(Constants.METRIC);
        List<Field> camposDrillDown = getTodosFieldsDimensaoDrillDown();
        List<Field> camposNaoDrillDown = getTodosFieldsDimensaoNaoDrillDown();
        int cont = 0;
        sortDrillDownSequence(camposDrillDown);
        sortDrillDownSequence(camposNaoDrillDown);

        for (Field campo : camposDrillDown) {
            this.fields.set(cont++, campo);
        }
        for (Field campo : camposNaoDrillDown) {
            this.fields.set(cont++, campo);
        }
        for (Field field : cmpMetrica) {
            this.fields.set(cont++, field);
        }
    }

    private void sortDrillDownSequence(List<Field> campos) {
        DrillDownComparator drillDownComparator = new DrillDownComparator();
        campos.sort(drillDownComparator);
    }

    public String getSelectFieldsDisp() {
        List<Field> cmps = new ArrayList<>();
        int cont = 0;
        for (Field field : this.fields) {
            if (field != null && !field.getTitle().equalsIgnoreCase("Não visualizado") && "N".equals(field.getDefaultField())) {
                cmps.set(cont++, field);
            }
        }
        BIComparator.classificacao(cmps, Constants.TITLE);
        SelectFields slct = new SelectFields(cmps);
        slct.setComponentParameter("style=\"overflow: auto;\" class=\"fundoAzulLargura\" onFocus=\"selecionado(this);\"  onDblClick=\"editaField(this);\"");
        slct.setName("lstCmpDisp");
        slct.setSize(18);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.addClasseTipoField(Constants.DIMENSION, "odd");
        slct.addClasseTipoField(Constants.METRIC, "cor");
        slct.montaComponente();
        return slct.toString();
    }

    public void setLocalizacaoFields(String campos, int localExibicao) {
        if (campos != null && !campos.isEmpty()) {
            Field campo;
            for (String codigosFields : BIUtil.stringtoList(campos)) {
                campo = this.getFieldPorCodigo(codigosFields);
                campo.setDisplayLocation(localExibicao);
            }
        }
    }

    public void setLocalizacaoFieldsSomCalculo(String campos, int localExibicao) {
        if (campos != null && !campos.isEmpty()) {
            Field campo;
            for (String codigosFields : BIUtil.stringtoList(campos)) {
                campo = this.getFieldPorCodigo(codigosFields);
                campo.setDisplayLocation(localExibicao);
                campo.setDefaultField("T");
            }

        }
    }

    public void resetLocalizacaoFields() {
        for (Field campo : this.fields) {
            if (campo != null) {
                campo.setDisplayLocation(0);
                campo.setDefaultField("N");
            }
        }
    }


    public void validaTabelaMultiDimensao() throws BIException {
        int dimensoesLinha = this.getQtdeFieldsExibidos(Constants.DIMENSION, Constants.LINE);
        int metricasColuna = this.getQtdeFieldsExibidos(Constants.METRIC, Constants.COLUMN);
        int metricasLinha = this.getQtdeFieldsExibidos(Constants.METRIC, Constants.LINE);

        if (dimensoesLinha < 1) {
            if (this.tableType == 0) {
                throw new BIGeneralException("Deve haver pelo menos uma dimensão nas linhas!");
            }
        }
        if (metricasColuna + metricasLinha < 1) {
            throw new BIGeneralException("Você deve selecionar pelo menos uma métrica!");
        }
        if (metricasColuna > 0 && metricasLinha > 0) {
            throw new BIGeneralException("As métricas devem estar nas colunas OU nas linhas.\nNão podem estar em ambas.");
        }
    }

    public String getSelectFieldsColuna() {
        List<Field> cmps = new ArrayList<>();
        int cont = 0;
        for (Field field : this.fields) {
            if (field != null && !"N".equals(field.getDefaultField())
                    && !"Não Visualizado".equalsIgnoreCase(field.getTitle())) {
                if (field.isDimension() && field.getDisplayLocation() == 0) {
                    field.setDisplayLocation(Constants.COLUMN);
                    cmps.set(cont++, field);
                } else if (field.getDisplayLocation() == Constants.COLUMN) {
                    cmps.set(cont++, field);
                }
            }
        }
        SelectFields slct = new SelectFields(cmps);
        slct.setComponentParameter("style=\"overflow: auto;\" class=\"fundoAzulLargura\" onFocus=\"selecionado(this);\" onDblClick=\"editaField(this);\"");
        slct.setName("cmpColuna");
        slct.setSize(8);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.addClasseTipoField(Constants.DIMENSION, "odd");
        slct.addClasseTipoField(Constants.METRIC, "cor");
        slct.montaComponente();
        return slct.toString();
    }

    public String getSelectFieldsLinha() {
        List<Field> cmps = new ArrayList<>();
        int cont = 0;
        for (Field field : this.fields) {
            if (field != null && !"N".equals(field.getDefaultField())
                    && !"Não Visualizado".equalsIgnoreCase(field.getTitle())) {
                if (field.isMetric() && field.getDisplayLocation() == 0) {
                    field.setDisplayLocation(Constants.LINE);
                    cmps.set(cont++, field);
                } else if (field.getDisplayLocation() == Constants.LINE) {
                    cmps.set(cont++, field);
                }
            }
        }

        SelectFields slct = new SelectFields(cmps);
        slct.setComponentParameter("style=\"overflow: auto;\" class=\"fundoAzulLargura\" onFocus=\"selecionado(this);\" onDblClick=\"editaField(this);\"");
        slct.setName("cmpLinha");
        slct.setSize(8);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.addClasseTipoField(Constants.DIMENSION, "odd");
        slct.addClasseTipoField(Constants.METRIC, "cor");
        slct.montaComponente();
        return slct.toString();
    }

    public String getSelectDimensaoDisp() {
        List<Field> cmps = new ArrayList<>();
        int cont = 0;
        for (Field field : fields) {
            if (field != null && field.getFieldType().equals("D") && field.getDefaultField().equals("N")) {
                cmps.set(cont++, field);
            }
        }
        BIComparator.classificacao(cmps, Constants.TITLE);
        SelectFields slct = new SelectFields(cmps);
        slct.setComponentParameter("style=\"overflow: auto;\" class=\"fundoAzulLargura\" onFocus=\"selecionado(this);\" onDblClick=\"editaField(this);\"");
        slct.setName("lstDimDisp");
        slct.setSize(8);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.addClasseTipoField(Constants.DIMENSION, "odd");
        slct.montaComponente();
        return slct.toString();
    }

    public String getSelectDimensaoSel() {
        List<Field> cmps = new ArrayList<>();
        int cont = 0;
        for (Field field : fields) {
            if (field != null && field.getFieldType().equals("D") && field.getDefaultField().equals("S"))
                cmps.set(cont++, field);
        }
        BIComparator.classificacao(cmps, Constants.VISUALIZATION_SEQUENCE);
        SelectFields slct = new SelectFields(cmps);
        slct.setComponentParameter("class=\"fundoAzulLargura\"");
        slct.setName("lstDimSel");
        slct.setSize(8);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.montaComponente();
        return slct.toString();
    }

    public String getSelectOrdenacaoDisp() {
        return this.getSelectOrdenacaoDisp(this.fields);
    }

    public String getSelectOrdenacaoDisp(List<Field> campos) {
        List<Field> cmps = new ArrayList<>();
        List<Field> convertido = new ArrayList<>();

        for (int i = 0; i < campos.size(); i++) {
            Field c = (Field) campos.get(i);
            convertido.set(i, c);
        }

        List<Field> listaFields = BIUtil.getFieldList(convertido);
        for (Field campo : listaFields) {
            if (campo != null && !campo.isFixedValue())
                cmps.add(campo);
        }
        for (Field campo : listaFields) {
            if (campo != null && campo.getAccumulatedLine().equals("S")) {
                Field campoAux = new Field();
                campoAux.setCode(campo.getCode());
                campoAux.setFieldType(campo.getFieldType());
                if (campoAux.getTitle() == null || !campoAux.getTitle().contains("Acumulado ( ")) {
                    campoAux.setTitle("Acumulado ( " + campo.getTitle() + " )");
                }
                cmps.add(campoAux);
            }
        }
        List<Field> retorno = new ArrayList<>();
        for (int i1 = 0; i1 < cmps.size(); i1++) {
            retorno.set(i1, cmps.get(i1));
        }
        SelectFields slct = new SelectFields(retorno);
        slct.setComponentParameter("class=\"fundoAzulLargura\"");
        slct.setName("lstDimDisp");
        slct.setSize(8);
        slct.setActive(true);
        slct.montaComponente();
        return slct.toString();
    }

    public String getSelectOrdenacaoSel() {
        return this.getSelectOrdenacaoSel(this.fields);
    }

    public String getSelectOrdenacaoSel(List<Field> campos) {
        List<Field> cmps = new ArrayList<>();
        int cont = 0;
        for (int pos = 1; pos < campos.size() + 1; pos++) {
            for (Object o : campos) {
                Field campo = (Field) o;
                if (campo != null && (campo.getOrder() == pos || campo.getAccumulatedOrder() == pos))
                    cmps.set(cont++, campo);
            }
        }
        Select slct = new Select();
        slct.setComponentParameter("class=\"fundoAzulLargura\"");
        slct.setName("lstDimSel");
        slct.setSize(8);
        slct.setComponentParameter("multiple");

        for (Field cmp : cmps) {
            if (cmp != null) {
                if (cmp.getOrder() != 0) {
                    slct.addElement(cmp.getCode() + "[" + cmp.getOrderDirection() + "]",
                            cmp.getTitle() + "[" + cmp.getOrderDirection() + "]", false);
                }
                if (cmp.getAccumulatedOrder() != 0 && cmp.getAccumulatedLine().equals("S")) {
                    slct.addElement("Acum" + cmp.getCode() + "[" + cmp.getAccumulatedOrderDirection() + "]",
                            "Acumulado (" + cmp.getTitle() + ")" + "[" + cmp.getAccumulatedOrderDirection() + "]", false);
                }
            }
        }
        return slct.toString();
    }

    public String getSelectValoresDisp() {
        List<Field> cmps = new ArrayList<>();
        int cont = 0;
        for (Field field : fields)
            if (field != null && field.getFieldType().equals("V") && field.getDefaultField().equals("N"))
                cmps.set(cont++, field);
        BIComparator.classificacao(cmps, Constants.TITLE);

        SelectFields slct = new SelectFields(cmps);
        slct.setComponentParameter("style=\"overflow: auto;\" class=\"fundoAzulLargura\" onFocus=\"selecionado(this);\" onDblClick=\"editaField(this);\"");
        slct.setName("lstValDisp");
        slct.setSize(8);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.addClasseTipoField(Constants.METRIC, "cor");
        slct.montaComponente();
        return slct.toString();
    }

    public String getSelectValoresSel() {
        List<Field> cmps = new ArrayList<>();

        int cont = 0;
        for (Field field : fields)
            if (field != null && field.getFieldType().equals("V") && field.getDefaultField().equals("S"))
                cmps.set(cont++,field);
        BIComparator.classificacao(cmps, Constants.VISUALIZATION_SEQUENCE);

        SelectFields slct = new SelectFields(cmps);
        slct.setComponentParameter("class=\"fundoAzulLargura\"");
        slct.setName("lstValSel");
        slct.setSize(16);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.montaComponente();
        return slct.toString();
    }

    public String getSelectFieldsSel() {
        List<Field> cmps = new ArrayList<>();

        int cont = 0;
        for (Field field : fields) {
            if (field != null && !field.getDefaultField().equals("N") && !"N�o visualizado".equals(field.getTitle())) {
                cmps.set(cont++, field);
            }
        }
        this.ordenaFieldsPorSequenciaVisualizacao(cmps);

        SelectFields slct = new SelectFields(cmps, false);
        slct.setComponentParameter("style=\"overflow: auto;\" class=\"fundoAzulLargura\" onFocus=\"selecionado(this);\"  onDblClick=\"editaField(this);\"");
        slct.setName("lstCmpSel");
        slct.setSize(18);
        slct.setActive(true);
        slct.setMultipleSelection(true);
        slct.addClasseTipoField(Constants.DIMENSION, "odd");
        slct.addClasseTipoField(Constants.METRIC, "cor");
        slct.montaComponente();
        return slct.toString();
    }

    public String getExpressaoFiltrosMetrica() throws BIException {
        String filtrosMetricas = "";
        if (this.filters != null && this.getMetricFilters() != null) {
            String toStringComCodigo = this.getMetricFilters().toStringWithCode();
            if (toStringComCodigo != null) {
                filtrosMetricas = toStringComCodigo;
            }
        }
        String filtrosFuncao = "";
        if (this.getFiltersFunction() != null) {
            filtrosFuncao = this.filtersFunction.toString(!filtrosMetricas.isEmpty());
        }
        return (filtrosMetricas + filtrosFuncao);
    }

    public String getExpressaoFiltrosMetricaSql() throws BIException {
        String filtrosMetricas = "";
        if (this.filters != null && this.filters.getMetricSqlFilter() != null) {
            String toStringComCodigo = this.filters.getMetricSqlFilter().toStringWithCode();
            if (toStringComCodigo != null) {
                filtrosMetricas = toStringComCodigo;
            }
        }
        return (filtrosMetricas);

    }

    private ExpressionCondition getConditionExpressao(String condicao) {
        condicao = condicao.trim();
        int indexInicialOperatorCondicional = ConditionalExpression.getIndiceInicialOperadorCondicional(condicao);
        int indexFinalOperatorCondicional = ConditionalExpression.getIndiceFinalOperadorCondicional(condicao);

        String primeiraParte = condicao.substring(0, indexInicialOperatorCondicional);
        String operadorCondicional = condicao.substring(indexInicialOperatorCondicional, indexFinalOperatorCondicional);
        String segundaParte = condicao.substring(indexFinalOperatorCondicional);

        Expression expPrimeiraParte = new Expression(this.convertePartesExpressaoDeStringParaArray(primeiraParte));
        Expression expSegundaParte = new Expression(this.convertePartesExpressaoDeStringParaArray(segundaParte));
        Operator operador = new Operator(operadorCondicional);
        return new ExpressionCondition(expPrimeiraParte, operador, expSegundaParte);
    }

    public ConditionalExpression getExpressaoCondicional(String expressao) {
        expressao = expressao.trim();
        if (expressao.toUpperCase().trim().indexOf("SE(") == 0 || expressao.toUpperCase().trim().indexOf("IF(") == 0) {
            expressao = expressao.substring(3);
        }
        if (expressao.lastIndexOf(")") > 0) {
            expressao = expressao.substring(0, expressao.lastIndexOf(")"));
        }

        String parteCondicional = expressao.substring(0, expressao.indexOf(";"));
        String parteTrue = expressao.substring(expressao.indexOf(";") + 1, expressao.lastIndexOf(";"));
        String parteFalse = expressao.substring(expressao.lastIndexOf(";") + 1);

        ExpressionCondition expParteCondicional = this.getConditionExpressao(parteCondicional);
        Expression expParteTrue = new Expression(this.convertePartesExpressaoDeStringParaArray(parteTrue));
        Expression expParteFalse = new Expression(this.convertePartesExpressaoDeStringParaArray(parteFalse));

        return new ConditionalExpression(expParteCondicional, expParteTrue, expParteFalse);
    }

    private ArrayList<Object> convertePartesExpressaoDeStringParaArray(String expressao) {
        ArrayList<Object> partes_expressao = new ArrayList<Object>();

        String codigo_campo_aux = "";
        String str_constante = "";
        int nivel_parenteses_1 = 1;
        int nivel_parenteses_2 = 1;
        int nivel_parenteses_3 = 1;
        int nivel_parenteses_4 = 1;
        int nivel_parenteses_5 = 1;

        int num_abertos = 0;
        String nivel_aux = "";

        char[] array_partes_expressao = expressao.toCharArray();

        for (int i = 0; i < array_partes_expressao.length; i++) {
            if (array_partes_expressao[i] == '(') {
                num_abertos++;

                if (num_abertos == 1) {
                    nivel_aux = String.valueOf(nivel_parenteses_1);
                    nivel_parenteses_1++;
                } else if (num_abertos == 2) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + nivel_parenteses_2;
                    nivel_parenteses_2++;
                } else if (num_abertos == 3) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + (nivel_parenteses_2 - 1) + "." + nivel_parenteses_3;
                    nivel_parenteses_3++;
                } else if (num_abertos == 4) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + (nivel_parenteses_2 - 1) + "." + (nivel_parenteses_3 - 1) + "." + nivel_parenteses_4;
                    nivel_parenteses_4++;
                } else if (num_abertos == 5) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + (nivel_parenteses_2 - 1) + "." + (nivel_parenteses_3 - 1) + "."
                            + (nivel_parenteses_4 - 1) + "." + nivel_parenteses_5;
                    nivel_parenteses_5++;
                }

                partes_expressao.add(new Parenthesis(Parenthesis.ABRE, nivel_aux));

            } else if (array_partes_expressao[i] == ')') {
                if (!str_constante.isEmpty()) {
                    partes_expressao.add(str_constante);
                    str_constante = "";
                }
                if (num_abertos == 1) {
                    nivel_aux = String.valueOf(nivel_parenteses_1 - 1);
                    nivel_parenteses_2 = 1;
                } else if (num_abertos == 2) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + (nivel_parenteses_2 - 1);
                    nivel_parenteses_3 = 1;
                } else if (num_abertos == 3) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + (nivel_parenteses_2 - 1) + "." + (nivel_parenteses_3 - 1);
                    nivel_parenteses_4 = 1;
                } else if (num_abertos == 4) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + (nivel_parenteses_2 - 1) + "." + (nivel_parenteses_3 - 1) + "."
                            + (nivel_parenteses_4 - 1);
                    nivel_parenteses_5 = 1;
                } else if (num_abertos == 5) {
                    nivel_aux = (nivel_parenteses_1 - 1) + "." + (nivel_parenteses_2 - 1) + "." + (nivel_parenteses_3 - 1) + "."
                            + (nivel_parenteses_4 - 1) + "." + (nivel_parenteses_5 - 1);
                }

                num_abertos--;
                partes_expressao.add(new Parenthesis(Parenthesis.FECHA, nivel_aux));

            } else if (array_partes_expressao[i] == '+' || array_partes_expressao[i] == '-' || array_partes_expressao[i] == '*'
                    || array_partes_expressao[i] == '/') {
                if (!str_constante.isEmpty()) {
                    partes_expressao.add(str_constante);
                    str_constante = "";
                }
                partes_expressao.add(new ArithmeticOperator(String.valueOf(array_partes_expressao[i])));
            } else if (array_partes_expressao[i] == '#') {
                if (!str_constante.isEmpty()) {
                    partes_expressao.add(str_constante);
                    str_constante = "";
                }
                if (array_partes_expressao[i + 1] == '$') {
                    codigo_campo_aux = expressao.substring(i + 2, expressao.indexOf("$!", i));

                    partes_expressao.add(this.getFieldPorCodigo(codigo_campo_aux));
                    i = i + codigo_campo_aux.length() + 3;
                }
            } else if (array_partes_expressao[i] == '0' || array_partes_expressao[i] == '1' || array_partes_expressao[i] == '2'
                    || array_partes_expressao[i] == '3' || array_partes_expressao[i] == '4' || array_partes_expressao[i] == '5'
                    || array_partes_expressao[i] == '6' || array_partes_expressao[i] == '7' || array_partes_expressao[i] == '8'
                    || array_partes_expressao[i] == '9' || array_partes_expressao[i] == ',' || array_partes_expressao[i] == '.') {
                str_constante += array_partes_expressao[i];
                if (i == array_partes_expressao.length - 1) {
                    partes_expressao.add(str_constante);
                }
            } else if (array_partes_expressao[i] == ' ') {
                if (!str_constante.isEmpty()) {
                    partes_expressao.add(str_constante);
                    str_constante = "";
                }
            }
        }
        return partes_expressao;
    }

    public Expression getExpressaoField(String expressao) {
        return this.getExpressao(expressao);
    }

    private Expression getExpressao(String expressao) {
        ArrayList<Object> partes_expressao = this.convertePartesExpressaoDeStringParaArray(expressao);
        return new Expression(partes_expressao);
    }

    public boolean validaFieldsExpressao(String expressao) {
        expressao = expressao.trim();

        if (expressao.toUpperCase().indexOf("SE(") == 0 || expressao.toUpperCase().indexOf("IF(") == 0) {
            ConditionalExpression obj_expressaoCondicional = this.getExpressaoCondicional(expressao);
            return this.validaFieldsExpressaoCondicional(obj_expressaoCondicional);
        } else {
            Expression obj_expressao = this.getExpressao(expressao);
            return this.validaFieldsExpressao(obj_expressao);
        }
    }

    public boolean validaFieldsExpressaoCondicional(ConditionalExpression expressaoCondicional) {
        ExpressionCondition expParteCondicional = expressaoCondicional.getExpConditionalPart();
        Expression expParteTrue = expressaoCondicional.getExpPartTrue();
        Expression expParteFalse = expressaoCondicional.getExpPartFalse();

        Expression primeiroAtributoCondition = expParteCondicional.getFirstAttribute();
        Expression segundoAtributoCondition = expParteCondicional.getSecondAttribute();

        if (!validaFieldsExpressao(primeiroAtributoCondition)) {
            return false;
        }
        if (!validaFieldsExpressao(segundoAtributoCondition)) {
            return false;
        }

        boolean eh_expressaoCondicionalData;
        Field campo_aux;
        if (primeiroAtributoCondition.getExpressionParts().size() == 1) {
            if (primeiroAtributoCondition.getParteExpressao(0) != null && primeiroAtributoCondition.getParteExpressao(0) instanceof Field) {
                campo_aux = (Field) primeiroAtributoCondition.getParteExpressao(0);

                eh_expressaoCondicionalData = campo_aux.getDataType().equalsIgnoreCase("D");
            } else {
                eh_expressaoCondicionalData = false;
            }
        } else {
            eh_expressaoCondicionalData = false;
        }

        if (segundoAtributoCondition.getExpressionParts().size() == 1) {
            if (segundoAtributoCondition.getParteExpressao(0) != null) {
                if (segundoAtributoCondition.getParteExpressao(0) instanceof Field) {
                    campo_aux = (Field) segundoAtributoCondition.getParteExpressao(0);

                    if (!campo_aux.getDataType().equalsIgnoreCase("D")) {
                        if (eh_expressaoCondicionalData) {
                            return false;
                        }
                    }
                } else {
                    if (eh_expressaoCondicionalData) {
                        return false;
                    }
                }
            } else {
                if (eh_expressaoCondicionalData) {
                    return false;
                }
            }
        } else {
            if (eh_expressaoCondicionalData) {
                return false;
            }
        }

        if (!validaFieldsExpressao(expParteTrue)) {
            return false;
        }
        return validaFieldsExpressao(expParteFalse);
    }

    public boolean validaFieldsExpressao(Expression expressao) {
        ArrayList<Object> partes_expressao;
        Object obj_parte;
        boolean eh_expressaoData = false;
        if (expressao != null) {
            partes_expressao = expressao.getExpressionParts();
            if (partes_expressao != null) {
                for (int i = 0; i < partes_expressao.size(); i++) {
                    obj_parte = partes_expressao.get(i);
                    if (obj_parte != null && !(obj_parte instanceof ArithmeticOperator)) {
                        if (obj_parte instanceof Field) {
                            if (((Field) obj_parte).getDataType().equalsIgnoreCase("D") && !eh_expressaoData && i == 0) {
                                eh_expressaoData = true;
                            } else if (((Field) obj_parte).getDataType().equalsIgnoreCase("D") && !eh_expressaoData) {
                                return false;
                            } else if (!((Field) obj_parte).getDataType().equalsIgnoreCase("D") && eh_expressaoData) {
                                return false;
                            }
                        } else if (obj_parte instanceof Expression) {
                            if (eh_expressaoData) {
                                return false;
                            } else {
                                if (!validaFieldsExpressao((Expression) obj_parte)) {
                                    return false;
                                }
                            }
                        } else if (obj_parte instanceof String) {
                            if (eh_expressaoData) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean necessitaAgregacao(String strExpressao) {
        if (strExpressao.toUpperCase().indexOf("SE(") == 0 || strExpressao.toUpperCase().indexOf("IF(") == 0) {
            return false;
        }

        Expression expressao = this.getExpressao(strExpressao);
        for (Object obj_parte : expressao.getExpressionParts()) {
            if (obj_parte instanceof Field) {
                if (((Field) obj_parte).getDataType().equalsIgnoreCase("D")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getTableAgregacao(String tipoTabela, int codigoAlterar) throws BIException {
        StringBuilder str_table = new StringBuilder();
        str_table.append("<table style='width:100%;'>").append("\n");
        List<String> v = new ArrayList<>();
        v.add("NORMAL");
        v.add("ALTERAR");
        ValidateParameters val = new ValidateParameters();
        val.add("tipoTabela", tipoTabela, true, v);
        val.verifica_parametros();
        List<Field> listaFields = BIUtil.getFieldList(fields);
        if (tipoTabela.equals("NORMAL")) {
            for (Field campo : listaFields)
                if (campo != null && campo.getFieldType().equals("V")) {
                    str_table.append("<tr>").append("\n")
                            .append("<td style='padding: 2px; width:45%; white-space:nowrap; text-align: center;' class='oddPreto'><b>").append(campo.getTitle()).append("</b></td>").append("\n")
                            .append("<td style='padding: 2px; width:45%; white-space:nowrap; text-align: center;' class='oddPreto'><b>").append(campo.getAggregationType()).append("</b></td>").append("\n")
                            .append("<td style='padding: 2px; width:10%; white-space:nowrap; text-align: center;' class='oddPreto'><a href=\"javascript:alterar_Agregacao(")
                            .append(campo.getCode()).append(")\"><img src='./imagens/mini-lapis.png' alt='Alterar este Filtros.' border='0'></a></td>").append("\n")
                            .append("</tr>").append("\n");
                }
        } else if (tipoTabela.equals("ALTERAR")) {
            for (Field campo : listaFields) {
                if (campo == null || !campo.getFieldType().equals("V"))
                    continue;
                if (codigoAlterar == campo.getCode()) {
                    str_table.append("<tr> ").append("\n")
                            .append("<td style='padding: 2px; width:45%; white-space:nowrap; text-align: center;' class='even'>").append("\n")
                            .append("<input type='text' name='txtField' size='10' maxlength='10' value='").append(campo.getTitle()).append("' disabled class='fundoAzul'>").append("\n")
                            .append("</td>").append("\n")
                            .append("<td style='padding: 2px; width:45%; white-space:nowrap; text-align: center;' class='even'>").append("\n")
                            .append(getSelectAgregacao(campo.getAggregationType())).append("\n")
                            .append("</td>").append("\n")
                            .append("<td class='even' style='width:10%; white-space:nowrap; text-align: center;'><a href=\"javascript:salvar_Agregacao(").append(campo.getCode()).append(")\" class='blueLink'>Salvar</a></td>").append("\n")
                            .append("</tr>").append("\n");
                } else {
                    str_table.append("<tr>").append("\n")
                            .append("<td style='padding: 2px; width:45%; white-space:nowrap; text-align: center;' class='oddPreto'><b>").append(campo.getTitle()).append("</b></td>").append("\n")
                            .append("<td style='padding: 2px; width:45%; white-space:nowrap; text-align: center;' class='oddPreto'><b>").append(campo.getAggregationType()).append("</b></td>").append("\n")
                            .append("<td style='padding: 2px; width:10%; white-space:nowrap; text-align: center;' class='oddPreto'><a href=\"javascript:alterar_Agregacao(")
                            .append(campo.getCode()).append(")\"><img src='./imagens/mini-lapis.png' alt='Alterar este Filtros.' border='0'></a></td>").append("\n")
                            .append("</tr>").append("\n");
                }
            }
        }
        str_table.append("\t\t</table>").append("\n");
        return str_table.toString();
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setCode(String code) throws BIException {
        if (code != null && !code.isEmpty()) {
            this.code = Integer.parseInt(code);
        } else {
            throw new BIGeneralException("Erro ao atribuir o código do indicador, parâmetro sem valor.");
        }
    }

    public static String getSelectAgregacao(String tipoAgregacao) throws BIException {
        List<Hashtable<String, String>> list_agregacao = BIUtil.getAggregationList();
        SelectHashtable slct = new SelectHashtable(list_agregacao, "agregacao", "descricao");
        slct.setComponentParameter("class='fundoAzul'");
        slct.setName("selAgregacao");
        slct.setSelectedValue(tipoAgregacao);
        slct.montaComponente();
        return slct.toString();
    }

    public void atualizaOrdenacao(String lista_dimensoes) throws BIException {
        List<String> list_ordem;
        String aux = "";
        String campo_ordem = "";
        String ordenacao = "";
        list_ordem = BIUtil.stringtoList(lista_dimensoes);
        for (Field campo : this.fields) {
            if (campo != null) {
                campo.setOrder(0);
                campo.setAccumulatedOrder(0);
            }
        }
        if (list_ordem != null) {

            for (int i = 0; i < list_ordem.size(); i++) {
                aux = list_ordem.get(i);
                if (aux.contains("Acum")) {
                    aux = aux.substring(4);
                    campo_ordem = aux.substring(0, aux.indexOf("["));
                    int indField = getFieldIndex(campo_ordem);
                    ordenacao = aux.substring(aux.indexOf("[") + 1, aux.indexOf("]"));
                    fields.get(indField).setAccumulatedOrder(i + 1);
                    fields.get(indField).setAccumulatedOrderDirection(ordenacao);
                } else {
                    campo_ordem = aux.substring(0, aux.indexOf("["));
                    int indField = getFieldIndex(campo_ordem);
                    ordenacao = aux.substring(aux.indexOf("[") + 1, aux.indexOf("]"));
                    fields.get(indField).setOrder(i + 1);
                    fields.get(indField).setOrderDirection(ordenacao);
                }
            }
        }
    }

    public void addFiltro(Field campo, String operador, String valor) throws BIException {
        this.filters.addFilter(campo, operador, valor);
    }

    public boolean verificaColunasDimensao() {
        Field camp = null;
        int cont = 0;
        for (Field field : fields) {
            if (field != null && field.getFieldType().equals("D") && field.getDefaultField().equals("S")) {
                camp = field;
                cont++;
            }
        }
        if (cont == 1) {
            return camp.getDrillDownSequence() >= 1;
        }
        return false;
    }

    public Field buscaFieldPelaOrdem(int ordem) {
        for (Field campo : this.fields) {
            if (campo != null && campo.getOrder() == ordem) {
                return campo;
            }
        }
        return null;
    }

    public void incrementaOrdem(Field campo) {
        Field campoAux = buscaFieldPelaOrdem(campo.getOrder() + 1);
        if (campoAux != null) {
            incrementaOrdem(campoAux);
        }
        campo.setOrder(campo.getOrder() + 1);
    }

    public void adicionaUnicaOrdenacaoColunas(String codField, String ordenacao) {
        Field campo = this.getFieldPorCodigo(codField);
        if (campo != null) {
            for (Field campoAux : this.fields) {
                if (campoAux != null) {
                    campoAux.setOrder(0);
                }
            }
            campo.setOrder(1);
            campo.setOrderDirection(ordenacao);
        }
    }

    public void salvarCasasDecimais(String campos, String decimais) throws BIException {
        if (campos != null && !campos.isEmpty() && decimais != null && !decimais.isEmpty()) {
            List<String> list_campos;
            List<String> list_decimais;
            list_campos = BIUtil.stringtoList(campos);
            list_decimais = BIUtil.stringtoList(decimais);
            for (int i = 0; i < list_campos.size(); i++) {
                Field campo = this.getFieldPorCodigo(list_campos.get(i));
                campo.setNumDecimalPositions(Integer.parseInt(list_decimais.get(i)));
            }
        } else {
            throw new BIGeneralException("Erro ao salvar casa decimais, parâmetros sem valor.");
        }
    }

    public String getConfiguraCasasDecimais() throws BIException {
        StringBuilder componente = new StringBuilder();
        List<Field> cmpAux = new ArrayList<>();
        int cont = 0;
        for (Field field : fields)
            if (field != null && field.getFieldType().equals("V") && field.getDefaultField().equals("S")) {
                cmpAux.set(cont, field);
                cont++;
            }

        for (int x = 0; x < cmpAux.size(); x++) {
            if (cmpAux.get(x) == null)
                continue;
            componente.append("<tr>").append("\n")
                    .append("<td class='texto' align='center' width='30%' nowrap>").append("\n")
                    .append("<input type='hidden' name='").append(cmpAux.get(x).getCode()).append("' value='").append(cmpAux.get(x).getTitle()).append("'>").append("\n")
                    .append(cmpAux.get(x).getTitle())
                    .append("</td>").append("\n")
                    .append("<td class='text' style='text-align: center; white-space: nowrap; width:20%;'>").append("\n")
                    .append("<select name='selCasasDecimais").append(x).append("' size='1' class='fundoAzul'>").append("\n");
            for (int i = 0; i <= 10; i++)
                if (i == cmpAux.get(x).getNumDecimalPositions())
                    componente.append("<option value='").append(i).append("' selected>").append(BIUtil.getDescNumber(i)).append("</option>").append("\n");
                else
                    componente.append("<option value='").append(i).append("'>").append(BIUtil.getDescNumber(i)).append("</option>").append("\n");
            componente.append("</select>").append("\n")
                    .append("</td>").append("\n")
                    .append("</tr>").append("\n");
        }
        return componente.toString();
    }

    public Field buscaFieldPorNome(String apelidoTabela, String nome) throws BIException {
        for (Field campo : this.fields) {
            if (campo != null) {
                if (apelidoTabela != null && !apelidoTabela.isEmpty()) {
                    if (campo.getTableNickName() != null && !campo.getTableNickName().trim().isEmpty()) {
                        if (campo.getTableNickName().equals(apelidoTabela) && campo.getName().equals(nome)) {
                            return campo;
                        }
                    } else {
                        if (campo.getName().equals(apelidoTabela + "." + nome))
                            return campo;
                    }
                } else {
                    if (campo.getName().equals(nome))
                        return campo;
                }
            }
        }
        BIGeneralException biex = new BIGeneralException("Nao foi possivel encontrar o campo " + apelidoTabela + "." + nome + " na Análise cod: " + this.code + " - " + this.name);
        biex.setAction("buscar campo");
        biex.setLocal(getClass(), "buscaFieldPorNome(String, String)");
        throw biex;
    }
    
    public String getUsuarioConexao() {
        // TODO get from deb using this.connectionId
        return "";
    }

    public String getBanco() throws BIException {
        // Get this from DB using this.connectionId
        return "MYSQL";
    }

    public boolean isCaseSensitiveConnection() {
        try {
            return false; // TODO gwt this from db connection
            //return config.getConexao(this.connectionId).getCaseSensitive().equals("true");
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isMultidimensional() {
        return isMultidimensional;
    }

    public void addFieldExcluido(Field campo) {
        if (this.deletedFields == null) {
            this.deletedFields = new ArrayList<>();
        }
        this.deletedFields.add(campo);

        List<Field> campos = this.fields;
        campos.remove(this.buscaIndiceField(campo.getCode()));

        this.fields = new ArrayList<>();

    }

    public void addCampoExcluido(Field campo) {
        if (this.deletedFields == null) {
            this.deletedFields = new ArrayList<>();
        }
        this.deletedFields.add(campo);

        List<Field> camposList = new ArrayList<>(this.fields);
        camposList.removeIf(c -> c.getCode() == campo.getCode());

        this.fields = new ArrayList<>(camposList);
    }

    public ConnectionBean getParametrosConexao() throws BIException {
        try {
            return new ConnectionBean(this.connectionId);
        } catch (Exception e) {
            throw new BIException(e);
        }
    }

    public String getExpressaoSQLIndicator() throws BIException {
        this.setClausulas();
        String ligacoesTabela;
        if (this.whereClause != null && this.whereClause.getLigacoesTabela() != null
                && !"".equalsIgnoreCase(this.whereClause.getLigacoesTabela().trim()))
            ligacoesTabela = this.whereClause.getLigacoesTabela();
        else
            ligacoesTabela = "WHERE 1=1\n";

        String clausulaTabela = "";
        if (this.fromClause != null && this.fromClause.getCondicaoTabela() != null) {
            clausulaTabela = this.fromClause.getCondicaoTabela();
        }

        String sql = searchClause.trim() + "\n" + clausulaTabela.trim() + "\n" + ligacoesTabela.trim();

        if (this.fixedConditionClause != null && this.fixedConditionClause.getCondicaoFixa() != null
                && !"".equalsIgnoreCase(this.fixedConditionClause.getCondicaoFixa().trim())) {
            sql += "\n" + this.fixedConditionClause.getCondicaoFixa().trim();
        }

        if (filters.getDimensionFilter() != null) {
            String filtroDimensao = filters.getDimensionFilter().toString();
            if (filtroDimensao != null) {
                filtroDimensao = filtroDimensao.trim();
                sql += "\nAND " + filtroDimensao;
            }
        }

        String filtroMetricaSql = "";
        if (filters.getMetricSqlFilter() != null && filters.getMetricSqlFilter().toString() != null) {
            filtroMetricaSql = "\n" + filters.getMetricSqlFilter().toStringWithAggregation(false).trim();
        }

        String filtroMetrica = "";
        if (filters.getMetricFilters() != null && filters.getMetricFilters().toString() != null) {
            filtroMetrica = filters.getMetricFilters().toString().trim();
        }

        sql += filtroMetricaSql;

        if (this.restrictions != null) {
            String expressaoRestricoes = "";
            if (this.userId == null) {
                expressaoRestricoes = this.restrictions.getExpressaoRestricaoUsuarioConectado().trim();
            } else {
                expressaoRestricoes = this.restrictions.getExpressaoRestricaoUsuario(this.userId).trim();
            }

            if (!expressaoRestricoes.isEmpty()) {
                sql += "\n" + expressaoRestricoes;
            }
        }
        if (!this.groupClause.trim().isEmpty()) {
            sql += "\n" + this.groupClause.trim();
        }

        if (!this.orderClause.trim().isEmpty()) {
            sql += "\n" + this.orderClause.trim();
        }
        sql += "\n" + filtroMetrica;

        if (this.getBanco().equals("OPENEDGE") || isCaseSensitiveConnection()) {
            return quoteSQLString(sql, "\"");
        } else {
            return sql.trim();
        }
    }

    public String quoteSQLString(String conn, String quote) {

        String[] fixecConn = conn.trim().replaceAll("\\n", " ").replaceAll(" +", " ").split("((?<=[\\s,.)(=\\-+/*])|(?=[\\s,.)(=\\-+/*]))");
        List<String> sqlWords = Arrays.asList("?", "<", ">", "=", "-", "+", "/", "*", "}", "{", "]", "[", ")", "(", ",", " ", ".", "MAX", "MIN", "SUM", "AND", "AS", "ASC", "BETWEEN", "COUNT", "BY", "CASE", "CURRENT_DATE", "CURRENT_TIME", "DELETE", "DESC", "INSERT", "UPDATE", "DISTINCT", "EACH", "ELSE", "ELSEIF", "FALSE", "FOR", "TOP", "FROM", "GROUP", "HAVING", "IF", "IN", "INSERT", "INTERVAL", "INTO", "IS", "INNER", "JOIN", "KEY", "KEYS", "LEFT", "LIKE", "LIMIT", "MATCH", "NOT", "NULL", "ON", "OPTION", "OR", "ORDER", "OUT", "OUTER", "REPLACE", "RIGHT", "SELECT", "SET", "TABLE", "THEN", "TO", "TRUE", "UPDATE", "VALUES", "WHEN", "THEN", "WHERE", "DATE", "DECIMAL", "ELSE", "EXISTS", "FOR", "VARCHAR", "UNION", "GROUP", "WITH");
        StringBuilder retorno = new StringBuilder();

        for (String word : fixecConn) {
            if (!sqlWords.contains(word.toUpperCase()) && !StringUtils.isNumeric(word)) {
                word = quote + word + quote;
            }
            retorno.append(word);
        }

        return retorno.toString();
    }

    public ArrayList<Field> getListaDimensoes() {
        return this.getListaFields(this.fields, Constants.DIMENSION);
    }

    public ArrayList<Field> getListaMetricas() {
        return this.getListaFields(this.fields, Constants.METRIC);
    }

    private ArrayList<Field> getListaFields(List<Field> campos, String tipoField) {
        ArrayList<Field> retorno = new ArrayList<>();
        for (int i = 0; i < campos.size(); i++) {
            if (campos.get(i) == null)
                continue;
            if (campos.get(i).getFieldType().equals(tipoField)
                    && !(campos.get(i).getTitle().equalsIgnoreCase("Não visualizado") && campos.get(i).isFixedValue())) {
                retorno.add(this.fields.get(i));
            }
        }
        return retorno;
    }

    public boolean hasAllExpressionFields(Field[] campos, Field campo) {
        boolean retorno = false;
        if (campo != null && campo.isExpression()
                && (campo.getName().toUpperCase().trim().indexOf("SE(") == 0 || campo.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
            ConditionalExpression expressaoCondicional = this.getExpressaoCondicional(campo.getName());

            if (expressaoCondicional != null
                    && expressaoCondicional.getExpConditionalPart().getFirstAttribute().temTodosFieldsExpressao(campos, campo)
                    && expressaoCondicional.getExpConditionalPart().getSecondAttribute().temTodosFieldsExpressao(campos, campo)) {
                if (expressaoCondicional.getExpPartTrue().temTodosFieldsExpressao(campos, campo)
                        && expressaoCondicional.getExpPartFalse().temTodosFieldsExpressao(campos, campo)) {
                    retorno = true;
                }
            }
        } else {
            retorno = this.getExpressaoField(campo.getName()).temTodosFieldsExpressao(campos, campo);
        }
        return retorno;
    }

    public List<Field> getFieldsOrdenacaoAcumulado() {
        List<Field> listFields = new ArrayList<>();
        List<Field> camposOriginais = this.getFields();
        for (int cont = 1; cont < this.getFields().size(); cont++) {
            for (int i = 0; i < this.getFields().size(); i++) {
                if (camposOriginais.get(i) != null && camposOriginais.get(i).getDefaultField().equals("S")) {
                    if (camposOriginais.get(i).getAccumulatedOrder() != 0 && camposOriginais.get(i).getAccumulatedOrder() == cont) {
                        listFields.add(camposOriginais.get(i));
                    }
                }
            }
        }
        return listFields;
    }

    public void atualizaFiltroClone(Field[] campos, Filters filtros) {
        if (this.filters != null && this.filters.getMetricFilters() != null) {
            for (int j = 0; j < filtros.getMetricFilters().size(); j++) {
                for (Field campo : campos) {
                    MetricFilter filtroMetrica = filtros.getMetricFilters().get(j);
                    if (filtroMetrica.getField().getCode() == campo.getCode()) {
                        filtroMetrica.getCondition().setField(campo);
                        break;
                    }
                }
            }
        }
    }

    public void atualizaFiltroFuncaoClone(Field[] campos, FiltersFunction filtrosFuncao) {
        if (filtrosFuncao != null && filtrosFuncao.getFilterAccumulated() != null) {
            for (Field campo : campos) {
                FilterAccumulated filtroAcumulado = filtrosFuncao.getFilterAccumulated();
                if (filtroAcumulado.getField().getCode() == campo.getCode()) {
                    filtroAcumulado.setField(campo);
                    break;
                }
            }
        }
    }

    public boolean ehFieldUltimaColunaLinhaTabela(int localExibicao, String tipoField) {
        boolean retorno = false;
        int contador = 0;
        for (Field field : this.fields) {
            if (field != null) {
                if (field.getDisplayLocation() == localExibicao && field.getFieldType().equalsIgnoreCase(tipoField)) {
                    if (field.getDefaultField().equals("S")) {
                        contador++;
                    }
                }
            }
        }
        if (contador <= 1) {
            retorno = true;
        }
        return retorno;
    }

    public void setTodasMetricasTotalizaField() {
        for (Field field : fields) {
            if (field != null && "S".equals(field.getDefaultField()) && Constants.METRIC.equals(field.getFieldType())) {
                field.setTotalizingField("S");
            }
        }
    }

    public FiltersFunction getFiltersFunction() {
        if (this.filtersFunction == null) {
            this.filtersFunction = new FiltersFunction();
        }
        return this.filtersFunction;
    }

    public ArrayList<ValuesRepository> getAccumulatedValuesRepository() {
        if (accumulatedValuesRepository == null) {
            this.accumulatedValuesRepository = new ArrayList<>();
        }
        return accumulatedValuesRepository;
    }

    public ValuesRepository getRepositorioValoresField(int codigoField) {
        if (this.accumulatedValuesRepository != null) {
            for (ValuesRepository repositorioValores : this.accumulatedValuesRepository) {
                if (codigoField == repositorioValores.getFieldCode()) {
                    return repositorioValores;
                }
            }
        }
        return null;
    }

    public MetricDimensionRestrictions getMetricDimensionRestrictions() {
        if (metricDimensionRestrictions == null) {
            this.metricDimensionRestrictions = new MetricDimensionRestrictions();
        }
        return metricDimensionRestrictions;
    }

    public ColorsAlert getColorAlerts() {
        if (colorAlerts == null) {
            this.colorAlerts = new ColorsAlert();
        }
        return colorAlerts;
    }

    public void setColorAlerts(ColorsAlert colorAlerts) {
        if (this.colorAlerts == null) {
            this.colorAlerts = new ColorsAlert();
        }
        this.colorAlerts = colorAlerts;
    }

    public void startFieldsCalculationByRestriction() {
        for (Field campo : this.fields) {
            if (campo != null && "T".equals(campo.getDefaultField()) && campo.isCalculatorPerRestriction()) {
                campo.setDefaultField("S");
                campo.setCalculatorPerRestriction(false);
            }
        }
    }

    public Field getFieldDimensaoUltimoNivel(List<Field> camposVerificar) {
        Field campo = null;
        for (Field campoAux : camposVerificar) {
            if (campoAux != null) {
                if ("S".equals(campoAux.getDefaultField()) && campoAux.getDisplayLocation() == Constants.COLUMN
                        && !(campoAux.getTitle().equalsIgnoreCase("Não visualizado") && campoAux.isFixedValue())) {
                    if (campo == null) {
                        campo = campoAux;
                    }
                    if (campoAux.getDrillDownSequence() > campo.getDrillDownSequence()) {
                        campo = campoAux;
                    }
                }
            }
        }
        return campo;
    }

    public int getLastLevelDimensionCode() {
        int retorno = 0;
        Field campo = null;
        List<Field> camposNaoDrillDown = this.getTodosFieldsDimensaoNaoDrillDown();
        if (!camposNaoDrillDown.isEmpty()) {
            campo = this.getFieldDimensaoUltimoNivel(camposNaoDrillDown);
        }
        if (campo != null) {
            retorno = campo.getCode();
        } else {
            campo = this.getFieldDimensaoUltimoNivel(this.getTodosFieldsDimensaoDrillDown());
            if (campo != null) {
                retorno = campo.getCode();
            }
        }
        return retorno;
    }

    public static boolean existeMaisQueUmaAnalise(String nomeAnalise, int repositorio) throws BIException {
        // TODO search analise in db
        return false;
    }

    public static boolean existeNomeAnaliseRepositorio(String nomeAnalise, int repositorio) throws BIException {
        // TODO search analise in db
        return false;
    }

    public boolean contemField(Field campo) {
        return this.contemField(this.fields, campo);
    }

    private boolean contemField(List<Field> campos, Field campo) {
        for (Field cp : campos) {
            if (cp != null && cp.getCode() == campo.getCode()) {
                return true;
            }
        }
        return false;
    }

    public List<String> getIndicatoresFilhosNaoReplicouFields() {
        if (this.indicadoresFilhosNaoReplicouFields == null) {
            this.indicadoresFilhosNaoReplicouFields = new ArrayList<>();
        }
        return indicadoresFilhosNaoReplicouFields;
    }

    public void atualizaPermissoes() throws BIException {
        analysisUserPermission permissaoUsuarioAnalise = new analysisUserPermission();
        analysisUserPermissions = permissaoUsuarioAnalise.consultaPermissoesIndicador(this.originalCode);
    }

    private List<Field> getFieldsDrillDown() {
        List<Field> retorno = new ArrayList<>();
        for (Field campo : this.fields) {
            if (campo != null) {
                if (campo.getFieldType().equals(Constants.DIMENSION) && campo.isDrillDown()
                        && !(campo.getTitle().equalsIgnoreCase("Não visualizado") && campo.isFixedValue())) {
                    retorno.add(campo);
                }
            }
        }
        return retorno;
    }

    public List<Field> getFieldsDrillDownOrdenados() {
        List<Field> camposDrillDown = this.getFieldsDrillDown();
        DrillDownComparator drillDownComparator = new DrillDownComparator();
        camposDrillDown.sort(drillDownComparator);
        return camposDrillDown;
    }

    public List<Field> getDimensoesSemDrillDown() {
        List<Field> retorno = new ArrayList<Field>();
        for (Field campo : this.fields) {
            if (campo != null) {
                if (campo.getFieldType().equals(Constants.DIMENSION) && !campo.isDrillDown()
                        && !(campo.getTitle().equalsIgnoreCase("Não visualizado") && campo.isFixedValue())) {
                    retorno.add(campo);
                }
            }
        }
        return retorno;
    }

    private Field procuraFieldFiltro(int filtro) {
        for (Field campo : this.fields) {
            if (campo.getGeneralFilter() == filtro) {
                return campo;
            }
        }
        return null;
    }

    public void geraDimensionFilterSemField(Field campo) {
        this.geraDimensionFilterSemField(campo.getCode());
    }

    public void geraDimensionFilterSemField(int codField) {
        if (this.getDimensionFilter() != null) {
            this.getDimensionFilter().removePerField(codField);
        }
    }

    public void geraMetricFilterSemField(int codigoField) {
        if (this.getMetricFilters() != null) {
            for (int i = 0; i < this.getMetricFilters().size(); i++) {
                MetricFilter filtroMetricaAux = this.getMetricFilters().get(i);
                if (filtroMetricaAux.getField().getCode() == codigoField) {
                    this.getMetricFilters().remove(filtroMetricaAux);
                }
            }
        }
    }

    public void limpa() {
        this.cubo = null;
        if (this.scheduled) {
            this.tableRecords = null;
        }
    }

    public final int getPanelIndex() {
        return panelIndex;
    }

    public final void setPanelIndex(int panelIndex) {
        this.panelIndex = panelIndex;
    }

    public boolean stopProcess() {
        return this.cuboListener.stopProcess();
    }

    private CachedRowSet getRegistrosAnaliseParaCongelar() throws BIException {
        CachedRowSet cachedrowset;
        ConnectionBean conexao = this.getNovaConexaoIndicator();
        ResultSet results;
        PreparedStatement stmtResults;

        String sql = this.getExpressaoSQLIndicator();

        try {
            stmtResults = conexao.prepareStatement(sql);
            this.applyValues(stmtResults, sql);
            conexao.setDirtyRead();
            results = stmtResults.executeQuery();
            cachedrowset = RowSetProvider.newFactory().createCachedRowSet();
            cachedrowset.populate(results);
            return cachedrowset;
        } catch (SQLException e) {
            throw new BISQLException(e, "Não foi possivel armazenar os registros do banco para esta análise congelada");
        }
    }

}
