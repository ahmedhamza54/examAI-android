package tn.esprit.examaijetpack.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import tn.esprit.examaijetpack.Constants
import java.util.concurrent.TimeUnit

class EditViewModel : ViewModel() {

    // StateFlows
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _examText = MutableStateFlow("")
    val examText: StateFlow<String> = _examText

    private val _updateResult = MutableStateFlow<String?>(null)
    val updateResult: StateFlow<String?> = _updateResult

    // Retrofit Setup
    private val examApi: ExamApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Set connection timeout to 60 seconds
            .readTimeout(60, TimeUnit.SECONDS)    // Set read timeout to 60 seconds
            .writeTimeout(60, TimeUnit.SECONDS)   // Set write timeout to 60 seconds
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BACKEND_URL + "/")
            .client(client) // Use the custom client
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExamApi::class.java)
    }

    // Update exam text
    fun updateText(newText: String) {
        _examText.value = newText
    }

    // Save exam
    fun saveExam(id: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = UpdateExamRequest(text = _examText.value)
                val response = examApi.updateExam(id, request)
                _updateResult.value = response.message
                onSuccess(_examText.value)
                //onSuccess()
            } catch (e: HttpException) {
                val errorMessage = "HTTP Error: Code ${e.code()}, Message: ${e.message()}"
                Log.e("EditViewModel", errorMessage)
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                Log.e("EditViewModel", errorMessage)
                onError(errorMessage)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // API Definition
    interface ExamApi {
        @PUT("exams/{id}")
        suspend fun updateExam(
            @Path("id") id: String,
            @Body request: UpdateExamRequest
        ): UpdateExamResponse
    }

    // Request Model
    data class UpdateExamRequest(
        val text: String
    )

    // Response Model
    data class UpdateExamResponse(
        val message: String
    )
}
