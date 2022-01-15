package me.aleiv.cinematicCore.paper.objects;

import com.github.juliarn.npc.modifier.EquipmentModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class NPCItems {

    private HashMap<Integer, ItemStack> items;

    public NPCItems() {
        this.items = new HashMap<>();
    }

    public NPCItems(Player player) {
        this.items = new HashMap<>();
        this.items.put(EquipmentModifier.HEAD, player.getInventory().getHelmet());
        this.items.put(EquipmentModifier.CHEST, player.getInventory().getChestplate());
        this.items.put(EquipmentModifier.LEGS, player.getInventory().getLeggings());
        this.items.put(EquipmentModifier.FEET, player.getInventory().getBoots());
        this.items.put(EquipmentModifier.OFFHAND, player.getInventory().getItemInOffHand());
        this.items.put(EquipmentModifier.MAINHAND, player.getInventory().getItemInMainHand());
    }

    public NPCItems(HashMap<Integer, ItemStack> items) {
        this.items = items;
    }

    /**
     * @param slot {@link EquipmentModifier}
     * @param item {@link ItemStack}
     */
    public void setItem(int slot, ItemStack item) {
        this.items.put(slot, item);
    }

    public HashMap<Integer, ItemStack> getItems() {
        return new HashMap<>(this.items);
    }

    public ItemStack getHelmet() {
        return this.items.get(EquipmentModifier.HEAD);
    }

    public ItemStack getChestplate() {
        return this.items.get(EquipmentModifier.CHEST);
    }

    public ItemStack getLeggings() {
        return this.items.get(EquipmentModifier.LEGS);
    }

    public ItemStack getBoots() {
        return this.items.get(EquipmentModifier.FEET);
    }

    public ItemStack getItemInOffHand() {
        return this.items.get(EquipmentModifier.OFFHAND);
    }

    public ItemStack getItemInMainHand() {
        return this.items.get(EquipmentModifier.MAINHAND);
    }

    public List<ItemStack> getItemsAsList() {
        return this.items.values().stream().collect(java.util.stream.Collectors.toList());
    }

    @Override
    public NPCItems clone() {
        return new NPCItems(new HashMap<>(this.items));
    }

}
