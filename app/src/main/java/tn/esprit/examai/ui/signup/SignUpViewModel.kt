import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SignUpViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val role = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    val signUpSuccess = MutableLiveData<Boolean>()

    fun signUp() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://192.168.125.121:3000/auth/signup") // Replace with actual backend URL
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Create JSON payload
                val payload = JSONObject().apply {
                    put("name", name.value)
                    put("email", email.value)
                    put("password", password.value)
                    put("role", role.value)
                }

                connection.outputStream.use { it.write(payload.toString().toByteArray()) }

                val responseCode = connection.responseCode
                val responseBody = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        // Successful sign-up
                        signUpSuccess.value = true
                    } else {
                        // Error handling: parse and log the error message
                        val jsonResponse = JSONObject(responseBody)
                        val error = jsonResponse.optString("message", "An error occurred.")
                        errorMessage.value = error
                        Log.e("SignUp Error", error) // Log the error message
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                    val error = "An error occurred. Please try again."
                    errorMessage.value = error
                    Log.e("SignUp Error", "Exception: ${e.localizedMessage}")
                }
            }
        }
    }

}
