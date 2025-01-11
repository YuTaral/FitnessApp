package com.example.fitnessapp.network

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import com.example.fitnessapp.LoginActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** NetworkManager Object to implement the logic for sending a request and
 * executing the common logic for all requests
 */
object NetworkManager {
    private lateinit var progressDialog: AlertDialog

    @Volatile
    private var responseBody: CustomResponse? = null

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
    fun sendRequest(request: () -> Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit) {
        sendRequest(request, onSuccessCallback, onErrorCallback = {})
    }

    /** Overload send request method
     * @param request lambda to create Call object with the request
     * @param onSuccessCallback the callback to execute on success
     */
    fun sendRequest(request: () -> Call<CustomResponse>, onSuccessCallback: (CustomResponse) -> Unit, onErrorCallback: (CustomResponse) -> Unit) {
        if (!isNetworkAvailable()) {
            Utils.showMessageWithVibration(R.string.error_msg_no_internet)
            onErrorCallback(getEmptyResponse())
            return
        }

        // Update the request factory property
        requestFactory = request

        // Execute the call, passing new Call<CustomResponse> object
        executeCall(requestFactory(), onSuccessCallback, onErrorCallback)
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
                    responseBody = response.body()!!

                    // Process the response
                    if (responseBody != null && response.isSuccessful) {

                        updateNotification()

                        try {

                            if (Utils.isSuccessResponse(responseBody!!.code)) {
                                // Execute the callback and show the message if it's different from success
                                onSuccessCallback(responseBody!!)
                                if (responseBody!!.message != "Success") {
                                    Utils.showMessage(responseBody!!.message)
                                }

                            } else if (Utils.istTokenExpiredResponse(responseBody!!.code)) {
                                if (AppStateManager.user != null) {
                                    // Token expired, logout
                                    UserRepository().logout(onSuccess = { Utils.onLogout() })
                                } else {
                                    // If user is not logged in, execute the onError which will display
                                    // the login page
                                    onError(onErrorCallback)
                                }

                            } else if (Utils.isTokenRefreshResponse(responseBody!!.code) && responseBody!!.data.isNotEmpty()) {
                                // Update the token using the returned token
                                APIService.updateToken(responseBody!!.data[0])

                                // Resend the original request by recreating the Call<CustomResponse> object
                                executeCall(requestFactory(), onSuccessCallback, onErrorCallback)

                            } else if (Utils.isFail(responseBody!!.code)) {
                                // Execute on error callback
                                onError(onErrorCallback)
                            }

                        } catch (ex: Exception) {
                            onException(Utils.getActivity().getString(R.string.error_msg_unexpected), ex)
                        }
                    } else {
                        try {
                            onError(onErrorCallback)
                        } catch (ex: Exception) {
                            onException(Utils.getActivity().getString(R.string.error_msg_unexpected), ex)
                        }
                    }

                    // Make sure to remove the progress dialog
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }

                } catch (ex: Exception) {
                    onException(response.message(), ex)
                }
            }

            override fun onFailure(call: Call<CustomResponse>, t: Throwable) {
                // Display network problem error
                onException(Utils.getActivity().getString(R.string.error_msg_unexpected_network_problem), t as Exception)

                try {
                    if (responseBody == null) {
                        responseBody = getEmptyResponse()
                    }

                    // Try to execute the on error callback
                    onErrorCallback(responseBody!!)

                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        })
    }

    /** Update the notification property received from the request
     * if it's different from the the value in AppStateManager
     */
    private fun updateNotification() {
        if (responseBody == null || AppStateManager.activeActivity is LoginActivity) {
            return
        }

        if (responseBody!!.notification != AppStateManager.notification) {
            AppStateManager.notification = responseBody!!.notification
        }
    }

    /** Show request in progress dialog when the request is sent */
    private fun showRequestInProgressDialog() {
        val dialogBuilder = AlertDialog.Builder(Utils.getActivity(), R.style.LoadingDialog)
                            .setView(R.layout.dialog_req_progress)
                            .setCancelable(false)

        progressDialog = dialogBuilder.create()
        progressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
        }, 10000)
    }

    /** Executes the logic when request error occurs
     * @param onErrorCallback the callback to execute
     */
    private fun onError(onErrorCallback: (CustomResponse) -> Unit) {
        var message = Utils.getActivity().getString(R.string.error_msg_unexpected)

        updateNotification()

        if (responseBody != null) {
            // Execute the callback
            onErrorCallback(responseBody!!)

            if (responseBody!!.message.isNotEmpty()) {
                message = responseBody!!.message
            }
        }

        Utils.showMessageWithVibration(message)
    }

    /** Execute the logic if exception occurs
     * @param message the message to show
     * @param exception the exception
     */
    private fun onException(message: String, exception: Exception) {
        // Dismiss the dialog
        progressDialog.dismiss()

        updateNotification()

        // Show the message and log the error
        Utils.showMessageWithVibration(message)
        exception.printStackTrace()
    }

    /** Create CustomResponse object with fail code when CustomResponse is not available */
    private fun getEmptyResponse(): CustomResponse {
        return CustomResponse(Constants.ResponseCode.FAIL.ordinal, "", listOf(), AppStateManager.notification)
    }

    /**
     * Utility function to check if the network is available
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = Utils.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}