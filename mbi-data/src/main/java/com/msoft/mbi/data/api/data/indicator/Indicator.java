package com.msoft.mbi.data.api.data.indicator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.generation.*;
import com.msoft.mbi.cube.multi.metadata.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metadata.HTMLLineMask;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.metadata.CubeMetaData;
import com.msoft.mbi.cube.multi.renderers.*;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnText;
import com.msoft.mbi.cube.util.CubeListener;
import com.msoft.mbi.cube.util.DefaultCubeListener;
import com.msoft.mbi.cube.util.logicOperators.LogicalOperators;
import com.msoft.mbi.data.api.data.consult.CachedResults;
import com.msoft.mbi.data.api.data.consult.ConsultResult;
import com.msoft.mbi.data.api.data.exception.BIDatabaseException;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIGeneralException;
import com.msoft.mbi.data.api.data.exception.DateException;
import com.msoft.mbi.data.api.data.filters.*;
import com.msoft.mbi.data.api.data.filters.interpreters.DimensionFilterInterpreter;
import com.msoft.mbi.data.api.data.filters.interpreters.MetricFilterInterpreter;
import com.msoft.mbi.data.api.data.htmlbuilder.*;
import com.msoft.mbi.data.api.data.util.BIData;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import com.msoft.mbi.data.api.data.util.ValuesRepository;
import com.msoft.mbi.model.support.DatabaseType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
@Setter
@Data
@Log4j2
@SuppressWarnings("unused")
public class Indicator {

    public Indicator() throws BIException {
        this.mapper = new ObjectMapper();
        temporaryFrozenStatus = false;
        frozenStatus = false;
        this.analysisGroupPermissions = new ArrayList<>();
        this.analysisUserPermissions = new ArrayList<>();
        this.analysisComments = new AnalysisComments();
        this.fields = new ArrayList<>();
        this.filters = new Filters();
        this.filtersFunction = new FiltersFunction();
    }

    public Indicator(int code) throws BIException {
        this();
        this.code = code;
    }

    private int code;
    private String name;
    private int areaCode;
    private int companyId;
    private List<Field> fields;
    private Filters filters;
    private FiltersFunction filtersFunction;
    private AnalysisComments analysisComments;
    private List<AnalysisUserPermission> analysisUserPermissions;
    private List<AnalysisGroupPermissions> analysisGroupPermissions;
    private String fileName;
    private String searchClause;
    private String fixedConditionClause;
    private String fromClause;
    private String whereClause;
    private String groupClause;
    private String orderClause;
    private String dimensionFilters;
    private String metricSqlFilters;
    private String metricFilters;
    private boolean scheduled = false;
    private Integer scheduledCode = null;
    private String filterTable;
    private boolean temporaryFrozenStatus;
    private boolean frozenStatus;
    private int panelCode;
    private CachedResults tableRecords;
    private ConsultResult[] tableResults;
    private String connectionId;
    private DatabaseType databaseType;
    private UUID tenantId;
    private boolean multidimensional;
    private String currentView = "T";
    private String dateFormat = "dd/MM/yyyy";

    private CubeListener cubeListener = new DefaultCubeListener();

    private int leftCoordinates = 10;
    private int topCoordinates = 60;
    private int height = 0;
    private int width = 0;
    private boolean isMaximized = true;
    private boolean isOpen = true;
    private Restrictions restrictions;
    private PartialTotalizations partialTotalizations;
    private boolean usesSequence = false;
    private int tableType;
    private ValuesRepository sequeceValuesRepository;
    // private ArrayList<ValuesRepository> accumulatedValuesRepository; // TODO: CODE THIS WHEN IMPLEMENTIND ANALYSIS PERMISSIONS
    private List<MetricDimensionRestriction> metricDimensionRestrictions;
    private ColorsAlert colorAlerts;
    private List<Field> dimensionColumn;
    private int panelIndex;
    private boolean hasData;

    private int originalCode;
    private Integer originalIndicator;
    private boolean inheritsRestrictions = false;
    private boolean inheritsFields = false;
    private boolean replicateChanges;

    private AnalysisParameters analysisParameters;
    private Map<Field, MetaDataField> biCubeMappedFields;

    private Cube cube;
    private TableGenerator cubeTable;

    private final ObjectMapper mapper;

    public static final int DEFAULT_TABLE = 1;
    public static final int MULTIDIMENSIONAL_TABLE = 2;

    public String getSqlExpression(DatabaseType databaseType, boolean caseSensitive) throws BIException {
        this.setClauses();

        StringBuilder sqlBuilder = new StringBuilder();

        sqlBuilder.append(this.searchClause)
                .append("\n")
                .append(this.fromClause)
                .append("\n")
                .append(this.whereClause);

        this.appendClause(sqlBuilder, this.fixedConditionClause);
        this.appendClause(sqlBuilder, this.dimensionFilters);
        
        if (this.metricSqlFilters != null && !this.metricSqlFilters.isEmpty()) {
            sqlBuilder.append("\n").append(this.filters.getMetricSqlFilter().toStringWithAggregation(false).trim());
        }

        // User restrictions can be built here if needed

        this.appendClause(sqlBuilder, this.groupClause);
        this.appendClause(sqlBuilder, this.orderClause);
        this.appendClause(sqlBuilder, this.metricFilters);
        
        String sql = sqlBuilder.toString().trim();

        if (databaseType.equals(DatabaseType.OPENEDGE) || caseSensitive) {
            return this.applyValues(BIUtil.quoteSQLString(sql, "\""));
        } else {
            return this.applyValues(sql);
        }
    }

    public String applyValues(String query) throws BIException {

        if (query != null) {
            query = (new DimensionTextFilter(this.filters.getDimensionFilter())).applyValues(query, 0);
            query = this.applyMetricSqlValues(query);

            if (this.restrictions != null) {
                query = this.restrictions.applyRestrictionValues(query);
            }
            query = this.applyMetricValues(query);
        }
        return query;
    }

    private String applyMetricValues(String query) throws BIException {
        if (this.filters != null && this.filters.getMetricFilters() != null) {
            for (MetricFilter metricFilter : this.filters.getMetricFilters()) {
                MetricTextFilter metricTextFilter = new MetricTextFilter(metricFilter);
                query = metricTextFilter.applyValues(query, 0);
            }
        }
        return query;
    }

    private String applyMetricSqlValues(String query) throws BIException {
        if (this.filters != null && this.filters.getMetricSqlFilter() != null) {
            for (MetricFilter metricFilter : this.filters.getMetricSqlFilter()) {
                MetricTextFilter metricTextFilter = new MetricTextFilter(metricFilter);
                query = metricTextFilter.applyValues(query, 0);
            }
        }
        return query;
    }
    
    private void appendClause(StringBuilder sql, String clause) {
        if (clause != null && !clause.isEmpty()) {
            sql.append("\n").append(clause);
        }
    }

