package com.samuraixd.cookierun.ui

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.samuraixd.cookierun.core.RouteStore
import com.samuraixd.cookierun.core.RunAction
import com.samuraixd.cookierun.core.RunRoute
import com.samuraixd.cookierun.service.FloatingControlService
import com.samuraixd.cookierun.service.ScreenCaptureService

class MainActivity : AppCompatActivity() {
    private lateinit var store: RouteStore

    private val capture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = Intent(this, ScreenCaptureService::class.java)
                    .putExtra(ScreenCaptureService.EXTRA_CODE, result.resultCode)
                    .putExtra(ScreenCaptureService.EXTRA_DATA, result.data)

                startForegroundService(intent)
                toast("เปิดตรวจหน้าจอแล้ว")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        store = RouteStore(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        layout.addView(TextView(this).apply {
            text = "SamuraixD CookieRun — Run Recorder"
            textSize = 25f
        })

        fun addButton(text: String, action: () -> Unit) {
            layout.addView(Button(this).apply {
                this.text = text
                setOnClickListener { action() }
            })
        }

        addButton("1. อนุญาตไอคอนลอย") {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        }

        addButton("2. เปิด Accessibility") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        addButton("3. เปิดตรวจหน้าจอ") {
            val manager = getSystemService(MediaProjectionManager::class.java)
            capture.launch(manager.createScreenCaptureIntent())
        }

        addButton("4. เปิดไอคอนลอย") {
            startService(Intent(this, FloatingControlService::class.java))
        }

        addButton("สร้างไฟล์วิ่งตัวอย่างสำหรับทดสอบ") {
            val route = RunRoute(
                "sample",
                System.currentTimeMillis(),
                6000,
                listOf(
                    RunAction("jump", 900),
                    RunAction("jump", 1700),
                    RunAction("slide", 2800, 650),
                    RunAction("jump", 4300)
                )
            )

            val file = store.save(route)
            toast("บันทึก ${file.name}")
        }

        layout.addView(TextView(this).apply {
            text = """
                V1 โครง Android 14+: Overlay + เล่น Route + วนลูป + MediaProjection + ตัวจับคู่ 6 ใบ

                การบันทึก touch จริงจากเกมจะต่อใน Recorder module หลังทดสอบบนมือถือจริง เพราะ Android 14 แยกการดัก MotionEvent ออกจากการส่ง touch เข้าเกม
            """.trimIndent()
            textSize = 15f
        })

        setContentView(layout)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
