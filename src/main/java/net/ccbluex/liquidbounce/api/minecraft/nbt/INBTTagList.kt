package net.ccbluex.liquidbounce.api.minecraft.nbt

interface INBTTagList : INBTBase {
    fun hasNoTags(): Boolean
    fun tagCount(): Int
    fun getCompoundTagAt(index: Int): INBTTagCompound
    fun appendTag(createNBTTagString: INBTBase)
}