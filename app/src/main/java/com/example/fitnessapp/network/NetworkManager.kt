package com.example.fitnessapp.network

import android.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.network.repositories.UserRepository
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
                    progressDialog.dismiss()
                    val body = response.body()!!

                    // Process the response
                    if (response.isSuccessful) {

                        try {

                            if (Utils.isSuccessResponse(body.code)) {
                                // Execute the callback and show the message if it's different from success
                                onSuccessCallback(body)
                                if (body.message != "Success") {
                                    Utils.showToast(body.message)
                                }

                            } else if (Utils.istTokenExpiredResponse(body.code)) {
                                // Token expired, logout
                                UserRepository().logout(onSuccess = { Utils.onLogout() })

                            } else if (Utils.isTokenRefreshResponse(body.code) && body.data.isNotEmpty()) {
                                // Update the token using the returned token
                                APIService.updateToken(body.data[0])

                                // Execute the callback
                                onSuccessCallback(body)

                            } else if (Utils.isFail(body.code)) {
                                // Execute on error callback
                                onError(body, onErrorCallback)
                            }

                        } catch (ex: Exception) {
                            // Show unexpected error message in case something goes wrong
                            Utils.showMessage(R.string.error_msg_unexpected)
                        }
                    } else {
                        try {
                            // Execute on error callback
                            onError(body, onErrorCallback)
                        } catch (ex: Exception) {
                            // Show unexpected error message in case something goes wrong
                            Utils.showMessage(R.string.error_msg_unexpected)
                        }
                    }
                } catch (e: Exception) {
                    progressDialog.dismiss()
                    Utils.showMessage(response.message())
                }
            }

            override fun onFailure(call: Call<CustomResponse>, t: Throwable) {
                progressDialog.dismiss()
                Utils.showMessage(R.string.error_msg_unexpected_network_problem)
            }
        })
    }

    /** Overload send request method
     * @param request the request to send
     * @param onSuccessCallback the callback to execute on success
     */
    fun sendRequest(request: Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit) {
        sendRequest(request, onSuccessCallback, onErrorCallback = {})
    }

    /** Show request in progress dialog when the request is sent */
    private fun showRequestInProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
                            .setView(R.layout.progress_dialog)
                            .setCancelable(false)

        progressDialog = dialogBuilder.create()
        progressDialog.show()
    }

    /** Executes the logic when request error occurs
     * @param body the request's response body
     * @param onErrorCallback the callback to execute
     */
    private fun onError(body: CustomResponse, onErrorCallback: (CustomResponse) -> Unit) {
        // Show error message
        if (body.message.isNotEmpty()) {
            Utils.showMessage(body.message)
        } else {
            Utils.showMessage(R.string.error_msg_unexpected)
        }

        // Execute the callback
        onErrorCallback(body)
    }
}