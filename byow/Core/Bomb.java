package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

public class Bomb implements Serializable {
    private static final long TOTALTIME = 2500;
    private long timeLeft;
    private int posX;
    private int posY;
    private int owner;
    private TETile tile = Tileset.BOMB;

    Bomb(int x, int y, int o) {
        timeLeft = TOTALTIME;
        posX = x;
        posY = y;
        owner = o;
    }

    public boolean tick(long timePassed) {
        timeLeft -= timePassed;
        return timeLeft <= 0;
    }

    public int[] getPos() {
        return new int[] {posX, posY};
    }

    public void updatePos(int x, int y) {
        posX = x;
        posY = y;
    }

    public int getOwner() {
        return this.owner;
    }

    public void updateOwner(int o) {
        owner = o;
    }

    public TETile getTile() {
        return tile;
    }
}
