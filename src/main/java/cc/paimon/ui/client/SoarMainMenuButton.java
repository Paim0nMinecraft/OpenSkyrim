package cc.paimon.ui.client;

import cc.paimon.utils.SimpleAnimation;

public class SoarMainMenuButton {

	private String name;
	public SimpleAnimation opacityAnimation = new SimpleAnimation(0.0F);
	
	public SoarMainMenuButton(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
