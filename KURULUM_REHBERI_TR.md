# Mola 2.2.2 — Android Auto ve KingInstaller kurulum rehberi

Mola 2.2.2 (eski adı Dem TV); sesli karşılama, TV/IPTV, ana sayfada Radyo ve Podcast, direksiyon tuşlarıyla kanal değiştirme, Türk radyoları, YouTube, tarayıcı, Favori Kanallar, kendi web kısayollarınız ve izinli ekran yansıtmayı içerir. 2.1.5 için normal kurulumdan sonra Android Auto’da görünmediği bildirildi. **Telefon kurulumu ile Android Auto’da görünme farklıdır.** Araç ekranında görünmeyen kurulumlar için aşağıdaki KingInstaller yöntemini deneyin. Her telefon/araç kombinasyonunda sonuç garantisi yoktur; sürüm araç doğrulaması bekleyen ön sürüm olarak kalır.

> **ÖNEMLİ:** Önce Mola’yı kurun. Kurulumdan sonra Android Auto’da görünmüyorsa [bu videodaki](https://www.youtube.com/watch?v=CdU0OlLds2k) işlemleri uygulayın (Shizuku kurulum rehberi). Ardından Mola’yı [KingInstaller](https://github.com/fcaronte/KingInstaller/releases/download/2.3/KingInstaller-v2.3.apk) uygulamasıyla Shizuku seçeneği açıkken tekrar yükleyin; Android Auto’da çalışacaktır ❤

## 1. Doğrudan indirme bağlantıları

Mola 2.2.2 APK (güncel):
https://media.githubusercontent.com/media/moris545454/araba-/main/distribution/FermataX-yeni-arayuz-debug.apk

KingInstaller 2.3 APK — geliştiricinin resmî GitHub yayını:
https://github.com/fcaronte/KingInstaller/releases/download/2.3/KingInstaller-v2.3.apk

İki indirme düğmesi ve kısa rehber:
https://dem-tv-indir.chirpypeach128.chatgpt.site

Mola sürüm ayrıntıları:
https://github.com/moris545454/araba-/releases/tag/v2.1.5

KingInstaller sürüm ayrıntıları:
https://github.com/fcaronte/KingInstaller/releases/tag/2.3

Her iki APK’yı telefona indirin. Mola dosyası `FermataX-yeni-arayuz-debug.apk` adıyla görünebilir; kurulan uygulama **Mola**’dir. Instagram indirme başlatmazsa menüsünden harici tarayıcıda açın. Mola için en az Android 9 ve araçta çalışan bir Android Auto bağlantısı gerekir.

## 2. KingInstaller’ı kurun ve yükleme iznini açın

1. İndirilen **KingInstaller-v2.3.apk** dosyasını açın.
2. Android izin isterse **Ayarlar → Uygulamalar → Özel uygulama erişimi → Bilinmeyen uygulamaları yükle** bölümüne girin.
3. Dosyayı açan tarayıcı/dosya yöneticisi için **Bu kaynaktan izin ver** seçeneğini açın; kuruluma dönüp **Yükle** seçeneğini kullanın.
4. KingInstaller’ın Mola’yı kurabilmesi için aynı bölümde **KingInstaller → Bu kaynaktan izin ver** seçeneğini de açın. Tarayıcıya verilen izin KingInstaller’ı kapsamaz.

Telefon üreticisine göre menü adları değişebilir. Ayarlarda “bilinmeyen uygulamalar” araması yapabilirsiniz. Yükleme işi bitince verdiğiniz kurulum izinlerini kapatabilirsiniz; sonraki yüklemede tekrar istenebilir.

## 3. Mola’yı KingInstaller içinden yükleyin

1. **KingInstaller** uygulamasını açın.
2. Dosya seçme düğmesinden **İndirilenler / Downloads** klasörüne gidin; **FermataX-yeni-arayuz-debug.apk** dosyasını seçin. KingInstaller’ın kendi APK’sını seçmeyin.
3. İlk denemede **Shizuku / Root seçenekleri kapalıyken Install / Yükle** düğmesine dokunun. Bu, geliştiricinin önerdiği klasik kurulum yöntemidir.
4. Android’in kurulum/güncelleme ekranını onaylayın. KingInstaller için bilinmeyen uygulama yükleme izni sorulursa verin.
5. Kurulum bitince **Mola**’yi telefonda bir kez açın. Güncelleme ve ekran paylaşımı bildirimlerini almak istiyorsanız bildirim iznini verin.

**Daha eski bir Mola kuruluysa:** 2.1.7 ve sonrası yeni bir imza anahtarıyla derlenir. 2.1.7–2.1.9 (Dem TV) kuruluysa 2.2.2’yi doğrudan üzerine yükleyin. 2.1.6 veya daha eskisi kuruluysa önce onu kaldırın, sonra yeni sürümü kurun. Kaldırma eklediğiniz IPTV kaynaklarını ve ayarları siler; gerekiyorsa bağlantılarını önceden not alın. Bazı cihazlarda üzerine yükleme kurulum kaynağını değiştirmeyebilir; hata veya görünmeme sürerse cihaz bilgileriyle bildirin.

KingInstaller ayrı bir üçüncü taraf yükleyicidir; Mola APK’sının içine eklenmemiştir. Geliştiricisi, yükleme kaynağını Google Play olarak kaydetmenin bazı Android Auto görünürlük kısıtlamalarında yardımcı olduğunu belirtir. Bu, Mola’nın Google Play’den yayımlandığı veya Google tarafından onaylandığı anlamına gelmez. Klasik yöntemin başarısı Android ve üretici politikalarına bağlıdır.

## 4. Android Auto’da bilinmeyen kaynakları açın

1. Telefon ayarlarında **Android Auto** aratın.
2. Sayfanın altındaki **Sürüm** bilgisine yaklaşık **10 kez** dokunun; geliştirici ayarlarını etkinleştirmeyi onaylayın.
3. **⋮ → Geliştirici ayarları → Bilinmeyen kaynaklar / Unknown sources** seçeneği varsa açın.
4. **Android Auto → Başlatıcıyı özelleştir** bölümünde Mola listeleniyorsa işaretleyin.
5. Araç bağlantısını kesip yeniden bağlanın. Gerekirse telefonu yeniden başlatın.
6. Araç park halindeyken **Mola**’yi açıp TV, YouTube, tarayıcı ve yansıtmayı ayrı ayrı kontrol edin.

Telefondaki APK yükleme izni ile Android Auto’daki bilinmeyen kaynaklar farklı ayarlardır. İkisi de bütün cihazlarda görünme garantisi vermez. Ayrı bir “Medya hizmeti”, “FermataX Mirror” veya ScreenOnAuto APK’sı kurmanız gerekmez.

**Hâlâ görünmüyorsa:** Mola’nın telefonda açıldığını, KingInstaller kurulumunun tamamlandığını ve Android Auto başlatıcı listesini kontrol edin. Android Auto ve Android System WebView/Chrome’u güncelleyin. Klasik yöntemin desteklenmediği cihazlarda yalnızca bu adımlar yeterli olmayabilir; [KingInstaller geliştiricisinin uyumluluk açıklamalarını](https://github.com/fcaronte/KingInstaller#readme) inceleyin. Klasik yöntem işe yaramazsa [Shizuku kurulum videosunu](https://www.youtube.com/watch?v=CdU0OlLds2k) izleyip Shizuku’yu kurun, sonra KingInstaller’da Shizuku seçeneğini açarak Mola’yı tekrar yükleyin.

## 5. Ekran yansıtma

Android Auto’da **Mola → Ekran Yansıtma** düğmesine dokunun. Telefonun kilidini açıp Android ekran paylaşımı isteğini onaylayın. İstek otomatik görünmezse telefondaki Mola bildirimine veya uygulama simgesine dokunun. Seçenek sunulursa tüm telefon ekranını paylaşmayı seçin. Paylaşımı iptal ederseniz görüntü yakalanmaz; uygulamadaki **Ana sayfa** düğmesi veya araç bağlantısının kesilmesi paylaşımı bitirir.

Paylaşım görüntü içindir; telefon sesi veya uzaktan dokunmatik kontrol aktarılmaz. Banka/DRM uygulamalarının koruduğu ekranlar siyah görünebilir. Mola, Android Auto’nun harita/bölünmüş ekran düzenini zorla değiştirmez; kendisine ayrılan alanı kullanır.

## 6. Güncelleme ve sorun bildirimi

2.1.4 kapanma bildirimi nedeniyle kararlı listeden çekildi. **2.1.5 ön sürümü elle indirilir; otomatik kararlı güncelleme bildirimi gönderilmez.** Sonraki kararlı sürümler için **Ayarlar → Arayüz → Uygulama güncellemeleri** bölümünü kullanabilirsiniz.

Güncelleme sonrasında Mola Android Auto’dan kaybolursa güncel Mola APK’sını KingInstaller içinden yeniden, mevcut kurulumun üzerine yüklemeyi deneyin. Android Auto bağlantısını yenileyin. Kurulum kaynağının güncellemelerde korunması her cihazda garanti değildir.

Mola’da kamera, mikrofon, konum, erişilebilirlik, diğer uygulamaların üzerinde gösterme ve genel dosya okuma izinleri kaldırılmıştır. KingInstaller’ın yükleme izni ayrıdır. Play Protect engelini aşmak için korumayı kapatmayın; dosyanın kaynağını ve uyarıyı kontrol edin. Bu rehber banka veya Play Protect kabul garantisi değildir.

Sorun bildirirken **telefon modeli, Android sürümü, Android Auto sürümü, Mola sürümü, kurulum yöntemi ve tam hata mesajını** paylaşın. Kapanma varsa uygulama açılabildiğinde **Ayarlar → Tanılama → Tanılama raporunu dışa aktar** bölümünü kullanın; kişisel bilgiler içeren raporları herkese açık yayımlamayın.

## 7. Kendi kanal listenizi ekleme

Varsayılan **Türkiye kanalları** ve **Türk radyoları** listeleri olduğu gibi kalır; yanına istediğiniz kadar liste ekleyebilirsiniz.

1. Ana sayfada **TV**’ye dokunun. Listeleriniz görünür; alttaki işlem çubuğunda **+ (IPTV kaynağı ekle)** düğmesi vardır. Düğme yalnızca bu TV liste ekranında bulunur; bir listenin içindeyken geri gelerek ulaşırsınız.
2. Açılan menüden birini seçin:
   - **Bağlantı ile liste ekle:** Yalnızca M3U bağlantısını (http:// veya https://) yazın. Ad otomatik verilir; aynı liste zaten ekliyse uyarı gösterilir.
   - **M3U oynatma listesi:** Ad, bağlantı/dosya, EPG, logo ve bağlantı ayarlarıyla ayrıntılı ekleme.
   - **Xtream Codes** veya **Stalker portalı:** Sağlayıcınızın verdiği hesap bilgileriyle ekleme.
   - **Varsayılan listeleri geri yükle:** Yalnızca TV veya radyo listesini sildiyseniz görünür.
3. Bir listeye uzun basarak **Yenile**, **Düzenle**, **Sil** ve M3U listelerde **Liste bağlantısını kopyala** seçeneklerini kullanabilirsiniz.

## 8. Ana sayfa: Favori Kanallar ve kendi kısayollarınız

- **Favori Kanallar:** Bir kanala uzun basıp **Favorilere ekle** seçin; kanal ana sayfadaki **Favori Kanallar** rafında görünür. Rafa dokunarak oynatabilirsiniz.
- **Kısayol ekle:** Ana sayfadaki **Kısayol ekle** kartına dokunun → **Yeni web kısayolu** → site bağlantısını ve adını yazın. En fazla 6 kısayol eklenebilir. Aynı karttan **Kaldır: …** seçenekleriyle silinir. Kısayollar uygulama içindeki tarayıcıda açılır.

Kendi eklediğiniz listelerin içeriğinden ve yayın haklarından liste sağlayıcısı sorumludur.

## 9. Direksiyon tuşları

Araç bağlıyken direksiyondaki **ileri / geri** tuşları çalan listede sonraki veya önceki öğeye geçer: TV’de kanal, radyoda istasyon, podcast’te bölüm. Aracınız **kanal yukarı / aşağı** tuşu gönderiyorsa onlar da aynı işi yapar. Atamaları **Ayarlar → Arayüz → Tuş atamaları** bölümünden değiştirebilirsiniz.

## 10. Sesli karşılama

**Ayarlar → Arayüz → Sesli karşılama** bölümünden “Araca bağlanınca sesli karşıla” seçeneğini açın ve isterseniz adınızı yazıp klavyedeki **Bitti** tuşuna basın. Telefon araca bağlandığında Mola saate göre kısa bir karşılama yapar. “Günde yalnızca bir kez” seçilirse gün içindeki sonraki bağlantılarda sessiz kalır. Telefonda Türkçe konuşma sesi yüklü değilse karşılama yapılmaz; Ayarlar → Erişilebilirlik → Metin okuma bölümünden Google’ın Türkçe sesini yükleyebilirsiniz.
