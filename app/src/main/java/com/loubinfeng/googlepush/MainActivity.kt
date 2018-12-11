package com.loubinfeng.googlepush

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client: OkHttpClient by lazy { OkHttpClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    private fun setup() {
        et_server_url.text?.insert(0, "https://fcm.googleapis.com/fcm/send")
        et_server_url.setOnLongClickListener {
            IntentIntegrator(this@MainActivity).setRequestCode(1).initiateScan()
            true
        }
        et_app_key.setOnLongClickListener {
            IntentIntegrator(this@MainActivity).setRequestCode(2).initiateScan()
            true
        }
        et_request_content.setOnLongClickListener {
            IntentIntegrator(this@MainActivity).setRequestCode(3).initiateScan()
            true
        }
        et_register_id.setOnLongClickListener {
            IntentIntegrator(this@MainActivity).setRequestCode(4).initiateScan()
            true
        }
        btn_send.setOnClickListener {
            makeRequest()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result = IntentIntegrator.parseActivityResult(REQUEST_CODE, resultCode, data)
        result?.contents?.let {
            var text = when (requestCode) {
                1 -> et_server_url.text
                2 -> et_app_key.text
                3 -> et_request_content.text
                4 -> et_register_id.text
                else -> null
            }
            text?.clear()
            text?.insert(0, it)
        }
    }

    private fun makeRequest() {
        et_server_url.text?.let { url ->
            et_app_key.text?.let { key ->
                et_register_id.text?.let { id ->
                    et_request_content.text?.let { content ->
                        client.newCall(
                            Request.Builder().url(url.toString())
                                .addHeader("Authorization", "key=$key")
                                .addHeader("Content-Type", "application/json")
                                .post(
                                    RequestBody.create(
                                        MediaType.parse("application/json; charset=utf-8"),
                                        JSONObject().put("to", id.toString()).put("data", content.toString()).toString()
                                    )
                                )
                                .build()
                        ).enqueue(object : Callback {
                            override fun onFailure(call: Call?, e: IOException?) {
                                tv_response.text = e?.message
                            }

                            override fun onResponse(call: Call?, response: Response?) {
                                tv_response.text = response?.let { it.code().toString() + it.body().toString() }
                            }
                        })
                    }
                }
            }
        }
    }

}
