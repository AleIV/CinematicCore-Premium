package me.aleiv.cinematicCore.paper.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.Cinematic;
import me.aleiv.cinematicCore.paper.objects.Frame;
import me.aleiv.cinematicCore.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("cinematic|c")
@CommandPermission("cinematic.cmd")
public class CinematicCMD extends BaseCommand {

    private @NonNull CinematicTool instance;

    public CinematicCMD(CinematicTool instance) {
        this.instance = instance;

        var manager = instance.getCommandManager();

        manager.getCommandCompletions().registerAsyncCompletion("bool", c -> {
            return ImmutableList.of("true", "false");
        });

        manager.getCommandCompletions().registerAsyncCompletion("name", c -> {
            return ImmutableList.of("scene-name");
        });

        manager.getCommandCompletions().registerAsyncCompletion("ticks", c -> {
            return ImmutableList.of("time-in-ticks");
        });

        manager.getCommandCompletions().registerAsyncCompletion("ticksremove", c -> {
            return ImmutableList.of("time-in-ticks-to-remove");
        });

        manager.getCommandCompletions().registerAsyncCompletion("scenes", c -> {
            return ImmutableList.of("scenes...");
        });

        manager.getCommandCompletions().registerAsyncCompletion("newname", c -> {
            return ImmutableList.of("new-scene-name");
        });

        manager.getCommandCompletions().registerAsyncCompletion("event", c -> {
            return ImmutableList.of("command-to-execute");
        });

        
    }

    @Default
    public void info(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic Tool");
        sender.sendMessage(
                ChatColor.WHITE + "/cinematic record <scene name>" + ChatColor.AQUA + " Start a new scene recording.");
        sender.sendMessage(
                ChatColor.WHITE + "/cinematic stop" + ChatColor.AQUA + " Stop and save the current scene record.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic record-static <scene name> <scene duration in ticks>"
                + ChatColor.AQUA + " Record static scene.");
        sender.sendMessage(
                ChatColor.WHITE + "/cinematic play <true for all players/false for sender> <list of scenes>..."
                        + ChatColor.AQUA + " Play final cinematic to sender or all players.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic list" + ChatColor.AQUA + " List of all scenes.");
        sender.sendMessage(
                ChatColor.WHITE + "/cinematic delete <scene to delete>" + ChatColor.AQUA + " Delete a scene.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic merge <new scene> <scene1> <scene2>" + ChatColor.AQUA
                + " Merge 2 scenes.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic clone <scene to clone> <name of clone>" + ChatColor.AQUA
                + " Clone scene.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic add-event <scene> <event>" + ChatColor.AQUA
                + " Add an event to execute in a cinematic.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic remove-event <scene> <event>" + ChatColor.AQUA
                + " Remove an event in a cinematic.");
        sender.sendMessage(
                ChatColor.WHITE + "/cinematic event-list <scene>" + ChatColor.AQUA + " List of events in a cinematic.");

    }

    @Subcommand("stop")
    public void stopRec(Player sender) {

        var game = instance.getGame();
        var recording = game.getRecording();
        var uuid = sender.getUniqueId();

        if (!recording.containsKey(uuid)) {
            sender.sendMessage(ChatColor.RED + "You are not recording.");

        } else {
            game.stopRecord(sender);
            sender.sendMessage(ChatColor.GREEN + "Cinematic recorded and saved.");
        }
    }

    @Subcommand("rec|record")
    @CommandCompletion("@name")
    public void rec(Player sender, String cinematic) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var recording = game.getRecording();

