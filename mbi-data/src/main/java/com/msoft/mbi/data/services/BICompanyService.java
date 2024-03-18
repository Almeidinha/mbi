package com.msoft.mbi.data.services;


import com.msoft.mbi.model.BICompanyEntity;

public interface BICompanyService extends CrudService<BICompanyEntity, Integer> {

    int findCompanyIdByUserEmail(String email);

    public BICompanyEntity getCurrentUserCompany();

    public int getCurrentUserCompanyId();

}
