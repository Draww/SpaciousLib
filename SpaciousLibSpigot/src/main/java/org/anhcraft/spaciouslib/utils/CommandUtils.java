package org.anhcraft.spaciouslib.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * A class helps you to manage plugin command
 */
public class CommandUtils {
    /**
     * Registers a command
     * @param plugin the plugin which is owned the command
     * @param command the command
     */
    public static void register(JavaPlugin plugin, PluginCommand command){
        try {
            Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit." + GameVersion.getVersion().toString() + ".CraftServer");
            Object craftServer = ReflectionUtils.cast(craftServerClass, Bukkit.getServer());
            SimpleCommandMap commandMap = (SimpleCommandMap) ReflectionUtils.getField("commandMap", craftServerClass, craftServer);
            commandMap.register(plugin.getDescription().getName(), command);
            ReflectionUtils.setField("commandMap", craftServerClass, craftServer, commandMap);
            SimplePluginManager simplePluginManager = (SimplePluginManager) ReflectionUtils.getField("pluginManager", craftServerClass, craftServer);
            SimpleCommandMap commandPluginManagerMap = (SimpleCommandMap) ReflectionUtils.getField("commandMap", simplePluginManager.getClass(), simplePluginManager);
            commandPluginManagerMap.register(plugin.getDescription().getName(), command);
            ReflectionUtils.setField("commandMap", simplePluginManager.getClass(), simplePluginManager, commandPluginManagerMap);
            ReflectionUtils.setField("pluginManager", craftServerClass, craftServer, simplePluginManager);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregisters a command
     * @param plugin the plugin which is owned the command
     * @param command the command
     */
    public static void unregister(JavaPlugin plugin, PluginCommand command){
        try {
            Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit." + GameVersion.getVersion().toString() + ".CraftServer");

            Object craftServer = ReflectionUtils.cast(craftServerClass, Bukkit.getServer());
            SimpleCommandMap commandMap = (SimpleCommandMap) ReflectionUtils.getField("commandMap", craftServerClass, craftServer);
            Map<String, Command> knownCommands = (Map<String, Command>) ReflectionUtils.getField("knownCommands", commandMap.getClass(), commandMap);
            knownCommands.remove(plugin.getName()+":"+command.getName());
            for (String alias : command.getAliases()){
                alias = plugin.getName()+":"+alias;
                if(knownCommands.containsKey(alias) &&
                        knownCommands.get(alias).toString().contains(command.getName())){
                    knownCommands.remove(alias);
                }
            }
            ReflectionUtils.setField("knownCommands", commandMap.getClass(), commandMap, knownCommands);
            ReflectionUtils.setField("commandMap", craftServerClass, craftServer, commandMap);

            SimplePluginManager simplePluginManager = (SimplePluginManager) ReflectionUtils.getField("pluginManager", craftServerClass, craftServer);
            SimpleCommandMap commandPluginManagerMap = (SimpleCommandMap) ReflectionUtils.getField("commandMap", simplePluginManager.getClass(), simplePluginManager);

            Map<String, Command> knownCommandsPluginManager = (Map<String, Command>) ReflectionUtils.getField("knownCommands", commandPluginManagerMap.getClass(), commandPluginManagerMap);
            knownCommandsPluginManager.remove(plugin.getName()+":"+command.getName());
            for (String alias : command.getAliases()){
                alias = plugin.getName()+":"+alias;
                if(knownCommandsPluginManager.containsKey(alias) &&
                        knownCommandsPluginManager.get(alias).toString().contains(command.getName())){
                    knownCommandsPluginManager.remove(alias);
                }
            }
            ReflectionUtils.setField("knownCommands", commandPluginManagerMap.getClass(), commandPluginManagerMap, knownCommandsPluginManager);
            ReflectionUtils.setField("commandMap", simplePluginManager.getClass(), simplePluginManager, commandPluginManagerMap);
            ReflectionUtils.setField("pluginManager", craftServerClass, craftServer, simplePluginManager);
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
