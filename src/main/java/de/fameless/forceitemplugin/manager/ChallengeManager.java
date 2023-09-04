package de.fameless.forceitemplugin.manager;

import de.fameless.forceitemplugin.challenge.ChallengeType;
import de.fameless.forceitemplugin.timer.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChallengeManager {

    private static ChallengeType challengeType;

    public static ChallengeType getChallengeType() {
        return challengeType;
    }

    public static void setChallengeType(ChallengeType type) {
        challengeType = type;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) {
                if (player.getOpenInventory().getTitle().endsWith("Timer")) {
                    player.closeInventory();
                }
            }
        }
        if (challengeType.equals(ChallengeType.FORCE_ITEM)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Item has been selected! Progress reset");
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemManager.resetProgress(player);
                ItemManager.itemMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        } else if (challengeType.equals(ChallengeType.FORCE_BLOCK)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Block has been selected! Progress reset");
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemManager.resetProgress(player);
                ItemManager.blockMap.put(player.getUniqueId(), ItemManager.nextItem(player));
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        } else if (challengeType.equals(ChallengeType.FORCE_MOB)) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "Challenge Force Mob has been selected! Progress reset");
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemManager.resetProgress(player);
                ItemManager.entityMap.put(player.getUniqueId(), ItemManager.nextMob(player));
                NametagManager.updateNametag(player);
                BossbarManager.updateBossbar(player);
            }
        }
        Timer.setTime(Timer.getStartTime());
    }
}