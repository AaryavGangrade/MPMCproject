package com.example.firebasechat.activity

import LawyerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import data.LawyerContacts
import data.LawyerFirmname
import data.Coordinates
import data.LawyerName
import data.LawyerPracticeArea
import com.example.firebasechat.databinding.FragmentContactBinding
import data.Coordinates.place

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize RecyclerView and set its layout manager
        val recyclerView = binding.rvContact
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Create a list of lawyer details
        val lawyerDetails = mutableListOf<LawyerDetails>()

        // Populate lawyer details from data objects
        val names = LawyerName.names
        val contacts = LawyerContacts.contacts
        val firmNames = LawyerFirmname.firmnames
        val practiceAreas = LawyerPracticeArea.practiceareas
        val cordinate = Coordinates.place


        for (i in names.indices) {
            lawyerDetails.add(
                LawyerDetails(
                    names[i],
                    contacts[i],
                    firmNames[i],
                    practiceAreas[i],
                    cordinate[i],
                )
            )
        }

        // Create and set the adapter
        val adapter = LawyerAdapter(lawyerDetails)
        recyclerView.adapter = adapter

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
