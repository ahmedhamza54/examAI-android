package tn.esprit.examaijetpack.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.esprit.examaijetpack.Constants.BACKEND_URL
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.Charsets.UTF_8
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.json.JSONObject

class EditViewModel : ViewModel() {

    var examText by mutableStateOf("")
        private set

    fun updateText(newText: String) {
        examText = newText
    }

    fun saveExam(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val backendUrl = "$BACKEND_URL/exams/$id"
                Log.d("test", "")

                val connection = URL(backendUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                Log.d("test1", "")

                // Create a proper JSON object for the request body
                val jsonObject = JSONObject()
                jsonObject.put("text", examText)

                Log.d("test2", "")

                try {
                    val requestBody = jsonObject.toString()
                    connection.outputStream.use { outputStream ->
                        outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
                    }
                    Log.d("test3", "Output stream written successfully")
                } catch (e: Exception) {
                    onError("Failed to write to output stream: ${e.message}")
                }


                //connection.outputStream.use { outputStream ->
                  //  outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
                //}



                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    onSuccess()
                } else {
                    onError("Failed to save exam: $responseCode")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

}
