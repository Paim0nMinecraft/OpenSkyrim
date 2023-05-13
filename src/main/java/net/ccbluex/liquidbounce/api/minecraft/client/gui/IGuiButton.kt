package net.ccbluex.liquidbounce.api.minecraft.client.gui

interface IGuiButton : IGui {
    var displayString: String
    val id: Int
    var enabled: Boolean
}