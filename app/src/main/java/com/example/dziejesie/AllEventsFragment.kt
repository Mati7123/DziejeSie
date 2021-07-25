package com.example.dziejesie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dziejesie.adapters.EventsAdapter
import com.example.dziejesie.databinding.FragmentAllBinding
import kotlin.concurrent.thread

class AllEventsFragment : Fragment() {

    private lateinit var binding: FragmentAllBinding
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAllBinding.inflate(
            inflater, container, false
        ).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventsAdapter = EventsAdapter(ServiceLocator.repository, requireActivity())
        thread {
            binding.eventList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = eventsAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        eventsAdapter.load()
    }
}