package com.msoft.mbi.data.api.data.util;

import com.msoft.mbi.data.api.data.indicator.Comment;
import com.msoft.mbi.data.api.data.indicator.Field;

import java.util.*;

public class BIComparator {

    static final Comparator<Field> CODE_ORDERING = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return Integer.compare(o1.getFieldId(), o2.getFieldId());
        } else {
            return 0;
        }
    };

    static final Comparator<Field> NAME_ORDERING = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return o1.getName().compareTo(o2.getName());
        } else {
            return 0;
        }
    };

    static final Comparator<Field> TITLE_ORDERING = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return o1.getTitle().compareTo(o2.getTitle());
        } else {
            return 0;
        }
    };

    static final Comparator<Field> VISUALIZATION_SEQUENCE_ORDERING = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return Integer.compare(o1.getVisualizationSequence(), o2.getVisualizationSequence());
        } else {
            return 0;
        }
    };

    static final Comparator<Comment> COMMENT_ORDERING = (o1, o2) -> {
        if (o1 != null && o2 != null) {
            return o2.getDateHour().compareTo(o1.getDateHour());
        } else {
            return 0;
        }
    };

    public static void classification(List<Field> fields, String attribute) {
        if (attribute.equalsIgnoreCase(Constants.CODE)) {
            fields.sort(CODE_ORDERING);
        } else if (attribute.equalsIgnoreCase(Constants.NAME)) {
            fields.sort(NAME_ORDERING);
        } else if (attribute.equalsIgnoreCase(Constants.TITLE)) {
            fields.sort(TITLE_ORDERING);
        } else if (attribute.equalsIgnoreCase(Constants.VISUALIZATION_SEQUENCE)) {
            fields.sort(VISUALIZATION_SEQUENCE_ORDERING);
        }
    }

    public static void orderByComment(Comment[] comments, int attribute) {
        if (attribute == Comment.DATAHORA) {
            Arrays.sort(comments, COMMENT_ORDERING);
        }
    }
}
