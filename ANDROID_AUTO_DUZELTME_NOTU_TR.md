# Android Auto görünürlük düzeltmesi

## Kök neden

Auto varyantındaki `AutoFermataMediaService`, `FermataMediaService` sınıfından türemesine rağmen manifestinde Android Auto’nun medya uygulamasını keşfetmek için kullandığı standart `android.media.browse.MediaBrowserService` action’ı yoktu. `automotive_app_desc.xml` içinde `media` capability’si bulunuyor olsa da Android Auto, bağlanacağı medya tarayıcı servisini bu action üzerinden bulamadığı için uygulama araç ekranında listelenmeyebiliyordu.

`MEDIA_PLAY_FROM_SEARCH` ve `MEDIA_BUTTON` action’ları oynatma/arama davranışı içindir; uygulamanın medya kataloğunda keşfedilmesi için tek başlarına yeterli değildir.

## Uygulanan değişiklik

`fermata/src/auto/AndroidManifest.xml` içindeki mevcut `AutoFermataMediaService` üzerine şu standart action eklendi:

```xml
<action android:name="android.media.browse.MediaBrowserService" />
```

Mevcut playback servisi, Car App/Navigation servisi, projection servisi, uygulama kimliği ve diğer çalışma davranışları değiştirilmedi. İkinci bir medya servisi eklenmedi; böylece Android Auto’da duplicate medya kutucuğu oluşturulmaması hedefi korunuyor.

Buna karşılık regresyon testi, Auto medya servisinin bu action’ı taşımasını kontrol edecek şekilde güncellendi.

## Doğrulama

- Manifest XML parse kontrolü: başarılı.
- Auto manifest sözleşme kontrolü: başarılı.
- Auto medya servisinde bulunan action’lar:
  - `android.media.browse.MediaBrowserService`
  - `android.media.action.MEDIA_PLAY_FROM_SEARCH`
  - `android.intent.action.MEDIA_BUTTON`
- Projection entry sayısı: 1.
- Tam Gradle testi bu sandbox’ta çalıştırılamadı; ortamda Android SDK (`ANDROID_HOME`/`sdk.dir`) kurulu değil.

## Kurulumdan sonra

Yeni APK derlenip telefona kurulduktan sonra Android Auto’yu tamamen kapatıp yeniden bağlayın. Gerekirse Android Auto ayarlarında **Başlatıcıyı özelleştir** listesini yenileyin. Uygulamanın görünmesi, Android Auto/telefon üreticisinin sürüm ve güvenlik politikalarına da bağlıdır; KingInstaller yalnızca kurulum kaynağıyla ilgili kısımları etkileyebilir.

Resmî Android dokümantasyonu: <https://developer.android.com/training/cars/media/auto>
