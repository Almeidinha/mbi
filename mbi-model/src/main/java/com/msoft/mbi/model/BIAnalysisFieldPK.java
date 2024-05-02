package com.msoft.mbi.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class BIAnalysisFieldPK implements Serializable {

    private Integer fieldId;

    private BIIndEntity biIndByInd;
}
