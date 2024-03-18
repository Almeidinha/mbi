package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.inputs.FilterBuilderInput;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;
import com.msoft.mbi.data.api.dtos.indicators.BIIndLogicDTO;

public interface FiltersService {

    FiltersDTO getFiltersDTO(Integer id) throws BIException;

    FiltersDTO getFiltersDTOFromDTO(Integer id, BIIndLogicDTO dto) throws BIException;

    void updateIndFilters(Integer id, FiltersDTO dto);


    FiltersDTO buildFilters(FilterBuilderInput filterBuilderInput) throws BIException;

    FiltersDTO removeFilter(FilterBuilderInput input);

    FiltersDTO editFilter(FilterBuilderInput input);

}
