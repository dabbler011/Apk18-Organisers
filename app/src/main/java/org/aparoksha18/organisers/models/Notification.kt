package org.aparoksha18.organisers.models

/**
 * Created by akshat on 4/10/17.
 */

data class Notification(
        var description: String = "",
        var senderName: String="",
        var timestamp: Long = 0,
        var title: String = "",
        var verified: Boolean = false,
        var eventID: Int = 0
)