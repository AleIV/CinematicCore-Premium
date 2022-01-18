package me.aleiv.cinematicCore.paper.utilities;

import me.aleiv.cinematicCore.paper.CinematicTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class ScoreboardUtils {

    public static void createNametagTeam(String npcName, String teamName) {
        Bukkit.getScheduler().runTaskAsynchronously(CinematicTool.getInstance(), () -> {
            Scoreboard sc = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
            if (sc != null) {
                Team team = sc.registerNewTeam(teamName);
                team.addEntry(npcName);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            }
        });
    }

    public static void removeNametagTeam(String teamName) {
        Bukkit.getScheduler().runTaskAsynchronously(CinematicTool.getInstance(), () -> {
            ScoreboardManager scm = Bukkit.getScoreboardManager();
            if (scm == null) return;

            Scoreboard sc = scm.getMainScoreboard();

            Team team = sc.getTeam(teamName);
            if (team != null && team.getName().equals("sc_npc_")) {
                team.unregister();
            }
        });
    }

    public static void removeAllTeams() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
        if (scoreboard == null) return;

        new ArrayList<>(scoreboard.getTeams()).forEach(team -> {
            if (team.getName().startsWith("sc_npc_")) {
                team.unregister();
            }
        });
    }

    public static Team getPlayerTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
        if (scoreboard == null) return null;

        return scoreboard.getEntryTeam(player.getName());
    }

    public static void changePlayerTeam(Player player, Team team) {
        Bukkit.getScheduler().runTaskAsynchronously(CinematicTool.getInstance(), () -> {
            Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
            if (scoreboard == null) return;

            Team oldTeam = scoreboard.getEntryTeam(player.getName());
            if (oldTeam != null) {
                oldTeam.removeEntry(player.getName());
            }

            team.addEntry(player.getName());
        });
    }

}
