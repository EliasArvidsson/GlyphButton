package glyphbutton.window;

import java.util.ArrayList;
import java.util.List;

import glyphbutton.widgets.ButtonWidget;
import glyphbutton.widgets.Widget;

public class Window { // window class to represent a window in the terminal GUI. Work in progress.

    private int width;
    private int height;
    private String title;
    private int borderStyle;
    private List<Widget> widgets = new ArrayList<>();

    /**
     * Constructor for the Window class.
     * 
     * @param width
     * @param height
     * @param title
     * @param borderStyle
     */
    public Window(int width, int height, String title, int borderStyle) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.borderStyle = borderStyle;
        windowWidgets(); // Initialize window widgets (minimize and close buttons)
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    private void windowWidgets() {

        Widget minimizeButton = new ButtonWidget("minimizeWindow", "[-]", () -> {
            // Minimize window logic here
        });

        minimizeButton.setPosition(width - 10, 0); // Position the minimize button at the top-right corner of the window

        Widget closeButton = new ButtonWidget("closeWindow", "[x]", () -> {
            // Close window logic here
        });
        closeButton.setPosition(width - 4, 0); // Position the close button at the top-right corner of the window

        widgets.add(minimizeButton);
        widgets.add(closeButton);

    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
    }
}
