package net.ccbluex.liquidbounce.utils.misc

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * LiquidBounce Hacked Client
 * A minecraft forge injection client using Mixin
 *
 * @game Minecraft
 * @author CCBlueX
 */
object HttpUtils {

    private const val DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0"

    init {
        HttpURLConnection.setFollowRedirects(true)
    }

    private fun make(
        url: String, method: String,
        agent: String = DEFAULT_AGENT
    ): HttpURLConnection {
        val httpConnection = URL(url).openConnection() as HttpURLConnection

        httpConnection.requestMethod = method
        httpConnection.connectTimeout = 2000
        httpConnection.readTimeout = 10000

        httpConnection.setRequestProperty("User-Agent", agent)

        httpConnection.instanceFollowRedirects = true
        httpConnection.doOutput = true

        return httpConnection
    }

    @Throws(IOException::class)
    fun request(
        url: String, method: String,
        agent: String = DEFAULT_AGENT
    ): String {
        val connection = make(url, method, agent)

        return connection.inputStream.reader().readText()
    }

    @JvmStatic
    fun post(url: String, body: RequestBody): String {
        val builder: Request.Builder = Request.Builder()
        val request: Request =
            builder.post(body).url(url).build()
        val client = OkHttpClient.Builder().build()
        val e = client.newCall(request).execute()
        return e.body!!.string()
    }

    @JvmStatic
    fun put(url: String, body: RequestBody): String {
        val builder: Request.Builder = Request.Builder()
        val request: Request =
            builder.put(body).url(url).build()
        val client = OkHttpClient.Builder().build()
        val e = client.newCall(request).execute()
        return e.body!!.string()
    }

    @Throws(IOException::class)
    fun requestStream(
        url: String, method: String,
        agent: String = DEFAULT_AGENT
    ): InputStream? {
        val connection = make(url, method, agent)

        return connection.inputStream
    }

    @JvmStatic
    fun download(url: String): String {
        val builder: Request.Builder = Request.Builder()
        val request: Request =
            builder.get().url(url).build()
        val client = OkHttpClient.Builder().build()
        val e = client.newCall(request).execute()
        return e.body!!.string()
    }

    @Throws(IOException::class)
    @JvmStatic
    fun get(url: String) = request(url, "GET")
}