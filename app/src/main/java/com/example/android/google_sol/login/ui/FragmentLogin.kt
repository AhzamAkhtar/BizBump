package com.example.android.google_sol.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.google_sol.databinding.FragmentLoginBinding
import com.example.android.google_sol.login.util.LoginViewModal

class FragmentLogin:Fragment() {
    private val binding by lazy { FragmentLoginBinding.inflate(layoutInflater) }
    private val viewModel : LoginViewModal by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSignup.setOnClickListener{
            viewModel.setScreenState(LoginActivity.SIGNUP_SCREEN)
        }
    }

}