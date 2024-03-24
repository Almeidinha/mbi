package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.msoft.mbi.data.api.data.util.Constants.*;

@Getter
@Setter
@Data
@Log4j2
@NoArgsConstructor
@SuppressWarnings("unused")
public class Field implements Cloneable {

    private int fieldId;
    protected Indicator indicator;
    private String name;
    private String title;
    private String nickname;
    private boolean expression = false;
    private int drillDownSequence;
    private int visualizationSequence;
    private String defaultField = "";
    private int order;
    private Integer delegateOrder;
    private String tableNickname;
    private String orderDirection = "ASC";
    private int numDecimalPositions;
    private boolean totalizingField;
    private boolean verticalAnalysis;
    private String verticalAnalysisType;
    private boolean horizontalAnalysis;
    private String horizontalAnalysisType;
    private String aggregationType;
    private boolean accumulatedParticipation;
    private boolean accumulatedValue;
    private List<LineColor> lineColors;
    private boolean lastColorValueList;
    private String dataType;
    private String fieldType;
    private int displayLocation;
    private int columnWidth;
    private String columnAlignment;
    private boolean sumLine;
    private String accumulatedLine;
    private String dateMask;
    private boolean partialTotalization;
    private boolean partialMedia;
    private boolean partialExpression;
    private boolean partialTotalExpression;
    private boolean applyTotalizationExpression;
    private int generalFilter;
    private boolean requiredField;

    private boolean horizontalParticipation;
    private boolean horizontalParticipationAccumulated;
    private int accumulatedOrder;
    private String accumulatedOrderDirection = "ASC";
    private boolean mediaLine;
    private boolean childField;
    private boolean fixedValue = false;
    private FieldColorValues fieldColorValues;
    private boolean calculatorPerRestriction = false;
    private boolean replicateChanges;
    private List<Field> dependentCalculatedFields;
    private boolean isNavigableUpwards;

    private boolean drillDown;
    private boolean drillUp;
    private boolean navigable;
    private boolean deleted;

    private int numberOfSteps;

    public boolean isDimension() {
        return DIMENSION.equals(this.fieldType);
    }

    public boolean isMetric() {
        return METRIC.equals(this.fieldType);
    }

    public Field(int fieldId, Indicator indicator) throws BIException {
        this();
        this.fieldId = fieldId;
        this.indicator = indicator;
    }

    public Field(int fieldId, Indicator indicator, NamedParameterJdbcTemplate jdbcTemplate) throws BIException {
        this();
        this.fieldId = fieldId;
        this.indicator = indicator;
        this.setLineColor(jdbcTemplate);
        this.setFieldColorValues(this.searchForFieldColorValues(indicator, this, jdbcTemplate));

    }

    public Indicator getIndicator() {
        if (Optional.ofNullable(this.indicator).isEmpty()) {
            try {
                this.indicator = new Indicator();
            } catch (BIException e) {
                log.error("Error in Indicator getIndicator() : " + e.getMessage());
            }
        }
        return this.indicator;
    }
    
    public void setDefaultField(String defaultField) {
        this.defaultField = defaultField;
        if ("N".equals(this.defaultField)) {
            this.displayLocation = 0;
        } else if ("T".equals(this.defaultField)) {
            if (this.displayLocation == 0) {
                this.displayLocation = 1;
            }
        }
    }
    
    public String getTableNickname() {
        if (Optional.ofNullable(this.tableNickname).isPresent()) {
            if (!this.tableNickname.isEmpty())
                return tableNickname;
        }
        return "";
    }


    public void setOrderDirection(String orderDirection) {
        if (Optional.ofNullable(orderDirection).isEmpty() || orderDirection.isEmpty()) {
            this.orderDirection = "ASC";
        } else {
            this.orderDirection = orderDirection;
        }
    }

    public void setTotalizingField(String totalizingField) {
        this.totalizingField = false;
        this.applyTotalizationExpression = false;
        if (Optional.ofNullable(totalizingField).isPresent()) {
            if (totalizingField.equals("S") || totalizingField.equals("E")) {
                this.totalizingField = true;
            }
            if (totalizingField.equals("E")) {
                this.applyTotalizationExpression = true;
            }
        }
    }
    
    public String getAggregationType() {
        if (Optional.ofNullable(this.aggregationType).isEmpty() || this.aggregationType.isEmpty()) {
            this.aggregationType = "EMPTY";

            if ((this.fieldType != null) && this.fieldType.equals(METRIC)) {
                this.aggregationType = "SUM";
            }
        }
        return this.aggregationType;
    }

    public List<LineColor> getLineColors() {
        if (Optional.ofNullable(this.lineColors).isEmpty()) {
            this.lineColors = new ArrayList<>();
        }
        return this.lineColors;
    }

