package me.aleiv.cinematicCore.paper.objects;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.profile.Profile;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class NPCInfo {

    private Profile profile;
    private Location location;
    private boolean overlay;
    private boolean lookAtPlayer;
    private List<ItemStack> items;

    public NPCInfo(Player player) {
        this(player, true, false, new ArrayList<>());
    }

    public NPCInfo(Player player, boolean lookAtPlayer) {
        this(player, true, lookAtPlayer, new ArrayList<>());
    }

    public NPCInfo(Player player, boolean overlay, boolean lookAtPlayer) {
        this(player, overlay, lookAtPlayer, new ArrayList<>());
    }

    public NPCInfo(Player player, boolean overlay, boolean lookAtPlayer, List<ItemStack> items) {
        this.profile = this.createProfile(player);
        this.location = player.getLocation();
        this.overlay = overlay;
        this.lookAtPlayer = lookAtPlayer;
        this.items = items;
    }

    public NPCInfo(Profile profile, Location location, boolean overlay, boolean lookAtPlayer, List<ItemStack> items) {
        this.profile = profile;
        this.location = location;
        this.overlay = overlay;
        this.lookAtPlayer = lookAtPlayer;
        this.items = items;
    }

    private Profile createProfile(Player player) {
        Profile profile = new Profile(player.getName());
        profile.complete();
        profile.setUniqueId(UUID.randomUUID());

        return profile;
    }

    public NPC.Builder createBuilder() {
        return NPC.builder()
                .location(this.location)
                .usePlayerProfiles(true)
                .profile(this.profile)
                .lookAtPlayer(this.lookAtPlayer)
                .imitatePlayer(false)
                .spawnCustomizer((npc, p) -> {
                    this.items.forEach(item -> npc.equipment().queue(getItemSlot(item), item));
                    npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, this.overlay);
                });
    }

    private EnumWrappers.ItemSlot getItemSlot(ItemStack item) {
        if (item.getType().name().contains("HELMET")) {
            return EnumWrappers.ItemSlot.HEAD;
        }
        if (item.getType().name().contains("CHESTPLATE")) {
            return EnumWrappers.ItemSlot.CHEST;
        }
        if (item.getType().name().contains("LEGGINGS")) {
            return EnumWrappers.ItemSlot.LEGS;
        }
        if (item.getType().name().contains("BOOTS")) {
            return EnumWrappers.ItemSlot.FEET;
        }
        if (item.getType().name().contains("SHIELD")) {
            return EnumWrappers.ItemSlot.OFFHAND;
        }
        return EnumWrappers.ItemSlot.MAINHAND;
    }
}
