package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.client.block.IBlock
import net.ccbluex.liquidbounce.api.minecraft.item.IItemBucket
import net.minecraft.item.ItemBucket

class ItemBucketImpl(wrapped: ItemBucket) : ItemImpl<ItemBucket>(wrapped), IItemBucket {
    override val isFull: IBlock
        get() = BlockImpl(wrapped.containedBlock)
}