    public boolean hasLineColor(NamedParameterJdbcTemplate jdbcTemplate) {

        String sql = "SELECT COUNT(*) FROM bi_color_conditions WHERE field_id = :fieldId AND indicator_id = :ind";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("fieldId", this.fieldId)
                .addValue("ind", this.indicator.getCode());

        int count =  Optional.ofNullable(jdbcTemplate.queryForObject(sql, namedParameters, Integer.class)).orElse(0);

        return count > 0;
    }

    public void setLineColor(NamedParameterJdbcTemplate jdbcTemplate) throws BIException {
        String sql = "SELECT initial_value, final_value, background_color, font_color, class_description "
                + "FROM bi_color_conditions WHERE field_id = :fieldId AND indicator_id = :ind";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("fieldId", this.fieldId)
                .addValue("ind", this.indicator.getCode());

        /* TODO check this way!
        List<LineColor> colors = jdbcTemplate.query(sql, namedParameters, (rs, rowNum) ->
                    LineColor.builder()
                            .initialValue(rs.getString("initial_value"))
                            .finalValue(rs.getString("final_value"))
                            .colorClass(rs.getString("class_description"))
                            .backGroundColor(rs.getString("background_color"))
                            .fontColor(rs.getString("background_color"))
                            .build()

                );
         */

        jdbcTemplate.query(sql, namedParameters, resultSet -> {
            try {
                while (resultSet.next()) {
                    LineColor lineColor = new LineColor();
                    lineColor.setInitialValue(BIUtil.verificanullString(resultSet, "initial_value"));
                    lineColor.setFinalValue(BIUtil.verificanullString(resultSet, "final_value"));
                    lineColor.setColorClass(BIUtil.verificanullString(resultSet, "class_description"));
                    lineColor.setBackGroundColor(BIUtil.verificanullString(resultSet, "background_color"));
                    lineColor.setFontColor(BIUtil.verificanullString(resultSet, "font_color"));
                    this.addLineColor(lineColor);
                }
            } catch (BIException e) {
                throw new RuntimeException(e);
            }
        });
    }

 
    public void addLineColor(LineColor lineColor) {
        this.getLineColors().add(lineColor);
    }

    public int searchColor(String initialValue, String finalValue) {
        int index = 0;
        try {
            double initial = Double.parseDouble(initialValue);
            double finalVal = Double.parseDouble(finalValue);
            for (LineColor lineColor : this.getLineColors()) {
                if (lineColor != null && Double.parseDouble(lineColor.getInitialValue()) == initial
                        && Double.parseDouble(lineColor.getFinalValue()) == finalVal) {
                    return index;
                }
                index++;
            }
        } catch (NumberFormatException e) {
            log.error("Error parsing color values to double", e);
        }
        return -1;
    }

    public void deleteCor(String initialValue, String finalValue) {
        int searchColor = this.searchColor(initialValue, finalValue);
        if (searchColor != -1)
            this.deleteCor(searchColor);
    }

    public void deleteCor(int index) {
        this.getLineColors().remove(index);
        if (this.getLineColors().isEmpty() && !this.lastColorValueList) {
            this.setLastColorValueList(true);
        }
    }

    public Operators getAvailableOperators() {
        return new Operators();
    }

    public static String convertTitleToNickname(String title) {

        Map<String, String> replacements = new HashMap<>();

        replacements.put("[^\\p{ASCII}]", "");
        replacements.put("[\\s-_]", "");
        replacements.put("'", CONSTANT_APOSTROPHE);
        replacements.put("!", CONSTANT_EXCLAMATION);
        replacements.put("@", CONSTANT_AT_SIGN);
        replacements.put("#", CONSTANT_HASH);
        replacements.put("\\$", CONSTANT_DOLLAR_SIGN);
        replacements.put("%", CONSTANT_PERCENT);
        replacements.put("´", CONSTANT_ACUTE);
        replacements.put("`", CONSTANT_GRAVE_ACCENT);
        replacements.put("&", CONSTANT_AMPERSAND);
        replacements.put("\\*", CONSTANT_ASTERISK);
        replacements.put("=", CONSTANT_EQUAL);
        replacements.put("\\+", CONSTANT_PLUS);
        replacements.put("§", SECTION_SIGN);
        replacements.put("\\|", CONSTANT_VERTICAL_BAR);
        replacements.put(",", CONSTANT_COMA);
        replacements.put("\\.", CONSTANT_DOT);
        replacements.put(";", CONSTANT_SEMICOLON);
        replacements.put(":", CONSTANT_COLON);
        replacements.put("\\?", CONSTANT_QUESTION_MARK);
        replacements.put("/", CONSTANT_SLASH);
        replacements.put("º", MASCULINE_ORDINAL);
        replacements.put("ª", FEMININE_ORDINAL);
        replacements.put("£", POUND_SIGN);
        replacements.put("¢", CENT_SIGN);
        replacements.put("¬", NOT_SIGN);
        replacements.put("¹", CONSTANT_SUPERSCRIPT_ONE);
        replacements.put("²", CONSTANT_SUPERSCRIPT_TWO);
        replacements.put("³", CONSTANT_SUPERSCRIPT_THREE);
        replacements.put("\\(", "__");
        replacements.put("\\)", "__");
        replacements.put("\\\\", "___");

        StringBuilder result = getTitleTiNicknameResult(title, replacements);
        return result.toString().toLowerCase();
    }

