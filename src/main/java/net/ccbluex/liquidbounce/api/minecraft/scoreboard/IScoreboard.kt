package net.ccbluex.liquidbounce.api.minecraft.scoreboard

interface IScoreboard {
    fun getPlayersTeam(name: String?): ITeam?
    fun getObjectiveInDisplaySlot(index: Int): IScoreObjective?
    fun getSortedScores(objective: IScoreObjective): Collection<IScore>
}