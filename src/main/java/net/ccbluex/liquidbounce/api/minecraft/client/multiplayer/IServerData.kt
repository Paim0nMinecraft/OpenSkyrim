package net.ccbluex.liquidbounce.api.minecraft.client.multiplayer

interface IServerData {
    val pingToServer: Long
    val version: Int
    val gameVersion: String
    val serverMOTD: String
    val populationInfo: String
    val serverName: String
    val serverIP: String
}
