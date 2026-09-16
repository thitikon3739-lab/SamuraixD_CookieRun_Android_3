package com.samuraixd.cookierun.core

import android.content.Context
import org.json.JSONObject

data class P(val x:Int,val y:Int)
data class Box(val x:Int,val y:Int,val w:Int,val h:Int)
class Profile(ctx:Context){
    private val o=JSONObject(ctx.assets.open("default_profile.json").bufferedReader().readText())
    val baseW=o.getInt("baseWidth"); val baseH=o.getInt("baseHeight")
    private val p=o.getJSONObject("points")
    private fun point(name:String):P{ val a=p.getJSONArray(name); return P(a.getInt(0),a.getInt(1)) }
    val start=point("start"); val fastStartItem=point("fastStartItem"); val purchase=point("purchase"); val play=point("play");
    val fastStartUse=point("fastStartUse"); val relayUse=point("relayUse"); val finish=point("finish")
    private val r=o.getJSONObject("route");
    fun routePoint(name:String):P { val a=r.getJSONArray(name); return P(a.getInt(0),a.getInt(1)) }
    val cards:List<Box> = (0 until o.getJSONArray("cardBoxes").length()).map { i -> val a=o.getJSONArray("cardBoxes").getJSONArray(i); Box(a.getInt(0),a.getInt(1),a.getInt(2),a.getInt(3)) }
    fun scale(p:P,w:Int,h:Int)=P((p.x*w.toFloat()/baseW).toInt(),(p.y*h.toFloat()/baseH).toInt())
    fun scale(b:Box,w:Int,h:Int)=Box((b.x*w.toFloat()/baseW).toInt(),(b.y*h.toFloat()/baseH).toInt(),(b.w*w.toFloat()/baseW).toInt(),(b.h*h.toFloat()/baseH).toInt())
}
