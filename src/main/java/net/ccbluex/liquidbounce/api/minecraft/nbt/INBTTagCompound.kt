package net.ccbluex.liquidbounce.api.minecraft.nbt

interface INBTTagCompound : INBTBase {
    fun hasKey(name: String): Boolean
    fun getShort(name: String): Short
    fun setString(key: String, value: String)
    fun setTag(key: String, tag: INBTBase)
    fun setInteger(key: String, value: Int)
}