RIYA SCREEN HELPER — SOURCE PROJECT

Yeh source project hai, ready-to-install APK nahi. AndroidIDE jaise Android app development environment mein folder import karke Gradle sync/build karna hoga. Build ke liye Android SDK Platform 35 aur JDK 17 chahiye.

SETUP:
1. ZIP extract karo.
2. AndroidIDE mein RiyaScreenHelper folder open/import karo.
3. Gradle sync hone do, phir app ka debug APK build karo aur install karo.
4. App open > Open Accessibility Settings > RIYA Screen Reader ko manually ON karo.
5. OFF karne ke liye wahi setting manually OFF kar do.

TERMUX BRIDGE TEST:
Install hone aur Accessibility ON hone ke baad Termux mein:
python - <<'PY'
import json, urllib.request
url = 'http://127.0.0.1:8765/screen'
try:
    with urllib.request.urlopen(url, timeout=3) as r:
        data = json.load(r)
    print(data.get('text', ''))
except Exception as e:
    print('Bridge not ready:', e)
PY

NOTE:
- Ye visible UI text read karta hai, screenshots/images ko visually understand nahi karta.
- Password fields skip kiye jaate hain, lekin screen par dikh rahe OTPs ya private messages ko doosre code se share mat karna.
- Local HTTP bridge 127.0.0.1 par hi bind hota hai; internet par expose nahi hota.
- Android background restrictions ke kaaran service/bridge har phone par continuously available rahe, iski guarantee nahi.
- Source project hai; build/install aur manual Accessibility approval zaroori hai.
