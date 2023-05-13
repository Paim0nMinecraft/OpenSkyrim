package xiatian

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.cnfont.FontLoaders
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.hurtPercent
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.roundToInt

@ElementInfo(name = "Target2")
class Target : Element(-46.0,-40.0,1F,Side(Side.Horizontal.MIDDLE,Side.Vertical.MIDDLE)) {
    private val modeValue = ListValue("Mode", arrayOf("Best","Novoline","novoline2","novoline3","newnovoline"), "Novoline")
    private val switchModeValue = ListValue("SwitchMode", arrayOf("Slide","Zoom","None"), "Slide")
    private val animSpeedValue = IntegerValue("AnimSpeed",10,5,20)
    private val switchAnimSpeedValue = IntegerValue("SwitchAnimSpeed",20,5,40)
    val backgroundalpha = IntegerValue("Alpha", 120, 0, 255)
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val gredValue = IntegerValue("GradientRed", 255, 0, 255)
    private val ggreenValue = IntegerValue("GradientGreen", 255, 0, 255)
    private val gblueValue = IntegerValue("GradientBlue", 255, 0, 255)
    private var easingHP = 0f
    private var prevTarget:IEntityLivingBase?=null
    private var lastHealth=20F
    private var lastChangeHealth=20F
    private var changeTime=System.currentTimeMillis()
    private var displayPercent=0f
    private var lastUpdate = System.currentTimeMillis()
    private val decimalFormat = DecimalFormat("0.0")
    private  val counter1 = intArrayOf(50)
    private val counter2 = intArrayOf(80)

    private fun getHealth(entity: IEntityLivingBase?):Float{
        return if(entity==null || entity.isDead){ 0f }else{ entity.health }
    }

    override fun drawElement(): Border? {
        var target=(LiquidBounce.moduleManager[KillAura::class.java] as KillAura).target
        val time=System.currentTimeMillis()
        val pct = (time - lastUpdate) / (switchAnimSpeedValue.get()*50f)
        lastUpdate=System.currentTimeMillis()

        if (classProvider.isGuiHudDesigner(mc.currentScreen)) {
            target=mc.thePlayer
        }
        if (target != null) {
            prevTarget = target
        }
        prevTarget ?: return getTBorder()

        if (target!=null) {
            if (displayPercent < 1) {
                displayPercent += pct
            }
            if (displayPercent > 1) {
                displayPercent = 1f
            }
        } else {
            if (displayPercent > 0) {
                displayPercent -= pct
            }
            if (displayPercent < 0) {
                displayPercent = 0f
                prevTarget=null
                return getTBorder()
            }
        }

        if(getHealth(prevTarget)!=lastHealth){
            lastChangeHealth=lastHealth
            lastHealth=getHealth(prevTarget)
            changeTime=time
        }
        val nowAnimHP=if((time-(animSpeedValue.get()*50))<changeTime){
            getHealth(prevTarget)+(lastChangeHealth-getHealth(prevTarget))*(1-((time-changeTime)/(animSpeedValue.get()*50F)))
        }else{
            getHealth(prevTarget)
        }

        when(switchModeValue.get().toLowerCase()){
            "zoom" -> {
                val border=getTBorder() ?: return null
                GL11.glScalef(displayPercent,displayPercent,displayPercent)
                GL11.glTranslatef(((border.x2 * 0.5f * (1-displayPercent))/displayPercent), ((border.y2 * 0.5f * (1-displayPercent))/displayPercent).toFloat(), 0f)
            }
            "slide" -> {
                val percent= EaseUtils.easeInQuint(1.0-displayPercent)
                val xAxis= classProvider.createScaledResolution(mc).scaledWidth-renderX
                GL11.glTranslated(xAxis*percent,0.0,0.0)
            }
        }

        when(modeValue.get().toLowerCase()){

            "sparklingwater" -> drawSparklingWater(prevTarget!!,nowAnimHP)
            "best"->drawBest(prevTarget!!,nowAnimHP)
            "novoline2"-> drawnovoline2(prevTarget!!,nowAnimHP)
            "novoline3"-> drawnovoline3(prevTarget!!,nowAnimHP)
            "newnovoline"-> drawnewnovo(prevTarget!!,nowAnimHP)
            "watermelon" -> drawWaterMelon(prevTarget!!,nowAnimHP)
        }

        return getTBorder()
    }

