# LogFlow

LogFlow, Yazılım Mimarisi dersi kapsamında aşamalı olarak
geliştirilen bir log işleme uygulamasıdır.

Uygulama log dosyasını okur, satırları tipli kayıtlara
dönüştürür, yapılandırılan filtreleri uygular, seçilen alanları
çıkarır ve sonucu yeni bir dosyaya yazar.

## Gereksinimler

- JDK 17 veya üzeri
- Maven
- Unix uyumlu terminal

## Sürümler

- v1: Dosyadan satır okuma ve temel pipeline
- v2: LogRecord, parser ve parser testleri
- v3: Filtreleme, projeksiyon, FileSink ve PipelineBuilder

Önceki sürümler Git etiketleri üzerinden incelenebilir.

## Veri akışı

```text
FileLineSource
→ ParserStage
→ PredicateFilterStage
→ PredicateFilterStage
→ ProjectionStage
→ FileSink
```

Aşamalar `PipelineBuilder` ile istenen sırada ve sayıda
bir araya getirilebilir.

## Derleme ve çalıştırma

Komutlar `logflow` klasöründe çalıştırılır.

Önce Java 17 seçilmesi gerekirse:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

Uygulamayı çalıştırma örneği:

```bash
./logflow data/access-small.log output/errors.log \
--min-status 400 \
--fields timestamp,clientIp,status,path
```

Hem status hem path filtresi kullanma örneği:

```bash
./logflow data/access-small.log output/api-errors.log \
--min-status 400 \
--path-prefix /api \
--fields timestamp,clientIp,status
```

## Komut satırı seçenekleri

| Değer veya seçenek | Açıklama |
| --- | --- |
| İlk değer | Okunacak log dosyası |
| İkinci değer | Yazılacak çıktı dosyası |
| `--min-status 400` | En düşük HTTP durum kodu |
| `--path-prefix /api` | Yolun başlaması gereken değer |
| `--fields status,path` | Çıktıya yazılacak alanlar |

Girdi ve çıktı dosyaları zorunludur.
Diğer seçenekler isteğe bağlıdır.

`--fields` verilmezse şu alanlar kullanılır:

```text
timestamp,clientIp,status,path
```

Kullanılabilecek alan adları:

- timestamp
- clientIp
- method
- path
- status
- bytes
- userAgent
- attributes
- raw

## Filtreleme

`PredicateFilterStage`, constructor üzerinden verilen
`Predicate<LogRecord>` koşulunu kullanır.

Örnek koşullar:

```java
log -> log.status() >= 400
log -> log.path().startsWith("/api")
```

Zaman aralığı filtresi de aynı aşamayla oluşturulabilir:

```java
log -> !log.timestamp().isBefore(start)
       && log.timestamp().isBefore(end)
```

Koşulu sağlamayan kayıtlar sonraki aşamaya iletilmez.

## Projeksiyon ve veri minimizasyonu

`ProjectionStage`, yalnızca seçilen alanları çıktıya ekler.

Örneğin:

```text
--fields clientIp,status,path
```

kullanıldığında çıktı şu biçimdedir:

```text
clientIp=192.0.2.11,status=404,path=/page/10
```

Seçilmeyen `raw`, `userAgent` ve diğer alanlar çıktı
dosyasına yazılmaz.

## FileSink yaşam döngüsü

FileSink:

1. `open()` ile çıktı dosyasını açar.
2. `consume()` ile her çıktıyı ayrı satıra yazar.
3. `close()` ile writer'ı kapatır.

Pipeline, hata oluşsa bile `close()` çağrısını `finally`
bloğu üzerinden gerçekleştirir. Böylece arabellekte kalan
çıktıların kaybolması önlenir.

## Örnek veri

`data/access-small.log`, 200 sentetik log satırı içerir.

Aşağıdaki komut 20 adet hata kaydı üretir:

```bash
./logflow data/access-small.log output/errors.log \
--min-status 400 \
--fields timestamp,clientIp,status,path
```

Sonucun satır sayısını kontrol etmek için:

```bash
wc -l output/errors.log
```

## Testler

Bütün testleri ve JaCoCo raporunu oluşturmak için:

```bash
mvn clean verify
```

Projede toplam 21 otomatik test bulunur.

Test edilen başlıca davranışlar:

- Geçerli ve hatalı log satırlarını ayrıştırma
- Status filtresi
- Path-prefix filtresi
- Timestamp aralığı filtresi
- Projeksiyon ve veri minimizasyonu
- FileSink açma, yazma ve kapatma davranışı
- Fixture üzerinde üç aşamalı entegrasyon
- Filtre sıralamasının sonuç ve işlem sayısına etkisi

Kapsam raporu şu konumda oluşturulur:

```text
target/site/jacoco/index.html
```

Mac üzerinde açmak için:

```bash
open target/site/jacoco/index.html
```

## Filtre sıralaması sonucu

200 satırlık örnek dosyada iki filtre sırası karşılaştırılmıştır.

| Sıralama | Koşul kontrolü | Çıktı |
| --- | ---: | ---: |
| Status → Path | 220 | 11 kayıt |
| Path → Status | 311 | 11 kayıt |

İki sıralama aynı çıktıyı üretmiştir. Daha seçici olan status
filtresinin önce uygulanması 91 daha az koşul kontrolü
gerektirmiştir. Deneyin ayrıntıları `ARCHITECTURE.md`
dosyasında bulunmaktadır.

## Üretilen dosyalar

Aşağıdaki klasörler Git tarafından takip edilmez:

- `out/`
- `target/`
- `output/`

Bu klasörler derleme, test ve çalışma sırasında yeniden
oluşturulabilir.