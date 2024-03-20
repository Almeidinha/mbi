package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.exception.*;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.data.indicator.FieldComparator;
import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;

@Log4j2
public class BIUtil {

    public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
    private static final String NULL_DATE           = "31/12/1899";

    public static List<String> stringtoList(String word) {
        return BIUtil.stringtoList(word, "#!");
    }
    public static List<String> stringtoList(String word, String delimiter) {

        if (word == null || delimiter == null || word.isEmpty()) {
            return Collections.emptyList();
        }

        String escapedDelimiter = Pattern.quote(delimiter);
        word = word.replaceAll(escapedDelimiter + "$|^" + escapedDelimiter, "");

        List<String> result = Arrays.asList(word.split(escapedDelimiter));
        return (!result.isEmpty()) ? result : Collections.emptyList();
    }

    public static Object textFormat(Field field, String valor) throws BIFilterException {
        try {
            String type = field.getDataType();
            if (Constants.STRING.equals(type)) {
                return formatStringToText(valor);
            } else if (Constants.NUMBER.equals(type)) {
                return formatDoubleToTextObject(valor, field.getNumDecimalPositions());
            } else {
                return formatDateToText(valor, field);
            }
        } catch (Exception e) {
            log.error("Error in BIUtil.textFormat() : " + e.getMessage());
            throw new BIFilterException("Erro ao converter valor para o tipode dado correspondente.", e);
        }
    }

    public static Object formatDoubleToTextObject(String value, int decimalPositions) {
        if (value == null) {
            value = "0";
        }

        NumberFormat numberFormat = getNumberFormat(decimalPositions);

        try {
            double parsedValue = Double.parseDouble(value.trim());
            double formattedValue = formatDoubleValue(parsedValue, decimalPositions);
            return numberFormat.format(formattedValue);
        } catch (NumberFormatException e) {
            log.info("Error, canta parse the value " + value);
            return value;
        }
    }

