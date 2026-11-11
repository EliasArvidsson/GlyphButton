package glyphbutton.draw;

import glyphbutton.styles.BorderStyles;

public class WindowBorderBuilder {

    /**
     * 
     * @param width
     * @param height
     * @param borderStyle
     * @return
     */
    public String borderBuilder(int width, int height, int borderStyle) {

        char[] borderCharacters = BorderStyles.getBorderCharacters(borderStyle).toCharArray();

        StringBuilder sb = new StringBuilder();
        sb.append(borderCharacters[0]); // Top-left corner
        for (int i = 0; i < width; i++) { // Top border
            sb.append(borderCharacters[1]);
        }
        sb.append(borderCharacters[2]); // Top-right corner

        sb.append("\n");

        for (int i = 0; i < height; i++) { // Side borders
            sb.append(borderCharacters[3]); // Left border
            for (int j = 0; j < width; j++) {
                sb.append(" "); // Space inside the window
            }
            sb.append(borderCharacters[3]); // Right border
            sb.append("\n");
        }

        sb.append(borderCharacters[4]); // Bottom-left corner
        for (int i = 0; i < width; i++) { // Bottom border
            sb.append(borderCharacters[1]);
        }
        sb.append(borderCharacters[5]); // Bottom-right corner
        sb.append("\n");

        return sb.toString();

    }

    // Further logic and functions will be added at a later date.

}
