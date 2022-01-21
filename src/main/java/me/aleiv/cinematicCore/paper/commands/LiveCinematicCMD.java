package me.aleiv.cinematicCore.paper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.LiveCinematicInfo;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("cinematic|c")
@Subcommand("live")
@CommandPermission("cinematic.cmd")
public class LiveCinematicCMD extends BaseCommand {

    private final CinematicTool plugin;

    public LiveCinematicCMD(CinematicTool plugin) {
        this.plugin = plugin;
    }

    @Subcommand("start")
    public void start(Player player) {
        LiveCinematicInfo info = plugin.getLiveCinematics().createCinematic(player);
        if (info == null) {
            player.sendMessage("§cYou are already in a live cinematic!");
            return;
        }
        player.sendMessage("§3You are now in a live cinematic! Do §6/cinematic live add §3to add players.");
    }

    private LiveCinematicInfo getInfo(Player parent) {
        LiveCinematicInfo info = plugin.getLiveCinematics().getCinematicInfo(parent);
        if (info == null) {
            parent.sendMessage("§cYou aren't any parent of a live cinematic!");
            return null;
        }
        return info;
    }

    private Player getPlayer(Player sender, String playerName) {
        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) {
            sender.sendMessage("§cPlayer not found!");
            return null;
        }
        return player;
    }

    @Subcommand("add-player|add")
    @CommandCompletion("@players @bool")
    @Syntax("<player> [force]")
    public void addPlayer(Player player, String playerName, @Optional @Default("false") Boolean force) {
        LiveCinematicInfo info = this.getInfo(player);
        if (info == null) return;
        Player target = this.getPlayer(player, playerName);
        if (target == null) return;

        if (info.getParentUUID().equals(target.getUniqueId())) {
            player.sendMessage("§cYou can't add yourself to a live cinematic!");
            return;
        }
        if (info.isPlayerInCinematic(target.getUniqueId())) {
            player.sendMessage("§cPlayer is already in the live cinematic!");
            return;
        }
        info.addPlayer(target, force);
        player.sendMessage("§3Player §e" + target.getName() + " §3added to live cinematic!");
    }

    @Subcommand("add-range")
    @CommandCompletion("@nothing @bool")
    @Syntax("<radius> [force]")
    public void addRange(Player player, int radius, @Optional @Default("false") Boolean force) {
        LiveCinematicInfo info = this.getInfo(player);
        if (info == null) return;

        List<Player> players = player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).filter(p -> p.getGameMode() != GameMode.SPECTATOR).toList();
        if (players.isEmpty()) {
            player.sendMessage("§cNo players found in range.");
            return;
        }
        players.forEach(p -> info.addPlayer(p, force));
        player.sendMessage("§3Added players in range to live cinematic.");
        StringBuilder builder = new StringBuilder();
        players.forEach(p -> builder.append("§f").append(p.getName()).append("§3, "));
        player.sendMessage("§3Players: " + builder.toString().substring(0, builder.length() - 2));
    }

    @Subcommand("remove")
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void removePlayer(Player player, String playerName) {
        LiveCinematicInfo info = this.getInfo(player);
        if (info == null) return;
        Player target = this.getPlayer(player, playerName);
        if (target == null) return;

        if (info.getParentUUID().equals(target.getUniqueId())) {
            player.sendMessage("§cYou can't remove yourself from a live cinematic!");
            return;
        }
        if (!info.isPlayerInCinematic(target.getUniqueId())) {
            player.sendMessage("§cPlayer is not in the live cinematic!");
            return;
        }
        info.removePlayer(target);
        player.sendMessage("§3Player §e" + target.getName() + " §3removed from live cinematic!");
    }

    @Subcommand("stop")
    public void stop(Player player) {
        if (plugin.getLiveCinematics().stopCinematic(player)) {
            player.sendMessage("§3Stopped live cinematic!");
        } else {
            player.sendMessage("§cYou aren't in a live cinematic!");
        }
    }

}
