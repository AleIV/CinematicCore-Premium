package me.aleiv.cinematicCore.paper.objects;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.modifier.EquipmentModifier;
import com.github.juliarn.npc.profile.Profile;
import lombok.Data;
import me.aleiv.cinematicCore.paper.utilities.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

@Data
public class NPCInfo {

    private Profile profile;
    private Location location;
    private boolean overlay;
    private boolean lookAtPlayer;
    private boolean hideNameTag;
    private String teamName;

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
        this.location = LocationUtils.getSafeLocation(player.getLocation().clone());
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
        Profile profile = new Profile(player.getUniqueId());
        profile.complete();

        profile.setName(player.getName());
        profile.setUniqueId(UUID.randomUUID());

        return profile;
    }

    public NPC.Builder createBuilder() {
        this.teamName = "sc_npc_" + UUID.randomUUID().toString().substring(0, 8);
        Scoreboard sc = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
        if (sc != null && this.hideNameTag) {
            Team team = sc.registerNewTeam(teamName);
            team.addEntry(this.profile.getName());
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        return NPC.builder()
                .location(this.location)
                .profile(this.profile)
                .lookAtPlayer(this.lookAtPlayer)
                .imitatePlayer(false);
    }

}
