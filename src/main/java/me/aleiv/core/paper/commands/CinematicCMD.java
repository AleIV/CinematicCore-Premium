package me.aleiv.core.paper.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.objects.Cinematic;
import me.aleiv.core.paper.objects.Frame;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("cinematic|c")
@CommandPermission("cinematic.cmd")
public class CinematicCMD extends BaseCommand {

    private @NonNull Core instance;

    public CinematicCMD(Core instance) {
        this.instance = instance;

    }

    @Default
    public void info(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic Tool");
        sender.sendMessage(
                ChatColor.WHITE + "/cinematic record <scene name>" + ChatColor.AQUA + " Start a new scene recording.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic stop" + ChatColor.AQUA + " Stop and save the current scene record.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic record-static <scene name> <scene duration in ticks>"
                + ChatColor.AQUA + " Record static scene.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic play <true for all players/false for sender> <list of scenes>..." + ChatColor.AQUA
                + " Play final cinematic to sender or all players.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic list" + ChatColor.AQUA + " List of all scenes.");
        sender.sendMessage(
                ChatColor.WHITE + "/cinematic delete <scene to delete>" + ChatColor.AQUA + " Delete a scene.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic merge <new scene> <scene1> <scene2>"
                + ChatColor.AQUA + " Merge 2 scenes.");
        sender.sendMessage(ChatColor.WHITE + "/cinematic clone <scene to clone> <name of clone>" + ChatColor.AQUA
                + " Clone scene.");
        
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

    @Subcommand("rec-static|record-static")
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
    public void play(Player sender, Boolean bool, String... cinematic) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var allExist = true;

        for (var name : cinematic) {
            if(!cinematics.containsKey(name)){
                allExist = false;
            }
        }

        if(!allExist){
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        }else{
            List<Player> players = new ArrayList<>();
            if(bool){
                players = Bukkit.getOnlinePlayers().stream().map(player -> (Player) player).toList();
            }else{
                players.add(sender);
            }

            game.play(players, cinematic);
        }
        
    }

    @Subcommand("globalmute")
    public void globalmute(CommandSender sender) {
        var game = instance.getGame();
        game.setGlobalmute(!game.getGlobalmute());
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic globalmute " + game.getGlobalmute());

    }

    @Subcommand("npcs")
    public void npcs(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setNpcs(bool);
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic npcs " + bool);

    }

    @Subcommand("fade")
    public void fade(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setFade(bool);
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic fade " + bool);

    }

    @Subcommand("autoHide")
    public void autoHide(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setAutoHide(bool);
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic autoHide " + bool);

    }

    @Subcommand("restorePlayerInfo")
    public void restorePlayerInfo(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.setRestorePlayerInfo(bool);
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic restorePlayerInfo " + bool);

    }

    @Subcommand("hide")
    public void hide(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.hide(bool);
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic hide " + bool);

    }

    @Subcommand("list")
    public void list(CommandSender sender) {
        var game = instance.getGame();
        var cinematics = game.getCinematics().keySet();
        sender.sendMessage(ChatColor.DARK_AQUA + "Scene list: " + ChatColor.WHITE + cinematics.toString());

    }

    @Subcommand("delete")
    public void delete(CommandSender sender, String cinematic) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");
        } else {
            cinematics.remove(cinematic);
            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic + " deleted.");
        }

    }

    @Subcommand("merge")
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
            cinematics.put(name, new Cinematic(name, frames));

            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic1 + " and " + cinematic2 + " merged.");

        }

    }

    @Subcommand("clone")
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
            var cine2 = new Cinematic(cinematic2, list);

            cinematics.put(cinematic2, cine2);

            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic1 + " cloned in " + cinematic2);

        }

    }

    @Subcommand("rename")
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

        }
    }

}
