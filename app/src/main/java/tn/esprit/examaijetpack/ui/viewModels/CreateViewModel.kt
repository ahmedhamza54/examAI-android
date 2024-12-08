package tn.esprit.examaijetpack.ui.viewModels

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import tn.esprit.examaijetpack.Constants
import tn.esprit.examaijetpack.ui.Models.Exam
import okhttp3.OkHttpClient
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import tn.esprit.examaijetpack.ui.Models.Subject_Grade_Semester
import java.util.concurrent.TimeUnit

class CreateViewModel : ViewModel() {
    // StateFlows
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _examResponse = MutableStateFlow<Pair<String, String>?>(null)
    val examResponse: StateFlow<Pair<String, String>?> = _examResponse

    private val _specialization = MutableStateFlow<String?>(null)
    val specialization: StateFlow<String?> = _specialization

    // Retrofit Setup
    private val examApi: ExamApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS) // Set connection timeout to 30 seconds
            .readTimeout(60, TimeUnit.SECONDS)    // Set read timeout to 30 seconds
            .writeTimeout(60, TimeUnit.SECONDS)   // Set write timeout to 30 seconds
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BACKEND_URL + "/")
            .client(client) // Use the custom client
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExamApi::class.java)
    }

    // Function to send the request
    fun createExam(exam: Exam) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = examApi.createExam(exam)
                _examResponse.value = Pair(response.id, response.text)
            } catch (e: HttpException) {
                Log.e("HTTP Error", "Code: ${e.code()}, Message: ${e.message()}")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }




    fun clearExamResponse() {
        _examResponse.value = null
    }
    // Response Model
    data class ExamResponse(
        val id: String,
        val text: String
    )
    // API Definition
    interface ExamApi {
        @POST("exams") // Replace with the actual endpoint
        suspend fun createExam(@Body exam: Exam): ExamResponse
        @GET("exams/chapters")
        suspend fun getChapters(
            @Query("subject") subject: String,
            @Query("grade") grade: String,
            @Query("semester") semester: String
        ): List<String>

    }


    private val _chapters = MutableStateFlow<List<String>>(emptyList())
    val chapters: StateFlow<List<String>> = _chapters
    fun getChapters(subject: String, grade: String, semester: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
              //  val subject_grade_semester = Subject_Grade_Semester(subject, grade, semester)
                val fetchedChapters = examApi.getChapters(subject, grade, semester)
                _chapters.value = fetchedChapters
            } catch (e: HttpException) {
                Log.e("HTTP Error", "Code: ${e.code()}, Message: ${e.message()}")
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
