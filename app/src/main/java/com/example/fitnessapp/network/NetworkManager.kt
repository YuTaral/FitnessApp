package com.example.fitnessapp.network

import android.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** NetworkManager Object to hold the logic for sending a request and
 * executing the common logic for all requests
 */
object NetworkManager {
    private lateinit var progressDialog: AlertDialog

    /** Send a request
     * @param request the request to send
     * @param onSuccessCallback the callback to execute on success
     * @param onErrorCallback the callback to execute if the response code is not success
     */
    fun sendRequest(request: Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit, onErrorCallback: (CustomResponse) -> Unit) {
        // Show progress dialog
        showRequestInProgressDialog()

        request.enqueue(object : Callback<CustomResponse> {
            override fun onResponse(call: Call<CustomResponse>, response: Response<CustomResponse>) {
                try {
                    progressDialog.hide()
                    val body = response.body()!!

                    // Process the response
                    if (response.isSuccessful && Utils.isSuccessRespCode(body.responseCode)) {
                        onSuccessCallback(body)
                    } else {
                        if (body.userMessage.isNotEmpty()) {
                            Utils.showMessage(body.userMessage)
                        } else {
                            Utils.showMessage(R.string.error_msg_unexpected)
                        }
                        onErrorCallback(body)
                    }
                } catch (e: Exception) {
                    Utils.showMessage(response.raw().toString())
                }
            }

            override fun onFailure(call: Call<CustomResponse>, t: Throwable) {
                progressDialog.hide()
                Utils.onNetworkFailure(t)
            }
        })
    }

    /** Overloads send request method
     * @param request the request to send
     * @param onSuccessCallback the callback to execute on success
     */
    fun sendRequest(request: Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit) {
        sendRequest(request, onSuccessCallback, onErrorCallback = {})
    }

    /** Shows request in progress dialog when the request is sent */
    private fun showRequestInProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
                            .setView(R.layout.progress_dialog)
                            .setCancelable(false)

        progressDialog = dialogBuilder.create()
        progressDialog.show()
    }
}