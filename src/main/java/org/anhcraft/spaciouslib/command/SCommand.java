package org.anhcraft.spaciouslib.command;

import org.anhcraft.spaciouslib.utils.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class SCommand {
    public enum ArgumentType{
        CUSTOM,
        URL,
        EMAIL,
        ONLINE_PLAYER,
        INTEGER_NUMBER,
        REAL_NUMBER,
        BOOLEAN,
        WORLD
    }

    private Command command;
    private String name;
    private CommandRunnable rootRunnable;
    private LinkedHashMap<ArgumentType, String> argErrorMessages = new LinkedHashMap<>();
    private LinkedHashMap<CommandArgument, ArgumentType> args = new LinkedHashMap<>();
    private String doesNotEnoughtArgsErrorMessage;

    @Deprecated
    public SCommand(Command command){
        this.command = command;
        this.name = command.getName();
    }

    public SCommand(PluginCommand command){
        this.command = command;
        this.name = command.getName();
    }

    public SCommand(String name, CommandRunnable rootRunnable) throws Exception {
        name = name.trim();
        if(name.split(" ").length != 1){
            throw new Exception("Invalid command name!");
        }
        this.name = name.toLowerCase();
        this.rootRunnable = rootRunnable;
        initArgErrorMessage();
    }

    private void initArgErrorMessage() {
        setArgErrorMessage(ArgumentType.URL,
                "&cYou must type a valid URL! (e.g: https://example.com/)");
        setArgErrorMessage(ArgumentType.EMAIL,
                "&cYou must type a valid email address! (e.g: email@website.com)");
        setArgErrorMessage(ArgumentType.ONLINE_PLAYER,
                "&cThat player isn't online!");
        setArgErrorMessage(ArgumentType.INTEGER_NUMBER,
                "&cYou must type a valid integer number! (e.g: 1, 5, 10, -3, etc)");
        setArgErrorMessage(ArgumentType.REAL_NUMBER,
                "&cYou must type a valid real number! (e.g: 0.1, 5, -3.2, 49.0, etc)");
        setArgErrorMessage(ArgumentType.BOOLEAN,
                "&cYou must type a valid boolean! (true, false)");
        setArgErrorMessage(ArgumentType.WORLD,
                "&cCouldn't find that world!");
    }

    public ArgumentType getArgumentType(CommandArgument arg){
        return this.args.get(arg);
    }

    public ArrayList<CommandArgument> getArguments(){
        return new ArrayList<>(this.args.keySet());
    }

    public ArrayList<CommandArgument> getArguments(boolean optional){
        ArrayList<CommandArgument> x = new ArrayList<>();
        for(CommandArgument c : getArguments()){
            if(c.isOptional() == optional){
                x.add(c);
            }
        }
        return x;
    }

    public SCommand removeArgument(CommandArgument arg){
        this.args.remove(arg);
        return this;
    }

    public SCommand setArgument(CommandArgument arg, ArgumentType type){
        this.args.put(arg, type);
        return this;
    }

    public SCommand setArgument(String name, CommandRunnable argRunnable, ArgumentType type, boolean optional) throws Exception {
        setArgument(new CommandArgument(name, argRunnable, optional), type);
        return this;
    }

    public SCommand setArgErrorMessage(ArgumentType type, String message){
        this.argErrorMessages.put(type, Strings.color(message));
        return this;
    }

    public SCommand setDoesNotEnoughtArgsErrorMessage(String message){
        this.doesNotEnoughtArgsErrorMessage = Strings.color(message);
        return this;
    }

    public String getCommandString(boolean color){
        StringBuilder cmd;
        if(color){
            cmd = new StringBuilder("&f/" + this.name);
            for(CommandArgument arg : args.keySet()){
                cmd.append(" ").append(arg.isOptional() ? "&8[" : "&e<").append("&a")
                        .append(arg.getName()).append("&c:")
                        .append(args.get(arg).toString())
                        .append(arg.isOptional() ? "&8]" : "&e>");
            }
        } else {
            cmd = new StringBuilder("/" + this.name);
            for(CommandArgument arg : args.keySet()){
                cmd.append(" ").append(arg.isOptional() ? "[" : "<")
                        .append(arg.getName()).append(":")
                        .append(args.get(arg).toString())
                        .append(arg.isOptional() ? "]" : ">");
            }
        }
        return cmd.toString();
    }

    public String getCommandString(boolean color, int maxArgSize){
        StringBuilder cmd;
        int i = 0;
        if(color){
            cmd = new StringBuilder("&f/" + this.name);
            for(CommandArgument arg : getArguments()){
                if(i >= maxArgSize){
                    break;
                }
                cmd.append(" &e<&a").append(arg.getName()).append("&c:")
                        .append(getArgumentType(arg).toString()).append("&e>");
                i++;
            }
        } else {
            cmd = new StringBuilder("/" + this.name);
            for(CommandArgument arg : getArguments()){
                if(i >= maxArgSize){
                    break;
                }
                cmd.append(" <").append(arg.getName()).append(":")
                        .append(getArgumentType(arg).toString()).append(">");
                i++;
            }
        }
        return cmd.toString();
    }

    public Command getCommand(){
        return this.command;
    }

    public SCommand normalize(){
        LinkedHashMap<CommandArgument, ArgumentType> require = new LinkedHashMap<>();
        LinkedHashMap<CommandArgument, ArgumentType> optional = new LinkedHashMap<>();
        for(CommandArgument c : getArguments()){
            if(c.isOptional()) {
                continue;
            }
            require.put(c, getArgumentType(c));
        }
        for(CommandArgument c : getArguments()){
            if(!c.isOptional()) {
                continue;
            }
            optional.put(c, getArgumentType(c));
        }
        this.args = new LinkedHashMap<>();
        this.args.putAll(require);
        this.args.putAll(optional);
        return this;
    }

    public boolean isValid(){
        int a = 0;
        for(CommandArgument c : getArguments()){
            if(c.isOptional()) {
                a = 1;
            } else {
                if(a == 1){
                  return false;
                }
            }
        }
        return true;
    }

    public SCommand buildExecutor(JavaPlugin plugin) throws Exception {
        SCommand sc = this;
        if(!isValid()){
            throw new Exception("This command hasn't normalized yet! Please use normalize() method before using this method!");
        }
        if(getCommand() == null){
            CommandManager.register(new BukkitCommand(this.name) {
                @Override
                public boolean execute(CommandSender s, String l, String[] a) {
                    sc.execute(s, a);
                    return false;
                }
            });
        } else if(getCommand() instanceof PluginCommand){
            ((PluginCommand) getCommand()).setExecutor(new CommandExecutor() {
                @Override
                public boolean onCommand(CommandSender s, Command command,
                                         String l, String[] a) {
                    sc.execute(s, a);
                    return false;
                }
            });
        }
        return sc;
    }

    private void execute(CommandSender s, String[] a) {
        SCommand sc = this;
        if(a.length == 0){
            rootRunnable.run(sc, s, a);
        } else {
            LinkedHashMap<CommandArgument, String> values = new LinkedHashMap<>();
            int i = 0;
            for(String value : a) {
                if(getArguments().size() <= i) {
                    break;
                }
                values.put(getArguments().get(i), value);
                i++;
            }
            if(values.size() < getArguments(false).size()) {
                s.sendMessage(sc.doesNotEnoughtArgsErrorMessage);
            } else {
                boolean hasError = false;
                argTypeValidator:
                for(CommandArgument arg : values.keySet()) {
                    String value = values.get(arg);
                    switch(getArgumentType(arg)) {
                        case URL:
                            if(!Pattern.compile("(https?|ftp):(/{1,})((?!-)([-a-zA-Z0-9]{1,})(?<!-))\\.((?!-)([-a-zA-Z0-9]{1,})).*").matcher(value).matches()) {
                                s.sendMessage(sc.argErrorMessages.get(ArgumentType.URL));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case EMAIL:
                            if(!Pattern.compile("[A-Za-z0-9\\.\\-\\_]+@[A-Za-z0-9\\-\\_]+\\.[A-Za-z0-9\\-\\_]+").matcher(value).matches()) {
                                s.sendMessage(sc.argErrorMessages.get(ArgumentType.EMAIL));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case BOOLEAN:
                            if(!value.equalsIgnoreCase("true")
                                    && !value.equalsIgnoreCase("false")) {
                                s.sendMessage(sc.argErrorMessages.get(ArgumentType.BOOLEAN));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case WORLD:
                            if(Bukkit.getServer().getWorld(value) == null) {
                                s.sendMessage(sc.argErrorMessages.get(ArgumentType.WORLD));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case REAL_NUMBER:
                            if(!Pattern.compile("(^(-|)[0-9]+$)|(^(-|)[0-9]+\\.[0-9]+$)").matcher(value).matches()) {
                                s.sendMessage(sc.argErrorMessages.get(ArgumentType.REAL_NUMBER));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case INTEGER_NUMBER:
                            if(!Pattern.compile("^(-|)[0-9]+$").matcher(value).matches()) {
                                s.sendMessage(sc.argErrorMessages.get(ArgumentType.INTEGER_NUMBER));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                        case ONLINE_PLAYER:
                            if(!Bukkit.getServer().getOfflinePlayer(value).isOnline()) {
                                s.sendMessage(sc.argErrorMessages.get(ArgumentType.ONLINE_PLAYER));
                                hasError = true;
                                break argTypeValidator;
                            }
                            break;
                    }
                }
                if(!hasError) {
                    CommandArgument arg = new ArrayList<>(values.keySet())
                            .get(values.size() - 1);
                    arg.getRunnable().run(sc, s, a);
                }
            }
        }
    }
}
