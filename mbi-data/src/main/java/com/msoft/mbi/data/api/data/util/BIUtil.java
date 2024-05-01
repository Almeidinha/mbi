package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.cube.multi.column.MaskColumnMetaData;
import com.msoft.mbi.cube.multi.metadata.HTMLLineMask;
import com.msoft.mbi.cube.multi.metadata.MetaDataField;
import com.msoft.mbi.cube.multi.renderers.MaskMonth;
import com.msoft.mbi.cube.multi.renderers.MaskMonthYear;
import com.msoft.mbi.cube.multi.renderers.MaskPeriod;
import com.msoft.mbi.cube.multi.renderers.MaskWeek;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLColumnText;
import com.msoft.mbi.cube.multi.renderers.linkHTML.LinkHTMLText;
import com.msoft.mbi.data.api.data.exception.*;
import com.msoft.mbi.data.api.data.indicator.Field;
import com.msoft.mbi.data.api.dtos.user.BIUserDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BIUtil {
    private static final String NULL_DATE           = "31/12/1899";

    public static List<String> stringToList(String word) {
        return BIUtil.stringToList(word, "#!");
    }
    public static List<String> stringToList(String word, String delimiter) {
        if (word == null || delimiter == null || word.isEmpty()) {
            return Collections.emptyList();
        }

        String escapedDelimiter = Pattern.quote(delimiter);
        String[] splitWords = word.split(escapedDelimiter);

        List<String> result = new ArrayList<>();
        for (String splitWord : splitWords) {
            if (!splitWord.isEmpty()) {
                result.add(splitWord);
            }
        }

        return result;
    }

    public static Object textFormat(Field field, String valor) throws BIFilterException {
        try {
            String type = field.getDataType();
            if (Constants.STRING.equals(type)) {
                return formatStringToText(valor);
            } else if (Constants.NUMBER.equals(type)) {
                return formatDoubleToTextObject(valor, field.getNumDecimalPositions());
            } else {
                return formatDateToText(valor);
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

    private static String formatStringToText(String value) {
        if (value == null) {
            return "NULL";
        }

        String trimmedValue = value.trim();
        if (!trimmedValue.startsWith("'")) {
            trimmedValue = "'" + trimmedValue;
        }
        if (!trimmedValue.endsWith("'")) {
            trimmedValue += "'";
        }
        return trimmedValue;
    }

    private static Object formatDateToText(String value) throws BIException {
        if (value == null) {
            value = NULL_DATE;
        }

        // TODO get format from db
        return getFormattedDate(value, BIData.DAY_MONTH_YEAR_4DF);
    }

    public static String getFormattedDate(String date, String format) throws BIException {
        try {
            date = date.trim();
            LocalDate localDate = LocalDate.parse(date);

            DateTimeFormatter formatter;
            switch (format.trim()) {
                case BIData.DAY_MONTH_YEAR_4DF:
                    formatter = DateTimeFormatter.ofPattern(BIData.DAY_MONTH_YEAR_4DF);
                    break;
                case BIData.DAY_MONTH_YEAR_2DF:
                    formatter = DateTimeFormatter.ofPattern(BIData.DAY_MONTH_YEAR_2DF);
                    break;
                case BIData.MONTH_DAY_YEAR_4DF:
                    formatter = DateTimeFormatter.ofPattern(BIData.MONTH_DAY_YEAR_4DF);
                    break;
                case BIData.MONTH_DAY_YEAR_2DF:
                    formatter = DateTimeFormatter.ofPattern(BIData.MONTH_DAY_YEAR_2DF);
                    break;
                case BIData.YEAR_MONTH_DAY_4DF:
                    formatter = DateTimeFormatter.ofPattern(BIData.YEAR_MONTH_DAY_4DF);
                    break;
                case BIData.YEAR_MONTH_DAY_DASH_4DF:
                    formatter = DateTimeFormatter.ofPattern(BIData.YEAR_MONTH_DAY_DASH_4DF);
                    break;
                case BIData.DAY_MONTH_YEAR_DASH_4DF:
                    formatter = DateTimeFormatter.ofPattern(BIData.DAY_MONTH_YEAR_DASH_4DF);
                    break;
                case BIData.YEAR_MONTH_DAY_2D_NS:
                    formatter = DateTimeFormatter.ofPattern(BIData.YEAR_MONTH_DAY_2D_NS);
                    break;
                default:
                    return date;
            }
            return localDate.format(formatter);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ex) {
            throw BIException.builder()
                    .message("Error formatting the date")
                    .local("BIUtil")
                    .action("getFormattedDate(String, String)")
                    .build();
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
            sdf = new SimpleDateFormat(BIData.DAY_MONTH_YEAR_4DF);
            date = new Date(sdf.parse(BIUtil.getFormattedDate(value, BIData.DAY_MONTH_YEAR_4DF)).getTime());
        } catch (ParseException e) {
            String conexao = field.getIndicator().getConnectionId();
            // TODO Get this from db
            String format = BIData.DAY_MONTH_YEAR_4DF;
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

    public static void closeStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("Error closing PreparedStatement. Error: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.error("Erro ao Fechar PreparedStatement. Erro: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet set) {
        if (set != null) {
            try {
                set.close();
            } catch (SQLException e) {
                log.error("Erro ao Fechar ResultSet. Erro: " + e.getMessage());
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

            int last = 0;
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
                if (ultimoTemp != null && !ultimoTemp.isEmpty())
                        {last = Integer.parseInt(ultimoTemp);
                }
                return String.valueOf(last + 1);
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
        BigDecimal bd = BigDecimal.valueOf(value);
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

    public static String getDescNumber(int number) {
        return switch (number) {
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
    }

    public static int verifyNullInt(ResultSet results, String name) throws BIException {
        try {
            return Math.max(results.getInt(name), 0);
        } catch (NullPointerException exception) {
            BINullPointerException binullex = new BINullPointerException(exception);
            binullex.setAction("capturar name do banco.");
            binullex.setLocal("BIUtil", "verificanullInt(ResultSet, String)");
            throw binullex;
        } catch (SQLException sqle) {
            String tabela = "";
            try {
                ResultSetMetaData rmd = results.getMetaData();
                for (int i = 1; i <= rmd.getColumnCount(); i++) {
                    if (rmd.getColumnName(i).equalsIgnoreCase(name))
                        tabela = rmd.getTableName(i);
                }
            } catch (SQLException sqlException) {
                BIDatabaseException databaseException = new BIDatabaseException(sqlException);
                databaseException.setAction("capturar nome da tabela.");
                databaseException.setLocal("BIUtil", "verificanullInt(ResultSet, String)");
                throw databaseException;
            }
            log.info("Error in BIUtil.verifyNullInt() : " + sqle.getMessage());
            BIFieldNotFoundException notFoundException = new BIFieldNotFoundException(sqle, tabela, name);
            notFoundException.setAction("capturar campo do banco.");
            notFoundException.setLocal("BIUtil", "verificanullInt(ResultSet, String)");
            throw notFoundException;
        }
    }

    public static String verifyNullString(ResultSet results, String name) throws BIException {
        try {
            if (results.getString(name) != null && !results.getString(name).isEmpty()) {
                return results.getString(name).trim();
            } else {
                return "";
            }
        } catch (NullPointerException nullPointerException) {
            BINullPointerException biNullPointerException = new BINullPointerException(nullPointerException);
            biNullPointerException.setAction("capturar name do banco.");
            biNullPointerException.setLocal("BIUtil", "verificanullString(ResultSet, String)");
            throw biNullPointerException;
        } catch (SQLException sqlException) {
            String tabela = "";
            try {
                ResultSetMetaData rmd = results.getMetaData();
                for (int i = 1; i <= rmd.getColumnCount(); i++) {
                    if (rmd.getColumnName(i).equalsIgnoreCase(name))
                        tabela = rmd.getTableName(i);
                }
            } catch (SQLException exception) {
                BIDatabaseException bidbex = new BIDatabaseException(exception);
                bidbex.setAction("capturar nome da tabela.");
                bidbex.setLocal("BIUtil", "verificanullString(ResultSet, String)");
                throw bidbex;
            }
            BIFieldNotFoundException bifnfex = new BIFieldNotFoundException(sqlException, tabela, name);
            bifnfex.setAction("capturar nome da tabela.");
            bifnfex.setLocal("BIUtil", "verificanullString(ResultSet, String)");
            throw bifnfex;
        }
    }

    public static void removeTrailingComma(StringBuilder builder) {
        if (!builder.isEmpty() &&  builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    public static String quoteSQLString(String input, String quote) {

        Set<String> sqlKeywords = new HashSet<>(Set.of(
                "?", "<", ">", "=", "-", "+", "/", "*", "}", "{", "]", "[", ")", "(", ",", " ", ".", "MAX", "MIN",
                "SUM", "AND", "AS", "ASC", "BETWEEN", "COUNT", "BY", "CASE", "CURRENT_DATE", "CURRENT_TIME",
                "DELETE", "DESC", "INSERT", "DISTINCT", "EACH", "ELSEIF", "FALSE", "TOP",
                "FROM", "HAVING", "IF", "IN", "INTERVAL", "INTO", "IS", "INNER", "JOIN", "KEY",
                "KEYS", "LEFT", "LIKE", "LIMIT", "MATCH", "NOT", "NULL", "ON", "OPTION", "OR", "ORDER", "OUT",
                "OUTER", "REPLACE", "RIGHT", "SELECT", "SET", "TABLE", "THEN", "TO", "TRUE", "UPDATE", "VALUES",
                "WHEN", "WHERE", "DATE", "DECIMAL", "ELSE", "EXISTS", "FOR", "VARCHAR", "UNION", "GROUP",
                "WITH"));

        String[] words = input.trim()
                .replaceAll("\\n", " ")
                .replaceAll(" +", " ")
                .split("((?<=[\\s,.)(=\\-+/*])|(?=[\\s,.)(=\\-+/*]))");

        StringBuilder builder = new StringBuilder();

        for (String word : words) {
            if (!sqlKeywords.contains(word.toUpperCase()) && !StringUtils.isNumeric(word)) {
                word = quote + word + quote;
            }
            builder.append(word);
        }

        return builder.toString();
    }

    public static void setMetaDataFieldMask(MetaDataField metaDataField, Field field) {
        String fieldDateMask = field.getDateMask();

        if (!"".equalsIgnoreCase(field.getDateMask()) && fieldDateMask != null && !"".equalsIgnoreCase(field.getDataType())) {
            if (Constants.DATE.equalsIgnoreCase(field.getDataType())) {
                metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.DATA_TYPE));
            } else if (Constants.DIMENSION.equals(field.getFieldType()) && (Constants.NUMBER.equals(field.getDataType()) || Constants.STRING.equals(field.getDataType()))) {
                if (field.getName().equalsIgnoreCase("num_mes") && (fieldDateMask.equalsIgnoreCase(MaskMonth.ABBREVIATED)
                            || fieldDateMask.equalsIgnoreCase(MaskMonth.NOT_ABBREVIATED))) {
                    metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_MONTH));
                }
                if (field.getName().equalsIgnoreCase("num_dia_semana") && (fieldDateMask.equalsIgnoreCase(MaskWeek.ABBREVIATED)
                            || fieldDateMask.equalsIgnoreCase(MaskWeek.NOT_ABBREVIATED))) {
                    metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_WEEK));
                }
                if (field.getName().equalsIgnoreCase("num_bimestre") || field.getName().equalsIgnoreCase("num_trimestre")
                        || field.getName().equalsIgnoreCase("num_semestre") && (Constants.NUMBER.equals(field.getDataType()) && (MaskPeriod.validaMascara(fieldDateMask)))) {
                    metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_PERIOD));
                }
                if (field.getName().equalsIgnoreCase("ano_mes_dat") && (Constants.STRING.equals(field.getDataType()) && (MaskMonthYear.validaMascara(fieldDateMask)))) {
                    metaDataField.addMask(new MaskColumnMetaData(fieldDateMask, MaskColumnMetaData.TYPE_EIS_DIMENSION_MONTH_YEAR));
                }
            }
        } else if (Constants.DATE.equals(field.getDataType())) {
            metaDataField.addMask(new MaskColumnMetaData(BIData.DAY_MONTH_YEAR_4DF, MaskColumnMetaData.DATA_TYPE));
        }
    }

    public static int getFieldIndex(List<Field> fields, String code) throws BIException {
        int fieldId = Integer.parseInt(code);
        return IntStream.range(0, fields.size())
                .filter(i -> fields.get(i) != null && fields.get(i).getFieldId() == fieldId)
                .findFirst()
                .orElseThrow(() -> BIException.builder()
                        .message("Nao foi possivel encontrar o campo de codigo " + code + " no indicador atual.")
                        .action("buscar indice de um field")
                        .local("Indicator: getIndiceField(String)")
                        .build()
                );
    }

}
