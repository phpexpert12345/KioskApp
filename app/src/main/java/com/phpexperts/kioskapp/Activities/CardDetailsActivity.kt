package com.phpexperts.kioskapp.Activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.phpexperts.kioskapp.R
import com.phpexperts.kioskapp.Utils.Apis
import com.phpexperts.kioskapp.Utils.KioskVolleyService
import com.phpexperts.kioskapp.Utils.Util
import kotlinx.android.synthetic.main.layout_card_details.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class CardDetailsActivity :AppCompatActivity(), KioskVolleyService.KioskResult {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_card_details)
        txt_back.setOnClickListener {
            finish()
        }
        val calendar=Calendar.getInstance()
        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectExpiryDate(calendar)
        }
        edit_expiry_date.setOnClickListener {
            DatePickerDialog(this, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        linear_cancel.setOnClickListener{
            finish()
        }
        txt_continue.setOnClickListener{
            if(edit_name_card.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.enter_name_on_card),Toast.LENGTH_SHORT).show()
            }
            else if(edit_card_number.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.enter_card_number),Toast.LENGTH_SHORT).show()
            }
            else if(edit_card_number.text.toString().length<16){
                Toast.makeText(this,getString(R.string.enter_valid_card_number), Toast.LENGTH_SHORT).show()
            }
            else if(edit_expiry_date.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.select_date),Toast.LENGTH_SHORT).show()
            }
            else if(edit_cvv.text.toString().isEmpty()){
                Toast.makeText(this,getString(R.string.enter_cvv),Toast.LENGTH_SHORT).show()
            }
            else if(edit_cvv.text.toString().length<3){
                Toast.makeText(this,getString(R.string.valid_cvv), Toast.LENGTH_SHORT).show()
            }
            else {
                if(Util.CheckInternetConnection(this)){
                    SubmitCardDetails()
                }
            }
        }
    }

    fun SubmitCardDetails(){
        val kioskVolleyService=KioskVolleyService()
        kioskVolleyService.url= Apis.BASE_URL+"card_details"
        kioskVolleyService.type="card_details"
        kioskVolleyService.context=this
        val jsonObject=JSONObject()
        jsonObject.put("card_name", edit_name_card.text.toString())
        jsonObject.put("card_number", edit_card_number.text.toString())
        jsonObject.put("expiry_date", edit_expiry_date.text.toString())
        jsonObject.put("cvv", edit_cvv.text.toString())
        kioskVolleyService.kioskResult=this
        kioskVolleyService.params=jsonObject
        kioskVolleyService.createJsonRequest()
    }

    override fun onResult(response: JSONObject, type: String) {
        if(type.equals("card_details")){
            Toast.makeText(this, getString(R.string.submit_successful),Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,ActivityThankYou::class.java))
            finish()
        }
    }
    fun selectExpiryDate(calendar: Calendar){
        val myFormat = "MM/dd/yyyy"

        val sdf = SimpleDateFormat(myFormat, Locale.US)

        edit_expiry_date.setText(sdf.format(calendar.getTime()))
    }
}