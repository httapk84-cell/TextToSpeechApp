# Gradle Wrapper is not included here. Open this project in Android Studio and let it generate the wrapper automatically.

1. Mở Android Studio.
2. Chọn: File > Open > chọn thư mục "TextToSpeechApp".
3. Android Studio sẽ tải Gradle và các dependencies.
4. Sau khi build xong, chọn:
   - Build > Build Bundle(s) / APK(s) > Build APK(s)
5. File APK sẽ xuất ra trong:
   - app/build/outputs/apk/debug/app-debug.apk

Cách sử dụng app:
- Nhập văn bản vào ô nhập liệu
- Nhấn "Phát giọng nói" để nghe
- Nhấn "Lưu MP3" để tạo file âm thanh
- File được lưu ở:
  - Android/data/com.example.texttospeechapp/files/Music/

Lưu ý:
- Một số thiết bị không hỗ trợ tiếng Việt đầy đủ từ TTS engine mặc định của Android.
- Nếu cần MP3 chất lượng cao hơn, hãy dùng motor giọng nói hỗ trợ hoặc thêm engine TTS khác.
