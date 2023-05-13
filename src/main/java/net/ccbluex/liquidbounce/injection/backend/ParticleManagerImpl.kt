/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */

package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.IParticleManager
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.minecraft.client.particle.ParticleManager
import net.minecraft.util.EnumParticleTypes

class ParticleManagerImpl(val wrapped: ParticleManager) : IParticleManager {
    override fun emitParticleAtEntity(entity: IEntity, buffer: EnumParticleTypes) =
        wrapped.emitParticleAtEntity(entity.unwrap(), buffer)

    override fun spawnEffectParticle(
        particleID: Int,
        posX: Double,
        posY: Double,
        posZ: Double,
        motionX: Double,
        motionY: Double,
        motionZ: Double,
        StateId: Int
    ) {
        wrapped.spawnEffectParticle(particleID, posX, posY, posZ, motionX, motionY, motionZ, StateId)
    }
}

inline fun IParticleManager.unwrap(): ParticleManager = (this as ParticleManagerImpl).wrapped
inline fun ParticleManager.wrap(): IParticleManager = ParticleManagerImpl(this)