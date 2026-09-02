package fr.hadrix.drawer;

import fr.hadrix.drawer.commands.DrawerCommand;
import fr.hadrix.drawer.listeners.DrawerBlockListener;
import fr.hadrix.drawer.listeners.DrawerInteractListener;
import fr.hadrix.drawer.manager.DrawerManager;
import fr.hadrix.drawer.utils.DrawerConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;

public final class Drawer extends JavaPlugin {

    private DrawerManager drawerManager;

    private void registerDrawerRecipe() {

        ItemStack drawerItem = new ItemStack(Material.OAK_PLANKS, 1);
        ItemMeta drawerMeta = drawerItem.getItemMeta();

        drawerMeta.setDisplayName(DrawerConstants.DEFAULT_ITEM_NAME);
        drawerMeta.setItemName(DrawerConstants.DEFAULT_ITEM_NAME);
        drawerMeta.setLore(Arrays.asList(DrawerConstants.EMPTY_DRAWER_TEXT, "", "Color : BLACK_STAINED_GLASS_PANE"));

        drawerItem.setItemMeta(drawerMeta);

        NamespacedKey key = new NamespacedKey(this, "custom_drawer");

        ShapedRecipe recipe = new ShapedRecipe(key, drawerItem);

        recipe.shape(
                "PPP",
                "PCP",
                "PPP"
        );

        recipe.setIngredient('P', new RecipeChoice.MaterialChoice(Tag.PLANKS));
        recipe.setIngredient('C', Material.CHEST);

        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onEnable() {

        this.drawerManager = new DrawerManager();
        this.drawerManager.loadDrawers();


        getCommand("drawer").setExecutor(new DrawerCommand(drawerManager));

        getServer().getPluginManager().registerEvents(new DrawerBlockListener(drawerManager), this);
        getServer().getPluginManager().registerEvents(new DrawerInteractListener(drawerManager), this);
        registerDrawerRecipe();
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
        File DrawerJsonPath = new File(FilePath, "DrawerList.json");
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
        if (this.drawerManager != null) {
            this.drawerManager.saveDrawers();
        }
    }
}
