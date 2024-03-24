package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
@NoArgsConstructor
@SuppressWarnings("unused")
public class MetricSqlFilter extends ArrayList<MetricFilter> {


    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "";
        }

        return this.stream()
                .filter(Objects::nonNull)
                .map(MetricFilter::toString)
                .filter(oneClause -> oneClause != null && !oneClause.isEmpty())
                .collect(Collectors.joining(" AND ", "HAVING ", ""));
    }

    public String toStringWithCode() {
        return this.stream()
                .map(metricFilter -> {
                    try {
                        return new MetricTextFilter(metricFilter);
                    } catch (BIException e) {
                        log.error("Error in MetricTextFilter(metricFilter) :" + e.getMessage());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .map(metricTextFilter -> {
                    try {
                        return metricTextFilter.toStringWithCode();
                    } catch (BIException e) {
                        log.error("Error: in metricTextFilter.toStringWithCode()" + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(oneClause -> !oneClause.isEmpty())
                .collect(Collectors.joining(" AND ", "HAVING ", ""));
    }

    public String toStringWhere() throws BIException {
        if (this.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (MetricFilter fTemp : this) {
            Field cmpAux = fTemp.getField();
            Operator op = fTemp.getOperator();
            if (cmpAux != null && op != null) {
                String clause = cmpAux.isExpression()
                        ? String.format("%s %s %s", cmpAux.getName(), op.getSymbol(), fTemp.getSQLValue())
                        : String.format("%s.%s %s %s", cmpAux.getTableNickname(), cmpAux.getName(), op.getSymbol(), fTemp.getSQLValue());
                result.append(" AND ").append(clause);
            }
        }

        return result.toString().startsWith(" AND ") ? result.substring(5) : result.toString();
    }

    public String toStringWithAggregation(boolean withAggregation) {
        if (isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder(withAggregation ? "HAVING " : "AND ");

        List<String> clauses = this.stream()
                .filter(filter -> filter.getField() != null)
                .map(filter -> withAggregation ? filter.toString() : filter.toStringWithoutAggregation())
                .filter(clause -> clause != null && !clause.isEmpty())
                .collect(Collectors.toList());

        if (clauses.isEmpty()) {
            return "";
        }

        result.append(String.join(" AND ", clauses));
        return result.toString();
    }

    public void updateFields(List<Field> fields, List<Field> originalFields) {
        this.stream()
                .filter(Objects::nonNull)
                .forEach(metric -> {
                    Field metricField = metric.getField();
                    IntStream.range(0, originalFields.size())
                            .filter(i -> originalFields.get(i) != null && metricField.getFieldId() == originalFields.get(i).getFieldId())
                            .findFirst()
                            .ifPresent(i -> metricField.setFieldId(fields.get(i).getFieldId()));
                });
    }

    public int applyValues(PreparedStatement stmt, int position) throws BIException {
        for (MetricFilter metricFilter : this) {
            Field field = metricFilter.getField();
            if (!field.isExpression() || !(field.getName().toUpperCase().trim().startsWith("SE(") || field.getName().toUpperCase().trim().startsWith("IF("))) {
                Condition condition = metricFilter.getCondition();
                if (condition != null) {
                    position = metricFilter.applyValues(stmt, position);
                }
            }
        }
        return position;
    }
}
