package me.aleiv.cinematicCore.paper.files;

import com.github.juliarn.npc.modifier.EquipmentModifier;
import com.github.juliarn.npc.profile.Profile;
import me.aleiv.cinematicCore.paper.CinematicTool;
import me.aleiv.cinematicCore.paper.objects.NPCInfo;
import me.aleiv.cinematicCore.paper.objects.NPCItems;
import me.aleiv.cinematicCore.paper.utilities.ItemStackUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class DataFile {

    private final JavaPlugin plugin;

    private YamlConfiguration data;
    private File dataFile;

    public DataFile(JavaPlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            try {
                plugin.saveResource("data.yml", true);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create data.yml file.");
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);

        plugin.getLogger().log(Level.FINE, "File data.yml loaded correctly.");

    }

    public YamlConfiguration getData() {
        return data;
    }

    public File getFile() {
        return dataFile;
    }

    public void save() {
        try {
            this.data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLocation(String path, BasicLocation location) {
        this.data.set(path + ".x", location.getX());
        this.data.set(path + ".y", location.getY());
        this.data.set(path + ".z", location.getZ());
        this.data.set(path + ".yaw", location.getYaw());
        this.data.set(path + ".pitch", location.getPitch());
        this.data.set(path + ".world", location.getWorldName());
    }

    public BasicLocation getLocation(String path) {
        return new BasicLocation(
                this.data.getDouble(path + ".x"),
                this.data.getDouble(path + ".y"),
                this.data.getDouble(path + ".z"),
                this.data.getInt(path + ".yaw"),
                this.data.getInt(path + ".pitch"),
                this.data.getString(path + ".world")
        );
    }

    // MADE FUNCTIONS FOR EASY TO ACCESS PLUGIN

    public void saveNpc(NPCInfo info) {
        String path = "guards." + info.getUuid().toString() + ".";
        this.data.set(path + "uuid", info.getProfile().getUniqueId().toString());
        this.data.set(path + "name", info.getProfile().getName());

        Optional<Profile.Property> p = info.getProfile().getProperty("textures");
        if (p.isPresent()) {
            this.data.set(path + "texture", p.get().getValue());
            this.data.set(path + "signature", p.get().getSignature());
        }

        this.saveLocation(path + "loc", info.getLocation());

        this.data.set(path + "lookAtPlayer", info.isLookAtPlayer());
        this.data.set(path + "overlay", info.isOverlay());
        this.data.set(path + "hideNameTag", info.isHideNameTag());

        this.saveItems(path, info.getNPCItems());

        this.save();
    }

    public void removeNPC(NPCInfo info) {
        this.data.set("guards." + info.getUuid().toString(), null);
        this.save();
    }

    public void removeNPCs() {
        this.data.set("guards", null);
        this.save();
    }

    private void saveItems(String unpath, NPCItems items) {
        String path = unpath + "items.";

        this.data.set(path + "mainHand", ItemStackUtils.serializeItem(items.getItemInMainHand()));
        this.data.set(path + "offHand", ItemStackUtils.serializeItem(items.getItemInOffHand()));
        this.data.set(path + "helmet", ItemStackUtils.serializeItem(items.getHelmet()));
        this.data.set(path + "chestplate", ItemStackUtils.serializeItem(items.getChestplate()));
        this.data.set(path + "leggings", ItemStackUtils.serializeItem(items.getLeggings()));
        this.data.set(path + "boots", ItemStackUtils.serializeItem(items.getBoots()));
    }

    public NPCItems getItems(String unpath) {
        String path = unpath + "items.";

        HashMap<Integer, ItemStack> items = new HashMap<>();
        items.put(EquipmentModifier.HEAD, ItemStackUtils.deserializeItem(this.data.getString(path + "helmet")));
        items.put(EquipmentModifier.CHEST, ItemStackUtils.deserializeItem(this.data.getString(path + "chestplate")));
        items.put(EquipmentModifier.LEGS, ItemStackUtils.deserializeItem(this.data.getString(path + "leggings")));
        items.put(EquipmentModifier.FEET, ItemStackUtils.deserializeItem(this.data.getString(path + "boots")));
        items.put(EquipmentModifier.MAINHAND, ItemStackUtils.deserializeItem(this.data.getString(path + "mainHand")));
        items.put(EquipmentModifier.OFFHAND, ItemStackUtils.deserializeItem(this.data.getString(path + "offHand")));

        return new NPCItems(items);
    }

    public NPCInfo getNPC(UUID uuid) {
        String path = "guards." + uuid.toString() + ".";
        if (this.data.contains(path)) {

            Profile profile = new Profile(UUID.fromString(this.data.getString(path + "uuid")), this.data.getString(path + "name"), List.of(new Profile.Property("textures", this.data.getString(path + "texture"), this.data.getString(path + "signature"))));

            return new NPCInfo(
                    uuid,
                    profile,
                    this.getLocation(path + "loc"),
                    this.getItems(path),
                    this.data.getBoolean(path + "lookAtPlayer"),
                    this.data.getBoolean(path + "overlay"),
                    this.data.getBoolean(path + "hideNameTag")
            );
        }
        return null;
    }

    public List<UUID> getAllUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        try {
            for (String key : this.data.getConfigurationSection("guards").getKeys(false)) {
                uuids.add(UUID.fromString(key));
            }
        } catch (Exception ignore) {}
        return uuids;
    }

    public List<NPCInfo> getAllNPCs() {
        List<NPCInfo> npcs = new ArrayList<>();
        for (UUID uuid : this.getAllUUIDs()) {
            npcs.add(this.getNPC(uuid));
        }
        return npcs;
    }

}
