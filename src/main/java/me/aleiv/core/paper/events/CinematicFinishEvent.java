package me.aleiv.core.paper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import me.aleiv.core.paper.objects.CinematicProgress;

public class CinematicFinishEvent extends Event {
    
    private static final @Getter HandlerList HandlerList = new HandlerList();
    @SuppressWarnings({"java:S116", "java:S1170"})
    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter CinematicProgress cinematicProgress;


    public CinematicFinishEvent(CinematicProgress cinematicProgress, boolean async) {
        super(async);
        this.cinematicProgress = cinematicProgress;
    }

    public CinematicFinishEvent(CinematicProgress cinematicProgress) {
        this(cinematicProgress, false);
    }

}