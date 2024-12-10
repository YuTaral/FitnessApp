package com.example.fitnessapp.network

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import com.example.fitnessapp.R
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** NetworkManager Object to hold the logic for sending a request and
 * executing the common logic for all requests
 */
object NetworkManager {
    private lateinit var progressDialog: AlertDialog

    /** Use Factory pattern to create the call object. This is needed, because when
     * we need to refresh the token, the new token is returned as response from the server.
     * We must update it and execute the original request. We need a new instance of the
     * call object, when adding the token in AuthorizationInterceptor, the old token is used,
     * as OkHttpClient is immutable and does not take into account the change
     */
    private lateinit var requestFactory: () -> Call<CustomResponse>

    /** Overload send request method
     * @param request lambda to create Call object with the request
     * @param onSuccessCallback the callback to execute on success
     */
    fun sendRequest(request: () -> Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit, onErrorCallback: (CustomResponse) -> Unit) {
        // Update the request factory property
        requestFactory = request

        // Execute the call, passing new Call<CustomResponse> object
        executeCall(requestFactory(), onSuccessCallback, onErrorCallback)
    }

    /** Overload send request method
     * @param request lambda to create Call object with the request
     * @param onSuccessCallback the callback to execute on success
     */
    fun sendRequest(request: () -> Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit) {
        sendRequest(request, onSuccessCallback, onErrorCallback = {})
    }

    /** Execute to call (request)
     * @param call the request to send
     * @param onSuccessCallback the callback to execute on success
     * @param onErrorCallback the callback to execute if the response code is not success
     */
    private  fun executeCall(call: Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit, onErrorCallback: (CustomResponse) -> Unit) {
        // Show progress dialog
        showRequestInProgressDialog()

        call.enqueue(object : Callback<CustomResponse> {
            override fun onResponse(call: Call<CustomResponse>, response: Response<CustomResponse>) {
                try {
                    progressDialog.dismiss()
                    val body = response.body()

                    // Process the response
                    if (body != null && response.isSuccessful) {

                        try {

                            if (Utils.isSuccessResponse(body.code)) {
                                // Execute the callback and show the message if it's different from success
                                onSuccessCallback(body)
                                if (body.message != "Success") {
                                    Utils.showToast(body.message)
                                }

                            } else if (Utils.istTokenExpiredResponse(body.code)) {
                                if (StateEngine.user != null) {
                                    // Token expired, logout
                                    UserRepository().logout(onSuccess = { Utils.onLogout() })
                                } else {
                                    // If user is not logged in, execute the onError which will display
                                    // the login page
                                    onError(body, onErrorCallback)
                                }

                            } else if (Utils.isTokenRefreshResponse(body.code) && body.data.isNotEmpty()) {
                                // Update the token using the returned token
                                APIService.updateToken(body.data[0])

                                // Resend the original request by recreating the Call<CustomResponse> object
                                executeCall(requestFactory(), onSuccessCallback, onErrorCallback)

                            } else if (Utils.isFail(body.code)) {
                                // Execute on error callback
                                onError(body, onErrorCallback)
                            }

                        } catch (ex: Exception) {
                            onException(Utils.getContext().getString(R.string.error_msg_unexpected), ex)
                        }
                    } else {
                        try {
                            onError(body, onErrorCallback)
                        } catch (ex: Exception) {
                            onException(Utils.getContext().getString(R.string.error_msg_unexpected), ex)
                        }
                    }

                } catch (ex: Exception) {
                    onException(response.message(), ex)
                }
            }

            override fun onFailure(call: Call<CustomResponse>, t: Throwable) {
                onException(Utils.getContext().getString(R.string.error_msg_unexpected_network_problem), t as Exception)
            }
        })
    }

    /** Show request in progress dialog when the request is sent */
    private fun showRequestInProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
                            .setView(R.layout.progress_dialog)
                            .setCancelable(false)

        progressDialog = dialogBuilder.create()
        progressDialog.show()

        // Create a handler to dismiss the progressDialog after 30 seconds in case something goes wrong
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if the dialog is still showing, and dismiss it if so
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
                Utils.showMessage(R.string.error_msg_unexpected_network_problem)
            }
        }, 30000) // 30000 milliseconds = 30 seconds
    }

    /** Executes the logic when request error occurs
     * @param body the request's response body
     * @param onErrorCallback the callback to execute
     */
    private fun onError(body: CustomResponse?, onErrorCallback: (CustomResponse) -> Unit) {
        var message = Utils.getContext().getString(R.string.error_msg_unexpected)

        if (body != null) {
            // Execute the callback
            onErrorCallback(body)

            if (body.message.isNotEmpty()) {
                message = body.message
            }
        }

        Utils.showMessage(message)
    }

    /** Execute the logic if exception occurs
     * @param message the message to show
     * @param exception the exception
     */
    private fun onException(message: String, exception: Exception) {
        // Dismiss the dialog
        progressDialog.dismiss()

        // Show the message and log the error
        Utils.showMessage(message)
        Utils.logException(exception)
    }
}