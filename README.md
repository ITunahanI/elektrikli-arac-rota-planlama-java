# Elektrikli Araç Şarj ve Rota Planlama Sistemi (Java)

Bu proje, Nesneye Yönelik Programlama (OOP) dersi kapsamında **Java** diliyle geliştirilmiş olup; elektrikli araçların batarya kapasitesi, menzili, şarj istasyonları ve maliyet/süre parametrelerine göre en uygun rotayı belirleyen konsol tabanlı bir uygulamadır.

## Özellikler ve Mimari
* **Nesneye Yönelik Programlama (OOP):** Sınıf/Nesne yapıları, Kalıtım (`Vehicle` -> `ElectricCar`), Soyutlama (`abstract class Vehicle` ve `interface RouteStrategy`), Çok Biçimlilik (`RouteStrategy` uygulamaları) ve Kapsülleme (`private double consumptionPerKm` ve getter/setter metotları) eksiksiz uygulanmıştır.
* **SOLID Prensipleri:**
  * *Single Responsibility (SRP):* `Station` ve `Graph` sınıfları yalnızca kendi verilerini yönetir.
  * *Open/Closed (OCP):* Yeni bir rota hesaplama yöntemi eklendiğinde mevcut kodlar değiştirilmeden `RouteStrategy` arayüzünden (`implements`) türetilebilir.
  * *Liskov Substitution (LSP):* `RoutePlanner`, tüm `RouteStrategy` alt sınıflarıyla sorunsuz çalışır.
  * *Dependency Inversion (DIP):* `RoutePlanner` somut sınıflara değil, soyut `RouteStrategy` arayüzüne bağımlıdır.
* **Tasarım Kalıpları:** `Singleton` (`DataManager` sınıfında tekil veri yönetimi) ve `Strategy` (`RouteStrategy` ile dinamik algoritma değişimi) kalıpları kullanılmıştır.
* **Algoritma:** Graf veri yapısı üzerinde araç menzilini (`getRange()`) dikkate alan **Dijkstra En Kısa Yol Algoritması** (`DijkstraRouteStrategy`) entegre edilmiştir.
* **Veri Kalıcılığı:** Eklenen araçlar ve şarj istasyonları `data.txt` dosyasında kalıcı olarak saklanır.

## Kurulum ve Çalıştırma
Projeyi derlemek ve konsol uygulamasını başlatmak için:
```bash
javac Main.java
java Main
```

Birim testlerini (8 adet) derleyip çalıştırmak için (`-ea` assertion parametresi ile):
```bash
javac Main.java MainTest.java
java -ea MainTest
```
