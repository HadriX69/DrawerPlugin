package fr.hadrix.drawer;

import com.google.gson.Gson;
import fr.hadrix.drawer.commands.GiveDrawer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public final class Drawer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("givedrawer").setExecutor(new GiveDrawer());
        getServer().getPluginManager().registerEvents((Listener) new GiveDrawer(), this);
        GiveDrawer.LoadDrawer();
    }

    @Override
    public void onLoad()
    {
        String FilePath = getDataFolder().getPath();
        File DrawerList = new File(FilePath);
        //check if Drawer File exist
        if(!DrawerList.exists())
        {
            try {
                DrawerList.mkdir();
                getLogger().info("-- Drawer Folder has been Created !");
            }catch (Exception e) {
                getLogger().severe("Error : " + e.toString());
                //System.err.println(e);
            }
        }
        //check if DrawerList.json exist
        File DrawerJsonPath = new File(FilePath + "/DrawerList.json");
        if (!DrawerJsonPath.exists())
        {
                try{
                    File DrawerJson = new File(FilePath + "/DrawerList.json");
                    if(DrawerJson.createNewFile()) {
                        getLogger().info("-- DrawerList.json has been Created !");
                    }
                } catch (Exception e) {
                    getLogger().severe("Error : " + e.toString());
                    //System.err.println(e);
                }
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        GiveDrawer.SaveDrawer();
    }
}
