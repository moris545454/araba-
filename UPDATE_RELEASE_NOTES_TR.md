[Dem TV 2.1.0 APK indir](https://media.githubusercontent.com/media/moris545454/araba-/v2.1.0/distribution/FermataX-yeni-arayuz-debug.apk)

Telefon ve Android Auto için tek universal APK. Önceki kurulumun üzerine güncellenebilir.

- Uygulamanın görünen adı ve simgesi **Dem TV** oldu. Özgün D/oynat simgesi, grafit–fildişi–deniz yeşili renkler, asimetrik yumuşak yüzeyler, dairesel içerik görselleri ve sade menü satırlarıyla ortak arayüz yenilendi.
- Android Auto’da bu APK yalnızca **Dem TV** ana girişini yayınlar. Ayrı medya ve ekran yansıtma girişleri kaldırıldı; oynatma servisi uygulama içinde çalışmaya devam eder.
- Ana ekrana [Dem TV canlı yayın](https://demtv.demiralayboncuk-092.workers.dev/) kısayolu eklendi; uygulamanın web bölümünde açılır.
- **Ekran yansıtma** artık yardım mesajı yerine Dem TV araç ekranının tamamını kullanan görüntüleyiciyi açar. Telefonda Dem TV’yi açıp Android’in ekran paylaşımını onaylayın. Geri veya “Yansıtmayı bitir” ile durdurun. Araç ekranından çıkışta ve bağlantı sonlandığında paylaşım kapatılır.
- Yeni görüntüleme akışı root, ekran üzerine çizme, sistem ayarı değiştirme veya erişilebilirlik izni istemez. Telefonun sesini ya da dokunmatik kontrolünü aktarmak bu akışın parçası değildir. Görüntü en-boy oranı korunarak ayrılan araç alanına sığdırılır.
- Gönderilen ScreenOnAuto ZIP’i kaynak kod/APK içermediğinden kod aktarımı yapılmadı. Belgelenen `MirrorControlActivity / START_MIRROR` API’si eklendi: yerleşik araç ekranı hazır değilse ve ScreenOnAuto telefonda kuruluysa onunla başlatma seçeneği gösterilir. Ayrı kurulu uygulamaların Android Auto simgelerini Dem TV kaldıramaz.
- Türkçe, TV/YouTube önceliği, gizli sol menü ve ayarlardan açma seçeneği, düzenlenebilir/silinebilir varsayılan Türkiye kanal kaynağı ve mevcut medya özellikleri korundu.

**Güncellemeler:** Ayarlar → Arayüz → Uygulama güncellemeleri üzerinden kontrol ve APK kurulumu yapılabilir. Android’in arka plan ve bildirim izinleri açık olduğunda yeni sürüm bildirimi gösterilir; yükleme Android onayıyla tamamlanır.

Paket kimliği ve imza önceki sürümle aynı tutuldu. Eski güncelleme istemcilerinin uyumluluğu için indirme dosyasının teknik adı değişmedi; uygulama adı Dem TV’dir. GitHub Actions bu hesapta kapalı olduğundan APK yerelde derlenip Git LFS üzerinden yayımlanır.

**Doğrulama sınırı:** Fiziksel telefon/araç testi yapılmadı. Android Auto’nun sistem çubukları ve bölünmüş ekran/harita yerleşimi ana ünitenin denetimindedir; uygulama kendisine ayrılan alanı doldurur, harita bölmesini bütün araçlarda zorla kaldırma garantisi verilmez. TV akışlarının tamamı ayrı ayrı denenmedi.
