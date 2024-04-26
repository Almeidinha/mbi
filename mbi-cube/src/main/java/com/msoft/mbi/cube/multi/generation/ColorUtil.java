package com.msoft.mbi.cube.multi.generation;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.util.HSSFColor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public final class ColorUtil {

    public static final String BLUE = "3377CC";
    public static final String WHITE = "ffffff";
    public static final String DARK_BLUE = "000080";
    public static final String BLACK = "000000";

    public static final String GREY_25_PERCENT = "cccccc";
    public static final String PALE_BLUE = "A2C8E8";
    public static final String LIGHT_TURQUOISE = "D7E3F7";

    public static Map<String, Short> coresExcelHTML;

    private static void addColor(String hexCode, HSSFColor.HSSFColorPredefined color) {
        getCoresExcelHTML().put(hexCode, color.getIndex());
    }

    public static synchronized Map<String, Short> getCoresExcelHTML() {
        if (coresExcelHTML == null) {
            coresExcelHTML = new HashMap<>();
            initializeColors();
        }
        return coresExcelHTML;
    }

    private static void initializeColors() {
        addColor("ff0000", HSSFColor.HSSFColorPredefined.RED);
        addColor("ff0000", HSSFColor.HSSFColorPredefined.RED);
        addColor("ff0033", HSSFColor.HSSFColorPredefined.RED);
        addColor("ff3300", HSSFColor.HSSFColorPredefined.RED);
        addColor("ff3333", HSSFColor.HSSFColorPredefined.RED);
        addColor("ff0066", HSSFColor.HSSFColorPredefined.RED);
        addColor("ff3366", HSSFColor.HSSFColorPredefined.RED);

        addColor("cc0000", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("cc0033", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("cc3300", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("cc3333", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("cc0066", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("cc3366", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("990000", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("990033", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("993300", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("993333", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("990066", HSSFColor.HSSFColorPredefined.DARK_RED);
        addColor("993366", HSSFColor.HSSFColorPredefined.DARK_RED);

        addColor("ff00ff", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff33ff", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff66ff", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff66cc", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff33cc", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff00cc", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff0099", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff3399", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff6699", HSSFColor.HSSFColorPredefined.PINK);
        addColor("cc00cc", HSSFColor.HSSFColorPredefined.PINK);
        addColor("cc6699", HSSFColor.HSSFColorPredefined.PINK);
        addColor("ff99ff", HSSFColor.HSSFColorPredefined.PINK);
        addColor("cc3399", HSSFColor.HSSFColorPredefined.PLUM);

        addColor("333399", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("330099", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("3300cc", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("3333cc", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("3333ff", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("3366ff", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("3366cc", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("336699", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("000066", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("003399", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("000099", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("0000cc", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("0033cc", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("0000ff", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("0033ff", HSSFColor.HSSFColorPredefined.DARK_BLUE);
        addColor("000080", HSSFColor.HSSFColorPredefined.DARK_BLUE);

        addColor("0066ff", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("0066cc", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("006699", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("669999", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("6699cc", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("6699ff", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("9999cc", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("666699", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("6666ff", HSSFColor.HSSFColorPredefined.BLUE_GREY);
        addColor("333366", HSSFColor.HSSFColorPredefined.BLUE_GREY);

        addColor("00cc00", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("00cc33", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("00cc66", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("00ff66", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("00ff33", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("00ff00", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("339900", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("339933", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("009933", HSSFColor.HSSFColorPredefined.GREEN);
        addColor("009900", HSSFColor.HSSFColorPredefined.GREEN);

        addColor("99ff00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("99ff33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("99ff66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("33ff33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("33ff66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("33ff99", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("66ff00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("66ff33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("66ff66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("99ff99", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("00ff99", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("33ff00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("99cc00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("99cc33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("99cc66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("669900", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("669933", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);
        addColor("669966", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN);

        addColor("ffffff", HSSFColor.HSSFColorPredefined.WHITE);

        addColor("003300", HSSFColor.HSSFColorPredefined.BLACK);
        addColor("003333", HSSFColor.HSSFColorPredefined.BLACK);
        addColor("000000", HSSFColor.HSSFColorPredefined.BLACK);
        addColor("000033", HSSFColor.HSSFColorPredefined.BLACK);
        addColor("333300", HSSFColor.HSSFColorPredefined.BLACK);
        addColor("333333", HSSFColor.HSSFColorPredefined.BLACK);
        addColor("330033", HSSFColor.HSSFColorPredefined.BLACK);
        addColor("330000", HSSFColor.HSSFColorPredefined.BLACK);

        addColor("663300", HSSFColor.HSSFColorPredefined.BROWN);
        addColor("663333", HSSFColor.HSSFColorPredefined.BROWN);
        addColor("660033", HSSFColor.HSSFColorPredefined.BROWN);
        addColor("660000", HSSFColor.HSSFColorPredefined.BROWN);
        addColor("996600", HSSFColor.HSSFColorPredefined.BROWN);
        addColor("996633", HSSFColor.HSSFColorPredefined.BROWN);
        addColor("996666", HSSFColor.HSSFColorPredefined.BROWN);

        addColor("ff9900", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ff9933", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ff9966", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ffcc66", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ffcc99", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ffcc33", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ffcc00", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ff6600", HSSFColor.HSSFColorPredefined.GOLD);
        addColor("ff6633", HSSFColor.HSSFColorPredefined.GOLD);

        addColor("6633ff", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("6633cc", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("6600cc", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("663399", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("660099", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("660066", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("663366", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("990099", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("9900cc", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("9900ff", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("9933ff", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("9933cc", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("993399", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("996699", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("9966cc", HSSFColor.HSSFColorPredefined.VIOLET);
        addColor("9966ff", HSSFColor.HSSFColorPredefined.VIOLET);

        addColor("ffff00", HSSFColor.HSSFColorPredefined.YELLOW);
        addColor("ffff33", HSSFColor.HSSFColorPredefined.YELLOW);
        addColor("ffff66", HSSFColor.HSSFColorPredefined.YELLOW);
        addColor("ffff99", HSSFColor.HSSFColorPredefined.YELLOW);
        addColor("ffffcc", HSSFColor.HSSFColorPredefined.YELLOW);

        addColor("cc9900", HSSFColor.HSSFColorPredefined.DARK_YELLOW);
        addColor("cc9933", HSSFColor.HSSFColorPredefined.DARK_YELLOW);
        addColor("cc9966", HSSFColor.HSSFColorPredefined.DARK_YELLOW);
        addColor("999900", HSSFColor.HSSFColorPredefined.DARK_YELLOW);
        addColor("999933", HSSFColor.HSSFColorPredefined.DARK_YELLOW);
        addColor("999966", HSSFColor.HSSFColorPredefined.DARK_YELLOW);

        addColor("666600", HSSFColor.HSSFColorPredefined.GREY_80_PERCENT);
        addColor("666633", HSSFColor.HSSFColorPredefined.GREY_80_PERCENT);
        addColor("666666", HSSFColor.HSSFColorPredefined.GREY_80_PERCENT);

        addColor("ffccff", HSSFColor.HSSFColorPredefined.ROSE);
        addColor("ffcccc", HSSFColor.HSSFColorPredefined.ROSE);
        addColor("ff9999", HSSFColor.HSSFColorPredefined.ROSE);
        addColor("ff99cc", HSSFColor.HSSFColorPredefined.ROSE);

        addColor("66ffff", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("66ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("33ffff", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("33ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("00ffff", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("00ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("99ffff", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("99ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE);
        addColor("ccffff", HSSFColor.HSSFColorPredefined.TURQUOISE);

        addColor("006600", HSSFColor.HSSFColorPredefined.DARK_GREEN);
        addColor("006633", HSSFColor.HSSFColorPredefined.DARK_GREEN);
        addColor("006666", HSSFColor.HSSFColorPredefined.DARK_GREEN);
        addColor("336600", HSSFColor.HSSFColorPredefined.DARK_GREEN);
        addColor("336633", HSSFColor.HSSFColorPredefined.DARK_GREEN);
        addColor("336666", HSSFColor.HSSFColorPredefined.DARK_GREEN);

        addColor("3377cc", HSSFColor.HSSFColorPredefined.BLUE);
        addColor("3300ff", HSSFColor.HSSFColorPredefined.BLUE);
        addColor("6600ff", HSSFColor.HSSFColorPredefined.BLUE);

        addColor("cc00ff", HSSFColor.HSSFColorPredefined.LAVENDER);
        addColor("ccccff", HSSFColor.HSSFColorPredefined.LAVENDER);
        addColor("cc99ff", HSSFColor.HSSFColorPredefined.LAVENDER);

        addColor("0099ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE);
        addColor("cc33ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE);
        addColor("cc66cc", HSSFColor.HSSFColorPredefined.LIGHT_BLUE);
        addColor("cc66ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE);
        addColor("cc99cc", HSSFColor.HSSFColorPredefined.LIGHT_BLUE);
        addColor("3399ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE);

        addColor("cc0099", HSSFColor.HSSFColorPredefined.PLUM);
        addColor("cc33cc", HSSFColor.HSSFColorPredefined.PLUM);

        addColor("cc6600", HSSFColor.HSSFColorPredefined.ORANGE);
        addColor("cc6633", HSSFColor.HSSFColorPredefined.ORANGE);
        addColor("cc6666", HSSFColor.HSSFColorPredefined.ORANGE);

        addColor("999999", HSSFColor.HSSFColorPredefined.GREY_40_PERCENT);

        addColor("9999ff", HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE);
        addColor("6666cc", HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE);

        addColor("cc9999", HSSFColor.HSSFColorPredefined.TAN);
        addColor("ff6666", HSSFColor.HSSFColorPredefined.TAN);

        addColor("99cc99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN);
        addColor("66cc99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN);
        addColor("ccff99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN);
        addColor("ccffcc", HSSFColor.HSSFColorPredefined.LIGHT_GREEN);
        addColor("66ff99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN);

        addColor("99cccc", HSSFColor.HSSFColorPredefined.AQUA);

        addColor("99ccff", HSSFColor.HSSFColorPredefined.PALE_BLUE);
        addColor("a2c8e8", HSSFColor.HSSFColorPredefined.PALE_BLUE);

        addColor("cccc00", HSSFColor.HSSFColorPredefined.LIME);
        addColor("cccc33", HSSFColor.HSSFColorPredefined.LIME);
        addColor("cccc66", HSSFColor.HSSFColorPredefined.LIME);
        addColor("cccc99", HSSFColor.HSSFColorPredefined.LIME);
        addColor("ccff00", HSSFColor.HSSFColorPredefined.LIME);
        addColor("ccff33", HSSFColor.HSSFColorPredefined.LIME);
        addColor("ccff66", HSSFColor.HSSFColorPredefined.LIME);
        addColor("33cc00", HSSFColor.HSSFColorPredefined.LIME);
        addColor("33cc33", HSSFColor.HSSFColorPredefined.LIME);
        addColor("33cc66", HSSFColor.HSSFColorPredefined.LIME);
        addColor("66cc00", HSSFColor.HSSFColorPredefined.LIME);
        addColor("66cc33", HSSFColor.HSSFColorPredefined.LIME);
        addColor("66cc66", HSSFColor.HSSFColorPredefined.LIME);

        addColor("cccccc", HSSFColor.HSSFColorPredefined.GREY_25_PERCENT);

        addColor("00cc99", HSSFColor.HSSFColorPredefined.SEA_GREEN);
        addColor("33cc99", HSSFColor.HSSFColorPredefined.SEA_GREEN);
        addColor("009966", HSSFColor.HSSFColorPredefined.SEA_GREEN);
        addColor("339966", HSSFColor.HSSFColorPredefined.SEA_GREEN);

        addColor("00cccc", HSSFColor.HSSFColorPredefined.TEAL);
        addColor("009999", HSSFColor.HSSFColorPredefined.TEAL);
        addColor("339999", HSSFColor.HSSFColorPredefined.TEAL);

        addColor("00ccff", HSSFColor.HSSFColorPredefined.SKY_BLUE);
        addColor("33ccff", HSSFColor.HSSFColorPredefined.SKY_BLUE);
        addColor("66ccff", HSSFColor.HSSFColorPredefined.SKY_BLUE);
        addColor("0099cc", HSSFColor.HSSFColorPredefined.SKY_BLUE);

        addColor("33cccc", HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE);
        addColor("66cccc", HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE);
        addColor("d7e3f7", HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE);

        addColor("3399cc", HSSFColor.HSSFColorPredefined.ROYAL_BLUE);

        addColor("003366", HSSFColor.HSSFColorPredefined.DARK_TEAL);

        addColor("330066", HSSFColor.HSSFColorPredefined.INDIGO);
        
    }
    

    public static short getCorExcel(String corHTML) {
        return coresExcelHTML.get(corHTML.toLowerCase());
    }

}
