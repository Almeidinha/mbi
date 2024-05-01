package com.msoft.mbi.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@EqualsAndHashCode
public class BIAnalysisGraphPK implements Serializable {

    private int indicatorId;

    private int graphId;

}
