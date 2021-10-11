package me.aleiv.core.paper.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import me.aleiv.core.paper.Core;
import me.aleiv.core.paper.objects.Cinematic;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("cinematic|c")
@CommandPermission("cinematic.cmd")
public class CinematicCMD extends BaseCommand {

    private @NonNull Core instance;

    public CinematicCMD(Core instance) {
        this.instance = instance;

    }

    @Subcommand("stop")
    public void stopRec(Player sender) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var recording = game.getRecording();
        var uuid = sender.getUniqueId();

        if (!recording.containsKey(uuid)) {
            sender.sendMessage(ChatColor.RED + "You are not recording.");

        } else {
            var cinematic = recording.get(uuid);
            recording.remove(uuid);
            cinematics.put(cinematic.getName(), cinematic);
            sender.sendMessage(ChatColor.GREEN + "Cinematic recorded and saved.");
        }
    }

    @Subcommand("rec-add")
    public void recAdd(Player sender, String cinematic) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();

        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

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
                            var cine = cinematics.get(cinematic);
                            game.getRecording().put(sender.getUniqueId(), cine);

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

    @Subcommand("rec")
    public void rec(Player sender, String cinematic) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();

        if (cinematics.containsKey(cinematic)) {
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
                            game.getRecording().put(sender.getUniqueId(), new Cinematic(cinematic));

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

    @Subcommand("play-all")
    public void playALL(CommandSender sender, String... cinematic) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();
        var task = new BukkitTCT();
        game.hide(true);

        List<Integer> list = new ArrayList<>();

        game.sendBlack();
        task.addWithDelay(new BukkitRunnable() {
            @Override
            public void run() {

            }

        }, 50 * 110);

        var players = Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList();
        players.forEach(p -> p.setGameMode(GameMode.SPECTATOR));

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
                        players.forEach(p -> {
                            p.teleport(loc);
                        });
                    }

                }, 50);
                c++;
            }
            list.add(c);

        }

        var task2 = new BukkitTCT();

        for (var integer : list) {
            task2.addWithDelay(new BukkitRunnable() {
                @Override
                public void run() {
                    game.sendBlack();
                }

            }, ((50 * integer) + 50 * 110) - (50 * 110));
        }

        task.execute();
        task2.execute();

    }

    @Subcommand("globalmute")
    public void globalmute(CommandSender sender) {
        var game = instance.getGame();
        game.setGlobalmute(!game.getGlobalmute());
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic globalmute " + game.getGlobalmute());

    }

    @Subcommand("hide")
    public void hide(CommandSender sender, Boolean bool) {
        var game = instance.getGame();
        game.hide(bool);
        instance.adminMessage(ChatColor.DARK_AQUA + "Cinematic hide " + bool);

    }

    @Subcommand("play")
    public void playCinematic(CommandSender sender, String cinematic) {

        var game = instance.getGame();
        var cinematics = game.getCinematics();

        if (!cinematics.containsKey(cinematic)) {
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");

        } else {
            var cine = cinematics.get(cinematic);
            var frames = cine.getProlongedFrames();
            var players = Bukkit.getOnlinePlayers().stream().map(p -> (Player) p).toList();
            players.forEach(p -> p.setGameMode(GameMode.SPECTATOR));
            game.play(new BukkitTCT(), players, frames).execute();

        }

    }

    @Subcommand("delete")
    public void delete(CommandSender sender, String cinematic) {
        var game = instance.getGame();
        var cinematics = game.getCinematics();
        if(!cinematics.containsKey(cinematic)){
            sender.sendMessage(ChatColor.RED + "Cinematic doesn't exist.");
        }else{
            cinematics.remove(cinematic);
            sender.sendMessage(ChatColor.DARK_AQUA + "Cinematic " + cinematic + " deleted.");
        }

    }

}
