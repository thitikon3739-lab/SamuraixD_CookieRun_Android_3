package com.samuraixd.cookierun.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.samuraixd.cookierun.core.*
import kotlinx.coroutines.*

class RunAccessibilityService: AccessibilityService(){
    companion object { @Volatile var instance:RunAccessibilityService?=null }
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Default)
    override fun onServiceConnected(){ super.onServiceConnected(); instance=this }
    override fun onAccessibilityEvent(event:AccessibilityEvent?){ }
    override fun onInterrupt(){ }
    override fun onDestroy(){ instance=null; scope.cancel(); super.onDestroy() }
    fun tap(x:Int,y:Int,duration:Long=45,onDone:(()->Unit)?=null){
        val p=Path().apply{moveTo(x.toFloat(),y.toFloat())}; val g=GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(p,0,duration)).build()
        dispatchGesture(g,object:GestureResultCallback(){override fun onCompleted(gestureDescription:GestureDescription?){onDone?.invoke()}},null)
    }
    fun hold(x:Int,y:Int,duration:Long,onDone:(()->Unit)?=null){ tap(x,y,duration,onDone) }
    fun playRoute(route:RunRoute,w:Int,h:Int,profile:Profile,onFinished:()->Unit){
        scope.launch { val start=android.os.SystemClock.elapsedRealtime(); for(a in route.actions){ val target=start+a.tMs; val wait=target-android.os.SystemClock.elapsedRealtime(); if(wait>0) delay(wait)
            val key=if(a.type=="jump")"jump" else "slide"; val p=profile.scale(profile.routePoint(key),w,h); if(a.type=="jump") tap(p.x,p.y) else hold(p.x,p.y,a.durationMs)
        }; val tail=(start+route.durationMs)-android.os.SystemClock.elapsedRealtime(); if(tail>0) delay(tail); withContext(Dispatchers.Main){onFinished()} }
    }
}
