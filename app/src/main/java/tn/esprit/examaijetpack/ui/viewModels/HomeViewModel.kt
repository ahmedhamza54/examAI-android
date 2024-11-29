package tn.esprit.examaijetpack.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import tn.esprit.examaijetpack.Constants



// ViewModel
class HomeViewModel : ViewModel() {
    private val api: ExamApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BACKEND_URL + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExamApi::class.java)
    }

    private val _exams = MutableStateFlow<List<Exam>>(emptyList())
    val exams: StateFlow<List<Exam>> = _exams

    fun fetchExams() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val examList = api.getExams()
                _exams.value = examList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
// API Interface
interface ExamApi {
    @GET("exams") // Adjust the endpoint as per your backend API
    suspend fun getExams(): List<Exam>
}
data class Exam(
    val _id: String,
    val teacherId: String,
    val subject: String,
    val grade: String,
    val chapters: List<String>,
    val difficultyLevel: Int,
    val prompt: String,
    val text: String
)