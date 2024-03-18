package com.msoft.mbi.cube.multi.calculation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bestcode.mathparser.IMathParser;
import com.bestcode.mathparser.MathParserFactory;

public class Calculo {

    protected String expressao = null;
    private transient IMathParser parser = null;
    private Map<String, String> mapVariables;
    private final String CONSTANTE_ACENTO_AGUDO = "_1";
    private final String CONSTANTE_ACENTO_CIRCUNFLEXO = "_2";
    private final String CONSTANTE_ACENTO_TIL = "_3";
    private final String CONSTANTE_CEDILHA = "_4";
    private final String CONSTANTE_CIFRAO = "_5";
    private final String CONSTANTE_ARROBA = "_6";
    private final String CONSTANTE_E_COMERCIAL = "_7";
    private final String CONSTANTE_ASTERISTICO = "_8";
    private final String CONSTANTE_SUSTENIDO = "_9";
    private final String CONSTANTE_EXCLAMCAO = "_10";
    private final String CONSTANTE_ASPAS_SIMPLES = "_11";
    private final String CONSTANTE_PORCENTO = "_13";
    private final String CONSTANTE_APOSTROFO = "_14";
    private final String CONSTANTE_MAIS = "_15";
    private final String CONSTANTE_IQUAL = "_16";
    private final String CONSTANTE_VIRGULA = "_17";
    private final String CONSTANTE_PONTO = "_18";
    private final String CONSTANTE_PONTO_VIRGULA = "_19";
    private final String CONSTANTE_DOIS_PONTO = "_20";
    private final String CONSTANTE_CONTRA_BARRA = "_21";
    private final String CONSTANTE_PIPE = "_22";
    private final String CONSTANTE_INTERROGACAO = "_23";
    private final String CONSTANTE_PRIMEIRO = "_24";
    private final String CONSTANTE_PRIMEIRA = "_25";
    private final String CONSTANTE_EURO = "_26";
    private final String CONSTANTE_C_CORTADO = "_27";
    private final String CONSTANTE_L_DEITADO = "_28";
    private final String CONSTANTE_SS = "_29";
    private final String CONSTANTE_EXPOENTE_UM = "_30";
    private final String CONSTANTE_EXPOENTE_DOIS = "_31";
    private final String CONSTANTE_EXPOENTE_TRES = "_32";

    // Constantes para operadores
    private final String CONSTANTE_OPERADOR_MAIS = "_33";
    private final String CONSTANTE_OPERADOR_MENOS = "_34";
    private final String CONSTANTE_OPERADOR_DIVISAO = "_35";
    private final String CONSTANTE_OPERADOR_MULTIPLICACAO = "_36";

    public Calculo(String expressao) {
        this.expressao = expressao;
        this.criaVariables();
        this.compilaExpressao();
    }

    public Map<String, String> getVariables() {
        return mapVariables;
    }

    public void putVariable(String variable, String titulo) {
        this.mapVariables.put(variable, titulo);
    }

