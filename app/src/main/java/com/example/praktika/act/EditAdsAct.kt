package com.example.praktika.act

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.praktika.R
import com.example.praktika.databinding.ActivityEditAdsBinding
import com.example.praktika.dialogs.DialogSpinnerHelper
import com.example.praktika.frag.FragmentCloseInterface
import com.example.praktika.frag.ImageListFrag
import com.example.praktika.utils.CityHelper
import com.example.praktika.utils.ImagePicker
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil

class EditAdsAct : AppCompatActivity(), FragmentCloseInterface
{
    private val dialog = DialogSpinnerHelper()
    private lateinit var rootElement: ActivityEditAdsBinding
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(rootElement.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES)
        {
            val returnValue: ArrayList<String> = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS) as ArrayList<String>
            rootElement.scrollViewMain.visibility = View.GONE
            val fm = supportFragmentManager.beginTransaction()
            fm.replace(R.id.place_holder, ImageListFrag(this, returnValue))
            fm.commit()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS ->
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //Pix.start(context, Options.init().setRequestCode(100))
                    ImagePicker.getImages(this, 3)
                }
                else
                {
                    Toast.makeText(this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun onClickSelectCountry(view: View)
    {
        val listCountry = CityHelper.getAllCountries(this)
        rootElement.tvCity.text = getString(R.string.select_city)
        dialog.showSpinnerDialog(this, listCountry, rootElement.tvCountry)
    }

    fun onClickSelectCity(view: View)
    {
        val selectedCountry = rootElement.tvCountry.text.toString()
        if(selectedCountry != getString(R.string.select_country))
        {
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listCity, rootElement.tvCity)
        }
        else
        {
            Toast.makeText(this, this.resources.getString(R.string.attention_country), Toast.LENGTH_LONG).show()
        }
    }

    fun onClickGetImages(view: View)
    {
        ImagePicker.getImages(this, 3)
    }

    override fun onFragClose()
    {
        rootElement.scrollViewMain.visibility = View.VISIBLE
    }
}