package net.ccbluex.liquidbounce.api.minecraft.client.render

interface ITessellator {
    val worldRenderer: IWorldRenderer

    fun draw()
}
