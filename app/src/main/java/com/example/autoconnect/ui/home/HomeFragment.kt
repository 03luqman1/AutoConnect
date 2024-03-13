package com.example.autoconnect.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.R
import com.example.autoconnect.StartActivity
import com.example.autoconnect.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding.buttonSignOut.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), StartActivity::class.java))
            requireActivity().finish()
        }

        /*binding.buttonVehicleSearch.setOnClickListener {
            findNavController().popBackStack(R.id.navigation_home, false)
            findNavController().navigate(R.id.navigation_search)
        }

        binding.buttonGarage.setOnClickListener {
            findNavController().popBackStack(R.id.navigation_home, false)
            findNavController().navigate(R.id.navigation_garage)
        }*/

        binding.buttonLeaveReview.setOnClickListener {
            findNavController().navigate(R.id.navigation_search)
        }
        // Uncomment the following lines if you need to observe ViewModel data
        /*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        */

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
