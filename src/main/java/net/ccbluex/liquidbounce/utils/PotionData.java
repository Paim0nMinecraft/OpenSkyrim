package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.api.minecraft.potion.IPotion;

public class PotionData {
    public final IPotion potion;
    public int maxTimer = 0;
    public float animationX = 0;
    public final net.ccbluex.liquidbounce.utils.Translate translate;
    public final int level;

    public PotionData(IPotion potion, Translate translate, int level) {
        this.potion = potion;
        this.translate = translate;
        this.level = level;
    }

    public float getAnimationX() {
        return animationX;
    }

    public IPotion getPotion() {
        return potion;
    }

    public int getMaxTimer() {
        return maxTimer;
    }
}