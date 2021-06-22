package com.example.praktika.constants

object FirebaseAuthConstants
{
    // Общая ошибка
    const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"

    // Только ошибки регистрации
    const val ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE"
    const val ERROR_WEAK_PASSWORD = "ERROR_WEAK_PASSWORD"

    // Только ошибки входа
    const val ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
    const val ERROR_WRONG_PASSWORD = "ERROR_WRONG_PASSWORD"

    //
    const val SIGN_IN_EMAIL = "password"
    const val SIGN_IN_GOOGLE = "google.com"
}