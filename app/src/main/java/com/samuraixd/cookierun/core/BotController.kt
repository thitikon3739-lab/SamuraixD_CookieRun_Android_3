package com.samuraixd.cookierun.core

import android.content.Context
import com.samuraixd.cookierun.service.RunAccessibilityService
import com.samuraixd.cookierun.service.ScreenCaptureService
import kotlinx.coroutines.*
import java.io.File

class BotController(private val ctx:Context, private val onState:(String)->Unit){
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Default); private val profile=Profile(ctx); private val store=RouteStore(ctx)
    @Volatile var mode=BotMode.IDLE; @Volatile var loop=true; @Volatile private var stop=false
    private fun svc()=RunAccessibilityService.instance
    fun stop(){stop=true;mode=BotMode.IDLE;onState("หยุด")}
    private suspend fun tap(p:P){ val b=ScreenCaptureService.latest?:return; val q=profile.scale(p,b.width,b.height); withContext(Dispatchers.Main){svc()?.tap(q.x,q.y)}; delay(700) }
    fun autoPlay(file:File){ stop=false; mode=BotMode.PLAYING; scope.launch { val route=store.load(file); while(!stop){ onState("เริ่มรอบใหม่"); tap(profile.start); delay(1200); tap(profile.fastStartItem); tap(profile.purchase); tap(profile.play); delay(1600); tap(profile.fastStartUse)
            val b=ScreenCaptureService.latest; if(b!=null){ val done=CompletableDeferred<Unit>(); withContext(Dispatchers.Main){svc()?.playRoute(route,b.width,b.height,profile){done.complete(Unit)}}; done.await() }
            delay(1200); tap(profile.relayUse); delay(600); // route continues as one full two-cookie recording in normal use
            delay(1600); tap(profile.finish); delay(2500); solveCardsIfPresent(); if(!loop) break }
        mode=BotMode.IDLE; onState("หยุด") }
    }
    private suspend fun solveCardsIfPresent(){ val b=ScreenCaptureService.latest?:return; val boxes=profile.cards.map{profile.scale(it,b.width,b.height)}; val r=CardMatcher.solve(b,boxes)?:return; if(r.confidence<0.05)return; mode=BotMode.MATCHING; onState("จับคู่ ${r.i+1}+${r.j+1}"); for(idx in listOf(r.i,r.j)){ val x=boxes[idx].x+boxes[idx].w/2; val y=boxes[idx].y+boxes[idx].h/2; withContext(Dispatchers.Main){svc()?.tap(x,y)}; delay(350) }; mode=BotMode.PLAYING }
    fun routes()=store.list()
}
