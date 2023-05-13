/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package cc.paimon.modules.combat


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketUseEntity
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.server.SPacketEntityVelocity
import kotlin.math.cos
import kotlin.math.sin
//@NativeClass
@ModuleInfo(name = "Velocity", description = "使你改变自己受到的击退",category = ModuleCategory.COMBAT)
class OldVelocity : Module() {


    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Horizontal", 0F, 0F, 1F)
    private val verticalValue = FloatValue("Vertical", 0F, 0F, 1F)
    public val modeValue = ListValue("Mode", arrayOf("GrimReduce","Grim-Motion","HytFwNmslCnm","test", "Hytjump1", "vanilla","test1","test2", "Jumping","TestAAC5","HytTestAAC4","HytBest","HuaYuTingJump","Custom","AAC4","Simple", "SimpleFix","AAC", "AACPush", "AACZero",
        "Reverse", "SmoothReverse", "Jump", "AAC5Reduce", "HytPacketA" ,"Glitch","HytCancel","HytTick","Vanilla","HytTest","HytNewTest","HytPacket","NewAAC4","Hyt","FeiLe","HytMotion","NewHytMotion","HytPacketB","HytMotionB","HytPacketFix","NoXZ"), "Vanilla")
    private val aac4XZReducerValue = FloatValue("AAC4XZReducer", 1.36F, 1F, 3F)
    private val newaac4XZReducerValue = FloatValue("NewAAC4XZReducer", 0.45F, 0F, 1F)

