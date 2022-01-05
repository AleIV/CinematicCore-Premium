package me.aleiv.cinematicCore.paper.objects;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.event.PlayerNPCShowEvent;
import com.github.juliarn.npc.modifier.EquipmentModifier;
import com.github.juliarn.npc.modifier.LabyModModifier;
import com.github.juliarn.npc.modifier.MetadataModifier;
import com.github.juliarn.npc.profile.Profile;
import lombok.Data;
import me.aleiv.cinematicCore.paper.CinematicTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

@Data
public class NPCInfo {

    private Profile profile;
    private Location location;
    private boolean overlay;
    private boolean lookAtPlayer;
    private boolean hideNameTag;

    private HashMap<Integer, ItemStack> items;

    public NPCInfo(Player player) {
        this(player, false, true, true);
    }

    public NPCInfo(Player player, boolean lookAtPlayer) {
        this(player, lookAtPlayer, true, true);
    }

    public NPCInfo(Player player, boolean lookAtPlayer, boolean hideNameTag) {
        this(player, true, lookAtPlayer, hideNameTag);
    }

    public NPCInfo(Player player, boolean lookAtPlayer, boolean overlay, boolean hideNameTag) {
        this.profile = this.createProfile(player);
        this.location = player.getLocation();
        this.overlay = overlay;
        this.lookAtPlayer = lookAtPlayer;
        this.hideNameTag = hideNameTag;

        this.items = new HashMap<>();
        this.items.put(EquipmentModifier.HEAD, player.getInventory().getHelmet());
        this.items.put(EquipmentModifier.CHEST, player.getInventory().getChestplate());
        this.items.put(EquipmentModifier.LEGS, player.getInventory().getLeggings());
        this.items.put(EquipmentModifier.FEET, player.getInventory().getBoots());
        this.items.put(EquipmentModifier.OFFHAND, player.getInventory().getItemInOffHand());
        this.items.put(EquipmentModifier.MAINHAND, player.getInventory().getItemInMainHand());
    }

    private Profile createProfile(Player player) {
        Profile profile = new Profile(player.getName());
        profile.complete();
        profile.setUniqueId(UUID.randomUUID());

        return profile;
    }

    public NPC.Builder createBuilder() {
        String teamName = "sc_npc_" + UUID.randomUUID().toString().substring(0, 8);
        final MetadataModifier.EntityMetadata<Boolean, Byte> SKIN_LAYERS = new MetadataModifier.EntityMetadata<>(
                10,
                Byte.class,
                Arrays.asList(9, 9, 10, 14, 14, 15, 17),
                input -> (byte) (input ? 0xff : 0)
        );

        return NPC.builder()
                .location(this.location)
                .usePlayerProfiles(true)
                .profile(this.profile)
                .lookAtPlayer(this.lookAtPlayer)
                .imitatePlayer(false)
                .spawnCustomizer((npc, p) -> {
                    if (this.hideNameTag) {
                        if (p.getScoreboard().getTeam(teamName) == null) {
                            Team team = p.getScoreboard().registerNewTeam(teamName);
                            team.addEntry(npc.getProfile().getName());
                            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                        }
                    }
                });
    }

}