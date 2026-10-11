# Dem TV 2.1.4 — İzin ve güvenlik düzenlemesi

Bu sürüm, Play Protect ve bankacılık uygulaması uyarıları bildirildikten sonra APK'nın yetkilerini azaltmak için hazırlandı. Uyarıların kesin olarak Dem TV'den kaynaklandığı veya yanlış alarm olduğu doğrulanmış değildir. Bu çalışma bir antivirüs sertifikası, Google onayı veya banka uyumluluk garantisi değildir.

## APK'dan çıkarılanlar

- Erişilebilirlik servisi ve diğer uygulamalara dokunma/tuş gönderme kodu.
- Xposed modül bildirimi, başlangıç dosyası ve kancalama kodu.
- Root komutları ve eski ayrı telefon uygulaması kontrol/yansıtma altyapısı.
- Diğer uygulamaların üzerine çizme, tüm dosyalara erişme, sistem ayarlarını değiştirme, tuş kilidini kaldırma ve tam ekran bildirim izinleri.
- Eski geniş kapsamlı dosya paylaşım sağlayıcısı.
- Yayımlanan uygulama ve dinamik modüller için hata ayıklama erişimi.

Eski kodlar kaynak geçmişinde korunur; Gradle kaynak filtreleriyle yayımlanan APK'ya derlenmeleri engellenir. Gerçek APK'nın manifesti ve DEX dosyaları da yayın öncesinde kontrol edilir. Paket adı ve imza, önceki kuruluma güncellenebilmesi için korunur; teknik dosya/paket adındaki “debug” artık hata ayıklamanın açık olduğu anlamına gelmez.

## Korunan özellikler ve yeni izin davranışı

- TV/IPTV, YouTube, radyo, podcast, dosyalar, favoriler, son içerikler, web kısayolları ve ana sayfa düzeni korunur.
- Ekran yansıtma Android'in **MediaProjection** ekran paylaşımı onayı ve görünür ön plan servisiyle çalışır. Root veya erişilebilirlik istemez. İzin ekranı ve paylaşım servisi dış uygulamalara kapalıdır.
- Yansıtma görüntü içindir; başka uygulamalara dokunma/tuş gönderme, telefon sesini aktarma veya Android Auto'nun sistem harita düzenini değiştirme desteği sağlamaz.
- Dosyalar için Android'in **klasör seçicisi / Storage Access Framework** kullanılır. Önceden tüm dosya erişimine dayanan klasörler varsa telefondan yeniden seçip izin vermek gerekebilir. Kayıtlı kaynaklar/favoriler silinmez.
- Parlaklık kontrolü yalnızca Dem TV'nin penceresini değiştirir; telefonun sistem parlaklığını değiştirmez. Video bittiğinde önceki pencere ayarı geri yüklenir.
- Web siteleri, uygulamanın mevcut kamera/mikrofon/konum iznini otomatik kullanamaz. HTTPS üzerinden açılan mevcut site için telefonda ayrı onay gerekir. Onay site ve kaynak türüne bağlıdır; başka alan adına/porta aktarılmaz ve uygulama süreci bitince unutulur. Android Auto'da yeni site onayı otomatik verilmez.
- WebView yerel dosya erişimi ve dosya URL'leri üzerinden çapraz erişim kapalıdır. Safe Browsing açıktır. HTTPS sayfalarda güvensiz karma içerik yüklenmez. Yerel medya oynatımı web tarayıcısından ayrı çalışır.
- Web metin girişleri ve JavaScript hata içerikleri günlük kaydına yazılmaz. Yayımlanan APK'da eski debug dosya günlüğü kapalıdır.
- Güncelleme, GitHub'dan HTTPS üzerinden alınır; APK boyutu, SHA-256, paket adı, sürüm kodu ve mevcut uygulamayla aynı imza doğrulanır. Kurulum Android'in kullanıcı onayıyla yapılır.

HTTPS indirmelerinde geçersiz/öz imzalı sertifikaları otomatik kabul eden eski TLS yolu kaldırıldı. Kullanıcı kaynakları da sistemin sertifika deposuna ve HTTPS sunucu adı doğrulamasına tabidir. Sertifikası geçersiz bir kaynak artık bağlanamayabilir; kaynak sahibi sertifikayı düzeltmelidir.

## Neden bazı izinler kaldı?

| İzin | Kullanım |
| --- | --- |
| İnternet / ağ durumu | Kullanıcının açtığı medya kaynakları ve sürüm kontrolü |
| Medya dosyalarını okuma | Kullanıcı tarafından seçilen yerel ses/video içerikleri |
| Mikrofon | Kullanıcı etkinleştirirse sesli arama veya ayrıca onaylanan web sitesi |
| Kamera / konum | Kullanıcı ayrıca onaylarsa web sitesi işlevleri |
| Bildirim / ön plan medya ve ekran paylaşımı | Oynatma, paylaşım göstergesi ve güncelleme bildirimi |
| APK yükleme isteği | Doğrulanmış güncelleme için Android yükleme ekranını açma |

SMS, rehber, arama kaydı veya telefon durumu okuma izni istenmez. APK kurulum izni sessiz kurulum yetkisi değildir. Bazı kullanıcının eklediği IPTV kaynakları HTTP kullandığı için uygulama genelinde HTTP medya erişimi korunur; bu tür kaynakların trafiği şifreli değildir. Güncelleme bağlantısı HTTPS olarak zorunludur.

## Bu yayın için tamamlanan kontroller

2.561 birim testi hatasız geçti; Mobile ve Auto Android Lint kontrollerinde 0 hata var. Gerçek APK üzerinde eski izin/servis, DEX sınıfı, Xposed varlığı ve hata ayıklama kontrolleri geçti. Önceki sürümle aynı imza ve ana sayfa görselinin birebir korunması doğrulandı. [APK kontrol raporu ve SHA-256 özeti](distribution/security-apk-check.json).

## Doğrulamanın sınırı

Birim testleri, Android Lint, APK manifest/DEX kontrolleri ve imza doğrulaması yapılır. Telefon/araç üzerinde uçtan uca fiziksel test, bağımsız zararlı yazılım analizi, Play Protect kabulü veya Ziraat Bankası doğrulaması bu kontrollerin yerine geçmez ve yapılmış gibi sunulmaz. KingInstaller ayrı bir geliştiricinin uygulamasıdır; bu sürüm onun kodunu veya bankaların cihaz kontrol kurallarını değiştirmez. Banka, KingInstaller, root, başka uygulamalar veya cihaz politikası nedeniyle erişimi sınırlamaya devam edebilir.

Banka uyarısı alan kullanıcı Dem TV ve KingInstaller'ı geçici olarak kaldırmalı, Play Protect'i açık tutup tarama yapmalı ve bankanın resmî iletişim kanalından hangi uygulamanın tespit edildiğini öğrenmelidir. Bu sürümün uyarıyı kesin kaldıracağı söylenemez. Play Protect ya da bankanın korumasını kapatmak çözüm olarak önerilmez.
