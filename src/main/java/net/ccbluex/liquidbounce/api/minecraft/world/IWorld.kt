package net.ccbluex.liquidbounce.api.minecraft.world

import net.ccbluex.liquidbounce.api.minecraft.block.state.IIBlockState
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.scoreboard.IScoreboard
import net.ccbluex.liquidbounce.api.minecraft.util.IAxisAlignedBB
import net.ccbluex.liquidbounce.api.minecraft.util.IMovingObjectPosition
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.api.minecraft.world.border.IWorldBorder
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos

interface IWorld {
    val isRemote: Boolean
    val scoreboard: IScoreboard
    val worldBorder: IWorldBorder

    fun getEntityByID(id: Int): IEntity?

    fun rayTraceBlocks(start: WVec3, end: WVec3): IMovingObjectPosition?
    fun rayTraceBlocks(start: WVec3, end: WVec3, stopOnLiquid: Boolean): IMovingObjectPosition?
    fun rayTraceBlocks(
        start: WVec3,
        end: WVec3,
        stopOnLiquid: Boolean,
        ignoreBlockWithoutBoundingBox: Boolean,
        returnLastUncollidableBlock: Boolean
    ): IMovingObjectPosition?

    fun getEntitiesInAABBexcluding(
        entityIn: IEntity?,
        boundingBox: IAxisAlignedBB,
        predicate: (IEntity?) -> Boolean
    ): Collection<IEntity>

    fun getBlockState2(blockPos: BlockPos): IBlockState

    fun getBlockState(blockPos: WBlockPos): IIBlockState
    fun getEntitiesWithinAABBExcludingEntity(entity: IEntity?, bb: IAxisAlignedBB): Collection<IEntity>
    fun getCollidingBoundingBoxes(entity: IEntity, bb: IAxisAlignedBB): Collection<IAxisAlignedBB>
    fun checkBlockCollision(aabb: IAxisAlignedBB): Boolean
    fun getCollisionBoxes(bb: IAxisAlignedBB): Collection<IAxisAlignedBB>
    fun getChunkFromChunkCoords(x: Int, z: Int): IChunk
}