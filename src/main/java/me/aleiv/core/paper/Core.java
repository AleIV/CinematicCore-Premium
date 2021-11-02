package me.aleiv.core.paper;

import java.time.Duration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import io.github.znetworkw.znpcservers.NPCLibrary;
import kr.entree.spigradle.annotations.SpigotPlugin;
import lombok.Getter;
import me.aleiv.core.paper.commands.CinematicCMD;
import me.aleiv.core.paper.listeners.GlobalListener;
import me.aleiv.core.paper.objects.Cinematic;
import me.aleiv.core.paper.utilities.JsonConfig;
import me.aleiv.core.paper.utilities.TCT.BukkitTCT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import us.jcedeno.libs.rapidinv.RapidInvManager;


@SpigotPlugin
public class Core extends JavaPlugin {

    private static @Getter Core instance;
    private @Getter Game game;
    private @Getter PaperCommandManager commandManager;
    private @Getter static MiniMessage miniMessage = MiniMessage.get();
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @Getter NPCLibrary npcLibrary;

    @Override
    public void onEnable() {
        instance = this;

        game = new Game(this);

        RapidInvManager.register(this);
        BukkitTCT.registerPlugin(this);
        this.npcLibrary = new NPCLibrary();
        this.npcLibrary.register(this);
        

        //LISTENERS

        Bukkit.getPluginManager().registerEvents(new GlobalListener(this), this);


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
        this.npcLibrary.unregister();
    }

    public void updateJson(){
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

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

    /*public void adminMessage(String text) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.hasPermission("admin.perm"))
                player.sendMessage(text);
        });
    }*/

    public Component componentToString(String str) {
        return miniMessage.parse(str);
    }

    public void broadcastMessage(String text) {
        Bukkit.broadcast(miniMessage.parse(text));
    }

    public void sendActionBar(Player player, String text) {
        player.sendActionBar(miniMessage.parse(text));
    }

    public void showTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(miniMessage.parse(title), miniMessage.parse(subtitle), Times
                .of(Duration.ofMillis(50 * fadeIn), Duration.ofMillis(50 * stay), Duration.ofMillis(50 * fadeIn))));
    }

    public void sendHeader(Player player, String text) {
        player.sendPlayerListHeader(miniMessage.parse(text));
    }

    public void sendFooter(Player player, String text) {
        player.sendPlayerListFooter(miniMessage.parse(text));
    }

}