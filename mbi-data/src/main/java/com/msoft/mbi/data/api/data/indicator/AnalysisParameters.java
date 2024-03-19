package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class AnalysisParameters {

    @Setter
    @Getter
    private List<AnalysisParameter> parameters;
    private int lastParameterIndex;
    @Setter
    @Getter
    private Integer indicator;

    public static final int NAO_VISUALIZACAO_TABELA = 2;
    public static final int NAO_VISUALIZACAO_GRAFICOS = 4;
    public static final int NAO_VISUALIZACAO_COMENTARIOS = 8;
    public static final int NAO_VISUALIZACAO_ANEXOS = 16;
    public static final int NAO_VISUALIZACAO_BORDAS = 32;
    public static final int NAO_VISUALIZACAO_TITULO_ANALISE = 64;

    @Getter
    private Hashtable<Integer, ArrayList<Integer>> userConfigurations;

    public static final String NAO_VIZUALIZACAO_ABAS_ANALISE = "naoVizualizacaoAbasAnalise";
    public static final String VIZUALIZACAO_PADRAO_ANALISE = "vizualizacaoPadraoAnalise";

    public AnalysisParameters(Integer indicador) throws BIException {
        this.indicator = indicador;
        this.lastParameterIndex = Integer
                .parseInt(BIUtil.getNewData("parametro", "bi_parametro_analise", "where ind =" + this.indicator));
        this.parameters = new ArrayList<>();
    }

    public AnalysisParameters() {
        this.parameters = new ArrayList<>();
    }

    public void addParametro(AnalysisParameter parametroAnalise) {
        this.parameters.add(parametroAnalise);
    }

    public void criaParametro(String descricao, String valor, Integer userId) {
        AnalysisParameter parametroAnalise = new AnalysisParameter();
        parametroAnalise.setIndicatorCode(this.indicator);
        parametroAnalise.setParameterId(this.lastParameterIndex);
        parametroAnalise.setDescription(descricao);
        parametroAnalise.setParameterValue(valor);
        parametroAnalise.setUserId(userId);
        this.addParametro(parametroAnalise);
        this.lastParameterIndex++;
    }

    public void criaParametro(String descricao, int valor, Integer userId) {
        this.criaParametro(descricao, valor + "", userId);
    }

    public void inserirParametros(ConnectionBean conexaoBean) throws BIException {
        Iterator<AnalysisParameter> iParametros = this.parameters.iterator();
        AnalysisParameter parametroAnalise;
        while (iParametros.hasNext()) {
            parametroAnalise = iParametros.next();
            parametroAnalise.include(conexaoBean);
        }
    }

    public void removeParametroFromUsuarioAnalise(int userId) throws BIException {
        List<AnalysisParameter> parametrosRemover = new ArrayList<>();
        for (AnalysisParameter parametroAnalise : this.parameters) {
            if (parametroAnalise.getUserId() == userId) {
                parametrosRemover.add(parametroAnalise);
            }
        }
        this.parameters.removeAll(parametrosRemover);
    }

    public void populaVizualizacaoAbasFromAnalise() {
        this.userConfigurations = new Hashtable<>();
        ArrayList<Integer> usuariosConfiguracao;
        for (AnalysisParameter analysisParameter : this.parameters) {
            if (AnalysisParameters.NAO_VIZUALIZACAO_ABAS_ANALISE.equals(analysisParameter.getDescription())) {
                int valorParametro = Integer.parseInt(analysisParameter.getParameterValue());
                usuariosConfiguracao = this.userConfigurations.get(valorParametro);
                if (usuariosConfiguracao != null) {
                    usuariosConfiguracao.add(analysisParameter.getUserId());
                } else {
                    usuariosConfiguracao = new ArrayList<Integer>();
                    usuariosConfiguracao.add(analysisParameter.getUserId());
                    this.userConfigurations.put(valorParametro, usuariosConfiguracao);
                }
            }
        }
    }

    public String getParametroAnaliseUsuario(int usuario, String desParametro) {
        for (AnalysisParameter parametroAnalise : this.parameters) {
            if (usuario == parametroAnalise.getUserId()
                    && desParametro.equals(parametroAnalise.getDescription().trim())) {
                return parametroAnalise.getParameterValue();
            }
        }
        return "";
    }

    public String getParametroAnaliseUsuario(String desParametro) {
        for (AnalysisParameter parametroAnalise : this.parameters) {
            if (desParametro.equals(parametroAnalise.getDescription().trim())) {
                return parametroAnalise.getParameterValue();
            }
        }
        return "";
    }

    public void removeParametroValor(String desParametro, String valParametro) {
        List<AnalysisParameter> parametrosRemover = new ArrayList<>();
        for (AnalysisParameter parametroAnalise : this.parameters) {
            if (desParametro.equals(parametroAnalise.getDescription().trim())
                    && valParametro.equals(parametroAnalise.getParameterValue().trim())) {
                parametrosRemover.add(parametroAnalise);
            }
        }
        this.parameters.removeAll(parametrosRemover);
    }

    public static void excluiParametrosFromAnalise(Integer indicador, ConnectionBean conexao) throws BIException {
        String sql = "DELETE FROM bi_parametro_analise WHERE ind = ? ";
        PreparedStatement statement = null;
        try {
            statement = conexao.prepareStatement(sql);
            statement.setInt(1, indicador);
            statement.execute();
        } catch (SQLException e) {
            BISQLException biex = new BISQLException(e, "Erro ocorrido no indicador " + indicador + "\n" + sql);
            biex.setAction("Excluir Parâmetros da análise");
            biex.setLocal("ParametrosAnalise",
                    "excluiParametrosFromAnalise(Integer, ConexaoBean)");
            throw biex;
        } finally {
            BIUtil.closeStatement(statement);
        }
    }

    @SuppressWarnings("unused")
    private String getSQLDeleteParametrosFromUsuarioAnalise() {
        return "DELETE FROM bi_parametro_analise WHERE ind = ? AND usuario = ? ";
    }

}
