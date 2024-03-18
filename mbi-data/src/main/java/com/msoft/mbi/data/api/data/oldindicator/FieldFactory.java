package com.msoft.mbi.data.api.data.oldindicator;

import com.msoft.mbi.data.api.data.exception.BIDatabaseException;
import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.util.BIUtil;
import com.msoft.mbi.data.api.data.util.Constants;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FieldFactory {
    private static final List<String> cores = Arrays.asList(
            "#0000FF", "#FF0000", "#00FF00", "#FFFF00", "#FF00FF", "#00FFFF", "#FFA500", "#363636",
            "#4876FF", "#CD0000", "#008B00", "#CDCD00", "#CD00CD", "#00CDCD", "#CD8500", "#696969",
            "#1E90FF", "#FF3030", "#00FF7F", "#FFD700", "#EE7AE9", "#008B8B", "#FF7F00", "#9C9C9C",
            "#1874CD", "#CD5555", "#698B22", "#FFC125", "#BF3EFF", "#008B8B", "#EE7621", "#CFCFCF"
    );

    public FieldFactory() {
    }

    public static List<Field> criaFields(ResultSet rsltField, Indicator indicador, NamedParameterJdbcTemplate jdbcTemplate) throws BIException {
        List<Field> fields = new ArrayList<>();
        try {
            while (rsltField.next()) {
                Field campo = new Field(BIUtil.verificanullInt(rsltField, "campo_analise"), indicador, jdbcTemplate);
                campo.setName(BIUtil.verificanullString(rsltField, "nom_campo"));
                campo.setTitle(BIUtil.verificanullString(rsltField, "titulo_campo"));
                campo.setFieldType(BIUtil.verificanullString(rsltField, "tip_campo"));
                campo.setDataType(BIUtil.verificanullString(rsltField, "tip_dado"));
                campo.setNickName(BIUtil.verificanullString(rsltField, "apelido_campo"));
                campo.setExpression(BIUtil.verificanullString(rsltField, "eh_expressao").equals("S"));
                campo.setDrillDownSequence(BIUtil.verificanullInt(rsltField, "sequencia_filtro"));
                campo.setVisualizationSequence(BIUtil.verificanullInt(rsltField, "sequencia_visualiz"));

                campo.setDisplayLocation(rsltField.getInt("local_apres"));
                campo.setDefaultField(BIUtil.verificanullString(rsltField, "padrao"));
                campo.setOrder(BIUtil.verificanullInt(rsltField, "ordem"));
                campo.setTableNickName(BIUtil.verificanullString(rsltField, "apelido_tabela"));
                campo.setOrderDirection(BIUtil.verificanullString(rsltField, "sentido_ordem"));
                campo.setNumDecimalPositions(BIUtil.verificanullInt(rsltField, "num_pos_decimais"));
                campo.setTotalizingField(BIUtil.verificanullString(rsltField, "totaliz_campo"));
                campo.setVerticalAnalysisType(BIUtil.verificanullString(rsltField, "analise_vertical"));
                campo.setAggregationType(BIUtil.verificanullString(rsltField, "tip_agregacao"));
                campo.setAccumulatedParticipation(rsltField.getBoolean("particip_acumulada"));
                campo.setAccumulatedValue(rsltField.getBoolean("val_acumulado"));
                campo.setColumnWidth(rsltField.getString("largura_coluna"));
                campo.setColumnAlignmentPosition(BIUtil.verificanullString(rsltField, "pos_alinham_coluna"));
                campo.setHorizontalAnalysisType(BIUtil.verificanullString(rsltField, "analise_horizont"));
                campo.setSumLine("S".equals(rsltField.getString("totaliz_camp_linha")));
                campo.setAccumulatedLine(rsltField.getString("acum_campo_linha"));
                campo.setDateMask(BIUtil.verificanullString(rsltField, "mascara_dat"));
                campo.setPartialTotalization(rsltField.getInt("totaliz_parcial") == 1);
                campo.setHorizontalParticipation(rsltField.getString("particip_horizont") != null && (rsltField.getString("particip_horizont").equals("S")));
                campo.setHorizontalParticipationAccumulated(rsltField.getString("part_acum_horizont") != null && (rsltField.getString("part_acum_horizont").equals("S")));
                campo.setAccumulatedOrder(rsltField.getInt("ordem_acumulado"));
                campo.setAccumulatedOrderDirection(rsltField.getString("sent_ord_acumulado"));
                campo.setMediaLine(rsltField.getString("utiliza_med_linha") != null && rsltField.getString("utiliza_med_linha").equals("S"));
                campo.setFixedValue("S".equals(rsltField.getString("val_fixo")));
                campo.setDrillDown("S".equals(rsltField.getString("eh_drilldown")));
                campo.setGeneralFilter(rsltField.getInt("filtro_geral"));
                String filtroObrigatorio = rsltField.getString("filtro_obrigatorio");
                campo.setRequiredField((filtroObrigatorio != null) && (filtroObrigatorio.equals("S")));
                FieldFactory.addField(fields, campo);
            }
        } catch (SQLException sqle) {
            BIDatabaseException biex = new BIDatabaseException(sqle);
            biex.setAction("buscar campos do indicador.");
            biex.setLocal("com.logocenter.logixbi.analysis.FieldFactory", "setFields()");
            throw biex;
        }
        return fields;
    }

    public static Field criaFieldFixoNaoVisualizado(int codigoIndicador) throws BIException {
        Field campo = new Field();
        campo.setCode(1000);
        campo.setName(" 1 ");
        campo.setTitle("Não visualizado");
        campo.setNickName("Fixo");
        campo.setTableNickName(" ");
        campo.setFieldType(Constants.DIMENSION);
        campo.setDataType(Constants.NUMBER);
        campo.setChildField(true);
        campo.setDisplayLocation(Constants.LINE);
        campo.setVisualizationSequence(0);
        campo.setAccumulatedLine("N");
        campo.setFixedValue(true);
        campo.setDefaultField("T");
        return campo;
    }

    public static List<Field> addField(List<Field> fieldList, Field campo) {
        fieldList.add(campo);
        return fieldList;
    }

    public static String getRandomColor() {
        Random rand = new Random();
        return cores.get(rand.nextInt(cores.size()));
    }
}
