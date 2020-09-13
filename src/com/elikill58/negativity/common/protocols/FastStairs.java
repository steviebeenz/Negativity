package com.elikill58.negativity.common.protocols;

import static com.elikill58.negativity.universal.CheatKeys.FAST_STAIRS;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.PlayerMoveEvent;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastStairs extends Cheat implements Listeners {

	public FastStairs() {
		super(FAST_STAIRS, false, Materials.BRICK_STAIRS, CheatCategory.MOVEMENT, true, "stairs");
	}
	
	@EventListener
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		if(!np.hasDetectionActive(this))
			return;
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		if(p.getFallDistance() != 0)
			return;
		String blockName = e.getTo().clone().sub(0, 0.0001, 0).getBlock().getType().getId();
		if(!blockName.contains("STAIRS"))
			return;
		Location from = e.getFrom().clone();
		from.setY(e.getTo().getY());
		double distance = from.distance(e.getTo()), lastDistance = np.doubles.get(FAST_STAIRS, "distance", 0.0);
		if(distance > 0.45 && lastDistance > distance) {
			boolean mayCancel = Negativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 140),
					"distance", "No fall damage. Block: " + blockName + ", distance: " + distance + ", lastDistance: " + lastDistance,
					hoverMsg("main", "%distance%", String.format("%.2f", distance)));
			if(mayCancel && isSetBack())
				e.setCancelled(true);
		}
		np.doubles.set(FAST_STAIRS, "distance", distance);
	}
}