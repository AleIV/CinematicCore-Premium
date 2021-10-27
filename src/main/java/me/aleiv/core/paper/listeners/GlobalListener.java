package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.CinematicFinishEvent;
import me.aleiv.core.paper.events.CinematicStartEvent;
import me.aleiv.core.paper.events.CinematicTickEvent;
import me.aleiv.core.paper.events.TaskChainTickEvent;
import me.aleiv.core.paper.objects.PlayerInfo;

public class GlobalListener implements Listener {

    Core instance;

    public GlobalListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void onCinemaTick(CinematicTickEvent e){
        var cinematicProgress = e.getCinematicProgress();
        cinematicProgress.checkEvent();
    }

    @EventHandler
    public void onTick(TaskChainTickEvent e){
        var game = instance.getGame();
        var cinematicProgressList = game.getCinematicProgressList();

        if (!cinematicProgressList.isEmpty()) {
            var iter = cinematicProgressList.iterator();
            while (iter.hasNext()) {
                var cinematic = iter.next();
                if(cinematic.getTask() == e.getBukkitTCT()){
                    Bukkit.getPluginManager().callEvent(new CinematicTickEvent(cinematic, !Bukkit.isPrimaryThread()));
                }
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var game = instance.getGame();
        var recording = game.getRecording();
        var uuid = e.getPlayer().getUniqueId();
        if (recording.containsKey(uuid)) {
            recording.remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void checkForMute(AsyncChatEvent e) {
        var game = instance.getGame();
        if (game.getGlobalmute() && !e.getPlayer().hasPermission("globalmute.talk")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCinematicStart(CinematicStartEvent e) {
        var game = instance.getGame();
        var cinematic = e.getCinematicProgress();
        var uuids = cinematic.getUuids();

        if (game.getRestoreGamemode() || game.getRestoreLocation()) {
            for (var uuid : uuids) {
                var player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    cinematic.getPlayerInfo().put(uuid, new PlayerInfo(player));
                }
                
            }
        }

        if (game.getNpcs()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.getGameMode() == GameMode.ADVENTURE || player.getGameMode() == GameMode.SURVIVAL) {
                    game.spawnClone(player, cinematic);
                }
            });
        }

        if (game.getAutoHide()) {
            game.hide(true);
        }

        for (var uuid : uuids) {
            var player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.SPECTATOR);
            }

        }

    }

    @EventHandler
    public void onCinematicFinish(CinematicFinishEvent e) {
        var game = instance.getGame();
        var cinematic = e.getCinematicProgress();
        var uuids = cinematic.getUuids();
        var playerInfoList = cinematic.getPlayerInfo();

        if (game.getRestoreGamemode()) {
            for (var uuid : uuids) {
                var player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    var playerInfo = playerInfoList.get(uuid);
                    player.setGameMode(playerInfo.getGamemode());
                }
            }
        }

        if (game.getRestoreLocation()) {
            for (var uuid : uuids) {
                var player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    var playerInfo = playerInfoList.get(uuid);
                    player.teleport(playerInfo.getLocation());
                }
            }
        }

        if (game.getRestoreGamemode() || game.getRestoreLocation()) {
            for (var uuid : uuids) {
                var player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 20, false, false, false));
                }
                
            }
        }

        if (game.getNpcs()) {
            cinematic.getSpawnedNpcs().forEach(npc -> {
                npc.delete();
            });
            cinematic.getSpawnedNpcs().clear();
        }

        if (game.getAutoHide()) {
            game.hide(false);
        }
    }
}
