package com.iteration.kingdomino.game.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import com.iteration.kingdomino.R
import com.iteration.kingdomino.game.model.GameInfo
import timber.log.Timber
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataManager @Inject constructor() {
    private val maxHistoryCount = 100

    /**
     * Transfers a started game to the records of ended games.
     *
     * @param context the context used to access [SharedPreferences]
     * @param gameId the started game id
     */
    fun classifyStartedGame(context: Context, gameId: Int) {
        val startedGamesFile = context.getString(R.string.preferences_started_games)
        val startedGames = context.getSharedPreferences(startedGamesFile, Context.MODE_PRIVATE)

        val endedGamesFile = context.getString(R.string.preferences_started_games)
        val endedGames = context.getSharedPreferences(endedGamesFile, Context.MODE_PRIVATE)

        // Add the data to ended games
        with(endedGames.edit()){
            val gameData = startedGames.getString(gameId.toString(), null)
            gameData ?: return
            putString(gameId.toString(), gameData)
            apply()
        }

        // trim endedGames to N records
        trimEndedGames(context)

        // Remove from started games
        with(startedGames.edit()){
            remove(gameId.toString())
            apply()
        }
    }

    /**
     * Limits the size of ended games to [maxHistoryCount].
     * Any additional games recorded will be deleted from [SharedPreferences], starting from the earliest.
     *
     * @param context
     */
    private fun trimEndedGames(context: Context) {
        val endedGamesFile = context.getString(R.string.preferences_ended_games)
        val endedGames = context.getSharedPreferences(endedGamesFile, Context.MODE_PRIVATE)
        val historyCount = endedGames.all.size

        Timber.d("$endedGamesFile contains $historyCount entries")
        if(historyCount <= maxHistoryCount){
            return
        }

        val toRemove = endedGames.all.entries
            .sortedByDescending {
                val type: Type = object : TypeToken<GameInfo>() {}.type
                val history : GameInfo = Gson().fromJson(it.value.toString(), type)
                return@sortedByDescending history.creationDate.toEpochSecond(ZoneOffset.UTC)
            }
            .subList(maxHistoryCount, endedGames.all.size)
            .map { it.key }

        Timber.d("Keys to remove: $toRemove")

        with(endedGames.edit()) {
            toRemove.forEach { remove(it) }
            apply()
        }
    }


    fun saveStartedGame(context: Context, gameInfo: GameInfo) {
        val startedGamesFile = context.getString(R.string.preferences_started_games)
        val startedGames = context.getSharedPreferences(startedGamesFile, Context.MODE_PRIVATE)

        with(startedGames.edit()) {
            putString(gameInfo.gameId.toString(), gameInfo.toJson())
            apply()
        }

    }

    fun getEndedGames(context: Context) : List<GameInfo> {
        val endedGamesFile = context.getString(R.string.preferences_ended_games)
        val endedGames = context.getSharedPreferences(endedGamesFile, Context.MODE_PRIVATE)

        return endedGames.all.values.map {
            val type: Type = object : TypeToken<GameInfo>() {}.type
            return@map Gson().fromJson<GameInfo?>(it.toString(), type)
        }
    }

    fun getStartedGames(context: Context) : List<GameInfo> {
        val startedGamesFile = context.getString(R.string.preferences_started_games)
        val startedGames = context.getSharedPreferences(startedGamesFile, Context.MODE_PRIVATE)

        return startedGames.all.values.map {
            val type: Type = object : TypeToken<GameInfo>() {}.type
            return@map GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java,
                    JsonDeserializer {json, _, _ -> return@JsonDeserializer ZonedDateTime.parse(json.asString).toLocalDateTime()})
                .create()
                .fromJson<GameInfo?>(it.toString(), type)
        }
    }

    fun getGameById(context: Context, gameId: Int) : GameInfo {
        val startedGamesFile = context.getString(R.string.preferences_started_games)
        val startedGames = context.getSharedPreferences(startedGamesFile, Context.MODE_PRIVATE)

        val gameData = startedGames.getString(gameId.toString(), null)
        val type: Type = object : TypeToken<GameInfo>() {}.type
        return Gson().fromJson(gameData, type)
    }

    fun deleteStartedGame(context: Context, gameId: Int) {
        val startedGamesFile = context.getString(R.string.preferences_started_games)
        val startedGames = context.getSharedPreferences(startedGamesFile, Context.MODE_PRIVATE)
        with(startedGames.edit()) {
            remove(gameId.toString())
            apply()
        }
    }

    fun deleteEndedGame(context: Context, gameId: Int) {
        val endedGamesFile = context.getString(R.string.preferences_ended_games)
        val endedGames = context.getSharedPreferences(endedGamesFile, Context.MODE_PRIVATE)
        with(endedGames.edit()) {
            remove(gameId.toString())
            apply()
        }
    }

    fun clearSharedPreferences(context: Context) {
        clearStartedGames(context)
        clearEndedGames(context)
    }

    fun clearStartedGames(context: Context) {
        val startedGamesFile = context.getString(R.string.preferences_started_games)
        val startedGames = context.getSharedPreferences(startedGamesFile, Context.MODE_PRIVATE)
        with(startedGames.edit()) {
            clear()
            apply()
        }
    }

    fun clearEndedGames(context: Context) {
        val endedGamesFile = context.getString(R.string.preferences_ended_games)
        val endedGames = context.getSharedPreferences(endedGamesFile, Context.MODE_PRIVATE)
        with(endedGames.edit()) {
            clear()
            apply()
        }
    }

    fun displaySharedPreferences(context: Context) {
        val spStringBuilder = StringBuilder("SharedPreferences:\n")

        val endedGamesFile = context.getString(R.string.preferences_ended_games)
        val endedGames = context.getSharedPreferences(endedGamesFile, Context.MODE_PRIVATE)
        spStringBuilder.append("    . ended games: (file=$endedGamesFile)\n")
            .append("${endedGames.all}\n\n")

        val startedGamesFile = context.getString(R.string.preferences_started_games)
        val startedGames = context.getSharedPreferences(startedGamesFile, Context.MODE_PRIVATE)
        spStringBuilder.append("    . started games: (file=$startedGamesFile)\n")
            .append("${startedGames.all}\n\n")

        Timber.d(spStringBuilder.toString())
    }

}