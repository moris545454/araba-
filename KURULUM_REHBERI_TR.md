# Dem TV — standart APK kurulumu

**Güncel hazırlanan sürüm: 2.1.5 ön sürüm.** 2.1.4’te kapanma bildirildi; kararlı olarak dağıtılmıyor. Yeni sürümün araç testi henüz tamamlanmadı. Bu rehber KingInstaller, root veya Shizuku gerektirmeyen normal APK kurulumunu anlatır; Android Auto’nun her cihazda uygulamayı göstermesini garanti etmez.

## İndirme

Herkese açık sayfa: https://dem-tv-indir.chirpypeach128.chatgpt.site

Doğrudan test APK’sı: https://media.githubusercontent.com/media/moris545454/araba-/v2.1.5/distribution/FermataX-yeni-arayuz-debug.apk

Sürüm ayrıntıları: https://github.com/moris545454/araba-/releases/tag/v2.1.5

## Telefona yükleme

1. Bağlantıyı Chrome gibi bir tarayıcıda açın. Instagram indirme başlatmazsa menüsünden harici tarayıcıda açın.
2. APK’yı indirin, ardından indirilen dosyaya dokunun.
3. Android izin isterse **Ayarlar → Uygulamalar → Özel uygulama erişimi → Bilinmeyen uygulamaları yükle** yolunu izleyin. Dosyayı açan tarayıcı/dosya yöneticisi için **Bu kaynaktan izin ver** seçeneğini açın. Telefon üreticisine göre menü adı değişebilir.
4. Kurulum ekranına dönüp **Yükle/Güncelle** seçeneğini kullanın. Önceki Dem TV’yi kaldırmayın; paket ve imza aynı tutulmuştur.
5. Telefonda uygulamayı açın. Yeni güncelleme/izin bildirimlerini almak istiyorsanız bildirim iznini verin. Kurulum bittikten sonra tarayıcının bilinmeyen uygulama yükleme iznini kapatabilirsiniz.

Kamera, mikrofon, konum, erişilebilirlik, diğer uygulamaların üzerinde gösterme ve tüm dosyalara erişim izni gerekmez. Play Protect engelini aşmak için korumayı kapatmayın; kaynağı, dosya bütünlüğünü ve uyarıyı kontrol edin.

## Android Auto ayarları

1. Android Auto ve Android System WebView/Chrome’u güncelleyin.
2. Telefon ayarlarında **Android Auto** sayfasını bulun. Test kurulumu için gerekiyorsa **Sürüm** bilgisine yaklaşık 10 kez dokunarak geliştirici ayarlarını açın.
3. Menüde varsa **⋮ → Geliştirici ayarları → Bilinmeyen kaynaklar** seçeneğini açın. Bu seçenek telefonun APK kurulum izninden ayrıdır; Google’ın uygulama kabul kurallarını ortadan kaldırmaz.
4. **Android Auto → Başlatıcıyı özelleştir** bölümünde Dem TV listeleniyorsa işaretleyin, araç bağlantısını yenileyin.
5. Araç park halindeyken Dem TV’yi açıp TV, YouTube, tarayıcı ve yansıtmayı ayrı ayrı doğrulayın.

**Dem TV listelenmiyorsa:** APK’nın normal yüklenmesi Android Auto kabulü için yeterli olmayabilir. Telefon üreticisi, Android Auto sürümü, sertifika/dağıtım kuralları ve eski projection SDK desteği sonucu etkiler. KingInstaller’in veya yükleme kaynağı taklidinin bu sürümün bir gerekliliği olduğu söylenmez; uygulama bunu yapmaz. Tüm cihazlarda dört işlevi garanti etmek için onaylı dağıtım ve gerçek cihaz/araç testleri gerekir.

## Ekran yansıtma

Android Auto’da **Ekran Yansıtma** düğmesine dokunun. Telefonun kilidini açıp Android ekran paylaşımı isteğini onaylayın. İstek otomatik görünmezse telefondaki Dem TV bildirimine veya uygulama simgesine dokunun. Paylaşımı iptal ederseniz görüntü yakalanmaz. Uygulama içindeki ana sayfa düğmesi paylaşımı bitirir. Banka/DRM uygulamalarının koruduğu ekranlar siyah görünebilir; bu koruma aşılmaz.

## Güncelleme ve hata bildirimi

Uygulama güncelleme denetleyicisi yalnızca kararlı sürümleri önerir. 2.1.5 ön sürümü yukarıdaki doğrudan bağlantıyla elle yüklenir; araç doğrulaması olmadan otomatik dağıtılmaz.

Kapanma varsa **telefon modeli, Android sürümü, Dem TV sürümü ve sorunun telefon ekranında mı Android Auto’da mı olduğu** bilgilerini paylaşın. Uygulama açılabiliyorsa **Ayarlar → Tanılama → Tanılama raporunu dışa aktar** bölümünü kullanın; raporu herkese açık depoya kişisel bilgilerle göndermeyin. Geliştirici için USB hata ayıklama/logcat kaydı, Java hatası ile yerel oynatma çökmesini ayırt etmekte kullanılır.
