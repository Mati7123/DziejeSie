package com.example.dziejesie.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dziejesie.DetailsActivity
import com.example.dziejesie.databinding.ItemEventBinding
import com.example.dziejesie.models.Event
import com.example.dziejesie.repository.EventsRepository
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EventsAdapter(
    private val repository: EventsRepository,
    private val context: FragmentActivity
) : RecyclerView.Adapter<EventsAdapter.EventsHolder>() {
    private val events = mutableListOf<Event>()

    inner class EventsHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(event: Event) {
            setView(event)
            setOnClickListener(event)
        }

        private fun setView(event: Event) {
            if (event.filePath.isNotEmpty()) {
                val storageRef = FirebaseStorage.getInstance().reference
                val dateRef = storageRef.storage.getReferenceFromUrl(event.filePath)
                dateRef.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(binding.eventImage)
                }
            }

            binding.eventNameItem.text = event.name
            binding.eventDateItem.text = event.date
            binding.eventLocalizationItem.text = event.localization
        }

        private fun setOnClickListener(event: Event) {
            itemView.setOnClickListener {
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("id", event.id)
                intent.putExtra("name", event.name)
                intent.putExtra("localization", event.localization)
                intent.putExtra("file", event.filePath)
                intent.putExtra("date", event.date)
                intent.putExtra("user", event.user)
                intent.putExtra("description", event.description)
                intent.putExtra("long", event.longitude.toString())
                intent.putExtra("lati", event.latitude.toString())
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = events.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return EventsHolder(binding)
    }

    override fun onBindViewHolder(holder: EventsHolder, position: Int) {
        holder.onBind(events[position])
    }

    fun addEvent(event: Event) {
        repository.add(event)
    }

    fun load() {
        repository.getAll {
            updateData(it.sortedByDescending { it.date })
        }
    }

    private fun updateData(newList: List<Event>) {
        val diffCalback = Diff(ArrayList(events), newList)
        events.apply {
            clear()
            addAll(newList)
        }
        DiffUtil.calculateDiff(diffCalback)
            .dispatchUpdatesTo(this)
    }
}

class Diff(private val oldList: List<Event>, private val newList: List<Event>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] === newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] === newList[newItemPosition]
}