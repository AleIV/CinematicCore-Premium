package me.aleiv.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.objects.GameTickEvent;

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

}
