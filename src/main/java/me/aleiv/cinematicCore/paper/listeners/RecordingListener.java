package me.aleiv.cinematicCore.paper.listeners;

import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.Frame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class RecordingListener implements Listener {

    CinematicTool instance;

    public RecordingListener(CinematicTool instance) {
        this.instance = instance;
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e){
        var game = instance.getGame();
        var recording = game.getRecording();
        var uuid = e.getPlayer().getUniqueId();
        if (recording.containsKey(uuid)){
            var cinematic = recording.get(uuid);
            var frames = cinematic.getFrames();
            var loc = e.getPlayer().getLocation().clone();
            var frame = new Frame(loc.getWorld().getName().toString(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                    loc.getPitch());
            frames.add(frame);
        }
    }
}
