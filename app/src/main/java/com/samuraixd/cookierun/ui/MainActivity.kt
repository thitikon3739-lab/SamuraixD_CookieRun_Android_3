package com.samuraixd.cookierun.ui

import android.app.*
import android.content.*
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.samuraixd.cookierun.core.*
import com.samuraixd.cookierun.service.*

class MainActivity:AppCompatActivity(){
    private lateinit var store:RouteStore
    private val capture=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){r->if(r.resultCode==Activity.RESULT_OK&&r.data!=null){val i=Intent(this,ScreenCaptureService::class.java).putExtra(ScreenCaptureService.EXTRA_CODE,r.resultCode).putExtra(ScreenCaptureService.EXTRA_DATA,r.data);startForegroundService(i);toast("เปิดตรวจหน้าจอแล้ว")}}
    override fun onCreate(b:Bundle?){super.onCreate(b);store=RouteStore(this); val l=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER;setPadding(40,40,40,40)}; val title=TextView(this).apply{text="SamuraixD CookieRun — Run Recorder";textSize=25f}; l.addView(title)
        fun add(t:String, f:()->Unit){l.addView(Button(this).apply{text=t;setOnClickListener{f()}})}
        add("1. อนุญาตไอคอนลอย"){if(!Settings.canDrawOverlays(this))startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))}
        add("2. เปิด Accessibility"){startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))}
        add("3. เปิดตรวจหน้าจอ"){val m=getSystemService(MediaProjectionManager::class.java);capture.launch(m.createScreenCaptureIntent())}
        add("4. เปิดไอคอนลอย"){startService(Intent(this,FloatingControlService::class.java))}
        add("สร้างไฟล์วิ่งตัวอย่างสำหรับทดสอบ"){ val route=RunRoute("sample",System.currentTimeMillis(),6000,listOf(RunAction("jump",900),RunAction("jump",1700),RunAction("slide",2800,650),RunAction("jump",4300))); val f=store.save(route);toast("บันทึก ${f.name}") }
        val note=TextView(this).apply{text="V1 โครง Android 14+: Overlay + เล่น Route + วนลูป + MediaProjection + ตัวจับคู่ 6 ใบ

การบันทึก touch จริงจากเกมจะต่อใน Recorder module หลังทดสอบบนมือถือจริง เพราะ Android 14 แยกการดัก MotionEvent ออกจากการส่ง touch เข้าเกม";textSize=15f};l.addView(note);setContentView(l)}
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
}
