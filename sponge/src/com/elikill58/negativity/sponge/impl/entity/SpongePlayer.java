package com.elikill58.negativity.sponge.impl.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.potion.PotionEffect;
import com.elikill58.negativity.api.potion.PotionEffectType;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.SpongePotionEffectType;
import com.elikill58.negativity.sponge.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge.impl.inventory.SpongePlayerInventory;
import com.elikill58.negativity.sponge.impl.item.SpongeItemStack;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge.impl.location.SpongeWorld;
import com.elikill58.negativity.sponge.utils.LocationUtils;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;
import com.flowpowered.math.vector.Vector3d;

public class SpongePlayer extends SpongeEntity<org.spongepowered.api.entity.living.player.Player> implements Player {

	private Version playerVersion;

	public SpongePlayer(org.spongepowered.api.entity.living.player.Player p) {
		super(p);
		this.playerVersion = loadVersion();
	}
	
	private Version loadVersion() {
		return PlayerVersionManager.getPlayerVersion(this);
	}

	@Override
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}

	@Override
	public void sendMessage(String msg) {
		entity.sendMessage(Text.of(msg));
	}

	@Override
	public boolean isOp() {
		return entity.hasPermission("*");
	}

	@Override
	public boolean hasElytra() {
		return entity.get(Keys.IS_ELYTRA_FLYING).orElse(false);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean hasLineOfSight(Entity entity) {
		return LocationUtils.hasLineOfSight(this.entity, (org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) entity.getLocation().getDefault());
	}

	@Override
	public float getWalkSpeed() {
		return entity.require(Keys.WALKING_SPEED).floatValue();
	}

	@Override
	public double getHealth() {
		return entity.require(Keys.HEALTH);
	}

	@Override
	public float getFallDistance() {
		return entity.require(Keys.FALL_DISTANCE);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.get(entity.gameMode().get().getName());
	}
	
	@Override
	public void setGameMode(GameMode gameMode) {
		switch (gameMode) {
		case ADVENTURE:
			entity.gameMode().set(GameModes.ADVENTURE);
			break;
		case CREATIVE:
			entity.gameMode().set(GameModes.CREATIVE);
			break;
		case CUSTOM:
			entity.gameMode().set(GameModes.NOT_SET);
			break;
		case SPECTATOR:
			entity.gameMode().set(GameModes.SPECTATOR);
			break;
		case SURVIVAL:
			entity.gameMode().set(GameModes.SURVIVAL);
			break;
		}
	}

	@Override
	public void damage(double amount) {
		entity.damage(amount, DamageSource.builder().type(DamageTypes.CUSTOM).build());
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(entity.getLocation());
	}

	@Override
	public int getPing() {
		return entity.getConnection().getLatency();
	}

	@Override
	public World getWorld() {
		return new SpongeWorld(entity.getWorld());
	}

	@Override
	public String getName() {
		return entity.getName();
	}

	@Override
	public boolean hasPermission(String perm) {
		return entity.hasPermission(perm);
	}

	@Override
	public Version getPlayerVersion() {
		return playerVersion == Version.HIGHER ? (playerVersion = loadVersion()) : playerVersion;
	}

	@Override
	public void kick(String reason) {
		entity.kick(Text.of(reason));
	}

	@Override
	public int getLevel() {
		return entity.require(Keys.EXPERIENCE_LEVEL);
	}
	
	@Override
	public double getFoodLevel() {
		return entity.require(Keys.FOOD_LEVEL);
	}

	@Override
	public boolean getAllowFlight() {
		return entity.get(Keys.CAN_FLY).orElse(false);
	}

	@Override
	public Entity getVehicle() {
		return SpongeEntityManager.getEntity(entity.getVehicle().orElse(null));
	}
	
	@Override
	public ItemStack getItemInHand() {
		return entity.getItemInHand(HandTypes.MAIN_HAND).map(SpongeItemStack::new).orElse(null);
	}

	@Override
	public boolean isFlying() {
		return entity.require(Keys.IS_FLYING);
	}

	@Override
	public void sendPluginMessage(String channelId, byte[] writeMessage) {
		(channelId.equalsIgnoreCase("fml") ? SpongeNegativity.fmlChannel : SpongeNegativity.channel).sendTo(entity, (chan) -> chan.writeByteArray(writeMessage));
	}

	@Override
	public boolean isSleeping() {
		return entity.require(Keys.IS_SLEEPING);
	}

	@Override
	public boolean isSneaking() {
		return entity.require(Keys.IS_SNEAKING);
	}

	@Override
	public double getEyeHeight() {
		return Utils.getPlayerHeadHeight(entity);
	}
	
	@Override
	public boolean hasPotionEffect(PotionEffectType type) {
		List<org.spongepowered.api.effect.potion.PotionEffect> potionEffects = entity.getOrNull(Keys.POTION_EFFECTS);
		if (potionEffects == null) {
			return false;
		}
		for (org.spongepowered.api.effect.potion.PotionEffect effect : potionEffects) {
			if (effect.getType().getId().equalsIgnoreCase(type.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<PotionEffect> getActivePotionEffect() {
		List<org.spongepowered.api.effect.potion.PotionEffect> effects = entity.getOrNull(Keys.POTION_EFFECTS);
		if (effects == null) {
			return Collections.emptyList();
		}
		return effects.stream()
			.map(this::createPotionEffect)
			.collect(Collectors.toList());
	}
	
	@Override
	public Optional<PotionEffect> getPotionEffect(PotionEffectType type) {
		return entity.get(Keys.POTION_EFFECTS).flatMap(effects -> {
			for (org.spongepowered.api.effect.potion.PotionEffect effect : effects) {
				if (effect.getType().getId().equalsIgnoreCase(type.getId())) {
					return Optional.of(createPotionEffect(effect));
				}
			}
			return Optional.empty();
		});
	}
	
	private PotionEffect createPotionEffect(org.spongepowered.api.effect.potion.PotionEffect effect) {
		return new PotionEffect(PotionEffectType.forId(effect.getType().getId()), effect.getDuration(), effect.getAmplifier());
	}
	
	@Override
	public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
		entity.transform(Keys.POTION_EFFECTS, effects -> {
			org.spongepowered.api.effect.potion.PotionEffect effect =
				org.spongepowered.api.effect.potion.PotionEffect.of(SpongePotionEffectType.getEffect(type), amplifier, duration);
			if (effects == null) {
				return Collections.singletonList(effect);
			}
			effects.add(effect);
			return effects;
		});
	}
	
	@Override
	public void removePotionEffect(PotionEffectType type) {
		entity.transform(Keys.POTION_EFFECTS, effects -> {
			if (effects != null) {
				effects.removeIf(effect -> effect.getType().getId().equals(type.getId()));
				return effects;
			}
			return Collections.emptyList();
		});
	}

	@Override
	public String getIP() {
		return entity.getConnection().getAddress().getAddress().getHostAddress();
	}

	@Override
	public boolean isOnline() {
		return entity.isOnline();
	}

	@Override
	public void setSneaking(boolean b) {
		entity.offer(Keys.IS_SNEAKING, b);
	}

	@Override
	public EntityType getType() {
		return EntityType.PLAYER;
	}

	@Override
	public boolean isSprinting() {
		return entity.require(Keys.IS_SPRINTING);
	}

	@Override
	public void teleport(Entity et) {
		teleport(et.getLocation());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void teleport(Location loc) {
		entity.setLocation((org.spongepowered.api.world.Location<org.spongepowered.api.world.World>) loc.getDefault());
	}

	@Override
	public boolean isInsideVehicle() {
		return entity.getVehicle().isPresent();
	}

	@Override
	public float getFlySpeed() {
		return entity.require(Keys.FLYING_SPEED).floatValue();
	}

	@Override
	public void setSprinting(boolean b) {
		entity.offer(Keys.IS_SPRINTING, b);
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		List<Entity> list = new ArrayList<>();
		entity.getNearbyEntities(x).forEach((entity) -> list.add(SpongeEntityManager.getEntity(entity)));
		return list;
	}

	@Override
	public boolean isSwimming() {
		if (!isSprinting())
			return false;
		Location loc = getLocation().clone();
		if (loc.getBlock().getType().getId().contains("WATER"))
			return true;
		return loc.sub(0, 1, 0).getBlock().getType().getId().contains("WATER");
	}

	@Override
	public ItemStack getItemInOffHand() {
		return entity.getItemInHand(HandTypes.OFF_HAND).map(SpongeItemStack::new).orElse(null);
	}

	@Override
	public boolean isDead() {
		return getHealth() <= 0;
	}

	@Override
	public Vector getVelocity() {
		Vector3d vel = entity.getVelocity();
		return new Vector(vel.getX(), vel.getY(), vel.getZ());
	}

	@Override
	public PlayerInventory getInventory() {
		return new SpongePlayerInventory(entity);
	}
	
	@Override
	public boolean hasOpenInventory() {
		return entity.getOpenInventory().isPresent() && entity.getOpenInventory().get().getArchetype().equals(InventoryArchetypes.CHEST);
	}

	@Override
	public Inventory getOpenInventory() {
		return entity.getOpenInventory().map(SpongeInventory::new).orElse(null);
	}

	@Override
	public void openInventory(Inventory inv) {
		entity.openInventory((org.spongepowered.api.item.inventory.Inventory) inv.getDefault());
	}

	@Override
	public void closeInventory() {
		Task.builder().execute(entity::closeInventory).submit(SpongeNegativity.getInstance());
	}

	@Override
	public void updateInventory() {
		
	}

	@Override
	public void setAllowFlight(boolean b) {
		entity.offer(Keys.CAN_FLY, b);
	}

	@Override
	public void setVanished(boolean vanished) {
		entity.offer(Keys.VANISH, vanished);
		if (vanished) {
			entity.offer(Keys.VANISH_IGNORES_COLLISION, true);
			entity.offer(Keys.VANISH_PREVENTS_TARGETING, true);
		}
	}

	@Override
	public void setVelocity(Vector vel) {
		entity.setVelocity(new Vector3d(vel.getX(), vel.getY(), vel.getZ()));
	}
	
	@Override
	public InetSocketAddress getAddress() {
		return entity.getConnection().getVirtualHost();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Player)) {
			return false;
		}
		return Player.isSamePlayer(this, (Player) obj);
	}
}
