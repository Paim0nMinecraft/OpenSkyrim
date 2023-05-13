package net.ccbluex.liquidbounce.api.minecraft.item

import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock

interface IItemBucket : IItem {
    val isFull: IBlock
}