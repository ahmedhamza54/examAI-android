package tn.esprit.examaijetpack.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.esprit.examaijetpack.Constants.BACKEND_URL
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.Charsets.UTF_8

class EditViewModel : ViewModel() {

    var examText: String = ""
        private set

    fun updateText(newText: String) {
        examText = newText
    }

    fun saveExam(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val backendUrl = "$BACKEND_URL/exams/$id"
                val connection = URL(backendUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val requestBody = """{"text": "$examText"}"""
                connection.outputStream.write(requestBody.toByteArray(UTF_8))

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
