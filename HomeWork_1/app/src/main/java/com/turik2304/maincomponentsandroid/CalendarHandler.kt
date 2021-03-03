package com.turik2304.maincomponentsandroid

import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import java.text.SimpleDateFormat
import java.util.*

private const val PROJECTION_TITLE_INDEX: Int = 0
private const val PROJECTION_DESCRIPTION_INDEX: Int = 1
private const val PROJECTION_DTSTART_INDEX: Int = 2
private const val PROJECTION_DTEND_INDEX: Int = 3

data class Event(
    val title: String,
    val startDate: String,
    val endDate: String,
    val description: String
)

class CalendarHandler {

    private val EVENT_PROJECTION: Array<String> = arrayOf(
        CalendarContract.Events.TITLE,          //0
        CalendarContract.Events.DESCRIPTION,    //1
        CalendarContract.Events.DTSTART,        //2
        CalendarContract.Events.DTEND,          //3
    )

    private val titleOfEventsList: MutableList<String?> = mutableListOf()
    private val startDateList: MutableList<Long?> = mutableListOf()
    private val endDateList: MutableList<Long?> = mutableListOf()
    private val descriptionList: MutableList<String?> = mutableListOf()

    private val currentDateInMillis = Calendar.getInstance().timeInMillis

    fun getUpcomingEvent(context: Context): Event {
        readCalendarEvents(context)
        val indexOfUpcomingEvent = getIndexOfUpcomingEvent(startDateList, currentDateInMillis)
        return if (indexOfUpcomingEvent != null) {
            val title = titleOfEventsList[indexOfUpcomingEvent] ?: "none"
            val startDate = startDateList[indexOfUpcomingEvent] ?: 0
            val endDate = endDateList[indexOfUpcomingEvent] ?: 0
            val description = descriptionList[indexOfUpcomingEvent] ?: "none"

            val formattedStartDate = getFormattedDate(startDate)
            val formattedEndDate = getFormattedDate(endDate)
            Event(title, formattedStartDate, formattedEndDate, description)
        } else {
            Event("none", "none", "none", "none")
        }
    }

    private fun readCalendarEvents(context: Context) {
        val cursor = context.contentResolver
            .query(
                Uri.parse("content://com.android.calendar/events"),
                EVENT_PROJECTION,
                null,
                null,
                null
            )
        cursor?.moveToFirst()

        while (cursor?.moveToNext() == true) {
            titleOfEventsList.add(cursor.getStringOrNull(PROJECTION_TITLE_INDEX))
            startDateList.add(cursor.getLongOrNull(PROJECTION_DTSTART_INDEX))
            endDateList.add(cursor.getLongOrNull(PROJECTION_DTEND_INDEX))
            descriptionList.add(cursor.getStringOrNull(PROJECTION_DESCRIPTION_INDEX))
        }
        cursor?.close()
    }
    //Ищем индекс ближайшего события
    private fun getIndexOfUpcomingEvent(
        listOfDateInMillis: List<Long?>,
        currentDateInMillis: Long
    ): Int? {
        //в буфер помещаются актуальные даты. Буфер - мапа, ключ - индекс события, значение - дата в мск
        val bufferMap = mutableMapOf<Int, Long?>()
        listOfDateInMillis.forEachIndexed { index, dateInMillis ->
            if (dateInMillis != null && dateInMillis > currentDateInMillis) {
                bufferMap[index] = dateInMillis - currentDateInMillis
            }
        }
        //возвращаем индекс ближайшего события
        return bufferMap.minByOrNull { it.value!! }?.key
    }

    private fun getFormattedDate(msec: Long): String {
        val longZero: Long = 0
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        return if (msec != longZero) {
            formatter.format(msec)
        } else {
            "none"
        }
    }


}