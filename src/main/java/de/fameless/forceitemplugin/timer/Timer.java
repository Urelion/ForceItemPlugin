package de.fameless.forceitemplugin.timer;

import de.fameless.forceitemplugin.ForceBattlePlugin;
import de.fameless.forceitemplugin.manager.ChallengeManager;
import de.fameless.forceitemplugin.manager.LeaderboardManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer implements CommandExecutor {
    private static int startTime;
    private static int time;
    private static boolean running;

    static {
        Timer.startTime = ForceBattlePlugin.getInstance().getConfig().getInt("challenge_duration");
    }

    public Timer() {
        run();
    }

    public static int getStartTime() {
        return Timer.startTime;
    }

    public static void setStartTime(int startTime) {
        Timer.startTime = startTime;
    }

    public static int getTime() {
        return Timer.time;
    }

    public static void setTime(int time) {
        Timer.time = time;
    }

    public static boolean isRunning() {
        return Timer.running;
    }

    public static void setRunning(boolean running) {
        Timer.running = running;
    }

    public static void run() {
        if (ForceBattlePlugin.getInstance().getConfig().get("time") != null) {
            setStartTime(ForceBattlePlugin.getInstance().getConfig().getInt("time"));
        } else if (ForceBattlePlugin.getInstance().getConfig().getBoolean("count_up")) {
            setStartTime(0);
        } else {
            setStartTime(ForceBattlePlugin.getInstance().getConfig().getInt("challenge_duration"));
        }
        setTime(Timer.startTime);
        new BukkitRunnable() {
            public void run() {
                Timer.sendActionbar();
                if (ForceBattlePlugin.getInstance().getConfig().getBoolean("count_up")) {
                    if (Timer.isRunning()) {
                        Timer.setTime(Timer.getTime() + 1);
                    }
                } else if (Timer.isRunning()) {
                    Timer.setTime(Timer.getTime() - 1);
                    if (Timer.time == 0) {
                        Timer.setRunning(false);
                        Timer.setTime(Timer.getStartTime());
                        LeaderboardManager.displayLeaderboard();
                    }
                }
            }
        }.runTaskTimer(ForceBattlePlugin.getInstance(), 0L, 20L);
    }

    public static void sendActionbar() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            int days = Timer.time / 86400;
            int hours = Timer.time / 3600 % 24;
            int minutes = Timer.time / 60 % 60;
            int seconds = Timer.time % 60;
            StringBuilder message = new StringBuilder();
            if (ForceBattlePlugin.getInstance().getConfig().getInt("challenge_duration") == -1) {
                message.append("Infinite Time");
            } else {
                if (days >= 1) {
                    message.append(days).append("d ");
                }
                if (hours >= 1) {
                    message.append(hours).append("h ");
                }
                if (minutes >= 1) {
                    message.append(minutes).append("m ");
                }
                if (seconds >= 1) {
                    message.append(seconds).append("s ");
                }
                if (getTime() == 0) {
                    message.append("0s");
                }
            }
            if (!isRunning()) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() + ChatColor.ITALIC + message));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() + message));
            }
        }
    }

    public static void toggle(Player player) {
        if (ChallengeManager.getChallengeType() == null) {
            player.sendMessage(ChatColor.GOLD + "You need to select a challenge to start the timer. /menu");
            return;
        }
        if (ForceBattlePlugin.getInstance().getConfig().getInt("challenge_duration") == -1) {
            player.sendMessage(ChatColor.RED + "Time is set to infinite.");
            return;
        }
        if (isRunning()) {
            setRunning(false);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendTitle(ChatColor.RED + "Timer paused.", "", 20, 40, 20);
            }
            Bukkit.broadcastMessage(ChatColor.RED + "Timer has been paused.\n" + ChatColor.GOLD + "You can't collect any more items!");
        } else {
            setRunning(true);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.sendTitle(ChatColor.RED + "Timer started.", "", 20, 40, 20);
            }
            Bukkit.broadcastMessage(ChatColor.GREEN + "Timer has been started.\n" + ChatColor.GOLD + "You can now start collecting your items!");
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("forcebattle.timer")) {
                player.sendMessage(ChatColor.RED + "Lacking permission: 'forcebattle.timer'");
                return false;
            }
            if (args.length >= 1) {
                String s2 = args[0];
                switch (s2) {
                    case "toggle": {
                        toggle(player);
                        break;
                    }
                    case "set": {
                        if (args.length == 2) {
                            try {
                                int time = Integer.parseInt(args[1]);
                                setTime(time);
                                setStartTime(time);
                                player.sendMessage(ChatColor.GOLD + "Timer has been set to " + time + " seconds.");
                                sendActionbar();
                            } catch (NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Time value must be an integer.");
                            }
                            break;
                        }
                        player.sendMessage(ChatColor.RED + "Usage: /timer set <time>");
                        break;
                    }
                    default: {
                        sender.sendMessage(ChatColor.RED + "Invalid usage! Please use: /timer toggle");
                        break;
                    }
                }
            } else {
                player.openInventory(TimerUI.getTimerUI());
            }
        }
        return false;
    }
}