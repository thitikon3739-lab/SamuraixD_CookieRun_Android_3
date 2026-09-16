# SamuraixD CookieRun Android V1

เป้าหมาย: ย้ายเฉพาะระบบที่ใช้งานจริงจาก Engine3 มาเป็น APK Android 14+ มีไอคอนลอย ย่อ/ขยาย/ลากได้, เล่น route ซ้ำวนลูป, ซื้อ Fast Start/กด Play/ใช้ Fast Start, รองรับ Relay/Finish และตัวจับคู่การ์ด 6 ใบ

## สิ่งที่ทำแล้วในซอร์ส V1
- Overlay floating icon + panel
- AccessibilityService สำหรับ dispatchGesture
- MediaProjection foreground service สำหรับจับภาพหน้าจอ
- Route JSON (jump + slide duration) และ RouteStore
- Route player แบบ timing-based
- Controller วนรอบ Start -> Fast Start -> Purchase -> Play -> Route -> Relay -> Finish
- CardMatcher 6 ใบ แบบ image histogram difference และไม่กดเมื่อ confidence ต่ำ
- พิกัดทั้งหมด scale จากฐาน 1280x720 ผ่าน assets/default_profile.json

## สำคัญ
ซอร์สนี้เป็น Android Studio project แต่ environment ที่สร้างไฟล์นี้ไม่มี Android SDK/Gradle จึงยังไม่ได้ compile เป็น APK ในที่นี่

Android 14+ บังคับให้ผู้ใช้อนุญาต screen capture ต่อ session และ Accessibility API สำหรับ MotionEvent มีข้อจำกัดว่าการดัก source สามารถกิน event ไม่ให้ไปถึงเกมได้ ดังนั้น V1 นี้เปิด Playback/Loop ก่อน ส่วน Recorder touch จริงควรทดสอบบนเครื่องจริงแล้วใช้ TouchInteractionController/forward gesture ตามรุ่นมือถือ เพื่อไม่ให้การกดระหว่างบันทึกหน่วงหรือหาย

## เปิดใน Android Studio
1. Open โฟลเดอร์โปรเจกต์
2. ใช้ JDK 17+
3. Sync Gradle
4. Run บน Android 14/15
5. ในแอป: อนุญาต Overlay -> เปิด Accessibility -> เปิด Screen Capture -> เปิดไอคอนลอย

## จุดที่ต้องปรับหลังทดสอบมือถือจริง
`app/src/main/assets/default_profile.json` คือพิกัดสำคัญทั้งหมด หาก UI CookieRun บนมือถือจริงต่างจาก 1280x720 เดิม ให้ปรับ profile นี้หรือเพิ่มหน้า Calibration
