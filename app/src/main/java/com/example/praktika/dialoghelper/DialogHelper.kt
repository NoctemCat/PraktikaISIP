package com.example.praktika.dialoghelper

import android.app.AlertDialog
import android.view.View
import com.example.praktika.MainActivity
import com.example.praktika.R
import com.example.praktika.accounthelper.AccountHelper
import com.example.praktika.databinding.SignDialogBinding

class DialogHelper(private val act: MainActivity)
{
    private val accHelper: AccountHelper = AccountHelper(act)
    private lateinit var dialog: AlertDialog
    private lateinit var rootDialogElement: SignDialogBinding

    fun createSignDialog(index:Int)
    {
        val builder = AlertDialog.Builder(act)
        rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        setDialogState(index)
        dialog = builder.create()

        rootDialogElement.btnGoogleSignIn.setOnClickListener {
            accHelper.signInWithGoogle()
        }
        rootDialogElement.btnSignUpIn.setOnClickListener {
            setOnClickSignUpIn(index)
        }
        rootDialogElement.btnForgetPass.setOnClickListener {
            setOnClickResetPassword()
        }

        dialog.show()
    }

    private fun setOnClickResetPassword()
    {
        accHelper.resetEmailPass(this ,rootDialogElement.edSignEmail.text.toString())
    }

    private fun setOnClickSignUpIn(index: Int)
    {
        if (index == DialogConst.SIGN_UP_STATE)
        {
            accHelper.signUpWithEmail(this, rootDialogElement.edSignEmail.text.toString(), rootDialogElement.edPassword.text.toString())
        }
        else
        {
            accHelper.signInWithEmail(this, rootDialogElement.edSignEmail.text.toString(), rootDialogElement.edPassword.text.toString())
        }
    }

    private fun setDialogState(index: Int)
    {
        if (index == DialogConst.SIGN_UP_STATE)
        {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.sign_up)
            rootDialogElement.btnSignUpIn.text = act.resources.getString(R.string.sign_up_action)
        }
        else
        {
            rootDialogElement.tvSignTitle.text = act.resources.getString(R.string.sign_in)
            rootDialogElement.btnSignUpIn.text = act.resources.getString(R.string.sign_in_action)
            rootDialogElement.btnForgetPass.visibility = View.VISIBLE
        }
    }

    fun getAccHelper(): AccountHelper
    {
        return accHelper
    }

    fun dismissDialog()
    {
        dialog.dismiss()
    }

    fun setTvDialogText(text: String)
    {
        rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
        rootDialogElement.tvDialogMessage.text = text
    }
}