package tn.esprit.examai.ui.signup

import SignUpViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import tn.esprit.examai.MainActivity // Replace with actual path
import tn.esprit.examai.R
import tn.esprit.examai.databinding.SignUpBinding
import tn.esprit.examai.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: SignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize dropdowns for role, subject, and grade
        setupDropdowns()

        // Bind input fields to ViewModel
        binding.apply {
            saveInfoButton.setOnClickListener {
                if (validateInputs()) {
                    viewModel.name.value = fullNameEditText.text.toString()
                    viewModel.email.value = emailEditText.text.toString()
                    viewModel.password.value = passwordEditText.text.toString()
                    viewModel.role.value = roleSpinner.selectedItem.toString()
                    viewModel.signUp()
                }
            }

            // Observe error messages and display them if needed
            viewModel.errorMessage.observe(this@SignUpActivity, Observer { error ->
                if (!error.isNullOrEmpty()) {
                    Toast.makeText(this@SignUpActivity, error, Toast.LENGTH_LONG).show()
                }
            })



            // Observe loading state to show/hide progress bar
            viewModel.isLoading.observe(this@SignUpActivity, Observer { isLoading ->
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            })

            // Observe signUpSuccess to navigate to MainActivity on success

            viewModel.signUpSuccess.observe(this@SignUpActivity, Observer { success ->
                if (success) {
                    // Show Snackbar for successful sign-up
                    Snackbar.make(binding.root, "Sign-up successful!", Snackbar.LENGTH_LONG).show()

                    // Navigate to LoginActivity after the Snackbar is displayed
                    startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                    finish() // Close SignUpActivity
                }
            })
        }
    }

    // Set up dropdowns with sample data
    private fun setupDropdowns() {
        val roles = arrayOf("teacher", "student")
        val subjects = arrayOf("Maths", "Science", "History")
        val grades = arrayOf("8ème", "9ème", "10ème")

        binding.roleSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        binding.subjectSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        binding.gradeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, grades)
    }

    // Validate inputs before sending data to ViewModel
    private fun validateInputs(): Boolean {
        binding.apply {
            if (fullNameEditText.text.isNullOrEmpty()) {
                fullNameEditText.error = "Full name is required"
                return false
            }
            if (emailEditText.text.isNullOrEmpty()) {
                emailEditText.error = "Email is required"
                return false
            }
            if (passwordEditText.text.isNullOrEmpty()) {
                passwordEditText.error = "Password is required"
                return false
            }
            if (passwordEditText.text.toString() != confirmPasswordEditText.text.toString()) {
                confirmPasswordEditText.error = "Passwords do not match"
                return false
            }
            if (!termsCheckBox.isChecked) {
                Toast.makeText(this@SignUpActivity, "Please accept the terms", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

}
