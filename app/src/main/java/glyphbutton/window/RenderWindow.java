package glyphbutton.window;

import glyphbutton.Focus;
import glyphbutton.styles.*;
import glyphbutton.widgets.Widget;
import glyphbutton.TerminalGuiLogic;

import java.util.List;

import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;


public class RenderWindow {

    private int borderStyle;
    private final int width;
    private final int height;
    private final LineReader reader;
    private final List<Widget> widgets;
    private final Terminal terminal;
    private final Focus focus;
    private int previousWindowHeight = 0;
    

    public RenderWindow(int borderStyle, int width, int height, LineReader reader, List<Widget> widgets, Terminal terminal, Focus focus, int previousWindowHeight) {
        this.borderStyle = borderStyle;
        this.width = width;
        this.height = height;
        this.reader = reader;
        this.widgets = widgets;
        this.terminal = terminal;
        this.focus = focus;
        this.previousWindowHeight = previousWindowHeight;

        setupKeyBindings(focus);
    }

    public void drawWindow() {
        borderBuilder();
        widgetBuilder();
    }
    
    private void borderBuilder() {
        
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

        String output = sb.toString();

        clearPreviousWindow(height);
        reader.printAbove(output);

    }

    private void clearPreviousWindow(int height) {

        if (height <= 0) return;

        if (height > previousWindowHeight) {
            height = previousWindowHeight;
        }

        height += 4; // Add 2 for the top and bottom borders

        // Move to the beginning of the previous window
        terminal.writer().print("\033[" + height + "A");

        // Clear each line occupied by the window
        for (int i = 0; i < height; i++) {
            terminal.writer().print("\033[2K");

            if (i < height - 1) {
                terminal.writer().print("\033[1B");
            }
        }

        // Return cursor to the starting position
        terminal.writer().print("\033[" + (height) + "A");

        terminal.flush();
    }

    private void widgetBuilder() {

        
        for (int i = 0; i < widgets.size(); i++) {
            Widget widget = widgets.get(i);

            int x = widget.getX();
            int y = widget.getY();

            String renderedWidget = widget.render(i == focusedWidget && focus == Focus.WINDOWS);

            // Move cursor to widget position
            terminal.writer().print(
                "\033[" + (y + 1) + ";" + (x + 1) + "H"
            );

            // Render widget
            terminal.writer().print(renderedWidget);
        }

        // Move cursor below the rendered window
        terminal.writer().print("\033[" + (height + 3) + ";1H");

        terminal.flush();
        
    }

    private int focusedWidget = 0;
    

    private void setupKeyBindings(Focus focus) {

        

        reader.getWidgets().put("activate-widget", () -> {
            if (focus == Focus.WINDOWS) {
                Widget widget = widgets.get(focusedWidget);
                widget.activate();
                drawWindow();
                
            }
            return true;
        });

    reader.getWidgets().put("switch-widget", () -> {
        if (focus == Focus.WINDOWS) {
            focusedWidget = (focusedWidget - 1 + widgets.size()) % widgets.size();
            drawWindow();
        }
        return true;
    });   
    

    // Bind the keys to the windows using Reference
    KeyMap<Binding> keyMap = reader.getKeyMaps().get(LineReader.MAIN);

        
    keyMap.bind(new Reference("switch-widget"),"\t");
    keyMap.bind(new Reference("activate-widget"), KeyMap.key(terminal, Capability.key_right));

    }

}