    public void orderFieldsByVisualizationSequence(List<Field> fields) {
        List<Field> sequenceZeroFields = fields.stream()
                .filter(field -> field != null && field.getVisualizationSequence() == 0)
                .sorted(Comparator.comparingInt(Field::getFieldId))
                .collect(Collectors.toList());

        List<Field> nonZeroSequenceFields = fields.stream()
                .filter(field -> field != null && field.getVisualizationSequence() != 0)
                .sorted(Comparator.comparingInt(Field::getVisualizationSequence))
                .toList();

        sequenceZeroFields.addAll(nonZeroSequenceFields);
        fields.clear();
        fields.addAll(sequenceZeroFields);
    }

    public boolean isFieldUsedToDelegateOrder(Field field) {
        if (field == null) return false;
        Integer fieldCode = field.getFieldId();

        return this.fields.stream()
                .filter(Objects::nonNull)
                .map(Field::getDelegateOrder)
                .anyMatch(order -> order != null && order.equals(fieldCode));
    }

    public void setClauses() throws BIException {
        StringBuilder select = new StringBuilder("SELECT ");
        StringBuilder groupBy = new StringBuilder();
        StringBuilder orderBy = new StringBuilder();

        this.orderFieldsByVisualizationSequence(this.fields);

        for (Field field : this.fields) {
            processFieldForClause(field, select, groupBy);
        }

        if (!groupBy.isEmpty()) {
            groupBy.insert(0, "GROUP BY ");
        }

        BIUtil.removeTrailingComma(select);
        BIUtil.removeTrailingComma(groupBy);
        BIUtil.removeTrailingComma(orderBy);

        this.searchClause = select.toString();
        this.groupClause = groupBy.toString();
        this.orderClause = orderBy.toString();

    }

    private void processFieldForClause(Field field, StringBuilder select, StringBuilder groupBy) throws BIException {

        if (field == null){
            return;
        }

        String aggregationType = field.getAggregationType();
        String fieldType = field.getFieldType();

        if (!"S".equals(field.getDefaultField()) && (fieldType.equals("D") && !isFieldUsedToDelegateOrder(field))) {
            return;
        }

        if (Constants.DIMENSION.equals(fieldType) && !"COUNT".equalsIgnoreCase(aggregationType) && !"COUNT_DIST".equalsIgnoreCase(aggregationType)) {
            if (field.isFixedValue()) {
                select.append(field.getName()).append(" ").append(field.getNickname().trim()).append(",");
            } else {
                select.append(field.getSqlExpressionWithNickName());
                if (!field.isExpression() || "EMPTY".equalsIgnoreCase(aggregationType) && !field.getTableNickname().isEmpty()) {
                    groupBy.append(field.getSqlExpressionWithoutNickName()).append(",");
                }
            }
        } else if (!field.isFixedValue()) {
            if (field.isExpression()) {
                if (!isConditionalExpression(field.getName())) {
                    String exp = convertExpressionFromCodeToNickName(field.getName(), !aggregationType.equals("EMPTY"));
                    if (!exp.toUpperCase().trim().contains("SE(") && !exp.toUpperCase().trim().contains("IF(")) {
                        if (aggregationType.equalsIgnoreCase("COUNT_DIST")) {
                            select.append("COUNT(DISTINCT ").append(exp).append(") ");
                        } else {
                            select.append(aggregationType).append("(").append(exp).append(") ");
                        }
                        select.append(field.getNickname().trim()).append(",");
                    }
                }
            } else {
                if ("EMPTY".equalsIgnoreCase(aggregationType)) {
                    select.append(field.getSqlExpressionWithNickName());
                    groupBy.append(field.getSqlExpressionWithoutNickName()).append(",");
                } else {
                    if ("COUNT_DIST".equalsIgnoreCase(aggregationType)) {
                        select.append("COUNT(DISTINCT ").append(field.getSqlExpressionWithoutNickName()).append(") ");
                    } else {
                        select.append(aggregationType).append("(").append(field.getSqlExpressionWithoutNickName()).append(") ");
                    }
                    select.append(field.getNickname().trim()).append(",");
                }
            }
        }
    }



    private boolean isConditionalExpression(String fieldName) {
        return fieldName.toUpperCase().trim().startsWith("SE(") || fieldName.toUpperCase().trim().startsWith("IF(");
    }

    public String convertExpressionFromCodeToNickName(String expression, boolean allFieldsWithNoAggregation) throws BIException {
        StringBuilder result = new StringBuilder(expression);
        String pattern = "#$";
        String endPattern = "$!";

        while (result.indexOf(pattern) != -1 && result.indexOf(endPattern) != -1) {
            int startIndex = result.indexOf(pattern);
            int endIndex = result.indexOf(endPattern);
            String strCodeTempField = result.substring(startIndex + pattern.length(), endIndex);

            Optional<Field> optionalField = this.fields.stream()
                    .filter(field -> Objects.equals(String.valueOf(field.getFieldId()), strCodeTempField))
                    .findFirst();

            if (optionalField.isPresent()) {
                Field tempField = optionalField.get();
                String replacement;
                if (tempField.isExpression()) {
                    boolean includeAggregation = allFieldsWithNoAggregation || "EMPTY".equals(tempField.getAggregationType());
                    replacement = "(" + convertExpressionFromCodeToNickName(tempField.getName(), includeAggregation) + ")";
                } else {
                    if (allFieldsWithNoAggregation || "EMPTY".equals(tempField.getAggregationType())) {
                        replacement = tempField.getSqlExpressionWithoutNickName();
                    } else {
                        if ("COUNT_DIST".equalsIgnoreCase(tempField.getAggregationType())) {
                            replacement = "COUNT(DISTINCT " + tempField.getSqlExpressionWithoutNickName() + ")";
                        } else {
                            replacement = tempField.getAggregationType() + "(" + tempField.getSqlExpressionWithoutNickName() + ")";
                        }
                    }
                }
                result.replace(startIndex, endIndex + endPattern.length(), replacement);
            } else {
                return "O campo com código " + strCodeTempField + " é inexistente.";
            }
        }
        return result.toString();
    }

    public Field getFieldByCode(String code) {
        if (code != null && !code.trim().isEmpty()) {
            return this.getFieldByCode(Integer.parseInt(code));
        }
        return null;
    }

    public MetricDimensionRestriction getRestrictionByFieldId(int code) {
        return this.metricDimensionRestrictions.stream()
                .filter(restriction -> restriction != null && restriction.getMetricId() == code)
                .findFirst().orElse(null);
    }

    public Field getFieldByCode(int code) {
        return this.fields.stream()
                .filter(field -> field != null && field.getFieldId() == code)
                .findFirst().orElse(null);
    }

