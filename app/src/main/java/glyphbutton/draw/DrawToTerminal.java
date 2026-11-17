package glyphbutton.draw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import glyphbutton.Focus;
import glyphbutton.draw.WindowPlacement.PlacementResult;
import glyphbutton.panel.overlay.Overlay;
import glyphbutton.panel.overlay.OverlayPosition;
import glyphbutton.panel.window.Window;

public class DrawToTerminal {

    private final Terminal terminal;
    private final LineReader reader;
    private List<Window> windows;
    private List<Overlay> overlays;
    private Focus focus;
    private final panelBuilder pb;
    private final WindowPlacement wp;
    private final WorkSpaceBuilder wsb;
    private int margin;
    private int focusedWindow;
    private int focusedWidget;
    private boolean optimizeSpace = false;
    private int maxWidth;
    private int maxHeight;
    

    public DrawToTerminal(Terminal terminal, LineReader reader) {

        this.terminal = terminal;
        this.reader = reader;
        pb = new panelBuilder();
        wp = new WindowPlacement();
        wsb = new WorkSpaceBuilder();
    }

    public void draw(List<Window> windows, List<Overlay> overlays, Focus focus, int focusedWindow, int focusedWidget, boolean optimizeSpace, int margin) {
        this.focus = focus;
        this.focusedWindow = focusedWindow;
        this.focusedWidget = focusedWidget;
        this.optimizeSpace = optimizeSpace;
        this.margin = margin;
        this.windows = windows;
        this.overlays = overlays;

        clearTerminal();
        drawWorkSpace();
        drawHistory();
        
        terminal.flush();
    }

    private void drawWorkSpace() {

        int windowAmount = windows.size();

        String[][] builtWindows = new String[windowAmount][];

        int[] widths = new int[windowAmount];
        int[] height = new int[windowAmount];
        for (int i = 0; i < windowAmount; i++) {
            builtWindows[i] = pb.buildPanel(windows.get(i).getWidth(), windows.get(i).getHeight(), 
            windows.get(i).getBorderStyle(), windows.get(i).getWidgets(), (focusedWindow == i), focusedWidget);
            widths[i] = windows.get(i).getWidth() + 2;
            height[i] = windows.get(i).getHeight() + 2;
        }

        maxWidth = terminal.getSize().getColumns() - 5;
        maxHeight = terminal.getSize().getRows() - 10;

        PlacementResult pr = wp.place(widths, height, maxWidth, maxHeight, margin, optimizeSpace);

        ArrayList<Integer> x = new ArrayList<Integer>();
        ArrayList<Integer> y = new ArrayList<Integer>();
        
        // not positive that this will work :(   update: it worked :)
        ArrayList<String[]> s = new ArrayList<>();

        for (int i = 0; i < pr.x().length; i++) {

            if (pr.x()[i] != null) {
                x.add(pr.x()[i].intValue());
                y.add(pr.y()[i].intValue());
                s.add(builtWindows[i]);
            } else {
                // window did not have space
            }
        }

        int[] xx = x.stream().mapToInt(Integer::intValue).toArray();
        int[] yy = y.stream().mapToInt(Integer::intValue).toArray();

        String[][] ss = new String[s.size()][];
        for (int i = 0; i < s.size(); i++) {
            ss[i] = s.get(i);
        }

        ArrayList<Integer> ox = new ArrayList<Integer>();
        ArrayList<Integer> oy = new ArrayList<Integer>();
        
        // not positive that this will work :(   update: it worked :)
        ArrayList<String[]> os = new ArrayList<>();

        // Overlay building
        for (Overlay i : overlays) {
            int[] coordinates = overlayPosition(i);
            ox.add(coordinates[0]);
            oy.add(coordinates[1]);
            os.add(pb.buildPanel(i.getWidth(), i.getHeight(), i.getBorderStyle(),
                i.getWidgets(), true, focusedWidget));
        }

        String[][] oss = new String[os.size()][];
        for (int i = 0; i < os.size(); i++) {
            oss[i] = s.get(i);
        }
        OverlayRecord or = new OverlayRecord(ox.stream().mapToInt(Integer::intValue).toArray(),
        oy.stream().mapToInt(Integer::intValue).toArray(), oss);

        //
        String workSpace = wsb.buildWorkSpace(ss, xx, yy, maxWidth, maxHeight, 4, or);

        reader.printAbove(workSpace);
        terminal.flush();
    }

    private int[] overlayPosition(Overlay i) {

        switch (i.getPosition()) {
            case CENTER:
                int[] pos = {(maxWidth / 2) - (i.getWidth() / 2) - 1, (maxHeight / 2) - (i.getHeight() / 2) - 1};
                return pos;
        
            default:
                throw new IllegalArgumentException("Invalid overlay position");
        }

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
