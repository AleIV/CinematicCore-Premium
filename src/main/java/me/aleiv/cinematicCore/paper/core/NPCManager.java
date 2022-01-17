package me.aleiv.cinematicCore.paper.core;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.modifier.NPCModifier;
import me.aleiv.cinematicCore.paper.files.DataFile;
import me.aleiv.cinematicCore.paper.objects.NPCInfo;
import me.aleiv.cinematicCore.paper.utilities.ScoreboardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class NPCManager implements Listener {

    private NPCPool npcPool;

    private DataFile dataFile;

    private HashMap<NPC, NPCInfo> npcs;
    private HashMap<UUID, NPCInfo> npcsByUUID;
    private HashMap<UUID, Team> teams;

    public NPCManager(JavaPlugin plugin) {
        this.npcPool = NPCPool.builder(plugin).spawnDistance(224).actionDistance(224).build();

        this.dataFile = new DataFile(plugin);

        this.npcs = new HashMap<>();
        this.npcsByUUID = new HashMap<>();
        this.teams = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);

        Bukkit.getScheduler().runTask(plugin, this::load);
    }

    private void load() {
        List<NPCInfo> npcs = this.dataFile.getAllNPCs();

        npcs.forEach(info -> {
            NPC npc = this.spawnNPC(info);
            this.npcs.put(npc, info);
            this.npcsByUUID.put(info.getUuid(), info);

            info.setCache(true);
        });
    }

    private void saveNPCs() {
        List<NPCInfo> savedNPCs = new ArrayList<>();
        this.npcs.values().forEach(info -> {
            if (info.isCache()) {
                this.dataFile.saveNpc(info);
                savedNPCs.add(info);
            }
        });

        this.dataFile.getAllUUIDs().stream().filter(uuid -> savedNPCs.stream().noneMatch(info -> info.getUuid().equals(uuid))).map(uuid -> this.npcsByUUID.get(uuid)).filter(Objects::nonNull).forEach(info -> this.dataFile.removeNPC(info));
    }

    public NPC spawnNPC(NPCInfo info) {
        NPC npc = info.createBuilder().build(this.npcPool);
        this.npcs.put(npc, info);
        this.npcsByUUID.put(info.getUuid(), info);

        if (info.isHideNameTag()) {
            Player player = Bukkit.getPlayer(info.getProfile().getName());
            if (player != null) {
                this.teams.put(player.getUniqueId(), ScoreboardUtils.getPlayerTeam(player));
            }
            ScoreboardUtils.createNametagTeam(info.getProfile().getName(), info.getTeamName());
        }

        return npc;
    }

    public void removeNPC(NPC npc) {
        NPCInfo npcInfo = this.npcs.get(npc);
        ScoreboardUtils.removeNametagTeam(npcInfo.getTeamName());

        if (npcInfo.isHideNameTag()) {
            Player player = Bukkit.getPlayer(npcInfo.getProfile().getName());
            if (player != null) {
                Team team = this.teams.remove(player.getUniqueId());
                if (team != null) {
                    ScoreboardUtils.changePlayerTeam(player, team);
                }
            }
        }

        this.npcsByUUID.remove(npcInfo.getUuid());
        this.npcPool.removeNPC(npc.getEntityId());
        this.npcs.remove(npc);
    }

    public void removeNPC(NPCInfo npcInfo) {
        ScoreboardUtils.removeNametagTeam(npcInfo.getTeamName());

        // reverse lookup for npc
        for (Map.Entry<NPC, NPCInfo> entry : new HashMap<>(this.npcs).entrySet()) {
            if (entry.getValue().equals(npcInfo)) {
                this.npcPool.removeNPC(entry.getKey().getEntityId());
                this.npcs.remove(entry.getKey());
                this.npcsByUUID.remove(npcInfo.getUuid());
            }
        }
    }

    public NPCInfo getNPCInfo(NPC npc) {
        return this.npcs.get(npc);
    }

    public NPCInfo getNPCInfo(UUID uuid) {
        return this.npcsByUUID.get(uuid);
    }

    @EventHandler
    public void handleNPCShow(PlayerNPCShowEvent event) {
        NPC npc = event.getNPC();
        NPCInfo npcInfo = this.npcs.get(npc);

        if (npcInfo == null) return;

        List<NPCModifier> npcModifiers = new ArrayList<>();
        npcInfo.getNPCItems().getItems().forEach((itemSlot, itemStack) -> {
            if (itemStack == null || itemStack.getType() == Material.AIR) return;
            npcModifiers.add(npc.equipment().queue(itemSlot, itemStack));
        });
        npcModifiers.add(npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, npcInfo.isOverlay()));
        npcModifiers.add(npc.rotation().queueRotate(npcInfo.getLocation().getYaw(), npcInfo.getLocation().getPitch()));

        event.send(npcModifiers.toArray(new NPCModifier[npcModifiers.size()]));
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        this.saveNPCs();
        new ArrayList<>(this.npcs.keySet()).forEach(this::removeNPC);
        ScoreboardUtils.removeAllTeams();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        this.npcsByUUID.values().forEach(info -> {
            if (info.getProfile().getName().equalsIgnoreCase(player.getName()) && info.isHideNameTag()) {
                Team team = this.teams.remove(player.getUniqueId());

                if (team != null) {
                    ScoreboardUtils.changePlayerTeam(player, team);
                }
            }
        });
    }

    public List<NPCInfo> getNPCs() {
        return new ArrayList<>(this.npcs.values());
    }

}
