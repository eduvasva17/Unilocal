package com.example.unilocal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.unilocal.R
import com.example.unilocal.activities.ForgotPassActivity
import com.example.unilocal.activities.MapActivity
import com.example.unilocal.databinding.FragmentSettingsBinding
import com.example.unilocal.ui.login.LoginActivity


class SettingsFragment : Fragment() {

    lateinit var  binding: FragmentSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.buttonLogOut.setOnClickListener {
            val sp = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE)
            sp.edit().clear().apply()

            goToLogin()
            requireActivity().finish()
        }
        binding.buttonChangePass.setOnClickListener {
            goToChangePass()
        }

        return binding.root
    }

    private fun goToChangePass() {
        val intent = Intent(context, ForgotPassActivity::class.java)
        startActivity(intent)
    }

    private fun goToLogin(){
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }

}