package me.aleiv.cinematicCore.paper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.aleiv.cinematicCore.paper.objects.CinematicProgress;

public class CinematicTickEvent extends Event {
    
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter CinematicProgress cinematicProgress;


    public CinematicTickEvent(CinematicProgress cinematicProgress, boolean async) {
        super(async);
        this.cinematicProgress = cinematicProgress;
    }

    public CinematicTickEvent(CinematicProgress cinematicProgress) {
        this(cinematicProgress, false);
    }

}