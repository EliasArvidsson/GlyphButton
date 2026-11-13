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

    private int offsetY = 0;

    /**
     * Constructor for the Window class.
     * Initialize a window object. Default values used.
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

    /**
     * Returns the (inner) width of the window
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the (inner) height of the window
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the (inner) width of the window
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the (inner) height of the window
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the content of the title widget
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the content of the title widget
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns int representing a style
     * @return
     */
    public int getBorderStyle() {
        return borderStyle;
    }

    /**
     * Sets the style for the window
     * @param borderStyle
     */
    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    /**
     * Widgets necessary for a window
     */
    private void windowWidgets() {

        ButtonWidget minimizeButton = new ButtonWidget("minimizeWindow", "[-]", () -> {
            // Minimize window logic here
        });

        minimizeButton.setPosition(width - 10, 0); // Position the minimize button at the top-right corner of the window
        minimizeButton.setOnWindowBorder(true);

        ButtonWidget closeButton = new ButtonWidget("closeWindow", "[x]", () -> {
            // Close window logic here
        });
        closeButton.setPosition(width - 4, 0); // Position the close button at the top-right corner of the window
        closeButton.setOnWindowBorder(true);

        widgets.add(minimizeButton);
        widgets.add(closeButton);

    }

    /**
     * Returns a list of widget objects
     * @return
     */
    public List<Widget> getWidgets() {
        return widgets;
    }

    /**
     * Add a widget object to the Widget list
     * @param widget
     */
    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     * Sets the offset that the window will have from the top corner
     * 
     * @param y
     */
    public void setOffsetY(int y) {
        offsetY = y;
    }

    /**
     * Returns the vertical offset, Y
     * @return
     */
    public int getOffsetY() {
        return offsetY;
    }
}
