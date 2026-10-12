# Dem TV 2.1.7 — favori kanallar, web kısayolları, radyo ve liste ekleme

- **Favori Kanallar rafı:** Favorilere eklenen kanallar ana sayfada, Son İçerikler’in üstünde görünür.
- **Kısayol ekle:** Ana sayfaya kendi web sitelerinizi (en fazla 6) ekleyip kaldırabilirsiniz; uygulama içi tarayıcıda açılır.
- **Türk radyoları:** TV listesine Radio Browser dizininden en çok oy alan 150 Türk radyosunun listesi bir kez eklenir. Silinirse geri eklenmez; “Varsayılan listeleri geri yükle” ile geri alınır.
- **Liste ekleme:** **+** düğmesi yalnızca TV ana listesinde bulunur. Menü: Bağlantı ile liste ekle, M3U oynatma listesi, Xtream Codes, Stalker portalı ve gerektiğinde Varsayılan listeleri geri yükle.
- **Liste bağlantısını kopyala:** M3U listelerinin uzun basma menüsünde.
- YouTube video ölçekleme ayarı Türkçe telefon dilinde doğru çalışır.
- Varsayılan Türkiye kanalları listesi, kayıtlı kaynaklar, ayarlar ve mevcut TV/YouTube/tarayıcı/yansıtma akışları değiştirilmedi.

Doğrulama: Yeni kurallar ve kaynak sözleşmeleri için birim testleri eklendi. Fiziksel telefon/araç testi yapılmadı. APK yeni bir imza anahtarıyla derlendi; eski bir Dem TV kuruluysa önce kaldırılmalıdır.

---

# Dem TV 2.1.5 — dört temel işlev için ön sürüm

2.1.4’te kullanıcı TV/YouTube açılışında uygulamanın kapandığını bildirdi. Bu sürüm kararlı listeden çekildi. **Kullanıcı 2.1.5’in çalıştığını bildirdi.** Normal kurulumla Android Auto’da görünmediği bildirildiği için rehbere KingInstaller üzerinden kurulum eklendi. Araç doğrulaması beklenmektedir; kapanmanın kesin nedeni gerçek cihaz hata kaydıyla doğrulanmış değildir. Bu nedenle henüz otomatik kararlı güncelleme olarak yayınlanmaz.

## Değişiklikler

- APK’da yalnızca TV, Web/YouTube ve TV’nin ExoPlayer/VLC motorları derlenir. Ekran paylaşımı temel Auto bileşenindedir.
- Radyo, podcast, sesli kitap, Stremio, sohbet, Cast, Google Drive, SMB/SFTP eklentileri paketlenmez.
- Kamera, mikrofon, konum ve genel dosya/medya okuma izinleri kaldırılır. Eski SDK’nın otomatik eklediği READ_EXTERNAL_STORAGE izni de manifest birleştirmesinde engellenir.
- Android Auto ekran parlaklığını kendisi yönetir. Video açma/kapamada bu pencere API’si çağrılmaz; telefondaki isteğe bağlı parlaklık hatası oynatmayı kesmez.
- Kaldırılmış bir sayfayı hatırlayan eski oturumlar güncelleme sonrası ana sayfaya döner.
- TV/YouTube/yansıtma ana kart görselleri, tarayıcı ve iki web kısayolu; kullanıcı TV kaynakları, varsayılan M3U ve ana sayfa dönüşü korunur.
- Native ekran paylaşımı Android’in kullanıcı izniyle başlar. Root, erişilebilirlik, Xposed ve giriş enjeksiyonu APK’ya geri eklenmez.
- Paket kimliği, imza, şifreli kullanıcı tercihleri, TLS sertifika denetimi ve imza/özet doğrulayan güncelleme mekanizması korunur.

## Kurulum ve sınırlar

Telefona standart Android yükleyicisiyle kurulabilir. Android Auto’da görünmeyen kurulumlar için rehberde KingInstaller 2.3 üzerinden klasik kurulum anlatılır. KingInstaller ayrı yükleyicidir; Dem TV APK’sında KingInstaller bağımlılığı veya yükleme kaynağı taklidi yoktur. Android Auto’da özel video/tarayıcı/yansıtma arayüzünün kabulü Google kuralları ve cihaz desteğine bağlıdır. Her yeni Android sürümünde sorunsuz çalışma veya banka/Play Protect kabulü garanti edilmez.

Doğrulama: 2.510 birim testi geçti; telefon ve Auto lint kontrollerinde hata yok (mevcut uyarılar sürüyor). Nihai APK statik izin/sınıf denetiminden geçti; güncelleme imzası ve ana kart görselleri önceki sürümle aynı.

Android 11 yazılım emülatörü, uygulama kurulmadan önce sistem yeniden başlamaları yaşadığı için güvenilir TV/YouTube çalışma sonucu alınamadı. Geliştirici tarafından kontrollü fiziksel telefon/araç testi yapılmadı; kullanıcı çalışma teyidi bir cihaz bildirimiyle sınırlıdır. Birim testleri, lint ve statik APK denetimi fiziksel telefon/araç testinin yerine geçmez. Kapanmayı tamamıyla doğrulamak için telefon modeli, Android ve uygulama sürümüyle hata kaydı gerekir.

[Kurulum rehberi](KURULUM_REHBERI_TR.md) · [Güvenlik kapsamı](GUVENLIK_NOTLARI_TR.md)
