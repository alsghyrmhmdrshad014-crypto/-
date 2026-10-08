package com.example.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.model.ConnectionInfo
import com.example.model.ConnectionStatus
import com.example.model.LaptopProfile
import com.example.model.OperatingSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class RemoteClient {

    private val fastHttpClient = OkHttpClient.Builder()
        .connectTimeout(800, TimeUnit.MILLISECONDS)
        .readTimeout(800, TimeUnit.MILLISECONDS)
        .writeTimeout(800, TimeUnit.MILLISECONDS)
        .build()

    private val standardHttpClient = OkHttpClient.Builder()
        .connectTimeout(2500, TimeUnit.MILLISECONDS)
        .readTimeout(2500, TimeUnit.MILLISECONDS)
        .writeTimeout(2500, TimeUnit.MILLISECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun ping(laptop: LaptopProfile): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        val url = "http://${laptop.ipAddress}:${laptop.port}/ping"
        try {
            val request = Request.Builder().url(url).get().build()
            val response = standardHttpClient.newCall(request).execute()
            val latency = System.currentTimeMillis() - start
            val success = response.isSuccessful
            response.close()
            Pair(success, latency)
        } catch (e: Exception) {
            Pair(false, -1L)
        }
    }

    suspend fun sendMouseMove(laptop: LaptopProfile, dx: Float, dy: Float): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/mouse"
        val json = JSONObject().apply {
            put("dx", dx)
            put("dy", dy)
        }
        postJson(fastHttpClient, url, json.toString())
    }

    suspend fun sendClick(laptop: LaptopProfile, button: String): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/click"
        val json = JSONObject().apply {
            put("button", button)
        }
        postJson(fastHttpClient, url, json.toString())
    }

    suspend fun sendScroll(laptop: LaptopProfile, dy: Int): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/scroll"
        val json = JSONObject().apply {
            put("dy", dy)
        }
        postJson(fastHttpClient, url, json.toString())
    }

    suspend fun sendKey(laptop: LaptopProfile, key: String): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/key"
        val json = JSONObject().apply {
            put("key", key)
        }
        postJson(standardHttpClient, url, json.toString())
    }

    suspend fun sendText(laptop: LaptopProfile, text: String): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/type"
        val json = JSONObject().apply {
            put("text", text)
        }
        postJson(standardHttpClient, url, json.toString())
    }

    suspend fun sendShortcut(laptop: LaptopProfile, keys: List<String>): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/shortcut"
        val json = JSONObject().apply {
            put("keys", org.json.JSONArray(keys))
        }
        postJson(standardHttpClient, url, json.toString())
    }

    suspend fun sendMedia(laptop: LaptopProfile, action: String): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/media"
        val json = JSONObject().apply {
            put("action", action)
        }
        postJson(standardHttpClient, url, json.toString())
    }

    suspend fun sendSystem(laptop: LaptopProfile, action: String): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/system"
        val json = JSONObject().apply {
            put("action", action)
        }
        postJson(standardHttpClient, url, json.toString())
    }

    suspend fun sendAppCommand(laptop: LaptopProfile, action: String, target: String): Boolean = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/app"
        val json = JSONObject().apply {
            put("action", action)
            put("target", target)
        }
        postJson(standardHttpClient, url, json.toString())
    }

    suspend fun fetchScreenSnapshot(laptop: LaptopProfile): Bitmap? = withContext(Dispatchers.IO) {
        val url = "http://${laptop.ipAddress}:${laptop.port}/screen"
        try {
            val request = Request.Builder().url(url).get().build()
            val response = standardHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.byteStream()?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun postJson(client: OkHttpClient, url: String, jsonString: String): Boolean {
        return try {
            val body = jsonString.toRequestBody(jsonMediaType)
            val request = Request.Builder().url(url).post(body).build()
            val response = client.newCall(request).execute()
            val success = response.isSuccessful
            response.close()
            success
        } catch (e: IOException) {
            false
        }
    }
}
