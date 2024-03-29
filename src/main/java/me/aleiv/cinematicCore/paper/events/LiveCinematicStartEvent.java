package me.aleiv.cinematicCore.paper.events;

import lombok.Getter;
import me.aleiv.cinematicCore.paper.objects.LiveCinematicInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LiveCinematicStartEvent extends Event {

    private static final @Getter HandlerList HandlerList = new HandlerList();

    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter LiveCinematicInfo liveCinematicInfo;


    public LiveCinematicStartEvent(LiveCinematicInfo liveCinematicInfo) {
        this.liveCinematicInfo = liveCinematicInfo;
    }

}