package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;

public class Avatar implements Serializable {
    private int posX;
    private int posY;
    private TETile tile;
    public static final int MAXHP = 3;
    private int hp;
    private String facing = "W";

    Avatar(int x, int y, TETile t) {
        posX = x;
        posY = y;
        tile = t;
        hp = MAXHP;
    }

    public void updatePos(int x, int y) {
        posX = x;
        posY = y;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public TETile getTile() {
        return tile;
    }

    public int getHp() {
        return hp;
    }

    public void updateHp(int amount) {
        hp += amount;
    }

    public boolean dead() {
        return hp <= 0;
    }

    public String getFacing() {
        return facing;
    }

    public void updateFacing(String f) {
        facing = f;
    }
}
