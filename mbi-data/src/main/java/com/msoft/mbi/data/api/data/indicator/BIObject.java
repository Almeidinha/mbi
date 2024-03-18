package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BIGeneralException;

import javax.sql.rowset.CachedRowSet;
import java.io.*;

public class BIObject {

    public static String       caminho_congelados = ".../path";

    public BIObject() throws BIException {
    }

    public static void gravaIndicator(CachedRowSet registrosCongelados, Indicator indicador) throws BIException {
        String nome = "bi_ind_" + indicador.getCode() + ".dat";
        String caminho = caminho_congelados + File.separator + nome;
        BIObject.gravaObjecto(registrosCongelados, caminho);
    }

    public static CachedRowSet getIndicator(int cod_ind) throws BIException {
        return BIObject.getIndicator(String.valueOf(cod_ind));
    }

    public static void deletaIndicatorCongelado(Indicator indicador) throws BIException {
        if (indicador != null) {
            BIObject.deletaIndicatorCongelado(indicador.getOriginalCode());
        }
    }

    private static void deletaIndicatorCongelado(int indicador) {
        String nome = "bi_ind_" + indicador + ".dat";
        String caminho = caminho_congelados + File.separator + nome;
        BIObject.removeObjeto(caminho);
    }

    public static CachedRowSet getIndicator(String cod_ind) throws BIException {
        String nome = "bi_ind_" + cod_ind + ".dat";
        String caminho = caminho_congelados + File.separator + nome;
        return (CachedRowSet) BIObject.recuperaObjeto(caminho);
    }

    private static void removeGrafico(int indicador, int grafico) {
        String nome = "bi_grafico_" + grafico + "_" + indicador + ".dat";
        String caminho = caminho_congelados + File.separator + nome;
        BIObject.removeObjeto(caminho);
    }

    public static boolean ehCongelado(int cod_ind) throws BIException {

        try {
            // TODO consult Indicator in DB to check if its frozen
        } catch (Exception e) {
            throw new BIGeneralException(e, "Não foi possível verificar se o indicador " + cod_ind + " é congelado.");
        }
        return false;
    }

    public static boolean ehCongelado(String cod_ind) throws BIException {
        return BIObject.ehCongelado(Integer.parseInt(cod_ind));
    }

    public static Object recuperaObjeto(String caminho) throws BIGeneralException {
        ObjectInputStream in;
        Object object;
        try {
            File ob = new File(caminho);
            if(!ob.exists()) return null;
            in = new ObjectInputStream(new FileInputStream(caminho));
            object = in.readObject();
            in.close();
        } catch (Exception ex) {
            BIGeneralException biex = new BIGeneralException(ex);
            biex.setAction("carregar arquivo " + caminho);
            biex.setLocal("com.logocenter.logixbi.documents.BIObjeto", "recuperaObjeto(String)");
            throw biex;
        }
        return object;
    }

    public static void gravaObjecto(Object objeto, String caminho) throws BIGeneralException {
        ObjectOutputStream out;
        try {
            File dir = new File(caminho_congelados);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(caminho);
            if (!file.exists()) {
                out = new ObjectOutputStream(new FileOutputStream(file));
                out.writeObject(objeto);
                out.close();
            }
        } catch (Exception ex) {
            BIGeneralException biex = new BIGeneralException(ex);
            biex.setAction("gravar o arquivo " + caminho);
            biex.setLocal("com.logocenter.logixbi.documents.BIObjeto", "gravaObjecto(Indicator)");
            throw biex;
        }
    }

    public static void removeObjeto(String caminho) {
        File arquivo = new File(caminho);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }
}
