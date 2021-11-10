package me.aleiv.cinematicCore.paper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.znetworkw.znpcservers.NPCLibrary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aleiv.cinematicCore.paper.events.CinematicFinishEvent;
import me.aleiv.cinematicCore.paper.events.CinematicStartEvent;
import me.aleiv.cinematicCore.paper.listeners.RecordingListener;
import me.aleiv.cinematicCore.paper.objects.Cinematic;
import me.aleiv.cinematicCore.paper.objects.CinematicProgress;
import me.aleiv.cinematicCore.paper.objects.Frame;
import me.aleiv.cinematicCore.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@Data
@EqualsAndHashCode(callSuper = false)
public class Game{
    CinematicTool instance;

    Boolean globalmute = false;
    Boolean npcs = false;
    Boolean fade = true;
    Boolean autoHide = false;
    Boolean restoreLocation = true;
    Boolean restoreGamemode = true;

    RecordingListener recordingListener;

    HashMap<String, Cinematic> cinematics = new HashMap<>();
    HashMap<UUID, Cinematic> recording = new HashMap<>();
    List<CinematicProgress> cinematicProgressList = new ArrayList<>();


    public Game(CinematicTool instance){
        this.instance = instance;

        recordingListener = new RecordingListener(instance);
    }

    public void record(Player player, List<Frame> frames, int seconds) {
        var task = new BukkitTCT();

        var count = 0;
        for (int i = 0; i < seconds; i++) {
            for (int j = 0; j < 20; j++) {

                var c = (int) count / 20;
                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        instance.sendActionBar(player, ChatColor.YELLOW + "" + c + "/" + seconds);
                        var loc = player.getLocation().clone();
                        var frame = new Frame(loc.getWorld().getName().toString(), loc.getX(), loc.getY(), loc.getZ(),
                                loc.getYaw(), loc.getPitch());
                        frames.add(frame);
                    }

                }, 50);

                count++;
            }

        }

        task.execute();
    }

    public void play(List<UUID> uuids, String... cinematic) {

        var task = new BukkitTCT();

        List<Integer> list = new ArrayList<>();

        if(fade){
            sendBlack();

            task.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    
                }
    
            }, 50 * 110);

        }

        var scenes = Arrays.asList(cinematic).stream().map(name -> cinematics.get(name)).toList();
        var actualCinematic = new CinematicProgress(scenes, uuids, task, instance);
        cinematicProgressList.add(actualCinematic);

        task.add(new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new CinematicStartEvent(actualCinematic, false));
            }
            
        });

        for (var str : cinematic) {
            var cine = cinematics.get(str);
            var frames = cine.getProlongedFrames();

            var c = 0;
            for (var frame : frames) {
                var world = Bukkit.getWorld(frame.getWorld());
                var loc = new Location(world, frame.getX(), frame.getY(), frame.getZ(), frame.getYaw(),
                        frame.getPitch());

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        uuids.forEach(uuid -> {
                            var player = Bukkit.getPlayer(uuid);
                            if(player != null){
                                player.teleport(loc);
                            }
                        });
                    }

                }, 50);
                c++;
            }
            list.add(c);

        }

        if(fade){
            var task2 = new BukkitTCT();

            for (var integer : list) {
                task2.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendBlack();
                    }
    
                }, ((50 * integer) + 50 * 110) - (50 * 110));
            }
    
            task2.execute();
        }

        var completable = task.execute();

        completable.thenAccept(bool -> {
            cinematicProgressList.remove(actualCinematic);
            Bukkit.getScheduler().runTask(instance, Btask ->{
                Bukkit.getPluginManager().callEvent(new CinematicFinishEvent(actualCinematic, false));
            });

        });

    }

    public void spawnClone(Player player, CinematicProgress cinematic){
        var loc = player.getLocation();
        var npc = NPCLibrary.createPlayerNPC(loc, player.getName(), false, player.getInventory(), player);
        npc.getViewers().forEach(npc::spawn);
        

        cinematic.getSpawnedNpcs().add(npc);
    }

    public void startRecord(Player player, String cinematic) {
        recording.put(player.getUniqueId(), new Cinematic(cinematic));
        instance.registerListener(recordingListener);
    }

    public void stopRecord(Player player) {
        instance.unregisterListener(recordingListener);

        var uuid = player.getUniqueId();
        var cinematic = recording.get(uuid);
        recording.remove(uuid);
        cinematics.put(cinematic.getName(), cinematic);

        instance.updateJson();
    }

    public void recordStatic(Player player, String cinematic, Integer ticks) {
        List<Frame> frames = new ArrayList<>();
        var loc = player.getLocation();

        for (int i = 0; i < ticks; i++) {

            var frame = new Frame(loc.getWorld().getName().toString(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
                    loc.getPitch());
            frames.add(frame);
        }

        var newCinematic = new Cinematic(cinematic);
        newCinematic.setFrames(frames);

        cinematics.put(cinematic, newCinematic);

        instance.updateJson();
    }

    public CinematicProgress getCinematicProgress(Player player){
        return cinematicProgressList.stream().filter(cine -> cine.isViewer(player.getUniqueId())).findAny().orElse(null);
    }

    public void sendBlack() {
        String black = Character.toString('\u3400');
        Bukkit.getOnlinePlayers().forEach(p -> {
            instance.showTitle(p, black, "", 100, 20, 100);
        });
    }

    public void hide(boolean bool) {

        if (bool) {
            Bukkit.getOnlinePlayers().forEach(p1 -> {
                Bukkit.getOnlinePlayers().forEach(p2 -> {
                    p1.hidePlayer(instance, p2);
                });
            });
        } else {
            Bukkit.getOnlinePlayers().forEach(p1 -> {
                Bukkit.getOnlinePlayers().forEach(p2 -> {
                    p1.showPlayer(instance, p2);
                });
            });
        }

    }

    

}