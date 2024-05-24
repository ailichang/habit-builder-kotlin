package com.habitbuilder.util

import android.content.Context
import androidx.annotation.RawRes
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class JsonFileReader {
    companion object{
        fun loadJsonArray(context: Context, @RawRes resourceId:Int, jsonArrayName:String): JSONArray?{
            val stringBuilder = StringBuilder()
            val inputStream = context.resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            try {
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                reader.close()
                val jsonObject = JSONObject(stringBuilder.toString())
                return jsonObject.getJSONArray(jsonArrayName)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}