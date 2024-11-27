package tn.esprit.examaijetpack.ui.Models

data class Exam(
    val teacherId: String,
    val subject: String,
    val grade: String,
    val chapters: List<String>,
    val difficultyLevel: Int,
    val prompt: String
)
