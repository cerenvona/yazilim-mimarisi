# LogFlow

Yazılım Mimarisi dersi kapsamında aşamalı olarak geliştirilen
bir log işleme uygulamasıdır.

## Gereksinimler

- JDK 17 veya üzeri
- Maven
- Hızlı çalıştırma betiği için Unix uyumlu terminal

## Sürümler

- v1: Dosyadan satır okuma ve konsola yazdırma.
- v2: Log satırlarını LogRecord nesnelerine dönüştürme,
  hatalı satırları sayma ve parser testleri.

Önceki sürümler Git etiketleri üzerinden incelenebilir.

## Çalıştırma

Komutlar logflow klasöründe çalıştırılır:

```bash
./logflow data/access-small.log
```

Betik kaynak kodları derler ve uygulamayı başlatır.

Alternatif olarak:

```bash
mkdir -p out
javac -d out src/*.java
java -cp out Main data/access-small.log
```

Mac üzerinde Java 17 seçmek gerekirse:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

## Veri akışı

FileLineSource dosyayı satır satır okur.
ParserStage her geçerli satırı bir LogRecord nesnesine dönüştürür.
ConsoleSink bu nesnenin alanlarını okunabilir biçimde gösterir.

SummarySource, veri akışı tamamlandıktan sonra hatalı satır
sayısını konsola yazar.

Main yalnızca komut satırı argümanını alır, bileşenleri
birbirine bağlar ve pipeline'ı çalıştırır.

## Log biçimi

Parser, Common Log Format satırlarını ve isteğe bağlı
referer/user-agent alanlarını içeren Combined Log Format
satırlarını destekler.

LogRecord alanları:

- timestamp: Instant türünde zaman bilgisi
- clientIp: İstemci IP bilgisi
- method: HTTP metodu
- path: Sorgu parametreleri dahil istek yolu
- status: HTTP durum kodu
- bytes: Yanıt boyutu
- userAgent: Varsa tarayıcı/istemci bilgisi
- attributes: Gelecek aşamalar için genişletme alanı
- raw: Orijinal log satırı

LogRecord bir Java record olarak tanımlanmıştır.
attributes alanı Map.copyOf ile değiştirilemez hale getirilir.

User-agent bulunmadığında boş metin kullanılır.
Yanıt boyutu "-" olduğunda 0 kabul edilir.
Durum kodunun 100–599 aralığında olması beklenir.

Hatalı satırlar çıktı üretmeden atlanır ve sayılır.
Sonraki satırların işlenmesi devam eder.

## Örnek veri

data/access-small.log, 200 adet sentetik log satırı içerir.

Örnek dosyayla yapılan çalıştırmada hatalı satır sayısı 0'dır.

## Testler

JUnit 5 testleri ve JaCoCo kapsam raporu:

```bash
mvn clean verify
```

Testler dosya sistemini kullanmaz. Üretilen kayıtlar,
toplayıcı emitter olarak kullanılan records::add ile
bellekte bir listede tutulur.

ParserStageTest içinde 13 test bulunur:

1. Geçerli satırın alanlarını doğrulama
2. Eksik alan
3. Hatalı zaman damgası
4. Metin biçiminde hatalı durum kodu
5. Boş satır
6. Fazladan boşluklar
7. Boşluk içeren tırnaklı user-agent
8. Sorgu parametrelerini koruma
9. Null girdi
10. "-" biçimindeki yanıt boyutu
11. Aralık dışındaki durum kodları
12. long sınırını aşan yanıt boyutu
13. Hatalı satırları sayma ve işlemeye devam etme

## Test kapsamı

JaCoCo raporunda ölçülen sonuçlar:

| Ölçüm | Sonuç |
| --- | --- |
| ParserStage satır kapsamı | %100 — 38/38 satır |
| ParserStage dal kapsamı | %100 |
| LogRecord satır kapsamı | %100 — 3/3 satır |
| Projenin genel satır kapsamı | %37,61 — 41/109 satır |

Otomatik testler parser davranışına odaklanır.
Dosya okuma ve konsol çıktısı örnek dosyayla manuel olarak
kontrol edilmiştir; bu manuel çalıştırma JaCoCo test
kapsamı hesabına dahil değildir.

Raporun konumu:

target/site/jacoco/index.html

Mac üzerinde raporu açmak için:

```bash
open target/site/jacoco/index.html
```

out ve target klasörleri üretilen dosyaları içerir ve
Git tarafından takip edilmez. Kapsam raporu
mvn clean verify komutuyla yeniden oluşturulabilir.