package glyphbutton.window;

public class Window { // window class to represent a window in the terminal GUI. Work in progress.

    private int width;
    private int height;
    private String title;
    private String style;

    public Window(int width, int height, String title, String style) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.style = style;
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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

}
