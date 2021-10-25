package me.aleiv.core.paper.objects;

import java.util.List;

import org.bukkit.entity.Player;

import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

public class CinematicProgress {
    List<Cinematic> scenes;
    List<Player> players;

    long startTime = 0;
    BukkitTCT task;

    public CinematicProgress(List<Cinematic> scenes, List<Player> players, long startTime, BukkitTCT task){
        this.scenes = scenes;
        this.players = players;
        this.startTime = startTime;
        this.task = task;
    }

}
