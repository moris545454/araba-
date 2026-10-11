# GitHub dağıtımı ve uygulama güncellemeleri

Depo: https://github.com/moris545454/araba-

Dağıtılan tek APK, telefon ve Android Auto için `me.app.fermataX.auto.debug` paketidir. İlk yayımlanan sürüm 2.0.2 (309); güncel sürüm Dem TV 2.1.4 (315). Mevcut arayüz ve diğer özellikler korunur.

Uygulama açılışta ve yaklaşık altı saatlik arka plan aralıklarında GitHub'ın en son yayımlanmış Release kaydını kontrol eder. Android pil/ağ politikaları bu kontrolleri geciktirebilir; bu anlık sunucu push bildirimi değildir. Açılış kontrolleri bir saatlik başarı önbelleği kullanır. Bildirim izni verilmişse daha yüksek sürüm kodu için tek bildirim gösterilir. Ayarlar → Arayüz → Uygulama güncellemeleri ekranından kontrol, bildirim ayarları, otomatik kontrolü kapatma ve İndir ve yükle erişilebilir. Kurulum telefonda Android'in onayıyla yapılır; araç arayüzünde APK kurulumu açılmaz.

APK indirilmeden önce `update-auto-debug.json` okunur; yalnızca bu depodaki tamamlanmış, taslak olmayan ve ön sürüm olmayan Release varlıkları kabul edilir. İndirilen APK'nın SHA-256 özeti, boyutu, paket adı, sürüm kodu ve mevcut kurulumla aynı imzaya sahip olması doğrulanır. Hata veya ağ kesintisi mevcut uygulamayı değiştirmez.

## Mevcut dağıtım

GitHub hesabında Actions devre dışı; doğrudan Release varlığı yükleme bağlantısı da bu ortamın kimlik bilgisini kabul etmiyor. Bu nedenle ilk APK `distribution/FermataX-yeni-arayuz-debug.apk` yolunda Git LFS ile tutulur. Release sayfası tek dosya APK indirme bağlantısını verir. `distribution/update-auto-debug.json` sürüm etiketiyle birlikte sabitlenir. Uygulama önce Release varlıklarını arar; bulunmazsa aynı etiketin depodaki manifestini okur ve yalnızca GitHub'ın bu depoya ait sürüm etiketindeki APK dosyasını indirir.

Actions kapalı olsa da uygulama içi sürüm kontrolü, bildirim ve indirme çalışır. Elle yeni sürüm yayımlarken aynı imzayla APK oluşturun, `python scripts/verify_security_apk.py APK --aapt /SDK/build-tools/36.0.0/aapt` kontrolünü çalıştırın, LFS dosyasını güncelleyin, `python scripts/create_update_manifest.py distribution/FermataX-yeni-arayuz-debug.apk distribution/update-auto-debug.json repository-lfs` çalıştırın ve bütün değişiklikleri aynı sürüm etiketiyle yayımlayın. Sürüm kodunu artırın. Yeni Release taslak/ön sürüm olmamalıdır.

## Otomatik derleme için sonraki sürümü yayımlama

Önce GitHub hesabında Actions etkinleştirilmeli ve aynı imza anahtarı secret olarak kurulmalıdır.


1. Değişikliği yapın. `gradle/libs.versions.toml` içindeki `appVersionCode` değerini artırın; `appVersionName` değerini yeni sürüme taşıyın.
2. `UPDATE_RELEASE_NOTES_TR.md` dosyasına o sürümün değişikliklerini yazın ve commit/push yapın.
3. Sürüm adıyla eşleşen etiketi gönderin: örneğin `git tag v2.1.0` ve `git push origin v2.1.0`.
4. `Publish app update` workflow'u test, Lint ve universal APK derlemesini çalıştırır. Aynı imza korunur. APK ve güncelleme manifesti önce taslak Release'e yüklenir; ikisi de hazır olduğunda yayımlanır. Kontroller başarısızsa sürüm yayımlanmaz.

Workflow aynı etiketi yeniden yazmaz. Yeni bir sürüm için sürüm kodunu da artırmak gerekir. GitHub Actions bölümünden elle de tetiklenebilir.

İmza anahtarı Git'e eklenmez. Otomatik APK derlemesinin çalışması için aynı anahtarın `FERMATAX_DEBUG_KEYSTORE_BASE64` GitHub Actions secret'ına kurulması gerekir. Bu oturumdaki GitHub bağlantısı Actions secret yönetimine yetkili değildir; secret kurulumu henüz yapılmadı. Bu secret veya imza anahtarı değiştirilirse mevcut kullanıcılar uygulamanın üzerine güncelleme kuramaz. Kaynak ZIP imza anahtarı içermez. Bu dağıtım mevcut imzayı ve teknik debug paket adını korur; 2.1.4 APK ve dinamik modüllerinde debuggable=false kullanılır; ileride release paketine geçiş ayrı bir paket/imza geçişi olarak planlanmalıdır.
