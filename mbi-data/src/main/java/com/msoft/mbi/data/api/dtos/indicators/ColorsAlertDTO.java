package com.msoft.mbi.data.api.dtos.indicators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorsAlertDTO {

    private ArrayList<ColorAlertDTO> colorAlertList;

}
