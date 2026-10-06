# Değişiklik Geçmişi

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