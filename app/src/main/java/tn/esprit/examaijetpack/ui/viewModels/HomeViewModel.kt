package tn.esprit.examaijetpack.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
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

    private val _refreshTrigger = MutableStateFlow(false)
    val refreshTrigger: StateFlow<Boolean> = _refreshTrigger

    fun fetchExams(teacherId:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val examList = api.getExams(teacherId)
                _exams.value = examList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteExam(examId:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                api.deleteExam(examId)
                //_exams.value = examList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _refreshTrigger.value = !_refreshTrigger.value
    }
    fun fetchExamsByGrade(grade: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val examList = api.getExamsByGrade(grade)
                _exams.value = examList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
// API Interface
interface ExamApi {
    @GET("exams/teacher/{teacherId}") // Adjust the endpoint as per your backend API
    suspend fun getExams(@Path("teacherId") teacherId: String): List<Exam>
    @GET("exams/grade/{grade}")
    suspend fun getExamsByGrade(@Path("grade") grade: String): List<Exam>
    @DELETE("exams/{examId}")
    suspend fun deleteExam(@Path("examId") examId: String)


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