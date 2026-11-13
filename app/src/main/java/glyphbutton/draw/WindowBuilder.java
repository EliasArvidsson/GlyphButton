package glyphbutton.draw;

import java.util.List;

import glyphbutton.styles.BorderStyles;
import glyphbutton.widgets.Widget;

public class WindowBuilder {
    
    private char[] borderCharacters;
    private int width;
    private int height;
    private StringBuilder sb;
    private String[] window;
    private List<Widget> widgets;

    /**
     * 
     * @param width
     * @param height
     * @param borderStyle
     * @param widgets
     * @return
     */
    public String[] buildWindow(int width, int height, int borderStyle, List<Widget> widgets, boolean isFocused, int focusedWidget) {

        borderCharacters = BorderStyles.getBorderCharacters(borderStyle).toCharArray();

        this.width = width;
        this.height = height;
        this.widgets = widgets;

        if (isFocused) {
            widgets.get(focusedWidget).setFocused(true);
        } else {
            widgets.get(focusedWidget).setFocused(false);
        }

        window = new String[height + 2];

        buildWindowTop();
        buildWindowMiddle();
        buildWindowBottom();

        return window;
    }

    private int buildWidget(int x, int y, char character) {

        for (Widget w : widgets) {

            if (w.getY() == y && w.getX() == x && width >= (x + w.getLabel().length()) && !w.onWindowBorder()) {

                sb.append(w.draw());

                return w.getLabel().length() - 1;
            }
        }
        sb.append(character);
        return 0;
    }

    private int buildWidgetOnBorder(int x, int y, char character) {

        for (Widget w : widgets) {

            if (w.getY() == y && w.getX() == x && width >= (x + w.getLabel().length()) && w.onWindowBorder()) {

                sb.append(w.draw());

                return w.getLabel().length() - 1;
            }
        }
        sb.append(character);
        return 0;
    }

    private void buildWindowTop() {
        sb = new StringBuilder();

        sb.append(borderCharacters[0]); // Top-left corner

        for (int p = 0; p < width; p++) {

            p += buildWidgetOnBorder(p, 0, borderCharacters[1]);  
        }

        sb.append(borderCharacters[2]); // Top-right corner

        window[0] = sb.toString();
    }

    private void buildWindowMiddle() {
        
        for (int i = 0; i < height; i++) {
            sb = new StringBuilder();

            sb.append(borderCharacters[3]); // Left border

            for (int o = 0; o < width; o++) {
                o += buildWidget(o, i, ' ');
            }

            sb.append(borderCharacters[3]); // Right border

            window[i + 1] = sb.toString();
        }
    }

    private void buildWindowBottom() {
        sb = new StringBuilder();

        sb.append(borderCharacters[4]); // Bottom-left corner

        for (int p = 0; p < width; p++) {
            sb.append(borderCharacters[1]);
        }

        sb.append(borderCharacters[5]); // Bottom-right corner

        window[height + 1] = sb.toString();
    }

}