    public Field getFieldByName(String tableNickname, String name) throws BIException {
        return this.fields.stream()
                .filter(Objects::nonNull)
                .filter(field -> field.getName().equals(name) &&
                        (StringUtils.isEmpty(tableNickname) ||
                                (field.getTableNickname() != null &&
                                        (field.getTableNickname().equals(tableNickname) || field.getName().equals(tableNickname + "." + name)))))
                .findFirst()
                .orElseThrow(() -> {
                    BIGeneralException exception = new BIGeneralException("Field " + tableNickname + "." + name + " not found in Analysis code: " + this.code + " - " + this.name);
                    exception.setAction("search field");
                    exception.setLocal(getClass(), "getFieldByName(String, String)");
                    return exception;
                });
    }

    public void setDimensionFilters(String dimensionFilterClause) throws BIException {
        if (StringUtils.isNotBlank(dimensionFilterClause)) {

            Pattern pattern = Pattern.compile("^\\s*(AND\\s+)?", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(dimensionFilterClause);

            dimensionFilterClause = matcher.replaceFirst("");

            if (this.filters != null) {
                DimensionFilterInterpreter filterInterpreter = new DimensionFilterInterpreter(dimensionFilterClause, this);
                this.filters.setDimensionFilter(filterInterpreter.getFilter());
                this.dimensionFilters = "AND " + this.filters.getDimensionFilter().toString().trim();
            }
        }
    }

    public void setMetricFilters(String metricFilterClause) throws BIException {
        if (StringUtils.isNotBlank(metricFilterClause)) {
            MetricFilterInterpreter interpreter = new MetricFilterInterpreter(metricFilterClause, this);
            this.filters.setMetricFilters(interpreter.getFilters());

            this.addFilterIfPresent(interpreter.hasSequenceFilters(), interpreter.getFilterSequence());
            this.addFilterIfPresent(interpreter.hasAccumulatedFilters(), interpreter.getFilterAccumulated());
            this.metricFilters = this.filters.getMetricFilters().toString().trim();
        }
    }

    private void addFilterIfPresent(boolean condition, FilterFunction filter) {
        if (condition) {
            if (this.filtersFunction == null) {
                this.filtersFunction = new FiltersFunction();
            }
            this.filtersFunction.addFilter(filter);
        }
    }

    public void setMetricSqlFilters(String havingClause) throws BIException {
        if (havingClause != null && !havingClause.trim().isEmpty()) {
            MetricFilterInterpreter interpreter = new MetricFilterInterpreter(havingClause, this);
            filters.setMetricSqlFilter(interpreter.getFilters());
            this.metricSqlFilters = this.filters.getMetricSqlFilter().toStringWithAggregation(false).trim();
        }
    }

    public void startTableProcess() throws BIGeneralException {
        try {
            this.montaSaida(true);
        } catch (Exception e) {
            throw new BIGeneralException(e);
        }
    }

    public String getStringTable(boolean montaSemLink) throws BIException {
        StringWriter out = new StringWriter();

        Object tabelaDrillUp = "&nbsp;";

        if (!montaSemLink) {
            if (this.multidimensional) {
                tabelaDrillUp = HtmlHelper.buildStringDrillUp(this.dimensionColumn.get(0).isDrillUp());
            } else {
                tabelaDrillUp = this.buildDefaultStringDrillUp();
            }
        }

        HtmlHelper.buildStringTitleAndDrillUp(out, montaSemLink, tabelaDrillUp, this.name);

        this.cubeTable.process(new PrinterHTML(out, (!montaSemLink)));
        return out.toString();

    }

    public ObjectNode getJsonTable(boolean montaSemLink) {

        ArrayNode drillUpTable = this.mapper.createArrayNode();
        if (!montaSemLink) {
            if (this.multidimensional) {
                drillUpTable = this.buildJsonDrillUp();
            } else {
                drillUpTable = this.buildDefaultJsonDrillUp();
            }
        }

        ObjectNode jsonTitle = this.buildJsonTitleAndDrillUp(montaSemLink, drillUpTable);
        ObjectNode objectNode = this.mapper.createObjectNode();
        JsonPrinter jsonPrinter = new JsonPrinter(objectNode);

        long startTime = System.nanoTime();

        this.cubeTable.process(jsonPrinter);

        long elapsedTime = System.nanoTime() - startTime;
        log.info("Total execution time to cubeTable.processar in millis: " + elapsedTime/1000000);

        objectNode.set("title", jsonTitle);

        return objectNode;

    }

    public void montaSaida(boolean consult) throws BIGeneralException {
        if (consult) {
            try {
                this.cubeListener.start();
                buildTableData();
                stopProcess();
            } finally {
                this.cubeListener.finish();
            }
        }
    }

    private void buildTableData() throws BIGeneralException {
        try {
            determineMultiDimensionality();
            configureCubeAndTable();
        } catch (BIException e) {
            throw new BIGeneralException(e);
        } catch (DateException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayNode buildJsonDrillUp() {

        ArrayNode arrayNode = this.mapper.createArrayNode();

        ObjectNode json = this.mapper.createObjectNode();

        json.put("id", "opcao_drill_up");

        if (this.dimensionColumn.get(0).isDrillUp()) {
            json.put("image", "vect-arrow-square-up-left");
            json.put("hint", "Drill Up");
            json.put("onclick", "addDrillUp(0)");
        }

        arrayNode.add(json);

        return arrayNode;
    }


    public ArrayNode buildDefaultJsonDrillUp() {

        ArrayNode arrayNode = this.mapper.createArrayNode();
        ObjectNode json = this.mapper.createObjectNode();


        if (checkColumnsDimension()) {

            BIDriller bi = new BIDriller();
            Field campoAux = bi.searchCurrentField(this);
            Field prox = null;
            if (campoAux != null) {
                prox = bi.getNextDrillDownSequence(campoAux, this.fields);
            }
            json.put("editable", false);

            if (prox == null) {
                json.put("onclick", "'addDrillUp" + this.getCode() + ")' class='vect-arrow-square-up-left' title='Drill-Up'");
            } else {
                BIDriller di = new BIDriller();
                Field aux = di.searchCurrentField(this);
                json.put("onclick", "'addDrillUp(" + this.getCode() + ")' class='vect-arrow-square-up-left'");
                json.put("onclick", "'DrillDownSemFiltro(" + aux.getFieldId() + ")' class='vect-arrow-square-down-right' title='Descer um nível sem utilizar Drill-Down'");
            }
        }
        arrayNode.add(json);
        return arrayNode;
    }

    public String buildDefaultStringDrillUp() {
        if (!checkColumnsDimension()) {
            return "";
        }
        return HtmlHelper.buildDefaultStringDrillUp(this);
    }

    private ObjectNode buildJsonTitleAndDrillUp(boolean noLinks, ArrayNode drillUpTable) {

        ObjectNode objectNode = this.mapper.createObjectNode();

        objectNode.set("style", this.buildJsonTitle());
        objectNode.set("drillUp", drillUpTable);

        return objectNode;
    }

    public ObjectNode buildJsonTitle() {

        ObjectNode json = this.mapper.createObjectNode();

        json.put("tableId","indicator_" + this.code);
        json.put("width","100px");
        json.put("editable",true);
        json.put("alignment", CellProperty.ALIGNMENT_CENTER);
        json.put("titleId","title_" + this.code);
        json.put("fontStyle","normal");
        json.put("fontFamily","verdana");
        json.put("fontSize","14px");
        json.put("backgroundColor","#FFFFFF");
        json.put("fontWeight","bold");
        json.put("fontColor","#000080");

        json.put("textDecoration","none");
        json.put("cursor","pointer");
        json.put("content",this.name);

        return json;
    }

    private void determineMultiDimensionality() {
        try {
            if (this.getTableType() == 0 || this.getTableType() == 2) {
                validateMultiDimensionalTable();
                this.multidimensional = true;
            } else {
                this.multidimensional = false;
            }
        } catch (Exception e) {
            this.multidimensional = false;
        }
    }

    private void configureCubeAndTable() throws BIException, DateException {
        if (this.multidimensional) {
            orderFieldsPerDrillDownSequence();
            this.dimensionColumn = this.loadFields(Constants.DIMENSION, Constants.COLUMN);
            if (this.stopProcess())
                return;
            configureFieldsNavigation(this.dimensionColumn);
            this.cube = Cube.factoryMultiDimensionalCube(createCubeMetaData());
            this.cubeTable = new MultiDimensionalDefaultTableBuilder(this.cube);
        } else {
            this.cube = Cube.factoryDefaultCube(createStandardMetaDataCube());
            this.cubeTable = new DefaultTableBuilder(this.cube);
        }
        configureCubeListener();
    }

    private void configureCubeListener() {
        if (this.cube != null && this.cubeListener != null) {
            this.cube.setCubeListener(new CubeProcessor(this));
        }
    }

    public int getMaxDrillDownSequence() {
        return this.fields.stream()
                .filter(field -> field != null && field.isDrillDown())
                .mapToInt(Field::getDrillDownSequence)
                .max()
                .orElse(0);
    }

    private CubeMetaData createStandardMetaDataCube() throws BIException {
        CubeMetaData cubeMetaData = new CubeMetaData();
        loadDefaultFieldRegisters();
        int lastDrillDownSequence = getMaxDrillDownSequence();

        MetaDataField firtsColumn = null;

        for (Field field : fields) {
            if (isFieldValid(field)) {
                MetaDataField metaDataField = createCampoMetaData(field);
                configureCampoMetaData(field, metaDataField, lastDrillDownSequence);
                cubeMetaData.addField(metaDataField, field.getFieldType());

                if ("S".equals(field.getDefaultField()) && this.usesSequence && firtsColumn == null) {
                    firtsColumn = metaDataField;
                    configurePrimerColumn(firtsColumn);
                }
            }
        }

        createCubeMetadataMetricFilterRestrictions(cubeMetaData);
        return cubeMetaData;
    }

    private boolean isFieldValid(Field field) {
        return field != null && !(field.getTitle().equalsIgnoreCase("Não visualizado") && field.isFixedValue());
    }

    private void configurePrimerColumn(MetaDataField firstColumn) {
        firstColumn.setShowSequence(true);
        FilterSequence filterSequence = getFiltersFunction().getFilterSequence();
        if (filterSequence != null) {
            firstColumn.setRankingExpression(filterSequence.toString());
        }
    }

    private MetaDataField createCampoMetaData(Field field) {
        MetaDataField dataCubeField = createMetaDataCubeField(field);
        dataCubeField.setSequence(field.getVisualizationSequence());
        return dataCubeField;
    }

    private void configureCampoMetaData(Field field, MetaDataField metaDataField, int lastDrillDownSequence) {
        HtmlHelper.configureSorting(field, metaDataField);
        if (field.isDrillDown() && field.getDrillDownSequence() != lastDrillDownSequence) {
            HtmlHelper.createHTMLDrillDownMask(field, metaDataField, this.code);
        }
        configureAlertColor(field, metaDataField);
    }

    private void configureAlertColor(Field field, MetaDataField metaDataField) {
        int alertSequence = 1;
        for (LineColor lineColor : field.getLineColors()) {
            if (lineColor != null) {
                String backGroundColor = lineColor.getBackGroundColor().startsWith("#") ? lineColor.getBackGroundColor().substring(1) : lineColor.getBackGroundColor();
                String fontColor = lineColor.getFontColor().startsWith("#") ? lineColor.getFontColor().substring(1) : lineColor.getFontColor();
                ColorAlertMetadata alertColor = new ColorAlertMetadata(alertSequence++, LogicalOperators.BETWEEN_INCLUSIVE,
                        Double.parseDouble(lineColor.getInitialValue()), Double.parseDouble(lineColor.getInitialValue()), fontColor, backGroundColor,
                        "Verdana", false, false, 10);
                metaDataField.addColorAlert(alertColor, ColorAlertMetadata.VALUE_ALERT_TYPE);
            }
        }
    }

    public void processCube(ResultSet resultSet) throws BIException {
        try {
            long startTime = System.nanoTime();

            this.cube.process(resultSet);

            long elapsedTime = System.nanoTime() - startTime;
            log.info("Total execution time to cubo.processarCubo in millis: " + elapsedTime/1000000);
        } catch (SQLException sqle) {
            BIDatabaseException biex = new BIDatabaseException(sqle);
            biex.setAction("Processamento do Cubo.");
            biex.setLocal(getClass(), "processarCubo(CachedRowSet)");
            throw biex;
        }
    }

    private void loadMultiDimensionalFieldRegisters() {
        this.loadFieldRegisters(this.getLastLevelDimensionCode());
    }

    private void loadDefaultFieldRegisters() {
        this.loadFieldRegisters(-1);
    }

    public void loadFieldRegisters(int dimensionField) {
        startFieldsCalculationByRestriction();
        for (Field field : this.fields) {
            if (field != null && !field.getDefaultField().equals("N") && field.getFieldType().equals(Constants.METRIC)) {

                MetricDimensionRestriction restMetDim = this.getRestrictionByFieldId(field.getFieldId());
                if (restMetDim != null && restMetDim.isRestrictMetric(this, dimensionField)) {
                    field.setDefaultField("T");
                    field.setCalculatorPerRestriction(true);
                }
            }
        }
    }

    public boolean checkColumnsDimension() {
        long countDimensionFields = fields.stream()
                .filter(field -> field != null && field.getFieldType().equals("D") && field.getDefaultField().equals("S"))
                .count();

        Field validField = fields.stream()
                .filter(field -> field != null && field.getFieldType().equals("D") && field.getDefaultField().equals("S"))
                .findFirst()
                .orElse(null);

        return countDimensionFields == 1 && validField != null && validField.getDrillDownSequence() >= 1;
    }

    private void checkForPartialTotalization() {
        List<Field> metricFields = this.getFieldsPerType(Constants.METRIC);
        List<Field> dimensionFields = this.getFieldsPerType(Constants.DIMENSION);

        boolean isPartialAverage = metricFields.stream().anyMatch(field -> !"N".equalsIgnoreCase(field.getDefaultField()) && field.isPartialMedia());
        boolean isPartialTotalization = metricFields.stream().anyMatch(field -> !"N".equalsIgnoreCase(field.getDefaultField()) && field.isPartialTotalization());
        boolean isPartialExpression = metricFields.stream().anyMatch(field -> !"N".equalsIgnoreCase(field.getDefaultField()) && field.isPartialExpression());
        boolean isPartialTotalExpression = metricFields.stream().anyMatch(field -> !"N".equalsIgnoreCase(field.getDefaultField()) && field.isPartialTotalExpression());

        dimensionFields.forEach(field -> {
            field.setPartialMedia(isPartialAverage && field.isPartialMedia());
            field.setPartialTotalization(isPartialTotalization && field.isPartialTotalization());
            field.setPartialExpression(isPartialExpression && field.isPartialExpression());
            field.setPartialTotalExpression(isPartialTotalExpression && field.isPartialTotalExpression());
        });
    }

    private MetaDataField createMetaDataCubeField(Field field) {
        MetaDataField metaDataField = new MetaDataField();

        if (field == null) {
            return metaDataField;
        }

        metaDataField.setAccumulateLineField("S".equals(field.getAccumulatedLine()));
        metaDataField.setAggregationType(field.getAggregationType());
        metaDataField.setHorizontalAnalysisType(field.getHorizontalAnalysisType());
        metaDataField.setVerticalAnalysisType(field.getVerticalAnalysisType());
        metaDataField.setFieldNickname(field.getNickname());
        metaDataField.setId(field.getFieldId());
        metaDataField.setDrillDown(field.isDrillDown());
        metaDataField.setExpression(field.isExpression());
        metaDataField.setColumnWidth(field.getColumnWidth());
        metaDataField.setDisplayLocation(field.getDisplayLocation() == Constants.LINE ? Constants.COLUMN : Constants.LINE);
        metaDataField.setNullValueMask("-");
        metaDataField.setShowSequence(false);
        metaDataField.setName(field.getName());
        metaDataField.setNumDecimalPositions(field.getNumDecimalPositions());
        metaDataField.setOrder(field.getOrder());
        metaDataField.setAccumulatedOrder(field.getAccumulatedOrder());
        metaDataField.setDefaultField(field.getDefaultField());
        metaDataField.setTitle(field.getTitle());
        metaDataField.setHorizontalParticipation(field.isHorizontalParticipation());
        metaDataField.setAccumulatedParticipation(field.isAccumulatedParticipation());
        metaDataField.setHorizontalAccumulatedParticipation(field.isHorizontalParticipationAccumulated());
        metaDataField.setColumnAlignmentPosition(field.getColumnAlignment());
        metaDataField.setOrderDirection(field.getOrderDirection());
        metaDataField.setAccumulatedOrderDirection(field.getAccumulatedOrderDirection());
        metaDataField.setTotalField(field.isTotalizingField());
        metaDataField.setTotalLineField(field.isSumLine());
        metaDataField.setUsesMediaLine(field.isMediaLine());
        metaDataField.setAccumulatedValue(field.isAccumulatedValue());

        setMetaDataFieldOrder(metaDataField, field);
        BIUtil.setMetaDataFieldMask(metaDataField, field);
        setMetaDataFieldOrderDirection(metaDataField, field);
        setMetaDataFieldLineTotalizationType(metaDataField, field);

        setMetaDataFieldFlags(metaDataField, field);
        setMetaDataFieldDataType(metaDataField, field);

        LinkHTMLText testLink = new LinkHTMLColumnText(metaDataField.getTitle(), metaDataField.getColumnWidth());
        testLink.addParameter("data-code-col", String.valueOf(field.getFieldId()));
        HTMLLineMask  mascaraLinkHTML = new HTMLLineMask("field_maintenance",HTMLLineMask.VALUE_TYPE, testLink);
        metaDataField.addHTMLLineMask(mascaraLinkHTML);

        return metaDataField;
    }

    private void setMetaDataFieldOrder(MetaDataField metaDataField, Field field) {
        if (field.getDelegateOrder() != null) {
            Field campoIndicator = getFieldByCode(field.getDelegateOrder());
            if (campoIndicator != null) {
                metaDataField.setOrderingField(createMetaDataCubeField(campoIndicator));
            }
        }
    }

    private void setMetaDataFieldOrderDirection(MetaDataField metaDataField, Field field) {
        metaDataField.setOrderDirection(field.getOrderDirection().trim().toUpperCase());
        metaDataField.setAccumulatedOrderDirection(field.getAccumulatedOrderDirection() != null ?
                field.getAccumulatedOrderDirection().trim().toUpperCase() : "ASC");
    }

    private void setMetaDataFieldLineTotalizationType(MetaDataField metaDataField, Field field) {
        if (metaDataField.isExpression()) {
            if (field.isApplyTotalizationExpression() || field.isPartialExpression()) {
                metaDataField.setTotalLinesType(MetaDataField.TOTAL_APPLY_EXPRESSION);
            } else {
                metaDataField.setTotalLinesType(MetaDataField.TOTAL_APPLY_SUM);
            }
            if (field.getName().toUpperCase().trim().indexOf("SE(") == 0 || field.getName().toUpperCase().trim().indexOf("IF(") == 0) {
                metaDataField.setAggregationApplyOrder(MetaDataField.AGGREGATION_APPLY_AFTER);
            }
        }
    }

    private void setMetaDataFieldFlags(MetaDataField metaDataField, Field field) {
        boolean isVisible = !"N".equalsIgnoreCase(field.getDefaultField());
        metaDataField.setTotalPartial(field.isPartialTotalization() && isVisible);
        metaDataField.setMediaPartial(field.isPartialMedia() && isVisible);
        metaDataField.setExpressionInPartial(field.isPartialExpression() && isVisible);
        metaDataField.setExpressionInTotalPartial(field.isPartialTotalExpression() && isVisible);
    }

    private void setMetaDataFieldDataType(MetaDataField metaDataField, Field field) {
        String tipoDado = field.getDataType();
        if (Constants.DIMENSION.equals(field.getFieldType()) && Constants.NUMBER.equals(field.getDataType())) {
            tipoDado = MetaDataField.DECIMAL_TYPE;
        }
        metaDataField.setDataType(tipoDado);
    }

    public boolean checkFilters(DimensionFilter dimensionFilter, Field field) {
        if (dimensionFilter == null || field == null || dimensionFilter.getCondition() != null) {
            return false;
        }

        return dimensionFilter.getFilters().stream()
                .filter(Objects::nonNull)
                .filter(filter -> filter.getCondition() == null)
                .anyMatch(filter -> isMatchingFilter(filter, field));
    }

    private boolean isMatchingFilter(DimensionFilter dimensionFilter, Field field) {
        return dimensionFilter.getField() != null
                && dimensionFilter.getField().getNickname().equalsIgnoreCase(field.getNickname())
                && "=".equalsIgnoreCase(dimensionFilter.getOperator().getSymbol());
    }

    public Expression getFieldExpression(String expression) {
        return this.getExpression(expression);
    }

    private Expression getExpression(String expression) {
        List<Object> expressionSlice = StringHelper.convertExpressionFromStringToArray(expression, this);
        return new Expression(expressionSlice);
    }

    private CubeMetaData createCubeMetaData() throws BIException, DateException {
        Map<Field, MetaDataField> biCubeMappedFields = new HashMap<>();
        CubeMetaData cubeMetaData = new CubeMetaData();

        loadMultiDimensionalFieldRegisters();
        checkForPartialTotalization();

        int sequenceDrillDown = Integer.MAX_VALUE;
        int sequence = Integer.MAX_VALUE;
        MetaDataField firtsLineDimensionDrillDown = null;

        int verificationActivity = 0;

        for (Field field : fields) {
            verificationActivity++;
            if (verificationActivity % 100 == 0 && stopProcess()) {
                return null;
            }

            if (!isFieldValid(field)) {
                continue;
            }

            MetaDataField metadataField = createMetaDataCubeField(field);
            String fieldType = field.getFieldType();

            if (Constants.DIMENSION.equals(fieldType)) {
                metadataField.setSequence(field.getDrillDownSequence());
                if (field.getDisplayLocation() == Constants.COLUMN && "S".equals(field.getDefaultField())) {
                    if (field.getDrillDownSequence() < sequenceDrillDown) {
                        firtsLineDimensionDrillDown = metadataField;
                        sequenceDrillDown = field.getDrillDownSequence();
                    }
                    if (field.getVisualizationSequence() < sequence) {
                        sequence = field.getVisualizationSequence();
                    }
                    HtmlHelper.createDimensionLineMask(field, metadataField, this);
                }
            } else {
                metadataField.setSequence(field.getVisualizationSequence());
            }

            cubeMetaData.addField(metadataField, fieldType);
            biCubeMappedFields.put(field, metadataField);
        }

        if (this.usesSequence && firtsLineDimensionDrillDown != null) {
            firtsLineDimensionDrillDown.setShowSequence(true);
            FilterSequence filterSequence = getFiltersFunction().getFilterSequence();
            if (filterSequence != null) {
                firtsLineDimensionDrillDown.setRankingExpression(filterSequence.toString());
            }
        }

        processColorAlerts(biCubeMappedFields);

        createCubeMetadataMetricFilterRestrictions(cubeMetaData);
        return cubeMetaData;
    }

    private void processColorAlerts(Map<Field, MetaDataField> biCubeMappedFields) throws BIException, DateException {
        List<ColorAlert> colorAlertsList = colorAlerts.getColorAlertList();
        for (ColorAlert colorAlert : colorAlertsList) {
            MetaDataField metadataField = biCubeMappedFields.get(colorAlert.getFirstField());
            if (metadataField == null) {
                continue;
            }

            int action = Objects.equals(colorAlert.getAction(), ColorAlert.LINHA) ? ColorAlertMetadata.PAINT_LINE_ACTION : ColorAlertMetadata.PAINT_CELL_ACTION;
            AlertProperty props = colorAlert.getAlertProperty();
            String backgroundColor = props.getCellBackgroundColor().startsWith("#") ? props.getCellBackgroundColor().substring(1) : props.getCellBackgroundColor();
            String fontColor = props.getFontColor().startsWith("#") ? props.getFontColor().substring(1) : props.getFontColor();

            if (!colorAlert.isCompareToAnotherField()) {
                ColorAlertMetadata cubeAlert = createAlertMetaDataCube(colorAlert, action, fontColor, backgroundColor);
                metadataField.addColorAlert(cubeAlert, ColorAlertMetadata.VALUE_ALERT_TYPE);
            } else {
                String value = colorAlert.getFirstDoubleValue();
                if ("".equals(value)) {
                    value = "0.00";
                }
                ColorAlertMetadata alertMetadata = new ColorAlertMetadata(colorAlert.getSequence(), colorAlert.getOperator().getSymbol(),
                        Double.parseDouble(value), action, colorAlert.getFirstFieldFunction(), fontColor, backgroundColor, props.getFontName(),
                        props.hasBold(), props.hasItalic(), props.getFontSize(), colorAlert.getValueType(), colorAlert.getSecondField().getTitle(),
                        colorAlert.getSecondFieldFunction());
                metadataField.addColorAlert(alertMetadata, ColorAlertMetadata.SECOND_FIELD_ALERT_TYPE);
            }
        }
    }

    private void createCubeMetadataMetricFilterRestrictions(CubeMetaData cubeMetaData) throws BIException {
        String metricFilterExpression = buildMetricFiltersExpression();
        cubeMetaData.setMetricFieldsExpression(metricFilterExpression);

        String accumulatedFilterExpression = buildAccumulatedFiltersExpression();
        cubeMetaData.setAccumulatedFieldExpression(accumulatedFilterExpression);
    }

    private String buildMetricFiltersExpression() throws BIException {
        // TODO Validate this when implementing metric filters
        /*MetricFilters metricFilters = this.getMetricFilters();
        if (metricFilters != null) {
            return metricFilters.toStringWhitCode();
        }*/
        return "";
    }

    private String buildAccumulatedFiltersExpression() {
        if (this.filtersFunction == null) {
            return "";
        }

        FilterAccumulated accumulatedFilter = this.filtersFunction.getFilterAccumulated();
        if (accumulatedFilter != null) {
            return accumulatedFilter.toString();
        }
        return "";
    }

    private ColorAlertMetadata createAlertMetaDataCube(ColorAlert colorAlert, int action, String fontColor, String backGroundColor) throws BIException {
        Field field = colorAlert.getFirstField();
        AlertProperty alertProperty = colorAlert.getAlertProperty();

        if (field == null || field.getFieldType().equals(Constants.METRIC)) {
            return createMetricAlertMetaDataCube(colorAlert, alertProperty, action, fontColor, backGroundColor);
        } else if (!field.getDataType().equals(Constants.DATE)) {
            return createNonDateAlertMetaDataCube(colorAlert, alertProperty, action, fontColor, backGroundColor, field);
        } else {
            return createDateAlertMetaDataCube(colorAlert, alertProperty, action, fontColor, backGroundColor, field);
        }
    }

    private ColorAlertMetadata createDateAlertMetaDataCube(ColorAlert colorAlert, AlertProperty alertProperty, int action, String fontColor, String backGroundColor, Field field) throws BIException {
        String firstValue = colorAlert.getFirstValue();
        String secondValue = colorAlert.getSecondValue();
        if (firstValue.trim().startsWith("@|") && firstValue.trim().endsWith("|")) {
            DimensionFilter dimensionFilter = FilterFactory.createDimensionFilter(field, colorAlert.getOperator().getSymbol(), firstValue);
            return createAlertMetaDataCubeWithCondition(colorAlert, alertProperty, dimensionFilter, action, fontColor, backGroundColor);
        } else {
            return createAlertMetaDataCubeWithoutCondition(colorAlert, action, fontColor, backGroundColor, alertProperty, firstValue, secondValue);
        }
    }

    private ColorAlertMetadata createMetricAlertMetaDataCube(ColorAlert colorAlert, AlertProperty alertProperty, int action, String fontColor, String backGroundColor) {
        double firstValue = Double.parseDouble(colorAlert.getFirstDoubleValue());
        double secondValue = colorAlert.getSecondValue() != null ? Double.parseDouble(colorAlert.getSegundoValorDouble()) : 0;
        return this.getColorAlertMetadata(
                colorAlert, alertProperty,
                firstValue, secondValue,
                action, fontColor, backGroundColor
        );
    }

    private ColorAlertMetadata createNonDateAlertMetaDataCube(ColorAlert colorAlert, AlertProperty alertProperty, int action, String fontColor, String backGroundColor, Field field) throws BIException {
        String firstValue = colorAlert.getFirstValue();
        if (firstValue.trim().startsWith("@|") && firstValue.trim().endsWith("|")) {
            DimensionFilter dimensionFilter = FilterFactory.createDimensionFilter(field, colorAlert.getOperator().getSymbol(), firstValue);
            firstValue = dimensionFilter.getCondition().getValue();
        }
        Object valor1 = field.getDataType().equals(Constants.NUMBER) ? Integer.parseInt(firstValue) : firstValue;
        return this.getColorAlertMetadata(
                colorAlert, alertProperty,
                valor1, null,
                action, fontColor, backGroundColor
        );
    }

    private ColorAlertMetadata createAlertMetaDataCubeWithCondition(ColorAlert colorAlert, AlertProperty alertProperty, DimensionFilter dimensionFilter, int action, String fontColor, String backGroundColor) throws BIException {

        Date firstValue = null;
        Date secondValue = null;
        for (DimensionFilter filter : dimensionFilter.getFilters()) {
            if (filter != null && filter.getCondition() != null) {
                Condition condition = new TextCondition(filter.getCondition());
                Date formattedSQLDate = BIUtil.formatSQLDate(condition.getFormattedValue(), BIData.DAY_MONTH_YEAR_4DF);
                if (condition.getOperator().getSymbol().equals(Operators.GREATER_TAN_OR_EQUAL)) {
                    firstValue = formattedSQLDate;
                } else {
                    secondValue = formattedSQLDate;
                }
            }
        }
        colorAlert.getOperator().setSymbol(LogicalOperators.BETWEEN_INCLUSIVE);
        return this.getColorAlertMetadata(
                colorAlert, alertProperty,
                firstValue, secondValue,
                action, fontColor, backGroundColor
        );
    }

    private ColorAlertMetadata createAlertMetaDataCubeWithoutCondition(ColorAlert colorAlert, int action, String fontColor, String backGroundColor, AlertProperty alertProperty, String firstValue, String secondValue) throws BIDatabaseException {
        Date firstValueDate = BIUtil.formatSQLDate(firstValue, BIData.DAY_MONTH_YEAR_4DF);
        Date secondValueDate = colorAlert.getOperator().getSymbol().equals(Operators.BETWEEN) ?
                BIUtil.formatSQLDate(secondValue, BIData.DAY_MONTH_YEAR_4DF) : null;
        return this.getColorAlertMetadata(
                colorAlert, alertProperty,
                firstValueDate, secondValueDate,
                action, fontColor, backGroundColor
        );
    }

    private ColorAlertMetadata getColorAlertMetadata(ColorAlert colorAlert, AlertProperty alertProperty,
                                                     Object firstValueDate, Object secondValueDate, int action, String fontColor, String backGroundColor
    ) {
        return new ColorAlertMetadata(
                colorAlert.getSequence(),
                colorAlert.getOperator().getSymbol(),
                firstValueDate,
                secondValueDate,
                action,
                colorAlert.getFirstFieldFunction(),
                fontColor,
                backGroundColor,
                alertProperty.getFontName(),
                alertProperty.hasBold(),
                alertProperty.hasItalic(),
                alertProperty.getFontSize()
        );
    }


    private void configureFieldsNavigation(List<Field> columnFields) {
        for (Field field : columnFields) {
            int index = IntStream.range(0, this.fields.size())
                    .filter(i -> isValidFieldForDrillUp(i, field))
                    .reduce((a, b) -> b).orElse(-1);

            field.setDrillUp(index != -1 && this.fields.get(index).isDrillDown());

            index = IntStream.range(0, this.fields.size())
                    .filter(i -> isValidFieldForNavigableUpwards(i, field))
                    .findFirst().orElse(-1);

            if (index != -1 && this.fields.get(index).isDrillDown()) {
                field.setNavigableUpwards(true);
                field.setNavigable(!this.fields.get(index).getDefaultField().equals("S"));
            } else {
                field.setNavigableUpwards(false);
                field.setNavigable(false);
            }
        }
    }

    private boolean isValidFieldForDrillUp(int index, Field field) {
        Field currentField = this.fields.get(index);
        return currentField != null &&
                ((currentField.getFieldType().equals(Constants.DIMENSION) &&
                        currentField.getDisplayLocation() == Constants.LINE) ||
                        currentField.getFieldType().equals(Constants.METRIC) ||
                        (currentField.isDrillDown() &&
                                currentField.getDrillDownSequence() >= field.getDrillDownSequence()));
    }

    private boolean isValidFieldForNavigableUpwards(int index, Field field) {
        Field currentField = this.fields.get(index);
        return currentField != null &&
                ((currentField.getFieldType().equals(Constants.DIMENSION) &&
                        currentField.getDisplayLocation() == Constants.LINE) ||
                        currentField.getFieldType().equals(Constants.METRIC) ||
                        (currentField.isDrillDown() &&
                                currentField.getDrillDownSequence() <= field.getDrillDownSequence()));
    }

    public List<Field> getFieldsPerType(String fieldType) {
        return this.fields.stream()
                .filter(field -> field.getFieldType().equals(fieldType))
                .toList();
    }

    private List<Field> getAllDrillDownDimensions() {
        return this.fields.stream()
                .filter(field -> field.getFieldType().equals(Constants.DIMENSION)
                        && field.isDrillDown())
                .toList();
    }

    private List<Field> getAllNonDrillDownDimensions() {
        return this.fields.stream()
                .filter(field -> field.getFieldType().equals(Constants.DIMENSION)
                        && !field.isDrillDown())
                .toList();
    }

    private List<Field> sortDrillDownSequence(List<Field> fields) {
        DrillDownComparator drillDownComparator = new DrillDownComparator();
        return fields.stream().sorted(drillDownComparator).toList();
    }

    public void orderFieldsPerDrillDownSequence() {
        List<Field> metricFields = this.getFieldsPerType(Constants.METRIC);
        List<Field> drillDownFields = getAllDrillDownDimensions();
        List<Field> nonDrillDownFields = getAllNonDrillDownDimensions();

        this.fields.clear();

        this.fields.addAll(sortDrillDownSequence(drillDownFields));
        this.fields.addAll(sortDrillDownSequence(nonDrillDownFields));
        this.fields.addAll(metricFields);

    }

    public boolean stopProcess() {
        return this.cubeListener.stopProcess();
    }

    public void validateMultiDimensionalTable() throws BIException {

        long lineDimensions = this.getVisibleFieldsAmount(Constants.DIMENSION, Constants.LINE);
        long metricColumns = this.getVisibleFieldsAmount(Constants.METRIC, Constants.COLUMN);
        long metricLines = this.getVisibleFieldsAmount(Constants.METRIC, Constants.LINE);

        if (lineDimensions < 1 && this.tableType == 0) {
            throw new BIGeneralException("Deve haver pelo menos uma dimensão nas linhas!");
        }
        if (metricColumns + metricLines < 1) {
            throw new BIGeneralException("Você deve selecionar pelo menos uma métrica!");
        }
        if (metricColumns > 0 && metricLines > 0) {
            throw new BIGeneralException("As métricas devem estar nas colunas OU nas linhas.\nNão podem estar em ambas.");
        }
    }

    private long getVisibleFieldsAmount(String fieldType, int displayLocation) {
        return fields.stream()
                .filter(Objects::nonNull)
                .filter(field -> !"N".equals(field.getDefaultField()))
                .filter(field -> field.getFieldType().equals(fieldType))
                .filter(field -> field.getDisplayLocation() == displayLocation)
                .mapToLong(field -> {
                    long count = 1;
                    if (displayLocation == Constants.LINE && fieldType.equals("M")
                            && (field.isTotalizingField() || field.isAccumulatedParticipation() || field.isVerticalAnalysis()
                                || field.isAccumulatedValue() || field.isHorizontalAnalysis()
                                || field.isHorizontalParticipation() || field.isHorizontalParticipationAccumulated())) {
                        count++;

                    }
                    return count;
                })
                .sum();
    }

    public void startFieldsCalculationByRestriction() {
        fields.stream()
                .filter(Objects::nonNull)
                .filter(field -> "T".equals(field.getDefaultField()) && field.isCalculatorPerRestriction())
                .forEach(field -> {
                    field.setDefaultField("S");
                    field.setCalculatorPerRestriction(false);
                });
    }

    public Field getLastLevelDimensionField(List<Field> fieldsToVerify) {
        return fieldsToVerify.stream()
                .filter(Objects::nonNull)
                .filter(tempField ->
                        "S".equals(tempField.getDefaultField()) &&
                                tempField.getDisplayLocation() == Constants.COLUMN &&
                                isFieldValid(tempField))
                .max(Comparator.comparingInt(Field::getDrillDownSequence))
                .orElse(null);
    }

    public int getLastLevelDimensionCode() {
        List<Field> camposNaoDrillDown = getAllNonDrillDownDimensions();
        List<Field> camposDrillDown = getAllDrillDownDimensions();

        Field lastLevelField = getLastLevelDimensionField(camposNaoDrillDown);
        if (lastLevelField == null) {
            lastLevelField = getLastLevelDimensionField(camposDrillDown);
        }

        return lastLevelField != null ? lastLevelField.getFieldId() : 0;
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
                        MetricDimensionRestriction restMetDim = this.getRestrictionByFieldId(field.getFieldId());
                        if (restMetDim != null && restMetDim.isRestrictMetric(this, lastLevelCode)) {
                            field.setDefaultField("T");
                            field.setCalculatorPerRestriction(true);
                        }
                    }
                })
                .flatMap(field -> fieldBuilder(field, displayLocation, fieldType))
                .toList();
    }

