package byow.Core;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Persistence {
    private static final Path CWD = Paths.get(System.getProperty("user.dir"));
    private static final Path SAVEFILE = Paths.get(CWD.toString(), "saveFile.txt");

    public static void saveWorld(World world) {
        Utils.writeObject(SAVEFILE.toFile(), world);
    }

    public static World readWorld() {
        try {
            return Utils.readObject(SAVEFILE.toFile(), World.class);
        } catch (IllegalArgumentException e) {
            System.exit(0);
        }
        return null;
    }


}
