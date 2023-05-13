package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.nbt.IJsonToNBT
import net.ccbluex.liquidbounce.api.minecraft.nbt.INBTTagCompound
import net.minecraft.nbt.JsonToNBT

object JsonToNBTImpl : IJsonToNBT {
    override fun getTagFromJson(s: String): INBTTagCompound = JsonToNBT.getTagFromJson(s).wrap()
}