# LogFlow — v1

Yazılım Mimarisi dersi için geliştirilen işlem hattı (pipeline)
uygulamasının ilk sürümüdür.

## Amaç

Bir metin dosyasını satır satır okuyup pipeline üzerinden
terminale yazdırmak.

Dosya okuma, veri aktarımı ve çıktı üretme sorumlulukları
ayrı bileşenlerde tutulmuştur.

## Gereksinim

JDK 17 veya üzeri.

## Derleme ve çalıştırma

Terminalde logflow klasörüne geçildikten sonra:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Main data/access-small.log
```

## Bileşenler

- Source: Veri kaynağının arayüzü.
- Emitter: Veriyi sonraki bileşene ileten arayüz.
- Stage: Bir işlem aşamasının arayüzü.
- Sink: Çıktıyı tüketen bileşenin arayüzü.
- StageException: İşlem aşamalarında kullanılacak hata türü.
- FileLineSource: Dosyayı okur ve her satırı String olarak iletir.
- ConsoleSink: Gelen satırı terminale yazdırır.
- Pipeline: Kaynağı, sıralı aşamaları ve çıktı bileşenini bağlar.
- Main: Dosya yolunu alır, pipeline'ı kurar ve çalıştırır.

## Örnek veri

data/access-small.log dosyası 200 adet üretilmiş örnek
Common Log Format kaydı içerir.

## v1 davranışı

Bu sürümde ara işlem aşaması yoktur. Dosyadaki her satır
değiştirilmeden terminale yazdırılır.

Stage arayüzündeki open ve close metotları bu sürümde
kullanılmaz.

## Kontrol

Örnek dosya ile uygulama derlenip çalıştırılmıştır.
Kayıtlar terminale yazdırılmış ve program tamamlanmıştır.