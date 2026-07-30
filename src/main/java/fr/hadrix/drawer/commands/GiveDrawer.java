package fr.hadrix.drawer.commands;

import fr.hadrix.drawer.Drawer;
import fr.hadrix.drawer.DrawerBlock;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import java.io.File;
import java.nio.file.Path;
import java.util.*;


public class GiveDrawer implements CommandExecutor, Listener {

    static List<DrawerBlock> DrawerList = new ArrayList<DrawerBlock>();

    Map<Player, DrawerBlock> openedDrawers = new HashMap<>();

    static ObjectWriter Writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
    static ObjectMapper Mapper = new ObjectMapper();

    static Drawer main = JavaPlugin.getPlugin(Drawer.class);
    static String FilePath = main.getDataFolder().getPath() + "/DrawerList.json";

    private final Material[] BACKGROUND_COLORS = {
            Material.BLACK_STAINED_GLASS_PANE,
            Material.WHITE_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE
    };

    public void ReloadColorButton(Inventory inv, ItemStack Bg, int Color) {
        Bg.setType(BACKGROUND_COLORS[Color]);

        for (int i = 0; i <= 35; i++) {
            if(i != 0 && i != 13 && i != 28 && i != 31 && i != 34)
            {
                inv.setItem(i, Bg);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {


        if (sender instanceof Player) {
            Player player = (Player) sender;

            ItemStack Drawer = new ItemStack(Material.OAK_PLANKS, 1);
            ItemMeta DrawerMeta = Drawer.getItemMeta();

            DrawerMeta.setDisplayName("Drawer");
            DrawerMeta.setItemName("Drawer");
            DrawerMeta.setLore(Arrays.asList("Empty Drawer","","Color : BLACK_STAINED_GLASS_PANE"));
            Drawer.setItemMeta(DrawerMeta);

            //drawer settings
            player.getInventory().addItem(Drawer);

            return true;
        }
        return false;
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockPose = event.getBlock();
        boolean HasSave = false;
        int DrawerColor = 0;
        String DrawerType = "Empty Drawer";
        String DrawerQuantity = "0";
        int DrawerQuantityInt = 0;
        ItemStack itemHand = event.getItemInHand();

        if (itemHand.getType() == Material.OAK_PLANKS) {
            if (itemHand.hasItemMeta() && itemHand.getItemMeta().hasDisplayName()) {
                if (itemHand.getItemMeta().getDisplayName().equals("Drawer") &&
                        itemHand.getItemMeta().getItemName().equals("Drawer"))
                {
                    if(itemHand.getItemMeta().hasLore())
                    {
                        List<String> DrawerLore = itemHand.getItemMeta().getLore();
                        if(DrawerLore.size() == 3) {
                            if (!DrawerLore.get(0).equals("Empty Drawer")) {
                                for (int i = 0; i < BACKGROUND_COLORS.length; i++) {
                                    if (DrawerLore.get(2).equals("Color : " + BACKGROUND_COLORS[i])) {
                                        HasSave = true;
                                        DrawerColor = i;
                                        DrawerType = DrawerLore.get(0).replace("Type : ", "");
                                        DrawerQuantity = DrawerLore.get(1).replace("Quantity : ", "");
                                        DrawerQuantityInt = Integer.parseInt(DrawerQuantity);

                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if(HasSave)
                    {
                        DrawerBlock New_Drawer = new DrawerBlock();
                        New_Drawer.LocationX = blockPose.getX();
                        New_Drawer.LocationY = blockPose.getY();
                        New_Drawer.LocationZ = blockPose.getZ();

                        New_Drawer.maxItem = 2048;
                        New_Drawer.WorldName = blockPose.getWorld().getName();
                        New_Drawer.Color = DrawerColor;
                        New_Drawer.MaterialQuantity = DrawerQuantityInt;
                        New_Drawer.MaterialType = DrawerType;

                        ItemFrame frame = blockPose.getWorld().spawn(blockPose.getLocation(), ItemFrame.class, entity -> {
                            entity.setItem(new ItemStack(Material.valueOf(New_Drawer.MaterialType)));

                            entity.setVisible(false);

                            entity.setFixed(true);
                            entity.setInvulnerable(true);
                        });

                        New_Drawer.itemFrameUUID = frame.getUniqueId();

                        DrawerList.add(New_Drawer);
                    }
                    else
                    {
                    DrawerBlock New_Drawer = new DrawerBlock();
                    New_Drawer.LocationX = blockPose.getX();
                    New_Drawer.LocationY = blockPose.getY();
                    New_Drawer.LocationZ = blockPose.getZ();

                    New_Drawer.maxItem = 2048;
                    New_Drawer.WorldName = blockPose.getWorld().getName();
                    New_Drawer.Color = 0;
                    New_Drawer.MaterialQuantity = 0;
                    New_Drawer.MaterialType = "AIR";

                    DrawerList.add(New_Drawer);
                    }
                }
            }
        }
    }


    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        //Bukkit.broadcastMessage("Nombre de Drawer :  " + DrawerList.size());

        Player player = event.getPlayer();
        Block InteractBlock = event.getClickedBlock();
        ItemStack ItemHand = event.getItem();

        for (DrawerBlock drawer : DrawerList) {
            World world = Bukkit.getWorld(drawer.WorldName);

            if (world == null) continue;

            Location drawerLoc = new Location(world, drawer.LocationX, drawer.LocationY, drawer.LocationZ);

            if (drawerLoc.equals(InteractBlock.getLocation())) {

                org.bukkit.event.block.Action action = event.getAction();
                ItemStack itemInHand = player.getInventory().getItemInMainHand();

                boolean isDrawerItem = false;
                if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasItemName()) {
                    if (itemInHand.getItemMeta().getItemName().equals("Drawer")) {
                        isDrawerItem = true;
                    }
                }

                // Place the blocks in the drawer
                if (action == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK &&
                        itemInHand.getType() != Material.AIR && !player.isSneaking() && !isDrawerItem)
                {
                    event.setCancelled(true);
                    player.playSound(player,Sound.BLOCK_PISTON_CONTRACT,5.0f,2.0f);
                    if(drawer.MaterialType.equals("AIR"))
                    {
                        drawer.MaterialType = player.getInventory().getItemInMainHand().getType().name();
                        drawer.MaterialQuantity += player.getInventory().getItemInMainHand().getAmount();

                        player.getInventory().setItemInMainHand(null);

                        ItemFrame frame = world.spawn(InteractBlock.getLocation(), ItemFrame.class, entity -> {
                            entity.setItem(new ItemStack(Material.valueOf(drawer.MaterialType)));

                            entity.setVisible(false);

                            entity.setFixed(true);
                            entity.setInvulnerable(true);
                        });

                        drawer.itemFrameUUID = frame.getUniqueId();

                    }
                    else if (drawer.MaterialType.equals(itemInHand.getType().name()))
                    {
                        int spaceLeft = drawer.maxItem - drawer.MaterialQuantity;
                        int handAmount = itemInHand.getAmount();

                        if (spaceLeft > 0)
                        {
                            if (handAmount <= spaceLeft)
                            {
                                drawer.MaterialQuantity += handAmount;
                                player.getInventory().setItemInMainHand(null);
                            }
                            else
                            {
                                drawer.MaterialQuantity = drawer.maxItem;
                                itemInHand.setAmount(handAmount - spaceLeft);
                            }
                        }
                    }
                }

                // Access to DrawerGUI
                if (player.isSneaking() && action == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
                    event.setCancelled(true);
                    Inventory DrawerGUI = Bukkit.createInventory(null, 36, "Drawer");

                    //Drawer GUI ItemStack
                    ItemStack ColorButton = new ItemStack(Material.BLACK_WOOL, 1);
                    ItemStack DrawerStockedItem = new ItemStack(Material.valueOf(drawer.MaterialType), 1);
                    ItemStack FirstUpgrade = new ItemStack(Material.AIR, 1);
                    ItemStack SecondUpgrade = new ItemStack(Material.AIR, 1);
                    ItemStack ThirdUpgrade = new ItemStack(Material.AIR, 1);

                    ItemMeta ColorButtonMeta = ColorButton.getItemMeta();
                    ColorButtonMeta.setDisplayName("§lBackground Color");
                    ColorButton.setItemMeta(ColorButtonMeta);

                    if (DrawerStockedItem.getType() != Material.AIR) {
                        ItemMeta DrawerStockedItemMeta = DrawerStockedItem.getItemMeta();
                        if (DrawerStockedItemMeta != null) {
                            DrawerStockedItemMeta.setDisplayName("§b" + drawer.MaterialQuantity + " x " + drawer.MaterialType);
                            DrawerStockedItem.setItemMeta(DrawerStockedItemMeta);
                        }
                    }

                    ItemStack Background = new ItemStack(BACKGROUND_COLORS[0], 1);

                    ItemMeta BackgroundMeta = Background.getItemMeta();
                    BackgroundMeta.setDisplayName(" ");
                    Background.setItemMeta(BackgroundMeta);
                    Background.setType(BACKGROUND_COLORS[drawer.Color]);


                    for (int i = 0; i <= 35; i++)
                    {
                        DrawerGUI.setItem(i, Background);
                    }

                    DrawerGUI.setItem(0, ColorButton);
                    DrawerGUI.setItem(13, DrawerStockedItem);

                    //Upgrades
                    DrawerGUI.setItem(28, FirstUpgrade);
                    DrawerGUI.setItem(31, SecondUpgrade);
                    DrawerGUI.setItem(34, ThirdUpgrade);


                    openedDrawers.put(player, drawer);
                    //player.playSound(player,Sound.UI_HUD_BUBBLE_POP,5.0f,1.0f);
                    player.openInventory(DrawerGUI);
                }

                if (action == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK)
                {

                    if (drawer.MaterialType.equals("AIR") || drawer.MaterialQuantity <= 0) return;

                    if(player.getGameMode() == GameMode.CREATIVE)
                    {
                        event.setCancelled(true);
                    }

                    int desiredAmount;
                    if (player.isSneaking()) {
                        desiredAmount = Material.valueOf(drawer.MaterialType).getMaxStackSize();
                    } else {
                        desiredAmount = 1;
                    }

                    int amountToGive = Math.min(desiredAmount, drawer.MaterialQuantity);

                    drawer.MaterialQuantity -= amountToGive;

                    ItemStack itemToGive = new ItemStack(Material.valueOf(drawer.MaterialType), amountToGive);
                    HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(itemToGive);

                    if (!leftovers.isEmpty()) {
                        for (ItemStack remainingItem : leftovers.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), remainingItem);
                        }
                    }

                    player.playSound(player, Sound.ITEM_BUNDLE_DROP_CONTENTS, 5.0f, 1.0f);

                    if (drawer.MaterialQuantity == 0) {
                        drawer.MaterialType = "AIR";
                        Bukkit.getEntity(drawer.itemFrameUUID).remove();
                    }
                }
                break;
            }
        }
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block BreakBlock = event.getBlock();

        for (DrawerBlock drawer : DrawerList) {
            World world = Bukkit.getWorld(drawer.WorldName);

            if (world == null) continue;

            Location drawerLoc = new Location(world, drawer.LocationX, drawer.LocationY, drawer.LocationZ);

            if (drawerLoc.equals(BreakBlock.getLocation())) {
                DrawerList.remove(drawer);
                if(drawer.itemFrameUUID != null)
                {
                    Bukkit.getEntity(drawer.itemFrameUUID).remove();
                }
                event.setDropItems(false);

                ItemStack Drawer = new ItemStack(Material.OAK_PLANKS, 1);
                ItemMeta DrawerMeta = Drawer.getItemMeta();

                String DrawerBlocStatus = null;
                String DrawerBlockQuantity = null;
                if(drawer.MaterialType.equals("AIR"))
                {
                    DrawerBlocStatus = "Empty Drawer";
                    DrawerBlockQuantity = "";
                }
                else
                {
                    DrawerBlocStatus = "Type : " + drawer.MaterialType;
                    DrawerBlockQuantity = "Quantity : " + drawer.MaterialQuantity;

                }


                DrawerMeta.setDisplayName("Drawer");
                DrawerMeta.setItemName("Drawer");
                DrawerMeta.setLore(Arrays.asList(DrawerBlocStatus, DrawerBlockQuantity, "Color : " + BACKGROUND_COLORS[drawer.Color]));

                Drawer.setItemMeta(DrawerMeta);
                BreakBlock.getWorld().dropItemNaturally(BreakBlock.getLocation(), Drawer);
                break;
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        String invTitle = event.getView().getTitle();
        DrawerBlock currentDrawer = openedDrawers.get(player);
        ItemStack Background = new ItemStack(BACKGROUND_COLORS[0], 1);
        ItemMeta BackgroundMeta = Background.getItemMeta();
        BackgroundMeta.setDisplayName(" ");
        Background.setItemMeta(BackgroundMeta);


        if (invTitle.equals("Drawer")) {
            if (current == null) return;
            if (event.getRawSlot() == 0) {
                //player.sendMessage("§aCouleur Changé : " + Background.getType());

                currentDrawer.Color++;
                if (currentDrawer.Color >= BACKGROUND_COLORS.length) {
                    currentDrawer.Color = 0;
                }
                ReloadColorButton(inv, Background, currentDrawer.Color);

                player.playNote(player.getLocation(),Instrument.BIT, Note.natural(1, Note.Tone.A));
                event.setCancelled(true);
            }
            else if (event.getRawSlot() == 28 || event.getRawSlot() == 31 || event.getRawSlot() == 34)
            {
                //Upgrades Slots
            }
            else
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent event)
    {
        Inventory inv = event.getInventory();
        Player player = event.getPlayer().getKiller();

        if(inv.getType().name().equals("Drawer"))
        {
            openedDrawers.remove(player);
        }
    }

    public static void SaveDrawer ()
    {
        try {
            Writer.writeValue(Path.of(FilePath), DrawerList);
            main.getLogger().info("Saving Drawer(s)");
        } catch (Exception e) {
            main.getLogger().severe("Saving Drawer Error : " + e);
        }
    }

    public static void LoadDrawer ()
    {
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
}

