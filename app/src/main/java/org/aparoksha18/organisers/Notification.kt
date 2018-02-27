package org.aparoksha18.organisers

/**
 * Created by akshat on 4/10/17.
 */

data class Notification(
        var description: String = "",
        var senderName: String="",
        var timestamp: Long = 0,
        var title: String = ""
)