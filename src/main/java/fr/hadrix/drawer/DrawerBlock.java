package fr.hadrix.drawer;

import org.bukkit.Location;

import java.util.UUID;

public class DrawerBlock {


    Location loc;

    public int LocationX;
    public int LocationY;
    public int LocationZ;
    public String WorldName;
    public int maxItem;
    public int Color;
    public String MaterialType;
    public int MaterialQuantity;
    public UUID itemFrameUUID = null;

    public DrawerBlock() {}
}
