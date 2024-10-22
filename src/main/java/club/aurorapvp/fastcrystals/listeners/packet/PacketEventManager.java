package club.aurorapvp.fastcrystals.listeners.packet;

import club.aurorapvp.fastcrystals.FastCrystals;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public class PacketEventManager {
  public static void init() {
    PacketEvents.setAPI(SpigotPacketEventsBuilder.build(FastCrystals.getInstance()));
    PacketEvents.getAPI().getSettings()
            .bStats(true)
            .checkForUpdates(false)
            .debug(false);
    PacketEvents.getAPI().init();

    PacketEvents.getAPI().getEventManager().registerListener(new LeftClickPacketListener(), PacketListenerPriority.LOWEST);
    PacketEvents.getAPI().getEventManager().registerListener(new InteractPacketListener(), PacketListenerPriority.LOWEST);
    PacketEvents.getAPI().getEventManager().registerListener(new AnimationPacketListener(), PacketListenerPriority.LOWEST);
    PacketEvents.getAPI().getEventManager().registerListener(new EntityAttackListener(), PacketListenerPriority.LOWEST);
  }
}