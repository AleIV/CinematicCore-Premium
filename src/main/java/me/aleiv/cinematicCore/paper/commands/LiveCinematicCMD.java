package me.aleiv.cinematicCore.paper.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.LiveCinematicInfo;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("cinematic|c")
@Subcommand("live")
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

    private Player getPlayer(String playerName) {
        Player player = plugin.getServer().getPlayer(playerName);
        if (player == null) {
            player.sendMessage("§cPlayer not found!");
            return null;
        }
        return player;
    }

    @Subcommand("add-player|add")
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void addPlayer(Player player, String playerName) {
        LiveCinematicInfo info = this.getInfo(player);
        if (info == null) return;
        Player target = this.getPlayer(playerName);
        if (target == null) return;

        if (info.isPlayerInCinematic(target.getUniqueId())) {
            player.sendMessage("§cPlayer is already in the live cinematic!");
            return;
        }
        info.addPlayer(target);
        player.sendMessage("§3Player §e" + target.getName() + " §3added to live cinematic!");
    }

    @Subcommand("add-range")
    @CommandCompletion("@nothing")
    @Syntax("<radius>")
    public void addRange(Player player, int radius) {
        LiveCinematicInfo info = this.getInfo(player);
        if (info == null) return;

        List<Player> players = player.getNearbyEntities(radius, radius, radius).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).toList();
        if (players.isEmpty()) {
            player.sendMessage("§cNo players found in range.");
            return;
        }
        players.forEach(info::addPlayer);
        player.sendMessage("§3Added players in range to live cinematic.");
    }

    @Subcommand("remove")
    @CommandCompletion("@players")
    @Syntax("<player>")
    public void removePlayer(Player player, String playerName) {
        LiveCinematicInfo info = this.getInfo(player);
        if (info == null) return;
        Player target = this.getPlayer(playerName);
        if (target == null) return;

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
