package com.msoft.mbi.data.api.data.oldindicator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.msoft.mbi.data.api.data.util.BIMacro;
import com.msoft.mbi.data.api.data.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class BIMacros {

    ArrayList<BIMacro> biMacros;

    public BIMacros() {
        this.initiateMacros();
    }

    public ArrayList<BIMacro> getMacros() {
        return this.biMacros;
    }

    private ArrayList<BIMacro> getMacros(String fieldType) {
        ArrayList<BIMacro> a = new ArrayList<>();
        for (BIMacro m : this.biMacros) {
            if (m.getFieldType().equals(fieldType))
                a.add(m);
        }
        return a;
    }

    public ArrayList<BIMacro> getDateMacros() {
        return this.getMacros(Constants.DATE);
    }

    public ArrayList<BIMacro> getTextMacros() {
        return this.getMacros(Constants.STRING);
    }

    public String getMacrosAsJson(String type){
        JsonArray data_json = new JsonArray();

        for (BIMacro macro : type.equals("data") ? this.getMacros(Constants.DATE) : this.getMacros(Constants.STRING)) {
            JsonObject json = new JsonObject();
            json.addProperty("ID", macro.getId());
            json.addProperty("DESCRIPTION", macro.getDescription());
            json.addProperty("TYPE", macro.getFieldType());
            json.addProperty("INCREMENTAL_FIELD", macro.getIncrementalField());
            data_json.add(json);
        }
        return data_json.toString();
    }
    
    public BIMacro getMacroById(String id) {
        BIMacro macro_temp;
        BIMacro macro = null;
        for (BIMacro biMacro : biMacros) {
            macro_temp = biMacro;
            if (macro_temp.getId().equals(id)) {
                macro = macro_temp;
                break;
            }
        }
        return macro;
    }

    private void initiateMacros() {
        BIMacro macro;
        this.biMacros = new ArrayList<>();

        macro = new BIMacro();
        macro.setID(BIMacro.ANOANTERIOR);
        macro.setDescription("Ano Anterior");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(Calendar.YEAR);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ANOHOJE);
        macro.setDescription("Ano até Hoje");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ANOATUAL);
        macro.setDescription("Ano Atual");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(Calendar.YEAR);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ATEHOJE);
        macro.setDescription("Até Hoje");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(Calendar.DAY_OF_MONTH);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.HOJE);
        macro.setDescription("Hoje");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(Calendar.DAY_OF_MONTH);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.MESANTERIOR);
        macro.setDescription("Mês Anterior");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(Calendar.MONTH);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.MESATUAL);
        macro.setDescription("Mês Atual");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(Calendar.MONTH);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.MESATUALHOJE);
        macro.setDescription("Mês Atual até Hoje");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.MESATUALONTEM);
        macro.setDescription("Mês Atual até Ontem");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ONTEM);
        macro.setDescription("Ontem");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.PROX15DIAS);
        macro.setDescription("Próximos 15 dias");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.PROX30DIAS);
        macro.setDescription("Próximos 30 dias");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.PROX3MESES);
        macro.setDescription("Próximos 3 meses");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.PROX6MESES);
        macro.setDescription("Próximos 6 meses");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.PROX12MESES);
        macro.setDescription("Próximos 12 meses");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.SEMANAANTERIOR);
        macro.setDescription("Semana Anterior");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.SEMANAATUAL);
        macro.setDescription("Semana Atual");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ULT15DIAS);
        macro.setDescription("últimos 15 dias");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ULT30DIAS);
        macro.setDescription("últimos 30 dias");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ULT3MESES);
        macro.setDescription("últimos 3 meses");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ULT6MESES);
        macro.setDescription("últimos 6 meses");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ULT12MESES);
        macro.setDescription("últimos 12 meses");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.ULT13MESES);
        macro.setDescription("últimos 13 meses");
        macro.setFieldType(Constants.DATE);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.LOGIN_USUARIO_LOGADO);
        macro.setDescription("Login Usuário Logado");
        macro.setFieldType(Constants.STRING);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.NOME_USUARIO_LOGADO);
        macro.setDescription("Nome Usuário Logado");
        macro.setFieldType(Constants.STRING);
        macro.setIncrementalField(0);
        biMacros.add(macro);

        macro = new BIMacro();
        macro.setID(BIMacro.EMAIL_USUARIO_LOGADO);
        macro.setDescription("E-mail Usuário Logado");
        macro.setFieldType(Constants.STRING);
        macro.setIncrementalField(0);
        biMacros.add(macro);
    }
}