        if (!recording.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Someone is already recording.");

        } else if (cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic already exist.");

        } else {

            var task = new BukkitTCT();

            var count = 3;
            while (count >= 0) {
                final var c = count;

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (c == 0) {
                            sender.sendMessage(ChatColor.DARK_RED + "REC.");
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                            game.startRecord(sender, cinematic);

                        } else {
                            sender.sendMessage(ChatColor.DARK_RED + "" + c);
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        }
                    }

                }, 50 * 20);

                count--;
            }

            task.execute();

        }
    }

    @Subcommand("rec|record")
    @CommandCompletion("@name @ticks")
    public void rec(Player sender, String cinematic, Integer ticks) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var recording = game.getRecording();

        if (!recording.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Someone is already recording.");

        } else if (cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic already exist.");

        } else {
            List<Frame> frames = new ArrayList<>();
            var newCinematic = new Cinematic(cinematic, frames, new HashMap<>());
            game.record(sender, frames, ticks);
            game.getCinematics().put(cinematic, newCinematic);

            instance.updateJson();
        }
    }

    @Subcommand("rec-static|record-static")
    @CommandCompletion("@name @ticks")
    public void recStatic(Player sender, String cinematic, Integer ticks) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var recording = game.getRecording();

        if (!recording.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Someone is already recording.");

        } else if (cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic already exist.");

        } else {

            var task = new BukkitTCT();

            var count = 3;
            while (count >= 0) {
                final var c = count;

                task.addWithDelay(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (c == 0) {
                            sender.sendMessage(ChatColor.DARK_RED + "REC. " + ticks + " ticks.");
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);

                            game.recordStatic(sender, cinematic, ticks);

                        } else {
                            sender.sendMessage(ChatColor.DARK_RED + "" + c);
                            sender.playSound(sender.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                        }
                    }

                }, 50 * 20);

                count--;
            }

            task.execute();

        }
    }

    @Subcommand("play")
    @CommandCompletion("@bool @scenes")
    public void play(CommandSender sender, Boolean bool, String... cinematic) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var allExist = true;

        for (var name : cinematic) {
            if (!cinematics.containsKey(name)) {
                allExist = false;
            }
        }

        if (!allExist) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        } else {
            List<Player> players = new ArrayList<>();
            if (bool) {
                players = Bukkit.getOnlinePlayers().stream().map(player -> (Player) player).toList();
            } else {
                if (sender instanceof Player playerSender) {
                    players.add(playerSender);

                } else {
                    sender.sendMessage(ChatColor.RED + "Sender is not a player.");
                    return;
                }
            }

            var uuids = players.stream().map(player -> player.getUniqueId()).toList();
            game.play(uuids, cinematic);
        }

    }

    @Subcommand("save")
    public void save(CommandSender sender) {
        instance.updateJson();
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic files updated.");

    }

    @Subcommand("globalmute")
    @CommandCompletion("@bool")
    public void globalmute(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setGlobalmute(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic globalmute " + game.getGlobalmute());

    }

    @Subcommand("npcs")
    @CommandCompletion("@bool")
    public void npcs(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setNpcs(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic npcs " + bool);

    }

    @Subcommand("fade")
    @CommandCompletion("@bool")
    public void fade(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setFade(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic fade " + bool);

    }

    @Subcommand("autoHide")
    @CommandCompletion("@bool")
    public void autoHide(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setAutoHide(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic autoHide " + bool);

    }

    @Subcommand("restoreGamemode")
    @CommandCompletion("@bool")
    public void restoreGamemode(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setRestoreGamemode(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic restoreGamemode " + bool);

    }

    @Subcommand("restoreLocation")
    @CommandCompletion("@bool")
    public void restoreLocation(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setRestoreLocation(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic restoreLocation " + bool);

    }

    @Subcommand("hide")
    @CommandCompletion("@bool")
    public void hide(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.hide(bool);
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic hide " + bool);

    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        var game = instance.getGame();
        var cinematics = game.getCinematics().keySet();
        sender.sendMessage(ChatColor.DARK_AQUA + "Scene list: " + ChatColor.WHITE + cinematics.toString());

    }

    @Subcommand("delete")
    @CommandCompletion("@name")
    public void delete(CommandSender sender, String cinematic) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");
        } else {
            cinematics.remove(cinematic);
            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic + " deleted.");

            instance.updateJson();
        }

    }

    @Subcommand("merge")
    @CommandCompletion("@newname @name @name")
    public void merge(CommandSender sender, String name, String cinematic1, String cinematic2) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic1) || !cinematics.containsKey(cinematic2)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        } else {
            var cine1 = cinematics.get(cinematic1);
            var cine2 = cinematics.get(cinematic2);

            var frames1 = cine1.getFrames();
            var frames2 = cine2.getFrames();

            List<Frame> frames = new ArrayList<>();
            frames.addAll(frames1);
            frames.addAll(frames2);

            cinematics.remove(cinematic1);
            cinematics.remove(cinematic2);
            var newCinematic = new Cinematic(name);
            newCinematic.setFrames(frames);
            newCinematic.setTimedEvents(cine1.getTimedEvents());
            cinematics.put(name, newCinematic);

            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic1 + " and " + cinematic2 + " merged.");

            instance.updateJson();

        }

    }

    @Subcommand("clone")
    @CommandCompletion("@name @newname")
    public void clone(CommandSender sender, String cinematic1, String cinematic2) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();

        if (!cinematics.containsKey(cinematic1)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        } else if (cinematics.containsKey(cinematic2)) {
            sender.sendMessage(ChatColor.RED + "Cinematic already exist.");

        } else {
            var cine1 = cinematics.get(cinematic1);

            var list = cine1.getFrames().stream().toList();
            var events = new HashMap<>(cine1.getTimedEvents());
            var newCinematic = new Cinematic(cinematic2);
            newCinematic.setFrames(list);
            newCinematic.setTimedEvents(events);

            cinematics.put(cinematic2, newCinematic);

            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic1 + " cloned in " + cinematic2);

            instance.updateJson();

        }

    }

    @Subcommand("rename")
    @CommandCompletion("@name @newname")
    public void rename(CommandSender sender, String cinematic, String name) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        } else if (cinematics.containsKey(name)) {
            sender.sendMessage(ChatColor.RED + "Cinematic already exist.");

        } else {
            var cine = cinematics.get(cinematic);
            cine.setName(name);

            cinematics.remove(cinematic);
            cinematics.put(name, cine);

            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic + " renamed to " + name);

            instance.updateJson();

        }
    }

    @Subcommand("add-event")
    @CommandCompletion("@name @event")
    public void event(Player sender, String cinematic, String event) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");
        } else {
            var cine = cinematics.get(cinematic);
            var cinematicProgress = game.getCinematicProgress(sender);

            if (cinematicProgress != null) {
                var timedEvents = cine.getTimedEvents();
                var currentTick = cinematicProgress.getTask().getCurrentTask();

                if (timedEvents.containsKey(currentTick)) {
                    var listOfEvents = timedEvents.get(currentTick);
                    listOfEvents.add(event);

                } else {
                    List<String> list = new ArrayList<>();
                    list.add(event);
                    cine.getTimedEvents().put(currentTick, list);
                }

                instance.updateJson();
                sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic event added to " + cinematic + ". Event: " + event);
            } else {
                sender.sendMessage(ChatColor.RED + "You are not in a cinematic.");

            }

        }

    }

    @Subcommand("remove-frames")
    @CommandCompletion("@name @ticksremove")
    public void removeFrames(Player sender, String cinematic, Integer amount) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        } else {
            var cine = cinematics.get(cinematic);
            var frames = cine.getFrames();

            if (frames.size() <= amount) {
                sender.sendMessage(ChatColor.RED + "Frames are not enough.");

            } else {
                for (int i = 0; i < frames.size(); i++) {
                    if(i >= frames.size()-amount){
                        frames.remove(i);
                    }
                }
            }

            instance.updateJson();
            sender.sendMessage(ChatColor.DARK_AQUA + "" + amount + " frames removed from " + cinematic);

        }

    }

    @Subcommand("check-frame")
    public void checkCurrentFrame(Player sender) {
        var game = instance.getGame();
        var cinematicProgress = game.getCinematicProgress(sender);

        if (cinematicProgress != null) {
            var currentTick = cinematicProgress.getTask().getCurrentTask();

            sender.sendMessage(ChatColor.DARK_AQUA + "Current frame: " + currentTick);
        } else {

            sender.sendMessage(ChatColor.RED + "You are not in a cinematic.");
        }

    }

    @Subcommand("add-event-timed")
    @CommandCompletion("@name @ticks @event")
    public void eventTimed(Player sender, String cinematic, Integer currentTick, String event) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        } else {
            var cine = cinematics.get(cinematic);
            var timedEvents = cine.getTimedEvents();

            if (timedEvents.containsKey(currentTick)) {
                var listOfEvents = timedEvents.get(currentTick);
                listOfEvents.add(event);

            } else {
                List<String> list = new ArrayList<>();
                list.add(event);
                cine.getTimedEvents().put(currentTick, list);
            }

            instance.updateJson();
            sender.sendMessage(
                    ChatColor.DARK_AQUA + "Cinematic timed event added to " + cinematic + ". Event: " + event);

        }

    }

    @Subcommand("delete-event")
    @CommandCompletion("@name @ticks")
    public void deleteEvent(CommandSender sender, String cinematic, Integer event) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");
        } else {
            var cine = cinematics.get(cinematic);
            var timedEvents = cine.getTimedEvents();

            if (timedEvents.containsKey(event)) {
                timedEvents.remove(event);

                sender.sendMessage(
                        ChatColor.DARK_AQUA + "Cinematic event removed from " + cinematic + ". Event: " + event);

                instance.updateJson();
            } else {
                sender.sendMessage(ChatColor.RED + "Cinematic event doesn't exist.");

            }

        }

    }

    @Subcommand("event-list")
    @CommandCompletion("@name")
    public void eventList(CommandSender sender, String cinematic) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");
        } else {
            var cine = cinematics.get(cinematic);
            var timedEvents = cine.getTimedEvents();

            sender.sendMessage(
                    ChatColor.DARK_AQUA + cinematic + " events list: " + ChatColor.WHITE + timedEvents.toString());

        }

    }

}
