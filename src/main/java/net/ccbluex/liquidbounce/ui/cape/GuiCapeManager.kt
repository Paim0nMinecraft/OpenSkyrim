/*
 Skid from FDPClient
 */
package net.ccbluex.liquidbounce.ui.cape

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import org.lwjgl.opengl.GL11
import xiatian.novoline.font.Fonts
import java.io.File
import javax.imageio.ImageIO

object GuiCapeManager : GuiScreen() {
    private val jsonFile = File(LiquidBounce.fileManager.capeDir, "cape.json")

    private val embeddedCapes = mutableListOf<ICape>()

    var nowCape: ICape?
    val capeList = mutableListOf<ICape>()

    init {
        arrayOf(
            "astolfo",
            "fzy",
            "powerx",
            "lunar",
            "gast",
            "jiaran",
            "classic",
            "classic2",
            "aurora",
            "forest",
            "rose",
            "lavender",
            "ocean",
            "modern1",
            "modern2",
            "lava",
            "citrus",
            "fire",
            "nightlife",
            "abstract",
            "blur",
            "owner"
        ).forEach {
            try {
                embeddedCapes.add(loadCapeFromResource(it, "assets/minecraft/liquidwing/cape/$it.png"))
            } catch (e: Throwable) {
                System.out.println("Failed to load Capes")
            }
        }
        nowCape = embeddedCapes.random()
        pushEmbeddedCape()
    }

    private fun pushEmbeddedCape() {
        capeList.addAll(embeddedCapes)
    }

