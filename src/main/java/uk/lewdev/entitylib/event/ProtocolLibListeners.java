package uk.lewdev.entitylib.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.aleiv.cinematicCore.paper.CinematicTool;
import uk.lewdev.entitylib.FakeEntityPlugin;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;

/**
 * @author Lewys Davies (Lew_)
 */
@SuppressWarnings("all")
public class ProtocolLibListeners {

    private static boolean hasInit = false;

    private final FakeEntityPlugin plugin;

    public ProtocolLibListeners(FakeEntityPlugin plugin) {
        if (hasInit) {
            this.plugin = plugin;
            return;
        }

        hasInit = true;

        this.plugin = plugin;
        this.entityInteractListener();
    }

    private void entityInteractListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(CinematicTool.getInstance(),
                ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.ENTITY_ACTION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                int entityId = event.getPacket().getIntegers().getValues().get(0);
                Player player = event.getPlayer();
                FakeEntity entity = FakeEntity.ALL_ALIVE_INSTANCES.get(entityId);

                if (entity != null) {
                    Bukkit.getPluginManager().callEvent(new PlayerInteractFakeEntity(player, entity));
                }
            }
        });
    }
}
