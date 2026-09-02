package fr.hadrix.drawer.listeners;

import fr.hadrix.drawer.DrawerBlock;
import fr.hadrix.drawer.manager.DrawerManager;
import fr.hadrix.drawer.utils.DrawerConstants;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class DrawerInteractListener implements Listener {
    private final DrawerManager manager;

    public DrawerInteractListener(DrawerManager manager) {
        this.manager = manager;
    }

    public void ReloadColorButton(Inventory inv, ItemStack Bg, int Color) {
        Bg.setType(DrawerConstants.BACKGROUND_COLORS[Color]);

        for (int i = 0; i <= 35; i++) {
            if(i != 0 && i != 13 && i != 28 && i != 31 && i != 34)
            {
                inv.setItem(i, Bg);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        //Bukkit.broadcastMessage("Nombre de Drawer :  " + DrawerList.size());

        Player player = event.getPlayer();
        Block InteractBlock = event.getClickedBlock();
        ItemStack ItemHand = event.getItem();

        for (DrawerBlock drawer : manager.getDrawers()) {
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
                    player.playSound(player, Sound.BLOCK_PISTON_CONTRACT,5.0f,2.0f);
                    if(drawer.MaterialType.equals("AIR"))
                    {
                        drawer.MaterialType = player.getInventory().getItemInMainHand().getType().name();
                        drawer.MaterialQuantity += player.getInventory().getItemInMainHand().getAmount();

                        player.getInventory().setItemInMainHand(null);

                        BlockData blockData = InteractBlock.getBlockData();
                        final BlockFace face;

                        if (blockData instanceof org.bukkit.block.data.Directional) {
                            face = ((org.bukkit.block.data.Directional) blockData).getFacing();
                        } else {
                            face = BlockFace.NORTH; // Valeur par défaut si ce n'est pas un bloc directionnel
                        }

                        Location frameLoc = InteractBlock.getLocation().add(0.5, 0.5, 0.5).add(face.getDirection().multiply(0.5));

                        ItemFrame frame = InteractBlock.getWorld().spawn(frameLoc, ItemFrame.class, entity -> {
                            entity.setItem(new ItemStack(Material.valueOf(drawer.MaterialType)));
                            entity.setVisible(false);
                            entity.setFixed(true);
                            entity.setInvulnerable(true);
                            entity.setFacingDirection(face); // TRES IMPORTANT : Oriente le cadre pour qu'il soit plat
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

                    ItemStack Background = new ItemStack(DrawerConstants.BACKGROUND_COLORS[0], 1);

                    ItemMeta BackgroundMeta = Background.getItemMeta();
                    BackgroundMeta.setDisplayName(" ");
                    Background.setItemMeta(BackgroundMeta);
                    Background.setType(DrawerConstants.BACKGROUND_COLORS[drawer.Color]);


                    for (int i = 0; i <= 35; i++)
                    {
                        DrawerGUI.setItem(i, Background);
                    }

                    DrawerGUI.setItem(DrawerConstants.SLOT_COLOR_BTN, ColorButton);
                    DrawerGUI.setItem(DrawerConstants.SLOT_ITEM_DISPLAY, DrawerStockedItem);

                    //Upgrades
                    DrawerGUI.setItem(DrawerConstants.SLOT_UPGRADE_1, FirstUpgrade);
                    DrawerGUI.setItem(DrawerConstants.SLOT_UPGRADE_2, SecondUpgrade);
                    DrawerGUI.setItem(DrawerConstants.SLOT_UPGRADE_3, ThirdUpgrade);


                    manager.getOpenedDrawers().put(player, drawer);
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
                        Entity frameEntity = Bukkit.getEntity(drawer.itemFrameUUID);
                        if (frameEntity != null) {
                            frameEntity.remove();
                        }
                    }
                }
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        String invTitle = event.getView().getTitle();
        DrawerBlock currentDrawer = manager.getOpenedDrawers().get(player);
        ItemStack Background = new ItemStack(DrawerConstants.BACKGROUND_COLORS[0], 1);
        ItemMeta BackgroundMeta = Background.getItemMeta();
        BackgroundMeta.setDisplayName(" ");
        Background.setItemMeta(BackgroundMeta);


        if (invTitle.equals(DrawerConstants.GUI_TITLE)) {
            if (current == null) return;
            if (event.getRawSlot() == 0) {
                //player.sendMessage("§aCouleur Changé : " + Background.getType());

                currentDrawer.Color++;
                if (currentDrawer.Color >= DrawerConstants.BACKGROUND_COLORS.length) {
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
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();

        if(event.getView().getTitle().equals(DrawerConstants.GUI_TITLE)) {
            manager.getOpenedDrawers().remove(player);
        }
    }
}