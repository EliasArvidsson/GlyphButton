package glyphbutton.draw;

import java.util.Arrays;

import glyphbutton.styles.BorderStyles;

public class WindowBorderBuilder {

    private char[][] borderCharacters;
    private int[] width;
    private int[] height;
    private int margin;
    private int windowsToBuild;
    private StringBuilder sb;
    
    private int[] offsetY;

    /**
     * 
     * @param width
     * @param height
     * @param borderStyle
     * @return
     */
    public String buildBorder(int[] width, int[] height, int margin, int[] borderStyle, int[] offsetY) {

        this.borderCharacters = new char[borderStyle.length][];
        this.width = width;
        this.height = height;
        this.margin = margin;
        windowsToBuild = borderStyle.length;
        this.sb = new StringBuilder();

        this.offsetY = offsetY;

        for (int i = 0; i < borderStyle.length; i++) {
            borderCharacters[i] = BorderStyles.getBorderCharacters(borderStyle[i]).toCharArray();
        }

        while (true) {
            for (int i = 0; i < windowsToBuild; i++) {

                borderBuildingLogic(i);
            }
            sb.append("\n");
            if (Arrays.stream(height).allMatch(value -> value <= -1)) {
                break;
            }
        }
        sb.append("\n");

        return sb.toString();
    }

    private void borderBuildingLogic(int window) {

        if (height[window] < 0) {buildVoid(window);}

        if (height[window] == 0) {
            buildBorderBottom(window);
            height[window] -= 1;
        }

        if (height[window] > 0 && offsetY[window] < 0) {
            buildBorderCenter(window);
            height[window] -= 1;
        }

        if (offsetY[window] == 0) {
            buildBorderTop(window);
            offsetY[window] -= 1;
        }

        if (offsetY[window] > 0) {
            buildVoid(window);
            offsetY[window] -= 1;
        }

    }

    private void buildVoid(int window) {
        int duration = width[window] + margin + 2;
        for (int i = 0; i < duration; i++) {
            sb.append(" ");
        }
    }

    private void buildBorderTop(int window) {

        sb.append(borderCharacters[window][0]); // Top-left corner

        for (int p = 0; p < width[window]; p++) {
            sb.append(borderCharacters[window][1]);
        }

        sb.append(borderCharacters[window][2]); // Top-right corner

        addMargin(); 
    }

    private void buildBorderCenter(int window) {

        sb.append(borderCharacters[window][3]); // Left border

        for (int o = 0; o < width[window]; o++) {
            sb.append(" "); // Space inside the window
        }
        sb.append(borderCharacters[window][3]); // Right border  
        addMargin();
    }

    private void buildBorderBottom(int window) {

        sb.append(borderCharacters[window][4]); // Bottom-left corner

        for (int p = 0; p < width[window]; p++) {
            sb.append(borderCharacters[window][1]);
        }

        sb.append(borderCharacters[window][5]); // Bottom-right corner

        addMargin();
    }

    private void addMargin(){
        for (int p = 0; p < margin; p++) {sb.append(" ");}
    }

}
