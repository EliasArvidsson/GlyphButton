package glyphbutton.styles;

public class BorderStyles {
    
    public static final int SINGLE = 1;
    public static final int DOUBLE = 2;
    public static final int ROUND = 3;
    public static final int HEAVY = 4;

    public static String getBorderCharacters(int borderStyle) {
        switch (borderStyle) {
            case SINGLE:
                return "┌─┐│└┘┤├┬┴┼";
            case DOUBLE:
                return "╔═╗║╚╝╣╠╦╩╬";
            case ROUND:
                return "╭─╮│╰╯┤├┬┴┼";
            case HEAVY:
                return "┏━┓┃┗┛┫┣┳┻╋";
            default:
                throw new IllegalArgumentException("Invalid border style: " + borderStyle);
        }
    }

}
