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
import retrofit2.http.POST
import tn.esprit.examaijetpack.Constants
import java.util.concurrent.TimeUnit
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class RegenerateViewModel : ViewModel() {
    // StateFlows


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _regenerateResponse = MutableStateFlow<Pair<String, String>?>(null)
    val regenerateResponse: StateFlow<Pair<String, String>?> = _regenerateResponse

    // Retrofit setup
    private val regenerateApi: RegenerateApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BACKEND_URL + "/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RegenerateApi::class.java)
    }

    // Function to send regenerate request
    fun regenerateExam(id: String, text: String, prompt: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = regenerateApi.regenerateExam(RegenerateRequest(id, text, prompt))
                _regenerateResponse.value = Pair(response.id, response.text)
                Log.e("test regenerate", response.toString())
            } catch (e: HttpException) {
                Log.e("HTTP Error", "Code: ${e.code()}, Message: ${e.message()}")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear the response
    fun clearResponse() {
        _regenerateResponse.value = null
    }

    // Request model
    data class RegenerateRequest(
        val id: String,
        val text: String,
        val prompt: String
    )

    // Response model
    data class RegenerateResponse(
        val id: String,
        val text: String
    )

    // API definition
    interface RegenerateApi {
        @POST("exams/regenerate") // Replace with actual endpoint
        suspend fun regenerateExam(@Body request: RegenerateRequest): RegenerateResponse
    }
}
