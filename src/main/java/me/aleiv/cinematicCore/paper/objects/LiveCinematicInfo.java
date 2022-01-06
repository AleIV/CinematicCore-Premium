package me.aleiv.cinematicCore.paper.objects;

import com.github.juliarn.npc.NPC;
import me.aleiv.cinematicCore.paper.CinematicTool;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LiveCinematicInfo {

    private CinematicTool instance;

    private final UUID parentUUID;

    private final List<UUID> players;
    private final List<NPC> npcs;
    private final HashMap<UUID, NPC> npcsHashMap;
    private final HashMap<UUID, Location> locationsHashMap;
    private final HashMap<UUID, GameMode> gamemodesHashMap;

    private boolean running;

    public LiveCinematicInfo(UUID parentUUID) {
        this.instance = CinematicTool.getInstance();

        this.parentUUID = parentUUID;
        this.players = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.npcsHashMap = new HashMap<>();
        this.locationsHashMap = new HashMap<>();
        this.gamemodesHashMap = new HashMap<>();

        this.running = true;
    }

    public void addPlayer(Player player) {
        if (player.getUniqueId().equals(parentUUID) || this.players.contains(player.getUniqueId()) || this.instance.getLiveCinematics().isPlayerInCinematic(player.getUniqueId()))
            return;

        players.add(player.getUniqueId());
        NPCInfo npcInfo = new NPCInfo(player);
        NPC npc = npcInfo.createBuilder().build(this.instance.getNpcPool());
        this.npcs.add(npc);
        this.npcsHashMap.put(player.getUniqueId(), npc);
        this.locationsHashMap.put(player.getUniqueId(), player.getLocation());
        this.gamemodesHashMap.put(player.getUniqueId(), player.getGameMode());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        NPC npc = npcsHashMap.remove(player.getUniqueId());
        if (npc != null) {
            this.npcs.remove(npc);
            this.instance.getNpcPool().removeNPC(npc.getEntityId());
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

    public List<NPC> getNpcs() {
        return new ArrayList<>(npcs);
    }

    public UUID getParentUUID() {
        return parentUUID;
    }

    public void stop() {
        this.running = false;
        this.players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(this::removePlayer);
        this.players.clear();
        this.npcs.stream().map(NPC::getEntityId).forEach(this.instance.getNpcPool()::removeNPC);
        this.npcs.clear();
        this.npcsHashMap.clear();
        this.locationsHashMap.clear();
        this.gamemodesHashMap.clear();
    }

    public boolean isRunning() {
        return running;
    }
}
