package tn.esprit.examaijetpack.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import tn.esprit.examaijetpack.Constants.BACKEND_URL
import android.content.Context
import tn.esprit.examaijetpack.ui.screens.login.dataStore

class LoginViewModel : ViewModel() {

    // Observable email and password
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private val teacherIdKey = stringPreferencesKey("teacher_id")
    private val specializationKey = stringPreferencesKey("specialization")
    private val studentIdKey = stringPreferencesKey("student_id")
    private val gradeKey = stringPreferencesKey("grade")

    // Loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Error message state
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // Login success state
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _teacherId = MutableLiveData<String?>()
    val teacherId: LiveData<String?> get() = _teacherId

    private val _specialization = MutableLiveData<String?>()
    val specialization: LiveData<String?> get() = _specialization

    private val _studentId = MutableLiveData<String?>()
    val studentId: LiveData<String?> get() = _studentId

    private val _grade = MutableLiveData<String?>()
    val grade: LiveData<String?> get() = _grade

    fun saveTeacherId(context: Context,teacherId: String) {
        viewModelScope.launch {
           // val teacherIdKey = stringPreferencesKey("teacher_id")
            context.dataStore.edit { preferences ->
                preferences[teacherIdKey] = teacherId
            }
        }
    }
    fun saveSpecialization(context: Context,specialization: String) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[specializationKey] = specialization
            }
        }
    }

    fun saveStudentId(context: Context,studentId: String) {
        viewModelScope.launch {
            // val teacherIdKey = stringPreferencesKey("teacher_id")
            context.dataStore.edit { preferences ->
                preferences[studentIdKey] = studentId
            }
        }
    }
    fun saveGrade(context: Context,grade: String) {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                preferences[gradeKey] = grade
            }
        }
    }

    fun login() {
        // Validate input
        if (email.value.isNullOrEmpty() || password.value.isNullOrEmpty()) {
            _errorMessage.value = "Email and Password cannot be empty."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("$BACKEND_URL/auth/login")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Send JSON payload
                val payload = JSONObject().apply {
                    put("email", email.value)
                    put("password", password.value)
                }
                connection.outputStream.use { it.write(payload.toString().toByteArray()) }

                // Parse response
                val responseCode = connection.responseCode
                val responseBody = connection.inputStream.bufferedReader().readText()

                withContext(Dispatchers.Main) {
                    _isLoading.value = false

                    if (responseCode == HttpURLConnection.HTTP_CREATED) {
                        val jsonResponse = JSONObject(responseBody)
                        if (jsonResponse.has("accessToken")) {
                            _loginSuccess.value = true // Login successful
                            _teacherId.value = jsonResponse.optString("teacherId")
                            _specialization.value = jsonResponse.optString("specialization")
                            _studentId.value = jsonResponse.optString("studentId")
                            _grade.value = jsonResponse.optString("grade")
                        } else {
                            _errorMessage.value = "Unexpected response from server."
                        }
                    } else {
                        _errorMessage.value = "Login failed: ${connection.responseMessage}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    _errorMessage.value = "Error: ${e.localizedMessage}"
                }
            }
        }
    }
}