    private String converteTituloParaVariable(String titulo) {
        String retorno = titulo.replaceAll(" ", "");
        retorno = retorno.toLowerCase();
        retorno = retorno.replaceAll("-", "");
        retorno = retorno.replaceAll("_", "");
        retorno = retorno.replaceAll("á", "a" + CONSTANTE_ACENTO_AGUDO);
        retorno = retorno.replaceAll("ã", "a" + CONSTANTE_ACENTO_TIL);
        retorno = retorno.replaceAll("é", "e" + CONSTANTE_ACENTO_AGUDO);
        retorno = retorno.replaceAll("ê", "e" + CONSTANTE_ACENTO_CIRCUNFLEXO);
        retorno = retorno.replaceAll("í", "i" + CONSTANTE_ACENTO_AGUDO);
        retorno = retorno.replaceAll("ó", "o" + CONSTANTE_ACENTO_AGUDO);
        retorno = retorno.replaceAll("ô", "o" + CONSTANTE_ACENTO_CIRCUNFLEXO);
        retorno = retorno.replaceAll("õ", "o" + CONSTANTE_ACENTO_TIL);
        retorno = retorno.replaceAll("ú", "u" + CONSTANTE_ACENTO_AGUDO);
        retorno = retorno.replaceAll("ç", "c" + CONSTANTE_CEDILHA);
        retorno = retorno.replaceAll("'", "" + CONSTANTE_ASPAS_SIMPLES);
        retorno = retorno.replaceAll("!", "" + CONSTANTE_EXCLAMCAO);
        retorno = retorno.replaceAll("@", "" + CONSTANTE_ARROBA);
        retorno = retorno.replaceAll("#", "" + CONSTANTE_SUSTENIDO);
        retorno = retorno.replaceAll("[$]", "" + CONSTANTE_CIFRAO);
        retorno = retorno.replaceAll("%", "" + CONSTANTE_PORCENTO);
        retorno = retorno.replaceAll("`", "" + CONSTANTE_APOSTROFO);
        retorno = retorno.replaceAll("&", "" + CONSTANTE_E_COMERCIAL);
        retorno = retorno.replaceAll("[*]", "" + CONSTANTE_ASTERISTICO);
        retorno = retorno.replaceAll("=", "" + CONSTANTE_IQUAL);
        retorno = retorno.replaceAll("[+]", "" + CONSTANTE_MAIS);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_SS);
        retorno = retorno.replaceAll("[|]", "" + CONSTANTE_PIPE);
        retorno = retorno.replaceAll(",", "" + CONSTANTE_VIRGULA);
        retorno = retorno.replaceAll("[.]", "" + CONSTANTE_PONTO);
        retorno = retorno.replaceAll(";", "" + CONSTANTE_PONTO_VIRGULA);
        retorno = retorno.replaceAll(":", "" + CONSTANTE_DOIS_PONTO);
        retorno = retorno.replaceAll("[?]", "" + CONSTANTE_INTERROGACAO);
        retorno = retorno.replaceAll("[/]", "" + CONSTANTE_CONTRA_BARRA);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_PRIMEIRO);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_PRIMEIRA);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_EURO);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_C_CORTADO);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_L_DEITADO);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_EXPOENTE_UM);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_EXPOENTE_DOIS);
        retorno = retorno.replaceAll("�", "" + CONSTANTE_EXPOENTE_TRES);
        retorno = retorno.replaceAll("\\(", "__");
        retorno = retorno.replaceAll("\\)", "__");
        retorno = retorno.indexOf("\\") == -1 ? retorno : retorno.replaceAll("\\\\", "___");
        return retorno;
    }

    private void compilaExpressao() {
        this.parser = MathParserFactory.create();
        this.parser.setExpression(this.expressao);
    }

    private void criaVariables() {
        this.mapVariables = new HashMap<String, String>();
        Pattern p = Pattern.compile("\\[([^\\]])*");
        Matcher m = p.matcher(this.expressao);
        while (m.find()) {
            String tituloCampo = m.group();
            tituloCampo = tituloCampo.substring(1);
            String tituloAux = tituloCampo;
            String variable = this.converteTituloParaVariable(tituloCampo);
            tituloAux = tituloAux.replaceAll("\\(", "\\\\(");
            tituloAux = tituloAux.replaceAll("\\)", "\\\\)");
            tituloAux = this.converteOperadoresParaConstantes(tituloAux);
            this.expressao = this.converteOperadoresParaConstantes(this.expressao);
            this.expressao = this.expressao.replaceAll("\\[" + tituloAux + "\\]", "[" + variable + "]");
            this.expressao = this.converteConstatesParaOperadores(this.expressao);
            this.putVariable(variable, tituloCampo);
        }
    }

    private String converteOperadoresParaConstantes(String string) {
        string = string.replaceAll("\\$", "###");
        string = string.replaceAll("[-]", CONSTANTE_OPERADOR_MENOS);
        string = string.replaceAll("[/]", CONSTANTE_OPERADOR_DIVISAO);
        string = string.replaceAll("[*]", CONSTANTE_OPERADOR_MULTIPLICACAO);
        string = string.replaceAll("[+]", CONSTANTE_OPERADOR_MAIS);
        string = string.replaceAll("[ ]", "");
        return string;
    }

    private String converteConstatesParaOperadores(String string) {
        string = string.replaceAll("###", "\\$");
        string = string.replaceAll(CONSTANTE_OPERADOR_MENOS, "\\-");
        string = string.replaceAll(CONSTANTE_OPERADOR_DIVISAO, "\\/");
        string = string.replaceAll(CONSTANTE_OPERADOR_MULTIPLICACAO, "\\*");
        string = string.replaceAll(CONSTANTE_OPERADOR_MAIS, "\\+");
        return string;
    }


    public void setValorVariable(String variable, Double valor) throws Exception {
        this.parser.setVariable(variable, (valor != null ? valor : 0));
    }

    public Double calculaValor() throws Exception {
        return this.parser.getValue();
    }

}
