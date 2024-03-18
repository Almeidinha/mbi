package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Log4j2
@Getter
@Setter
@SuppressWarnings("unused")
public class MetricFilters extends ArrayList<MetricFilter> {

    @Override
    public String toString() {
        String result = this.stream()
                .map(MetricFilter::toString)
                .filter(clause -> clause != null && !clause.isEmpty())
                .collect(Collectors.joining(" AND "));
        return result.isEmpty() ? "" : "HAVING " + result;
    }

    public String toStringWithCode() {
        return this.stream()
                .map(filter -> {
                    try {
                        return filter.toStringWithCode();
                    } catch (BIException e) {
                        log.error("Error converting filter to string with code", e);
                        throw new RuntimeException("Error converting filter to string with code", e);
                    }
                })
                .filter(clause -> clause != null && !clause.isEmpty())
                .collect(Collectors.joining(" AND ", "HAVING ", ""));
    }

    public String toStringWhere() {
        return this.stream()
                .map(filter -> {
                    try {
                        Field field = filter.getField();
                        String fieldExpression = field.isExpression() ? field.getName() : field.getTableNickname() + "." + field.getName();
                        return fieldExpression + " " + filter.getOperator().getSymbol() + " " + filter.getSQLValue();
                    } catch (BIException e) {
                        log.error("Error converting filter to string", e);
                        throw new RuntimeException("Error converting filter to string", e);
                    }
                })
                .collect(Collectors.joining(" AND "));
    }

    public String toStringWithAggregation(boolean withAggregation) {
        StringJoiner joiner = new StringJoiner(" AND ");
        this.stream()
                .map(MetricFilter::toString)
                .filter(clause -> !clause.isEmpty())
                .forEach(joiner::add);

        if (joiner.length() > 0) {
            return (withAggregation ? "HAVING " : "AND ") + joiner.toString();
        } else {
            return "";
        }
    }

    public void updateFields(List<Field> fields, List<Field> originalFields) {
        if (fields == null || originalFields == null) {
            log.error("Input arrays cannot be null");
            throw new IllegalArgumentException("Input arrays cannot be null");
        }
        for (MetricFilter metric : this) {
            if (metric == null || metric.getField() == null) {
                continue;
            }
            originalFields.stream()
                    .filter(originalField -> originalField != null && Objects.equals(metric.getField().getFieldType(), originalField.getFieldType()))
                    .findFirst()
                    .ifPresent(originalField -> metric.getField().setFieldId(originalField.getFieldId()));
        }
    }

    public int applyValues(PreparedStatement stmt, int position) {
        for (MetricFilter metricFilter : this) {
            if (metricFilter == null) {
                continue;
            }
            Field field = metricFilter.getField();
            if (field == null) {
                continue;
            }

            String fieldName = field.getName().toUpperCase().trim();
            if (!field.isExpression() || !(fieldName.startsWith("SE(") || fieldName.startsWith("IF("))) {
                if (metricFilter.getCondition() != null) {
                    try {
                        position = (Integer) metricFilter.applyValues(stmt, position);
                    } catch (BIException e) {
                        log.error("Error applying values", e);
                    }
                }
            }
        }
        return position;
    }

}