    private val velocityTickValue = IntegerValue("VelocityTick", 1, 0, 10)
    // Reverse
    private val reverseStrengthValue = FloatValue("ReverseStrength", 1F, 0.1F, 1F)
    private val reverse2StrengthValue = FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F)

    private val hytpacketaset = FloatValue("HytPacketASet", 0.35F, 0.1F, 1F)
    private val hytpacketbset = FloatValue("HytPacketBSet", 0.5F, 1F, 1F)
    // AAC Push
    private val aacPushXZReducerValue = FloatValue("AACPushXZReducer", 2F, 1F, 3F)
    private val aacPushYReducerValue = BoolValue("AACPushYReducer", true)
    public var block: IBlock? = null

    private val noFireValue = BoolValue("noFire", false)
    private val cobwebValue = BoolValue("NoCobweb", true)

    private val onlyCombatValue = BoolValue("OnlyCombat", false)
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val hytGround = BoolValue("HytGround", true)

    //Custom
    private val customX = FloatValue("CustomX",0F,0F,1F)
    private val customYStart = BoolValue("CanCustomY",false)
    private val customY = FloatValue("CustomY",1F,1F,2F)
    private val customZ = FloatValue("CustomZ",0F,0F,1F)
    private val customC06FakeLag = BoolValue("CustomC06FakeLag",false)

    private var huayutingjumpflag = false

    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var
            velocityInput = false
    private var canCleanJump = false
    private var velocityTick = 0
    // SmoothReverse
    private var reverseHurt = false

    private var jumpingflag = false
    // AACPush
    private var jump = false
    private var canCancelJump = false
    override val tag: String
        get() = if(modeValue.get().toLowerCase() == "testaac5") "Simple" else modeValue.get()

    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb )
            return
        if ((onlyGroundValue.get() && !mc.thePlayer!!.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }
        if (noFireValue.get() && mc.thePlayer!!.burning) return
        when (modeValue.get().toLowerCase()) {
            "grimreduce"->{
                if (thePlayer.hurtTime > 0){
                    thePlayer.motionX += -1.0E-7
                    thePlayer.motionY += -1.0E-7
                    thePlayer.motionZ += -1.0E-7
                    thePlayer.isAirBorne = true
                }
            }
            "grim-motion" -> {
                if ( thePlayer.hurtTime > 0) {
                    thePlayer.motionX += -1.1E-7
                    thePlayer.motionY += -1.1E-7
                    thePlayer.motionZ += -1.2E-7
                    thePlayer.isAirBorne = true
                }
            }
            "test" -> {
                if(mc.thePlayer!!.hurtTime > 0 && jumpingflag) {
                    if(mc.thePlayer!!.onGround){
                        mc.thePlayer!!!!.hurtTime <= 2
                        mc.thePlayer!!.motionX *= 0.1
                        mc.thePlayer!!.motionZ *= 0.1
                        mc.thePlayer!!.hurtTime <= 4
                        mc.thePlayer!!.motionX *= 0.2
                        mc.thePlayer!!.motionZ *= 0.2
                    }else if(mc.thePlayer!!.hurtTime <= 2) {
                        mc.thePlayer!!.motionX *= 0.1
                        mc.thePlayer!!.motionZ *= 0.1
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                    jumpingflag = false
                }
            }
            "test1" -> {
                if(mc.thePlayer!!.hurtTime > 0 && jumpingflag) {
                    mc.thePlayer!!!!.hurtTime <= 2
                    mc.thePlayer!!.motionX *= 0.4
                    mc.thePlayer!!.motionZ *= 0.4
                    mc.thePlayer!!.hurtTime <= 4
                    mc.thePlayer!!.motionX *= 0.5
                    mc.thePlayer!!.motionZ *= 0.5
                }else if(mc.thePlayer!!.hurtTime <= 9) {
                    mc.thePlayer!!.motionX *= 0.6
                    mc.thePlayer!!.motionZ *= 0.6
                }
                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                jumpingflag = false
            }
            "test2" -> {
                if(mc.thePlayer!!.hurtTime > 0 && jumpingflag) {
                    mc.thePlayer!!!!.hurtTime <= 6
                    mc.thePlayer!!.motionX *= 0.3
                    mc.thePlayer!!.motionZ *= 0.3
                    mc.thePlayer!!.hurtTime <= 4
                    mc.thePlayer!!.motionX *= 0.5
                    mc.thePlayer!!.motionZ *= 0.5
                }else{
                    mc.thePlayer!!.motionX *= 0.3
                    mc.thePlayer!!.motionZ *= 0.3
                }
                mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                jumpingflag = false
            }
            "Hytjump1" -> {
                thePlayer.noClip = velocityInput

                if (thePlayer.hurtTime == 7)
                    thePlayer.motionX *= 0
                thePlayer.motionZ *= 0
                thePlayer.motionY = 0.42

                mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ, true))
            }
            "hytfwnmslcnm"->{
                if(mc.thePlayer!!.hurtTime > 0 && jumpingflag) {
                    if(mc.thePlayer!!.onGround){
                        mc.thePlayer!!!!.hurtTime <= 6
                        mc.thePlayer!!.motionX *= 0.326934583
                        mc.thePlayer!!.motionZ *= 0.326934583
                        mc.thePlayer!!.hurtTime <= 4
                        mc.thePlayer!!.motionX *= 0.428534723
                        mc.thePlayer!!.motionZ *= 0.428534723
                    }else if(mc.thePlayer!!.hurtTime <= 9) {
                        mc.thePlayer!!.motionX *= 0.326934583
                        mc.thePlayer!!.motionZ *= 0.326934583
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                    jumpingflag = false
                }
            }
            "testaac5"->{
                if(hytGround.get()){
                    if (thePlayer.hurtTime > 0 && !thePlayer.isDead && thePlayer.hurtTime <=5 && thePlayer.onGround) {
                        thePlayer.motionX *= 0.35
                        thePlayer.motionZ *= 0.35
                        thePlayer.motionY *= 0.001
                        thePlayer.motionY /= 0.01F
                    }
                }else{
                    if (thePlayer.hurtTime > 0 && !thePlayer.isDead && thePlayer.hurtTime <=5 ) {
                        thePlayer.motionX *= 0.35
                        thePlayer.motionZ *= 0.35
                        thePlayer.motionY *= 0.001
                        thePlayer.motionY /= 0.01F
                    }
                }
            }

            "jump" -> if (thePlayer.hurtTime > 0 && thePlayer.onGround) {
                thePlayer.motionY = 0.42

                val yaw = thePlayer.rotationYaw * 0.017453292F

                thePlayer.motionX -= sin(yaw) * 0.2
                thePlayer.motionZ += cos(yaw) * 0.2
            }

            "glitch" -> {
                thePlayer.noClip = velocityInput

                if (thePlayer.hurtTime == 7)
                    thePlayer.motionY = 0.4

                velocityInput = false
            }
            "feile"->{
                if(thePlayer.onGround){
                    canCleanJump = true
                    thePlayer.motionY = 1.5
                    thePlayer.motionZ = 1.2
                    thePlayer.motionX = 1.5
                    if(thePlayer.onGround && velocityTick > 2) {
                        velocityInput = false
                    }
                }
            }
            "aac5reduce" -> {
                if (thePlayer.hurtTime > 1 && velocityInput) {
                    thePlayer.motionX *= 0.81
                    thePlayer.motionZ *= 0.81
                }
                if (velocityInput && (thePlayer.hurtTime <5 || thePlayer.onGround) && velocityTimer.hasTimePassed(120L)) {
                    velocityInput = false
                }
            }

            "huayutingjump" -> {
                if(mc.thePlayer!!.hurtTime > 0 && huayutingjumpflag) {
                    if(mc.thePlayer!!.onGround){
                        if (mc.thePlayer!!.hurtTime <= 6) {
                            mc.thePlayer!!.motionX *= 0.600151164
                            mc.thePlayer!!.motionZ *= 0.600151164
                        }
                        if (mc.thePlayer!!.hurtTime <= 4) {
                            mc.thePlayer!!.motionX *= 0.700151164
                            mc.thePlayer!!.motionZ *= 0.700151164
                        }
                    }else if(mc.thePlayer!!.hurtTime <= 9) {
                        mc.thePlayer!!.motionX *= 0.6001421204
                        mc.thePlayer!!.motionZ *= 0.6001421204
                    }
                    mc.netHandler.addToSendQueue(classProvider.createCPacketEntityAction(mc.thePlayer!!, ICPacketEntityAction.WAction.START_SNEAKING))
                    huayutingjumpflag = false
                }
            }
            "hyttick" -> {
                if(velocityTick > velocityTickValue.get()) {
                    if(thePlayer.motionY > 0) thePlayer.motionY = 0.0
                    thePlayer.motionX = 0.0
                    thePlayer.motionZ = 0.0
                    thePlayer.jumpMovementFactor = -0.00001f
                    velocityInput = false
                }
                if(thePlayer.onGround && velocityTick > 1) {
                    velocityInput = false
                }
            }
            "reverse" -> {
                if (!velocityInput)
                    return

                if (!thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.speed * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(80L))
                    velocityInput = false
            }

            "aac4" -> {
                if (!thePlayer.onGround) {
                    if (velocityInput) {
                        thePlayer.speedInAir = 0.02f
                        thePlayer.motionX *= 0.6
                        thePlayer.motionZ *= 0.6
                    }
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    thePlayer.speedInAir = 0.02f
                }
            }
            "newaac4"->{
                if (thePlayer.hurtTime > 0 && !thePlayer.onGround){
                    val reduce = newaac4XZReducerValue.get()
                    thePlayer.motionX *= reduce
                    thePlayer.motionZ *= reduce
                }
            }


            "hyttestaac4"->{
                if (thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb){
                    return
                }
                if (!thePlayer.onGround) {
                    if (velocityInput) {
                        thePlayer.speedInAir = 0.02f
                        thePlayer.motionX *= 0.6
                        thePlayer.motionZ *= 0.6
                    }
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    thePlayer.speedInAir = 0.02f
                }
            }
            "hytbest"->{
                if (thePlayer.hurtTime > 0){
                    thePlayer.motionX /= 1
                    thePlayer.motionZ /= 1
                }
            }
            "smoothreverse" -> {
                if (!velocityInput) {
                    thePlayer.speedInAir = 0.02F
                    return
                }

                if (thePlayer.hurtTime > 0)
                    reverseHurt = true

                if (!thePlayer.onGround) {
                    if (reverseHurt)
                        thePlayer.speedInAir = reverse2StrengthValue.get()
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }

            "aac" -> if (velocityInput && velocityTimer.hasTimePassed(80L)) {
                thePlayer.motionX *= horizontalValue.get()
                thePlayer.motionZ *= horizontalValue.get()
                //mc.thePlayer.motionY *= verticalValue.get() ?
                velocityInput = false
            }
            "hyt" -> {
                if (thePlayer.hurtTime > 0 && !thePlayer.onGround) {
                    thePlayer.motionX *= 0.45f
                    thePlayer.motionZ *= 0.65f
                }
            }

            "hytpacket" ->{
                if(hytGround.get()){
                    if (thePlayer.hurtTime > 0 && !thePlayer.isDead && thePlayer.hurtTime <=5 && thePlayer.onGround) {
                        thePlayer.motionX *= 0.5
                        thePlayer.motionZ *= 0.5
                        thePlayer.motionY /= 1.781145F
                    }
                }else{
                    if (thePlayer.hurtTime > 0 && !thePlayer.isDead && thePlayer.hurtTime <=5 ) {
                        thePlayer.motionX *= 0.5
                        thePlayer.motionZ *= 0.5
                        thePlayer.motionY /= 1.781145F
                    }
                }

            }

            "hytmotion" ->{
                if(hytGround.get()){
                    if (thePlayer.hurtTime > 0 && !thePlayer.isDead && thePlayer.hurtTime <=5 && thePlayer.onGround) {
                        thePlayer.motionX *= 0.4
                        thePlayer.motionZ *= 0.4
                        thePlayer.motionY *= 0.381145F
                        thePlayer.motionY /= 1.781145F
                    }
                }else{
                    if (thePlayer.hurtTime > 0 && !thePlayer.isDead && thePlayer.hurtTime <=5) {
                        thePlayer.motionX *= 0.4
                        thePlayer.motionZ *= 0.4
                        thePlayer.motionY *= 0.381145F
                        thePlayer.motionY /= 1.781145F
                    }
                }

            }

            "hytmotionb" ->{
                if (thePlayer.hurtTime > 0 && !thePlayer.isDead && !thePlayer.onGround) {
                    if (!thePlayer.isPotionActive(classProvider.getPotionEnum(PotionType.MOVE_SPEED))) {
                        thePlayer.motionX *= 0.451145F
                        thePlayer.motionZ *= 0.451145F
                    }
                }
            }

            "newhytmotion" ->{
                if (thePlayer.hurtTime > 0 && !thePlayer.isDead && !thePlayer.onGround) {
                    if(!thePlayer.isPotionActive(classProvider.getPotionEnum(PotionType.MOVE_SPEED))) {
                        thePlayer.motionX *= 0.47188
                        thePlayer.motionZ *= 0.47188
                        if(thePlayer.motionY == 0.42 || thePlayer.motionY > 0.42) thePlayer.motionY *= 0.4
                    }else{
                        thePlayer.motionX *= 0.65025
                        thePlayer.motionZ *= 0.65025
                        if(thePlayer.motionY == 0.42 || thePlayer.motionY > 0.42) thePlayer.motionY *= 0.4
                    }
                }
            }

            "aacpush" -> {
                if (jump) {
                    if (thePlayer.onGround)
                        jump = false
                } else {
                    // Strafe
                    if (thePlayer.hurtTime > 0 && thePlayer.motionX != 0.0 && thePlayer.motionZ != 0.0)
                        thePlayer.onGround = true

                    // Reduce Y
                    if (thePlayer.hurtResistantTime > 0 && aacPushYReducerValue.get()
                        && !LiquidBounce.moduleManager[Speed::class.java]!!.state)
                        thePlayer.motionY -= 0.014999993
                }




                // Reduce XZ
                if (thePlayer.hurtResistantTime >= 19) {
                    val reduce = aacPushXZReducerValue.get()

                    thePlayer.motionX /= reduce
                    thePlayer.motionZ /= reduce
                }
            }

            "custom" -> {
                if (thePlayer.hurtTime > 0 && !thePlayer.isDead && !mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.MOVE_SPEED)) && !mc.thePlayer!!.isInWater) {
                    thePlayer.motionX *= customX.get()
                    thePlayer.motionZ *= customZ.get()
                    if(customYStart.get())thePlayer.motionY /= customY.get()
                    if (customC06FakeLag.get()) mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosLook(thePlayer.posX, thePlayer.posY, thePlayer.posZ, thePlayer.rotationYaw, thePlayer.rotationPitch, thePlayer.onGround))
                }
            }

            "aaczero" -> if (thePlayer.hurtTime > 0) {
                if (!velocityInput || thePlayer.onGround || thePlayer.fallDistance > 2F)
                    return

                thePlayer.motionY -= 1.0
                thePlayer.isAirBorne = true
                thePlayer.onGround = true
            } else
                velocityInput = false
        }


    }
    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        block = event.block
    }
