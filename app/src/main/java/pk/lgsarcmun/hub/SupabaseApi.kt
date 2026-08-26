package pk.lgsarcmun.hub

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class SupabaseApi {
    private val base = BuildConfig.SUPABASE_URL
    private val key = BuildConfig.SUPABASE_KEY

    private suspend fun post(path:String, body:String, auth:String?=null): JSONObject = withContext(Dispatchers.IO) {
        val c = URL("$base$path").openConnection() as HttpURLConnection
        c.requestMethod="POST"; c.doOutput=true; c.setRequestProperty("apikey",key); c.setRequestProperty("Content-Type","application/json"); c.setRequestProperty("Accept","application/json")
        if(auth!=null)c.setRequestProperty("Authorization","Bearer $auth")
        c.outputStream.use{it.write(body.toByteArray())}
        val text=(if(c.responseCode in 200..299)c.inputStream else c.errorStream).bufferedReader().readText()
        if(c.responseCode !in 200..299) throw IllegalStateException(text)
        JSONObject(text.ifBlank{"{}"})
    }
    suspend fun signIn(email:String,password:String): String = post("/auth/v1/token?grant_type=password",JSONObject().put("email",email).put("password",password).toString()).getString("access_token")
    suspend fun signUp(email:String,password:String,fullName:String): String = post("/auth/v1/signup",JSONObject().put("email",email).put("password",password).put("data",JSONObject().put("full_name",fullName)).toString()).optString("access_token","")
    suspend fun verify(memberId:String): JSONObject = post("/functions/v1/verify-membership",JSONObject().put("member_id",memberId).toString())
}
