package com.msoft.mbi.data.api.data.indicator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.cube.multi.Cube;
import com.msoft.mbi.cube.multi.column.MaskColumnMetaData;
import com.msoft.mbi.cube.multi.generation.*;
import com.msoft.mbi.cube.multi.metaData.ColorAlertMetadata;
import com.msoft.mbi.cube.multi.metaData.HTMLLineMask;
import com.msoft.mbi.cube.multi.metaData.MetaDataField;
import com.msoft.mbi.cube.multi.metaData.CubeMetaData;
import com.msoft.mbi.cube.multi.renderers.MaskMonth;
import com.msoft.mbi.cube.multi.renderers.MaskMonthYear;
import com.msoft.mbi.cube.multi.renderers.MaskPeriod;
import com.msoft.mbi.cube.multi.renderers.MaskWeek;
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
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import com.msoft.mbi.data.api.data.util.ValuesRepository;
import com.msoft.mbi.model.support.DatabaseType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
@SuppressWarnings("unused")
public class Indicator {

    public Indicator() throws BIException {
        this.mapper = new ObjectMapper();
        temporaryFrozenStatus = false;
        frozenStatus = false;
        this.analysisGroupPermissions = new ArrayList<>();
        this.AnalysisUserPermissions = new ArrayList<>();
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
    private List<AnalysisUserPermission> AnalysisUserPermissions;
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

    transient private CubeListener cubeListener = new DefaultCubeListener();

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
    // private MetricDimensionRestrictions metricDimensionRestrictions; // TODO: CODE THIS WHEN IMPLEMENTIND ANALYSIS PERMISSIONS
    private ColorsAlert colorAlerts;
    private List<Field> dimensionColumn;
    private int panelIndex;
    private boolean hasData;

    private int originalCode;
    private Integer originalIndicator;
    private boolean inheritsRestrictions = false;
    private boolean inheritsFields = false;
    private boolean replicateChanges;

    transient private AnalysisParameters analysisParameters;
    transient private Map<Field, MetaDataField> BICubeMappedFields;

    transient private Cube cube;
    transient private TableGenerator cubeTable;

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
            return this.applyValues(quoteSQLString(sql, "\""));
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

        this.removeTrailingComma(select);
        this.removeTrailingComma(groupBy);
        this.removeTrailingComma(orderBy);

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

    private void removeTrailingComma(StringBuilder builder) {
        if (!builder.isEmpty() &&  builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
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

    public Field getFieldByCode(int code) {
        return fields.stream()
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
            this.metricFilters = this.filters.getMetricFilters().toString().trim(); //interpreter.getFilters().toString().trim();
        }
    }

    private void addFilterIfPresent(boolean condition, FilterFunction filter) throws BIException {
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

    public String quoteSQLString(String input, String quote) {

        Set<String> sqlKeywords = new HashSet<>(Set.of(
                "?", "<", ">", "=", "-", "+", "/", "*", "}", "{", "]", "[", ")", "(", ",", " ", ".", "MAX", "MIN",
                "SUM", "AND", "AS", "ASC", "BETWEEN", "COUNT", "BY", "CASE", "CURRENT_DATE", "CURRENT_TIME",
                "DELETE", "DESC", "INSERT", "DISTINCT", "EACH", "ELSEIF", "FALSE", "TOP",
                "FROM", "HAVING", "IF", "IN", "INTERVAL", "INTO", "IS", "INNER", "JOIN", "KEY",
                "KEYS", "LEFT", "LIKE", "LIMIT", "MATCH", "NOT", "NULL", "ON", "OPTION", "OR", "ORDER", "OUT",
                "OUTER", "REPLACE", "RIGHT", "SELECT", "SET", "TABLE", "THEN", "TO", "TRUE", "UPDATE", "VALUES",
                "WHEN", "WHERE", "DATE", "DECIMAL", "ELSE", "EXISTS", "FOR", "VARCHAR", "UNION", "GROUP",
                "WITH"));

        String[] words = input.trim().replaceAll("\\n", " ").replaceAll(" +", " ").split("((?<=[\\s,.)(=\\-+/*])|(?=[\\s,.)(=\\-+/*]))");

        StringBuilder output = new StringBuilder();

        for (String word : words) {
            if (!sqlKeywords.contains(word.toUpperCase()) && !StringUtils.isNumeric(word)) {
                word = quote + word + quote;
            }
            output.append(word);
        }

        return output.toString();
    }

    public void startTableProcess() {
        try {
            this.montaSaida(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        System.out.println("Total execution time to cubeTable.processar in millis: " + elapsedTime/1000000);


        objectNode.set("title", jsonTitle);

        return objectNode;

    }


    public void montaSaida(boolean consult) throws Exception {
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

        ArrayNode ArrayNode = this.mapper.createArrayNode();

        ObjectNode json = this.mapper.createObjectNode();

        json.put("id", "opcao_drill_up");

        if (this.dimensionColumn.get(0).isDrillUp()) {
            json.put("image", "vect-arrow-square-up-left");
            json.put("hint", "Drill Up");
            json.put("onclick", "addDrillUp(0)");
        }

        ArrayNode.add(json);

        return ArrayNode;
    }


    public ArrayNode buildDefaultJsonDrillUp() {

        ArrayNode ArrayNode = this.mapper.createArrayNode();
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
        ArrayNode.add(json);
        return ArrayNode;
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
        json.put("alignment","center");
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
            this.cube = Cube.factoryMultiDimensionalCube(createCuboMetaData());
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

    public int getMaxDrillDownSequence() throws BIException {
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

                if (this.usesSequence && firtsColumn == null) {
                    firtsColumn = metaDataField;
                    configurePrimerColumn(firtsColumn);
                }
            }
        }

        createFiltroseRestricoesMetricasCuboMetaData(cubeMetaData);
        return cubeMetaData;
    }

    private boolean isFieldValid(Field field) {
        return field != null && !(field.getTitle().equalsIgnoreCase("Não visualizado") && field.isFixedValue());
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
        for (LineColor corLinha : field.getLineColors()) {
            if (corLinha != null) {
                String corFundo = corLinha.getBackGroundColor().startsWith("#") ? corLinha.getBackGroundColor().substring(1) : corLinha.getBackGroundColor();
                String corFonte = corLinha.getFontColor().startsWith("#") ? corLinha.getFontColor().substring(1) : corLinha.getFontColor();
                ColorAlertMetadata alertaCor = new ColorAlertMetadata(alertSequence++, LogicalOperators.BETWEEN_INCLUSIVE,
                        Double.parseDouble(corLinha.getInitialValue()), Double.parseDouble(corLinha.getInitialValue()), corFonte, corFundo,
                        "Verdana", false, false, 10);
                metaDataField.addColorAlert(alertaCor, ColorAlertMetadata.VALUE_ALERT_TYPE);
            }
        }
    }

    private void configurePrimerColumn(MetaDataField firstColumn) {
        firstColumn.setShowSequence(true);
        FilterSequence filterSequence = getFiltersFunction().getFilterSequence();
        if (filterSequence != null) {
            firstColumn.setRankingExpression(filterSequence.toString());
        }
    }

    public void processCube(ResultSet resultSet) throws BIException {
        try {
            long startTime = System.nanoTime();

            this.cube.process(resultSet);

            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("Total execution time to cubo.processarCubo in millis: " + elapsedTime/1000000);
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
                // TODO Check this when implementing restrictions
                /*MetricDimensionRestriction restMetDim = this.getMetricDimensionRestrictions().getRestMetricaDimensao(field.getCode());
                if (restMetDim != null && restMetDim.isMetricaRestrita(this, dimensionField)) {
                    field.setDefaultField("T");
                    field.setCalculatorPerRestriction(true);
                }*/
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
        metaDataField.setOrder(field.getAccumulatedOrder());
        metaDataField.setDefaultField(field.getDefaultField());
        metaDataField.setTitle(field.getTitle());
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
        setMetaDataFieldMask(metaDataField, field);
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

    private void setMetaDataFieldMask(MetaDataField metaDataField, Field field) {
        String fieldDateMask = field.getDateMask();

        if (!"".equalsIgnoreCase(field.getDateMask()) && fieldDateMask != null && !"".equalsIgnoreCase(field.getDataType())) {
            if (Constants.DATE.equalsIgnoreCase(field.getDataType())) {
                metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.DATA_TYPE));
            } else if (Constants.DIMENSION.equals(field.getFieldType())) {
                if (Constants.NUMBER.equals(field.getDataType()) || Constants.STRING.equals(field.getDataType())) {
                    if (field.getName().equalsIgnoreCase("num_mes")) {
                        if (fieldDateMask.equalsIgnoreCase(MaskMonth.ABBREVIATED)
                                || fieldDateMask.equalsIgnoreCase(MaskMonth.NOT_ABBREVIATED)) {
                            metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_MONTH));
                        }
                    }
                    if (field.getName().equalsIgnoreCase("num_dia_semana")) {
                        if (fieldDateMask.equalsIgnoreCase(MaskWeek.ABBREVIATED)
                                || fieldDateMask.equalsIgnoreCase(MaskWeek.NOT_ABBREVIATED)) {
                            metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_WEEK));
                        }
                    }
                    if (field.getName().equalsIgnoreCase("num_bimestre") || field.getName().equalsIgnoreCase("num_trimestre")
                            || field.getName().equalsIgnoreCase("num_semestre")) {
                        if (Constants.NUMBER.equals(field.getDataType()) && (MaskPeriod.validaMascara(fieldDateMask))) {
                            metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_PERIOD));
                        }
                    }
                    if (field.getName().equalsIgnoreCase("ano_mes_dat")) {
                        if (Constants.STRING.equals(field.getDataType()) && (MaskMonthYear.validaMascara(fieldDateMask))) {
                            metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_MONTH_YEAR));
                        }
                    }
                }
            }
        } else if (Constants.DATE.equals(field.getDataType())) {
            metaDataField.addMask(new MaskColumnMetaData(BIUtil.DEFAULT_DATE_FORMAT, MaskColumnMetaData.DATA_TYPE));
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

    private CubeMetaData createCuboMetaData() throws BIException, DateException {
        Map<Field, MetaDataField> BICubeMapedFields = new HashMap<>();
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

            if (field == null || (field.getTitle().equalsIgnoreCase("Não visualizado") && field.isFixedValue())) {
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
            BICubeMapedFields.put(field, metadataField);
        }

        if (this.usesSequence && firtsLineDimensionDrillDown != null) {
            firtsLineDimensionDrillDown.setShowSequence(true);
            FilterSequence filterSequence = getFiltersFunction().getFilterSequence();
            if (filterSequence != null) {
                firtsLineDimensionDrillDown.setRankingExpression(filterSequence.toString());
            }
        }

        processColorAlerts(BICubeMapedFields);

        createFiltroseRestricoesMetricasCuboMetaData(cubeMetaData);
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
                ColorAlertMetadata alertaCubo = createAlertaCuboMetaData(colorAlert, action, fontColor, backgroundColor);
                metadataField.addColorAlert(alertaCubo, ColorAlertMetadata.VALUE_ALERT_TYPE);
            } else {
                String value = colorAlert.getFirstDoubleValue();
                if ("".equals(value)) {
                    value = "0.00";
                }
                ColorAlertMetadata alertaCubo = new ColorAlertMetadata(colorAlert.getSequence(), colorAlert.getOperator().getSymbol(),
                        Double.parseDouble(value), action, colorAlert.getFirstFieldFunction(), fontColor, backgroundColor, props.getFontName(),
                        props.hasBold(), props.hasItalic(), props.getFontSize(), colorAlert.getValueType(), colorAlert.getSecondField().getTitle(),
                        colorAlert.getSecondFieldFunction());
                metadataField.addColorAlert(alertaCubo, ColorAlertMetadata.SECOND_FIELD_ALERT_TYPE);
            }
        }
    }

    private void createFiltroseRestricoesMetricasCuboMetaData(CubeMetaData cubeMetaData) throws BIException {
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

    private ColorAlertMetadata createAlertaCuboMetaData(ColorAlert colorAlert, int action, String fontColor, String backGroundColor) throws BIException, DateException {
        Field field = colorAlert.getFirstField();
        AlertProperty props = colorAlert.getAlertProperty();

        String operatorSymbol = colorAlert.getOperator().getSymbol();
        String firstValue = colorAlert.getFirstValue();
        String secondValue = colorAlert.getSecondValue();

        Object firstValueObject = parseAlertValue(field, firstValue, operatorSymbol);
        Object segundoValor = (secondValue != null) ? parseAlertValue(field, secondValue, operatorSymbol) : null;

        return new ColorAlertMetadata(colorAlert.getSequence(), operatorSymbol, firstValueObject, segundoValor, action,
                colorAlert.getFirstFieldFunction(), fontColor, backGroundColor, props.getFontName(), props.hasBold(), props.hasItalic(),
                props.getFontSize());
    }

    private Object parseAlertValue(Field field, String value, String operator) throws BIException, DateException {
        if (field == null || field.getFieldType().equals(Constants.METRIC)) {
            return Double.parseDouble(value);
        } else {
            if (field.getDataType().equals(Constants.DATE)) {
                return parseDateAlertValue(field, value, operator);
            } else if (field.getDataType().equals(Constants.NUMBER)) {
                return Integer.parseInt(value);
            } else {
                return value;
            }
        }
    }

    private Object parseDateAlertValue(Field field, String value, String operator) throws BIException {
        if (value.trim().startsWith("@|") && value.trim().endsWith("|")) {
            DimensionFilter dimensionFilter = FilterFactory.createDimensionFilter(field, operator, value);
            if (dimensionFilter.getFilters() != null && !dimensionFilter.getFilters().isEmpty()) {
                // Process and return date range values
            } else {
                // Process and return single date value
            }
        } else {
            // Process and return date value
        }
        return null; // Default return
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
        return this.fields.get(index) != null &&
                (this.fields.get(index).getFieldType().equals(Constants.DIMENSION) && this.fields.get(index).getDisplayLocation() == Constants.LINE) ||
                this.fields.get(index).getFieldType().equals(Constants.METRIC) ||
                this.fields.get(index).isDrillDown() && this.fields.get(index).getDrillDownSequence() >= field.getDrillDownSequence();
    }

    private boolean isValidFieldForNavigableUpwards(int index, Field field) {
        return this.fields.get(index) != null &&
                (this.fields.get(index).getFieldType().equals(Constants.DIMENSION) && this.fields.get(index).getDisplayLocation() == Constants.LINE) ||
                this.fields.get(index).getFieldType().equals(Constants.METRIC) ||
                this.fields.get(index).isDrillDown() && this.fields.get(index).getDrillDownSequence() <= field.getDrillDownSequence();
    }

    public List<Field> getFieldsPerType(String fieldType) {
        return this.fields.stream()
                .filter(field -> field.getFieldType().equals(fieldType))
                .collect(Collectors.toList());
    }

    private List<Field> getAllDrillDownDimensions() {
        return this.fields.stream()
                .filter(field -> field.getFieldType().equals(Constants.DIMENSION)
                        && field.isDrillDown())
                .collect(Collectors.toList());
    }

    private List<Field> getAllNonDrillDownDimensions() {
        return this.fields.stream()
                .filter(field -> field.getFieldType().equals(Constants.DIMENSION)
                        && !field.isDrillDown())
                .collect(Collectors.toList());
    }

    private void sortDrillDownSequence(List<Field> campos) {
        DrillDownComparator drillDownComparator = new DrillDownComparator();
        campos.sort(drillDownComparator);
    }

    public void orderFieldsPerDrillDownSequence() {
        List<Field> metricFields = this.getFieldsPerType(Constants.METRIC);
        List<Field> drillDownFields = getAllDrillDownDimensions();
        List<Field> nonDrillDownFields = getAllNonDrillDownDimensions();

        sortDrillDownSequence(drillDownFields);
        sortDrillDownSequence(nonDrillDownFields);

        this.fields.clear();

        this.fields.addAll(drillDownFields);
        this.fields.addAll(nonDrillDownFields);
        this.fields.addAll(metricFields);

    }

    public boolean stopProcess() {
        return this.cubeListener.stopProcess();
    }

    public void validateMultiDimensionalTable() throws BIException {

        long lineDimensions = this.getVisibleFieldsAmount(Constants.DIMENSION, Constants.LINE);
        long metricColumns = this.getVisibleFieldsAmount(Constants.METRIC, Constants.COLUMN);
        long metricLines = this.getVisibleFieldsAmount(Constants.METRIC, Constants.LINE);

        if (lineDimensions < 1) {
            if (this.tableType == 0) {
                throw new BIGeneralException("Deve haver pelo menos uma dimensão nas linhas!");
            }
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
                    if (displayLocation == Constants.LINE && fieldType.equals("M")) {
                        if (field.isTotalizingField() || field.isAccumulatedParticipation() || field.isVerticalAnalysis()
                                || field.isAccumulatedValue() || field.isHorizontalAnalysis()
                                || field.isHorizontalParticipation() || field.isHorizontalParticipationAccumulated()) {
                            count++;
                        }
                    }
                    return count;
                })
                .sum();
    }

    public void startFieldsCalculationByRestriction() {
        fields.stream()
                .filter(Objects::nonNull) // Filter out null fields
                .filter(field -> "T".equals(field.getDefaultField()) && field.isCalculatorPerRestriction())
                .forEach(field -> {
                    field.setDefaultField("S");
                    field.setCalculatorPerRestriction(false);
                });
    }

    public Field getLastLevelDimensionField(List<Field> fieldsToVerify) {
        return fieldsToVerify.stream()
                .filter(Objects::nonNull) // Filter out null fields
                .filter(tempField ->
                        "S".equals(tempField.getDefaultField()) &&
                                tempField.getDisplayLocation() == Constants.COLUMN &&
                                !(tempField.getTitle().equalsIgnoreCase("Não visualizado") && tempField.isFixedValue()))
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

                    if (displayLocation == Constants.LINE && fieldType.equals("M")) {
                        if (field.isTotalizingField()) {
                            builder.add(createField(field.getFieldId(), "", "total", "S", String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
                        }
                        if (field.isVerticalAnalysis()) {
                            builder.add(createField(field.getFieldId(), "", "%", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
                        }
                        if (field.isAccumulatedParticipation()) {
                            builder.add(createField(field.getFieldId(), "", "% Acumulada", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
                        }
                        if (field.isAccumulatedValue()) {
                            builder.add(createField(field.getFieldId(), "", field.getTitle() + " Acum.", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
                        }
                        if (field.isHorizontalAnalysis()) {
                            builder.add(createField(field.getFieldId(), "", "AH%", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
                        }
                        if (field.isHorizontalParticipation()) {
                            builder.add(createField(field.getFieldId(), "", "AH Participação", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
                        }
                        if (field.isHorizontalParticipationAccumulated()) {
                            builder.add(createField(field.getFieldId(), "", "AH Participação Acumulada", true, true, field.getDefaultField(), String.valueOf(field.getColumnWidth()), field.getColumnAlignment()));
                        }
                    }
                    return builder.build();
                })
                .toList();
    }

    private Field createField(int code, String name, String title, String defaultField, String columnWidth, String columnAlignment) {
        Field field = new Field();
        field.setFieldId(code);
        field.setName(name);
        field.setTitle(title);
        field.setDefaultField(defaultField);
        field.setColumnWidth(columnWidth);
        field.setColumnAlignment(columnAlignment);
        return field;
    }

    private Field createField(int code, String name, String title, boolean verticalAnalysis, boolean childField, String defaultField, String columnWidth, String columnAlignment) {
        Field field = createField(code, name, title, defaultField, columnWidth, columnAlignment);
        field.setVerticalAnalysis(verticalAnalysis);
        field.setChildField(childField);
        return field;
    }

    public int getFieldIndex(String code) throws BIException {
        return Indicator.getFieldIndex(this.fields, code);
    }

    public static int getFieldIndex(List<Field> campos, String code) throws BIException {
        for (int i = 0; i < campos.size(); i++) {
            Field field = campos.get(i);
            if (field != null && field.getFieldId() == Integer.parseInt(code)) {
                return i;
            }
        }

        BIGeneralException biex = new BIGeneralException("Nao foi possivel encontrar o campo de codigo " + code + " no indicador atual.");
        biex.setAction("buscar indice de um field");
        biex.setLocal("Indicator", "getIndiceField(String)");
        throw biex;
    }

    public void addFilter(Field field, String operator, String value) throws BIException {
        this.filters.addFilter(field, operator, value);
    }

    public DimensionFilter getDimensionFilter() {
        return filters.getDimensionFilter();
    }

}
