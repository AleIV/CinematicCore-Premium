package me.aleiv.core.paper.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import us.jcedeno.libs.Npc;

@Data
public class CinematicProgress {
    List<Cinematic> scenes;
    List<UUID> uuids;
    long startTime = 0;
    BukkitTCT task;

    HashMap<UUID, PlayerInfo> playerInfo = new HashMap<>();
    List<Npc> spawnedNpcs = new ArrayList<>();

    public CinematicProgress(List<Cinematic> scenes, List<UUID> uuids, long startTime, BukkitTCT task){
        this.scenes = scenes;
        this.uuids = uuids;
        this.startTime = startTime;
        this.task = task;
    }

}