    private Stream<Field> fieldBuilder(Field field, int displayLocation, String fieldType) {
        Stream.Builder<Field> builder = Stream.builder();
        builder.add(field);

        if (displayLocation == Constants.LINE && fieldType.equals("M")) {
            if (field.isTotalizingField()) {
                builder.add(createField(field.getFieldId(), "total", "S", String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
            }
            if (field.isVerticalAnalysis()) {
                builder.add(createChieldField(field.getFieldId(), "%", field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
            }
            if (field.isAccumulatedParticipation()) {
                builder.add(createChieldField(field.getFieldId(), "% Acumulada", field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
            }
            if (field.isAccumulatedValue()) {
                builder.add(createChieldField(field.getFieldId(), field.getTitle() + " Acum.", field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
            }
            if (field.isHorizontalAnalysis()) {
                builder.add(createChieldField(field.getFieldId(), "AH%", field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
            }
            if (field.isHorizontalParticipation()) {
                builder.add(createChieldField(field.getFieldId(), "AH Participação", field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
            }
            if (field.isHorizontalParticipationAccumulated()) {
                builder.add(createChieldField(field.getFieldId(), "AH Participação Acumulada", field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
            }
        }
        return builder.build();
    }

    private Field createField(int code, String title, String defaultField, String columnWidth, String columnAlignment) {
        Field field = new Field();
        field.setFieldId(code);
        field.setName("");
        field.setTitle(title);
        field.setDefaultField(defaultField);
        field.setColumnWidth(columnWidth);
        field.setColumnAlignment(columnAlignment);
        return field;
    }

    private Field createChieldField(int code, String title, String defaultField, String columnWidth, String columnAlignment) {
        Field field = createField(code, title, defaultField, columnWidth, columnAlignment);
        field.setVerticalAnalysis(true);
        field.setChildField(true);
        return field;
    }

    public void addFilter(Field field, String operator, String value) throws BIException {
        this.filters.addFilter(field, operator, value);
    }

    public DimensionFilter getDimensionFilter() {
        return filters.getDimensionFilter();
    }

}