    private static StringBuilder getTitleTiNicknameResult(String title, Map<String, String> replacements) {
        StringBuilder result = new StringBuilder(title);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String regex = entry.getKey();
            String replacement = entry.getValue();
            Pattern pattern = Pattern.compile(Pattern.quote(regex));
            Matcher matcher = pattern.matcher(result);
            int offset = 0;
            while (matcher.find()) {
                int start = matcher.start() + offset;
                int end = matcher.end() + offset;
                result.replace(start, end, replacement);
                offset += replacement.length() - (end - start);
            }
        }
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        Field c = (Field) super.clone();
        c.setFieldId(this.fieldId);
        c.setIndicator(this.indicator);
        c.setVerticalAnalysisType(this.verticalAnalysisType);
        c.setHorizontalAnalysisType(this.horizontalAnalysisType);
        c.setColumnAlignment(this.columnAlignment);
        c.setColumnWidth(String.valueOf(this.columnWidth));
        c.setNickname(this.nickname);
        c.setTableNickname(this.tableNickname);
        List<LineColor> lineColors = new ArrayList<>();
        for (LineColor lineColor : this.getLineColors()) {
            if (Optional.ofNullable(lineColor).isPresent()) {
                LineColor novoLineColor = (LineColor) lineColor.clone();

                lineColors.add(novoLineColor);
            }
        }
        c.setLineColors(lineColors);
        c.setExpression(this.expression);
        c.setDisplayLocation(this.displayLocation);
        c.setName(this.name);
        c.setNumDecimalPositions(this.numDecimalPositions);
        c.setOrder(this.order);
        c.setDefaultField(this.defaultField);
        c.setAccumulatedParticipation(this.accumulatedParticipation);
        c.setAccumulatedValue(this.accumulatedValue);
        c.setOrderDirection(this.orderDirection);
        c.setDrillDownSequence(this.drillDownSequence);
        c.setVisualizationSequence(this.visualizationSequence);
        c.setAggregationType(this.aggregationType);
        c.setFieldType(this.fieldType);
        c.setDataType(this.dataType);
        c.setTitle(this.title);
        c.setTotalizingField(this.totalizingField ? "S" : "N");
        c.setSumLine(this.sumLine);
        c.setAccumulatedLine(this.accumulatedLine);
        c.setDateMask(this.dateMask);
        c.setPartialTotalization(this.partialTotalization);
        c.setPartialMedia(this.isPartialMedia());
        c.setPartialExpression(this.partialExpression);
        c.setHorizontalParticipationAccumulated(this.horizontalParticipationAccumulated);
        c.setHorizontalParticipation(this.horizontalParticipation);
        c.setAccumulatedOrder(this.accumulatedOrder);
        c.setAccumulatedOrderDirection(this.accumulatedOrderDirection);
        c.setMediaLine(this.mediaLine);
        c.setApplyTotalizationExpression(this.applyTotalizationExpression);
        c.setFixedValue(this.fixedValue);
        c.setFieldColorValues((FieldColorValues) this.getFieldColorValues().clone(c.getIndicator(), c));
        c.setDelegateOrder(this.delegateOrder);
        return c;
    }

    public void setDisplayLocation(int displayLocation) {
        this.setDisplayLocation(displayLocation, true);
    }

    public void setDisplayLocation(int exhibitionLocal, boolean doValidate) {
        this.displayLocation = exhibitionLocal;
        if (doValidate) {
            if (this.displayLocation == Constants.LINE || this.displayLocation == Constants.COLUMN) {
                this.defaultField = "S";
            }
        }
    }

    public int getColumnWidth() {
        if (this.columnWidth != 0) {
            return columnWidth;
        } else {
            return 100;
        }
    }

    public void setColumnWidth(String columnWidth) {
        if (Optional.ofNullable(columnWidth).isEmpty()) {
            this.columnWidth = 100;
        } else {
            try {
                this.columnWidth = Integer.parseInt(columnWidth);
            } catch (NumberFormatException e) {
                this.columnWidth = 100;
            }
        }
    }

    public String getAccumulatedLine() {
        if (Optional.ofNullable(this.accumulatedLine).isEmpty()) {
            this.accumulatedLine = "N";
        }
        return this.accumulatedLine;
    }

    public String getInternalAlignmentPosition()  {
        if (Optional.ofNullable(this.columnAlignment).isEmpty() || this.columnAlignment.isEmpty()) {
            if (("D").equals(this.fieldType)) {
                if (this.dataType.equals("D"))
                    this.columnAlignment = MIDDLE_COLUMN_ALIGNMENT;
                else
                    this.columnAlignment = LEFT_COLUMN_ALIGNMENT;
            } else {
                this.columnAlignment = RIGHT_COLUMN_ALIGNMENT;
            }
        }
        return this.columnAlignment;
    }

    public String getColumnAlignment() {
        String aux = this.getInternalAlignmentPosition();
        return switch (aux) {
            case MIDDLE_COLUMN_ALIGNMENT -> "center";
            case LEFT_COLUMN_ALIGNMENT -> "left";
            case RIGHT_COLUMN_ALIGNMENT -> "right";
            default -> "center";
        };
    }

    public void setColumnAlignment(String alignmentPosition) {

        String lowerCaseAlignmentPosition = alignmentPosition.toLowerCase();

        switch (lowerCaseAlignmentPosition) {
            case "right" -> this.columnAlignment = RIGHT_COLUMN_ALIGNMENT;
            case "left" -> this.columnAlignment = LEFT_COLUMN_ALIGNMENT;
            case "center" -> this.columnAlignment = MIDDLE_COLUMN_ALIGNMENT;
            default -> this.columnAlignment = alignmentPosition;
        }
    }

    public String getDateMask() {
        return Optional.ofNullable(this.dateMask).orElse("");
    }


    public String getSqlExpressionWithNickName() {
        if (this.tableNickname.isEmpty())
            return this.name + " " + this.nickname + ",";
        else
            return this.tableNickname + "." + this.name + " " + this.nickname + ",";
    }

    public String getSqlExpressionWithoutNickName() {
        if (this.tableNickname.isEmpty())
            return this.name;
        else {
            return this.tableNickname + "." + this.name;
        }
    }

    public boolean isNumber() {
        return this.dataType.equals(NUMBER);
    }

    public boolean isDate() {
        return this.getDataType().equals(DATE);
    }

    public boolean isString() {
        return this.dataType.equals(Constants.STRING);
    }

    public boolean hasDateMask() {
        return Optional.ofNullable(this.dateMask).isPresent() && !this.dateMask.isEmpty();
    }


    public FieldColorValues getFieldColorValues() {
        if (Optional.ofNullable(this.fieldColorValues).isEmpty()) {
            this.fieldColorValues = new FieldColorValues();
        }
        return fieldColorValues;
    }

    public FieldColorValues searchForFieldColorValues(Indicator indicator, Field campo, NamedParameterJdbcTemplate jdbcTemplate)
            throws BIException {
        return new FieldColorValues().consult(indicator, campo, jdbcTemplate);
    }

    public String getDependentCalculatedFieldsMessage() {
        StringBuilder builder = new StringBuilder();
        if (Optional.ofNullable(this.dependentCalculatedFields).isPresent()) {
            builder.append("O campo que você está tentando excluir é utilizado para o cálculo do(s) seguinte(s) campos:\\n");
            for (Field campo : this.dependentCalculatedFields) {
                builder.append(campo.getFieldId()).append(" - ").append(campo.getTitle()).append("\\n");
            }
            builder.append("A exclusão deste campo causará problema na análise. Deseja excluir o campo mesmo assim?");
        }
        return builder.toString();
    }

    private String extractCode(String expression) {
        return expression.substring(expression.indexOf(Constants.START_EXPRESSION) + 2, expression.indexOf(Constants.END_EXPRESSION));
    }

    protected void setRequiredField(boolean requiredField) {
        this.requiredField = requiredField;
    }

    public void setRequiredField(String requiredField) {
        this.accumulatedParticipation = requiredField != null && requiredField.equals("S");
    }

    protected Field fieldUsedForCalculation() {
        List<Field> fields = this.indicator.getFields();
        if (fields == null) {
            return null;
        }
        String id = START_EXPRESSION + this.getFieldId() + END_EXPRESSION;
        return fields.stream()
                .filter(Field::isExpression)
                .filter(field -> field.getName().contains(id))
                .findFirst()
                .orElse(null);
    }
}
