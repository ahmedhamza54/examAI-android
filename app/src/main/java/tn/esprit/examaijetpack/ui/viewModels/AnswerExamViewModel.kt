package tn.esprit.examaijetpack.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import tn.esprit.examaijetpack.Constants

class AnswerExamViewModel : ViewModel() {

    private val client = OkHttpClient()

    fun saveExamAttempt(studentId: String, examId: String, answerText: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = Constants.BACKEND_URL +"/exam-attempts"

                // Create JSON payload
                val payload = JSONObject().apply {
                    put("studentId", studentId)
                    put("examId", examId)
                    put("answerText", answerText)
                }.toString()

                // Create request body
                val body: RequestBody = payload.toRequestBody("application/json".toMediaType())

                // Build request
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                // Execute request
                val response = client.newCall(request).execute()
                Log.d("ExamAttempt", "Response:"+ response.code)

                if (response.code == 201) {
                    onSuccess()
                    Log.d("ExamAttempt", "Exam attempt saved successfully")
                } else {
                    val errorBody = response.body?.string() ?: "Unknown error"
                    onError("Failed to save: $errorBody")
                }
            } catch (e: Exception) {
                onError("Error: ${e.localizedMessage}")
            }
        }
    }
}
