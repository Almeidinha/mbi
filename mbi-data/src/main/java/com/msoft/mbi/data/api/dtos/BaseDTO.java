package com.msoft.mbi.data.api.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseDTO implements Serializable {

    private Date createdAt;

    private Date updatedAt;

}
