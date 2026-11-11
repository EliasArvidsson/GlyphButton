package glyphbutton.draw;

import java.io.IOException;
import java.util.List;

import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

import glyphbutton.Focus;
import glyphbutton.window.Window;
import glyphbutton.widgets.*;

public class DrawToTerminal {

    private final Terminal terminal;
    private final LineReader reader;
    private List<Window> windows;
    private Focus focus;
    private final WindowBorderBuilder wbb;
    private String windowBorders;
    private final int margin;
    private int windowsAmount;
    private int focusedWindow;
    private int focusedWidget;

    public DrawToTerminal(Terminal terminal, LineReader reader, int margin) {

        this.terminal = terminal;
        this.reader = reader;
        wbb = new WindowBorderBuilder();
        this.margin = margin;
    }

    public void draw(List<Window> windows, Focus focus, int focusedWindow, int focusedWidget) {
        this.windowsAmount = windows.size();
        this.focus = focus;
        this.focusedWindow = focusedWindow;
        this.focusedWidget = focusedWidget;

        clearTerminal();

        // If the requested window configuration is the same as the one before, print the old one
        if (windows == this.windows) {
            reader.printAbove(windowBorders);
        } else {
            drawBorder(windows);
            this.windows = windows;
        }

        drawWidget();
        drawHistory();

        terminal.puts(Capability.cursor_address, 22, 0);
        terminal.flush();
    }

    private void clearTerminal() {

        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Draw borders
     * @param windows
     */
    private void drawBorder(List<Window> windows) {

        int[] width = new int[windowsAmount];
        int[] height = new int[windowsAmount];
        int[] borderStyle = new int[windowsAmount];
        int[] offsetY = new int[windowsAmount];

        for(int i = 0; i < windowsAmount; i++) {
            width[i] = windows.get(i).getWidth();
            height[i] = windows.get(i).getHeight();
            borderStyle[i] = windows.get(i).getBorderStyle();
            offsetY[i] = windows.get(i).getOffsetY();
        }

        windowBorders = wbb.buildBorder(width, height, margin, borderStyle, offsetY);
        reader.printAbove(windowBorders);
    }

    private void drawWidget() {

        for(int p = 0; p < windowsAmount; p++) {
            Window w = windows.get(p);

            //
            for (int i = 0; i < w.getWidgets().size(); i++) {

                Widget wid = w.getWidgets().get(i);

                String drawnWidget = wid.draw(focus == Focus.WINDOWS && (p == focusedWindow && i == focusedWidget));

                int x;
                if (p > 0) {
                    x = 0;
                    for (int o = 0; o < p; o++) {
                        Window ww = windows.get(o);
                        x += ww.getWidth() + margin + 2;
                    }
                    x += wid.getX();

                } else {x = wid.getX();}
                
                // Move cursor to widget position
                terminal.writer().print(
                    "\033[" + (wid.getY() + 1) + ";" + (x + 1) + "H");

                terminal.writer().print(drawnWidget);
            }
        }
        // Move cursor below the rendered window
        terminal.writer().print("\033[" + (23) + ";1H");

        terminal.flush();
    }

    private void drawHistory() {
        History history = reader.getHistory();
        if (history != null) {
            int historySize = history.size();

            reader.printAbove("\n"); // print a empty line for the line reader prompt

            if (historySize > 0) {
                for (int i = historySize - 1; i > historySize - 6 && i >= 0; i--) {
                    reader.printAbove(history.get(i));
                }
            }
            terminal.flush();
        }
    }
    
}
