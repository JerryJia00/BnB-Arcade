package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class KeyboardInputSource implements InputSource {
    private boolean printKeys = false;
    private String seed = "";
    private boolean dark = false;
    private boolean worldBuilt = false;

    public KeyboardInputSource() {
        StdDraw.enableDoubleBuffering();
        drawMainMenu();
    }

    private void drawMainMenu() {
        StdDraw.clear(Color.black);
        StdDraw.setXscale();
        StdDraw.setYscale();
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 24));
        StdDraw.text(0.5, 0.8, "New World (N)");
        StdDraw.text(0.5, 0.6, "Load (L)");
        StdDraw.text(0.5, 0.4, "Quit (Q)");
        if (dark) {
            StdDraw.setPenColor(Color.RED);
        }
        StdDraw.text(0.5, 0.2, "Dark (D)");
        StdDraw.show();
    }

    public char getNextInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (printKeys) {
                    if (c > 47 && c < 58) {
                        seed += c;
                    }
                    StdDraw.clear(Color.black);
                    StdDraw.text(0.5, 0.7, "Please enter a random seed :");
                    StdDraw.text(0.5, 0.5, seed);
                    StdDraw.text(0.5, 0.3, "Press S when you are done.");
                    StdDraw.show();
                }
                if (c == 'N') {
                    StdDraw.clear(Color.black);
                    StdDraw.setPenColor(Color.white);
                    StdDraw.text(0.5, 0.7, "Please enter a random seed :");
                    printKeys = true;
                    StdDraw.show();
                } else if (c == 'S' && printKeys) {
                    printKeys = false;
                    worldBuilt = true;
                } else if (c == 'D' && !printKeys && !worldBuilt) {
                    dark = !dark;
                    drawMainMenu();
                } else if (c == 'L' && !printKeys && !worldBuilt) {
                    worldBuilt = true;
                }
                return c;
            }
        }
    }

    public boolean hasNextInput() {
        return true;
    }

}
