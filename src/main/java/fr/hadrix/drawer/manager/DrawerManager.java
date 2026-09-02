package fr.hadrix.drawer.manager;

import fr.hadrix.drawer.Drawer;
import fr.hadrix.drawer.DrawerBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class DrawerManager {
    private static final List<DrawerBlock> DrawerList = new ArrayList<>();
    private final Map<Player, DrawerBlock> openedDrawers = new HashMap<>();

    // Path
    private static final Drawer main = JavaPlugin.getPlugin(Drawer.class);
    private static final String FilePath = main.getDataFolder().getPath() + "/DrawerList.json";

    private static final ObjectMapper Mapper = new ObjectMapper();
    private static final ObjectWriter Writer = new ObjectMapper().writerWithDefaultPrettyPrinter();

    public List<DrawerBlock> getDrawers() {
        return DrawerList;
    }

    public Map<Player, DrawerBlock> getOpenedDrawers() {
        return openedDrawers;
    }

    public void addDrawer(DrawerBlock drawer) {
        DrawerList.add(drawer);
    }

    public void removeDrawer(DrawerBlock drawer) {
        DrawerList.remove(drawer);
    }

    public static void loadDrawers() {
        File DrawerJson = new File(FilePath);

        if(!DrawerJson.exists() || DrawerJson.length() == 0)
        {
            main.getLogger().info("Loading Drawer Info : Aucun fichier DrawerList.json valide trouvé (premier démarrage ou fichier vide)");
            return;
        }

        try {
            List<DrawerBlock> loadedList = Mapper.readValue(Path.of(FilePath), new TypeReference<List<DrawerBlock>>() {
            });

            DrawerList.clear();
            if (loadedList != null) {
                DrawerList.addAll(loadedList);
            }

            main.getLogger().info("-- " + DrawerList.size() + " Drawer(s) chargé(s) en mémoire !");

        } catch (Exception e) {
            main.getLogger().severe("Loading Drawer Error : " + e);
        }
    }

    public static void saveDrawers() {
        try {
            Writer.writeValue(Path.of(FilePath), DrawerList);
            main.getLogger().info("Saving Drawer(s)");
        } catch (Exception e) {
            main.getLogger().severe("Saving Drawer Error : " + e);
        }
    }
}