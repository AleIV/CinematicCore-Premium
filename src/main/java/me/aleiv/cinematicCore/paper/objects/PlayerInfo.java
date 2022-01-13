package me.aleiv.cinematicCore.paper.objects;

import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

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
