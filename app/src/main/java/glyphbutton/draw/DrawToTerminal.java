package glyphbutton.draw;

import java.io.IOException;
import java.util.List;

import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import glyphbutton.Focus;
import glyphbutton.window.Window;
import glyphbutton.draw.WindowBorderBuilder;

public class DrawToTerminal {

    private final Terminal terminal;
    private final LineReader reader;
    private final List<Window> windows;
    private final Focus focus;

    public DrawToTerminal(Terminal terminal, LineReader reader, Focus focus, List<Window> windows) {

        this.terminal = terminal;
        this.reader = reader;
        this.focus = focus;
        this.windows = windows;

    }

    public void draw() {

        clearTerminal();
        drawWindows(windows);

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

        terminal.flush();

    }

    private void drawWindows(List<Window> windows) {

        /**
         * The list order is the priority that the windows are drawn in.
         */

    }

}
