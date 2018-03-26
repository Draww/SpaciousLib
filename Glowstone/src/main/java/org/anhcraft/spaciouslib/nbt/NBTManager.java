package org.anhcraft.spaciouslib.nbt;

import org.anhcraft.spaciouslib.utils.GameVersion;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A class helps you to load or apply NBT tags
 */
public class NBTManager {
    /**
     * Creates a new NBT Compound tag
     * @return NBTCompound object
     */
    public static NBTCompound newCompound(){
        try {
            Class<?> e = Class.forName("org.anhcraft.spaciouslib.nbt.NBTCompound_" +
                    GameVersion.getVersion().toString().replace("v", ""));
            Constructor c = e.getConstructor();
            return (NBTCompound) c.newInstance();
        } catch(ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * Loads all NBT tags from the given item
     * @param i the item
     * @return NBTCompound object
     */
    public static NBTCompound fromItem(ItemStack i){
        if(i == null){
            throw new NullPointerException();
        }
        try {
            Class<?> e = Class.forName("org.anhcraft.spaciouslib.nbt.NBTCompound_" +
                    GameVersion.getVersion().toString().replace("v", ""));
            Constructor c = e.getConstructor();
            NBTCompound compound = ((NBTCompound) c.newInstance());
            compound.fromItem(i.clone());
            return compound;
        } catch(ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * Loads all NBT tags from the given file
     * @param f the file
     * @return NBTCompound object
     */
    public static NBTCompound fromFile(File f){
        if(f == null){
            throw new NullPointerException();
        }
        try {
            Class<?> e = Class.forName("org.anhcraft.spaciouslib.nbt.NBTCompound_" +
                    GameVersion.getVersion().toString().replace("v", ""));
            Constructor c = e.getConstructor();
            NBTCompound compound = ((NBTCompound) c.newInstance());
            compound.fromFile(f);
            return compound;
        } catch(ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * Loads all NBT tags from the given entity
     * @param entity the entity
     * @return NBTCompound object
     */
    public static NBTCompound fromEntity(Entity entity){
        if(entity == null){
            throw new NullPointerException();
        }
        try {
            Class<?> e = Class.forName("org.anhcraft.spaciouslib.nbt.NBTCompound_" +
                    GameVersion.getVersion().toString().replace("v", ""));
            Constructor c = e.getConstructor();
            NBTCompound compound = ((NBTCompound) c.newInstance());
            compound.fromEntity(entity);
            return compound;
        } catch(ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}