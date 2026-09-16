package com.samuraixd.cookierun.core

import android.graphics.Bitmap
import kotlin.math.abs

object CardMatcher {
    data class Result(val i:Int,val j:Int,val confidence:Double)
    private fun signature(b:Bitmap, box:Box):DoubleArray{
        val sx=maxOf(0,box.x); val sy=maxOf(0,box.y); val ex=minOf(b.width,sx+box.w); val ey=minOf(b.height,sy+box.h)
        val bins=DoubleArray(16); var n=0
        val stepx=maxOf(1,(ex-sx)/20); val stepy=maxOf(1,(ey-sy)/20)
        var y=sy
        while(y<ey){ var x=sx; while(x<ex){ val c=b.getPixel(x,y); val gray=((c shr 16 and 255)*30+(c shr 8 and 255)*59+(c and 255)*11)/100; bins[(gray*16/256).coerceIn(0,15)]++; n++; x+=stepx }; y+=stepy }
        if(n>0) for(k in bins.indices) bins[k]/=n.toDouble(); return bins
    }
    private fun d(a:DoubleArray,b:DoubleArray)=a.indices.sumOf { abs(a[it]-b[it]) }
    fun solve(bitmap:Bitmap, boxes:List<Box>):Result?{
        if(boxes.size!=6) return null; val s=boxes.map{signature(bitmap,it)}; var best:Result?=null; var bestScore=Double.MAX_VALUE
        for(i in 0..4) for(j in i+1..5){ val others=(0..5).filter{it!=i&&it!=j}; val pair=d(s[i],s[j]); var within=0.0; var cnt=0
            for(a in others.indices) for(bb in a+1 until others.size){ within+=d(s[others[a]],s[others[bb]]);cnt++ }
            val cross=others.sumOf { (d(s[i],s[it])+d(s[j],s[it]))/2 }/others.size; val score=pair+(within/maxOf(1,cnt))-cross
            if(score<bestScore){ bestScore=score; best=Result(i,j,(cross-pair).coerceAtLeast(0.0)) }
        }
        return best?.takeIf{it.confidence>0.05}
    }
}
