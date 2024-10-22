package club.aurorapvp.fastcrystals.listeners.bukkit;

import club.aurorapvp.fastcrystals.FastCrystals;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityEventListener implements Listener {

  private static final EntityType CRYSTAL_TYPE;

  static {
    EntityType crystalType;
    try {
      crystalType = EntityType.valueOf("END_CRYSTAL");
    } catch (IllegalArgumentException e) {
      try {
        crystalType = EntityType.valueOf("ENDER_CRYSTAL");
      } catch (IllegalArgumentException ex) {
        crystalType = null;
        System.err.println("Could not find EntityType for End Crystal");
      }
    }
    CRYSTAL_TYPE = crystalType;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntitySpawn(EntitySpawnEvent event) {
    FastCrystals.setLastEntityId(event.getEntity().getEntityId());

    if (CRYSTAL_TYPE == null || event.getEntityType() != CRYSTAL_TYPE) {
      return;
    }

    if (event.getEntity() instanceof EnderCrystal) {
      FastCrystals.addCrystal(event.getEntity().getEntityId(), (EnderCrystal) event.getEntity());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
    if (CRYSTAL_TYPE == null || event.getEntityType() != CRYSTAL_TYPE) {
      return;
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        FastCrystals.removeCrystal(event.getEntity().getEntityId());
      }
    }.runTaskLaterAsynchronously(FastCrystals.getInstance(), 40L);
  }
}
