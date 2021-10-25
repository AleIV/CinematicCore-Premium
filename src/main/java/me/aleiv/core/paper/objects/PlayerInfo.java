package me.aleiv.core.paper.objects;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Data;

@Data
public class PlayerInfo {
    UUID uuid;
    GameMode gamemode;
    Location location;

    public PlayerInfo(Player player){
        this.uuid = player.getUniqueId();
        this.gamemode = player.getGameMode();
        this.location = player.getLocation().clone();
    }

    
}
