package me.aleiv.cinematicCore.paper.core;

import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.LiveCinematicInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LiveCinematics implements Listener {

    private CinematicTool instance;

    private ConcurrentHashMap<UUID, LiveCinematicInfo> liveCinematics;

    public LiveCinematics(CinematicTool instance) {
        this.instance = instance;
        this.liveCinematics = new ConcurrentHashMap<>();

        Bukkit.getPluginManager().registerEvents(this, instance);
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> this.liveCinematics.values().stream().filter(info -> !info.isRunning()).forEach(i -> this.liveCinematics.remove(i.getParentUUID())), 0L, 5*10L);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!e.isCancelled() && e.getTo() != null) {
            this.processMoveEvent(e);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (!e.isCancelled() && e.getTo() != null) {
            this.processMoveEvent(e);
        }
    }

    private void processMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        LiveCinematicInfo info = this.liveCinematics.get(player.getUniqueId());
        if (info != null && info.isRunning()) {
            info.getPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.teleport(e.getTo()));
            return;
        }

        this.liveCinematics.values().stream().filter(i -> i.getPlayers().contains(player.getUniqueId())).findFirst().ifPresent(i -> e.setCancelled(true));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        this.liveCinematics.values().stream().filter(i -> i.getPlayers().contains(e.getPlayer().getUniqueId())).findFirst().ifPresent(i -> i.removePlayer(e.getPlayer()));
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

}
