## 2.1.3 — Referanstaki kart görselleri

TV, YouTube ve ekran yansıtma kartları kullanıcı referansındaki özgün simge/ekran görsellerini kullanır. Referans dosyası değiştirilmeden nodpi kaynağı olarak saklanır; yalnızca dekoratif bölgeler Drawable ile çizilir. Ana kartlarda başlık ve açıklamalar yerel metin öğeleri, oklar yerel yüzeylerdir; ekranın geri kalanı görüntü olarak taklit edilmez. Görseller yeniden renklendirilmez; mavi TV, kırmızı YouTube, mor yansıtma ve referanstaki radyo/podcast/web/dosya/favori simgeleri korunur. Son içerik görselleri daire yerine yuvarlatılmış dikdörtgenlerde gösterilir; gerçek geçmiş kullanılır.

WindAsist/Dem TV kısayolları, kalıcı ana sayfa eylemi, yansıtma izni, oynatma ve kaynak yönetimi değiştirilmedi. Piksel ölçüleri ekran boyutu, yoğunluk ve yazı boyutu ayarına göre uyarlanır; sistem çubuklarını Android Auto çizer.

## 2.1.2 — Önceki ana sayfa düzeni, cam yüzeyler ve kalıcı ana sayfa

TV, YouTube ve ekran yansıtma üç öncelikli bölüm olarak kalır; altında radyo/podcast ve diğer kompakt kısayollar ile Son içerikler bulunur. WindAsist ve Dem TV bağlantıları üst başlıktan bu kısayol grubuna taşındı. Yeni bir düzen geçişi bu sırayı mevcut kurulumlara da uygular; sonrasındaki kullanıcı düzenlemeleri korunur.

Yüzeylerde yarı saydam renk geçişleri, ince ışıklı kenarlar ve belirgin kumanda odağı kullanılır; cam görünümü gerçek arka plan bulanıklaştırması gerektirmez. Ana sayfa düğmesi ortak kök yerleşiminde bulunduğundan video/WEB tam ekranı veya gizli yan menü tarafından gizlenmez. Düğme araçtaki yansıtma katmanını kapatır, video sunumundan çıkar ve doğrudan ana sayfayı açar. Medya kaynağını silmez veya oynatma motorunu durdurmaz.

# Dem TV 2.1.2 arayüz ve yansıtma

Görünen marka ve launcher/bildirim simgesi Dem TV olarak yenilendi. Grafit, fildişi ve deniz yeşili temel palet; TV, YouTube ve yansıtma için farklı pastel vurgular kullanılır. Ana kısayollar cam görünümündeki yuvarlatılmış yüzeylerde, son içerik görselleri dairesel olarak sunulur. TV kaynakları, medya listeleri, radyo/podcast alt sayfaları, ayarlar, açılır menüler ve işlem alanları ortak görsel sistemden yararlanır. Odak çerçeveleri kumanda kullanımında korunur.

Ana ekrandaki WindAsist ve Dem TV canlı yayın düğmeleri mevcut web eklentisine URL girdisi gönderir; bağlantılar açılırken eklentinin yükleme/hazır olma akışı korunur. Sol kısayol menüsü varsayılan olarak gizlidir ve ayarlardan açılabilir.

Android Auto manifestinde yalnızca CarService ana uygulama girişi yayınlanır. Medya servisi doğrudan uygulama bağlantılarına açıktır; eski MirrorService dışarıya yayınlanmaz. Ana ekrandaki yansıtma eylemi mevcut AutomotiveNavigationController üzerinden MainCarActivity’ye gider ve orada tam alan SurfaceView açar.

Araçtaki düğme telefonda izin etkinliğini doğrudan açmayı dener. Android arka plan etkinliği kısıtları bunu engellerse bildirim/telefon simgesinden aynı istek devam eder. İzin penceresi sırasında host pause isteği iptal etmez. Yeni capture-only izin yolu doğrudan Android MediaProjection onayı ister; eski etkileşimli yansıtmanın özel izinleri ve root dalı bu yolda çalışmaz. Tek onay için tek VirtualDisplay oluşturulur; yüzey boyutu değişiminde resize/setSurface kullanılır. Durdurma, geri, başka bir bölüme gezinme, host destroy ve bağlantı kesilmesinde sanal ekran, capture ve ön plan servisi bırakılır. Bu akış görüntü aktarır; ses veya telefonun dokunmatik kontrolünü sağlamaz.

ScreenOnAuto-releases-main.zip yalnızca dokümantasyon ve web varlıkları içerir. İçinden kod veya APK entegre edildiği iddia edilmez. Belgelenen explicit MirrorControlActivity / START_MIRROR çağrısı kurulu ScreenOnAuto için isteğe bağlı alternatif olarak kullanılır. Kaynak: https://github.com/slzn/ScreenOnAuto-releases/blob/main/docs/tr/control-mirroring-from-another-app.md

Fiziksel cihaz/araç doğrulaması gereklidir. Uygulama kendisine ayrılan alanı doldurur; Android Auto’nun sistem/harita bölünmesini zorla değiştirmez.

---

Önceki sürüm kayıtları:

# FermataX arayüz yenilemesi