    private fun drawWaterMelon(target: IEntityLivingBase, easingHealth: Float) {
        // background rect
        RenderUtils.drawRoundedCornerRect(
            -1.5f, 2.5f, 152.5f, 52.5f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -1f, 2f, 152f, 52f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0.5f, 1.5f, 151.5f, 51.5f,
            5.0f, Color(0, 0, 0, 40).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0f, 1f, 151.0f, 51.0f,
            5.0f, Color(0, 0, 0, 60).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            0.5f, 0.5f, 150.5f, 50.5f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            1f, 0f, 150.0f, 50.0f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        // head size based on hurt
        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) {
            1f
        } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 35
        // draw head
        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        GL11.glColor4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(mc.netHandler.getPlayerInfo(target.uniqueID)!!.locationSkin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        GL11.glPopMatrix()
        // draw name of target
        FontLoaders.F18.drawString("${target.name}", 45f, 12f, Color.WHITE.rgb)
        val df = DecimalFormat("0.00");
        // draw armour percent
        FontLoaders.F16.drawString(
            "Armor ${(df.format(PlayerUtils.getAr(target) * 100))}%",
            45f,
            24f,
            Color(200, 200, 200).rgb
        )
        // draw bar
        RenderUtils.drawRoundedCornerRect(45f, 32f, 145f, 42f, 5f, Color(0, 0, 0, 100).rgb)
        RenderUtils.drawRoundedCornerRect(
            45f,

            32f,
            45f + (easingHealth / target.maxHealth) * 100f,
            42f,
            5f,
            ColorUtils.rainbow().rgb
        )
        // draw hp as text
        FontLoaders.F16.drawString(
            "${((df.format((easingHealth / target.maxHealth) * 100)))}%",
            80f,
            34f,
            Color(255, 255, 255).rgb,
            true
        )
    }

    private fun drawSparklingWater(target: IEntityLivingBase, easingHealth: Float) {
        // background
        RenderUtils.drawRoundedCornerRect(
            -1.5f, 2.5f, 152.5f, 52.5f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -1f, 2f, 152f, 52f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0.5f, 1.5f, 151.5f, 51.5f,
            5.0f, Color(0, 0, 0, 40).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0f, 1f, 151.0f, 51.0f,
            5.0f, Color(0, 0, 0, 60).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            0.5f, 0.5f, 150.5f, 50.5f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            1f, 0f, 150.0f, 50.0f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        // draw entity
        if(target.hurtTime > 1) {
            GL11.glColor4f(1f, 0f, 0f, 0.5f)
            RenderUtils.drawEntityOnScreen(25, 48, 32, target)
        } else {
            GL11.glColor4f(1f, 1f, 1f, 1f)
            RenderUtils.drawEntityOnScreen(25, 45, 30, target)
        }

        // target text
        FontLoaders.F18.drawString("${target.name}", 45f, 6f, Color.WHITE.rgb)
        val df = DecimalFormat("0.00");
        // armour text
        FontLoaders.F16.drawString(
            "Armor ${(df.format(PlayerUtils.getAr(target) * 100))}%",
            45f,
            40f,
            Color(200, 200, 200).rgb
        )//bar
        RenderUtils.drawRoundedCornerRect(45f, 23f, 145f, 33f, 5f, Color(0, 0, 0, 100).rgb)
        RenderUtils.drawRoundedCornerRect(
            45f,
            23f,
            45f + (easingHealth / target.maxHealth) * 100f,
            33f,
            5f,
            ColorUtils.rainbow().rgb
        )
        FontLoaders.F16.drawString(
            "${((df.format((easingHealth / target.maxHealth) * 100)))}%",
            80f,
            25f,
            Color(255, 255, 255).rgb,
            true
        )
        /*
                // draw items
                 GlStateManager.resetColor()
                GL11.glPushMatrix()
                GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
                GlStateManager.enableRescaleNormal()
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                RenderHelper.enableGUIStandardItemLighting()

                val renderItem = mc.renderItem

                var x = 45
                var y = 28

                for (index in 3 downTo 0) {
                    val stack = entity.inventory.armorInventory[index] ?: continue

                    if (stack.item == null)
                        continue

                    renderItem.renderItemIntoGUI(stack, x, y)
                    renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
                    RenderUtils.drawExhiEnchants(stack, x.toFloat(), y.toFloat())

                    x += 16
                }

                val mainStack = entity.heldItem
                if (mainStack != null && mainStack.item != null) {
                    renderItem.renderItemIntoGUI(mainStack, x, y)
                    renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x, y)
                    RenderUtils.drawExhiEnchants(mainStack, x.toFloat(), y.toFloat())
                }

                RenderHelper.disableStandardItemLighting()
                GlStateManager.disableRescaleNormal()
                GlStateManager.enableAlpha()
                GlStateManager.disableBlend()
                GlStateManager.disableLighting()
                GlStateManager.disableCull()
                GL11.glPopMatrix()
                 */
    }

    private fun drawBest(target: IEntityLivingBase, easingHealth: Float){
        val addedLen = (60 +  FontLoaders.F18.getStringWidth(target.name!!) * 1.60f).toFloat()

        RenderUtils.drawRect(0f, 0f, addedLen, 47f, Color(0, 0, 0, 120).rgb)
        RenderUtils.drawRoundedCornerRect(0f, 0f, (easingHealth / target.maxHealth) * addedLen, 47f, 3f, Color(0, 0, 0, 90).rgb)

        RenderUtils.drawShadowWithCustomAlpha(0f, 0f, addedLen, 47f,200F)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 35

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.drawHead(mc.netHandler.getPlayerInfo(target.uniqueID)!!.locationSkin,0,0, size, size)
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        GL11.glScalef(1.5f, 1.5f, 1.5f)
        FontLoaders.F18.drawString(target.name!!, 39, 8, Color.WHITE.rgb)

        GL11.glPopMatrix()
        FontLoaders.F18.drawString("Health ${target.health.roundToInt()}", 56, 20 + ( FontLoaders.F18.height* 1.5).toInt(), Color.WHITE.rgb)
    }
    private fun drawnovoline2(target: IEntityLivingBase, easingHealth: Float){
        val width = (38 +  FontLoaders.F18.getStringWidth(target.name!!)).coerceAtLeast(118).toFloat()
        RenderUtils.drawRect(0f, 0f, width + 14f, 44f, Color(0, 0, 0, backgroundalpha.get()).rgb)
        RenderUtils.drawHead(mc.netHandler.getPlayerInfo(target.uniqueID)!!.locationSkin, 3,3,30,30)
        FontLoaders.F18.drawString(target.name!!, 34.5f, 4f, Color.WHITE.rgb)
        FontLoaders.F18.drawString("Health: ${decimalFormat.format(target.health)}", 34.5f, 14f, Color.WHITE.rgb)
        FontLoaders.F18.drawString(
            "Distance: ${decimalFormat.format(mc.thePlayer!!.getDistanceToEntity(target))}m",
            34.5f,
            24f,
            Color.WHITE.rgb
        )
        RenderUtils.drawRect(2.5f, 35.5f, width + 11.5f, 37.5f, Color(0, 0, 0, 200).rgb)
        RenderUtils.drawRect(3f, 36f, 3f + (easingHealth / target.maxHealth) * (width + 8f), 37f, Color(redValue.get(), greenValue.get(), blueValue.get()).rgb)
        RenderUtils.drawRect(2.5f, 39.5f, width + 11.5f, 41.5f, Color(0, 0, 0, 200).rgb)
        RenderUtils.drawRect(
            3f,
            40f,
            3f + (target.totalArmorValue / 20F) * (width + 8f),
            41f,
            Color(77, 128, 255).rgb
        )
        //easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
    }
    private fun drawnovoline3(target: IEntityLivingBase, easingHealth: Float){
        val mainColor = Color(redValue.get(), greenValue.get(), blueValue.get())
        val percent = target.health.toInt()
        val nameLength = (FontLoaders.F18.getStringWidth(target.name!!)).coerceAtLeast(
            FontLoaders.F18.getStringWidth(
                "${
                    decimalFormat.format(percent)
                }"
            )
        ).toFloat() + 20F
        val barWidth = (target.health / target.maxHealth).coerceIn(0F, target.maxHealth) * (nameLength - 2F)
        RenderUtils.drawRect(-2F, -2F, 3F + nameLength + 36F, 2F + 36F, Color(50, 50, 50, 150).rgb)
        RenderUtils.drawRect(-1F, -1F, 2F + nameLength + 36F, 1F + 36F, Color(0, 0, 0, 100).rgb)
        RenderUtils.drawHead(mc.netHandler.getPlayerInfo(target.uniqueID)!!.locationSkin, 0,0,36,36)
        Fonts.minecraftFont.drawStringWithShadow(target.name!!, 2 + 36, 2, -1)
        RenderUtils.drawRect(37F, 14F, 37F + nameLength, 24F, Color(0, 0, 0, 200).rgb)
        //easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
        val animateThingy =
            (easingHealth.coerceIn(target.health, target.maxHealth) / target.maxHealth) * (nameLength - 2F)
        if (easingHealth > target.health)
            RenderUtils.drawRect(38F, 15F, 38F + animateThingy, 23F, mainColor.darker().rgb)
        RenderUtils.drawRect(38F, 15F, 38F + barWidth, 23F, mainColor.rgb)
        Fonts.minecraftFont.drawStringWithShadow("${decimalFormat.format(percent)}", 38, 26, Color.WHITE.rgb)
        //FontLoaders.F18.drawStringWithShadow(
        //"❤",
        ///Fonts.minecraftFont.getStringWidth("${decimalFormat.format(percent)}") + 40F,
        //27F,
        //Color(redValue.get(), greenValue.get(), blueValue.get()).rgb

    }
    private fun drawnewnovo(target: IEntityLivingBase, easingHealth: Float){
        val width = (38 + Fonts.minecraftFont.getStringWidth(target.name!!))
            .coerceAtLeast(118)
            .toFloat()
        counter1[0] += 1
        counter2[0] += 1
        counter1[0] = counter1[0].coerceIn(0, 50)
        counter2[0] = counter2[0].coerceIn(0, 80)
        RenderUtils.drawRect(0F, 0F, width, 34.5F, Color(0, 0, 0, backgroundalpha.get()))
        //val customColor = Color(redValue.get(), greenValue.get(), blueValue.get(), 255)
        //val customColor1 = Color(gredValue.get(), ggreenValue.get(), gblueValue.get(), 255)
        val customColor = Color(redValue.get(), greenValue.get(), blueValue.get(), 255)
        val customColor1 = Color(gredValue.get(), ggreenValue.get(), gblueValue.get(), 255)
        RenderUtils.drawGradientSideways(
            34.0, 16.0, width.toDouble() - 2,
            24.0, Color(40, 40, 40, 220).rgb, Color(60, 60, 60, 255).rgb
        )
        RenderUtils.drawGradientSideways(
            34.0, 16.0, (36.0F + (easingHealth / target.maxHealth) * (width - 36.0F)).toDouble() - 2,
            24.0, Palette.fade2(customColor, counter1[0], FontLoaders.F18.height).rgb,
            Palette.fade2(customColor1, counter2[0], FontLoaders.F18.height).rgb
        )
        //easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
        Fonts.minecraftFont.drawString(target.name!!, 34, 4, Color(255, 255, 255, 255).rgb)
        RenderUtils.drawHead(mc.netHandler.getPlayerInfo(target.uniqueID)!!.locationSkin, 2,2,30,30)
        Fonts.minecraftFont.drawStringWithShadow(
            BigDecimal((target.health / target.maxHealth * 100).toDouble()).setScale(
                1,
                BigDecimal.ROUND_HALF_UP
            ).toString() + "%", (width/ 2 + 5).toInt(), 17, Color.white.rgb
        )
    }
    private fun getTBorder():Border?{
        return when(modeValue.get().toLowerCase()){
            "novoline" -> Border(0F,0F,140F,40F,0F)
            "best"->Border(0F, 0F, 150F, 47F,0F)
            "novoline2"-> Border(0F,0F,140F,40F,0F)
            "novoline3"-> Border(0F,0F,140F,40F,0F)
            "newnovoline"-> Border(0F,0F,140F,40F,0F)
            else -> null
        }
    }
}