//    @NativeMethod
var hytCount = 24
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (classProvider.isSPacketEntityVelocity(packet)) {
            val packetEntityVelocity = packet.asSPacketEntityVelocity()


            if (noFireValue.get() && mc.thePlayer!!.burning) return
            if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer)
                return

            velocityTimer.reset()

            when (modeValue.get().toLowerCase()) {
                "noxz" -> {
                    if (packetEntityVelocity.motionX == 0 && packetEntityVelocity.motionZ == 0) {
                        return
                    }
                    val ka = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
                    val target = LiquidBounce.combatManager.getNearByEntity(ka.rangeValue.get() + 1) ?: return
                    mc.thePlayer!!.motionX = 0.0
                    mc.thePlayer!!.motionZ = 0.0
                    packetEntityVelocity.motionX = 0
                    packetEntityVelocity.motionZ = 0
                    for (i in 0..hytCount) {
                        mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketUseEntity(target,ICPacketUseEntity.WAction.ATTACK))
                        mc.thePlayer!!.sendQueue.addToSendQueue(classProvider.createCPacketAnimation())
                    }
                    if (hytCount > 12) hytCount -= 5
                }
                "jumping" -> {
                    if(packet.unwrap() is SPacketEntityVelocity) {
                        jumpingflag = true

                        if(mc.thePlayer!!.hurtTime != 0) {
                            event.cancelEvent()
                            packet.asSPacketEntityVelocity().motionX = 0
                            packet.asSPacketEntityVelocity().motionY = 0
                            packet.asSPacketEntityVelocity().motionZ = 0
                        }
                    }
                }
                "vanilla" -> {
                    event.cancelEvent()
                }

                "huayutingjump" -> {
                    huayutingjumpflag = true

                    if(mc.thePlayer!!.hurtTime != 0) {
                        event.cancelEvent()
                        packetEntityVelocity.motionX = 0
                        packetEntityVelocity.motionY = 0
                        packetEntityVelocity.motionZ = 0
                    }
                }
                "simple" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F)
                        event.cancelEvent()

                    packetEntityVelocity.motionX = (packetEntityVelocity.motionX * horizontal).toInt()
                    packetEntityVelocity.motionY = (packetEntityVelocity.motionY * vertical).toInt()
                    packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * horizontal).toInt()
                }
                "hytpacketfix"->{
                    if (thePlayer.hurtTime > 0 && !thePlayer.isDead && !mc.thePlayer!!.isPotionActive(classProvider.getPotionEnum(PotionType.MOVE_SPEED)) && !mc.thePlayer!!.isInWater) {
                        thePlayer.motionX *= 0.4
                        thePlayer.motionZ *= 0.4
                        thePlayer.motionY /= 1.45F
                    }
                    if (thePlayer.hurtTime < 1) {
                        packetEntityVelocity.motionY = 0
                    }
                    if (thePlayer.hurtTime < 5) {
                        packetEntityVelocity.motionX = 0
                        packetEntityVelocity.motionZ = 0
                    }
                }
                "hyttest" -> {
                    if (thePlayer.onGround) {
                        canCancelJump = false
                        packetEntityVelocity.motionX = (0.985114).toInt()
                        packetEntityVelocity.motionY = (0.885113).toInt()
                        packetEntityVelocity.motionZ = (0.785112).toInt()
                        thePlayer.motionX /= 1.75
                        thePlayer.motionZ /= 1.75
                    }
                }


                "hytnewtest" -> {
                    if (thePlayer.onGround) {
                        velocityInput = true
                        val yaw = thePlayer.rotationYaw * 0.017453292F
                        packetEntityVelocity.motionX = (packetEntityVelocity.motionX * 0.75).toInt()
                        packetEntityVelocity.motionZ = (packetEntityVelocity.motionZ * 0.75).toInt()
                        thePlayer.motionX -= sin(yaw) * 0.2
                        thePlayer.motionZ += cos(yaw) * 0.2
                    }
                }

                "hytpacketa" -> {
                    packetEntityVelocity.motionX =
                        (packetEntityVelocity.motionX * hytpacketaset.get() / 1.5).toInt()
                    packetEntityVelocity.motionY = (0.7).toInt()
                    packetEntityVelocity.motionZ =
                        (packetEntityVelocity.motionZ * hytpacketaset.get() / 1.5).toInt()
                    event.cancelEvent()
                }

                "hytpacketb" -> {
                    packetEntityVelocity.motionX =
                        (packetEntityVelocity.motionX * hytpacketbset.get() / 2.5).toInt()
                    packetEntityVelocity.motionY =
                        (packetEntityVelocity.motionY * hytpacketbset.get() / 2.5).toInt()
                    packetEntityVelocity.motionZ =
                        (packetEntityVelocity.motionZ * hytpacketbset.get() / 2.5).toInt()
                }
                "hyttestaac4"->{
                    if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != mc.thePlayer)return
                    velocityTimer.reset()
                    velocityInput = true
                }

                "aac", "aac4", "reverse", "smoothreverse", "aac5reduce","aaczero" -> velocityInput = true

                "hyttick" -> {
                    velocityInput = true
                    val horizontal = 0F
                    val vertical = 0F

                    event.cancelEvent()

                }

                "glitch" -> {
                    if (!thePlayer.onGround)
                        return

                    velocityInput = true
                    event.cancelEvent()
                }

                "hytcancel" -> {
                    event.cancelEvent()
                }
            }

        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        val thePlayer = mc.thePlayer

        if (thePlayer == null || thePlayer.isInWater || thePlayer.isInLava || thePlayer.isInWeb)
            return

        when (modeValue.get().toLowerCase()) {
            "aacpush" -> {
                jump = true

                if (!thePlayer.isCollidedVertically)
                    event.cancelEvent()
            }
            "aac4" -> {
                if (thePlayer.hurtTime > 0) {
                    event.cancelEvent()
                }
            }
            "aaczero" -> if (thePlayer.hurtTime > 0)
                event.cancelEvent()

        }

    }
}
