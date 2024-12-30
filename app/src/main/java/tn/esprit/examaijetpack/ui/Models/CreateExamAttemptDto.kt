package tn.esprit.examaijetpack.ui.Models

data class CreateExamAttemptDto(
    val studentId: String,
    val examId: String,
    val answerText: String
)
