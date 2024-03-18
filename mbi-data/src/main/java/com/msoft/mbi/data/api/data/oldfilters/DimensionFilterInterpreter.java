package com.msoft.mbi.data.api.data.oldfilters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.Indicator;

import java.util.StringTokenizer;

public class DimensionFilterInterpreter {

    private final String dml;
    private DimensionFilter dimensionFilter;
    private final Indicator indicator;

    public DimensionFilterInterpreter(String dml, Indicator indicator) throws BIException {
        this.dml = dml;
        this.indicator = indicator;
        decodifica();
    }

    private void decodifica() throws BIException {
        dimensionFilter = buscaFiltro(dml);
    }

    private DimensionFilter buscaFiltro(String x) throws BIException {
        x = this.eliminaParentesesExtremos(x);
        int temp = -1;
        int aux1;
        int aux2;
        do {
            aux1 = x.indexOf(" and ", temp + 1);
            aux2 = x.indexOf(" or ", temp + 1);
            if (aux1 == -1 || (aux1 > aux2 && aux2 != -1))
                aux1 = aux2;
            temp = aux1;
            if (aux1 == -1) {
                return criaFiltro(x);
            }
        } while (!verificaConector(x.substring(0, aux1)));
        int pos;
        int posAnt = 0;
        int posAnt2 = 0;
        DimensionFilter f = new DimensionFilterJDBC();
        String conector = x.substring(aux1 + 1, x.indexOf(' ', aux1 + 1));
        f.setConnector(conector);
        while ((pos = x.indexOf(" " + conector + " ", posAnt)) != -1) {
            pos += conector.length() + 2;
            if (verificaConector(x.substring(0, pos))) {
                f.addDimensionFilter(buscaFiltro(x.substring(posAnt2, pos - (conector.length() + 2))));
                posAnt2 = pos;
            }
            posAnt = pos;
        }
        f.addDimensionFilter(buscaFiltro(x.substring(posAnt2)));
        return f;
    }

    private DimensionFilter criaFiltro(String x) throws BIException {
        StringTokenizer strTok = new StringTokenizer(x, " ");
        String nome = (String) strTok.nextElement();
        String apelido = "";
        if (nome.indexOf('.') != -1) {
            apelido = nome.substring(0, nome.indexOf('.'));
            nome = nome.substring(nome.indexOf('.') + 1);
        }
        Field campo = indicator.buscaFieldPorNome(apelido, nome);
        String operador = (String) strTok.nextElement();
        if (operador.equalsIgnoreCase("IS") || operador.equalsIgnoreCase("NOT")) {
            if (strTok.countTokens() > 1) {
                operador += " " + strTok.nextElement();
            }
        }

        x = x.substring(x.indexOf(operador));

        String valor = "";
        if (x.contains("'")) {
            valor += x.substring(x.indexOf("'"), x.lastIndexOf("'") + 1);
        } else {
            valor += (String) strTok.nextElement();
            if (operador.contains("IN(") && valor.lastIndexOf(")") == valor.length() - 1) {
                valor = valor.substring(0, valor.length() - 1);
            }
        }

        if (valor.contains("%") && !valor.startsWith("'%") && operador.equalsIgnoreCase("like")) {
            operador = operador + "%";
        }

        if (operador.equalsIgnoreCase("not like")) {
            operador = "notlike";
        }

        return FilterFactory.createDimensionFilter(campo, operador, valor);
    }

    private String eliminaParentesesExtremos(String x) {
        x = x.trim();
        while (x.charAt(0) == ' ') {
            x = x.substring(1);
        }
        if (x.charAt(0) == '(' && x.charAt(x.length() - 1) == ')') {
            x = x.substring(1);
            x = x.substring(0, x.length() - 1);
        }
        return x;
    }

    public DimensionFilter getFiltro() {
        return this.dimensionFilter;
    }

    private boolean verificaConector(String x) {
        int abre = 0;
        int fecha = 0;
        int aux;
        String strAux = x;
        while ((aux = strAux.indexOf("(")) != -1) {
            abre++;
            strAux = strAux.substring(aux + 1);
        }
        strAux = x;
        while ((aux = strAux.indexOf(")")) != -1) {
            fecha++;
            strAux = strAux.substring(aux + 1);
        }
        return abre == fecha;
    }
}
