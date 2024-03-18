package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.util.Constants;

import java.util.ArrayList;

public class ConsultResultGrouping {

    private final ArrayList<ConsultResultKey> keyList;
    private final ArrayList<ConsultRegister> resultList;
    
    public ConsultResultGrouping() {
        keyList = new ArrayList<>();
        resultList = new ArrayList<>();
    }

    
    public void addRegister(ConsultRegister registro) {
        ConsultResultKey chaveAux = registro.getKey();

        int indiceChave = this.getIndiceChave(chaveAux);
        if (indiceChave == -1) {
            keyList.add(chaveAux);
            resultList.add(registro);
        } else {
            resultList.set(indiceChave,
                    this.agrupaRegistros(resultList.get(indiceChave), registro));
        }
    }

    private int getIndiceChave(ConsultResultKey chave) {
        for (int i = 0; i < keyList.size(); i++) {
            if (chave.equals(keyList.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private ConsultRegister agrupaRegistros(ConsultRegister registro_original, ConsultRegister novo_registro) {
        ConsultRegister registro_agrupado = new ConsultRegister();
        ArrayList<Field> array_camposAux = registro_original.getFields();
        ArrayList<Object> array_valores_originaisAux = registro_original.getValues();
        ArrayList<Object> array_valores_novosAux = novo_registro.getValues();
        Field campoAux;
        double valor_1 = 0;
        double valor_2 = 0;
        for (int x = 0; x < array_camposAux.size(); x++) {
            campoAux = array_camposAux.get(x);
            if (campoAux != null) {
                if (!Constants.DIMENSION.equals(campoAux.getFieldType()) && campoAux.getAggregationType().equalsIgnoreCase("SUM")) {
                    String sValor1 = String.valueOf(array_valores_originaisAux.get(x));
                    String sValor2 = String.valueOf(array_valores_novosAux.get(x));
                    if (sValor1 != null && !sValor1.trim().isEmpty()) {
                        valor_1 = Double.parseDouble(sValor1);
                    }
                    if (sValor2 != null && !sValor2.trim().isEmpty()) {
                        valor_2 = Double.parseDouble(sValor2);
                    }
                    registro_agrupado.add(campoAux, valor_1 + valor_2);
                } else {
                    registro_agrupado.add(campoAux, array_valores_originaisAux.get(x));
                }
            }
        }

        return registro_agrupado;
    }

    public CachedResults getCachedResultados() throws BIException {
        ArrayList<Field> array_campos = new ArrayList<>();
        if (!this.resultList.isEmpty()) {
            array_campos = (this.resultList.get(0)).getFields();
        }

        ConsultResult[] resultadosConsulta = new ConsultResult[array_campos.size()];

        if (!this.resultList.isEmpty()) {
            ConsultResult resultadosConsultaAux;
            Field campoAux;
            int codigo_campoAux;
            int c, r;
            for (c = 0; c < array_campos.size(); c++) {
                campoAux = array_campos.get(c);
                resultadosConsultaAux = ConsultResultFactory.factory(campoAux);
                codigo_campoAux = campoAux.getFieldId();
                for (r = 0; r < this.resultList.size(); r++) {
                    resultadosConsultaAux
                            .addValor((this.resultList.get(r)).getValor(codigo_campoAux));
                }

                resultadosConsulta[c] = resultadosConsultaAux;
            }
        }

        return new CachedResults(resultadosConsulta);
    }
}
