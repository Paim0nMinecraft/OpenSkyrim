package net.ccbluex.liquidbounce.api.minecraft

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.minecraft.util.EnumParticleTypes

interface IParticleManager {
    fun emitParticleAtEntity(entity: IEntity, buffer: EnumParticleTypes)
    fun spawnEffectParticle(
        particleID: Int,
        posX: Double,
        posY: Double,
        posZ: Double,
        motionX: Double,
        motionY: Double,
        motionZ: Double,
        StateId: Int
    )
}