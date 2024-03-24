package com.msoft.mbi.cube.multi.calculation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bestcode.mathparser.IMathParser;
import com.bestcode.mathparser.MathParserFactory;

import static com.msoft.mbi.cube.util.Constants.*;

public class Calculation {

    protected String expression;
    private transient IMathParser parser = null;
    private Map<String, String> mapVariables;

    public Calculation(String expression) {
        this.expression = expression;
        this.createVariables();
        this.compileExpression();
    }

    public Map<String, String> getVariables() {
        return mapVariables;
    }

    public void putVariable(String variable, String title) {
        this.mapVariables.put(variable, title);
    }

    private String convertTitleToVariable(String title) {

        String result = title.replaceAll("[-_\\s]", "")
                .toLowerCase()
                .replaceAll("á", "a" + CONSTANT_ACUTE)
                .replaceAll("ã", "a" + CONSTANT_TILDE)
                .replaceAll("é", "e" + CONSTANT_ACUTE)
                .replaceAll("ê", "e" + CONSTANT_CIRCUMFLEX)
                .replaceAll("í", "i" + CONSTANT_ACUTE)
                .replaceAll("ó", "o" + CONSTANT_ACUTE)
                .replaceAll("ô", "o" + CONSTANT_CIRCUMFLEX)
                .replaceAll("õ", "o" + CONSTANT_TILDE)
                .replaceAll("ú", "u" + CONSTANT_ACUTE)
                .replaceAll("ç", "c" + CONSTANT_CEDILLA)
                .replaceAll("'", CONSTANT_APOSTROPHE)
                .replaceAll("!", CONSTANT_EXCLAMATION)
                .replaceAll("@", CONSTANT_AT_SIGN)
                .replaceAll("#", CONSTANT_HASH)
                .replaceAll("[$]", CONSTANT_DOLLAR_SIGN)
                .replaceAll("%", CONSTANT_PERCENT)
                .replaceAll("`", CONSTANT_GRAVE_ACCENT)
                .replaceAll("&", CONSTANT_AMPERSAND)
                .replaceAll("[*]", CONSTANT_ASTERISK)
                .replaceAll("=", CONSTANT_EQUAL)
                .replaceAll("[+]", CONSTANT_PLUS)
                .replaceAll("§", SECTION_SIGN)
                .replaceAll("[|]", CONSTANT_VERTICAL_BAR)
                .replaceAll(",", CONSTANT_COMA)
                .replaceAll("[.]", CONSTANT_DOT)
                .replaceAll(";", CONSTANT_SEMICOLON)
                .replaceAll(":", CONSTANT_COLON)
                .replaceAll("[?]", CONSTANT_QUESTION_MARK)
                .replaceAll("[/]", CONSTANT_SLASH)
                .replaceAll("ª", FEMININE_ORDINAL)
                .replaceAll("º", MASCULINE_ORDINAL)
                .replaceAll("£", POUND_SIGN)
                .replaceAll("¢", CENT_SIGN)
                .replaceAll("¬", NOT_SIGN)
                .replaceAll("¹", CONSTANT_SUPERSCRIPT_ONE)
                .replaceAll("²", CONSTANT_SUPERSCRIPT_TWO)
                .replaceAll("³", CONSTANT_SUPERSCRIPT_THREE)
                .replaceAll("\\(", "__")
                .replaceAll("\\)", "__");

        result = !result.contains("\\") ? result : result.replaceAll("\\\\", "___");

        return result;
    }


    private void compileExpression() {
        this.parser = MathParserFactory.create();
        this.parser.setExpression(this.expression);
    }

    private void createVariables() {
        this.mapVariables = new HashMap<>();
        Pattern p = Pattern.compile("\\[([^]]*)]");
        Matcher m = p.matcher(this.expression);

        while (m.find()) {
            String title = m.group(1);
            String variable = this.convertTitleToVariable(title);
            String regex = Pattern.quote("[" + title + "]");
            this.expression = this.expression.replaceAll(regex, "[" + variable + "]");
            this.putVariable(variable, title);
        }

        this.expression = this.convertOperatorsToConstants(this.expression);
        this.expression = this.convertConstantsToOperators(this.expression);
    }

    private String convertOperatorsToConstants(String string) {
        string = string.replace("$", "###")
                .replace("-", CONSTANT_OPERATOR_MINUS)
                .replace("/", CONSTANT_OPERATOR_DIVISION)
                .replace("*", CONSTANT_OPERATOR_MULTIPLY)
                .replace("+", CONSTANT_OPERATOR_PLUS)
                .replaceAll("\\s", "");
        return string;
    }

    private String convertConstantsToOperators(String string) {
        string = string.replace("###", "\\$")
                .replace(CONSTANT_OPERATOR_MINUS, "-")
                .replace(CONSTANT_OPERATOR_DIVISION, "/")
                .replace(CONSTANT_OPERATOR_MULTIPLY, "*")
                .replace(CONSTANT_OPERATOR_PLUS, "+");
        return string;
    }


    public void setVariableValue(String variable, Double value) throws Exception {
        this.parser.setVariable(variable, (value != null ? value : 0));
    }

    public Double calculateValue() throws Exception {
        return this.parser.getValue();
    }

}
