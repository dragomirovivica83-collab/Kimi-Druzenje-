package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-3.5-flash"

    suspend fun generisiOdgovor(sistemskaUputstva: String, porukaKorisnika: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d("GeminiClient", "Gemini API key is placeholder or empty. Falling back to simulation.")
            return@withContext simulirajKimiAI(porukaKorisnika)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", porukaKorisnika) })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", sistemskaUputstva) })
                })
            })
        }

        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("GeminiClient", "API Error: ${response.code} ${response.message}")
                    return@withContext simulirajKimiAI(porukaKorisnika)
                }
                val bodyStr = response.body?.string() ?: ""
                val jsonResponse = JSONObject(bodyStr)
                val candidates = jsonResponse.getJSONArray("candidates")
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val parts = content.getJSONArray("parts")
                parts.getJSONObject(0).getString("text")
            }
        } catch (e: Exception) {
            Log.e("GeminiClient", "Failed to call Gemini API, falling back to simulated logic", e)
            simulirajKimiAI(porukaKorisnika)
        }
    }

    private fun simulirajKimiAI(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("zdravo") || lower.contains("ćao") || lower.contains("cao") -> {
                "Ćao! Ja sam Kimi AI 🌟 Tvoj pametni saputnik u Kimi Druženju. Kako provodiš današnji dan?"
            }
            lower.contains("ko si ti") || lower.contains("kako se zoveš") || lower.contains("kako se zoves") -> {
                "Ja sam Kimi AI, tvoj virtuelni prijatelj i asistent aplikacije Kimi Druženje! 🚀 Ovde sam da ćaskamo i pomognem ti u svemu."
            }
            lower.contains("pravil") || lower.contains("banuj") || lower.contains("kazn") -> {
                "Naša pravila su jasna i poštena: bez uvreda, govora mržnje ili uznemiravanja. Naš tim (Vlasnik, Admini, Moderatori, Helperi) aktivno nadgleda prekršioce i deli opomene, utišavanja (mute) i banove!"
            }
            lower.contains("kako upoznati") || lower.contains("kako da nađem") || lower.contains("dečko") || lower.contains("devojka") -> {
                "Predlažem da odeš na tab 'Upoznavanje' (Swipe) i lajkuješ profile koji ti se dopadaju! Ako i oni tebe lajkuju, dobićete podudaranje (Sparivanje) i možete odmah početi čet."
            }
            lower.contains("ulog") || lower.contains("vlasnik") || lower.contains("moderator") || lower.contains("helper") -> {
                "Evo uloga u aplikaciji:\n👑 Vlasnik (Owner) - Potpuna kontrola nad ulogama i aplikacijom.\n🛡️ Admin - Upravlja moderatorima, kaznama i korisnicima.\n⚔️ Moderator - Rešava žalbe, upozorava i utišava nemirne.\n🩺 Helper - Pomaže korisnicima i usmerava ih ka pravilima."
            }
            lower.contains("ti si lep") || lower.contains("super") || lower.contains("svidj") -> {
                "Hvala ti puno! Trudim se da budem najbolji asistent za Kimi Druženje. I ti si sjajna osoba! 😊"
            }
            else -> {
                "Kao tvoj asistent u Kimi Druženju, tu sam da te podržim! Četovanje i druženje u tamno plavom i modernom okruženju je fantastično, zar ne? 😊 Slobodno me pitaj bilo šta o profilima, prijateljstvu ili ulogama!"
            }
        }
    }
}
