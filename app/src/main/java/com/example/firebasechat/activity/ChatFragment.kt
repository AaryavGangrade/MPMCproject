package com.example.firebasechat.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentChatBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ChatFragment : Fragment() {

    private val client = OkHttpClient()
    private lateinit var binding: FragmentChatBinding

    private val slideInAnimation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_top)
    }

    private val apiEndpoint = "https://nyay-api-sih.onrender.com/chat" // Replace with your actual API endpoint

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentChatBinding.inflate(inflater, container, false)

        // Set an OnClickListener for btnSend
        binding.btnSend.setOnClickListener {
            // Hide the "Send" button after clicking it
            binding.btnSend.visibility = View.GONE

            // Slide in the CardViews
            binding.questionCardView.visibility = View.VISIBLE
            binding.responseCardView.visibility = View.VISIBLE

            // Apply slide-in animation to CardViews
            binding.questionCardView.startAnimation(slideInAnimation)
            binding.responseCardView.startAnimation(slideInAnimation)

            // Setting response TextView to "Please wait.."
            binding.txtResponse.text = "Please wait.."

            // Validating text
            val question = binding.etQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                postFormData(question) { response ->
                    requireActivity().runOnUiThread {
                        binding.txtResponse.text = response
                    }
                }
            }
        }

        // Return the root view from the binding.
        return binding.root
    }

    private fun postFormData(userInput: String, callback: (String) -> Unit) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("user_input", userInput)
            .build()

        val request = Request.Builder()
            .url(apiEndpoint)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback("Failed to get a response")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    try {
                        val jsonObject = JSONObject(body)

                        if (jsonObject.has("result")) {
                            val result = jsonObject.getString("result")
                            callback(result)
                        } else {
                            callback("No 'result' field found in the response")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        callback("Invalid JSON format in response")
                    }
                } else {
                    callback("Empty response")
                }
            }
        })
    }
}
