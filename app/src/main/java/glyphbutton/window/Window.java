package glyphbutton.window;

import java.util.ArrayList;
import java.util.List;

import glyphbutton.widgets.ButtonWidget;
import glyphbutton.widgets.Widget;

public class Window { // window class to represent a window in the terminal GUI. Work in progress.

    private int width;
    private int height;
    private String title;
    private int borderStyle = 1;
    private List<Widget> widgets = new ArrayList<>();
    private boolean minimized = false;

    /**
     * Constructor for the Window class.
     * Initialize a window object. Default values used.
     * @param width
     * @param height
     * @param title
     * @param borderStyle
     */
    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        windowWidgets(); // Initialize window widgets (minimize and close buttons)
    }

    private ButtonWidget closeButton;

    public void setOnClose(Runnable onClose) {
        if (closeButton != null)
            closeButton.setOnPress(onClose);
    }

    private ButtonWidget minimizeButton;

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
        if (!minimized)
            return height;
        else return 0;
        
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

        minimizeButton = new ButtonWidget("minimizeWindow", "[-]");

        minimizeButton.setPosition(width - 10, 0); // Position the minimize button at the top-right corner of the window
        minimizeButton.setOnWindowBorder(true);
        minimizeButton.setOnPress(this::minimizeWindow);

        closeButton = new ButtonWidget("closeWindow", "[x]");
        closeButton.setPosition(width - 4, 0); // Position the close button at the top-right corner of the window
        closeButton.setOnWindowBorder(true);

        widgets.add(minimizeButton);
        widgets.add(closeButton);

    }

    public boolean getMinimized() {
        return minimized;
    }

    public void minimizeWindow() {
        if (!minimized) {
            minimized = true;
        } else {
            minimized = false;
        }
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

}
