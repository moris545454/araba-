# Dem TV — Android Auto kurulum rehberi

Bu rehber Dem TV 2.1.4 ve KingInstaller 2.3 içindir. Telefon ve Android Auto için **tek Dem TV APK** yeterlidir. Kurulumu telefonunuzda, araç park halindeyken yapın.

## 1. İndirme bağlantıları

| Uygulama | Doğrudan APK indirme | Sürüm sayfası |
| --- | --- | --- |
| **Dem TV 2.1.4** | [Dem TV APK indir](https://media.githubusercontent.com/media/moris545454/araba-/v2.1.4/distribution/FermataX-yeni-arayuz-debug.apk) | [Dem TV yayını](https://github.com/moris545454/araba-/releases/tag/v2.1.4) |
| **KingInstaller 2.3** | [KingInstaller APK indir](https://github.com/fcaronte/KingInstaller/releases/download/2.3/KingInstaller-v2.3.apk) | [KingInstaller yayını](https://github.com/fcaronte/KingInstaller/releases/tag/2.3) |

Bağlantılar APK dosyasına gider; tarayıcı indirmeyi onaylamanızı isteyebilir. İkisini de telefonunuza indirin. Önce KingInstaller'ı kurun; Dem TV APK dosyasını sonraki adımda KingInstaller içinden seçin.

Dem TV dosyasının adı `FermataX-yeni-arayuz-debug.apk` olarak görünebilir. Bu, eski güncellemelerle uyumluluk için korunmuş dosya adıdır; kurulan uygulamanın adı **Dem TV** olur. Dosya yaklaşık 168 MiB'dir.

## Güvenlik uyarısı

2.1.4, eski erişilebilirlik/root/Xposed altyapısını ve gereksiz hassas izinleri kaldırır. Bu bir banka veya Play Protect onayı değildir. Play Protect engeli ya da banka uyarısı devam ederse korumayı kapatmayın; bankanın resmî desteğine başvurun. KingInstaller ayrı bir uygulamadır ve cihaz denetimini etkileyebilir. [Değişikliklerin kapsamı ve sınırları](GUVENLIK_NOTLARI_TR.md).

## 2. Gerekenler

- Dem TV için Android 9 veya üzeri bir telefon.
- Android Auto'yu destekleyen araç veya multimedya ünitesi ve çalışan kablolu/kablosuz Android Auto bağlantısı.
- İnternet içerikleri ve indirmeler için internet bağlantısı.

Android sürümünün yeterli olması tek başına Android Auto uyumluluğunu garanti etmez. Telefon üreticisi, Android Auto sürümü ve güvenlik politikaları uygulamanın araç ekranında görünmesini etkileyebilir.

## 3. Telefonda bilinmeyen uygulama yükleme izni

Bu izin, Play Store dışından indirdiğiniz APK dosyasını yüklemek içindir. Android Auto'daki bilinmeyen kaynak ayarından ayrıdır.

1. İndirilen `KingInstaller-v2.3.apk` dosyasını açın.
2. Telefon yüklemeyi engellerse ekrandaki **Ayarlar** düğmesine dokunun.
3. Dosyayı açan tarayıcı veya dosya yöneticisi için **Bu kaynaktan izin ver** seçeneğini açın.
4. Geri dönüp **Yükle** düğmesine dokunun.
5. KingInstaller'ı açın. Dem TV'yi yüklerken KingInstaller için de aynı izin istenirse açın.

İzni elle bulmak için genellikle **Ayarlar → Uygulamalar → Özel uygulama erişimi → Bilinmeyen uygulamaları yükle → KingInstaller → Bu kaynaktan izin ver** yolunu kullanabilirsiniz. Menü adı değişiyorsa telefon ayarlarında “bilinmeyen” sözcüğünü aratın. Android'in bazı eski menülerinde bu ayar “Bilinmeyen kaynaklar” olarak görünür.

İndirme/yükleme işi bittikten sonra tarayıcı ve dosya yöneticisinin bu iznini kapatabilirsiniz. Dem TV güncellemesini kurarken Android izin isterse yüklemeyi başlatan uygulama için tekrar açın.

## 4. Dem TV'yi KingInstaller ile kurma

1. **KingInstaller** uygulamasını açın.
2. Dosya seçme düğmesinden **İndirilenler / Downloads** klasörüne gidin.
3. `FermataX-yeni-arayuz-debug.apk` dosyasını seçin. KingInstaller kendi APK'sını seçmeyin.
4. İlk denemede Shizuku veya Root anahtarlarını açmadan **Install / Yükle** düğmesine dokunun.
5. Android'in yükleme ekranını onaylayın. Dosya erişimi veya bilinmeyen uygulama yükleme izni sorulursa ilgili izni verin.
6. Kurulum bitince **Dem TV** uygulamasını telefonda bir kez açın. Kullanacağınız özelliklerin istediği izinleri verin. Güncelleme bildirimlerini almak için bildirimlere izin verin.

Bu adımlar KingInstaller 2.3 içindir. Eski KingInstaller 1.5 rehberlerindeki “Install as King” düğmesinin adı yeni sürümde farklı olabilir.

Mevcut Dem TV kurulumunu güncelliyorsanız önce üzerine yüklemeyi deneyin. Uygulamayı kaldırmak ayarları, favorileri ve eklenen kaynakları silebilir; sırf yeni APK kurmak için kaldırmanız gerekmez.

## 5. Android Auto'da bilinmeyen kaynakları açma

Bu işlem de **telefonda** yapılır.

1. Telefonun **Ayarlar** uygulamasında **Android Auto** aratıp ayarlarını açın. Bazı telefonlarda yol **Bağlı cihazlar → Bağlantı tercihleri → Android Auto** şeklindedir.
2. Sayfanın altındaki **Sürüm** bölümünü bulun. Gerekirse önce sürüm ayrıntılarını genişletin.
3. Sürüm bilgisine yaklaşık **10 kez** dokunun.
4. **Geliştirici ayarlarını etkinleştir** sorusunu onaylayın.
5. Sağ üstteki **⋮** menüsünden **Geliştirici ayarları** bölümüne girin.
6. **Bilinmeyen kaynaklar / Unknown sources** seçeneği varsa açın.
7. Normal Android Auto ayarlarına dönüp **Başlatıcıyı özelleştir / Customize launcher** bölümünü açın. Listede **Dem TV** varsa işaretli olduğundan emin olun.
8. Android Auto bağlantısını kesip yeniden bağlanın. Gerekirse telefonu yeniden başlatın.

Araç ekranında kullanacağınız uygulama **Dem TV**'dir. Ayrı bir “Medya hizmeti”, “FermataX Mirror” veya ScreenOnAuto uygulaması kurmanız gerekmez. Ayar adları ve seçeneklerin bulunması Android Auto sürümüne göre değişebilir; bilinmeyen kaynakları açmak tek başına görünmeyi garanti etmez.

## 6. Ekran yansıtma nasıl kullanılır?

1. Android Auto'da **Dem TV → Ekran yansıt** düğmesine dokunun.
2. Telefonun kilidini açın. Android'in ekran paylaşımı penceresinde ekranı paylaşmayı onaylayın. Tüm telefon ekranını görmek istiyorsanız, seçenek sunulduğunda tüm ekran paylaşımını seçin.
3. Telefon onay ekranını otomatik açmazsa **Dem TV bildirimi** üzerinden devam edin veya telefondaki **Dem TV** simgesine dokunun.
4. Onaydan sonra telefon görüntüsü Android Auto'daki Dem TV alanında gösterilir.
5. Bitirmek için **Ana sayfa**, **Yansıtmayı bitir** veya geri düğmesini kullanın. Android Auto bağlantısı kesildiğinde de yansıtma sonlanır.

Android ekran paylaşımı onayı her yeni paylaşım oturumunda gerekir; uygulama bunu sizin yerinize kabul edemez. Dem TV'nin yerleşik yansıtması için root, erişilebilirlik veya diğer uygulamaların üzerine çizme izni gerekmez. Yansıtma görüntüyü aktarır; telefon sesi ve araç ekranından telefonu kontrol etme desteği sağlamaz. Korumalı video içerikleri Android tarafından siyah gösterilebilir. Android Auto'nun bölünmüş ekran veya harita düzenini uygulama zorla değiştiremez; görüntü kendisine ayrılan alanı doldurur.

## 7. Güncelleme alma

- Dem TV'de **Ayarlar → Arayüz → Uygulama güncellemeleri** bölümünden yeni sürümü kontrol edebilirsiniz.
- Yeni sürüm bulunduğunda bildirim alabilmek için Dem TV bildirimlerini açık tutun.
- Güncelleme kontrolleri uygulama açılışında ve arka planda yapılır. Android'in pil/arka plan yönetimi kontrolü geciktirebilir; bunlar anlık sunucu bildirimleri değildir.
- Bildirimi veya güncelleme ekranını açın, APK'yı indirin ve **telefonda** yüklemeyi onaylayın. Android bilinmeyen uygulama yükleme izni isterse ekrandaki ayarlardan verin.
- Güncellemeyi mevcut uygulamanın üzerine kurun. Aynı paket ve imzayla yayımlanan güncellemelerde ayarlarınız korunur.

Yukarıdaki doğrudan Dem TV bağlantısı özellikle 2.1.4 sürümüne aittir. Daha yeni sürümleri [GitHub sürümlerinden](https://github.com/moris545454/araba-/releases/latest) veya uygulama içi güncelleme ekranından alın.

## 8. Sık karşılaşılan sorunlar

| Sorun | Kontrol / çözüm |
| --- | --- |
| APK indirilmedi veya açılmıyor | İndirme tamamlanmış mı kontrol edin. Dosya APK olmalı; GitHub sayfasını dosya olarak kaydetmeyin. Yukarıdaki doğrudan bağlantıdan tekrar indirin ve yeterli depolama alanı olduğundan emin olun. |
| “Bu kaynaktan yüklemeye izin verilmiyor” | APK'yı yüklemeye çalışan uygulamaya izin verin. Tarayıcı için verilen izin KingInstaller'ı kapsamaz. |
| “Uygulama yüklenmedi” veya paket çakışması | Dosyayı tekrar indirin. Eski kurulum farklı imzalı olabilir; hata metnini ve mevcut sürümü kontrol edin. Kaldırmadan önce ayarlarınızı ve kaynak bilgilerinizi yedekleyin. |
| Dem TV telefonda var ama araçta yok | Android Auto geliştirici ayarlarını, başlatıcı listesini ve KingInstaller ile kurduğunuzu kontrol edin. Bağlantıyı kesip tekrar bağlanın. |
| Klasik KingInstaller kurulumu Android Auto'da görünmüyor | [KingInstaller'ın güncel rehberindeki](https://github.com/fcaronte/KingInstaller#readme) isteğe bağlı Shizuku yöntemini inceleyin. Önce Shizuku kurulup çalıştırılmalı ve KingInstaller'a yetki verilmelidir. Sadece anahtarı açmak yeterli değildir; üretici kısıtlamaları devam edebilir. Root ilk kurulumun şartı değildir; Root seçeneği yalnızca zaten root erişimi olan telefonlar içindir. |
| Ekran paylaşımı başlamıyor | Telefonu açıp Dem TV bildirimine veya uygulama simgesine dokunun; Android ekran paylaşımı onayını verin. |
| Video var, ses az | Telefonda video oynarken **Medya sesi** düzeyini ve araç sesini kontrol edin. Ekran yansıtma telefon sesini aktarmaz. |
| Güncelleme bildirimi gelmiyor | Dem TV bildirim iznini kontrol edin; uygulama içinden elle güncelleme arayın. Android arka plan kontrollerini geciktirebilir. |

Sorun devam ederse telefon modeli, Android sürümü, Android Auto sürümü, Dem TV sürümü ve görünen hata mesajıyla birlikte [GitHub'da sorun bildirin](https://github.com/moris545454/araba-/issues). Bu ayarların tamamı uygulanmış olsa bile her telefon/araç kombinasyonunda çalışma garantisi yoktur.

Video ve yansıtma özelliklerini yalnızca araç park halindeyken kullanın.
