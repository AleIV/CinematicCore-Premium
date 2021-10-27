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
    BukkitTCT task;

    HashMap<UUID, PlayerInfo> playerInfo = new HashMap<>();
    List<Npc> spawnedNpcs = new ArrayList<>();

    public CinematicProgress(List<Cinematic> scenes, List<UUID> uuids, BukkitTCT task, Core instance){
        this.scenes = scenes;
        this.uuids = uuids;
        this.task = task;
        this.instance = instance;
    }

    public void checkEvent(){
        var currentTick = task.getCurrentTask();
        var timedEvents = scenes.get(0).getTimedEvents();
        if(timedEvents.containsKey(currentTick)){
            var event = timedEvents.get(currentTick);
            for (var string : event) {
                Bukkit.getScheduler().runTask(instance, task ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string)
                );
            }
        }
    }

    public boolean isViewer(UUID uuid) {
        return uuids.stream().anyMatch(member -> member.getMostSignificantBits() == uuid.getMostSignificantBits());
    }

}
