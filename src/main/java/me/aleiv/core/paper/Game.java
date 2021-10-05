package me.aleiv.core.paper;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aleiv.core.paper.objects.Cinematic;
import me.aleiv.core.paper.objects.Frame;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;

@Data
@EqualsAndHashCode(callSuper = false)
public class Game{
    Core instance;

    Boolean globalmute = false;

    HashMap<String, Cinematic> cinematics = new HashMap<>();
    HashMap<UUID, Cinematic> recording = new HashMap<>();
    
    public Game(Core instance) {
        this.instance = instance;
    }

    public void sendBlack(){
        String black = Character.toString('\u3400');
        Bukkit.getOnlinePlayers().forEach(p ->{
            instance.showTitle(p, black, "", 100, 20, 100);
        });
    }

    public void hide(boolean bool){

        if(bool){
            Bukkit.getOnlinePlayers().forEach(p1 ->{
                Bukkit.getOnlinePlayers().forEach(p2 ->{
                    p1.hidePlayer(instance, p2);
                });
            });
        }else{
            Bukkit.getOnlinePlayers().forEach(p1 ->{
                Bukkit.getOnlinePlayers().forEach(p2 ->{
                    p1.showPlayer(instance, p2);
                });
            });
        }

    }

    public BukkitTCT play(BukkitTCT task, List<Player> players, List<Frame> frames) {

        frames.forEach(frame -> {
            var world = Bukkit.getWorld(frame.getWorld());
            var loc = new Location(world, frame.getX(), frame.getY(), frame.getZ(), frame.getYaw(), frame.getPitch());

            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    players.forEach(p -> {
                        p.teleport(loc);
                    });
                }

            }, 50);
        });

        return task;

    }


}