package com.msoft.mbi.data.api.data.indicator;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.exception.BISQLException;
import com.msoft.mbi.data.api.data.util.BIDate;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.ConnectionBean;
import com.msoft.mbi.data.api.data.util.TextConverter;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Locale;

@Getter
@Setter
public class Comment {

    public static final int DATAHORA = 1;
    private int code;
    private int indicatorCode;
    private String commentText;
    private Timestamp dateHour;
    private Integer userId;
    private BIDate expirationDate;
    private String sendEmail;
    private boolean saved;

    public Comment() {
    }

    public int getUserCode() {
        return this.userId;
    }

    public String getDateHourToString() {
        Locale l = new Locale("pt", "BR");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, l);
        if (this.dateHour == null) {
            return "";
        }
        return df.format(this.dateHour);
    }

    public String getTextoHTML() {
        TextConverter converter = new TextConverter(this.commentText) {

            public String getTextToHTML() {

                StringBuilder newText = new StringBuilder();
                newText.append(this.text);
                for (int i = 0; i < this.HTML.length; i++) {
                    if (!this.UNICODE[i].equals(" "))
                        this.replace(newText, this.UNICODE[i], this.HTML[i]);
                }
                return newText.toString();
            }
        };
        return converter.getTextToHTML();
    }

    public void save(ConnectionBean connectionBean) throws BIException {
        if (!this.saved) {
            String sql_log = "INSERT INTO bi_coment (coment, ind, usuario, dat_hor_postagem, texto_coment, dat_expiracao, envia_email) VALUES ("
                    + this.code + ", " + this.indicatorCode + ", " + this.userId + ", '" + this.dateHour + "', '" + this.commentText
                    + "', '" + this.expirationDate + "' , '" + this.sendEmail + "')";
            String sql = "INSERT INTO bi_coment (coment, ind, usuario, dat_hor_postagem, texto_coment, dat_expiracao, envia_email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = null;
            try {
                stmt = connectionBean.prepareStatement(sql);
                stmt.setInt(1, this.code);
                stmt.setInt(2, this.indicatorCode);
                stmt.setInt(3, this.userId);
                stmt.setTimestamp(4, this.dateHour);
                stmt.setString(5, this.commentText);
                if (this.expirationDate == null) {
                    this.expirationDate = new BIDate();
                }
                stmt.setDate(6, new Date(this.expirationDate.getTimeInMillis()));
                stmt.setString(7, this.sendEmail);
                stmt.executeUpdate();
                this.saved = true;
            } catch (SQLException sqle) {
                BISQLException biex = new BISQLException(sqle, sql_log);
                biex.setAction("salvar comentario.");
                biex.setLocal(this.getClass(), "salvar(ConexaoBean)");
                throw biex;
            } finally {
                BIUtil.closeStatement(stmt);
            }
        }
    }

    public static void delete(ConnectionBean connectionBean, int code, int indicatorCode) throws BIException {
        String sql = "DELETE FROM bi_coment WHERE coment = " + code + " AND ind = " + indicatorCode;
        try {
            connectionBean.executeUpdate(sql);
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, sql);
            biex.setAction("excluir comentario.");
            biex.setLocal("BIComentario", "excluir(ConexaoBean, int)");
            throw biex;
        }
    }

    public static void excluirComentarios(ConnectionBean connectionBean, int code) throws BIException {
        String sql = "DELETE FROM bi_coment WHERE ind = " + code;
        try {
            connectionBean.executeUpdate(sql);
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, sql);
            biex.setAction("excluir comentario.");
            biex.setLocal("BIComentario", "excluir(ConexaoBean, int)");
            throw biex;
        }
    }

    public void setIndicatorCode(int indicatorCode) {
        this.indicatorCode = indicatorCode;
        this.saved = false;
    }

    public String getSummary() {
        StringBuilder result = new StringBuilder();
        Locale l = new Locale("pt", "BR");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, l);
        result.append(df.format(this.dateHour));
        result.append(" - Por ");
        result.append(this.userId);
        result.append(" - ").append(this.commentText);
        int limite = result.length();
        int index = result.indexOf("\n");
        limite = index != -1 ? index : Math.min(limite, 140);
        TextConverter converter = new TextConverter(result.substring(0, limite));
        if (limite >= 140)
            converter = new TextConverter(result.substring(0, limite) + "...");
        return converter.getTextToHTML();
    }

    public String getSummaryMaintenance() {
        StringBuilder result = new StringBuilder();
        Locale l = new Locale("pt", "BR");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, l);
        result.append(df.format(this.dateHour));
        result.append(" - Por ");
        result.append(this.userId);
        result.append(" - ").append(this.commentText);
        int limite = result.length();
        int index = result.indexOf("\n");
        limite = index != -1 ? index : Math.min(limite, 85);
        TextConverter converter = new TextConverter(result.substring(0, limite));
        if (limite >= 85)
            converter = new TextConverter(result.substring(0, limite) + "...");
        return converter.getTextToHTML();
    }

    public String getComment() {
        StringBuilder retorno = new StringBuilder();
        Locale l = new Locale("pt", "BR");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, l);
        retorno.append(df.format(this.dateHour));
        retorno.append(" - Por ");
        retorno.append(this.userId);
        retorno.append("\n").append(this.commentText).append("\n\n");
        TextConverter converter = new TextConverter(retorno.toString());
        return converter.getTextToHTML();
    }

    public String getLabel() {
        StringBuilder result = new StringBuilder();
        Locale l = new Locale("pt", "BR");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, l);
        result.append(df.format(this.dateHour));
        result.append(" - Por ");
        result.append(this.userId);
        return result.toString();
    }

    public BIDate getExpirationDate() {
        if (expirationDate == null) {
            return this.expirationDate = new BIDate();
        }
        return expirationDate;
    }

    public String getDataExpiracaoString() {
        if (this.expirationDate == null) {
            return "";
        }
        return this.expirationDate.toString();
    }

    public String getSendEmail() {
        if (sendEmail == null) {
            return "";
        }
        return sendEmail;
    }

    public static void updateEmail(ConnectionBean connectionBean, int code, String value) throws BIException {
        String sql = "UPDATE bi_coment SET envia_email = '" + value.trim() + "' WHERE coment = " + code;
        try {
            connectionBean.executeUpdate(sql);
        } catch (SQLException sqle) {
            BISQLException biex = new BISQLException(sqle, sql);
            biex.setAction("alterar envia email do  comentario.");
            biex.setLocal("BIComentario", "atualizarEmail(ConexaoBean, int, String)");
            throw biex;
        }
    }
}
