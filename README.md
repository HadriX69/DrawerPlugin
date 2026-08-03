# DrawerPlugin

> [!NOTE]
>💡 This Minecraft Plugin work on spigot server of the version 1.21 - 1.21.11,
> but i work on making the plugin compatible with versions 1.21+, and possibly adding compatibility for versions 1.19.4–1.20

This Plugin add drawer to Minecraft like the [Storage Drawers Mod](https://www.curseforge.com/minecraft/mc-mods/storage-drawers)

## Commands :
```
/drawer give : give you an empty drawer
/drawer give <player_name> : give an empty drawer to the designated player
/drawer list : List the Returns the number of drawers existing on the server
```
## Craft :
__8 oak planks with a chest in the middle__

## Drawer input
**Sneak + Right Click :** Open the Drawer Interface\
**Right Click :** if you have a block or an item in your main hand and the drawer is empty or contains the same block/item you  are holding in your hand, it stock it\
**Left Click :** if the drawer is not empty it's will give you 1 block/item from the drawer\
**Sneak + Left Click :** if the drawer is not empty it's will give you 64 or less (if the drawer contain less of 64 blocks/items) blocks/items

## ⚙️ How work this Plugin
when you start the server with the plugin a folder name **DrawerList** with a file name **DrawerList.json** will be create in your plugin file
When a player places a drawer on the ground, it is load in the server's RAM and when the server is stopped, the data for the drawer loaded in RAM is saved to the **DrawerList.json** file.

When the server is re/started, it checks if the **DrawerList.json** file contains data, and if so, it loads the drawer(s) into RAM.
