package byow.Core;


import edu.princeton.cs.introcs.StdDraw;

public class ByowTest {

    public static void main(String[] args) {
        //WorldGenerator wg = new WorldGenerator(4325, Engine.WIDTH, Engine.HEIGHT);
        //TETile[][] world = wg.generateWorld();
        //TERenderer ter = new TERenderer();
        //ter.initialize(Engine.WIDTH, Engine.HEIGHT);
        long a = System.nanoTime();
        StdDraw.pause(2000);
        System.out.println((System.nanoTime() - a) / 1_000_000_000);
    }
}
