package net.ccbluex.liquidbounce.api.minecraft.item

import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock

interface IItemBlock : IItem {
    val block: IBlock
}