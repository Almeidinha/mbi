package com.msoft.mbi.cube.multi.metaData;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.msoft.mbi.cube.multi.column.TipoTextoRoot;
import com.msoft.mbi.cube.multi.dimension.DimensaoMetaData;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CuboMetaData extends DimensaoMetaData {

    private List<CampoMetaData> camposDimensao;
    private List<CampoMetaData> camposMetrica;
    private List<CampoMetaData> campos;
    @Setter
    private String expressaoFiltrosMetrica;
    @Setter
    private String expressaoFiltrosAcumulado;
    @Serial
    private static final long serialVersionUID = 5087431304248017465L;

    public CuboMetaData() {
        super("Cubo", null, new TipoTextoRoot());
        this.camposDimensao = new ArrayList<>();
        this.camposMetrica = new ArrayList<>();
        this.campos = new ArrayList<>();
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
        this.campos.sort(comparator);
    }

    public void ordenaCamposDimensao() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        this.camposDimensao.sort(comparator);
    }

    public void ordenaCamposMetrica() {
        SequenciaCampoComparator comparator = new SequenciaCampoComparator();
        this.camposMetrica.sort(comparator);
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
