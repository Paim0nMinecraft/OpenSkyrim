/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package cc.paimon.modules.render;

import net.ccbluex.liquidbounce.api.enums.BlockType;
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock;
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityPlayerSP;
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.value.BlockValue;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock;
import static net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow;

@ModuleInfo(name = "BlockESP", description = "Allows you to see a selected block through walls.", category = ModuleCategory.RENDER)
public class BlockESP extends Module {
    private ListValue modeValue = new ListValue("Mode", new String[]{"Box", "2D"}, "Box");
    private BlockValue blockValue = new BlockValue("Block", 168);
    private IntegerValue radiusValue = new IntegerValue("Radius", 40, 5, 120);
    private IntegerValue blockLimitValue = new IntegerValue("BlockLimit", 256, 0, 2056);
    private IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private IntegerValue colorGreenValue = new IntegerValue("G", 179, 0, 255);
    private IntegerValue colorBlueValue = new IntegerValue("B", 72, 0, 255);
    private BoolValue colorRainbow = new BoolValue("Rainbow", false);
    private MSTimer searchTimer = new MSTimer();
    private List<WBlockPos> posList = new ArrayList<>();
    private Thread thread = null;
    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if (searchTimer.hasTimePassed(1000L) && (thread == null || !thread.isAlive())) {
            final int radius = radiusValue.get();
            final IBlock selectedBlock = functions.getBlockById(blockValue.get());

            if (selectedBlock == null || selectedBlock == classProvider.getBlockEnum(BlockType.AIR))
                return;

            thread = new Thread(new Runnable() {
                public void run() {
                    List<WBlockPos> blockList = new ArrayList<WBlockPos>();

                    for (int x = -radius; x < radius; x++) {
                        for (int y = radius; y >= -radius + 1; y--) {
                            for (int z = -radius; z < radius; z++) {
                                final IEntityPlayerSP thePlayer = mc.getThePlayer();

                                final int xPos = (int)thePlayer.getPosX() + x;
                                final int yPos = (int)thePlayer.getPosY() + y;
                                final int zPos = (int)thePlayer.getPosZ() + z;

                                final WBlockPos blockPos = new WBlockPos(xPos, yPos, zPos);
                                final IBlock block = getBlock(blockPos);

                                if (block == selectedBlock && blockList.size() < blockLimitValue.get())
                                    blockList.add(blockPos);
                            }
                        }
                    }
                    searchTimer.reset();

                    synchronized(posList) {
                        posList.clear();
                        posList.addAll(blockList);
                    }
                }
            }, "BlockESP-BlockFinder");

            thread.start();
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        synchronized (posList) {
            Color color;
            if (colorRainbow.get()) {
                color = rainbow();
            } else {
                color = new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
            }
            for (WBlockPos blockPos : posList) {
                switch (modeValue.get().toLowerCase()) {
                    case "box":
                        RenderUtils.drawBlockBox(blockPos, color, true);
                        break;
                    case "2d":
                        RenderUtils.draw2D(blockPos, color.getRGB(), Color.BLACK.getRGB());
                        break;
                }
            }
        }
    }

    @Override
    public String getTag() {
        if(blockValue.get() == 26) return "Bed";
        else return BlockUtils.getBlockName(blockValue.get());
    }
}
