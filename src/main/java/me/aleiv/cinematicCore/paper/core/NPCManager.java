package me.aleiv.cinematicCore.paper.core;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.NPCPool;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.modifier.NPCModifier;
import me.aleiv.cinematicCore.paper.objects.NPCInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NPCManager implements Listener {

    private NPCPool npcPool;

    private HashMap<NPC, NPCInfo> npcs;

    public NPCManager(JavaPlugin plugin) {
        this.npcPool = NPCPool.builder(plugin).spawnDistance(224).actionDistance(224).build();

        this.npcs = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public NPC spawnNPC(NPCInfo info) {
        NPC npc = info.createBuilder().build(this.npcPool);
        this.npcs.put(npc, info);
        return npc;
    }

    public void removeNPC(NPC npc) {
        try {
            Scoreboard sc = Bukkit.getScoreboardManager().getMainScoreboard();
            NPCInfo npcInfo = this.npcs.get(npc);
            sc.getTeam(npcInfo.getTeamName()).unregister();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.npcPool.removeNPC(npc.getEntityId());
        this.npcs.remove(npc);
    }

    public NPCInfo getNPCInfo(NPC npc) {
        return this.npcs.get(npc);
    }

    @EventHandler
    public void handleNPCShow(PlayerNPCShowEvent event) {
        NPC npc = event.getNPC();
        NPCInfo npcInfo = this.npcs.get(npc);

        if (npcInfo == null) return;

        List<NPCModifier> npcModifiers = new ArrayList<>();
        npcInfo.getItems().forEach((itemSlot, itemStack) -> {
            if (itemStack == null || itemStack.getType() == Material.AIR) return;
            npcModifiers.add(npc.equipment().queue(itemSlot, itemStack));
        });
        npcModifiers.add(npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, npcInfo.isOverlay()));
        npcModifiers.add(npc.rotation().queueRotate(npcInfo.getLocation().getYaw(), npcInfo.getLocation().getPitch()));

        event.send(npcModifiers.toArray(new NPCModifier[npcModifiers.size()]));
    }

    @EventHandler
    public void onDisable(PluginDisableEvent e) {
        this.npcs.keySet().forEach(this::removeNPC);
        Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
        new ArrayList<>(scoreboard.getTeams()).forEach(team -> {
            if (team.getName().startsWith("sc_npc_")) {
                team.unregister();
            }
        });
    }

}
