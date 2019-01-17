package com.bignerdranch.android.livingroomlightscontrol

import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.URISyntaxException

/**
 * Description: Send an HTTP Get request to a specified ip address and port.
 * Also send a parameter "parameterName" with the value of "parameterValue".
 * @param parameterValue the pin number to toggle
 * @param ipAddress the ip address to send the request to
 * @param portNumber the port number of the ip address
 * @param parameterName
 * @return The ip address' reply text, or an ERROR message is it fails to receive one
 */
fun sendRequest(parameterValue: String, ipAddress: String, portNumber: String, parameterName: String): String {
    var serverResponse = "ERROR"

    try {

        val httpclient = DefaultHttpClient() // create an HTTP client
        // define the URL e.g. http://myIpaddress:myport/?pin=13 (to toggle pin 13 for example)
        val website = URI("http://$ipAddress:$portNumber/?$parameterName=$parameterValue")
        val getRequest = HttpGet() // create an HTTP GET object
        getRequest.uri = website // set the URL of the GET request
        val response = httpclient.execute(getRequest) // execute the request
        // get the ip address server's reply
        var content: InputStream? = null
        content = response.entity.content
        val `in` = BufferedReader(InputStreamReader(
                content!!
        ))
        serverResponse = `in`.readLine()
        // Close the connection
        content.close()
    } catch (e: ClientProtocolException) {
        // HTTP error
        serverResponse = e.message
        e.printStackTrace()
    } catch (e: IOException) {
        // IO error
        serverResponse = e.message
        e.printStackTrace()
    } catch (e: URISyntaxException) {
        // URL syntax error
        serverResponse = e.message
        e.printStackTrace()
    }

    // return the server's reply/response text
    return serverResponse
}