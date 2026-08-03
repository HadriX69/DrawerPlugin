package fr.hadrix.drawer;

import fr.hadrix.drawer.commands.GiveDrawer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;

public final class Drawer extends JavaPlugin {

    private void registerDrawerRecipe() {

        ItemStack drawerItem = new ItemStack(Material.OAK_PLANKS, 1);
        ItemMeta drawerMeta = drawerItem.getItemMeta();

        drawerMeta.setDisplayName("Drawer");
        drawerMeta.setItemName("Drawer");
        drawerMeta.setLore(Arrays.asList("Empty Drawer", "", "Color : BLACK_STAINED_GLASS_PANE"));

        drawerItem.setItemMeta(drawerMeta);

        // 2. Créer une clé unique pour enregistrer cette recette dans le serveur
        NamespacedKey key = new NamespacedKey(this, "custom_drawer");

        // 3. Créer la recette (ShapedRecipe permet de définir une forme précise dans la table de craft)
        ShapedRecipe recipe = new ShapedRecipe(key, drawerItem);

        // 4. Dessiner la forme dans la grille 3x3
        // P = Planches (Planks), C = Coffre (Chest)
        recipe.shape(
                "PPP",
                "PCP",
                "PPP"
        );

        // 5. Assigner les matériaux aux lettres
        recipe.setIngredient('P', Material.OAK_PLANKS);
        recipe.setIngredient('C', Material.CHEST);

        // 6. Ajouter la recette au serveur
        Bukkit.addRecipe(recipe);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("drawer").setExecutor(new GiveDrawer());
        getServer().getPluginManager().registerEvents((Listener) new GiveDrawer(), this);
        GiveDrawer.LoadDrawer();
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
