package me.aleiv.cinematicCore.paper.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import io.github.znetworkw.znpcservers.npc.NPC;
import lombok.Data;
import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.utilities.TCT.BukkitTCT;

@Data
public class CinematicProgress {
    CinematicTool instance;

    List<Cinematic> scenes;
    List<UUID> uuids;
    BukkitTCT task;

    HashMap<UUID, PlayerInfo> playerInfo = new HashMap<>();
    List<NPC> spawnedNpcs = new ArrayList<>();

    public CinematicProgress(List<Cinematic> scenes, List<UUID> uuids, BukkitTCT task, CinematicTool instance){
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
            try {
                for (var string : event) {
                    System.out.println("TRYING " + event);
                    
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
                }   
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }
    }

    public boolean isViewer(UUID uuid) {
        return uuids.stream().anyMatch(member -> member.getMostSignificantBits() == uuid.getMostSignificantBits());
    }

}
