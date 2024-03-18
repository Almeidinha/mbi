package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.filters.DimensionFilter;
import com.msoft.mbi.data.api.data.filters.Filters;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import com.msoft.mbi.data.api.data.util.Constants;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@SuppressWarnings("unused")
public class BIDriller {

    private static final int MAX_SEQUENCE = 9999999;

    public void addDrillDown(String value, String fieldCode, Indicator indicator) throws BIException {
        if (value == null || fieldCode == null || indicator == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        List<Field> fields = indicator.getFields();
        Field field = fields.get(Indicator.getFieldIndex(fields, fieldCode));
        Field nextField = getNextFieldForDrillDown(field, indicator);

        if (hasDimension(indicator.getFields(), 1, field.getDisplayLocation())) {
            updateFieldSettings(field, "N", 0);
        } else if (nextField != null) {
            updateFieldSettings(field, "N", 0);
            updateFieldSettings(nextField, "S", indicator.isMultidimensional() ? Constants.COLUMN : 0);
        }

        this.clearFilters(field, indicator.getDimensionFilter(), null, indicator.getFilters());
        indicator.addFilter(field, "=", this.formatValue(field, value));
    }

    private Field getNextFieldForDrillDown(Field field, Indicator indicator) {
        Field nextField = this.getNextDrillDownSequence(field, indicator.getFields());
        while (indicator.isMultidimensional() && nextField != null && nextField.getDisplayLocation() == Constants.LINE) {
            nextField = this.getNextDrillDownSequence(nextField, indicator.getFields());
        }
        return nextField;
    }

    private void updateFieldSettings(Field field, String defaultField, int displayLocation) {
        field.setDefaultField(defaultField);
        field.setDisplayLocation(displayLocation);
    }

    private String formatValue(Field campo, String valor) {
        try {
            if (Constants.DATE.equals(campo.getDataType()) && !"".equals(valor) && valor != null) {
                String connectionId = campo.getIndicator().getConnectionId();
                ConnectionBean conexao = new ConnectionBean(connectionId);
                SimpleDateFormat sdf = null;
                try {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date d = sdf.parse(valor.trim());
                    // TODO GEt from connection in DB
                    // sdf.applyPattern(conexao.getFormatoData().trim());
                    return sdf.format(d);
                } catch (ParseException e) {
                    try {
                        sdf.applyLocalizedPattern("dd/MM/yyyy");
                        Date d = sdf.parse(valor.trim());
                        // TODO GEt from connection in DB
                        // sdf.applyPattern(conexao.getFormatoData().trim());
                        return sdf.format(d);
                    } catch (ParseException e1) {
                        try {
                            sdf = new SimpleDateFormat("yyyy/MM/dd");
                            Date d = sdf.parse(valor.trim());
                            // TODO GEt from connection in DB
                            // sdf.applyPattern(conexao.getFormatoData().trim());
                            return sdf.format(d);
                        } catch (ParseException e2) {
                            sdf = new SimpleDateFormat("dd-MM-yyyy");
                            Date d = sdf.parse(valor.trim());
                            // TODO GEt from connection in DB
                            // sdf.applyPattern(conexao.getFormatoData().trim());
                            return sdf.format(d);
                        }
                    }
                }
            }
        } catch (Exception e) {
            return valor.trim();
        }
        return valor;
    }

    public void navigateHide(String fieldCode, Indicator indicator, boolean hide) throws BIException {
        List<Field> fields = indicator.getFields();
        Field field = fields.get(Indicator.getFieldIndex(fields, fieldCode));
        Field nextField = this.getNextDrillDownSequence(field, indicator.getFields());

        while (nextField != null && nextField.getDisplayLocation() == Constants.LINE) {
            nextField = this.getNextDrillDownSequence(nextField, indicator.getFields());
        }
        if (this.hasDimension(indicator.getFields(), 1, field.getDisplayLocation())) {
            this.updateFieldSettings(field, "N", 0);
        } else if (nextField != null) {
            this.updateFieldSettings(field, "N", 0);
            nextField.setDefaultField("S");
        }
    }

    public void navigate(String fieldCode, Indicator indicator, boolean hide) throws BIException {
        Field field = indicator.getFields().get(Indicator.getFieldIndex(indicator.getFields(), fieldCode));
        Field nextField = this.getNextDrillDownSequence(field, indicator.getFields());

        while (nextField != null && nextField.getDisplayLocation() == Constants.LINE) {
            nextField = this.getNextDrillDownSequence(nextField, indicator.getFields());
        }
        if (nextField != null) {
            if (hide) {
                this.updateFieldSettings(nextField, "N", 0);
            } else {
                this.updateFieldSettings(nextField, "S", Constants.COLUMN);
            }
        }
    }


    public Field getNextDrillDownSequence(Field currentField, List<Field> fieldList) {
        int currentSequence = currentField.getDrillDownSequence();
        int nextSequence = MAX_SEQUENCE;
        Field selectedField = null;

        for (Field tempField : fieldList) {
            if (tempField != null && tempField.getFieldType().equals("D") && tempField.isDrillDown() &&
                    tempField.getDrillDownSequence() > currentSequence && tempField.getDrillDownSequence() < nextSequence) {
                selectedField = tempField;
                nextSequence = tempField.getDrillDownSequence();
            }
        }

        return selectedField;
    }

    private boolean hasDimension(List<Field> fields, int requiredCount, int displayLocation) {
        long count = fields.stream()
                .filter(field -> field != null
                        && Constants.DIMENSION.equals(field.getFieldType())
                        && Constants.STRING.equals(field.getDefaultField())
                        && field.getDisplayLocation() == displayLocation)
                .count();
        return count > requiredCount;
    }

    private void clearFilters(Field field, DimensionFilter dimensionFilter, DimensionFilter parentFilter, Filters filters) throws BIException {
        if (dimensionFilter == null)
            return;

        boolean isFieldCodeEqual = dimensionFilter.getCondition() != null && dimensionFilter.getField().getFieldId() == field.getFieldId();

        if (isFieldCodeEqual && dimensionFilter.getOperator().getSymbol().equals("=")) {
            filters.removeDimensionFilter(parentFilter, dimensionFilter, false);
        }

        if (dimensionFilter.getFilters() != null && dimensionFilter.getFilters().size() <= 1 && isFieldCodeEqual) {
            // dimensionFilter.getOperator().getSymbol().equals("=");
            // Do something if the operator symbol equals "="
        }
    }

    public void addDrillUp(Indicator indicator) throws BIException {
        Field fieldToHide = searchCurrentField(indicator);
        if (fieldToHide == null) {
            return;
        }

        Field fieldToShow = getPreviousField(fieldToHide.getDrillDownSequence(), indicator.getFields());
        while (indicator.isMultidimensional() && fieldToShow != null && fieldToShow.getDisplayLocation() == Constants.LINE) {
            fieldToShow = getPreviousField(fieldToShow.getDrillDownSequence(), indicator.getFields());
        }

        if (fieldToShow != null) {
            clearFilters(fieldToHide, indicator.getDimensionFilter(), null, indicator.getFilters());

            if (indicator.getDimensionFilter() != null) {
                int filterSize = indicator.getDimensionFilter().getFilters().size();
                while (filterSize > 0 && !indicator.getDimensionFilter().getDimensionFilter(filterSize - 1).isDrillDown()) {
                    filterSize--;
                }
                if (filterSize >= 1) {
                    clearFilters(fieldToShow, indicator.getDimensionFilter().getDimensionFilter(filterSize - 1), indicator.getDimensionFilter(), indicator.getFilters());
                } else if (indicator.getDimensionFilter().isDrillDown()) {
                    clearFilters(fieldToShow, indicator.getDimensionFilter(), null, indicator.getFilters());
                }
            }

            fieldToHide.setDefaultField("N");
            fieldToHide.setDisplayLocation(0);
            fieldToShow.setDefaultField("S");
            if (indicator.isMultidimensional()) {
                fieldToShow.setDisplayLocation(Constants.COLUMN);
            }
        }
    }


    public Field searchCurrentField(Indicator indicator) {
        return indicator.getFields().stream()
                .filter(field -> field != null && "D".equals(field.getFieldType()) && "S".equals(field.getDefaultField())
                        && (!indicator.isMultidimensional() || Constants.COLUMN == field.getDisplayLocation()))
                .findFirst().orElse(null);
    }

    public Field getPreviousField(int currentSequence, List<Field> fields) {
        return fields.stream()
                .filter(field -> field != null && "D".equals(field.getFieldType()) && field.isDrillDown() && field.getDrillDownSequence() < currentSequence)
                .max(Comparator.comparingInt(Field::getDrillDownSequence))
                .orElse(null);
    }

}
