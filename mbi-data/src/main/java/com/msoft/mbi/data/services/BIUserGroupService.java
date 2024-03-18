package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.dtos.user.BIUserGroupDTO;

import java.util.List;

public interface BIUserGroupService extends CrudService<BIUserGroupDTO, Integer> {

    public List<BIUserGroupDTO> findByBiCompanies();
}
