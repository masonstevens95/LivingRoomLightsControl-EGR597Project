package com.bignerdranch.android.livingroomlightscontrol

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast

import com.bignerdranch.android.livingroomlightscontrol.HttpRequestAsyncTask;
import com.bignerdranch.android.livingroomlightscontrol.sendRequest;

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.URISyntaxException

class HomeActivity : Activity(), View.OnClickListener {
    // declare switches and buttons and text inputs
    private var speakersSwitch: Switch? = null
    private var overheadSwitch: Switch? = null
    private var regularCycleButton: Button? = null
    private var ledCycleButton: Button? = null
    private var editTextIPAddress: EditText? = null
    private var editTextPortNumber: EditText? = null
    private var speakerBool: Boolean? = false
    private var overheadBool: Boolean? = false
    // get the pin number
    internal var parameterValue = ""
    // shared preferences objects used to save the IP address and port so that the user doesn't have to
    // type them next time he/she opens the app.
    lateinit var editor: SharedPreferences.Editor
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("HTTP_HELPER_PREFS", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        //assign switches
        speakersSwitch = findViewById<View>(R.id.speakersSwitch) as Switch
        overheadSwitch = findViewById<View>(R.id.overheadSwitch) as Switch

        // assign buttons
        regularCycleButton = findViewById<View>(R.id.regularCycleButton) as Button
        ledCycleButton = findViewById<View>(R.id.ledCycleButton) as Button

        // assign text inputs
        editTextIPAddress = findViewById<View>(R.id.editTextIPAddress) as EditText
        editTextPortNumber = findViewById<View>(R.id.editTextPortNumber) as EditText

        //set switch listeners
        speakersSwitch!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                //if speaker switch turns on, change bool value. Can't do anything until pattern button is pressed though.
                speakerBool = true
            } else {
                speakerBool = false
                parameterValue = "00"
            }
        }

        overheadSwitch!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                //if overhead switch turns on, change bool value. Can't do anything until pattern button is pressed though.
                overheadBool = true
            } else {
                overheadBool = false
                parameterValue = "10"
            }
        }

        // set button listener (this class)
        regularCycleButton!!.setOnClickListener(this)
        ledCycleButton!!.setOnClickListener(this)

        // get the IP address and port number from the last time the user used the app,
        // put an empty string "" is this is the first time.
        editTextIPAddress!!.setText(sharedPreferences.getString(PREF_IP, ""))
        editTextPortNumber!!.setText(sharedPreferences.getString(PREF_PORT, ""))
    }

    override fun onClick(view: View) {

        //status strings
        var statusSpeakerSwitch = ""
        var statusOverheadSwitch = ""
        // get the ip address
        val ipAddress = editTextIPAddress!!.text.toString().trim { it <= ' ' }
        // get the port number
        val portNumber = editTextPortNumber!!.text.toString().trim { it <= ' ' }


        // save the IP address and port for the next time the app is used
        editor.putString(PREF_IP, ipAddress) // set the ip address value to save
        editor.putString(PREF_PORT, portNumber) // set the port number to save
        editor.commit() // save the IP and PORT

        // We want to send several codes to arduino based on what button is clicked and if the switch is active.
        // 0 = switch inactive
        // 1 = regular speaker cycle
        // 2 = 3 LED fade overhead
        if (view.id == regularCycleButton!!.id) {
            if (speakersSwitch!!.isChecked) {
                parameterValue = "01"
            } else {
                //Toast to show that it can't do anything because it is off.
                parameterValue = "00"
                statusSpeakerSwitch = speakersSwitch!!.textOff.toString()
                Toast.makeText(applicationContext, "Speaker Switch is $statusSpeakerSwitch, can not activate pattern.", Toast.LENGTH_SHORT).show()
            }

        } else if (view.id == ledCycleButton!!.id) {
            if (overheadSwitch!!.isChecked) {
                parameterValue = "11"
            } else {
                //Toast to show that it can't do anything because it is off.
                parameterValue = "10"
                statusOverheadSwitch = overheadSwitch!!.textOff.toString()
                Toast.makeText(applicationContext, "Overhead Switch is $statusOverheadSwitch, can not activate pattern.", Toast.LENGTH_SHORT).show()
            }
        } else {
            parameterValue = "22"
        }


        // execute HTTP request
        if (ipAddress.length > 0 && portNumber.length > 0) {
            HttpRequestAsyncTask(
                    view.context, parameterValue, ipAddress, portNumber, "pin"
            ).execute()
        }
    }

    companion object {

        val PREF_IP = "PREF_IP_ADDRESS"
        val PREF_PORT = "PREF_PORT_NUMBER"
    }
}