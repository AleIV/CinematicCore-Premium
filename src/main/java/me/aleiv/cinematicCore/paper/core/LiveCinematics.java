package me.aleiv.cinematicCore.paper.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.NonNull;
import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.LiveCinematicInfo;

public class LiveCinematics implements Listener {

    private @NonNull CinematicTool instance;

    private ConcurrentHashMap<UUID, LiveCinematicInfo> liveCinematics;

    public LiveCinematics(CinematicTool instance) {
        this.instance = instance;
        this.liveCinematics = new ConcurrentHashMap<>();

        Bukkit.getPluginManager().registerEvents(this, instance);
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> this.liveCinematics.values().stream().filter(info -> !info.isRunning()).forEach(i -> this.liveCinematics.remove(i.getParentUUID())), 0L, 5*10L);

        // Teleport task
        Bukkit.getScheduler().runTaskTimer(instance, () -> Bukkit.getOnlinePlayers().forEach(p -> {
            LiveCinematicInfo info = this.getCinematicWherePlayerIsIn(p);
            if (info != null) {
                p.teleport(info.getParentPlayer().getLocation());
            }
        }), 0L, 1L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (this.liveCinematics.containsKey(e.getPlayer().getUniqueId())) {
            LiveCinematicInfo pInfo = this.liveCinematics.get(e.getPlayer().getUniqueId());
            pInfo.stop();
            return;
        }
        LiveCinematicInfo info = this.getCinematicWherePlayerIsIn(e.getPlayer());
        if (info != null) {
            info.forceRemove(e.getPlayer());
        }
    }

    public boolean isPlayerInCinematic(UUID uuid) {
        return this.liveCinematics.values().stream().anyMatch(i -> i.getPlayers().contains(uuid));
    }

    public LiveCinematicInfo createCinematic(Player parent) {
        if (this.liveCinematics.containsKey(parent.getUniqueId()) || this.isPlayerInCinematic(parent.getUniqueId())) return null;

        LiveCinematicInfo info = new LiveCinematicInfo(parent.getUniqueId());
        this.liveCinematics.put(parent.getUniqueId(), info);
        parent.setGameMode(GameMode.SPECTATOR);
        return info;
    }

    public boolean stopCinematic(Player parent) {
        LiveCinematicInfo info = this.liveCinematics.remove(parent.getUniqueId());
        if (info != null) {
            info.stop();
            return true;
        }
        return false;
    }

    public LiveCinematicInfo getCinematicInfo(Player parent) {
        return this.liveCinematics.get(parent.getUniqueId());
    }

    public List<LiveCinematicInfo> getCinematics() {
        return new ArrayList<>(this.liveCinematics.values());
    }

    public LiveCinematicInfo getCinematicWherePlayerIsIn(Player player) {
        return this.liveCinematics.values().stream().filter(i -> i.getPlayers().contains(player.getUniqueId())).findFirst().orElse(null);
    }

}
