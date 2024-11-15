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

class LoginViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    // LiveData to observe for successful login
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess


    fun login() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://192.168.125.121:3000/auth/login") // Replace with actual backend URL
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Create JSON payload
                val payload = JSONObject().apply {
                    put("email", email.value)
                    put("password", password.value)
                }

                connection.outputStream.use { it.write(payload.toString().toByteArray()) }

                val contentType = connection.getHeaderField("Content-Type")
                val responseBody = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    isLoading.value = false

                    // Check if response is JSON
                    if (contentType != null && contentType.contains("application/json")) {
                        val jsonResponse = JSONObject(responseBody)

                        // Check for the presence of all required keys in the JSON response
                        if (jsonResponse.has("accessToken") && jsonResponse.has("refreshToken") && jsonResponse.has("userId")) {
                            // Successful login
                            val accessToken = jsonResponse.getString("accessToken")
                            val refreshToken = jsonResponse.getString("refreshToken")
                            val userId = jsonResponse.getString("userId")

                            // Trigger successful login navigation
                            _loginSuccess.value = true
                        } else {
                            // Error: response does not contain required keys
                            errorMessage.value = jsonResponse.optString("message", "An unexpected error occurred.")
                            Log.e("Login Error", "Response missing required keys: $jsonResponse")
                        }
                    } else {
                        // Non-JSON response (e.g., HTML error page)
                        errorMessage.value = "Unexpected response from the server."
                        Log.e("Login Error", "Non-JSON response: $responseBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    Log.e("Login Error", "Exception: ${e.localizedMessage}")
                    errorMessage.value = "An error occurred. Please try again."
                }
            }
        }
    }
}
