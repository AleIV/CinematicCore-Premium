package me.aleiv.core.paper.objects;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Data;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

@Data
public class CinematicProgress {
    List<Cinematic> scenes;
    List<Player> players;
    long startTime = 0;
    BukkitTCT task;

    HashMap<UUID, PlayerInfo> playerInfo = new HashMap<>();

    public CinematicProgress(List<Cinematic> scenes, List<Player> players, long startTime, BukkitTCT task){
        this.scenes = scenes;
        this.players = players;
        this.startTime = startTime;
        this.task = task;
    }

}
