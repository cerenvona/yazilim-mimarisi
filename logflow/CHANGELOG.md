# Değişiklik Geçmişi

## v3 — Filtreleme, projeksiyon ve PipelineBuilder

### Eklenenler

- `src/PredicateFilterStage.java`:
  Predicate koşulunu sağlayan LogRecord kayıtlarını ileten,
  yeniden kullanılabilir filtre aşaması.
- `src/ProjectionStage.java`:
  Seçilen alanları belirtilen sırada String çıktıya dönüştüren
  veri minimizasyonu aşaması.
- `src/FileSink.java`:
  Çıktıları UTF-8 dosyasına yazan ve open/close yaşam
  döngüsünü uygulayan sink.
- `src/PipelineBuilder.java`:
  from, via ve to metotlarıyla akıcı pipeline oluşturma.
- `src/CommandLineOptions.java`:
  Girdi, çıktı, filtre ve alan seçeneklerini ayrıştırma.
- `test/PredicateFilterStageTest.java`:
  Status, path-prefix ve timestamp aralığı testleri.
- `test/ProjectionStageTest.java`:
  Alan seçimi, alan sırası ve bilinmeyen alan testleri.
- `test/FileSinkTest.java`:
  Dosya açma, yazma, klasör oluşturma ve kapatma testi.
- `test/PipelineIntegrationTest.java`:
  Üç aşamalı pipeline ve çıktı dosyası entegrasyon testi.
- `test/FilterOrderExperimentTest.java`:
  Filtre sıralarının sonuçlarını ve koşul kontrol sayılarını
  karşılaştıran deney.
- `test/fixtures/pipeline-fixture.log`:
  Entegrasyon testinde kullanılan beş satırlık örnek veri.

### Güncellenenler

- `src/Sink.java`:
  Varsayılan open ve close yaşam döngüsü metotları eklendi.
- `src/Pipeline.java`:
  Aşamaların ve sink'in yaşam döngüsü yönetimi eklendi.
- `src/Main.java`:
  Komut satırı seçeneklerine göre dinamik pipeline kuruluyor.
- `README.md`:
  v3 kullanım komutları, seçenekler ve testler eklendi.
- `ARCHITECTURE.md`:
  Bileşilebilirlik, veri minimizasyonu, yaşam döngüsü ve
  filtre sıralaması deneyi belgelendi.
- `../.gitignore`:
  `logflow/output/` klasörü takip dışına alındı.

### Sıralama deneyi

200 kayıt üzerinde:

- Status → Path: 220 koşul kontrolü
- Path → Status: 311 koşul kontrolü
- Her iki sıralama: aynı 11 çıktı
- Status filtresini önce çalıştırmak: 91 daha az kontrol

### Doğrulama

- Örnek veri üzerinde status filtresi 20 kayıt üretti.
- FileSink çıktı dosyasını oluşturdu ve kapattı.
- 21 otomatik test başarıyla geçti.
- `mvn clean verify` komutu `BUILD SUCCESS` ile tamamlandı.


## v2 — Tipli kayıtlar ve parser testleri

### Eklenenler

- src/LogRecord.java:
  Değiştirilemez log veri modeli ve attributes genişletme alanı.
- src/ParserStage.java:
  Log ayrıştırma, geçerli kayıt üretme ve hatalı satır sayacı.
- src/SummarySource.java:
  İşlem sonunda hatalı satır sayısını gösterme.
- test/ParserStageTest.java:
  Dosya sisteminden bağımsız 13 JUnit 5 testi.
- pom.xml:
  Java 17, Maven, JUnit 5 ve JaCoCo yapılandırması.
- CHANGELOG.md:
  Sürümlere ait değişiklik listesi.

### Güncellenenler

- src/Main.java:
  ParserStage ve SummarySource bağlantısı eklendi.
- src/ConsoleSink.java:
  String yerine LogRecord tüketip alanlarını gösteriyor.
- README.md:
  v2 kullanımı, test komutları ve ölçülen kapsam sonuçları.
- ARCHITECTURE.md:
  Yeni bileşenler, veri modeli ve hata davranışı.
- ../.gitignore:
  logflow/target/ klasörü takip dışına alındı.

### Doğrulama

- Örnek 200 satırlık dosya işlendi; hatalı satır sayısı 0.
- mvn clean verify başarıyla tamamlandı.
- ParserStage satır kapsamı: %100 (38/38).
- ParserStage dal kapsamı: %100.
- Genel satır kapsamı: %37,61 (41/109).

## v1 — Temel pipeline

- Source, Stage, Emitter ve Sink sözleşmeleri oluşturuldu.
- StageException eklendi.
- FileLineSource, ConsoleSink, Pipeline ve Main oluşturuldu.
- 200 satırlık sentetik örnek dosya eklendi.
- Çalıştırma betiği, README ve mimari belgesi eklendi.