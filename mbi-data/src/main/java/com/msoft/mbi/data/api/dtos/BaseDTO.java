package com.msoft.mbi.data.api.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Date createdAt;

    private Date updatedAt;

}
