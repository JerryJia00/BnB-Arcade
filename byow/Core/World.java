package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class World implements Serializable {
    private static final double TURN_PROB = 0.3;
    private final boolean DARK;
    private static final int VIEWSIZE = 9;
    private final Long seed;
    private final int worldWidth;
    private final int worldHeight;
    private final Random r;
    private final TETile[][] world;
    private final TETile[][] darkWorld;
    private Avatar player1;
    private Avatar player2;
    private static final Deque<Bomb> BOMBS = new ArrayDeque<>();
    private int winner = 0;
    private static final int BOMBRADIUS = 3;
    private static final int KICK = 5;
    private TERenderer ter;
    private int numRooms;

    public World(long s, int w, int h, TERenderer t, boolean d) {
        seed = s;
        DARK = d;
        worldWidth = w;
        worldHeight = h;
        world = new TETile[w][h];
        darkWorld = new TETile[w][h];
        numRooms = 0;
        r = new Random(seed);
        ter = t;
    }

    public TETile[][] getRenderWorld() {
        showBomb();
        if (DARK) {
            fillDarkWorld();
            return this.darkWorld;
        } else {
            return this.world;
        }
    }

    public TETile[][] getWorld() {
        return this.world;
    }

    public int getWinner() {
        return this.winner;
    }

    public void generateWorld() {
        boolean[] dir = {true, true, true, true};
        int roomWidth = r.nextInt(6) + 2;
        int roomHeight = r.nextInt(6) + 2;
        drawRoom(r.nextInt(worldWidth) / 10 + worldWidth / 5 * 3, r.nextInt(worldHeight) / 10
                + worldHeight / 5 * 3, dir, roomWidth, roomHeight, 4);
        replaceHallway();
        fillWorld();
    }

    private void fillWorld() {
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                if (world[i][j] == null) {
                    if (isOccupied(i - 1, j - 1, 3, 3, Tileset.FLOOR)) {
                        world[i][j] = Tileset.WALL;
                    } else {
                        world[i][j] = Tileset.NOTHING;
                    }
                }
            }
        }
    }

    private void replaceHallway() {
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                if (world[i][j] == Tileset.GRASS) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }

    private void drawRoom(int x, int y, boolean[] dir, int roomWidth, int roomHeight, int fromDir) {
        //System.out.println("Room");
        //System.out.println(x);
        //System.out.println(y);
        //System.out.println(roomWidth);
        //System.out.println(roomHeight);
        //System.out.println(fromDir);
        //System.out.println(debug());
        //System.out.println();
        switch (fromDir) {
            case 0:
                if (inBound(x - 1, y - 1, roomWidth + 2, roomHeight + 2)) {
                    if (!isOccupied(x - 2, y - 2, roomWidth + 2, roomHeight + 4, Tileset.FLOOR)
                            && !isOccupied(x - 2, y - 2, roomWidth + 2, roomHeight + 4,
                            Tileset.GRASS)) {
                        numRooms += 1;
                        for (int i = 0; i < roomHeight; i++) {
                            drawRow(x, y + i, roomWidth, Tileset.FLOOR);
                        }
                        drawHallwayInDir(x, y, dir, roomWidth, roomHeight, TURN_PROB);
                    }
                }
                break;
            case 1:
                if (inBound(x - 1, y - 1, roomWidth + 2, roomHeight + 2)) {
                    if (!isOccupied(x, y - 2, roomWidth + 2, roomHeight + 4, Tileset.FLOOR)
                            && !isOccupied(x, y - 2, roomWidth + 2, roomHeight + 4,
                            Tileset.GRASS)) {
                        numRooms += 1;
                        for (int i = 0; i < roomHeight; i++) {
                            drawRow(x, y + i, roomWidth, Tileset.FLOOR);
                        }
                        drawHallwayInDir(x, y, dir, roomWidth, roomHeight, TURN_PROB);
                    }
                }
                break;
            case 2:
                if (inBound(x - 1, y - 1, roomWidth + 2, roomHeight + 2)) {
                    if (!isOccupied(x - 2, y, roomWidth + 4, roomHeight + 2, Tileset.FLOOR)
                            && !isOccupied(x - 2, y, roomWidth + 4, roomHeight + 2,
                            Tileset.GRASS)) {
                        numRooms += 1;
                        for (int i = 0; i < roomHeight; i++) {
                            drawRow(x, y + i, roomWidth, Tileset.FLOOR);
                        }
                        drawHallwayInDir(x, y, dir, roomWidth, roomHeight, TURN_PROB);
                    }
                }
                break;
            case 3:
                if (inBound(x - 1, y - 1, roomWidth + 2, roomHeight + 2)) {
                    if (!isOccupied(x - 2, y - 2, roomWidth + 4, roomHeight + 2, Tileset.FLOOR)
                            && !isOccupied(x - 2, y - 2, roomWidth + 4, roomHeight + 2,
                            Tileset.GRASS)) {
                        numRooms += 1;
                        for (int i = 0; i < roomHeight; i++) {
                            drawRow(x, y + i, roomWidth, Tileset.FLOOR);
                        }
                        drawHallwayInDir(x, y, dir, roomWidth, roomHeight, TURN_PROB);
                    }
                }
                break;
            default:
                if (inBound(x - 1, y - 1, roomWidth + 2, roomHeight + 2)) {
                    if (!isOccupied(x - 2, y - 2, roomWidth + 4,
                            roomHeight + 4, Tileset.FLOOR)) {
                        numRooms += 1;
                        for (int i = 0; i < roomHeight; i++) {
                            drawRow(x, y + i, roomWidth, Tileset.FLOOR);
                        }
                        drawHallwayInDir(x, y, dir, roomWidth, roomHeight, TURN_PROB);
                    }
                }
        }
    }

    private void drawHallwayInDir(int x, int y, boolean[] dir,
                                  int roomWidth, int roomHeight, double prob) {
        if (dir[0]) {   // from the left
            int shift = r.nextInt(roomHeight);
            int length = r.nextInt(3) + 2;
            if (inBound(x - 1 - length, y - 1 + shift, length + 1, 3)) {
                if (!isOccupied(x - 2 - length, y - 2 + shift, length + 2, 5, Tileset.FLOOR)
                        && !isOccupied(x - 2 - length, y - 2 + shift, length + 2, 5,
                        Tileset.GRASS)) {
                    drawHallway(x - length, y + shift, length, 1, 0, prob);
                }
            }
        }
        if (dir[1]) {   // from the right
            int shift = r.nextInt(roomHeight);
            int length = r.nextInt(5) + 3;
            if (inBound(x + roomWidth, y - 1 + shift, length + 1, 3)) {
                if (!isOccupied(x + roomWidth, y - 2 + shift, length + 2, 5, Tileset.FLOOR)
                        && !isOccupied(x + roomWidth, y - 2 + shift, length + 2, 5,
                        Tileset.GRASS)) {
                    drawHallway(x + roomWidth, y + shift, length, 1, 1, prob);
                }
            }
        }
        if (dir[2]) {   // from the top
            int shift = r.nextInt(roomWidth);
            int length = r.nextInt(5) + 3;
            if (inBound(x - 1 + shift, y + roomHeight, 3, length + 1)) {
                if (!isOccupied(x - 2 + shift, y + roomHeight, 5, length + 2, Tileset.FLOOR)
                        && !isOccupied(x - 2 + shift, y + roomHeight, 5, length + 2,
                        Tileset.GRASS)) {
                    drawHallway(x + shift, y + roomHeight, 1, length, 2, prob);
                }
            }
        }
        if (dir[3]) {   // from the bottom
            int shift = r.nextInt(roomWidth);
            int length = r.nextInt(5) + 3;
            if (inBound(x - 1 + shift, y - 1 - length, 3, length + 1)) {
                if (!isOccupied(x - 2 + shift, y - 2 - length, 5, length + 2, Tileset.FLOOR)
                        && !isOccupied(x - 2 + shift, y - 2 - length, 5, length + 2,
                        Tileset.GRASS)) {
                    drawHallway(x + shift, y - length, 1, length, 3, prob);
                }
            }
        }
    }

    private boolean inBound(int x, int y, int width, int height) {
        return x >= 0 && y >= 0 && x + width < worldWidth - 1 && y + height < worldHeight - 1;
    }

    private boolean isOccupied(int x, int y, int width, int height, TETile tile) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                try {
                    TETile curTile = world[x + i][y + j];
                    if (curTile != null && curTile.equals(tile)) {
                        return true;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                    continue;
                }
            }
        }
        return false;
    }

    private void drawRow(int x, int y, int length, TETile tile) {
        for (int i = 0; i < length; i++) {
            world[x + i][y] = tile;
        }
    }

    private void drawHallway(int x, int y, int width, int height, int fromDir, double prob) {
        for (int i = 0; i < height; i++) {
            drawRow(x, y + i, width, Tileset.GRASS);
        }
        int endPoint = getEndPoint(x, y, width, height, fromDir);
        boolean[] dirs;
        if (RandomUtils.uniform(r) < prob) {
            if (fromDir == 0 || fromDir == 1) {
                dirs = new boolean[]{false, false, true, true};
                drawHallwayInDir(endPoint, y, dirs, 1, 1, 0);
            } else {
                dirs = new boolean[]{true, true, false, false};
                drawHallwayInDir(x, endPoint, dirs, 1, 1, 0);
            }
        } else {
            int roomWidth = r.nextInt(6) + 2;
            int roomHeight = r.nextInt(6) + 2;
            int shift;
            switch (fromDir) {
                case 0:
                    shift = r.nextInt(roomHeight);
                    dirs = new boolean[]{true, false, true, true};
                    drawRoom(endPoint - roomWidth, y - shift, dirs, roomWidth, roomHeight, 0);
                    break;
                case 1:
                    shift = r.nextInt(roomHeight);
                    dirs = new boolean[]{false, true, true, true};
                    drawRoom(endPoint, y - shift, dirs, roomWidth, roomHeight, 1);
                    break;
                case 2:
                    shift = r.nextInt(roomWidth);
                    dirs = new boolean[]{true, true, true, false};
                    drawRoom(x - shift, endPoint, dirs, roomWidth, roomHeight, 2);
                    break;
                case 3:
                    shift = r.nextInt(roomWidth);
                    dirs = new boolean[]{true, true, false, true};
                    drawRoom(x - shift, endPoint - roomHeight, dirs, roomWidth, roomHeight, 3);
                    break;
                default:
                    break;
            }
        }
    }

    public void findStartPos() {
        boolean stop = false;
        for (int x = 0; x < worldWidth; x++) {
            if (stop) {
                break;
            }
            for (int y = 0; y < worldHeight; y++) {
                if (world[x][y].description().equals("floor")) {
                    player1 = new Avatar(x, y, Tileset.AVATAR);
                    world[x][y] = player1.getTile();
                    stop = true;
                    break;
                }
            }
        }
        stop = false;
        for (int x = worldWidth - 1; x >= 0; x--) {
            if (stop) {
                break;
            }
            for (int y = worldHeight - 1; y >= 0; y--) {
                if (world[x][y].description().equals("floor")) {
                    player2 = new Avatar(x, y, Tileset.AVATAR2);
                    world[x][y] = player2.getTile();
                    stop = true;
                    break;
                }
            }
        }
    }

    public void move(String dir, int player) {
        Avatar curPlayer = getPlayer(player);
        int x = curPlayer.getPosX();
        int y = curPlayer.getPosY();
        curPlayer.updateFacing(dir);
        switch (dir) {
            case "W":
                if (world[x][y + 1].description().equals("floor")) {
                    world[x][y + 1] = curPlayer.getTile();
                    world[x][y] = Tileset.FLOOR;
                    curPlayer.updatePos(x, y + 1);
                } else if (world[x][y + 1].description().equals("bomb")) {
                    moveBomb(x, y + 1, "W", player);
                }
                break;
            case "S":
                if (world[x][y - 1].description().equals("floor")) {
                    world[x][y - 1] = curPlayer.getTile();
                    world[x][y] = Tileset.FLOOR;
                    curPlayer.updatePos(x, y - 1);
                } else if (world[x][y - 1].description().equals("bomb")) {
                    moveBomb(x, y - 1, "S", player);
                }
                break;
            case "A":
                if (world[x - 1][y].description().equals("floor")) {
                    world[x - 1][y] = curPlayer.getTile();
                    world[x][y] = Tileset.FLOOR;
                    curPlayer.updatePos(x - 1, y);
                } else if (world[x - 1][y].description().equals("bomb")) {
                    moveBomb(x - 1, y, "A", player);
                }
                break;
            case "D":
                if (world[x + 1][y].description().equals("floor")) {
                    world[x + 1][y] = curPlayer.getTile();
                    world[x][y] = Tileset.FLOOR;
                    curPlayer.updatePos(x + 1, y);
                } else if (world[x + 1][y].description().equals("bomb")) {
                    moveBomb(x + 1, y, "D", player);
                }
                break;
            default:
                break;
        }
    }
    
    private Avatar getPlayer(int player) {
        if (player == 1) {
            return player1;
        } else {
            return player2;
        }
    }

    private void moveBomb(int x, int y, String dir, int player) {
        Bomb b = getBomb(x, y);
        b.updateOwner(player);
        int endX = x;
        int endY = y;
        boolean bombStop = false;
        for (int i = 1; i <= KICK; i++) {
            if (bombStop) {
                break;
            }
            switch (dir) {
                case "W":
                    if (bombMeetObstacle(x, y + i)) {
                        bombStop = true;
                    } else {
                        animateBombMove(x, y + i - 1, x, y + i, b);
                        endY += 1;
                    }
                    break;
                case "S":
                    if (bombMeetObstacle(x, y - i)) {
                        bombStop = true;
                    } else {
                        animateBombMove(x, y - i + 1, x, y - i, b);
                        endY -= 1;
                    }
                    break;
                case "A":
                    if (bombMeetObstacle(x - i, y)) {
                        bombStop = true;
                    } else {
                        animateBombMove(x - i + 1, y, x - i, y, b);
                        endX -= 1;
                    }
                    break;
                case "D":
                    if (bombMeetObstacle(x + i, y)) {
                        bombStop = true;
                    } else {
                        animateBombMove(x + i - 1, y, x + i, y, b);
                        endX += 1;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void animateBombMove(int x, int y, int newX, int newY, Bomb b) {
        world[newX][newY] = Tileset.BOMB;
        world[x][y] = Tileset.FLOOR;
        b.updatePos(newX, newY);
        ter.renderFrame(getRenderWorld());
        StdDraw.pause(3);
    }

    private Bomb getBomb(int x, int y) {
        for (Bomb b : BOMBS) {
            if (b.getPos()[0] == x && b.getPos()[1] == y) {
                return b;
            }
        }
        return null;
    }

    private boolean bombMeetObstacle(int x, int y) {
        return isOccupied(x, y, 1, 1, Tileset.WALL)
                || isOccupied(x, y, 1, 1, player1.getTile())
                || isOccupied(x, y, 1, 1, player2.getTile())
                || isOccupied(x, y, 1, 1, Tileset.BOMB);
    }

    public void tick(long timePassed) {
        int count = 0;
        for (Bomb b : BOMBS) {
            if (b.tick(timePassed)) {
                count += 1;
                boom(b.getPos(), b.getOwner());
            }
        }
        for (int i = 0; i < count; i++) {
            BOMBS.removeFirst();
        }
    }

    private void boom(int[] pos, int owner) {
        int x = pos[0];
        int y = pos[1];
        boolean p1Touched = false;
        boolean p2Touched = false;
        for (int i = x; i < worldWidth; i++) {
            if (isOccupied(i, y, 1, 1, Tileset.WALL)) {
                break;
            }
            p1Touched = p1Touched || isOccupied(i, y, 1, 1, player1.getTile());
            p2Touched = p2Touched || isOccupied(i, y, 1, 1, player2.getTile());
        }
        for (int j = y; j < worldHeight; j++) {
            if (isOccupied(x, j, 1, 1, Tileset.WALL)) {
                break;
            }
            p1Touched = p1Touched || isOccupied(x, j, 1, 1, player1.getTile());
            p2Touched = p2Touched || isOccupied(x, j, 1, 1, player2.getTile());
        }
        for (int i = x; i >= 0; i--) {
            if (isOccupied(i, y, 1, 1, Tileset.WALL)) {
                break;
            }
            p1Touched = p1Touched || isOccupied(i, y, 1, 1, player1.getTile());
            p2Touched = p2Touched || isOccupied(i, y, 1, 1, player2.getTile());
        }
        for (int j = y; j >= 0; j--) {
            if (isOccupied(x, j, 1, 1, Tileset.WALL)) {
                break;
            }
            p1Touched = p1Touched || isOccupied(x, j, 1, 1, player1.getTile());
            p2Touched = p2Touched || isOccupied(x, j, 1, 1, player2.getTile());
        }
        for (int j = 0; j < BOMBRADIUS; j++) {
            for (int i = 1; i < 1 + j * 2; i++) {
                p1Touched = p1Touched || isOccupied(x - j + i, y + j, 1, 1, player1.getTile());
                p2Touched = p2Touched || isOccupied(x - j + i, y + j, 1, 1, player2.getTile());
            }
        }
        for (int j = 0; j < BOMBRADIUS; j++) {
            for (int i = 1; i < 1 + j * 2; i++) {
                p1Touched = p1Touched || isOccupied(x - j + i, y - j, 1, 1, player1.getTile());
                p2Touched = p2Touched || isOccupied(x - j + i, y - j, 1, 1, player2.getTile());
            }
        }
        for (int i = -BOMBRADIUS + 1; i <= BOMBRADIUS; i++) {
            p1Touched = p1Touched || isOccupied(x + i, y, 1, 1, player1.getTile());
            p2Touched = p2Touched || isOccupied(x + i, y, 1, 1, player2.getTile());
        }
        world[x][y] = Tileset.FLOOR;
        calculateDamage(p1Touched, p2Touched, owner);
    }

    private void calculateDamage(boolean p1Touched, boolean p2Touched, int owner) {
        if (p1Touched) {
            player1.updateHp(-1);
        }
        if (p2Touched) {
            player2.updateHp(-1);
        }
        if (player1.dead() && player2.dead()) {
            winner = 3 - owner;
        } else if (player1.dead()) {
            winner = 2;
        } else if (player2.dead()) {
            winner = 1;
        }
    }

    public String getHpInfo() {
        return String.format("Player1: %d/%d   Player2: %d/%d", player1.getHp(), Avatar.MAXHP,
                player2.getHp(), Avatar.MAXHP);
    }
    
    private void showBomb() {
        for (Bomb b : BOMBS) {
            int x = b.getPos()[0];
            int y = b.getPos()[1];
            if (world[x][y] == Tileset.FLOOR) {
                world[x][y] = b.getTile();
            }
        }
    }

    public void removeBomb() {
        for (Bomb b : BOMBS) {
            int x = b.getPos()[0];
            int y = b.getPos()[1];
            if (world[x][y] == Tileset.BOMB) {
                world[x][y] = Tileset.FLOOR;
            }
        }
    }

    public void placeBomb(int player) {
        Avatar curPlayer = getPlayer(player);
        int x = curPlayer.getPosX();
        int y = curPlayer.getPosY();
        /** switch (curPlayer.getFacing()) {
            case "W":
                y += 1;
                break;
            case "S":
                y -= 1;
                break;
            case "A":
                x -= 1;
                break;
            case "D":
                x += 1;
                break;
        } */
        Bomb b = new Bomb(x, y, player);
        BOMBS.addLast(b);
    }

    private void fillDarkWorld() {
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                darkWorld[i][j] = Tileset.NOTHING;
            }
        }
        fillWorldForPlayer(1);
        fillWorldForPlayer(2);
    }

    private void fillWorldForPlayer(int player) {
        Avatar curPlayer = getPlayer(player);
        int x = curPlayer.getPosX();
        int y = curPlayer.getPosY();
        fillTriangleInDir(x, y + VIEWSIZE, 1);
        fillTriangleInDir(x, y - VIEWSIZE, -1);
        for (int i = -VIEWSIZE + 1; i <= VIEWSIZE; i++) {
            try {
                darkWorld[x + i][y] = world[x + i][y];
            } catch (IndexOutOfBoundsException ignored) {
                continue;
            }
        }
    }

    private void fillTriangleInDir(int x, int y, int dir) {
        for (int j = 0; j < VIEWSIZE; j++) {
            for (int i = 1; i < 1 + j * 2; i++) {
                try {
                    darkWorld[x - j + i][y - dir * j] = world[x - j + i][y - dir * j];
                } catch (IndexOutOfBoundsException ignored) {
                    continue;
                }
            }
        }
    }

    private int getEndPoint(int x, int y, int width, int height, int dir) {
        switch (dir) {
            case 0:
                return x;
            case 1:
                return x + width - 1;
            case 2:
                return y + height - 1;
            case 3:
                return y;
            default:
                return 0;
        }
    }

    public String debug() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < worldHeight; j++) {
            for (int i = 0; i < worldWidth; i++) {
                try {
                    sb.append(world[i][worldHeight - j - 1].description().charAt(0));
                } catch (NullPointerException ignored) {
                    sb.append("e");
                }
            }
            sb.append("\n");
        }
        sb.append(BOMBS.toString());
        return sb.toString();
    }
}
