package com.msoft.mbi.cube.multi.generation;

import java.util.HashMap;

import org.apache.poi.hssf.util.HSSFColor;

public class CorUtil {

    private static final HashMap<String, Short> coresExcelHTML;

    static {
        coresExcelHTML = new HashMap<String, Short>();
        coresExcelHTML.put("ff0000", HSSFColor.HSSFColorPredefined.RED.getIndex());
        coresExcelHTML.put("ff0033", HSSFColor.HSSFColorPredefined.RED.getIndex());
        coresExcelHTML.put("ff3300", HSSFColor.HSSFColorPredefined.RED.getIndex());
        coresExcelHTML.put("ff3333", HSSFColor.HSSFColorPredefined.RED.getIndex());
        coresExcelHTML.put("ff0066", HSSFColor.HSSFColorPredefined.RED.getIndex());
        coresExcelHTML.put("ff3366", HSSFColor.HSSFColorPredefined.RED.getIndex());

        coresExcelHTML.put("cc0000", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("cc0033", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("cc3300", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("cc3333", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("cc0066", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("cc3366", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("990000", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("990033", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("993300", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("993333", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("990066", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("993366", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("663300", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("663333", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("660000", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
        coresExcelHTML.put("660033", HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());

        coresExcelHTML.put("ff00ff", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff33ff", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff66ff", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff66cc", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff33cc", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff00cc", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff0099", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff3399", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff6699", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("cc00cc", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("cc6699", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("ff99ff", HSSFColor.HSSFColorPredefined.PINK.getIndex());
        coresExcelHTML.put("cc3399", HSSFColor.HSSFColorPredefined.PLUM.getIndex());

        coresExcelHTML.put("333399", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("330099", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("3300cc", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("3333cc", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("3333ff", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("3366ff", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("3366cc", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("336699", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("000066", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("003399", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("000099", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("0000cc", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("0033cc", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("0000ff", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("0033ff", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());
        coresExcelHTML.put("000080", HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex());

        coresExcelHTML.put("0066ff", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("0066cc", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("006699", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("669999", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("6699cc", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("6699ff", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("9999cc", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("666699", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("6666ff", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());
        coresExcelHTML.put("333366", HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex());

        coresExcelHTML.put("00cc00", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("00cc33", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("00cc66", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("00ff66", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("00ff33", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("00ff00", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("339900", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("339933", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("009933", HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        coresExcelHTML.put("009900", HSSFColor.HSSFColorPredefined.GREEN.getIndex());

        coresExcelHTML.put("99ff00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("99ff33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("99ff66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("99ff00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("33ff33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("33ff66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("33ff99", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("66ff00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("66ff33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("66ff66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("99ff99", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("00ff99", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("33ff00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("99cc00", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("99cc33", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("99cc66", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("669900", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("669933", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());
        coresExcelHTML.put("669966", HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex());

        coresExcelHTML.put("ffffff", HSSFColor.HSSFColorPredefined.WHITE.getIndex());

        coresExcelHTML.put("003300", HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        coresExcelHTML.put("003333", HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        coresExcelHTML.put("000000", HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        coresExcelHTML.put("000033", HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        coresExcelHTML.put("333300", HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        coresExcelHTML.put("333333", HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        coresExcelHTML.put("330033", HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        coresExcelHTML.put("330000", HSSFColor.HSSFColorPredefined.BLACK.getIndex());

        coresExcelHTML.put("663300", HSSFColor.HSSFColorPredefined.BROWN.getIndex());
        coresExcelHTML.put("663333", HSSFColor.HSSFColorPredefined.BROWN.getIndex());
        coresExcelHTML.put("660033", HSSFColor.HSSFColorPredefined.BROWN.getIndex());
        coresExcelHTML.put("660000", HSSFColor.HSSFColorPredefined.BROWN.getIndex());
        coresExcelHTML.put("996600", HSSFColor.HSSFColorPredefined.BROWN.getIndex());
        coresExcelHTML.put("996633", HSSFColor.HSSFColorPredefined.BROWN.getIndex());
        coresExcelHTML.put("996666", HSSFColor.HSSFColorPredefined.BROWN.getIndex());

        coresExcelHTML.put("ff9900", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ff9933", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ff9966", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ffcc66", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ffcc99", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ffcc33", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ffcc00", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ff6600", HSSFColor.HSSFColorPredefined.GOLD.getIndex());
        coresExcelHTML.put("ff6633", HSSFColor.HSSFColorPredefined.GOLD.getIndex());

        coresExcelHTML.put("6633ff", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("6633cc", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("6600cc", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("663399", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("660099", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("660066", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("663366", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("990099", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("9900cc", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("9900ff", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("9933ff", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("9933cc", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("993399", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("996699", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("9966cc", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());
        coresExcelHTML.put("9966ff", HSSFColor.HSSFColorPredefined.VIOLET.getIndex());

        coresExcelHTML.put("ffff00", HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        coresExcelHTML.put("ffff33", HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        coresExcelHTML.put("ffff66", HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        coresExcelHTML.put("ffff99", HSSFColor.HSSFColorPredefined.YELLOW.getIndex());
        coresExcelHTML.put("ffffcc", HSSFColor.HSSFColorPredefined.YELLOW.getIndex());

        coresExcelHTML.put("cc9900", HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        coresExcelHTML.put("cc9933", HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        coresExcelHTML.put("cc9966", HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        coresExcelHTML.put("999900", HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        coresExcelHTML.put("999933", HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        coresExcelHTML.put("999966", HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
        coresExcelHTML.put("999966", HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());

        coresExcelHTML.put("666600", HSSFColor.HSSFColorPredefined.GREY_80_PERCENT.getIndex());
        coresExcelHTML.put("666633", HSSFColor.HSSFColorPredefined.GREY_80_PERCENT.getIndex());
        coresExcelHTML.put("666666", HSSFColor.HSSFColorPredefined.GREY_80_PERCENT.getIndex());

        coresExcelHTML.put("ffccff", HSSFColor.HSSFColorPredefined.ROSE.getIndex());
        coresExcelHTML.put("ffcccc", HSSFColor.HSSFColorPredefined.ROSE.getIndex());
        coresExcelHTML.put("ff9999", HSSFColor.HSSFColorPredefined.ROSE.getIndex());
        coresExcelHTML.put("ff99cc", HSSFColor.HSSFColorPredefined.ROSE.getIndex());

        coresExcelHTML.put("66ffff", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("66ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("33ffff", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("33ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("00ffff", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("00ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("99ffff", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("99ffcc", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());
        coresExcelHTML.put("ccffff", HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex());

        coresExcelHTML.put("006600", HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        coresExcelHTML.put("006633", HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        coresExcelHTML.put("006666", HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        coresExcelHTML.put("336600", HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        coresExcelHTML.put("336633", HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());
        coresExcelHTML.put("336666", HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex());

        coresExcelHTML.put("3377cc", HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        coresExcelHTML.put("3300ff", HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        coresExcelHTML.put("6600ff", HSSFColor.HSSFColorPredefined.BLUE.getIndex());

        coresExcelHTML.put("cc00ff", HSSFColor.HSSFColorPredefined.LAVENDER.getIndex());
        coresExcelHTML.put("ccccff", HSSFColor.HSSFColorPredefined.LAVENDER.getIndex());
        coresExcelHTML.put("cc99ff", HSSFColor.HSSFColorPredefined.LAVENDER.getIndex());

        coresExcelHTML.put("0099ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        coresExcelHTML.put("cc33ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        coresExcelHTML.put("cc66cc", HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        coresExcelHTML.put("cc66ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        coresExcelHTML.put("cc99cc", HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());
        coresExcelHTML.put("3399ff", HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex());

        coresExcelHTML.put("cc0099", HSSFColor.HSSFColorPredefined.PLUM.getIndex());
        coresExcelHTML.put("cc33cc", HSSFColor.HSSFColorPredefined.PLUM.getIndex());

        coresExcelHTML.put("cc33cc", HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
        coresExcelHTML.put("cc6600", HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
        coresExcelHTML.put("cc6633", HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
        coresExcelHTML.put("cc6666", HSSFColor.HSSFColorPredefined.ORANGE.getIndex());

        coresExcelHTML.put("999999", HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex());

        coresExcelHTML.put("9999ff", HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex());
        coresExcelHTML.put("6666cc", HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex());

        coresExcelHTML.put("cc9999", HSSFColor.HSSFColorPredefined.TAN.getIndex());
        coresExcelHTML.put("ff6666", HSSFColor.HSSFColorPredefined.TAN.getIndex());

        coresExcelHTML.put("99cc99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        coresExcelHTML.put("66cc99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        coresExcelHTML.put("ccff99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        coresExcelHTML.put("ccffcc", HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());
        coresExcelHTML.put("66ff99", HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex());

        coresExcelHTML.put("99cccc", HSSFColor.HSSFColorPredefined.AQUA.getIndex());

        coresExcelHTML.put("99ccff", HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
        coresExcelHTML.put("a2c8e8", HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());

        coresExcelHTML.put("cccc00", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("cccc33", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("cccc66", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("cccc99", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("ccff00", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("ccff33", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("ccff66", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("33cc00", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("33cc33", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("33cc66", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("66cc00", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("66cc33", HSSFColor.HSSFColorPredefined.LIME.getIndex());
        coresExcelHTML.put("66cc66", HSSFColor.HSSFColorPredefined.LIME.getIndex());

        coresExcelHTML.put("cccccc", HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());

        coresExcelHTML.put("00cc99", HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());
        coresExcelHTML.put("33cc99", HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());
        coresExcelHTML.put("009966", HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());
        coresExcelHTML.put("339966", HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex());

        coresExcelHTML.put("00cccc", HSSFColor.HSSFColorPredefined.TEAL.getIndex());
        coresExcelHTML.put("009999", HSSFColor.HSSFColorPredefined.TEAL.getIndex());
        coresExcelHTML.put("339999", HSSFColor.HSSFColorPredefined.TEAL.getIndex());

        coresExcelHTML.put("00ccff", HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        coresExcelHTML.put("33ccff", HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        coresExcelHTML.put("66ccff", HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        coresExcelHTML.put("0099cc", HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());

        coresExcelHTML.put("33cccc", HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE.getIndex());
        coresExcelHTML.put("66cccc", HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE.getIndex());
        coresExcelHTML.put("d7e3f7", HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE.getIndex());

        coresExcelHTML.put("3399cc", HSSFColor.HSSFColorPredefined.ROYAL_BLUE.getIndex());

        coresExcelHTML.put("003366", HSSFColor.HSSFColorPredefined.DARK_TEAL.getIndex());

        coresExcelHTML.put("330066", HSSFColor.HSSFColorPredefined.INDIGO.getIndex());
    }

    public static short getCorExcel(String corHTML) {
        return coresExcelHTML.get(corHTML.toLowerCase());
    }

}
