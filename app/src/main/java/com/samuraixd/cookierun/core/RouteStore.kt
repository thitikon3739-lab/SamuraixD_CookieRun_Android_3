package com.samuraixd.cookierun.core

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class RouteStore(private val context: Context) {
    private val dir = File(context.filesDir, "routes").apply { mkdirs() }
    fun list(): List<File> = dir.listFiles()?.filter { it.extension=="json" }?.sortedByDescending { it.lastModified() } ?: emptyList()
    fun save(route: RunRoute): File {
        val a=JSONArray(); route.actions.forEach { a.put(JSONObject().put("type",it.type).put("tMs",it.tMs).put("durationMs",it.durationMs)) }
        val o=JSONObject().put("schema",1).put("name",route.name).put("createdAt",route.createdAt).put("durationMs",route.durationMs).put("actions",a)
        return File(dir, "${route.name}_${route.createdAt}.json").also { it.writeText(o.toString(2)) }
    }
    fun load(file: File): RunRoute {
        val o=JSONObject(file.readText()); val a=o.getJSONArray("actions"); val out=mutableListOf<RunAction>()
        for(i in 0 until a.length()){ val x=a.getJSONObject(i); out += RunAction(x.getString("type"),x.getLong("tMs"),x.optLong("durationMs")) }
        return RunRoute(o.getString("name"),o.getLong("createdAt"),o.getLong("durationMs"),out)
    }
}
