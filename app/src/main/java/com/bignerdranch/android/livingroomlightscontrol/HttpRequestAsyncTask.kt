package com.bignerdranch.android.livingroomlightscontrol

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask

/**
 * An AsyncTask is needed to execute HTTP requests in the background so that they do not
 * block the user interface.
 */
class HttpRequestAsyncTask
/**
 * Description: The asyncTask class constructor. Assigns the values used in its other methods.
 * @param context the application context, needed to create the dialog
 * @param parameterValue the pin number to toggle
 * @param ipAddress the ip address to send the request to
 * @param portNumber the port number of the ip address
 */
(private val context: Context, private val parameterValue: String, // declare variables needed
 private val ipAddress: String, private val portNumber: String, private val parameter: String) : AsyncTask<Void, Void, Void>() {

    private var requestReply: String? = null
    private val alertDialog: AlertDialog

    init {

        alertDialog = AlertDialog.Builder(this.context)
                .setTitle("HTTP Response From IP Address:")
                .setCancelable(true)
                .create()
    }

    /**
     * Name: doInBackground
     * Description: Sends the request to the ip address
     * @param voids
     * @return
     */
    override fun doInBackground(vararg voids: Void): Void? {
        alertDialog.setMessage("Data sent, waiting for reply from server...")
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
        requestReply = sendRequest(parameterValue, ipAddress, portNumber, parameter)
        return null
    }

    /**
     * Name: onPostExecute
     * Description: This function is executed after the HTTP request returns from the ip address.
     * The function sets the dialog's message with the reply text from the server and display the dialog
     * if it's not displayed already (in case it was closed by accident);
     * @param aVoid void parameter
     */
    override fun onPostExecute(aVoid: Void) {
        alertDialog.setMessage(requestReply)
        if (!alertDialog.isShowing) {
            alertDialog.show() // show dialog
        }
    }

    /**
     * Name: onPreExecute
     * Description: This function is executed before the HTTP request is sent to ip address.
     * The function will set the dialog's message and display the dialog.
     */
    override fun onPreExecute() {
        alertDialog.setMessage("Sending data to server, please wait...")
        if (!alertDialog.isShowing) {
            alertDialog.show()
        }
    }

}