package me.aleiv.cinematicCore.paper;

import com.github.juliarn.npc.NPCPool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;
import me.aleiv.cinematicCore.paper.commands.CinematicCMD;
import me.aleiv.cinematicCore.paper.listeners.GlobalListener;
import me.aleiv.cinematicCore.paper.listeners.NPCListener;
import me.aleiv.cinematicCore.paper.objects.Cinematic;
import me.aleiv.cinematicCore.paper.utilities.JsonConfig;
import me.aleiv.cinematicCore.paper.utilities.TCT.BukkitTCT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;


@SpigotPlugin
public class CinematicTool extends JavaPlugin {

    private static @Getter CinematicTool instance;
    private @Getter Game game;
    //private @Getter LiveCinematics liveCinematics;
    private @Getter PaperCommandManager commandManager;
    private @Getter static MiniMessage miniMessage = MiniMessage.get();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @Getter NPCPool npcPool;

    @Override
    public void onEnable() {
        instance = this;

        game = new Game(this);
        //this.liveCinematics = new LiveCinematics(this);

        BukkitTCT.registerPlugin(this);
        this.npcPool = NPCPool.builder(this).build();

        //LISTENERS

        Bukkit.getPluginManager().registerEvents(new GlobalListener(this), this);
        Bukkit.getPluginManager().registerEvents(new NPCListener(this), this);


        //COMMANDS
        
        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new CinematicCMD(this));

        try {
            var jsonConfig = new JsonConfig("cinematics.json");
            var list = jsonConfig.getJsonObject();
            var iter = list.entrySet().iterator();
            var map = game.getCinematics();

            while (iter.hasNext()) {
                var entry = iter.next();
                var name = entry.getKey();
                var value = entry.getValue();
                var cinematic = gson.fromJson(value, Cinematic.class);
                map.put(name, cinematic);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }



    }

    @Override
    public void onDisable() {
        this.npcPool.getNPCs().stream().toList().forEach(npc -> this.npcPool.removeNPC(npc.getEntityId()));
    }

    public void pushJson(){
        var list = game.getCinematics();

        try {
            var jsonConfig = new JsonConfig("cinematics.json");
            var json = gson.toJson(list);
            var obj = gson.fromJson(json, JsonObject.class);
            jsonConfig.setJsonObject(obj);
            jsonConfig.save();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void pullJson(){
        try {
            var jsonConfig = new JsonConfig("cinematics.json");
            var list = jsonConfig.getJsonObject();
            var iter = list.entrySet().iterator();
            var map = game.getCinematics();

            while (iter.hasNext()) {
                var entry = iter.next();
                var name = entry.getKey();
                var value = entry.getValue();
                var cinematic = gson.fromJson(value, Cinematic.class);
                map.put(name, cinematic);

            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

    public Component componentToString(String str) {
        return miniMessage.parse(str);
    }

    public void showTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

}