package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Operator;
import com.msoft.mbi.data.api.data.indicator.TextCondition;
import com.msoft.mbi.data.api.data.util.BIMacro;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;

@Getter
@Setter
public class DimensionTextFilter extends DimensionFilter implements Cloneable {

    private boolean startParentheses;
    private boolean endParentheses;
    private int startParentCount;
    private int EndParentCount;

    public DimensionTextFilter() {
        super();
    }

    public DimensionTextFilter(DimensionFilter fd) throws BIException {
        if (fd != null) {
            this.setConnector(fd.getConnector());
            if (fd.getCondition() != null) {
                this.setCondition(new TextCondition(fd.getCondition()));
            }
            this.setMacro(fd.getMacro(), fd.getMacroField());
            ArrayList<DimensionFilter> lista = new ArrayList<>();
            if (fd.getFilters() != null) {
                for (DimensionFilter filtro : fd.getFilters()) {
                    lista.add(new DimensionTextFilter(filtro));
                }
            }
            this.setFilters(lista);
        }
    }

    public DimensionTextFilter(Condition condicao) {
        super.setCondition(condicao);
    }

    @SneakyThrows
    @Override
    public Object clone() {
        DimensionFilter f = new DimensionTextFilter();
        if (this.getCondition() == null || this.getMacro() != null) {
            if (this.getFilters() != null) {
                ArrayList<DimensionFilter> novo = new ArrayList<>();
                for (DimensionFilter dimensionFilter : this.getFilters()) {
                    novo.add((DimensionTextFilter) dimensionFilter.clone());
                }
                f.setFilters(novo);
                f.setConnector(this.getConnector());
            }
            if (this.getMacro() != null) {
                f.setMacro((BIMacro) this.getMacro().clone(), (Field) this.getMacroField().clone());
                if (this.getCondition() != null)
                    f.setCondition((Condition) this.getCondition().clone());
            }
        } else {
            f.setConnector(this.getConnector());
            f.setCondition((Condition) this.getCondition().clone());
        }
        f.setDrillDown(this.isDrillDown());
        return f;
    }

    public Object applyValues(Object stmt, Integer posicao) throws BISQLException {
        Condition condicao = this.getCondition();
        try {
            if (condicao != null) {
                stmt = condicao.applyValues(stmt, posicao);
            }
            if (this.getFilters() != null) {
                for (DimensionFilter fil : this.getFilters()) {
                    stmt = fil.applyValues(stmt, posicao);
                }
            }
            return stmt;
        } catch (Exception e) {
            throw new BISQLException(e, "Erro ao setar o valor na SQL.");
        }
    }

    public boolean haveStartParentheses() {
        return this.startParentheses;
    }

    public boolean haveEndParentheses() {
        return this.endParentheses;
    }

    public void setCondition(Field campo, Operator operador, String valor) throws BIException {
        TextCondition condicao = new TextCondition(campo, operador, valor);
        super.setCondition(condicao);

    }

    public void setCondition(Field campo, String operador, String valor) throws BIException {
        TextCondition condicao = new TextCondition(campo, operador, valor);
        super.setCondition(condicao);
    }


    public String getFormattedValue() {
        return this.getCondition().getFormattedValue();
    }

}
