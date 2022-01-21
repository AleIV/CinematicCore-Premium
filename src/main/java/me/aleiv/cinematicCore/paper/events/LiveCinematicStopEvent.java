package me.aleiv.cinematicCore.paper.events;

import lombok.Getter;
import me.aleiv.cinematicCore.paper.objects.LiveCinematicInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LiveCinematicStopEvent extends Event {

    private static final @Getter HandlerList HandlerList = new HandlerList();

    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter LiveCinematicInfo liveCinematicInfo;


    public LiveCinematicStopEvent(LiveCinematicInfo liveCinematicInfo) {
        super();
        this.liveCinematicInfo = liveCinematicInfo;
    }

}