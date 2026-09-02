package fr.hadrix.drawer.commands;

import fr.hadrix.drawer.manager.DrawerManager;
import fr.hadrix.drawer.utils.DrawerConstants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class DrawerCommand implements CommandExecutor {
    private final DrawerManager manager;

    public DrawerCommand(DrawerManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(cmd.getName().equals("drawer") && args.length == 0)
            {
                player.sendMessage("[Drawer Help]");
                player.sendMessage("/drawer list : Returns the number of drawers existing on the server");
                player.sendMessage("/drawer give <player_name> : Give a drawer");
                return true;
            }
            else if (args.length > 0)
            {
                if(args[0].equalsIgnoreCase("list"))
                {
                    player.sendMessage("§e There is " + manager.getDrawers().size() + "§e on the server !");
                    return true;
                }
                if(args[0].equalsIgnoreCase("give"))
                {
                    if(args.length == 1)
                    {
                        ItemStack Drawer = new ItemStack(Material.OAK_PLANKS, 1);
                        ItemMeta DrawerMeta = Drawer.getItemMeta();

                        DrawerMeta.setDisplayName(DrawerConstants.DEFAULT_ITEM_NAME);
                        DrawerMeta.setItemName(DrawerConstants.DEFAULT_ITEM_NAME);
                        DrawerMeta.setLore(Arrays.asList(DrawerConstants.EMPTY_DRAWER_TEXT,"","Color : BLACK_STAINED_GLASS_PANE"));
                        Drawer.setItemMeta(DrawerMeta);

                        //drawer settings
                        player.getInventory().addItem(Drawer);

                        return true;
                    }
                    else if (args.length == 2)
                    {
                        if(Bukkit.getPlayer(args[1]) != null)
                        {
                            Player giveplayer = Bukkit.getPlayerExact(args[1]);
                            ItemStack Drawer = new ItemStack(Material.OAK_PLANKS, 1);
                            ItemMeta DrawerMeta = Drawer.getItemMeta();

                            DrawerMeta.setDisplayName("Drawer");
                            DrawerMeta.setItemName("Drawer");
                            DrawerMeta.setLore(Arrays.asList("Empty Drawer","","Color : BLACK_STAINED_GLASS_PANE"));
                            Drawer.setItemMeta(DrawerMeta);

                            //drawer settings
                            giveplayer.getInventory().addItem(Drawer);
                            return true;
                        } else
                        {
                            player.sendMessage("§2 The player does not exist or is not connected");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}