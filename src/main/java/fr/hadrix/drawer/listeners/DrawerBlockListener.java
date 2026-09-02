package fr.hadrix.drawer.listeners;

import fr.hadrix.drawer.DrawerBlock;
import fr.hadrix.drawer.manager.DrawerManager;
import fr.hadrix.drawer.utils.DrawerConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class DrawerBlockListener implements Listener {
    private final DrawerManager manager;

    public DrawerBlockListener(DrawerManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block blockPose = event.getBlock();
        boolean HasSave = false;
        int DrawerColor = 0;
        String DrawerType = DrawerConstants.EMPTY_DRAWER_TEXT;
        String DrawerQuantity = "0";
        int DrawerQuantityInt = 0;
        ItemStack itemHand = event.getItemInHand();

        if (itemHand.getType() == Material.OAK_PLANKS) {
            if (itemHand.hasItemMeta() && itemHand.getItemMeta().hasDisplayName()) {
                if (itemHand.getItemMeta().getDisplayName().equals(DrawerConstants.DEFAULT_ITEM_NAME) &&
                        itemHand.getItemMeta().getItemName().equals(DrawerConstants.DEFAULT_ITEM_NAME))
                {
                    if(itemHand.getItemMeta().hasLore())
                    {
                        List<String> DrawerLore = itemHand.getItemMeta().getLore();
                        if(DrawerLore.size() == 3) {
                            if (!DrawerLore.get(0).equals(DrawerConstants.EMPTY_DRAWER_TEXT)) {
                                for (int i = 0; i < DrawerConstants.BACKGROUND_COLORS.length; i++) {
                                    if (DrawerLore.get(2).equals("Color : " + DrawerConstants.BACKGROUND_COLORS[i])) {
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

                        /*
                        ItemFrame frame = blockPose.getWorld().spawn(blockPose.getLocation(), ItemFrame.class, entity -> {
                            entity.setItem(new ItemStack(Material.valueOf(New_Drawer.MaterialType)));

                            entity.setVisible(false);

                            entity.setFixed(true);
                            entity.setInvulnerable(true);
                        });
                        */

                        BlockData blockData = blockPose.getBlockData();
                        final BlockFace face;

                        if (blockData instanceof org.bukkit.block.data.Directional) {
                            face = ((org.bukkit.block.data.Directional) blockData).getFacing();
                        } else {
                            face = BlockFace.NORTH;
                        }

                        Location frameLoc = blockPose.getLocation().add(0.5, 0.5, 0.5).add(face.getDirection().multiply(0.5));

                        ItemFrame frame = blockPose.getWorld().spawn(frameLoc, ItemFrame.class, entity -> {
                            entity.setItem(new ItemStack(Material.valueOf(New_Drawer.MaterialType)));
                            entity.setVisible(false);
                            entity.setFixed(true);
                            entity.setInvulnerable(true);
                            entity.setFacingDirection(face);
                        });

                        New_Drawer.itemFrameUUID = frame.getUniqueId();

                        manager.getDrawers().add(New_Drawer);
                    }
                    else
                    {
                        DrawerBlock New_Drawer = new DrawerBlock();
                        New_Drawer.LocationX = blockPose.getX();
                        New_Drawer.LocationY = blockPose.getY();
                        New_Drawer.LocationZ = blockPose.getZ();

                        New_Drawer.maxItem = DrawerConstants.DEFAULT_MAX_ITEMS;
                        New_Drawer.WorldName = blockPose.getWorld().getName();
                        New_Drawer.Color = 0;
                        New_Drawer.MaterialQuantity = 0;
                        New_Drawer.MaterialType = "AIR";

                        manager.getDrawers().add(New_Drawer);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block BreakBlock = event.getBlock();
        DrawerBlock DrawertoRemove = null;

        for (DrawerBlock drawer : manager.getDrawers()) {
            World world = Bukkit.getWorld(drawer.WorldName);

            if (world == null) continue;

            Location drawerLoc = new Location(world, drawer.LocationX, drawer.LocationY, drawer.LocationZ);

            if (drawerLoc.equals(BreakBlock.getLocation())) {
                DrawertoRemove = drawer;
                event.setDropItems(false);

                ItemStack Drawer = new ItemStack(Material.OAK_PLANKS, 1);
                ItemMeta DrawerMeta = Drawer.getItemMeta();

                String DrawerBlocStatus = null;
                String DrawerBlockQuantity = null;
                if(drawer.MaterialType.equals("AIR"))
                {
                    DrawerBlocStatus = DrawerConstants.EMPTY_DRAWER_TEXT;
                    DrawerBlockQuantity = "";
                }
                else
                {
                    DrawerBlocStatus = "Type : " + drawer.MaterialType;
                    DrawerBlockQuantity = "Quantity : " + drawer.MaterialQuantity;

                }


                DrawerMeta.setDisplayName(DrawerConstants.DEFAULT_ITEM_NAME);
                DrawerMeta.setItemName(DrawerConstants.DEFAULT_ITEM_NAME);
                DrawerMeta.setLore(Arrays.asList(DrawerBlocStatus, DrawerBlockQuantity, "Color : " + DrawerConstants.BACKGROUND_COLORS[drawer.Color]));

                Drawer.setItemMeta(DrawerMeta);
                BreakBlock.getWorld().dropItemNaturally(BreakBlock.getLocation(), Drawer);
                break;
            }
        }
        if(DrawertoRemove != null)
        {
            manager.getDrawers().remove(DrawertoRemove);
            if (DrawertoRemove.itemFrameUUID != null) {
                Entity frameEntity = Bukkit.getEntity(DrawertoRemove.itemFrameUUID);
                if (frameEntity != null)
                {
                    frameEntity.remove();
                }
            }
        }
    }
}