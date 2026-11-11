package glyphbutton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Reference;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

import glyphbutton.draw.DrawToTerminal;
import glyphbutton.styles.BorderStyles;
import glyphbutton.window.*;

public class TerminalGuiLogic {

    private final Terminal terminal;
    private final LineReader reader;
    private final List<Window> windows = new ArrayList<>();

    private KeyMap<Binding> keyMap;

    private int focusedWindow = 0;
    private int focusedWidget = 0;
    private Focus focus = Focus.PROMPT;

    private int margin = 1;

    public TerminalGuiLogic() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        clearScreen();
        initWindows();

        this.keyMap = reader.getKeyMaps().get(LineReader.MAIN);
        setupWindowKeyBindings();
        setupWidgetKeyBindings();
    }

    private void clearScreen() {

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

    private void setupWindowKeyBindings() {

        reader.getWidgets().put("switch-focus", () -> {
            focus = (focus == Focus.PROMPT) ? Focus.WINDOWS : Focus.PROMPT;
            drawWindows();

            return true;
        });

        reader.getWidgets().put("page-down", () -> {
            if (focus == Focus.WINDOWS) {
                focusedWindow = (focusedWindow + 1) % windows.size();
                drawWindows();
            }
            return true;
        });

        reader.getWidgets().put("page-up", () -> {
            if (focus == Focus.WINDOWS) {
                focusedWindow = (focusedWindow - 1 + windows.size()) % windows.size();
                drawWindows();
            }
            return true;
        });

        keyMap.bind(new Reference("switch-focus"), KeyMap.key(terminal, Capability.key_up));
        keyMap.bind(new Reference("page-down"), KeyMap.key(terminal, Capability.key_npage));
        keyMap.bind(new Reference("page-up"), KeyMap.key(terminal, Capability.key_ppage));

    }

    private void setupWidgetKeyBindings() {

        reader.getWidgets().put("activate-widget", () -> {
            if (focus == Focus.WINDOWS) {
                
                windows.get(focusedWindow).getWidgets().get(focusedWindow).activate();
                drawWindows();

            }
            return true;
        });

        reader.getWidgets().put("switch-widget", () -> {
            if (focus == Focus.WINDOWS) {
                int ws = windows.get(focusedWindow).getWidgets().size();
                focusedWidget = (focusedWidget - 1 + ws) % ws;
                drawWindows();
            }
            return true;
        });

        keyMap.bind(new Reference("switch-widget"), "\t");
        keyMap.bind(new Reference("activate-widget"), KeyMap.key(terminal, Capability.key_right));

    }

    public void run() throws IOException {

        while (true) {

            try {

                drawWindows();

                String line = reader.readLine("> ");
                handleCommand(line);

            } catch (UserInterruptException | EndOfFileException | IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    private void drawWindows() {

        DrawToTerminal dtt = new DrawToTerminal(terminal, reader, margin);

        dtt.draw(windows, focus, focusedWindow, focusedWidget);

    }



    private void handleCommand(String line) throws IOException {
        // Handle the command entered in the prompt
    }

    private void initWindows() {

        // Initialize windows with different border styles
        windows.add(new Window(30, 10, "Window 1", BorderStyles.SINGLE));
        windows.add(new Window(40, 15, "Window 2", BorderStyles.DOUBLE));
        windows.add(new Window(50, 20, "Window 3", BorderStyles.ROUND));
        windows.add(new Window(50, 13, "Window 4", BorderStyles.HEAVY));
        

    }

}