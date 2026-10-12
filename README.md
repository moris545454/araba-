# Dem TV

TV/IPTV izleme, YouTube, tarayıcı ve izinle ekran yansıtma için Türkçe Android uygulaması.

[APK indirme ve kurulum sayfası](https://dem-tv-indir.chirpypeach128.chatgpt.site) · [**2.1.7 APK’sını indir**](https://media.githubusercontent.com/media/moris545454/araba-/v2.1.7/distribution/FermataX-yeni-arayuz-debug.apk) · [Kurulum rehberi](KURULUM_REHBERI_TR.md) · [Güvenlik kapsamı](GUVENLIK_NOTLARI_TR.md) · [Sürüm notları](UPDATE_RELEASE_NOTES_TR.md)

## Sürüm durumu

**Dem TV 2.1.7** — [APK’yı indir](https://media.githubusercontent.com/media/moris545454/araba-/v2.1.7/distribution/FermataX-yeni-arayuz-debug.apk) · [sürüm notları](UPDATE_RELEASE_NOTES_TR.md)

- Ana sayfada **Favori Kanallar** rafı ve kendi web sitelerinizi ekleyebileceğiniz **Kısayol ekle** kartı.
- TV listesinde varsayılan **Türk radyoları** listesi (Radio Browser dizini).
- TV ana listesinde **+** düğmesiyle kendi M3U/IPTV listenizi ekleme; düğme yalnızca bu ekranda bulunur.

2.1.7 yeni bir imza anahtarıyla derlendi; telefonda daha eski bir Dem TV kuruluysa önce onu kaldırın. Fiziksel telefon ve araç testi yapılmadı.

2.1.4, kullanıcıların TV/YouTube açarken kapanma bildirmesi üzerine kararlı sürüm listesinden geri çekildi. **Kullanıcı 2.1.5’in çalıştığını bildirdi; normal kurulumla Android Auto’da görünmediği de bildirildi.** KingInstaller ile araç doğrulaması beklenmektedir; sürüm ön sürüm olarak kalır. Kapanmanın kesin nedeni cihaz hata kaydı olmadan doğrulanmış sayılmaz. Ön sürüm otomatik güncelleme olarak önerilmez.

## Kapsam

Ana sayfada TV, YouTube ve ekran yansıtma ana kartları; tarayıcı, WindAsist ve Dem TV web kısayolları bulunur. Uygulama sayfalarında ana sayfaya dönüş korunur. Yan kısayollar varsayılan olarak gizlidir; ayarlardan açılır.

TV’nin varsayılan M3U kaynağı https://onureroz.com/indirmeler/turk/index.m3u adresidir; yanında Radio Browser dizininden en çok oy alan Türk radyolarının listesi bulunur. Kullanıcı TV ana listesindeki + düğmesiyle kaynak ekleyebilir, uzun basarak silebilir; silinen varsayılan listeler aynı menüden geri yüklenir. HLS, DASH ve diğer IPTV biçimlerini oynatmak için ExoPlayer/VLC motorları korunmuştur. Radyo, podcast, sesli kitap, Stremio, sohbet, Cast, Google Drive, SMB ve SFTP eklentileri APK’ya derlenmez. Kamera, mikrofon, konum ve genel medya/dosya okuma izinleri kaldırılmıştır.

Ekran yansıtma Android’in MediaProjection iznini ister. Android Auto telefon ekranını kendiliğinden açmaya izin vermezse telefon uygulama simgesi veya bildirim aynı bekleyen isteği açar. İzin iptali kaydı başlatmaz; ana sayfaya dönüş/bağlantı kesilmesi paylaşımı durdurur. Bu görüntü paylaşımıdır; uzaktan dokunmatik kontrol veya telefon sesi aktarımı değildir.

## Android Auto

APK’da KingInstaller bağımlılığı, kurulum kaynağını taklit eden kod, erişilebilirlik servisi, root veya Xposed yoktur. Telefona standart Android yükleyicisiyle kurulabilir; Android Auto’da görünmeyen kurulumlar için [KingInstaller 2.3 üzerinden kurulum](KURULUM_REHBERI_TR.md) rehberini izleyin. KingInstaller ayrı bir üçüncü taraf yükleyicidir. [Resmî APK indirme](https://github.com/fcaronte/KingInstaller/releases/download/2.3/KingInstaller-v2.3.apk). **Bu, her telefonda Android Auto’nun uygulamayı kabul edeceği anlamına gelmez.** Özel video/tarayıcı/yansıtma arayüzü eski projection SDK’sını kullanır; Google’ın dağıtım kuralları, telefon ve Android Auto sürümü görünürlüğü belirler. Google onayı veya bütün yeni Android sürümlerinde sorunsuz çalışma garantisi verilmez.

## Doğrulama ve derleme

`verifyWebOnlyProductionGraph` görevi artık yalnızca `tv`, `web`, `exoplayer`, `vlc` üretim modüllerine izin verir. Statik APK kapısı yasak izinleri, kaldırılan eklenti sınıflarını, root/Xposed/erişilebilirlik kodlarını ve yansıtma bileşenlerini kontrol eder. Bu kontroller banka/Play Protect onayı veya zararlı yazılım sertifikası değildir.

Java 21, Android SDK 36 ve Gradle wrapper ile:

```bash
./gradlew :fermata:testMobileDebugUnitTest :fermata:testAutoDebugUnitTest :web:testMobileDebugUnitTest :tv:testMobileDebugUnitTest
./gradlew :fermata:lintMobileDebug :fermata:lintAutoDebug verifyWebOnlyProductionGraph
./gradlew :fermata:packageAutoDebugUniversalApk
python3 scripts/verify_security_apk.py /path/to/app.apk --aapt /path/to/aapt
```

Eski kurulumların güncellenmesi için paket kimliği ve imza korunur. Dağıtım varyantı dosya adında `debug` taşısa da APK `debuggable=false` olarak paketlenir. Yayın sertifikasının özel anahtarı depoya konmaz. [Teknik bağlam](MASTER_CONTEXT.md) ve [yayın süreci](GITHUB_UPDATES_TR.md).

## Lisans ve kaynak

Dem TV, [Fermata Media Player](https://github.com/AndreyPavlenko/Fermata) ve FermataX kaynaklarından türetilmiş GPL-3.0 projesidir. Andrey Pavlenko ve diğer katkıcıların telif/lisans bildirimleri korunur. Kaldırılan özelliklerin kaynak geçmişi Git’te bulunabilir; bu onların dağıtılan APK’da bulunduğu anlamına gelmez. [Lisans](LICENSE).
