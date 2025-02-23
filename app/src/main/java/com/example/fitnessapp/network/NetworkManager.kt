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
import com.example.fitnessapp.utils.Utils
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

/** NetworkManager Object to implement the logic for sending a request and
 * executing the common logic for all requests
 */
object NetworkManager {
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
        val progressDialog = showRequestInProgressDialog()

        call.enqueue(object : Callback<CustomResponse> {
            override fun onResponse(call: Call<CustomResponse>, response: Response<CustomResponse>) {
                var responseContent: CustomResponse? = null

                try {
                    if (response.isSuccessful) {
                        responseContent = response.body()

                        if (responseContent == null) {
                            // Response code is 2xx, but the body contains no data,
                            // this is unexpected error
                            Utils.showMessageWithVibration(R.string.error_msg_unexpected)
                            onErrorCallback(getEmptyResponse())
                            return
                        }

                        onSuccessCallback(responseContent)

                        if (responseContent.message != "Success") {
                            Utils.showMessage(responseContent.message)
                        }

                    } else {
                        // Extract the error body which must contain CustomResponse and set the
                        // responseBody which will be processed in onError(onErrorCallback)
                        val errorBody = JSONObject(response.errorBody()!!.string())

                        responseContent = if (errorBody.optJSONObject("value") != null) {
                            Gson().fromJson(errorBody.getJSONObject("value").toString(), CustomResponse::class.java)
                        } else {
                            Gson().fromJson(errorBody.toString(), CustomResponse::class.java)
                        }

                        if (responseContent == null || responseContent.code == 0) {
                            // Something went wrong, show unexpected error and try to execute
                            // on error callback
                            Utils.showMessageWithVibration(R.string.error_msg_unexpected)
                            onErrorCallback(getEmptyResponse())
                            return
                        }

                        // Execute on error callback
                        if (responseContent.code == HttpURLConnection.HTTP_UNAUTHORIZED) {

                            if (responseContent.data.size == 1) {
                                // Update the token using the returned token
                                APIService.updateToken(responseContent.data[0])

                                // Remove the progress dialog from the "first" request before
                                // resending it
                                progressDialog.dismiss()

                                // Resend the original request by recreating the Call<CustomResponse> object
                                executeCall(requestFactory(), onSuccessCallback, onErrorCallback)

                            } else {
                                if (AppStateManager.user != null) {
                                    // Token expired, logout
                                    UserRepository().logout(onSuccess = { Utils.onLogout() })
                                    Utils.showMessageWithVibration(responseContent.message)
                                } else {
                                    // If user is not logged in, execute the onError which will display
                                    // the login page
                                    onError(responseContent, onErrorCallback)
                                }
                            }
                        } else {
                            // Execute the on error callback if the request failed for some reason
                            // and show the error message
                            onError(responseContent, onErrorCallback)
                        }
                    }

                } catch (ex: Exception) {
                    onError(getEmptyResponse(), onErrorCallback)
                    ex.printStackTrace()
                }

                // Try to update the notification indication
                if (responseContent != null && AppStateManager.activeActivity !is LoginActivity) {
                    if (responseContent.notification != AppStateManager.notification) {
                        AppStateManager.notification = responseContent.notification
                    }
                }

                // Remove the progress dialog
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<CustomResponse>, t: Throwable) {
                // Display network problem error
                Utils.showMessageWithVibration(R.string.error_msg_unexpected_network_problem)
                (t as Exception).printStackTrace()
                onErrorCallback(getEmptyResponse())

                // Remove the progress dialog
                progressDialog.dismiss()
            }
        })
    }

    /** Show request in progress dialog when the request is sent */
    private fun showRequestInProgressDialog(): AlertDialog {
        val dialogBuilder = AlertDialog.Builder(Utils.getActivity(), R.style.Theme_FitnessApp_Dialog)
                            .setView(R.layout.dialog_req_progress)
                            .setCancelable(false)

        val progressDialog = dialogBuilder.create()
        progressDialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (progressDialog.isShowing) {
                progressDialog.dismiss()
            }
        }, 10000)

        return progressDialog
    }

    /** Executes the logic when request error occurs
     * @param responseContent the response content
     * @param onErrorCallback the callback to execute
     */
    private fun onError(responseContent: CustomResponse?, onErrorCallback: (CustomResponse) -> Unit) {
        var message = Utils.getActivity().getString(R.string.error_msg_unexpected)

        if (responseContent != null) {
            // Execute the callback
            onErrorCallback(responseContent)

            if (responseContent.message.isNotEmpty()) {
                message = responseContent.message
            }
        } else {
            onErrorCallback(getEmptyResponse())
        }

        Utils.showMessageWithVibration(message)
    }

    /** Create CustomResponse object with fail code when CustomResponse is not available */
    private fun getEmptyResponse(): CustomResponse {
        return CustomResponse(HttpURLConnection.HTTP_BAD_REQUEST, "", listOf(), AppStateManager.notification)
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