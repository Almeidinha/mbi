package com.msoft.mbi.data.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.dtos.filters.FiltersDTO;

import java.util.List;

public interface TestService {

    List<Object> getObjects();

    String getTable(Integer id);

    ObjectNode getJsonTable(Integer id);

    String getJsonTree(Integer id) throws BIException;

    FiltersDTO getFiltersDTO(Integer id) throws BIException;
}
