package com.example.listtodoapp.authentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.listtodoapp.R
import com.example.listtodoapp.databinding.FragmentLoginBinding
import com.example.listtodoapp.sharedPref.SPApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    var TAG = "LoginFragment"
    var bundle=Bundle()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = view.findNavController()
        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {

            auth.signInWithEmailAndPassword(  binding.etUsername.text.toString(), binding.etUserPassword.text.toString())
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(requireContext(), "Authentication Success.",
                            Toast.LENGTH_SHORT).show()
                        val currentUser = auth.currentUser
                        val sp = SPApp(requireContext())
                        sp.uid = currentUser!!.uid


                        if (findNavController().currentDestination?.id == R.id.LoginFragment)
                            navController.navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        binding.tvRegister.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.LoginFragment)
                navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

    }

}




