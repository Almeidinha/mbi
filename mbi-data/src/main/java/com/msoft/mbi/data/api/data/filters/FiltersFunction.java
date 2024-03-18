package com.msoft.mbi.data.api.data.filters;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class FiltersFunction {

    private ArrayList<FilterFunction> filters;

    public FiltersFunction() {
        this.filters = new ArrayList<>();
    }

    public void addFilter(FilterFunction filterFunction) {
        this.filters.add(filterFunction);
    }

    public void removeFilter(FilterFunction filterFunction) {
        this.filters.remove(filterFunction);
    }

    public void removeFilter(int filterIndex) {
        if (this.filters.get(filterIndex) != null) {
            this.filters.remove(filterIndex);
        }
    }

    public FilterSequence getFilterSequence() {
        FilterSequence retorno = null;
        for (FilterFunction filterFunction : this.filters) {
            if (filterFunction != null && filterFunction.getType() == FilterFunction.SEQUENCE_FILTER) {
                retorno = (FilterSequence) filterFunction;
                break;
            }
        }
        return retorno;
    }

    public int getFilterSequenceIndex() {
        int retorno = -1;
        for (FilterFunction filter : this.filters) {
            retorno++;
            if (filter != null && filter.getType() == FilterFunction.SEQUENCE_FILTER) {
                break;
            }
        }
        return retorno;
    }

    public FilterAccumulated getFilterAccumulated() {
        FilterAccumulated result = null;
        for (FilterFunction filterFunction : this.filters) {
            if (filterFunction != null && filterFunction.getType() == FilterFunction.ACCUMULATED_FILTER) {
                result = (FilterAccumulated) filterFunction;
                break;
            }
        }
        return result;
    }

    public boolean hasFilterAccumulated() {
        boolean result = false;
        for (FilterFunction filterFunction : this.filters) {
            if (filterFunction.getType() == FilterFunction.ACCUMULATED_FILTER) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void removeFilterSequence() {
        FilterSequence filterSequence;
        for (FilterFunction filterFunction : this.filters) {
            if (filterFunction.getType() == FilterFunction.SEQUENCE_FILTER) {
                filterSequence = (FilterSequence) filterFunction;
                this.filters.remove(filterSequence);
                break;
            }
        }
    }

    public void removeFilterAccumulated() {
        FilterAccumulated filterAccumulated;
        for (FilterFunction filter : this.filters) {
            if (filter.getType() == FilterFunction.ACCUMULATED_FILTER) {
                filterAccumulated = (FilterAccumulated) filter;
                this.filters.remove(filterAccumulated);
                break;
            }
        }
    }

    public String toString(boolean temFiltrosAcima) {
        StringBuilder result = new StringBuilder();
        for (FilterFunction filter : this.filters) {
            if (temFiltrosAcima) {
                result.append(" and ");
            }
            result.append(filter.toString());
            temFiltrosAcima = true;
        }
        return result.toString();
    }

    public Object clone() {
        FiltersFunction filtersFunction = new FiltersFunction();
        ArrayList<FilterFunction> filters = new ArrayList<>();
        if (this.filters != null) {
            for (FilterFunction filterFunction : this.filters) {
                filters.add((FilterFunction) filterFunction.clone());
            }
        }
        filtersFunction.setFilters(filters);
        return filtersFunction;
    }
}
