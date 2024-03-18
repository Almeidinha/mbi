package com.msoft.mbi.cube.multi.metaData;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.msoft.mbi.cube.multi.column.TipoTextoRoot;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;

public class CuboMetaData extends DimensaoMetaData {

    private List<CampoMetaData> camposDimensao;
    private List<CampoMetaData> camposMetrica;
    private List<CampoMetaData> campos;
    private String expressaoFiltrosMetrica;
    private String expressaoFiltrosAcumulado;
    @Serial
    private static final long serialVersionUID = 5087431304248017465L;

    public CuboMetaData() {
        super("Cubo", null, new TipoTextoRoot());
        this.camposDimensao = new ArrayList<>();
        this.camposMetrica = new ArrayList<>();
        this.campos = new ArrayList<>();
    }

    public List<CampoMetaData> getCamposDimensao() {
        return camposDimensao;
    }

    public List<CampoMetaData> getCamposMetrica() {
        return camposMetrica;
    }

    public String getExpressaoFiltrosMetrica() {
        return expressaoFiltrosMetrica;
    }

    public void setExpressaoFiltrosMetrica(String expressaoFiltrosMetrica) {
        this.expressaoFiltrosMetrica = expressaoFiltrosMetrica;
    }

    public List<CampoMetaData> getCampos() {
        return campos;
    }

    public String getExpressaoFiltrosAcumulado() {
        return expressaoFiltrosAcumulado;
    }

    public void setExpressaoFiltrosAcumulado(String expressaoFiltrosAcumulado) {
        this.expressaoFiltrosAcumulado = expressaoFiltrosAcumulado;
    }

    public void addCampo(CampoMetaData campo, String tipo) {
        if (CampoMetaData.DIMENSAO.equals(tipo)) {
            this.camposDimensao.add(campo);
        } else {
            this.camposMetrica.add(campo);
        }
        campo.setTipoCampo(tipo);
        this.campos.add(campo);
    }

    public void ordenaCampos() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        Collections.sort(this.campos, comparator);
    }

    public void ordenaCamposDimensao() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        Collections.sort(this.camposDimensao, comparator);
    }

    public void ordenaCamposMetrica() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        Collections.sort(this.camposMetrica, comparator);
    }

    public CampoMetaData getCampoMetricaByCodigo(int codigo) {
        for (CampoMetaData campo : this.camposMetrica) {
            if (campo.getCampo() == codigo) {
                return campo;
            }
        }
        return null;
    }

}
