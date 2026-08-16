package fr.hadrix.drawer;

import org.bukkit.Material;

public class DrawerConstants {
    // GUI Settings
    public static final String GUI_TITLE = "Drawer";
    public static final int GUI_SIZE = 36;

    // Slots
    public static final int SLOT_COLOR_BTN = 0;
    public static final int SLOT_ITEM_DISPLAY = 13;
    public static final int SLOT_UPGRADE_1 = 28;
    public static final int SLOT_UPGRADE_2 = 31;
    public static final int SLOT_UPGRADE_3 = 34;

    // Drawer Settings
    public static final int DEFAULT_MAX_ITEMS = 2048;
    public static final String DEFAULT_ITEM_NAME = "Drawer";
    public static final String EMPTY_DRAWER_TEXT = "Empty Drawer";

    public static final Material[] BACKGROUND_COLORS = {
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
}