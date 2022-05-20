package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 45;
    private static final TERenderer TER = new TERenderer();
    private World w;
    private boolean worldBuilt = false;
    private int mouseX;
    private int mouseY;
    private boolean prevColon = false;
    private boolean draw = false;
    private boolean dark = false;
    private long lastTime = System.currentTimeMillis();
    private String lastDescription = "";

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        KeyboardInputSource kip = new KeyboardInputSource();
        draw = true;
        interactWithSource(kip);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        StringInputSource sip = new StringInputSource(input);
        return interactWithSource(sip);
    }

    private TETile[][] interactWithSource(InputSource is) {
        while (is.hasNextInput()) {
            if (worldBuilt && checkGameOver()) {
                StdDraw.pause(1000000);
                System.exit(0);
            }
            if (worldBuilt && draw) {
                while (true) {
                    tick();
                    StdDraw.pause(1);
                    showMousePos();
                    if (StdDraw.hasNextKeyTyped()) {
                        break;
                    }
                }
            }
            char cur = is.getNextInput();
            if (prevColon) {
                if (cur == 'Q') {
                    w.removeBomb();
                    Persistence.saveWorld(w);
                    if (draw) {
                        System.exit(0);
                    }
                }
            }
            if (!worldBuilt) {
                interactBeforeWorldBuilt(is, cur);
            } else {
                interactAfterWorldBuilt(is, cur);
            }
        }
        return w.getWorld();
    }

    private void interactBeforeWorldBuilt(InputSource is, char cur) {
        switch (cur) {
            case 'N':
                Long seed = 0L;
                while (is.hasNextInput()) {
                    cur = is.getNextInput();
                    if (cur == 'S') {
                        this.w = new World(seed, WIDTH, HEIGHT, TER, dark);
                        w.generateWorld();
                        w.findStartPos();
                        if (draw) {
                            TER.initialize(Engine.WIDTH, Engine.HEIGHT + 10, 0, 10);
                            renderFrame();
                        }
                        worldBuilt = true;
                        break;
                    } else if (cur > 47 && cur < 58) {
                        seed = seed * 10 + cur - 48;
                    }
                }
                break;
            case 'L':
                w = Persistence.readWorld();
                worldBuilt = true;
                if (draw) {
                    TER.initialize(Engine.WIDTH, Engine.HEIGHT + 10, 0, 10);
                    renderFrame();
                }
                lastTime = System.currentTimeMillis();
                break;
            case 'Q':
                if (!worldBuilt) {
                    System.exit(0);
                }
                break;
            case 'D':
                dark = !dark;
                break;
            default:
                break;
        }
    }

    private void interactAfterWorldBuilt(InputSource is, char cur) {
        switch (cur) {
            case ':':
                prevColon = true;
                break;
            case 'W':
                movePlayer("W", 1);
                break;
            case 'S':
                movePlayer("S", 1);
                break;
            case 'A':
                movePlayer("A", 1);
                break;
            case 'D':
                movePlayer("D", 1);
                break;
            case 'I':
                movePlayer("W", 2);
                break;
            case 'K':
                movePlayer("S", 2);
                break;
            case 'J':
                movePlayer("A", 2);
                break;
            case 'L':
                movePlayer("D", 2);
                break;
            case 'E':
                w.placeBomb(1);
                break;
            case 'O':
                w.placeBomb(2);
                break;
            default:
                break;
        }
        showHud();
    }
    
    private void movePlayer(String dir, int player) {
        w.move(dir, player);
        renderFrame();
    }

    private void showHud() {
        if (draw) {
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(50, 5, w.getHpInfo());
            StdDraw.text(25, 5, lastDescription);
            StdDraw.show();
        }
    }

    private void showMousePos() {
        if (0 <= (int) StdDraw.mouseX() && WIDTH >= (int) StdDraw.mouseX()
                && 0 <= (int) StdDraw.mouseY() && HEIGHT >= (int) StdDraw.mouseY()) {
            if (!samePlace()) {
                StdDraw.clear();
                renderFrame();
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(50, 5, w.getHpInfo());
                try {
                    lastDescription = w.getRenderWorld()[(int) StdDraw.mouseX()]
                            [(int) StdDraw.mouseY() - 10].description();
                } catch (IndexOutOfBoundsException ignored) {
                    lastDescription = "";
                }
                StdDraw.text(25, 5, lastDescription);
                mouseX = (int) StdDraw.mouseX();
                mouseY = (int) StdDraw.mouseY();
                StdDraw.show();
            }
        }
    }

    private boolean samePlace() {
        return ((int) StdDraw.mouseX() == mouseX)
                && ((int) StdDraw.mouseY() == mouseY);
    }

    private void renderFrame() {
        if (draw) {
            TER.renderFrame(w.getRenderWorld());
        }
    }

    private boolean checkGameOver() {
        if (w.getWinner() != 0) {
            StdDraw.clear();
            StdDraw.setXscale();
            StdDraw.setYscale();
            StdDraw.setPenColor(Color.black);
            StdDraw.setFont(new Font("Arial", Font.BOLD, 40));
            StdDraw.text(0.5, 0.7, "Player" + w.getWinner() + " wins!");
            StdDraw.text(0.5, 0.4, "WINNER WINNER CHICKEN DINNER!");
            StdDraw.show();
            return true;
        }
        return false;
    }

    private void tick() {
        long elapsedTime = System.currentTimeMillis() - lastTime;
        System.out.println(elapsedTime);
        lastTime = System.currentTimeMillis();
        w.tick(elapsedTime);
    }

    @Override
    public String toString() {
        return w.debug();
    }
}
