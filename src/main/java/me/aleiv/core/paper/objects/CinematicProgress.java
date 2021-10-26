package me.aleiv.core.paper.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import lombok.Data;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import us.jcedeno.libs.Npc;

@Data
public class CinematicProgress {
    Core instance;

    List<Cinematic> scenes;
    List<UUID> uuids;
    long startTime = 0;
    BukkitTCT task;

    HashMap<UUID, PlayerInfo> playerInfo = new HashMap<>();
    List<Npc> spawnedNpcs = new ArrayList<>();

    public CinematicProgress(List<Cinematic> scenes, List<UUID> uuids, long startTime, BukkitTCT task, Core instance){
        this.scenes = scenes;
        this.uuids = uuids;
        this.startTime = startTime;
        this.task = task;
        this.instance = instance;
    }

    public Long getCurrentTime(){
        var game = instance.getGame();
        var gameTime = game.getGameTime();
        return gameTime-startTime;
    }

    public void checkEvent(){
        var currentTime = getCurrentTime();
        var timedEvents = scenes.get(0).getTimedEvents();
        if(timedEvents.containsKey(currentTime)){
            var event = timedEvents.get(currentTime);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), event);
        }
    }

    public boolean isViewer(UUID uuid) {
        return uuids.stream().anyMatch(member -> member.getMostSignificantBits() == uuid.getMostSignificantBits());
    }

}
