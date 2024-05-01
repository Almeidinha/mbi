package com.msoft.mbi.data.api.data.indicator;

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

    public static List<Field> createFields(ResultSet resultSet, Indicator indicator, NamedParameterJdbcTemplate jdbcTemplate) throws BIException {
        List<Field> fields = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Field field = new Field(BIUtil.verifyNullInt(resultSet, "campo_analise"), indicator, jdbcTemplate);
                field.setName(BIUtil.verifyNullString(resultSet, "nom_campo"));
                field.setTitle(BIUtil.verifyNullString(resultSet, "titulo_campo"));
                field.setFieldType(BIUtil.verifyNullString(resultSet, "tip_campo"));
                field.setDataType(BIUtil.verifyNullString(resultSet, "tip_dado"));
                field.setNickname(BIUtil.verifyNullString(resultSet, "apelido_campo"));
                field.setExpression(BIUtil.verifyNullString(resultSet, "eh_expressao").equals("S"));
                field.setDrillDownSequence(BIUtil.verifyNullInt(resultSet, "sequencia_filtro"));
                field.setVisualizationSequence(BIUtil.verifyNullInt(resultSet, "sequencia_visualiz"));

                field.setDisplayLocation(resultSet.getInt("local_apres"));
                field.setDefaultField(BIUtil.verifyNullString(resultSet, "padrao"));
                field.setOrder(BIUtil.verifyNullInt(resultSet, "ordem"));
                field.setTableNickname(BIUtil.verifyNullString(resultSet, "apelido_tabela"));
                field.setOrderDirection(BIUtil.verifyNullString(resultSet, "sentido_ordem"));
                field.setNumDecimalPositions(BIUtil.verifyNullInt(resultSet, "num_pos_decimais"));
                field.setTotalizingField(BIUtil.verifyNullString(resultSet, "totaliz_campo"));
                field.setVerticalAnalysisType(BIUtil.verifyNullString(resultSet, "analise_vertical"));
                field.setAggregationType(BIUtil.verifyNullString(resultSet, "tip_agregacao"));
                field.setAccumulatedParticipation(resultSet.getBoolean("particip_acumulada"));
                field.setAccumulatedValue(resultSet.getBoolean("val_acumulado"));
                field.setColumnWidth(resultSet.getString("largura_coluna"));
                field.setColumnAlignment(BIUtil.verifyNullString(resultSet, "pos_alinham_coluna"));
                field.setHorizontalAnalysisType(BIUtil.verifyNullString(resultSet, "analise_horizont"));
                field.setSumLine("S".equals(resultSet.getString("totaliz_camp_linha")));
                field.setAccumulatedLine(resultSet.getString("acum_campo_linha"));
                field.setDateMask(BIUtil.verifyNullString(resultSet, "mascara_dat"));
                field.setPartialTotalization(resultSet.getInt("totaliz_parcial") == 1);
                field.setHorizontalParticipation(resultSet.getString("particip_horizont") != null && (resultSet.getString("particip_horizont").equals("S")));
                field.setHorizontalParticipationAccumulated(resultSet.getString("part_acum_horizont") != null && (resultSet.getString("part_acum_horizont").equals("S")));
                field.setAccumulatedOrder(resultSet.getInt("ordem_acumulado"));
                field.setAccumulatedOrderDirection(resultSet.getString("sent_ord_acumulado"));
                field.setMediaLine(resultSet.getString("utiliza_med_linha") != null && resultSet.getString("utiliza_med_linha").equals("S"));
                field.setFixedValue("S".equals(resultSet.getString("val_fixo")));
                field.setDrillDown("S".equals(resultSet.getString("eh_drilldown")));
                field.setGeneralFilter(resultSet.getInt("filtro_geral"));
                String filtroObrigatorio = resultSet.getString("filtro_obrigatorio");
                field.setRequiredField((filtroObrigatorio != null) && (filtroObrigatorio.equals("S")));
                FieldFactory.addField(fields, field);
            }
        } catch (SQLException sqle) {
            BIDatabaseException biex = new BIDatabaseException(sqle);
            biex.setAction("buscar campos do indicador.");
            biex.setLocal("FieldFactory", "setFields()");
            throw biex;
        }
        return fields;
    }

    public static Field createFieldDixedVisualization(int indCode) throws BIException {
        Field campo = new Field();
        campo.setFieldId(1000);
        campo.setName(" 1 ");
        campo.setTitle("Não visualizado");
        campo.setNickname("Fixo");
        campo.setTableNickname(" ");
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
