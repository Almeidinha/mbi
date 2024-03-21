package com.msoft.mbi.cube.multi.metaData;

import java.util.ArrayList;
import java.util.List;

import com.msoft.mbi.cube.multi.column.TextTypeRoot;
import com.msoft.mbi.cube.multi.dimension.DimensionMetaData;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CubeMetaData extends DimensionMetaData {

    private final List<MetaDataField> dimensionFields;
    private final List<MetaDataField> metricFields;
    private final List<MetaDataField> fields;
    @Setter
    private String metricFieldsExpression;
    @Setter
    private String accumulatedFieldExpression;

    public CubeMetaData() {
        super("Cube", null, new TextTypeRoot());
        this.dimensionFields = new ArrayList<>();
        this.metricFields = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public void addField(MetaDataField campo, String type) {
        if (MetaDataField.DIMENSION.equals(type)) {
            this.dimensionFields.add(campo);
        } else {
            this.metricFields.add(campo);
        }
        campo.setFieldType(type);
        this.fields.add(campo);
    }

    public void orderFields() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        this.fields.sort(comparator);
    }

    public void orderDimensionFields() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        this.dimensionFields.sort(comparator);
    }

    public void orderMetricFields() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        this.metricFields.sort(comparator);
    }

    public MetaDataField getMetricFieldByCode(int code) {
        for (MetaDataField campo : this.metricFields) {
            if (campo.getId() == code) {
                return campo;
            }
        }
        return null;
    }

}
