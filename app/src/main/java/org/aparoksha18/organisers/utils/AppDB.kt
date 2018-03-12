package org.aparoksha18.organisers.utils

import android.content.Context
import net.rehacktive.waspdb.WaspDb
import net.rehacktive.waspdb.WaspFactory
import org.aparoksha18.organisers.models.Event

/**
 * Created by akshat on 12/03/18.
 */


class AppDB private constructor(context: Context) {
    private val waspDB: WaspDb = WaspFactory.openOrCreateDatabase(
            context.filesDir.path,
            "eventDB",
            "effervescence17")

    private val eventHash = waspDB.openOrCreateHash("events")

    companion object : SingletonHolder<AppDB, Context>(::AppDB)

    fun getAllEvents(): MutableList<Event> = eventHash.getAllValues<Event>()

    fun storeEvents(events: List<Event>) = events.forEach { eventHash.put(it.id, it) }

}