package glyphbutton.draw;

import glyphbutton.styles.BorderStyles;

public class WorkSpaceBuilder {

    private int maxWidth;
    private int maxHeight;

    private int[] xWindow;
    private int[] yWindow;

    private String[][] windows;
    private char[] borderCharacters;
    private StringBuilder sb;
    
    public String buildWorkSpace(String[][] windows, int[] xWindow, int[] yWindow, int maxWidth, int maxHeight, int borderStyle) {

        this.windows = windows;
        this.xWindow = xWindow;
        this.yWindow = yWindow;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;

        borderCharacters = BorderStyles.getBorderCharacters(borderStyle).toCharArray();

        sb = new StringBuilder();

        buildWindowTop();
        buildWindowMiddle();
        buildWindowBottom();
        

        return sb.toString();

    }

    private int buildWindow(int x, int y) {

        for (int i = 0; i < windows.length; i++) {

          if (yWindow[i] <= y && windows[i].length > y - yWindow[i] && xWindow[i] == x) {

            sb.append(windows[i][y - yWindow[i]]);
            return windows[i][y - yWindow[i]].length() - 1;
          }
        }
        sb.append("~");
        return 0;
    }

    private void buildWindowTop() {

        sb.append(borderCharacters[0]); // Top-left corner

        for (int p = 0; p < maxWidth; p++) {
            sb.append(borderCharacters[1]);
        }

        sb.append(borderCharacters[2]); // Top-right corner
        sb.append("\n");
    }

    private void buildWindowMiddle() {
        
        for (int i = 0; i < maxHeight; i++) {

            sb.append(borderCharacters[3]); // Left border

            for (int o = 0; o < maxWidth; o++) {
                o += buildWindow(o, i);
            }

            sb.append(borderCharacters[3]); // Right border
            sb.append("\n");
        }
    }

    private void buildWindowBottom() {

        sb.append(borderCharacters[4]); // Bottom-left corner

        for (int p = 0; p < maxWidth; p++) {
            sb.append(borderCharacters[1]);
        }

        sb.append(borderCharacters[5]); // Bottom-right corner
    }

}
