package me.aleiv.cinematicCore.paper.events;

import lombok.Getter;
import me.aleiv.cinematicCore.paper.utilities.TCT.BukkitTCT;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TaskChainTickEvent extends Event {
    
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter BukkitTCT bukkitTCT;


    public TaskChainTickEvent(BukkitTCT bukkitTCT, boolean async) {
        super(async);
        this.bukkitTCT = bukkitTCT;
    }

    public TaskChainTickEvent(BukkitTCT bukkitTCT) {
        this(bukkitTCT, false);
    }

}