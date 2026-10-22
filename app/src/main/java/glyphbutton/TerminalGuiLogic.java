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

import glyphbutton.widgets.ButtonWidget;
import glyphbutton.widgets.Widget;

public class TerminalGuiLogic {

    private final Terminal terminal;
    private final LineReader reader;
    private final List<Widget> widgets = new ArrayList<>();

    private int focusedWidget = 0;
    private Focus focus = Focus.PROMPT;

    public TerminalGuiLogic() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();
        
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();

        terminal.puts(Capability.clear_screen);
        initWidgets();
        setupKeyBindings();
    }

    private void setupKeyBindings() {
    
        // Register custom widgets
        reader.getWidgets().put("widget-down", () -> {
            if (focus == Focus.WIDGETS && !widgets.isEmpty()) {
                focusedWidget = (focusedWidget + 1) % widgets.size();
               
                redrawWidgets();
                
            }
            return true;
        });

        reader.getWidgets().put("widget-up", () -> {
            if (focus == Focus.WIDGETS && !widgets.isEmpty()) {
                focusedWidget = (focusedWidget - 1 + widgets.size()) % widgets.size();
                
                redrawWidgets();
                
            }
            return true;
        });

        reader.getWidgets().put("switch-focus", () -> {
            focus = (focus == Focus.PROMPT) ? Focus.WIDGETS : Focus.PROMPT;
            
            redrawWidgets();
            
            return true;    
        });

       
        reader.getWidgets().put("widget-run", () -> {
            if (focus == Focus.WIDGETS && !widgets.isEmpty()) {
                widgets.get(focusedWidget).activate();
            }
            return true;
        });
                   
            

        // Bind the keys to the widgets using Reference
        KeyMap<Binding> keyMap = reader.getKeyMaps().get(LineReader.MAIN);

        keyMap.bind(new Reference("widget-down"),  KeyMap.key(terminal, Capability.key_down));
        keyMap.bind(new Reference("widget-up"),    KeyMap.key(terminal, Capability.key_up));
        keyMap.bind(new Reference("switch-focus"), "\t");  // Tab
        keyMap.bind(new Reference("widget-run"), KeyMap.key(terminal, Capability.key_right));
    }

    public void run() throws IOException {
        while (true) {

            try {

                redrawWidgets();

                String line = reader.readLine(getPrompt());
                handleCommand(line);

            } catch (UserInterruptException | EndOfFileException e) {
                break;
            }
        }
    }

    private String getPrompt() {
        return focus == Focus.PROMPT ? "> " : "(widgets) > ";
    }

    private int lastWidgetLines = 0; // To keep track of the number of lines printed for widgets

    private void redrawWidgets() {

        if (widgets.isEmpty()) {clearPreviousWidgets(); return;};

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("┌─ Widgets ─────────────────────┐\n");

        for (int i = 0; i < widgets.size(); i++) {
            Widget w = widgets.get(i);
            boolean selected = (i == focusedWidget && focus == Focus.WIDGETS);
            sb.append(w.render(selected)).append("\n");
        }

        sb.append("└───────────────────────────────┘");
        sb.append("     (Tab to switch focus, ↑/↓ to navigate, Enter to activate)");
        
        String output = sb.toString();
        int newLines = output.split("\n", -1).length;

        // Clear previous widget lines
        clearPreviousWidgets();

        reader.printAbove(output);
        lastWidgetLines = newLines;

    }

    private void clearPreviousWidgets() {
        if (lastWidgetLines <= 0) return;

        int terminalHeight = terminal.getRows();

        terminal.writer().print("\033[" + terminalHeight + "A");

        for (int i = 0; i < lastWidgetLines; i++) {
            
            terminal.writer().print("\033[1A\033[2K");
            
        }
        terminal.flush();
        lastWidgetLines = 0;
    }

    private void handleCommand(String line) throws IOException {
        // Handle the command entered in the prompt
        // For now, just echo it back
        terminal.writer().println("You entered: " + line);
        terminal.flush();
    }

    private void initWidgets() {
        widgets.add(new ButtonWidget("uwu", "Help", () -> {
            String uwu = "▓▓▓▓▓▓▓▒▓▓▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▒▓▓▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▓▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▒▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒▒▓░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓▒▒▒▒▒▒▒▒▒▒▒▒▓▓▒░░▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▓▓▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▒▒▒▒▒▒▒▒▒▒▒░░░▒▒▒▒▒▒░▓▒▒▒▒▒▒▒▓▓▓▒▒▒▒▒▒▒▒▒▒▒▓▒▒░░░▒▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▓▒▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▒▒▒▓▒▒▓▒▒▒░░░░░▒▒▒▓▒░▒▒▒▒▒▒▒▒▒▓▓▓▒▒▒▒▒▒▒▒▒▒▓▒░░░░░▒▒▓▒▒▒▒▒▒▒▒▒▓▒▒▓▓▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▒▒▓▓▒▒▒▒▒▒░░░░░▒▒▒▒▒░▓▓▓▒▒▒▒▒▒▓▓▓▓▒▒▓▓▓▓▓▓▓▓▒░░░░░░▒▒▓▒▒▒▒▒▒▒▒▒▓▓▒▓▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▒▒▓▒▒▓▓█████▒░░░▒▒▓▒░░░▓▒▒▒▒▒▒▒▓▓▓▒▒▒▒▒▒▒▒▒▒░░░░░░░░▒▒▒▒▒▒▒░▒▒▒▓▓▓▓▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▒▓▒▓▓▒███████▓▓▒▒▓▒▒▒░░▒▒▒▒▒▒▒▓▓▓▒▒▒▒▒▒▒▒▒▓▒▒▒░░░░░▒▒▒▒▒▒▒░░▒▒▒▓▓▓▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▒▓▓▒▓███▒▒▒███████▓▓░░▒░▒▒▒▒▒▒▓▓▒▒▒▒▒▒▒▒▒▒░░░░░░░▒▒▒▓████████▓▒▒▒▓▓▒▒\n" + //
                                "▓▓▓█▓▓▓▓▓▓▓▒▓▓▒▒▒▒█▒▒░█░░▓██████▒░░▒▒▒▒▒▒▒▓░▒▒▒▒▒▒▒▓▓██████████████▓████▒▒▒▓▓▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒░░█▒░░▒▓▒░▓▓██▓█▓░░░▒▒▒▒▒▒▒░░▒▒▒▒▒▒█▓▓█▓▒░▓██████▓▓▓█▓▓███▒▓▒▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓█▓▓▓▓▓▒░░░▓░░░▒▓▒▓███▒▓▒░░░▒▒▒▒▒▒░░░▒▒▒▒▒▒▒░░▒█▒▒▓█░▓███▓▓▓█░░█▓▓▒▒▓▒▒▒\n" + //
                                "▓▓▓▓▓█▓▓▓▓█▓█▓▓▓░░░░▓░░░░▓▒▓▓▓▒▓░░░░▒▒▒▒▓░░░░▒▒▒▒▒▒░░░░▓▓▓▓█████▓▓▓█▒ ░█░▒▒▓▒▒▒▒\n" + //
                                "█▓▓▓▓▓▓▓▓▓▓▓█▓▓▓░░░░░░░░░░▓▓▓▓░░░░░░▒▒▒▒░░░░░░▒▒▒▒░░░░ ░▓▓▒▒▓▓▒▒░░ ░ ░▓░░▒▒▒▒▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▓▓█▓▓▓░░░░░░░░░▒▓▓▓▓░░░░░░▒▒░░░░░░░▒▒░░░░░░░░░ ▒▓▓▓▓▒░   ░░░░░▒▒▒▒▒▒▒\n" + //
                                "▓█▓▓▓▓▓▓▓█▓▓▓▓▓▓▓░░░▒▒░▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▒▒░░░░░░░░░░▒▒▒▒▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▒▒▒▒▒▒▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ ░░░▒▒▒▒▒▒▒▒\n" + //
                                "▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░▒▒▒▒▒▒▒▒▒▒▒▒░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒░░░░░░░░░░▒▒▒▒▒▒▒▒▒\n" + //
                                "▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░▒▒▒▒▒▒▒▒▒▒░░░░░░▒░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓\n" + //
                                "▒▓▓█▓▓▓▓▓▓█▓▓▓▓▓▓▓▓▓░░░▒▒▒▒▒▒▒░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▓▒▒▒▒▒▒▒▒▒▓▓▓▓▓\n" + //
                                "▒▓▓▓█▓▓▓▓▓█▓▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▓▓▓▓▓▓▓▓\n" + //
                                "▓▓▓▓▓█▓▓▓▓▓▓▓█▓▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▒▒░▓▓▓▓▓▓▓▓▓▓▓▓\n" + //
                                "▓▓▓▓▓▓█▓▓▓▓█▓█▓▓▓▓▓▒▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒▓▓▓▓▓▓▓▓▓▓▓▓▓▓\n" + //
                                "▓▓▓▓▓▓▓█▓▓▓▓▓█▓▓▓▓▒▒▒█▓▓▓▓▓▓▓▓▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒████▓▓▓▓▓▓▓▓▓▓▓▓▓\n" + //
                                "▓▓▓▓▓▓▓▓█▓▓▓▓▓▓▓▓▓▒▒▒▒▓▓▓█▓▓▓▓███▒░░░▒▒▒░░░░░░░░░░░░░░░░░▒▒▒▒▒██████▓▓▓▓▓▓▓▓▓▓▓▓\n" + //
                                "▓▓▓▓▓▓▓▓▓▓█▓▓▓█▓▓▓▒▒▒▒▓▓▓▓██████████▒░░░░░░░░░░░░░░░▒▒▓▒▒▒▒▒▒▒████████▓▓▓▓▓▓▓▓▓▓\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▓▓▓█▓▓▓▒▒▒▒▒▓▓▓█████████████▒░░░░░▒▒▒▓▓▒▒▒▒▒▒▒▒▒▒▒▒▓████████▓▓▓▓▓▓▓▓▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▓▓███▓▓▒▒▒▒▒▒▓▓██████████████████▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒█████████▓▓▓▓▓▓▓▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓▓████▓▓▒▒▒▒▒▒▓▓████████████▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒█████████▓▓▓▓▓▓▓▒▒▒\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓██████▓▓▒▒▒▒▒▒▓████████████▒▒▓▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒██████████▓▓▓▓▓▓▒▒▒▓\n" + //
                                "▓▓▓▓▓▓▓▓▓▓▓██████▓▓▒▒▒▒▒▒▒▓███████████▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒░░▓█████████▓▓▓█▓▓▒▒▒▒▒\n";

            terminal.writer().println(uwu);
        }));

        widgets.add(new ButtonWidget("clearTerminal", "Clear", () -> {
            try {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }));
    }
    
}
