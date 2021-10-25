package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.events.CinematicFinishEvent;
import me.aleiv.core.paper.events.CinematicStartEvent;
import me.aleiv.core.paper.events.GameTickEvent;
import me.aleiv.core.paper.objects.PlayerInfo;

public class GlobalListener implements Listener {

    Core instance;

    public GlobalListener(Core instance) {
        this.instance = instance;
    }

    @EventHandler
    public void tickEvent(GameTickEvent e){
        var game = instance.getGame();
        var cinematicProgressList = game.getCinematicProgressList();

        Bukkit.getScheduler().runTask(instance, task->{
            if(!cinematicProgressList.isEmpty()){
                
            }
        });
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
    public void onCinematicStart(CinematicStartEvent e){
        var game = instance.getGame();
        var cinematic = e.getCinematicProgress();

        if(game.getRestorePlayerInfo()){
            var players = cinematic.getPlayers();
            for (var player : players) {
                var uuid = player.getUniqueId();
                cinematic.getPlayerInfo().put(uuid, new PlayerInfo(player));
            }
            
        }

        if(game.getNpcs()){

        }

        if(game.getAutoHide()){
            game.hide(true);
        }

    }

    @EventHandler
    public void onCinematicFinish(CinematicFinishEvent e){
        var game = instance.getGame();
        var cinematic = e.getCinematicProgress();
        var players = cinematic.getPlayers();

        if(game.getRestorePlayerInfo()){
            var playerInfoList = cinematic.getPlayerInfo();
            for (var player : players) {
                var uuid = player.getUniqueId();
                var playerInfo = playerInfoList.get(uuid);
                
                player.teleport(playerInfo.getLocation());
                player.setGameMode(playerInfo.getGamemode());
            }
        }

        if(game.getNpcs()){

        }

        if(game.getAutoHide()){
            game.hide(false);
        }

        for (var player : players) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }


}
