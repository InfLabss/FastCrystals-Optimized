package club.aurorapvp.fastcrystals.listeners.packet;

import club.aurorapvp.fastcrystals.FastCrystals;
import club.aurorapvp.fastcrystals.player.CrystalPlayer;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity.InteractAction;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

public class InteractPacketListener implements PacketListener {

  @Override
  public void onPacketReceive(PacketReceiveEvent event) {
    if (event.getPacketType() != Client.INTERACT_ENTITY) {
      return;
    }

    WrapperPlayClientInteractEntity wrapper = new WrapperPlayClientInteractEntity(event);

    if (wrapper.getAction() != InteractAction.INTERACT_AT) {
      return;
    }

    if (event.getUser().getUUID() == null) {
      return;
    }

    CrystalPlayer player = CrystalPlayer.getPlayer(event.getUser().getUUID());

    if (player == null) {
      return;
    }

    ItemStack item;

    if (wrapper.getHand() == InteractionHand.MAIN_HAND) {
      item = player.getMainHand();
    } else {
      item = player.getOffHand();
    }

    if (item.getType() != Material.END_CRYSTAL) {
      return;
    }

    EnderCrystal crystal = FastCrystals.getCrystal(wrapper.getEntityId());

    if (crystal == null) {
      return;
    }

    Location blockLoc = crystal.getLocation().subtract(0.5, 1.0, 0.5);

    RayTraceResult result = player.getPlayer().rayTraceBlocks(
            player.getGameMode() == GameMode.CREATIVE ? 5.0 : 4.5,
            FluidCollisionMode.NEVER);

    if (result == null || result.getHitBlock() == null) {
      return;
    }

    if (result.getHitBlock().getType() != Material.OBSIDIAN
            && result.getHitBlock().getType() != Material.BEDROCK) {
      return;
    }

    if (!result.getHitBlock().getLocation().equals(blockLoc)) {
      return;
    }

    int packetEntityId = FastCrystals.getLastEntityId() + 1;
    final boolean shouldSendPacket = !FastCrystals.containsCrystal(crystal.getLocation())
            && FastCrystals.getCrystal(packetEntityId) == null;

    if (shouldSendPacket) {
      WrapperPlayServerSpawnEntity spawnCrystal = new WrapperPlayServerSpawnEntity(
              FastCrystals.getLastEntityId() + 1, Optional.of(UUID.randomUUID()),
              EntityTypes.END_CRYSTAL,
              new Vector3d(crystal.getX(), crystal.getY(), crystal.getZ()), crystal.getPitch(),
              crystal.getYaw(), crystal.getYaw(), 0, Optional.of(new Vector3d()));

      event.getUser().sendPacket(spawnCrystal);
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        Location loc = crystal.getLocation().subtract(0.5, 0.0, 0.5);

        if (loc.getBlock().getType() != Material.AIR) {
          return;
        }

        loc.add(0.5, 1.0, 0.5);

        Set<Entity> nearbyEntities = new HashSet<>(
                loc.getWorld().getNearbyEntities(loc, 0.5, 1, 0.5));

        if (nearbyEntities.isEmpty()) {
          EnderCrystal crystal = (EnderCrystal) loc.getWorld()
                  .spawnEntity(loc.subtract(0.0, 1.0, 0.0), EntityType.ENDER_CRYSTAL);

          crystal.setShowingBottom(false);

          if (shouldSendPacket && crystal.getEntityId() != packetEntityId) {
            WrapperPlayServerDestroyEntities crystalDestroy = new WrapperPlayServerDestroyEntities(
                    crystal.getEntityId());

            event.getUser().sendPacket(crystalDestroy);
          }

          if (player.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
          }
        }
      }
    }.runTask(FastCrystals.getInstance());
  }
}