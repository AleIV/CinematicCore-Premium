package me.aleiv.cinematicCore.paper.events;

import lombok.Getter;
import me.aleiv.cinematicCore.paper.objects.LiveCinematicInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class LiveCinematicPlayerRegisterEvent extends PlayerEvent {

    private static final @Getter HandlerList HandlerList = new HandlerList();

    private final @Getter HandlerList Handlers = HandlerList;
    private final @Getter LiveCinematicInfo liveCinematicInfo;
    private final @Getter Player player;


    public LiveCinematicPlayerRegisterEvent(Player player, LiveCinematicInfo liveCinematicInfo) {
        super(player);
        this.liveCinematicInfo = liveCinematicInfo;
        this.player = player;
    }

}