Referans tasarıma uygun koyu arka plan, renkli simgeler, yuvarlatılmış kartlar ve ortak odak/menü görünümü uygulandı. Ana ekranda TV, YouTube ve Ekran yansıtma büyük kartlarla öne çıkar. Geniş ekranda diğer kısayollar beşli sırada, dar ekranda daha az sütunda gösterilir. Gerçek oynatma geçmişinden en fazla on içerik yatay Son içerikler rafında görünür; mevcut oynatma kartı bunun altında kalır. Örnek kanal veya video verisi uygulamaya eklenmedi.

Ortak medya listeleri ve ızgaraları; TV kaynakları, radyo, podcast, dosyalar, favoriler, geçmiş ve diğer etkin eklentiler aynı görsel dili kullanır. Açılır menüler, kaynak giriş formları, ayarlar, ilk kurulum, araç çubuğu, yan gezinme ve oynatma çubuğu da yenilendi. Kartlar büyük yazıya göre uzar; seçili/odaklı öğeler belirgin çerçeveyle gösterilir. YouTube ve Web'in uygulama çerçevesi yenilendi; hizmetlerin sağladığı gömülü sayfa içeriği mevcut davranışını korur. Android Auto'nun sistem çubukları uygulamanın kontrolünde değildir.

Mevcut oynatma, kaynak ekleme, gezinme, geri tuşu ve tam ekran akışları korunur. Özellikler etkin modüllere göre görünür. Eski ana ekran sırası bir defa TV/YouTube/yansıtma önceliğine taşınır; sonraki kullanıcı düzenlemeleri korunur. Ekran yansıtma kartı mevcut FermataX Mirror girişine ilişkin rehberi açar. Ekran yakalama için mevcut Android sistem onayı gerekir. Yeni metinler projenin desteklediği bütün dillere eklendi.

GitHub güncellemeleri ve yeni sürüm yayınlama akışı için `GITHUB_UPDATES_TR.md` belgesine bakın.

## Doğrulama ve paket

Mobile/Auto ana uygulama, Web ve TV birim testleri ile Mobile/Auto Android Lint çalıştırıldı. Ayrıntılı sonuçlar dağıtım paketinin doğrulama raporundadır. Universal Auto debug APK derlendi ve imzası doğrulandı; tek APK telefon ve Android Auto kullanımına yöneliktir. Debug imzası dağıtım sürümü imzası değildir.

Fiziksel telefon veya araçta çalışma testi yapılmadı. Önizleme görselleri temsili tasarım çizimleridir, çalışan uygulamanın ekran görüntüleri değildir.

JDK 21 ve Android SDK 36 ile proje kökünde:

```
./gradlew :fermata:testMobileDebugUnitTest :fermata:testAutoDebugUnitTest :web:testMobileDebugUnitTest :tv:testMobileDebugUnitTest
./gradlew :fermata:lintMobileDebug :fermata:lintAutoDebug
./gradlew :fermata:packageAutoDebugUniversalApk
```

Cihazda doğrulanacak akışlar: telefon/Auto ana ekran, büyük yazı ve Türkçe başlıklar, odak ve araç kumandası, kart sıralama/sıfırlama, TV kaynak ekleme/oynatma, YouTube tam ekran/geri, radyo/podcast oynatma, Mirror yakalama onayı ve ekran yönü değişimi.

## 2.0.3 — Türkçe ve ortak işlem alanı

Uygulama bu sürümde bir kez Türkçeye geçirilir; sonraki dil seçimleri korunur. Eski yan kısayol menüsü varsayılan olarak gizlidir. Ayarlar → Arayüz → Sol kısayol menüsünü göster seçeneği telefon, Mirror ve Auto görünümünde aynı tercihi kullanır. Görünür olduğunda önceki düğmeler, sıralama ve gezinme davranışları korunur.

Ortak araç çubuğunun görünümü alttaki yuvarlatılmış işlem alanına taşındı. Mevcut mediatorlar, düğme kimlikleri, geri/filtre/görünüm/kaynak ekleme işlemleri korunur. İçerik üstten başlar; oynatıcı kontrolleri işlem alanının üzerinde gösterilir. Açılır menülerde seçili ve kumandayla odaklanan satırların görünür çerçevesi bulunur.

TV kaynağı https://onureroz.com/indirmeler/turk/index.m3u ilk kaynak listelemesinde bir kez normal M3U kaynağı olarak kaydedilir. Kaynak kaydı ağ isteği gerektirmez; mevcut kaynak yükleme ve önbellek sistemi kullanılır. Silme bir sonraki başlangıçta geri alınmaz. Aynı URL zaten kayıtlıysa ikinci kopyası oluşturulmaz. Başlangıç kaynağı adı “Türkiye kanalları”dır.

Türkçe ses komutları da eklendi: “YouTube aç”, “Radyo aç”, “TV TRT 1 oynat”, “YouTube’da video İstanbul ara”, “sonraki kanal”, “duraklat”, “favorilerimi oynat”. İleri/geri sarma, altyazı ve ses kanalı komutları Türkçe dilbilgisi dosyasını kullanır. Özel oynatıcı işlemleri arama komutu olarak tüketilmez.
