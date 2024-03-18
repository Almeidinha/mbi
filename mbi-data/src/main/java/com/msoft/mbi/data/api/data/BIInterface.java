package com.msoft.mbi.data.api.data;

import com.msoft.mbi.data.api.dtos.BIInterfaceActionDTO;
import com.msoft.mbi.data.services.BIInterfaceActionService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class BIInterface {

    private int code;
    private String name;
    private List<Action> actionList;
    private int maxWeight;

    private final BIInterfaceActionService biInterfaceActionService;

    // like in BIInterfaceEntity.id
    public static final int ADMINISTRATION = 1;
    public static final int ANALYSIS = 2;
    public static final int ANALYSIS_FIELD = 3;
    public static final int USER = 4;
    public static final int EMAIL_CONFIGURATION = 5;
    public static final int BI_UPDATE = 6;
    public static final int CLIENT = 7;
    public static final int PANEL = 8;
    public static final int ANALYSIS_CONNECTIONS = 9;
    public static final int PANEL_ANALYSIS_FIELD = 10;
    public static final int GPE = 11;
    public static final int PANEL_RELOAD_ANALYSIS = 12;
    public static final int PANEL_IMPORT_ANALYSIS = 13;
    public static final int PANEL_FILE = 14;
    public static final int PANEL_FIELDS = 15;
    public static final int PANEL_COLORS = 16;
    public static final int PANEL_GRAPH = 17;
    public static final int PANEL_CREATE_SHEET = 18;
    public static final int PANEL_ADD_SEQUENCE_FIELD = 19;
    public static final int PANEL_ADD_AGGREGATION_FIELD = 20;
    public static final int PANEL_ADD_ORDER_FIELD = 21;
    public static final int PANEL_ADD_VIEW_SEQUENCE = 22;
    public static final int PANEL_ADD_SEQUENCE_FILTER = 23;
    public static final int PANEL_VERTICAL_ANALYSIS = 24;
    public static final int PANEL_HORIZONTAL_ANALYSIS = 25;
    public static final int PANEL_TOTAL_PARTIAL = 26;
    public static final int PANEL_DECIMAL_PLACES = 27;
    public static final int PANEL_CALC_COLUMN = 28;
    public static final int SCHEDULE = 32;
    public static final int SLIDESHOW = 33;
    public static final int FILES = 34;

    public static final int[] PANEL_INTERFACES = { PANEL_RELOAD_ANALYSIS, PANEL_IMPORT_ANALYSIS, PANEL_FILE,
            PANEL_FIELDS, PANEL_COLORS, PANEL_GRAPH, PANEL_CREATE_SHEET, PANEL_ADD_SEQUENCE_FIELD,
            PANEL_ADD_AGGREGATION_FIELD, PANEL_ADD_ORDER_FIELD, PANEL_ADD_VIEW_SEQUENCE,
            PANEL_ADD_SEQUENCE_FILTER, PANEL_VERTICAL_ANALYSIS, PANEL_HORIZONTAL_ANALYSIS, PANEL_TOTAL_PARTIAL,
            PANEL_DECIMAL_PLACES, PANEL_CALC_COLUMN };

    public BIInterface(int code, BIInterfaceActionService biInterfaceActionService) {
        this.code = code;
        this.maxWeight = 0;
        this.biInterfaceActionService = biInterfaceActionService;
        loadActions();
    }

    public BIInterface(int code, String name, List<Action> actionList, int maxWeight, BIInterfaceActionService biInterfaceActionService) {
        this.biInterfaceActionService = biInterfaceActionService;
        this.code = code;
        this.name = name;
        this.actionList = actionList;
        this.maxWeight = maxWeight;
    }

    private void loadActions() {
        List<BIInterfaceActionDTO> biInterfaceActionDTO = this.biInterfaceActionService.loadInterfaceActions(this.code);

        this.actionList = biInterfaceActionDTO.stream()
                .map(action -> new Action(action.getId(), action.getActionWeight(), action.getDescription()))
                .collect(Collectors.toList());

        /*
        Integer weigth = biInterfaceActionDTO.stream()
                        .map(BIInterfaceActionDTO::getActionWeight).toList().stream().mapToInt(i->i).sum();
         */

        this.setMaxWeight(biInterfaceActionDTO.stream()
                .map(BIInterfaceActionDTO::getActionWeight).mapToInt(i -> i).sum());
    }
}
