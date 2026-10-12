# Mola 2.2.2 — düzeltmeler

- **Sesli karşılama ismi:** İsim yazarken klavyenin açılıp kapanması düzeltildi. İsim artık klavyedeki **Bitti/Tamam** tuşuna basınca kaydedilir.
- **Tekrarlayan “İndirilemiyor … .xml” bildirimleri kaldırıldı:** Kanal listesindeki yayın akışı (EPG) dosyası indirilemezse artık bildirim atılmaz; uygulama arka planda sessizce yeniden dener. Kanallar bundan etkilenmez.
- **Bildirim yok:** Mola artık güncelleme, liste veya yayın akışı indirme bildirimi göndermez. Yalnızca oynatma kontrolleri ve ekran paylaşımı için Android’in zorunlu bildirimi görünür.
- **Arka planda veri güncellemesi yok:** 6 saatlik arka plan güncelleme kontrolü kaldırıldı. Yayın akışı (EPG) gibi veriler yalnızca uygulama açıkken veya araç bağlıyken güncellenir; uygulama kullanılmıyorsa güncelleme açılana kadar bekler.
- Yeni sürüm **Ayarlar → Arayüz → Uygulama güncellemeleri** ekranından kontrol edilip kurulur.
- Aynı paket ve imza: kaldırmadan üzerine güncellenir.

---

# Mola 2.2.1 — Sesli karşılama

- **Sesli karşılama (isteğe bağlı):** Telefon Android Auto’ya bağlanınca Mola saate göre kısa bir karşılama yapar: “Günaydın Ahmet, Mola’ya hoş geldin. İyi yolculuklar!” Varsayılan olarak kapalıdır; **Ayarlar → Arayüz → Sesli karşılama** bölümünden açılır, isim girilebilir, “Günde yalnızca bir kez” seçilebilir.
- Konuşurken çalan müzik kısa süre kısılır, sonra kendi seviyesine döner; yayın durdurulmaz. Mikrofon kullanılmaz, yeni izin yoktur.
- Telefonda bu dil için ses yoksa karşılama sessizce atlanır; uygulamanın diğer işlevleri etkilenmez.
- Aynı paket ve imza: kaldırmadan üzerine güncellenir.

---

# Mola 2.2.0 — yeni isim

- Uygulamanın adı **Dem TV → Mola** oldu. Telefonda simgenin altında, Android Auto’da, bildirimlerde, ayarlarda ve kurulum sayfasında artık “Mola” yazar.
- Paket kimliği ve imza değişmedi: 2.1.7–2.1.9 kuruluysa kaldırmadan üzerine güncellenir; TV/radyo listeleri, favoriler ve ayarlar korunur.
- Özellikler 2.1.9 ile aynıdır (TV, Radyo, Podcast, YouTube, tarayıcı, ekran yansıtma, direksiyon tuşları).

---

# Mola 2.1.9 — Podcast ve direksiyon tuşları

- **Podcast ana sayfada:** Podcast arama (Apple Podcasts ve fyyd dizinleri), RSS bağlantısıyla abone olma, bölüm dinleme ve kaldığı yerden devam. Araçta ses oynatıcı çubuğuyla çalışır.
- **Direksiyon tuşları:** Direksiyondaki ileri/geri tuşları TV’de sonraki/önceki kanala, radyoda sonraki/önceki istasyona, podcast’te sonraki/önceki bölüme geçer. Kanal yukarı/aşağı tuşu gönderen araçlarda bu tuşlar da kanal değiştirir. Eşleştirme **Ayarlar → Arayüz → Tuş atamaları** bölümünden değiştirilebilir.
- 2.1.7/2.1.8 ile aynı imza: kaldırmadan üzerine güncellenir.

---

# Mola 2.1.8 — ana sayfada Radyo

