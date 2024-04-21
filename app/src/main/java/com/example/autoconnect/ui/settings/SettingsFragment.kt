package com.example.autoconnect.ui.settings

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.R
import com.example.autoconnect.VehicleInfo
import com.example.autoconnect.databinding.FragmentGarageBinding
import com.example.autoconnect.databinding.FragmentHomeBinding

class SettingsFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.autoconnect.R.layout.fragment_settings, container, false)

        val buttonBack: Button = view.findViewById(R.id.buttonBack2)

        buttonBack.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.navigation_home)
        }

        return view
    }


}