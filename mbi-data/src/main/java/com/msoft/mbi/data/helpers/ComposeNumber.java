package com.msoft.mbi.data.helpers;

public class ComposeNumber {

    public static boolean composeCount(int composto, int parcela) {
        if (parcela == 1)
            return (composto % 2f) != 0;
        if (((Math.log(parcela) / Math.log(2)) % 1) != 0)
            return false;
        int base = (int) (Math.log(composto) / Math.log(2));
        base++;
        int parte = (int) Math.pow(2, base);
        while (parte >= 1) {
            if (composto >= parte) {
                if (parte == parcela)
                    return true;
                composto = composto - parte;
            }
            parte = parte / 2;
        }
        return false;
    }

}
