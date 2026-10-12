# Mola 2.1.5 — azaltılmış yetkiler, doğrulama kapsamı

2.1.4 kullanıcıda kapanma bildirimleri üzerine kararlı listeden çekildi. Kullanıcı 2.1.5’in çalıştığını bildirdi; normal kurulumla Android Auto’da görünmediğini de bildirdi. KingInstaller ile araç doğrulaması ve kesin kapanma nedeni için hata kaydı beklenir; 2.1.5 ön sürüm olarak kalır. Play Protect/banka uyarısının kesin olarak hangi uygulamadan kaynaklandığı belirlenmemiştir. Bu belge bir antivirüs sertifikası, Google onayı veya banka uyumluluk garantisi değildir.

## APK’da bulunanlar

TV/IPTV, YouTube, tarayıcı; Android’in kullanıcı onayıyla MediaProjection görüntü paylaşımı. TV için ExoPlayer/VLC motorları korunur. WindAsist ve Mola bağlantıları tarayıcı kısayollarıdır. Paket ve imza, kullanıcı kaynakları ve Android Keystore kullanan şifreli tercih saklama korunur.

## APK’da bulunmayanlar

2.1.8 ile Radyo eklentisi (yalnızca internet izniyle Radio Browser dizini ve kullanıcının eklediği akışlar) yeniden pakete alındı. 2.1.9 ile Podcast eklentisi (Apple Podcasts/fyyd araması ve RSS akışları; ek izin yok) pakete alındı. Sesli kitap, Stremio, sohbet, Cast, Google Drive, SMB/SFTP eklentileri üretim modül listesinden çıkarılmıştır. Eski root/Xposed/erişilebilirlik/giriş enjeksiyonu sınıfları, Xposed başlangıç dosyası ve geniş harici dosya paylaşım sağlayıcısı paketlenmez. Kaynak geçmişi APK içeriği değildir; manifest ve DEX ayrıca taranır.

Kamera, mikrofon, hassas/yaklaşık konum, genel medya/dosya okuma, bütün dosyalara erişim, diğer uygulamaların üzerinde gösterme, sistem ayarlarını değiştirme, tuş kilidini kaldırma ve tam ekran bildirim izinleri yoktur. SMS, rehber, arama kaydı ve telefon durumu okuma izinleri de yoktur. Google hizmetleri derlemesi kapalıdır. APK hata ayıklamaya kapalıdır; teknik paket/dosya adındaki `debug` bir güncelleme kimliği olarak tutulmuştur.

Web sitelerinin kamera, mikrofon ve konum istekleri reddedilir. DRM medya izni yalnızca aynı HTTPS kaynağı için verilir. WebView yerel dosya erişimi kapalıdır; Safe Browsing açıktır ve HTTPS sayfalarda güvensiz karma içerik engellenir. Geçersiz/öz imzalı HTTPS sertifikaları kabul edilmez. IPTV HTTP kaynakları kullanıcı tarafından seçilebildiği için düz HTTP medya trafiği desteklenir; bu trafik şifreli değildir.

## Gereken izinler

| İzin | Amaç |
| --- | --- |
| İnternet / ağ durumu | Açılan yayınlar, web sayfaları ve GitHub sürüm kontrolü |
| Bildirim / ön plan medya ve ekran paylaşımı | Kullanıcıya oynatma/paylaşım durumu ve güncelleme bildirimi |
| Uyanık tutma | Kullanıcının açık video ekranı |
| APK yükleme isteği | Aynı imzalı, daha yüksek sürüm kodlu güncellemenin Android kullanıcı onayıyla kurulması |
| Yeniden başlatma bildirimi | Android tarafından ertelenebilen güncelleme kontrolünü yeniden planlama |

Ekran paylaşımı Android izin ekranında onaylanmadan başlamaz. İzin iptali, ana sayfaya dönüş veya araç bağlantısının kapanması paylaşımı sonlandırır. Paylaşım servisi ve izin etkinliği dış uygulamalara kapalıdır. Banka/DRM ekranlarının yakalamayı engelleyen koruması aşılmaz. Uzaktan dokunmatik kontrol ve telefon sesi aktarımı yoktur.

Güncellemede HTTPS, APK boyutu, SHA-256, paket adı, sürüm kodu ve aynı imza kontrol edilir; sessiz kurulum yapılmaz. Ön sürümler otomatik kararlı güncelleme olarak önerilmez. Tanılama yereldir; kullanıcı dışa aktarmadıkça hata raporu gönderilmez. Web metin girişleri/JavaScript içerikleri ve eski debug dosya günlüğü kaydedilmez.

## Kontrollerin sınırı

[Birim testleri ve sürüm notları](UPDATE_RELEASE_NOTES_TR.md), Mobile/Auto lint ve [gerçek APK manifest/DEX kontrol raporu](distribution/security-apk-check.json) yayın içeriğini denetler. Bunlar fiziksel telefon/Android Auto testi, bağımsız zararlı yazılım analizi, Play Protect veya banka kabulü yerine geçmez. Android 9 minimum/Android 16 hedef SDK değerleri uyumluluk iddiası değildir.

APK’da KingInstaller bağımlılığı veya kurulum kaynağı taklidi yoktur. Kurulum rehberi, Android Auto’da görünmeyen kurulumlarda ayrı üçüncü taraf KingInstaller yükleyicisinin klasik yöntemini de anlatır; KingInstaller’ın kendi yetkileri ve uyumluluğu Mola APK denetimine dahil değildir. Normal Android kurulumu, Google’ın özel video/tarayıcı/yansıtma arayüzünü her cihazda kabul edeceğini garanti etmez. Bankanın hangi uygulamayı tespit ettiği bankanın resmî kanalından öğrenilmelidir; korumalar kapatılarak sorun saklanmaz.
