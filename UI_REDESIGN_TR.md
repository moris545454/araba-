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
