package net.ccbluex.liquidbounce.api.minecraft.client.settings

interface IKeyBinding {
    val keyCode: Int
    var pressed: Boolean
    val isKeyDown: Boolean

    fun onTick(keyCode: Int)
}
