package com.msoft.mbi.data.api.dtos.indicators;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorsAlertDTO {

    private List<ColorAlertDTO> colorAlertList;

}
