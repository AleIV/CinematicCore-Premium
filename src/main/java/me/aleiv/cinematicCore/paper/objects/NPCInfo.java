package me.aleiv.cinematicCore.paper.objects;

import com.github.juliarn.npc.NPC;
import com.github.juliarn.npc.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import me.aleiv.cinematicCore.paper.files.BasicLocation;
import me.aleiv.cinematicCore.paper.utilities.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class NPCInfo {

    @Getter private final UUID uuid;

    private Profile profile;
    private BasicLocation location;
    @Getter private boolean overlay;
    @Getter private boolean lookAtPlayer;
    @Getter private boolean hideNameTag;
    @Getter private String teamName;

    private NPCItems npcItems;
    @Getter @Setter private boolean cache;

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
        this.uuid = UUID.randomUUID();
        this.profile = this.createProfile(player);
        this.location = new BasicLocation(LocationUtils.getSafeLocation(player.getLocation().clone()));
        this.overlay = overlay;
        this.lookAtPlayer = lookAtPlayer;
        this.hideNameTag = hideNameTag;

        this.npcItems = new NPCItems(player);
    }

    public NPCInfo(Profile profile, BasicLocation location, boolean lookAtPlayer, boolean overlay, boolean hideNameTag) {
        this(profile, location, new NPCItems(), lookAtPlayer, overlay, hideNameTag);
    }

    public NPCInfo(Profile profile, BasicLocation location, NPCItems items, boolean lookAtPlayer, boolean overlay, boolean hideNameTag) {
        this(UUID.randomUUID(), profile, location, items, lookAtPlayer, overlay, hideNameTag);
    }

    public NPCInfo(UUID uuid, Profile profile, BasicLocation location, NPCItems items, boolean lookAtPlayer, boolean overlay, boolean hideNameTag) {
        this.uuid = uuid;
        this.profile = profile;
        this.location = location;
        this.overlay = overlay;
        this.lookAtPlayer = lookAtPlayer;
        this.hideNameTag = hideNameTag;

        this.npcItems = items;
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
                .location(this.location.getLocation())
                .profile(this.profile)
                .lookAtPlayer(this.lookAtPlayer)
                .imitatePlayer(false);
    }

    public Profile getProfile() {
        return profile.clone();
    }

    public BasicLocation getLocation() {
        return location.clone();
    }

    public NPCItems getNPCItems() {
        return npcItems.clone();
    }

}
