package com.example.autoconnect.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.R
import com.example.autoconnect.ui.review.ReviewFragment
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

        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.navigation_settings)
        }

        binding.buttonAbout.setOnClickListener {
            findNavController().navigate(R.id.navigation_about)
        }

        binding.buttonLeaveReview.setOnClickListener {
            findNavController().navigate(R.id.navigation_review)
            //startActivity(Intent(requireContext(), ReviewFragment::class.java))
           // requireActivity()
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
