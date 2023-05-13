package net.ccbluex.liquidbounce.api.minecraft

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import javax.crypto.SecretKey

interface INetworkManager {
    fun sendPacket(packet: IPacket)
    fun enableEncryption(secretKey: SecretKey)
    fun sendPacket(packet: IPacket, any: () -> Unit)
}