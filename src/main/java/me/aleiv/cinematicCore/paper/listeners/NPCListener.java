package me.aleiv.cinematicCore.paper.listeners;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.EquipmentModifier;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.modifier.NPCModifier;
import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.NPCInfo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NPCListener implements Listener {

    private final CinematicTool instance;

    public NPCListener(CinematicTool instance) {
        this.instance = instance;
    }

    @EventHandler
    public void handleNPCShow(PlayerNPCShowEvent event) {
        NPC npc = event.getNPC();
        NPCInfo npcInfo = instance.getGame().getCinematicProgressList().stream()
                .map(c -> c.getSpawnedNpcs().get(npc))
                .filter(Objects::nonNull).findFirst().orElse(null);

        if (npcInfo == null) return;

        List<NPCModifier> npcModifiers = new ArrayList<>();
        npcInfo.getItems().forEach((itemSlot, itemStack) -> {
            if (itemStack == null || itemStack.getType() == Material.AIR) return;
            npcModifiers.add(npc.equipment().queue(EquipmentModifier.CHEST, itemStack));
        });
        npcModifiers.add(npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, npcInfo.isOverlay()));
        npcModifiers.add(npc.rotation().queueRotate(npcInfo.getLocation().getYaw(), npcInfo.getLocation().getPitch()));

        event.send(npcModifiers.toArray(new NPCModifier[npcModifiers.size()]));
    }
}
