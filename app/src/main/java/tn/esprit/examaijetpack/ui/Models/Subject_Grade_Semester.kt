package tn.esprit.examaijetpack.ui.Models

data class Subject_Grade_Semester (
    val subject: String,
    val grade: String,
    val semester: String
) {
    companion object {
        // Initializer method
        fun initialize(subject: String, grade: String, semester: String): Subject_Grade_Semester{
            return Subject_Grade_Semester(subject, grade, semester)
        }
    }
}