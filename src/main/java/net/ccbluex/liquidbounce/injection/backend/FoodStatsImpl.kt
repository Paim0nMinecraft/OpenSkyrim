package net.ccbluex.liquidbounce.injection.backend

import net.ccbluex.liquidbounce.api.minecraft.util.IFoodStats
import net.minecraft.util.FoodStats

class FoodStatsImpl(val wrapped: FoodStats) : IFoodStats {
    override val foodLevel: Int
        get() = wrapped.foodLevel

    override fun equals(other: Any?): Boolean {
        return other is FoodStatsImpl && other.wrapped == this.wrapped
    }
}

inline fun IFoodStats.unwrap(): FoodStats = (this as FoodStatsImpl).wrapped
inline fun FoodStats.wrap(): IFoodStats = FoodStatsImpl(this)
