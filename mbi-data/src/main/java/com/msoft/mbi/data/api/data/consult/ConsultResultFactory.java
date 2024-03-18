package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.util.Constants;

import java.util.ArrayList;
import java.util.Collection;

public class ConsultResultFactory {

    private ConsultResultFactory() {
        super();
    }

    public static ConsultResult factory(Field campo) {
        String mascara = campo.getDateMask();
        
        if (Constants.DIMENSION.equals(campo.getFieldType())) {
            if (mascara != null && !Constants.DATE.equals(campo.getDataType())) {
                if (Constants.NUMBER.equals(campo.getDataType())) {
                    if (campo.getName().equalsIgnoreCase("num_mes")) {
                        if (mascara.equalsIgnoreCase(ConsultResultMonth.ABREVIADO) || mascara.equalsIgnoreCase(ConsultResultMonth.NAO_ABREVIADO)) {
                            return new ConsultResultMonth(campo);
                        }
                    }
                    if (campo.getName().equalsIgnoreCase("num_dia_semana")) {
                        if (mascara.equalsIgnoreCase(ConsultResultWeek.ABREVIADO) || mascara.equalsIgnoreCase(ConsultResultWeek.NAO_ABREVIADO)) {
                            return new ConsultResultWeek(campo);
                        }
                    }
                    if (campo.getName().equalsIgnoreCase("num_bimestre") || campo.getName().equalsIgnoreCase("num_trimestre") || campo.getName().equalsIgnoreCase("num_semestre")) {
                        if (ConsultResultPeriod.validaMascara(mascara)) {
                            return new ConsultResultPeriod(campo);
                        }
                    }
                } else if (Constants.STRING.equals(campo.getDataType())) {
                    if (campo.getName().equalsIgnoreCase("ano_mes_dat")) {
                        if (ConsultResultMonthYear.validaMascara(mascara)) {
                            return new ConsultResultMonthYear(campo);
                        }
                    }
                }
            }
        } else {
            return new ConsultResultNumber(campo);
        }

        if (Constants.NUMBER.equals(campo.getDataType())) {
            return new ConsultResultNumber(campo);
        } else if (Constants.DATE.equals(campo.getDataType())) {
            return new ConsultResultDate(campo);
        } else {
            return new ConsultResultString(campo);
        }
    }

    public static ConsultResult factory(Field campo, Object valor) {
        ConsultResult resultadoConsulta = factory(campo);
        resultadoConsulta.addValor(valor);
        return resultadoConsulta;
    }

    public static ConsultResult factory(Field campo, ArrayList<Object> lista) {
        ConsultResult resultadoConsulta = factory(campo);
        resultadoConsulta.addToValues(lista);
        return resultadoConsulta;
    }

    public static ConsultResult factory(Field campo, Collection<Object> lista) {
        ConsultResult resultadoConsulta = factory(campo);
        resultadoConsulta.addToValues(lista);
        return resultadoConsulta;
    }
    
}
