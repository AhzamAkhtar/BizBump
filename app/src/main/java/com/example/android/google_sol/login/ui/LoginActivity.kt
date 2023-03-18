package com.example.android.google_sol.login.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.android.google_sol.R
import com.example.android.google_sol.databinding.ActivityLoginBinding
import com.example.android.google_sol.login.util.LoginViewModal

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val viewModel : LoginViewModal by viewModels()
    companion object {
        const val LOGIN_SCREEN = 1
        const val SIGNUP_SCREEN = 2
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.setScreenState(LOGIN_SCREEN)
        setupObserver()
    }

    private fun setupObserver(){
         viewModel.screenState.observe(this) {
             when (it) {
                 LOGIN_SCREEN -> {
                     loginScreen()
                 }
                 SIGNUP_SCREEN -> {
                     signupScreen()
                 }
             }
         }
    }

    private fun signupScreen() {
        val fragmentManagaer = supportFragmentManager
        val fragmentTransaction = fragmentManagaer.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile,FragmentSignup()).commit()
    }

    private fun loginScreen() {
        val fragmentManagaer = supportFragmentManager
        val fragmentTransaction = fragmentManagaer.beginTransaction()
        fragmentTransaction.replace(R.id.mainUserProfile,FragmentLogin()).commit()
    }
}