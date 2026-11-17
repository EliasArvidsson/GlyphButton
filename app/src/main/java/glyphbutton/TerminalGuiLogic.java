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
import glyphbutton.widgets.ButtonWidget;
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

    private DrawToTerminal dtt;

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
        dtt = new DrawToTerminal(terminal, reader);
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
                
                windows.get(focusedWindow).getWidgets().get(focusedWidget).activate();
                drawWindows();

            }
            return true;
        });

        reader.getWidgets().put("switch-widget", () -> {
            if (focus == Focus.WINDOWS) {
                int ws = windows.get(focusedWindow).getWidgets().size();
                
                if (!windows.get(focusedWindow).getMinimized())
                    focusedWidget = (focusedWidget + 1) % ws;
                else focusedWidget = (focusedWidget + 1) % 2;

                drawWindows();
            }
            return true;
        });

        keyMap.bind(new Reference("activate-widget"), KeyMap.key(terminal, Capability.key_right));
        keyMap.bind(new Reference("switch-widget"), "\t");

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

        dtt.draw(windows, focus, focusedWindow, focusedWidget, false, margin);

    }

    private void handleCommand(String line) throws IOException {
        // Handle the command entered in the prompt
    }

    public void addWindow(Window window) {
        window.setOnClose(() -> closeWindow(window));
        windows.add(window);
    }

    private void closeWindow(Window window) {
        windows.remove(window);

        if (windows.isEmpty()) {
            focusedWindow = 0;
            focus = Focus.PROMPT;
        } else if(focusedWindow >= windows.size()) {
            focusedWindow = windows.size() - 1;
        }
    }

    private void initWindows() {
 
        Window window1 = new Window(130, 4, "Window 1");

        window1.setBorderStyle(BorderStyles.SINGLE);

        ButtonWidget buttonWidget = new ButtonWidget("button", " --- ");
        buttonWidget.setPosition(12, 3);
        buttonWidget.setOnPress(() -> {
            buttonWidget.setLabel(" XXX ");
        });

        window1.addWidget(buttonWidget);

        addWindow(window1);

    }

}