    public static NumberFormat getNumberFormat(int decimalPositions) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(decimalPositions);
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setGroupingUsed(false);
        return numberFormat;
    }


    public static String formatDoubleToText(String value, int decimalPositions) {
       Object result = BIUtil.formatDoubleToTextObject(value, decimalPositions);
       return String.valueOf(result);
    }

    public static String formatDoubleToText(Object value, int decimalPositions) {
        Object result = BIUtil.formatDoubleToTextObject(String.valueOf(value), decimalPositions);
        return String.valueOf(result);
    }

    private static String formatStringToText(String valor) {
        if (valor == null) {
            return "NULL";
        }

        String trimmedValor = valor.trim();
        if (!trimmedValor.startsWith("'")) {
            trimmedValor = "'" + trimmedValor;
        }
        if (!trimmedValor.endsWith("'")) {
            trimmedValor += "'";
        }
        return trimmedValor;
    }

    private static Object formatDateToText(String valor, Field field) throws BIException {
        if (valor == null) {
            valor = NULL_DATE;
        }

        // TODO get format from db
        return getFormattedDate(valor,"dd/MM/yy");
    }

    public static String getFormattedDate(String date, String format) throws BIException {
        try {
            String dia;
            String mes;
            String ano;
            String ano_reduz;
            String data_total;
            date = date.trim();
            int num = date.trim().length();

            if (num == 10) {
                if ((date.charAt(4) == '/' || date.charAt(4) == '-')
                        && (date.charAt(7) == '/' || date.charAt(7) == '-')) {
                    ano = date.substring(0, 4);
                    mes = date.substring(5, 7);
                    dia = date.substring(8, 10);
                } else if ((date.charAt(2) == '/' || date.charAt(2) == '-')
                        && (date.charAt(5) == '/' || date.charAt(5) == '-')) {
                    ano = date.substring(6, 10);
                    mes = date.substring(3, 5);
                    dia = date.substring(0, 2);
                } else {
                    return date;
                }
            } else if (num == 12) {
                if ((date.charAt(5) == '/' || date.charAt(5) == '-')
                        && (date.charAt(8) == '/' || date.charAt(8) == '-')) {
                    ano = date.substring(1, 5);
                    mes = date.substring(6, 8);
                    dia = date.substring(9, 11);
                } else if ((date.charAt(3) == '/' || date.charAt(3) == '-')
                        && (date.charAt(6) == '/' || date.charAt(6) == '-')) {
                    ano = date.substring(7, 11);
                    mes = date.substring(4, 6);
                    dia = date.substring(1, 3);
                } else {
                    return date;
                }
            } else {
                return date;
            }

            ano_reduz = ano.substring(2, 4);

            switch (format.trim()) {
                case "dd/MM/yyyy" -> data_total = dia + "/" + mes + "/" + ano;
                case "dd/MM/yy" -> data_total = dia + "/" + mes + "/" + ano_reduz;
                case "MM/dd/yyyy" -> data_total = mes + "/" + dia + "/" + ano;
                case "MM/dd/yy" -> data_total = mes + "/" + dia + "/" + ano_reduz;
                case "yyyy/MM/dd" -> data_total = ano + "/" + mes + "/" + dia;
                case "yyyy-MM-dd" -> data_total = ano + "-" + mes + "-" + dia;
                case "dd-MM-yyyy" -> data_total = dia + "-" + mes + "-" + ano;
                case "yyyyMMdd" -> data_total = ano + mes + dia;
                default -> {
                    return date;
                }
            }
            return data_total;
        } catch (NullPointerException nullex) {
            BINullPointerException binullex = new BINullPointerException(nullex);
            binullex.setAction("formatar a data.");
            binullex.setLocal("BIUtil", "getFormattedDate(String, String)");
            throw binullex;
        } catch (ArrayIndexOutOfBoundsException arrex) {
            BIArrayIndexOutOfBoundsException biarex = new BIArrayIndexOutOfBoundsException(arrex);
            biarex.setAction("formatar a data.");
            biarex.setLocal("BIUtil", "getFormattedDate(String, String)");
            throw biarex;
        }
    }

    public static Object formatSQL(Field field, String valor) throws BIFilterException {
        try {
            String dataType = field.getDataType();
            if (Constants.STRING.equals(dataType)) {
                return formatStringSQL(valor);
            } else if (Constants.NUMBER.equals(dataType)) {
                return formatDoubleToTextObject(valor, field.getNumDecimalPositions());
            } else {
                return formatDateSQL(valor, field);
            }
        } catch (Exception e) {
            log.error("Error in BIUtil.formatSQL() : " + e.getMessage());
            throw new BIFilterException("Erro ao converter valor para o tipo de dado correspondente.", e);
        }
    }

    private static Object formatStringSQL(String valor) {
        if (valor == null) {
            valor = "NULL";
        }
        return valor.trim();
    }

    private static Object formatDateSQL(String value, Field field) throws ParseException {
        if (value == null) {
            value = NULL_DATE;
        }
        Date date = null;
        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
            date = new Date(sdf.parse(BIUtil.getFormattedDate(value, DEFAULT_DATE_FORMAT)).getTime());
        } catch (ParseException e) {
            String conexao = field.getIndicator().getConnectionId();
            // TODO Get this from db
            String format = DEFAULT_DATE_FORMAT;
            //String format = Configuracao.getInstanse().getConexao(conexao).getFormatoData();
            sdf = new SimpleDateFormat(format);
            try {
                date = new Date(sdf.parse(BIUtil.getFormattedDate(value, format)).getTime());
            } catch (BIException e1) {
                e1.printStackTrace();
            }
        } catch (BIException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static void closeStatement(PreparedStatement statement) throws BIException {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Erro ao Fechar PreparedStatement. Erro: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement statement) throws BIException {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.out.println("Erro ao Fechar PreparedStatement. Erro: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet set) throws BIException {
        if (set != null) {
            try {
                set.close();
            } catch (SQLException e) {
                System.out.println("Erro ao Fechar ResultSet. Erro: " + e.getMessage());
            }
        }
    }

    public static String getNewData(String field, String table) throws BIException {
        return BIUtil.getNewData(field, table, "");
    }

    public static String getNewData(String field, String table, String whereClause) throws BIException {
        ConnectionBean connectionBean = new ConnectionBean();
        try {
            return BIUtil.getNewData(field, table, whereClause, connectionBean);
        } finally {
            connectionBean.closeConnection();
        }

    }

    public static String getNewData(String campo, String table, String whereClause, ConnectionBean connectionBean) throws BIException {
        if (campo != null && !campo.isEmpty() && table != null && !table.isEmpty()) {

            int ultimo = 0;
            String ultimoTemp = "";
            String sqlNovoDado = "SELECT MAX(" + campo + ") AS maximo FROM " + table + " " + whereClause;
            ResultSet results = null;
            try {
                try {
                    results = connectionBean.executeSQL(sqlNovoDado);
                } catch (SQLException sqle) {
                    BISQLException bisqlex = new BISQLException(sqle, sqlNovoDado);
                    bisqlex.setAction("buscar o indice do novo registro.");
                    bisqlex.setLocal("BIUtil", "getNovoDado(String, String, String)");
                    throw bisqlex;
                }

                try {
                    while (results.next()) {
                        ultimoTemp = results.getString("maximo");
                    }
                    results.close();
                } catch (SQLException sqle1) {
                    BIDatabaseException bidbex = new BIDatabaseException(sqle1);
                    bidbex.setAction("identificar o indice do novo registro.");
                    bidbex.setLocal("util.BIUtil", "getNovoDado(String, String, String)");
                    throw bidbex;
                }
                if (ultimoTemp != null) {
                    if (!ultimoTemp.isEmpty())
                        ultimo = Integer.parseInt(ultimoTemp);
                }
                return String.valueOf(ultimo + 1);
            } finally {
                closeResultSet(results);
            }
        } else {
            throw new BIGeneralException("Erro ao buscar novo dado, campo ou tabela vazio.");
        }
    }

    public static String getMacroValues(String id, Integer userId) {
        // TODO load user from db
        BIUserDTO biUserDTO = new BIUserDTO();

        return getMacroValues(id, biUserDTO);
    }

    public static String getMacroValues(String id, BIUserDTO user) {
        if (BIMacro.LOGIN_USUARIO_LOGADO.equals(id)) {
            return user.getEmail();
        } else if (BIMacro.EMAIL_USUARIO_LOGADO.equals(id)) {
            return user.getEmail();
        } else if (BIMacro.NOME_USUARIO_LOGADO.equals(id)) {
            return user.getFirstName();
        }
        return id;
    }

    public static double formatDoubleValue(double value, int decimalPositions) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimalPositions, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double formatDoubleValue(String value, int decimalPositions) {
        return formatDoubleValue(Double.parseDouble(value), decimalPositions);
    }

    public static String replaceString(String strOrigem, String strVelha, String strNova) throws BIException {
        int i;
        try {
            while ((i = strOrigem.indexOf(strVelha)) != -1) {
                strOrigem = strOrigem.substring(0, i) + strNova + strOrigem.substring(i + strVelha.length());
            }
        } catch (NullPointerException nullex) {
            BINullPointerException biex = new BINullPointerException(nullex);
            biex.setAction("substituir uma determinada palavra por outra.");
            biex.setLocal("BIUtil", "substituirUmaString(String, String, String)");
            throw biex;
        }
        return strOrigem;
    }

    public static String replaceStringIgnoreCase(String strOrigem, String strVelha, String strNova) throws BIException {
        int i;
        try {
            while ((i = strOrigem.toUpperCase().indexOf(strVelha.toUpperCase())) != -1) {
                strOrigem = strOrigem.substring(0, i) + strNova + strOrigem.substring(i + strVelha.length());
            }
        } catch (NullPointerException nullex) {
            BINullPointerException biex = new BINullPointerException(nullex);
            biex.setAction("substituir uma determinada palavra por outra.");
            biex.setLocal("BIUtil.BIUtil", "substituirUmaString(String, String, String)");
            throw biex;
        }
        return strOrigem;
    }

    public static java.sql.Date formatSQLDate(String valor, String formato) throws BIDatabaseException{
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(formato);
            java.sql.Date date;

            if(!valor.trim().isEmpty()){
                date = new java.sql.Date(sdf.parse(valor).getTime());
            }else{
                date = new java.sql.Date(sdf.parse("01/01/1970").getTime());
            }

            return date;
        }catch(Exception e){
            throw new BIDatabaseException(e);
        }
    }

    public static String getDescNumber(int numero) {
        String desc_number = switch (numero) {
            case 0 -> "nenhuma";
            case 1 -> "uma";
            case 2 -> "duas";
            case 3 -> "três";
            case 4 -> "quatro";
            case 5 -> "cinco";
            case 6 -> "seis";
            case 7 -> "sete";
            case 8 -> "oito";
            case 9 -> "nove";
            case 10 -> "dez";
            default -> "";
        };
        return desc_number;
    }

    public static List<Hashtable<String, String>> getAggregationList() {
        List<Hashtable<String, String>> list_agregacao = new ArrayList<>();
        Hashtable<String, String> hash_agregacao = new Hashtable<>();

        hash_agregacao.put("agregacao", "VAZIO");
        hash_agregacao.put("descricao", "Sem Agregação");
        list_agregacao.add(hash_agregacao);

        hash_agregacao = new Hashtable<>();
        hash_agregacao.put("agregacao", "SUM");
        hash_agregacao.put("descricao", "Somatário");
        list_agregacao.add(hash_agregacao);

        hash_agregacao = new Hashtable<>();
        hash_agregacao.put("agregacao", "AVG");
        hash_agregacao.put("descricao", "Média");
        list_agregacao.add(hash_agregacao);

        hash_agregacao = new Hashtable<>();
        hash_agregacao.put("agregacao", "MIN");
        hash_agregacao.put("descricao", "Mínimo");
        list_agregacao.add(hash_agregacao);

        hash_agregacao = new Hashtable<>();
        hash_agregacao.put("agregacao", "MAX");
        hash_agregacao.put("descricao", "Máximo");
        list_agregacao.add(hash_agregacao);

        hash_agregacao = new Hashtable<>();
        hash_agregacao.put("agregacao", "COUNT");
        hash_agregacao.put("descricao", "Contagem");
        list_agregacao.add(hash_agregacao);

        hash_agregacao = new Hashtable<>();
        hash_agregacao.put("agregacao", "COUNT_DIST");
        hash_agregacao.put("descricao", "Contagem Distinta");
        list_agregacao.add(hash_agregacao);

        return list_agregacao;
    }

    public static List<Field> ordenaCampoPeloTitulo(List<Field> campos) {
        campos.sort(FieldComparator.ORDENACAO_TITULO);
        return campos;
    }

    public static List<Field> getFieldList(List<Field> campos) {
        List<Field> listaCampos = new ArrayList<>();
        for (Field campo : campos) {
            if (campo != null) {
                listaCampos.add(campo);
            }
        }
        return ordenaCampoPeloTitulo(listaCampos);
    }

    public static int verificanullInt(ResultSet results, String campo) throws BIException {
        try {
            return Math.max(results.getInt(campo), 0);
        } catch (NullPointerException npex) {
            BINullPointerException binullex = new BINullPointerException(npex);
            binullex.setAction("capturar campo do banco.");
            binullex.setLocal("BIUtil", "verificanullInt(ResultSet, String)");
            throw binullex;
        } catch (SQLException sqle) {
            String tabela = "";
            try {
                ResultSetMetaData rmd = results.getMetaData();
                for (int i = 1; i <= rmd.getColumnCount(); i++) {
                    if (rmd.getColumnName(i).equalsIgnoreCase(campo))
                        tabela = rmd.getTableName(i);
                }
            } catch (SQLException sqle1) {
                BIDatabaseException bidbex = new BIDatabaseException(sqle1);
                bidbex.setAction("capturar nome da tabela.");
                bidbex.setLocal("BIUtil", "verificanullInt(ResultSet, String)");
                throw bidbex;
            }
            sqle.printStackTrace();
            BIFieldNotFoundException bifnfex = new BIFieldNotFoundException(sqle, tabela, campo);
            bifnfex.setAction("capturar campo do banco.");
            bifnfex.setLocal("BIUtil", "verificanullInt(ResultSet, String)");
            throw bifnfex;
        }
    }

    public static String verificanullString(ResultSet results, String campo) throws BIException {
        try {
            if (results.getString(campo) != null && !results.getString(campo).equals("")) {
                return results.getString(campo).trim();
            } else {
                return "";
            }
        } catch (NullPointerException npex) {
            BINullPointerException binullex = new BINullPointerException(npex);
            binullex.setAction("capturar campo do banco.");
            binullex.setLocal("BIUtil", "verificanullString(ResultSet, String)");
            throw binullex;
        } catch (SQLException sqle) {
            String tabela = "";
            try {
                ResultSetMetaData rmd = results.getMetaData();
                for (int i = 1; i <= rmd.getColumnCount(); i++) {
                    if (rmd.getColumnName(i).equalsIgnoreCase(campo))
                        tabela = rmd.getTableName(i);
                }
            } catch (SQLException sqle1) {
                BIDatabaseException bidbex = new BIDatabaseException(sqle1);
                bidbex.setAction("capturar nome da tabela.");
                bidbex.setLocal("BIUtil", "verificanullString(ResultSet, String)");
                throw bidbex;
            }
            BIFieldNotFoundException bifnfex = new BIFieldNotFoundException(sqle, tabela, campo);
            bifnfex.setAction("capturar nome da tabela.");
            bifnfex.setLocal("BIUtil", "verificanullString(ResultSet, String)");
            throw bifnfex;
        }
    }

}
