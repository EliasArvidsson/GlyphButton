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
import org.jline.reader.History;
import org.jline.reader.History.Entry;

import glyphbutton.styles.BorderStyles;
import glyphbutton.window.*;

public class TerminalGuiLogic {

    private final Terminal terminal;
    private final LineReader reader;
    private final List<Window> windows = new ArrayList<>();

    private int focusedWindow = 0;
    private Focus focus = Focus.PROMPT;

    public TerminalGuiLogic() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        terminal.puts(Capability.clear_screen);
        initWindows();
        setupKeyBindings();
    }

    private void setupKeyBindings() {

        reader.getWidgets().put("switch-focus", () -> {
            focus = (focus == Focus.PROMPT) ? Focus.WINDOWS : Focus.PROMPT;
            clearScreen();
            drawWindows();

            return true;
        });

        reader.getWidgets().put("page-down", () -> {
            if (focus == Focus.WINDOWS) {
                focusedWindow = (focusedWindow + 1) % windows.size();
                clearScreen();
                drawWindows();
            }
            return true;
        });

        reader.getWidgets().put("page-up", () -> {
            if (focus == Focus.WINDOWS) {
                focusedWindow = (focusedWindow - 1 + windows.size()) % windows.size();
                clearScreen();
                drawWindows();
            }
            return true;
        });

        // Bind the keys to the windows using Reference
        KeyMap<Binding> keyMap = reader.getKeyMaps().get(LineReader.MAIN);

        keyMap.bind(new Reference("switch-focus"), KeyMap.key(terminal, Capability.key_up));
        keyMap.bind(new Reference("page-down"), KeyMap.key(terminal, Capability.key_npage));
        keyMap.bind(new Reference("page-up"), KeyMap.key(terminal, Capability.key_ppage));

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

    public void run() throws IOException {

        clearScreen();

        while (true) {

            try {

                drawWindows();

                String line = reader.readLine(getPrompt());
                handleCommand(line);

            } catch (UserInterruptException | EndOfFileException e) {
                break;
            }
        }
    }

    private String getPrompt() {
        return focus == Focus.PROMPT ? "> " : "(" + windows.get(focusedWindow).getTitle() + ") > ";
    }

    private void drawWindows() {

        Window window = windows.get(focusedWindow);
        RenderWindow renderWindow = new RenderWindow(window.getBorderStyle(), window.getWidth(), window.getHeight(),
                reader, window.getWidgets(), terminal, focus);
        renderWindow.drawWindow();

        drawHistory();

        terminal.puts(Capability.cursor_address, window.getHeight() + 2, 1);

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
            } else {
                reader.printAbove("No commands in history.");
            }
            terminal.flush();
        }
    }

    private void handleCommand(String line) throws IOException {
        // Handle the command entered in the prompt
        // For now, just echo it back
        terminal.writer().println("You entered: " + line);
        terminal.flush();

    }

    private void initWindows() {

        // Initialize windows with different border styles
        windows.add(new Window(30, 10, "Window 1", BorderStyles.SINGLE));
        windows.add(new Window(40, 15, "Window 2", BorderStyles.DOUBLE));
        windows.add(new Window(50, 20, "Window 3", BorderStyles.ROUND));
        windows.add(new Window(100, 12, "Window 4", BorderStyles.HEAVY));

    }

}