    fun load() {
        capeList.clear()

        pushEmbeddedCape()

        // add capes from files
        for (file in LiquidBounce.fileManager.capeDir.listFiles()) {
            if (file.isFile && !file.name.equals(jsonFile.name)) {
                try {
                    val args = file.name.split(".").toTypedArray()
                    val name = java.lang.String.join(".", *args.copyOfRange(0, args.size - 1))
                    loadCapeFromFile(name, file)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        if (!jsonFile.exists()) {
            return
        }

        val json = JsonParser().parse(jsonFile.reader(Charsets.UTF_8)).asJsonObject

        if (json.has("name")) {
            val name = json.get("name").asString
            if (!name.equals("NONE")) {
                val result = capeList.find { it.name == name } ?: embeddedCapes.random()
                nowCape = result
            }
        }
    }

    fun save() {
        val json = JsonObject()

        json.addProperty(
            "name", if (nowCape != null) {
                nowCape!!.name
            } else {
                "NONE"
            }
        )

        jsonFile.writeText(FileManager.PRETTY_GSON.toJson(json), Charsets.UTF_8)
    }

    private fun loadCapeFromResource(name: String, loc: String) =
        SingleImageCape(name, ImageIO.read(GuiCapeManager::class.java.classLoader.getResourceAsStream(loc)))

    private fun loadCapeFromFile(name: String, file: File) = SingleImageCape(name, ImageIO.read(file))


    override fun onGuiClosed() {
        save()
    }

    // render
    override fun initGui() {
        this.buttonList.add(GuiButton(0, 0, 0, FontLoaders.F18.getStringWidth("< 退出") + 10, 20, "< 退出"))
        this.buttonList.add(
            GuiButton(
                1,
                (width * 0.3).toInt(),
                (height * 0.5).toInt(),
                FontLoaders.F18.getStringWidth("<-") + 10,
                20,
                "<-"
            )
        )
        this.buttonList.add(
            GuiButton(
                2,
                (width * 0.7).toInt(),
                (height * 0.5).toInt(),
                FontLoaders.F18.getStringWidth("->") + 10,
                20,
                "->"
            )
        )
    }

    override fun actionPerformed(p_actionPerformed_1_: GuiButton) {
        fun next(index: Int) {
            var chooseIndex = index
            if (chooseIndex >= capeList.size) {
                chooseIndex = -1
            }

            if (chooseIndex < -1) {
                chooseIndex = capeList.size - 1
            }

            nowCape = if (chooseIndex != -1) {
                capeList[chooseIndex]
            } else {
                null
            }
        }

        when (p_actionPerformed_1_.id) {
            0 -> mc.displayGuiScreen(null)
            1 -> next(capeList.indexOf(nowCape) - 1)
            2 -> next(capeList.indexOf(nowCape) + 1)
        }
    }

    override fun drawScreen(p_drawScreen_1_: Int, p_drawScreen_2_: Int, p_drawScreen_3_: Float) {
        // draw background
        drawBackground(0)

        GL11.glPushMatrix()
        Fonts.posterama.posterama13.posterama13.drawCenteredString(
            if (nowCape == null) {
                "§cNONE"
            } else {
                "§a${nowCape!!.name}"
            }, width * 0.50f, height * 0.23f, -1, false
        )
        GL11.glScalef(2f, 2f, 2f)
        Fonts.posterama.posterama20.posterama20.drawCenteredString(
            "Cape Manager",
            width * 0.25f,
            height * 0.03f,
            -1,
            false
        )
        GL11.glPopMatrix()

        // draw buttons
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_)

        // draw entity
        mc.player ?: return
        GL11.glEnable(GL11.GL_CULL_FACE)
        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)
        GlStateManager.enableColorMaterial()
        GlStateManager.pushMatrix()
        GL11.glTranslatef(width * 0.5f - 60, height * 0.3f, 0f)
        GL11.glScalef(2f, 2f, 2f)
        GL11.glTranslatef(30f, 100f, 0f)
        GlStateManager.translate(0F, 0F, 50F)
        GlStateManager.scale(-50F, 50F, 50F)
        GlStateManager.rotate(180F, 0F, 0F, 1F)

        val renderYawOffset = mc.player.renderYawOffset
        val rotationYaw = mc.player.rotationYaw
        val rotationPitch = mc.player.rotationPitch
        val prevRotationYawHead = mc.player.prevRotationYawHead
        val rotationYawHead = mc.player.rotationYawHead
        val armor0 = mc.player.inventory.armorInventory[0]
        val armor1 = mc.player.inventory.armorInventory[1]
        val armor2 = mc.player.inventory.armorInventory[2]
        val armor3 = mc.player.inventory.armorInventory[3]
        val current = mc.player.inventory.mainInventory[mc.player.inventory.currentItem]

        GlStateManager.rotate(135F, 0F, 1F, 0F)
        RenderHelper.enableStandardItemLighting()
        GlStateManager.rotate(-135F, 0F, 1F, 0F)
        GlStateManager.rotate(0f, 1F, 0F, 0F)

        mc.player.renderYawOffset = 180f
        mc.player.rotationYaw = 180f
        mc.player.rotationPitch = 0f
        mc.player.rotationYawHead = mc.player.rotationYaw
        mc.player.prevRotationYawHead = mc.player.rotationYaw


        GlStateManager.translate(0F, 0F, 0F)


        val renderManager = MinecraftInstance.mc.renderManager
        renderManager.playerViewY = 180F
        renderManager.isRenderShadow = false
        renderManager.renderEntityWithPosYaw(MinecraftInstance.mc.thePlayer!!, 0.0, 0.0, 0.0, 0F, 1F)
        renderManager.isRenderShadow = true



        mc.player.renderYawOffset = renderYawOffset
        mc.player.rotationYaw = rotationYaw
        mc.player.rotationPitch = rotationPitch
        mc.player.prevRotationYawHead = prevRotationYawHead
        mc.player.rotationYawHead = rotationYawHead
        mc.player.inventory.armorInventory[0] = armor0
        mc.player.inventory.armorInventory[1] = armor1
        mc.player.inventory.armorInventory[2] = armor2
        mc.player.inventory.armorInventory[3] = armor3
        mc.player.inventory.mainInventory[mc.player.inventory.currentItem] = current

        GlStateManager.popMatrix()
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
        GlStateManager.disableTexture2D()
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
        GlStateManager.resetColor()
    }

    override fun doesGuiPauseGame() = false
}
