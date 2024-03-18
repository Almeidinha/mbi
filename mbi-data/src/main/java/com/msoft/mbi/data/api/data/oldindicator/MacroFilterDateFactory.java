package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.util.BIMacro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MacroFilterDateFactory extends MacroFilterFactory {

    private final BIMacro macro;
    private final String increment;

    public MacroFilterDateFactory(Field field, BIMacro macro, String increment, Operator operator) throws BIException {
        this.macro = macro;
        this.increment = increment;
        super.operators = new ArrayList<>();
        super.values = new ArrayList<>();
        super.setField(field);

        if (macro.getId().equals(BIMacro.HOJE) || macro.getId().equals(BIMacro.ONTEM)) {
            super.operators.add(operator.clone());
        }

        this.unravelMacro();
    }

    @Override
    public void unravelMacro() throws BIException {
        GregorianCalendar data_hoje = new GregorianCalendar();
        if (macro.getIncrementalField() > 0 && increment != null && !increment.trim().isEmpty()) {
            data_hoje.add(macro.getIncrementalField(), Integer.parseInt(increment));
        }

        if (macro.getId().equals(BIMacro.HOJE)) {
            super.values.add(this.getFormattedDate(data_hoje));

        } else if (macro.getId().equals(BIMacro.ONTEM)) {
            data_hoje.add(Calendar.DAY_OF_MONTH, -1);

            super.values.add(this.getFormattedDate(data_hoje));
        } else if (macro.getId().equals(BIMacro.ATEHOJE)) {
            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(data_hoje));

        } else if (macro.getId().equals(BIMacro.SEMANAATUAL)) {

            int dia_semana_atual = data_hoje.get(Calendar.DAY_OF_WEEK);
            int diferenca_primeiro_dia = dia_semana_atual - 1;

            data_hoje.add(Calendar.DAY_OF_MONTH, -diferenca_primeiro_dia);
            String primeiro_dia = String.valueOf(data_hoje.get(Calendar.DAY_OF_MONTH));
            String mes_atual_primeiro_dia = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            String ano_atual_primeiro_dia = String.valueOf(data_hoje.get(Calendar.YEAR));

            data_hoje.add(Calendar.DAY_OF_MONTH, 6);

            String ultimo_dia = String.valueOf(data_hoje.get(Calendar.DAY_OF_MONTH));
            String mes_atual_ultimo_dia = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            String ano_atual_ultimo_dia = String.valueOf(data_hoje.get(Calendar.YEAR));

            if (primeiro_dia.length() == 1) {
                primeiro_dia = "0" + primeiro_dia;
            }
            if (ultimo_dia.length() == 1) {
                ultimo_dia = "0" + ultimo_dia;
            }

            if (mes_atual_primeiro_dia.length() == 1) {
                mes_atual_primeiro_dia = "0" + mes_atual_primeiro_dia;
            }
            if (mes_atual_ultimo_dia.length() == 1) {
                mes_atual_ultimo_dia = "0" + mes_atual_ultimo_dia;
            }

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate(primeiro_dia, mes_atual_primeiro_dia, ano_atual_primeiro_dia));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(ultimo_dia, mes_atual_ultimo_dia, ano_atual_ultimo_dia));

        } else if (macro.getId().equals(BIMacro.SEMANAANTERIOR)) {

            data_hoje.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);

            int dia_semana_atual = data_hoje.get(Calendar.DAY_OF_WEEK);
            int diferenca_primeiro_dia = dia_semana_atual - 1;

            data_hoje.add(Calendar.DAY_OF_MONTH, -diferenca_primeiro_dia);
            String primeiro_dia = String.valueOf(data_hoje.get(Calendar.DAY_OF_MONTH));
            String mes_atual_primeiro_dia = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            String ano_atual_primeiro_dia = String.valueOf(data_hoje.get(Calendar.YEAR));

            data_hoje.add(Calendar.DAY_OF_MONTH, 6);

            String ultimo_dia = String.valueOf(data_hoje.get(Calendar.DAY_OF_MONTH));
            String mes_atual_ultimo_dia = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            String ano_atual_ultimo_dia = String.valueOf(data_hoje.get(Calendar.YEAR));

            if (primeiro_dia.length() == 1) {
                primeiro_dia = "0" + primeiro_dia;
            }
            if (ultimo_dia.length() == 1) {
                ultimo_dia = "0" + ultimo_dia;
            }

            if (mes_atual_primeiro_dia.length() == 1) {
                mes_atual_primeiro_dia = "0" + mes_atual_primeiro_dia;
            }
            if (mes_atual_ultimo_dia.length() == 1) {
                mes_atual_ultimo_dia = "0" + mes_atual_ultimo_dia;
            }

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate(primeiro_dia, mes_atual_primeiro_dia, ano_atual_primeiro_dia));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(ultimo_dia, mes_atual_ultimo_dia, ano_atual_ultimo_dia));

        } else if (macro.getId().equals(BIMacro.MESATUAL)) {
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }
            String primeiro_dia = "01";
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate(primeiro_dia, mes_atual, ano_atual));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(ultimo_dia, mes_atual, ano_atual));

        } else if (macro.getId().equals(BIMacro.ANOATUAL)) {
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate("01", "01", ano_atual));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate("31", "12", ano_atual));

        } else if (macro.getId().equals(BIMacro.MESATUALHOJE)) {
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate("01", mes_atual, ano_atual));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(data_hoje));

        } else if (macro.getId().equals(BIMacro.MESATUALONTEM)) {
            data_hoje.add(Calendar.DATE, -1);

            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate("01", mes_atual, ano_atual));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(data_hoje));

        } else if (macro.getId().equals(BIMacro.ANOHOJE)) {
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate("01", "01", ano_atual));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(data_hoje));

        } else if (macro.getId().equals(BIMacro.MESANTERIOR)) {
            // GregorianCalendar data_fim = new GregorianCalendar();
            data_hoje.add(Calendar.MONTH, -1);

            String ano_final = String.valueOf(data_hoje.get(Calendar.YEAR));
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_final = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }
            if (mes_final.length() == 1) {
                mes_final = "0" + mes_final;
            }
            String primeiro_dia = "01";
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate(primeiro_dia, mes_atual, ano_atual));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate(ultimo_dia, mes_final, ano_final));

        } else if (macro.getId().equals(BIMacro.ANOANTERIOR)) {
            data_hoje.add(Calendar.YEAR, -1);

            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));

            super.operators.add(new Operator(">="));
            super.values.add(this.getFormattedDate("01", "01", ano_atual));

            super.operators.add(new Operator("<="));
            super.values.add(this.getFormattedDate("31", "12", ano_atual));

        } else if (macro.getId().equals(BIMacro.ULT15DIAS)) {

            String data_de_hoje = this.getFormattedDate(data_hoje);
            data_hoje.add(Calendar.DAY_OF_MONTH, -15);
            String data_de_15_dias_atras = this.getFormattedDate(data_hoje);

            super.operators.add(new Operator(">="));
            super.values.add(data_de_15_dias_atras);

            super.operators.add(new Operator("<="));
            super.values.add(data_de_hoje);

        } else if (macro.getId().equals(BIMacro.ULT30DIAS)) {

            String data_de_hoje = this.getFormattedDate(data_hoje);
            data_hoje.add(Calendar.DAY_OF_MONTH, -30);
            String data_de_30_dias_atras = this.getFormattedDate(data_hoje);

            super.operators.add(new Operator(">="));
            super.values.add(data_de_30_dias_atras);

            super.operators.add(new Operator("<="));
            super.values.add(data_de_hoje);

        } else if (macro.getId().equals(BIMacro.ULT3MESES)) {
            data_hoje.add(Calendar.MONTH, -1);
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            String data_final = this.getFormattedDate(ultimo_dia, mes_atual, ano_atual);

            data_hoje.add(Calendar.MONTH, -2);

            String ano_3_meses_atras = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_3_meses_atras = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_3_meses_atras.length() == 1) {
                mes_3_meses_atras = "0" + mes_3_meses_atras;
            }

            String data_3_meses_atras = this.getFormattedDate("01", mes_3_meses_atras, ano_3_meses_atras);

            super.operators.add(new Operator(">="));
            super.values.add(data_3_meses_atras);

            super.operators.add(new Operator("<="));
            super.values.add(data_final);

        } else if (macro.getId().equals(BIMacro.ULT6MESES)) {
            data_hoje.add(Calendar.MONTH, -1);
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            String data_final = this.getFormattedDate(ultimo_dia, mes_atual, ano_atual);

            data_hoje.add(Calendar.MONTH, -5);

            String ano_6_meses_atras = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_6_meses_atras = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_6_meses_atras.length() == 1) {
                mes_6_meses_atras = "0" + mes_6_meses_atras;
            }

            String data_6_meses_atras = this.getFormattedDate("01", mes_6_meses_atras, ano_6_meses_atras);

            super.operators.add(new Operator(">="));
            super.values.add(data_6_meses_atras);

            super.operators.add(new Operator("<="));
            super.values.add(data_final);

        } else if (macro.getId().equals(BIMacro.ULT12MESES)) {
            data_hoje.add(Calendar.MONTH, -1);
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            String data_final = this.getFormattedDate(ultimo_dia, mes_atual, ano_atual);

            data_hoje.add(Calendar.MONTH, -11);

            String ano_12_meses_atras = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_12_meses_atras = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_12_meses_atras.length() == 1) {
                mes_12_meses_atras = "0" + mes_12_meses_atras;
            }

            String data_12_meses_atras = this.getFormattedDate("01", mes_12_meses_atras, ano_12_meses_atras);

            super.operators.add(new Operator(">="));
            super.values.add(data_12_meses_atras);

            super.operators.add(new Operator("<="));
            super.values.add(data_final);
        } else if (macro.getId().equals(BIMacro.ULT13MESES)) {
            // data_hoje.add(Calendar.MONTH, -1);

            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);

            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }

            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            String data_final = this.getFormattedDate(ultimo_dia, mes_atual, ano_atual);

            data_hoje.add(Calendar.MONTH, -12);

            String ano_13_meses_atras = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_13_meses_atras = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_13_meses_atras.length() == 1) {
                mes_13_meses_atras = "0" + mes_13_meses_atras;
            }

            String data_13_meses_atras = this.getFormattedDate("01", mes_13_meses_atras, ano_13_meses_atras);

            super.operators.add(new Operator(">="));
            super.values.add(data_13_meses_atras);

            super.operators.add(new Operator("<="));
            super.values.add(data_final);

        } else if (macro.getId().equals(BIMacro.PROX15DIAS)) {
            String data_de_hoje = this.getFormattedDate(data_hoje);
            data_hoje.add(Calendar.DAY_OF_MONTH, 15);
            String data_de_proximo_15_dias = this.getFormattedDate(data_hoje);

            super.operators.add(new Operator(">="));
            super.values.add(data_de_hoje);

            super.operators.add(new Operator("<="));
            super.values.add(data_de_proximo_15_dias);
        } else if (macro.getId().equals(BIMacro.PROX30DIAS)) {
            String data_de_hoje = this.getFormattedDate(data_hoje);
            data_hoje.add(Calendar.DAY_OF_MONTH, 30);
            String data_de_proximo_30_dias = this.getFormattedDate(data_hoje);

            super.operators.add(new Operator(">="));
            super.values.add(data_de_hoje);

            super.operators.add(new Operator("<="));
            super.values.add(data_de_proximo_30_dias);
        } else if (macro.getId().equals(BIMacro.PROX3MESES)) {
            data_hoje.add(Calendar.MONTH, 1);
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }

            String data_atual = this.getFormattedDate("01", mes_atual, ano_atual);

            data_hoje.add(Calendar.MONTH, 2);

            String ano_proximos_3_meses = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_proximos_3_meses = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_proximos_3_meses.length() == 1) {
                mes_proximos_3_meses = "0" + mes_proximos_3_meses;
            }
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            String data_proximos_3_meses = this.getFormattedDate(ultimo_dia, mes_proximos_3_meses, ano_proximos_3_meses);

            super.operators.add(new Operator(">="));
            super.values.add(data_atual);

            super.operators.add(new Operator("<="));
            super.values.add(data_proximos_3_meses);

        } else if (macro.getId().equals(BIMacro.PROX6MESES)) {
            data_hoje.add(Calendar.MONTH, 1);
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }

            String data_atual = this.getFormattedDate("01", mes_atual, ano_atual);

            data_hoje.add(Calendar.MONTH, 5);

            String ano_proximos_6_meses = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_proximos_6_meses = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_proximos_6_meses.length() == 1) {
                mes_proximos_6_meses = "0" + mes_proximos_6_meses;
            }
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            String data_proximos_6_meses = this.getFormattedDate(ultimo_dia, mes_proximos_6_meses, ano_proximos_6_meses);

            super.operators.add(new Operator(">="));
            super.values.add(data_atual);

            super.operators.add(new Operator("<="));
            super.values.add(data_proximos_6_meses);

        } else if (macro.getId().equals(BIMacro.PROX12MESES)) {
            data_hoje.add(Calendar.MONTH, 1);
            String ano_atual = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_atual = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_atual.length() == 1) {
                mes_atual = "0" + mes_atual;
            }

            String data_atual = this.getFormattedDate("01", mes_atual, ano_atual);

            data_hoje.add(Calendar.MONTH, 11);

            String ano_proximos_12_meses = String.valueOf(data_hoje.get(Calendar.YEAR));
            String mes_proximos_12_meses = String.valueOf(data_hoje.get(Calendar.MONTH) + 1);
            if (mes_proximos_12_meses.length() == 1) {
                mes_proximos_12_meses = "0" + mes_proximos_12_meses;
            }
            String ultimo_dia = String.valueOf(data_hoje.getActualMaximum(Calendar.DAY_OF_MONTH));

            String data_proximos_12_meses = this.getFormattedDate(ultimo_dia, mes_proximos_12_meses, ano_proximos_12_meses);

            super.operators.add(new Operator(">="));
            super.values.add(data_atual);

            super.operators.add(new Operator("<="));
            super.values.add(data_proximos_12_meses);
        }
    }

    private String getFormattedDate(GregorianCalendar hoje) throws BIException {
        try {
            String conexao = this.getField().getIndicator().getConnectionId();
            // String formatoBanco = Configuracao.getInstanse().getConexao(conexao).getFormatoData();
            // TODO Het this from DB
            String format = "dd/MM/yyyy";
            Date date = hoje.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            throw new BIException(e);
        }
    }

    private String getFormattedDate(String dia, String mes, String ano) throws BIException {
        try {
            String conexao = this.getField().getIndicator().getConnectionId();
            //String formatoBanco = Configuracao.getInstanse().getConexao(conexao).getFormatoData();
            // TODO Het this from DB
            String format = "dd/MM/yyyy";
            
            String data_hoje = dia + "/" + mes + "/" + ano;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(data_hoje);
            sdf.applyPattern(format);
            return sdf.format(date);
        } catch (Exception e) {
            throw new BIException(e);
        }
    }
}
