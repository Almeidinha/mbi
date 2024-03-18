package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import lombok.Getter;
import lombok.Setter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Setter
@Getter
public class AnalysisParameter {

    private Integer indicatorCode;
    private Integer userId;
    private Integer parameterId;
    private String description;
    private String parameterValue;

    public void include(ConnectionBean connection) throws BISQLException {
        String sql = this.getSQLIncluirParametroAnalise();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, this.indicatorCode);
            statement.setInt(2, this.userId);
            statement.setInt(3, this.parameterId);
            statement.setString(4, this.description);
            statement.setString(5, this.parameterValue);
            int result = statement.executeUpdate();
            System.out.println("retorno:" + result);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException sqlex) {
                System.out.println("Erro no rollback!!!");
            }
            BISQLException biex = new BISQLException(e, "Erro ocorrido no indicador " + this.indicatorCode + "\n" + sql);
            biex.setAction("Incluir parâmetros da análise");
            biex.setLocal(this.getClass(), "incluir(ConexaoBean)");
            throw biex;
        }
    }

    private String getSQLIncluirParametroAnalise() {
        return "INSERT INTO bi_parametro_analise(ind, usuario, parametro, descricao_parametro, val_parametro) VALUES (?, ?, ?, ?, ?)";
    }
}
