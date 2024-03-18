package com.msoft.mbi.data.api.data;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Action {

    private int code;
    private int weight;
    private String description;

    public Action(int code, int weight, String description) {
        this.code = code;
        this.weight = weight;
        this.description = description;
    }

    // as in BIInterfaceActionEntity.actionWeight
    public static final int ANALYSIS_EDIT = 1;
    public static final int ANALYSIS_VIEW = 2;
    public static final int ANALYSIS_VIEW_ALL = 4;
    public static final int ANALYSIS_DELETE = 8;
    public static final int ANALYSIS_ADD = 16;
    public static final int ATTACHMENT_ADD = 32;
    public static final int FIELD_EDIT = 1;
    public static final int FIELD_VIEW = 2;
    public static final int FIELD_DELETE = 4;
    public static final int FIELD_ADD = 8;
    public static final int USER_EDIT = 1;
    public static final int USER_VIEW = 2;
    public static final int USER_DELETE = 4;
    public static final int USER_ADD = 8;
    public static final int CLIENT_EDIR = 1;
    public static final int CLIENT_VIEW = 2;
    public static final int CLIENTE_EXCLUIR = 4;
    public static final int CLIENT_DELETE = 8;
    public static final int EMAIL_CONFIG_EDIT = 1;
    public static final int EMAIL_CONFIG_VIEW = 2;
    public static final int EMAIL_CONFIG_DELETE = 4;
    public static final int EMAIL_CONFIG_ADD = 8;
    public static final int PANEL_EDIT = 1;
    public static final int PANEL_VIEW = 2;
    public static final int PANEL_VIEW_ALL = 4;
    public static final int PANEL_DELETE = 8;
    public static final int PANEL_DELETE_ANALYSIS = 16;
    public static final int DOWNLOAD_AND_UPDATE_APP = 1;
    public static final int CHANGE_ANALYSIS_CONNECTION = 1;
    public static final int PANEL_FIELD_EDIT = 1;
    public static final int PANEL_FIELD_EDIT_PARTIAL = 2;
    public static final int PANEL_FIELD_VIEW = 4;
    public static final int GPE_ENABLE_BUTTON = 1;
    public static final int PANEL_RELOAD_ANALYSIS_BUTTON_ENABLE = 1;
    public static final int PANEL_IMPORT_ANALYSIS_FROM_REPO = 1;
    public static final int PANEL_IMPORT_ANALYSIS_OTHER_PANEL = 2;
    public static final int PANEL_IMPORT_ANALYSIS_FROM_FILES = 4;
    public static final int PANEL_IMPORT_ANALYSIS_MY_PANELS = 8;
    public static final int PANEL_FILES_SAVE_ANALYSIS = 1;
    public static final int PANEL_FILES_SAVE_ANALYSIS_AS = 2;
    public static final int PANEL_FILES_SCHEDULE_ANALYSIS = 4;
    public static final int PANEL_FILES_MAIL_ANALYSIS = 8;
    public static final int PANEL_FILES_ANALYSIS_RESTRICTIONS = 16;
    public static final int PANEL_FILES_PRINT_ANALYSIS = 32;
    public static final int PANEL_FILES_DELETE_ANALYSIS = 64;
    public static final int PANEL_FILES_SAVE_PANEL = 128;
    public static final int PANEL_FILES_SAVE_PANEL_AS = 256;
    public static final int PANEL_FILES_DELETE_PANEL = 512;
    public static final int PANEL_FILES_COPY_URL = 1024;
    public static final int PANEL_FIELDS_ENABLE_BUTTON = 1;
    public static final int PANEL_COLORS_ENABLE_BUTTON = 1;
    public static final int PANEL_GRAPH_ENABLE_BUTTON = 1;
    public static final int PANEL_CREATE_SHEET_ENABLE_BUTTON = 1;
    public static final int PANEL_ADD_SEQUENCE_FIELD_ENABLE_BUTTON = 1;
    public static final int PANEL_AGGREGATION_ENABLE_BUTTON = 1;
    public static final int PANEL_ORDER_ENABLE_BUTTON = 1;
    public static final int PANEL_VIEW_SEQUENCE__ENABLE_BUTTON = 1;
    public static final int PANEL_FILTER_SEQUENCE_ENABLE_BUTTON = 1;
    public static final int PANEL_VERTICAL_ANALYSIS_ENABLE_BUTTON = 1;
    public static final int PANEL_HORIZONTAL_ANALYSIS_ENABLE_BUTTON = 1;
    public static final int PANEL_TOTALIZATION_ENABLE_BUTTON = 1;
    public static final int PANEL_DECIMAL_VALUES_ENABLE_BUTTON = 1;
    public static final int PANEL_CALC_COLUMN_ENABLE_BUTTON = 1;
    public static final int SLIDESHOW = 1;
    public static final int ADD_FILE = 1;


}
