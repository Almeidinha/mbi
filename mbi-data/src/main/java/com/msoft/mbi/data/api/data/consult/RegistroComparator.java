package com.msoft.mbi.data.api.data.consult;

import com.msoft.mbi.data.api.data.exception.BIException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class RegistroComparator implements Comparator<Object> {

    private final ArrayList<String> fieldCodeList;
    private final ArrayList<String> orderDirectionList;
    private int currentFieldIndex = -1;

    public RegistroComparator(ArrayList<String> cod_campos, ArrayList<String> sentido_ordem) {
        this.fieldCodeList = cod_campos;
        this.orderDirectionList = sentido_ordem;
    }

    public int compare(Object o1, Object o2) {
        ConsultRegister c1 = (ConsultRegister) o1;
        ConsultRegister c2 = (ConsultRegister) o2;
        if (c1 != null && c2 != null) {
            int res = 0;
            Object val_1;
            Object val_2;

            try {
                while (res == 0 && nextLevel()) {

                    val_1 = c1.getValor(Integer.parseInt(String.valueOf(fieldCodeList.get(currentFieldIndex))));
                    val_2 = c2.getValor(Integer.parseInt(String.valueOf(fieldCodeList.get(currentFieldIndex))));

                    if (val_1 instanceof Date && val_2 instanceof Date) {
                        res = ((Date) val_2).compareTo(((Date) val_1));
                    } else if (val_1 instanceof Integer && val_2 instanceof Integer) {
                        res = ((Integer) val_2).compareTo(((Integer) val_1));
                    } else if (val_1 instanceof Double && val_2 instanceof Double) {
                        res = ((Double) val_2).compareTo(((Double) val_1));
                    } else if (val_1 instanceof String valor1 && val_2 instanceof String valor2) {
                        if (MonthYearComparator.contains(valor1) && MonthYearComparator.contains(valor2)) {
                            res = MonthYearComparator.compare(valor2, valor1);
                        } else {
                            res = ((String) val_2).compareTo(((String) val_1));
                        }
                    } else {
                        res = String
                                .valueOf(c2.getValor(
                                        Integer.parseInt(String.valueOf(fieldCodeList.get(currentFieldIndex)))))
                                .compareTo(String.valueOf(c1.getValor(Integer
                                        .parseInt(String.valueOf(fieldCodeList.get(currentFieldIndex))))));
                    }
                    res = res * this.getOrder(String.valueOf(this.orderDirectionList.get(currentFieldIndex)));
                }
                currentFieldIndex = -1;
            } catch (BIException ignored) {
            }

            return res;
        } else {
            return 0;
        }
    }

    private int getOrder(String sentidoOrdem) {
        if (sentidoOrdem != null && sentidoOrdem.equalsIgnoreCase("DESC")) {
            return 1;
        } else {
            return -1;
        }
    }

    private boolean nextLevel() {
        if (fieldCodeList != null && !fieldCodeList.isEmpty()) {
            if ((currentFieldIndex + 1) < fieldCodeList.size()) {
                currentFieldIndex++;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}
