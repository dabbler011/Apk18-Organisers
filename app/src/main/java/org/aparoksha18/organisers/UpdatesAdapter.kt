package org.aparoksha18.organisers

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.notification_container.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by akshat on 12/10/17.
 */
class UpdatesAdapter : RecyclerView.Adapter<UpdatesAdapter.ViewHolder>() {
    val list = ArrayList<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent!!.context)
                    .inflate(R.layout.notification_container, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    fun updateData(list: List<Notification>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun  bindItem(notification: Notification) {
            itemView.tv_title.text = notification.title
            itemView.tv_description.text = notification.description

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/India"))
            calendar.timeInMillis = notification.timestamp.times(1000L)

            val sdf = SimpleDateFormat("hh:mm a")
            //sdf.timeZone = TimeZone.getTimeZone("Asia/India")

            val time = sdf.format(calendar.time)

            sdf.applyPattern("MMM d")
            itemView.tv_timestamp.text = notification.timestamp.toString()
            itemView.tv_timestamp.text = "$time ${sdf.format(calendar.time)}"
        }
    }

}