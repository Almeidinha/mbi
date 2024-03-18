package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.consult.CachedResults;
import com.msoft.mbi.data.api.data.exception.BIException;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Expression {

    private ArrayList<Object> expressionParts = new ArrayList<>();

    public Expression() {
    }

    public Expression(ArrayList<Object> objetosExpressao) {
        this.carregaExpressao(objetosExpressao);
    }

    public void carregaExpressao(ArrayList<Object> objetosExpressao) {
        Object obj_parte;
        Parenthesis parenthesis;

        for (int i = 0; i < objetosExpressao.size(); i++) {
            obj_parte = objetosExpressao.get(i);
            if (obj_parte instanceof Parenthesis) {
                parenthesis = (Parenthesis) obj_parte;
                if (parenthesis.getType() == Parenthesis.ABRE) {
                    expressionParts.add(new Expression(this.getArrayExpressao(parenthesis.getNivel(), objetosExpressao)));
                    i = this.getIndiceFinalExpressao(parenthesis.getNivel(), objetosExpressao);
                }

            } else {
                expressionParts.add(obj_parte);
            }
        }
    }

    private ArrayList<Object> getArrayExpressao(String nivel_parentese, ArrayList<Object> objetosExpressao) {
        Object obj_parte;
        Parenthesis parentese;
        int indice_inicial = 0;
        int indice_final = 0;
        for (int i = 0; i < objetosExpressao.size(); i++) {
            obj_parte = objetosExpressao.get(i);
            if (obj_parte instanceof Parenthesis) {
                parentese = (Parenthesis) obj_parte;
                if (parentese.getNivel().equals(nivel_parentese) && parentese.getType() == Parenthesis.ABRE) {
                    indice_inicial = i;
                } else if (parentese.getNivel().equals(nivel_parentese) && parentese.getType() == Parenthesis.FECHA) {
                    indice_final = i;
                }
            }
        }
        ArrayList<Object> newArrayExpressao = new ArrayList<Object>();

        for (int x = (indice_inicial + 1); x < indice_final; x++) {
            obj_parte = objetosExpressao.get(x);
            newArrayExpressao.add(obj_parte);
        }

        return newArrayExpressao;
    }

    private int getIndiceFinalExpressao(String nivel_parentese, ArrayList<Object> objetosExpressao) {
        Object obj_parte;
        Parenthesis parentese;
        for (int i = 0; i < objetosExpressao.size(); i++) {
            obj_parte = objetosExpressao.get(i);
            if (obj_parte instanceof Parenthesis) {
                parentese = (Parenthesis) obj_parte;
                if (parentese.getNivel().equals(nivel_parentese) && parentese.getType() == Parenthesis.FECHA) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void addParteExpressao(Object parte) {
        if (expressionParts == null) {
            expressionParts = new ArrayList<>();
        }
        expressionParts.add(parte);
    }

    public Object getParteExpressao(int index) {
        Object parte = null;
        if (expressionParts != null && index >= 0) {
            parte = expressionParts.get(index);
        }
        return parte;
    }

    public double calculaExpressao(CachedResults registros_tabela) throws BIException {
        return calculaExpressao(this, registros_tabela);
    }

    public double calculaExpressao(Expression expressao, CachedResults registros_tabela) throws BIException {
        ArrayList<Object> partes_expressao;
        Object obj_parte;
        boolean achouOperador = false;
        double resultadoFinal = 0;
        double valorAux = 0;
        ArithmeticOperator operadorEncontrado = null;
        if (expressao != null) {
            partes_expressao = expressao.getExpressionParts();
            if (partes_expressao != null) {
                for (Object o : partes_expressao) {
                    obj_parte = o;
                    if (obj_parte != null) {
                        if (obj_parte instanceof Field campo) {
                            if (campo.isExpression() && campo.getName().toLowerCase().startsWith("se(")) {
                                ConditionalExpression expressaoCondicional = campo.getIndicator().getExpressaoCondicional(campo.getName());
                                valorAux = campo.getIndicator().calculaExpressaoCondicional(expressaoCondicional, registros_tabela);
                            } else {
                                valorAux = registros_tabela.getDouble(campo.getCode());
                            }
                            if (achouOperador) {
                                if (operadorEncontrado.getSymbol().equalsIgnoreCase("+")) {
                                    resultadoFinal += valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("-")) {
                                    resultadoFinal -= valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("*")) {
                                    resultadoFinal *= valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("/")) {
                                    resultadoFinal /= valorAux;
                                }
                            } else {
                                resultadoFinal = valorAux;
                            }
                        } else if (obj_parte instanceof Expression) {
                            valorAux = calculaExpressao(((Expression) obj_parte), registros_tabela);
                            if (achouOperador) {
                                if (operadorEncontrado.getSymbol().equalsIgnoreCase("+")) {
                                    resultadoFinal += valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("-")) {
                                    resultadoFinal -= valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("*")) {
                                    resultadoFinal *= valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("/")) {
                                    resultadoFinal /= valorAux;
                                }
                            } else {
                                resultadoFinal = valorAux;
                            }
                        } else if (obj_parte instanceof ArithmeticOperator) {
                            achouOperador = true;
                            operadorEncontrado = ((ArithmeticOperator) obj_parte);
                        } else if (obj_parte instanceof String) {
                            valorAux = (Double.parseDouble(obj_parte.toString()));
                            if (achouOperador) {
                                if (operadorEncontrado.getSymbol().equalsIgnoreCase("+")) {
                                    resultadoFinal += valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("-")) {
                                    resultadoFinal -= valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("*")) {
                                    resultadoFinal *= valorAux;
                                } else if (operadorEncontrado.getSymbol().equalsIgnoreCase("/")) {
                                    resultadoFinal /= valorAux;
                                }
                            } else {
                                resultadoFinal = valorAux;
                            }
                        }
                    }
                }
            }
        }
        return resultadoFinal;
    }

    public static CachedResults aplicaExpressaoNoRegistroTotalizado(Indicator indicador, Field campo, CachedResults registroTotalizado) throws BIException {
        Expression expressao = indicador.getExpressaoField(campo.getName());
        double valorDaExpressao;
        if (campo.isExpression() && (campo.getName().toUpperCase().trim().indexOf("SE(") == 0 || campo.getName().toUpperCase().trim().indexOf("IF(") == 0)) {
            ConditionalExpression expCondicional = indicador.getExpressaoCondicional(campo.getName());
            valorDaExpressao = indicador.calculaExpressaoCondicional(expCondicional, registroTotalizado);
        } else {
            valorDaExpressao = expressao.calculaExpressao(registroTotalizado);
        }
        registroTotalizado.setDouble(valorDaExpressao, campo.getCode());
        return registroTotalizado;
    }

    public int getFieldsExpressionAmount() {
        int retorno = 0;
        for (Object expressionPart : this.expressionParts) {
            if (expressionPart instanceof Field) {
                retorno++;
            }
        }
        return retorno;
    }

    public boolean temTodosFieldsExpressao(Field[] campos, Field campo) {
        boolean retorno = false;
        int qtdRetorno = 0;
        int quantidadeFieldsExpressao = this.getFieldsExpressionAmount();
        for (int i = 0; i < this.getExpressionParts().size(); i++) {
            Object objectParteExpressao = this.getParteExpressao(i);
            if (objectParteExpressao instanceof Field campoExp) {
                for (Field field : campos) {
                    if (field != null && campoExp.getCode() == field.getCode()) {
                        qtdRetorno++;
                    }
                }
            }
        }
        if (qtdRetorno >= quantidadeFieldsExpressao) {
            retorno = true;
        }
        return retorno;
    }
}
