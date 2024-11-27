package tn.esprit.examaijetpack.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log
import tn.esprit.examaijetpack.Constants.BACKEND_URL

class SignUpViewModel : ViewModel() {

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    private val _termsAccepted = MutableLiveData<Boolean>(false)
    val termsAccepted: LiveData<Boolean> get() = _termsAccepted

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _signUpSuccess = MutableLiveData<Boolean>()
    val signUpSuccess: LiveData<Boolean> get() = _signUpSuccess

    fun setTermsAccepted(accepted: Boolean) {
        _termsAccepted.value = accepted
    }

    fun signUp() {
        if (name.value.isNullOrEmpty() || email.value.isNullOrEmpty() ||
            password.value.isNullOrEmpty() || confirmPassword.value.isNullOrEmpty()) {
            _errorMessage.value = "All fields are required."
            return
        }

        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Passwords do not match."
            return
        }

        if (_termsAccepted.value != true) {
            _errorMessage.value = "You must accept the terms and conditions."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$BACKEND_URL/auth/signup")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val payload = JSONObject().apply {
                    put("name", name.value)
                    put("email", email.value)
                    put("password", password.value)
                }
                connection.outputStream.use { it.write(payload.toString().toByteArray()) }
                val responseCode = connection.responseCode
                val responseBody = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    _isLoading.value = false

                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        _signUpSuccess.value = true
                    } else {
                        _errorMessage.value = "Sign-up failed: $responseBody"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _errorMessage.value = "An error occurred: ${e.localizedMessage}"
                }
            }
        }
    }
}
