package com.msoft.mbi.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor
public class BIAnalysisFieldPK implements Serializable {

    private int fieldId;

    private int biIndByInd;
}
