package glyphbutton.panel.overlay;

import java.util.List;

import glyphbutton.widgets.Widget;

public class Popup extends Overlay{

    public Popup(int width, int height, String title) {
        super(width, height, title);
        
    }

    @Override
    public int getBorderStyle() {
        return super.getBorderStyle();
    }

    @Override
    public void setBorderStyle(int borderStyle) {
        super.setBorderStyle(borderStyle);
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
    }

        @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public OverlayPosition getPosition() {
        return super.getPosition();
    }

    @Override
    public void setPosition(OverlayPosition positions) {
        super.setPosition(positions);
    }
    
    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    public List<Widget> getWidgets() {
        return super.getWidgets();
    }

    @Override
    public void addWidget(Widget widget) {
        super.addWidget(widget);
    }

    @Override
    public void setOnClose(Runnable onClose) {
        super.setOnClose(onClose);
    }

}
