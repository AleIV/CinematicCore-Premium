package me.aleiv.cinematicCore.paper.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.github.juliarn.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.aleiv.cinematicCore.paper.CinematicTool;

public class LiveCinematicInfo {

    private CinematicTool instance;

    private final UUID parentUUID;

    private List<UUID> playersInTransit;

    private final List<UUID> players;
    private final HashMap<UUID, NPC> npcsHashMap;
    private final HashMap<UUID, Location> locationsHashMap;
    private final HashMap<UUID, GameMode> gamemodesHashMap;

    private boolean running;

    public LiveCinematicInfo(UUID parentUUID) {
        this.instance = CinematicTool.getInstance();

        this.playersInTransit = new ArrayList<>();

        this.parentUUID = parentUUID;
        this.players = new ArrayList<>();
        this.npcsHashMap = new HashMap<>();
        this.locationsHashMap = new HashMap<>();
        this.gamemodesHashMap = new HashMap<>();

        this.running = true;

        this.registerPlayer(this.getParentPlayer());
    }

    public void addPlayer(Player player, boolean force) {
        if (!this.running || this.playersInTransit.contains(player.getUniqueId()) || player.getUniqueId().equals(parentUUID) || this.players.contains(player.getUniqueId()) || this.instance.getLiveCinematics().isPlayerInCinematic(player.getUniqueId()))
            return;

        if (!force) {
            this.playersInTransit.add(player.getUniqueId());
            this.instance.getGame().sendBlack(player);
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.instance, () -> {
                this.playersInTransit.remove(player.getUniqueId());
                players.add(player.getUniqueId());
                this.registerPlayer(player);
            }, 110L);
        } else {
            players.add(player.getUniqueId());
            this.registerPlayer(player);
        }
    }

    private void registerPlayer(Player player) {
        if (!this.running) return;

        if (this.instance.getGame().getNpcs() && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
            NPCInfo npcInfo = new NPCInfo(player, false, true, false);
            NPC npc = this.instance.getNpcManager().spawnNPC(npcInfo);
            this.npcsHashMap.put(player.getUniqueId(), npc);
        }
        this.locationsHashMap.put(player.getUniqueId(), player.getLocation());
        this.gamemodesHashMap.put(player.getUniqueId(), player.getGameMode());
        player.setGameMode(GameMode.SPECTATOR);
        Player p = this.getParentPlayer();
        if (p != null) {
            player.teleport(p.getLocation());
        }
    }

    public void removePlayer(Player player) {
        if (!this.running || this.playersInTransit.contains(player.getUniqueId())) return;

        this.playersInTransit.add(player.getUniqueId());
        this.instance.getGame().sendBlack(player);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.instance, () -> {
            if (!this.playersInTransit.remove(player.getUniqueId())) return;
            this.forceRemove(player);
        }, 110L);
    }

    public void forceRemove(Player player) {
        this.playersInTransit.remove(player.getUniqueId());
        players.remove(player.getUniqueId());
        NPC npc = npcsHashMap.remove(player.getUniqueId());
        if (npc != null) {
            this.instance.getNpcManager().removeNPC(npc);
        }
        Location loc = this.locationsHashMap.remove(player.getUniqueId());
        if (loc != null) {
            player.teleport(loc);
        }
        GameMode gm = this.gamemodesHashMap.remove(player.getUniqueId());
        if (gm != null) {
            player.setGameMode(gm);
        }
    }

    public boolean isPlayerParent(UUID uuid) {
        return players.contains(uuid);
    }

    public boolean isPlayerInCinematic(UUID uuid) {
        return players.contains(uuid);
    }

    public List<UUID> getPlayers() {
        return new ArrayList<>(players);
    }

    public UUID getParentUUID() {
        return parentUUID;
    }

    public void stop() {
        if (!this.running) return;
        this.running = false;

        this.instance.getGame().sendBlack(this.players);
        this.instance.getGame().sendBlack(List.of(this.parentUUID));
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.instance, () -> {
            forceRemove(this.getParentPlayer());
            this.players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).toList().forEach(this::forceRemove);
            this.players.clear();
            this.npcsHashMap.values().forEach(npc -> this.instance.getNpcManager().removeNPC(npc));
            this.npcsHashMap.clear();
            this.locationsHashMap.clear();
            this.gamemodesHashMap.clear();
        }, 110L);
    }

    public boolean isRunning() {
        return running;
    }

    public Player getParentPlayer() {
        return Bukkit.getPlayer(parentUUID);
    }
}
