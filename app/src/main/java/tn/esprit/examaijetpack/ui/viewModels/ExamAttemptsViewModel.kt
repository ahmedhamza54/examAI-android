package tn.esprit.examaijetpack.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import tn.esprit.examaijetpack.Constants
import java.util.concurrent.TimeUnit

// Data class for ExamAttempt
data class ExamAttempt(
    val _id: String,
    val studentId: String,
    val examId: String,
    val score: String,
    val answerText: String,
    val attemptDate: String
)
data class CorrectionResponse(
    val message: String,
    val correction: String
)


class ExamAttemptsViewModel : ViewModel() {

    // StateFlow to hold exam attempts
    private val _examAttempts = MutableStateFlow<List<ExamAttempt>>(emptyList())
    val examAttempts: StateFlow<List<ExamAttempt>> = _examAttempts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Retrofit Setup
    private val api: ExamAttemptsApi by lazy {
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
            .create(ExamAttemptsApi::class.java)
    }

    // Fetch Exam Attempts for a specific student
    fun fetchExamAttempts(studentId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val attempts = api.getExamAttempts(studentId)
                _examAttempts.value = attempts
            } catch (e: HttpException) {
                Log.e("HTTP Error", "Code: ${e.code()}, Message: ${e.message()}")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun correctExam(examId: String, attemptId: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { // Use IO dispatcher for network operations
            try {
                val response = api.correctExam(examId, attemptId)
                Log.d("Correction", "Message: ${response.message}, Correction: ${response.correction}")
                onComplete(true, response.correction) // Notify success with correction result
                fetchExamAttempts("67588f48560d1cdec95fc73f")
            } catch (e: HttpException) {
                Log.e("HTTP Error", "Code: ${e.code()}, Message: ${e.message()}")
                onComplete(false, "HTTP Error: ${e.message()}")
            } catch (e: Exception) {
                Log.e("General Error", e.message ?: "Unknown error")
                onComplete(false, e.message ?: "Unknown error")
            }
        }
    }



    // API Interface
    interface ExamAttemptsApi {
        @GET("exam-attempts/{studentId}")
        suspend fun getExamAttempts(@Path("studentId") studentId: String): List<ExamAttempt>
        @GET("exams/correct/{idExam}/{idAttempt}")
        suspend fun correctExam(
            @Path("idExam") examId: String,
            @Path("idAttempt") attemptId: String
        ): CorrectionResponse

    }
}
