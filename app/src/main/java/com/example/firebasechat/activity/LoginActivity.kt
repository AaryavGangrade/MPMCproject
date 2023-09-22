package com.example.firebasechat.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasechat.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            startHomeActivity()
        }

        binding.lgLogin.setOnClickListener {
            val email = binding.lgEmail.text.toString()
            val password = binding.lgPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Email and password are required")
            } else {
                signInUser(email, password)
            }
        }

        binding.lgSignUp.setOnClickListener {
            startSignUpActivity()
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Login successful")
                    binding.lgEmail.text.clear()
                    binding.lgPassword.text.clear()
                    startHomeActivity()
                } else {
                    showToast("Login failed: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun startHomeActivity() {
        val intent = Intent(this@LoginActivity,StartActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startSignUpActivity() {
        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}