- **Radyo ana sayfada:** Radyo eklentisi yeniden pakete alındı; ana sayfadaki kısa yol kartı ve gezinme menüsünden açılır.
- **Türk radyoları en üstte:** Radyo ekranında ilk giriş Radio Browser dizinindeki en çok dinlenen Türk radyolarıdır. Ardından Popüler, En çok oy alan, Ülkeler ve Etiketler gelir.
- **Kendi radyo akışını ekle:** Radyo ekranındaki ekle düğmesiyle istasyon adı ve akış adresi kaydedilebilir.
- Radyo araçta ses oynatıcı çubuğuyla çalışır; geri tuşu yayını durdurmaz.
- 2.1.7 ile aynı imza: 2.1.7 kuruluysa kaldırmadan üzerine güncellenir.

---

# Mola 2.1.7 — favori kanallar, web kısayolları, radyo ve liste ekleme

- **Favori Kanallar rafı:** Favorilere eklenen kanallar ana sayfada, Son İçerikler’in üstünde görünür.
- **Kısayol ekle:** Ana sayfaya kendi web sitelerinizi (en fazla 6) ekleyip kaldırabilirsiniz; uygulama içi tarayıcıda açılır.
- **Türk radyoları:** TV listesine Radio Browser dizininden en çok oy alan 150 Türk radyosunun listesi bir kez eklenir. Silinirse geri eklenmez; “Varsayılan listeleri geri yükle” ile geri alınır.
- **Liste ekleme:** **+** düğmesi yalnızca TV ana listesinde bulunur. Menü: Bağlantı ile liste ekle, M3U oynatma listesi, Xtream Codes, Stalker portalı ve gerektiğinde Varsayılan listeleri geri yükle.
- **Liste bağlantısını kopyala:** M3U listelerinin uzun basma menüsünde.
- YouTube video ölçekleme ayarı Türkçe telefon dilinde doğru çalışır.
- Varsayılan Türkiye kanalları listesi, kayıtlı kaynaklar, ayarlar ve mevcut TV/YouTube/tarayıcı/yansıtma akışları değiştirilmedi.

Doğrulama: Yeni kurallar ve kaynak sözleşmeleri için birim testleri eklendi. Fiziksel telefon/araç testi yapılmadı. APK yeni bir imza anahtarıyla derlendi; eski bir Mola kuruluysa önce kaldırılmalıdır.

---

# Mola 2.1.5 — dört temel işlev için ön sürüm

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

Telefona standart Android yükleyicisiyle kurulabilir. Android Auto’da görünmeyen kurulumlar için rehberde KingInstaller 2.3 üzerinden klasik kurulum anlatılır. KingInstaller ayrı yükleyicidir; Mola APK’sında KingInstaller bağımlılığı veya yükleme kaynağı taklidi yoktur. Android Auto’da özel video/tarayıcı/yansıtma arayüzünün kabulü Google kuralları ve cihaz desteğine bağlıdır. Her yeni Android sürümünde sorunsuz çalışma veya banka/Play Protect kabulü garanti edilmez.

Doğrulama: 2.510 birim testi geçti; telefon ve Auto lint kontrollerinde hata yok (mevcut uyarılar sürüyor). Nihai APK statik izin/sınıf denetiminden geçti; güncelleme imzası ve ana kart görselleri önceki sürümle aynı.

Android 11 yazılım emülatörü, uygulama kurulmadan önce sistem yeniden başlamaları yaşadığı için güvenilir TV/YouTube çalışma sonucu alınamadı. Geliştirici tarafından kontrollü fiziksel telefon/araç testi yapılmadı; kullanıcı çalışma teyidi bir cihaz bildirimiyle sınırlıdır. Birim testleri, lint ve statik APK denetimi fiziksel telefon/araç testinin yerine geçmez. Kapanmayı tamamıyla doğrulamak için telefon modeli, Android ve uygulama sürümüyle hata kaydı gerekir.

[Kurulum rehberi](KURULUM_REHBERI_TR.md) · [Güvenlik kapsamı](GUVENLIK_NOTLARI_TR.md)
