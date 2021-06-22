@file:Suppress("MoveLambdaOutsideParentheses")

package com.example.praktika.accounthelper

import android.util.Log
import android.widget.Toast
import com.example.praktika.MainActivity
import com.example.praktika.R
import com.example.praktika.constants.FirebaseAuthConstants
import com.example.praktika.dialoghelper.DialogHelper
import com.example.praktika.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*

class AccountHelper(private val act: MainActivity)
{
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(dialogHelper: DialogHelper, email: String, password: String)
    {
        if(email.isEmpty() || password.isEmpty())
        {
            val text =
                if (email.isEmpty()) act.resources.getString(R.string.dialog_empty_email)
                else act.resources.getString(R.string.dialog_empty_password)

            dialogHelper.setTvDialogText(text)
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendEmailVerification(task.result?.user!!)
                act.uiUpdate(task.result?.user!!)
                dialogHelper.dismissDialog()
            }
            else if (task.exception is FirebaseAuthException)
            {
                val exception = task.exception as FirebaseAuthException

                if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE)
                {
                    mAuth.fetchSignInMethodsForEmail(email).addOnSuccessListener { taskFetch ->
                        if (taskFetch.signInMethods!!.all { it == FirebaseAuthConstants.SIGN_IN_GOOGLE
                                    && it != FirebaseAuthConstants.SIGN_IN_EMAIL})
                        {
                            linkEmailToGoogle(dialogHelper, email, password)
                        }
                        else
                        {
                            emailInputExceptionHandler(dialogHelper, exception)
                        }
                    }
                }
                else
                {
                    emailInputExceptionHandler(dialogHelper, exception)
                }
            }
        }


    }

    fun signInWithEmail(dialogHelper: DialogHelper, email: String, password: String)
    {
        if(email.isEmpty() || password.isEmpty())
        {
            val text =
                if (email.isEmpty()) act.resources.getString(R.string.dialog_empty_email)
                else act.resources.getString(R.string.dialog_empty_password)

            dialogHelper.setTvDialogText(text)
            return
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                act.uiUpdate(task.result?.user!!)
                dialogHelper.dismissDialog()
            }
            else if (task.exception is FirebaseAuthException)
            {
                val exception = task.exception as FirebaseAuthException
                emailInputExceptionHandler(dialogHelper, exception)
            }
        }
    }

    private fun emailInputExceptionHandler(dialogHelper: DialogHelper, exception: FirebaseAuthException)
    {
        Log.d("LogISIP", "Exception: ${exception.errorCode}")
        Toast.makeText(act, "${exception.message}", Toast.LENGTH_LONG).show()
        when(exception.errorCode)
        {
            // Общая ошибка
            FirebaseAuthConstants.ERROR_INVALID_EMAIL ->
            {
                Toast.makeText(act, "${FirebaseAuthConstants.ERROR_INVALID_EMAIL}: ${exception.message}", Toast.LENGTH_LONG).show()
                dialogHelper.setTvDialogText(act.resources.getString(R.string.dialog_error_invalid_email))
            }

            // Только регистрация
            FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE ->
            {
                Toast.makeText(act, "${FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE}: ${exception.message}", Toast.LENGTH_LONG).show()
                dialogHelper.setTvDialogText(act.resources.getString(R.string.dialog_error_email_already_in_use))

            }
            FirebaseAuthConstants.ERROR_WEAK_PASSWORD ->
            {
                Toast.makeText(act, "${FirebaseAuthConstants.ERROR_WEAK_PASSWORD}: ${exception.message}", Toast.LENGTH_LONG).show()
                dialogHelper.setTvDialogText(act.resources.getString(R.string.dialog_error_weak_password))
            }

            // Только вход
            FirebaseAuthConstants.ERROR_USER_NOT_FOUND ->
            {
                Toast.makeText(act, "${FirebaseAuthConstants.ERROR_USER_NOT_FOUND}: ${exception.message}", Toast.LENGTH_LONG).show()
                dialogHelper.setTvDialogText(act.resources.getString(R.string.dialog_error_user_not_found))

            }
            FirebaseAuthConstants.ERROR_WRONG_PASSWORD ->
            {
                Toast.makeText(act, "${FirebaseAuthConstants.ERROR_WRONG_PASSWORD}: ${exception.message}", Toast.LENGTH_LONG).show()
                dialogHelper.setTvDialogText(act.resources.getString(R.string.dialog_error_wrong_password))

            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser)
    {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                Toast.makeText(act, act.resources.getString(R.string.send_verification_done), Toast.LENGTH_LONG).show()
            }
            else
            {
                Toast.makeText(act, act.resources.getString(R.string.send_verification_error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getSignInClient(): GoogleSignInClient
    {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
        requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act, gso)
    }

    fun signInWithGoogle()
    {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signInFirebaseWithGoogle(token: String)
    {
        val credentials = GoogleAuthProvider.getCredential(token, null)
        mAuth.signInWithCredential(credentials).addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                act.uiUpdate(task.result?.user)
            }
            else
            {
                Log.d("LogISIP", "Google Sign In Exception: ${task.exception}")
            }
        }
    }

    private fun linkEmailToGoogle(dialogHelper: DialogHelper, email: String, password: String)
    {
        val credential = EmailAuthProvider.getCredential(email, password)
        mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                Toast.makeText(act, act.resources.getString(R.string.link_done), Toast.LENGTH_LONG).show()
                act.uiUpdate(task.result?.user!!)
                dialogHelper.dismissDialog()
            }
            else if (task.exception is FirebaseAuthException)
            {
                val exception = task.exception as FirebaseAuthException
                emailInputExceptionHandler(dialogHelper, exception)
            }
        }
    }

    fun resetEmailPass(dialogHelper: DialogHelper, email: String)
    {
        if(email.isEmpty())
        {
            val text =act.resources.getString(R.string.dialog_empty_email)
            dialogHelper.setTvDialogText(text)
            return
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                Toast.makeText(act, R.string.email_reset_password_was_send, Toast.LENGTH_LONG).show()
                dialogHelper.dismissDialog()
            }
            else if (task.exception is FirebaseAuthException)
            {
                val exception = task.exception as FirebaseAuthException
                emailInputExceptionHandler(dialogHelper, exception)
            }
        }
    }

    fun signOutGoogle()
    {
        getSignInClient().signOut()
    }
}