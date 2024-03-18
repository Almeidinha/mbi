package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.indicator.Comment;
import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.*;

public class BIComparator {

    static final Comparator<Field> ORDENACAO_CODIGO = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return Integer.compare(o1.getFieldId(), o2.getFieldId());
        } else {
            return 0;
        }
    };

    static final Comparator<Field> ORDENACAO_NOME = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return o1.getName().compareTo(o2.getName());
        } else {
            return 0;
        }
    };

    static final Comparator<Field> ORDENACAO_TITULO = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return o1.getTitle().compareTo(o2.getTitle());
        } else {
            return 0;
        }
    };

    static final Comparator<Field> ORDENACAO_SEQUENCIA_VISUALIZACAO = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return Integer.compare(o1.getVisualizationSequence(), o2.getVisualizationSequence());
        } else {
            return 0;
        }
    };

    static final Comparator<Comment> ORDENACAO_COMENTARIO = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return o2.getDateHour().compareTo(o1.getDateHour());
        } else {
            return 0;
        }
    };

    public static void classificacao(List<Field> campos, String atributo) {
        if (atributo.equalsIgnoreCase(Constants.CODE)) {
            campos.sort(ORDENACAO_CODIGO);
        } else if (atributo.equalsIgnoreCase(Constants.NAME)) {
            campos.sort(ORDENACAO_NOME);
        } else if (atributo.equalsIgnoreCase(Constants.TITLE)) {
            campos.sort(ORDENACAO_TITULO);
        } else if (atributo.equalsIgnoreCase(Constants.VISUALIZATION_SEQUENCE)) {
            campos.sort(ORDENACAO_SEQUENCIA_VISUALIZACAO);
        }
    }

    public static void ordenaComentario(Comment[] comentario, int atributo) {
        if (atributo == Comment.DATAHORA) {
            Arrays.sort(comentario, ORDENACAO_COMENTARIO);
        }
    }
}
