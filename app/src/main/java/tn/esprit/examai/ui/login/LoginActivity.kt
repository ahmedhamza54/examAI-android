package tn.esprit.examai.ui.login

// LoginActivity.kt
import LoginViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import tn.esprit.examai.databinding.LoginBinding
import tn.esprit.examai.MainActivity
import tn.esprit.examai.R
import tn.esprit.examai.ui.signup.SignUpActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel:     LoginViewModel by viewModels()
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Bind input fields with ViewModel
        binding.emailEditText.addTextChangedListener { viewModel.email.value = it.toString() }
        binding.passwordEditText.addTextChangedListener { viewModel.password.value = it.toString() }

        // Observe errorMessage LiveData to display login error
        viewModel.errorMessage.observe(this, Observer { message ->
            binding.errorMessageTextView.text = message
            binding.errorMessageTextView.visibility = if (message.isNullOrEmpty()) View.GONE else View.VISIBLE
        })

        // Observe isLoading to manage button state
        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.loginButton.isEnabled = !isLoading
            binding.loginButton.text = if (isLoading) "Logging in..." else "Login"
        })

        // Set click listener on Login button
        binding.loginButton.setOnClickListener {
            viewModel.login()
        }

        //Set click listener on sign up text
        binding.signUpTextView.setOnClickListener {
            // Start SignUpActivity when the text is clicked
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Observe loginSuccess to navigate to MainActivity on success
        viewModel.loginSuccess.observe(this, Observer { success ->
            if (success) {
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Close LoginActivity
            }
        })

    }
}