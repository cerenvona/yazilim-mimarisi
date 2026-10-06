# LogFlow v1 Mimarisi

## Bileşenler ve bağlantı

```mermaid
flowchart LR
    A["FileLineSource"] -->|"Emitter ile String satırları"| B["ConsoleSink"]
```

FileLineSource dosyayı satır satır okur.
ConsoleSink kendisine gelen satırı terminale yazdırır.
Pipeline bu bileşenleri birbirine bağlar.

## Sorumlulukların ayrılması

- Dosya okuma: FileLineSource
- Terminale yazdırma: ConsoleSink
- Aşamaları sıralama ve bağlama: Pipeline
- Argümanları okuma ve uygulamayı başlatma: Main

Main içinde dosya okuma veya kayıt işleme mantığı bulunmaz.

## Arayüz ve uygulama farkı

Source, Sink, Stage ve Emitter birer arayüzdür.
Bileşenlerin hangi işlemleri sunacağını tanımlarlar.

FileLineSource ve ConsoleSink ise bu arayüzlerin somut
uygulamalarıdır. İşlemlerin nasıl yapılacağını belirlerler.

Bu sürümde bir kayıt, dosyadan okunan String satırıdır.

## Neden Emitter kullanılıyor?

Bir aşama her girdiden sıfır, bir veya birden fazla çıktı
üretebilir. Emitter, bu çıktıları ayrı ayrı iletmeyi sağlar.
Tek bir değer döndürmek bu esnekliği sağlamaz.

## İşlem hattı

Pipeline, then metodu ile eklenen aşamaları sıralı bir listede
saklar ve veri aktarım bağlantılarını aynı sırayla kurar.

v1 sürümünde ara aşama yoktur. Kaynak doğrudan sink'e bağlanır.
Sonraki sürümde araya ParserStage eklenecektir.

Stage üzerindeki open ve close metotları gelecekteki yaşam
döngüsü işlemleri için ayrılmıştır; v1'de kullanılmaz.