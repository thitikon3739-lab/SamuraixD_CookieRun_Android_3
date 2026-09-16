package com.samuraixd.cookierun.service

import android.app.*
import android.content.*
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*

class ScreenCaptureService: Service(){
    companion object { @Volatile var latest:Bitmap?=null; const val EXTRA_CODE="code"; const val EXTRA_DATA="data" }
    private var mp:MediaProjection?=null; private var reader:ImageReader?=null
    override fun onBind(i:Intent?)=null
    override fun onStartCommand(i:Intent?,flags:Int,startId:Int):Int{
        startForeground(7,notification()); val code=i?.getIntExtra(EXTRA_CODE,Activity.RESULT_CANCELED)?:return START_NOT_STICKY
        val data: Intent? = if(Build.VERSION.SDK_INT>=33) i.getParcelableExtra(EXTRA_DATA,Intent::class.java) else @Suppress("DEPRECATION") i.getParcelableExtra(EXTRA_DATA)
        if(data==null)return START_NOT_STICKY; val m=getSystemService(MediaProjectionManager::class.java); mp=m.getMediaProjection(code,data)
        val dm=resources.displayMetrics; reader=ImageReader.newInstance(dm.widthPixels,dm.heightPixels,PixelFormat.RGBA_8888,2)
        mp?.createVirtualDisplay("SamuraixDCapture",dm.widthPixels,dm.heightPixels,dm.densityDpi,DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,reader!!.surface,null,null)
        reader!!.setOnImageAvailableListener({ r -> val img=r.acquireLatestImage()?:return@setOnImageAvailableListener; try{ val p=img.planes[0]; val row=p.rowStride; val px=p.pixelStride; val extra=row-px*img.width; val tmp=Bitmap.createBitmap(img.width+extra/px,img.height,Bitmap.Config.ARGB_8888); tmp.copyPixelsFromBuffer(p.buffer); latest=Bitmap.createBitmap(tmp,0,0,img.width,img.height); tmp.recycle() } finally{img.close()} },Handler(Looper.getMainLooper()))
        return START_STICKY
    }
    private fun notification():Notification{ val id="capture"; val nm=getSystemService(NotificationManager::class.java); nm.createNotificationChannel(NotificationChannel(id,"Screen capture",NotificationManager.IMPORTANCE_LOW)); return Notification.Builder(this,id).setContentTitle("SamuraixD Run").setContentText("กำลังตรวจหน้าจอเกม").setSmallIcon(android.R.drawable.ic_media_play).build() }
    override fun onDestroy(){ reader?.close(); mp?.stop(); latest?.recycle(); latest=null; super.onDestroy() }
}
