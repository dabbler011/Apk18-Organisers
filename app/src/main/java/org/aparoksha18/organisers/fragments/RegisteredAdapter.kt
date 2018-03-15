package org.aparoksha18.organisers.fragments

/**
 * Created by akshat on 15/3/18.
 */
/*

class RegisteredAdapter(options: FirebaseRecyclerOptions<Notification>):  FirebaseRecyclerAdapter<Notification, NotificationAdapter.NotificationViewHolder>(options)
*/
/*

class NotificationAdapter(options: FirebaseRecyclerOptions<Notification>,
                          private val noNotifsTV : TextView)
    : FirebaseRecyclerAdapter<Notification, NotificationAdapter.NotificationViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.notification_container, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int, model: Notification) {
        holder.bindView(model)
    }

    class NotificationViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        fun bindView(notification: Notification) {

            itemView.titleTV.text = notification.title
            itemView.descriptionTV.text = notification.description

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/India"))
            calendar.timeInMillis = notification.timestamp.times(1000L)

            val sdf = SimpleDateFormat("MMM d, hh:mm a")
            sdf.timeZone = TimeZone.getTimeZone("Asia/India")

            itemView.timeTV.text = sdf.format(calendar.time)
        }
    }

    override fun getItem(position: Int): Notification {
        return super.getItem(itemCount - 1 - position)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        noNotifsTV.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
    }
}*/
