package me.aleiv.cinematicCore.paper.objects;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.profile.Profile;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

@Data
public class NPCInfo {

    private Profile profile;
    private Location location;
    private boolean overlay;
    private boolean lookAtPlayer;

    private HashMap<EnumWrappers.ItemSlot, ItemStack> items;

    public NPCInfo(Player player) {
        this(player, true, false);
    }

    public NPCInfo(Player player, boolean lookAtPlayer) {
        this(player, true, lookAtPlayer);
    }

    public NPCInfo(Player player, boolean overlay, boolean lookAtPlayer) {
        this.profile = this.createProfile(player);
        this.location = player.getLocation();
        this.overlay = overlay;
        this.lookAtPlayer = lookAtPlayer;

        this.items.put(EnumWrappers.ItemSlot.HEAD, player.getInventory().getHelmet());
        this.items.put(EnumWrappers.ItemSlot.CHEST, player.getInventory().getChestplate());
        this.items.put(EnumWrappers.ItemSlot.LEGS, player.getInventory().getLeggings());
        this.items.put(EnumWrappers.ItemSlot.FEET, player.getInventory().getBoots());
        this.items.put(EnumWrappers.ItemSlot.OFFHAND, player.getInventory().getItemInOffHand());
        this.items.put(EnumWrappers.ItemSlot.MAINHAND, player.getInventory().getItemInMainHand());
    }

    public NPCInfo(Profile profile, Location location, boolean overlay, boolean lookAtPlayer) {
        this.profile = profile;
        this.location = location;
        this.overlay = overlay;
        this.lookAtPlayer = lookAtPlayer;
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
                    this.items.forEach((slot, item) -> {
                        if (item != null) {
                            npc.equipment().queue(slot, item);
                        }
                    });

                    npc.metadata().queue(MetadataModifier.EntityMetadata.SKIN_LAYERS, this.overlay);
                });
    }

}
