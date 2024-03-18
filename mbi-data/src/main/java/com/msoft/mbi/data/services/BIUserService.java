package com.msoft.mbi.data.services;

import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import com.msoft.mbi.model.BIUserEntity;

public interface BIUserService extends CrudService<BIUserDTO, Integer> {
    BIUserDTO findByEmail(String email);

    BIUserEntity findEntityByEmail(String email);

    BIUserDTO patch(Integer id, BIUserDTO biUserDTO);

    int findUseridByEmail(String email);

    public BIUserEntity getCurrentUser();

    public int getCurrentUserId();

}
