package com.samuraixd.cookierun.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.*
import com.samuraixd.cookierun.core.BotController

class FloatingControlService:Service(){
    private lateinit var wm:WindowManager; private var root:LinearLayout?=null; private lateinit var bot:BotController
    override fun onBind(i:Intent?):IBinder?=null
    override fun onCreate(){ super.onCreate(); wm=getSystemService(WINDOW_SERVICE) as WindowManager; bot=BotController(this){s->root?.post{(root?.getChildAt(1) as? TextView)?.text=s}}; show() }
    private fun show(){ val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,12,18,12);setBackgroundColor(0xDD111111.toInt())}; val head=Button(this).apply{text="S"}; val state=TextView(this).apply{text="หยุด";setTextColor(0xFFFFFFFF.toInt());textSize=14f}; val panel=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL};
        fun btn(t:String, f:()->Unit)=Button(this).apply{text=t;setOnClickListener{f()}}
        panel.addView(btn("▶ เล่นไฟล์ล่าสุดวนลูป"){val f=bot.routes().firstOrNull();if(f!=null)bot.autoPlay(f)})
        panel.addView(btn("■ หยุด"){bot.stop()}); panel.addView(btn("เปิดแอปหลัก"){startActivity(packageManager.getLaunchIntentForPackage(packageName)?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))})
        box.addView(head);box.addView(state);box.addView(panel);head.setOnClickListener{panel.visibility=if(panel.visibility==View.VISIBLE)View.GONE else View.VISIBLE}
        val lp=WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT).apply{gravity=Gravity.TOP or Gravity.START;x=20;y=180}
        head.setOnTouchListener(object:View.OnTouchListener{var sx=0f;var sy=0f;var ox=0;var oy=0;override fun onTouch(v:View,e:MotionEvent):Boolean{when(e.action){MotionEvent.ACTION_DOWN->{sx=e.rawX;sy=e.rawY;ox=lp.x;oy=lp.y} ; MotionEvent.ACTION_MOVE->{lp.x=ox+(e.rawX-sx).toInt();lp.y=oy+(e.rawY-sy).toInt();wm.updateViewLayout(box,lp)}};return false}})
        wm.addView(box,lp);root=box }
    override fun onDestroy(){bot.stop();root?.let{wm.removeView(it)};super.onDestroy()}
}
