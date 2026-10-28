import java.io.*;
import java.util.*;
import java.util.Scanner;

// 1. Sınıf: DataManager (Singleton) - SRP & Veri Kalıcılığı
class DataManager {
    private static DataManager instance;
    public List<Station> stations;
    public List<ElectricCar> vehicles;

    private DataManager() {
        stations = new ArrayList<>();
        vehicles = new ArrayList<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public void addVehicle(ElectricCar vehicle) {
        vehicles.add(vehicle);
    }

    public void saveToFile(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            for (Station s : stations) {
                out.println("STATION;" + s.getName() + ";" + s.getCostPerKwh() + ";" + s.isFastCharge());
            }
            for (ElectricCar v : vehicles) {
                out.println("VEHICLE;" + v.getBrand() + ";" + v.getBatteryCapacity() + ";" + v.getConsumption());
            }
        } catch (IOException e) {
            System.out.println("Dosya kaydetme hatası: " + e.getMessage());
        }
    }

    public boolean loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return false;
        stations.clear();
        vehicles.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals("STATION")) {
                    stations.add(new Station(parts[1], Double.parseDouble(parts[2]), Boolean.parseBoolean(parts[3])));
                } else if (parts[0].equals("VEHICLE")) {
                    vehicles.add(new ElectricCar(parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

// 2. Sınıf: Vehicle (Abstract) - OCP & Soyutlama
abstract class Vehicle {
    protected String brand;
    protected double batteryCapacity;

    public Vehicle(String brand, double batteryCapacity) {
        this.brand = brand;
        this.batteryCapacity = batteryCapacity;
    }

    public String getBrand() { return brand; }
    public double getBatteryCapacity() { return batteryCapacity; }
    public abstract double getRange();
}

// 3. Sınıf: ElectricCar (Inherits Vehicle) - Kalıtım & Kapsülleme
class ElectricCar extends Vehicle {
    private double consumptionPerKm;

    public ElectricCar(String brand, double batteryCapacity, double consumptionPerKm) {
        super(brand, batteryCapacity);
        this.consumptionPerKm = consumptionPerKm;
    }

    @Override
    public double getRange() {
        return batteryCapacity / consumptionPerKm;
    }

    public double getConsumption() {
        return consumptionPerKm;
    }
}

// 4. Sınıf: Station - SRP
class Station {
    private String name;
    private double costPerKwh;
    private boolean isFastCharge;

    public Station(String name, double costPerKwh, boolean isFastCharge) {
        this.name = name;
        this.costPerKwh = costPerKwh;
        this.isFastCharge = isFastCharge;
    }

    public String getName() { return name; }
    public double getCostPerKwh() { return costPerKwh; }
    public boolean isFastCharge() { return isFastCharge; }
}

// 5. Sınıf: RouteStrategy (Interface) - Strategy Pattern, DIP & Çok Biçimlilik
interface RouteStrategy {
    String calculateRoute(String start, String end, Vehicle vehicle, Object data);
}

// 6. Sınıf: CostOptimizedStrategy (Concrete Strategy)
class CostOptimizedStrategy implements RouteStrategy {
    @Override
    @SuppressWarnings("unchecked")
    public String calculateRoute(String start, String end, Vehicle vehicle, Object data) {
        if (data instanceof List) {
            List<Station> stations = (List<Station>) data;
            if (!stations.isEmpty()) {
                Station cheapest = Collections.min(stations, Comparator.comparingDouble(Station::getCostPerKwh));
                return start + " -> " + end + " rotası en düşük maliyetle (" + cheapest.getName() + ": " + cheapest.getCostPerKwh() + " TL/kWh) hesaplandı.";
            }
        }
        return start + " -> " + end + " rotası en düşük maliyetle hesaplandı.";
    }
}

// 7. Sınıf: TimeOptimizedStrategy (Concrete Strategy)
class TimeOptimizedStrategy implements RouteStrategy {
    @Override
    @SuppressWarnings("unchecked")
    public String calculateRoute(String start, String end, Vehicle vehicle, Object data) {
        if (data instanceof List) {
            List<Station> stations = (List<Station>) data;
            List<String> fastNames = new ArrayList<>();
            for (Station s : stations) {
                if (s.isFastCharge()) fastNames.add(s.getName());
            }
            if (!fastNames.isEmpty()) {
                return start + " -> " + end + " rotası en kısa süreyle (Hızlı Şarj: " + String.join(", ", fastNames) + ") hesaplandı.";
            }
        }
        return start + " -> " + end + " rotası en kısa süreyle hesaplandı.";
    }
}

// 8. Sınıf: RoutePlanner (Context) - Liskov Substitution
class RoutePlanner {
    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public String plan(String start, String end, Vehicle vehicle, Object data) {
        return strategy.calculateRoute(start, end, vehicle, data);
    }
}

// 9. Sınıf: Graph - Veri Yapısı
class Graph {
    public static class Edge {
        public String target;
        public double weight;
        public Edge(String target, double weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    public Map<String, List<Edge>> edges = new HashMap<>();

    public void addEdge(String from, String to, double weight) {
        edges.putIfAbsent(from, new ArrayList<>());
        edges.putIfAbsent(to, new ArrayList<>());
        edges.get(from).add(new Edge(to, weight));
        edges.get(to).add(new Edge(from, weight));
    }
}

// 10. Sınıf: DijkstraRouteStrategy - Algoritma & Menzil Kontrolü
class DijkstraRouteStrategy implements RouteStrategy {
    private static class NodeDist implements Comparable<NodeDist> {
        String node;
        double dist;
        NodeDist(String node, double dist) {
            this.node = node;
            this.dist = dist;
        }
        @Override
        public int compareTo(NodeDist other) {
            return Double.compare(this.dist, other.dist);
        }
    }

    @Override
    public String calculateRoute(String start, String end, Vehicle vehicle, Object data) {
        if (!(data instanceof Graph)) return "Geçersiz veri yapısı.";
        Graph graph = (Graph) data;
        if (!graph.edges.containsKey(start)) return "Başlangıç noktası bulunamadı.";

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        for (String node : graph.edges.keySet()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);

        PriorityQueue<NodeDist> pq = new PriorityQueue<>();
        pq.add(new NodeDist(start, 0.0));
        double maxRange = (vehicle != null) ? vehicle.getRange() : Double.POSITIVE_INFINITY;

        while (!pq.isEmpty()) {
            NodeDist current = pq.poll();
            if (current.dist > distances.get(current.node)) continue;

            for (Graph.Edge edge : graph.edges.getOrDefault(current.node, new ArrayList<>())) {
                if (edge.weight > maxRange) continue;
                double newDist = current.dist + edge.weight;
                if (newDist < distances.getOrDefault(edge.target, Double.POSITIVE_INFINITY)) {
                    distances.put(edge.target, newDist);
                    previous.put(edge.target, current.node);
                    pq.add(new NodeDist(edge.target, newDist));
                }
            }
        }

        List<String> path = new ArrayList<>();
        String curr = end;
        while (curr != null) {
            path.add(curr);
            curr = previous.get(curr);
        }
        Collections.reverse(path);

        if (!path.isEmpty() && path.get(0).equals(start)) {
            int val = (int) Math.round(distances.get(end));
            return "En ideal rota: " + String.join(" -> ", path) + " (Değer: " + val + ")";
        }
        return "Geçerli bir rota bulunamadı (Menzil yetersiz veya bağlantı yok).";
    }
}

// Ana Sınıf ve Konsol Uygulaması
public class Main {
    private static void initializeDefaultData(DataManager dm, Graph graph) {
        if (!dm.loadFromFile("data.txt")) {
            dm.addStation(new Station("Edirne Merkez Şarj", 7.5, true));
            dm.addStation(new Station("Kırklareli İstasyon", 6.2, false));
            dm.addStation(new Station("Lüleburgaz Hızlı Şarj", 8.0, true));
            dm.addStation(new Station("Çorlu Enerji", 6.8, true));
            dm.addStation(new Station("İstanbul Avrupa", 8.5, true));

            dm.addVehicle(new ElectricCar("Peugeout 206 Electiric", 88.5, 0.18));
            dm.addVehicle(new ElectricCar("Hyundai Accent Electiric", 75.0, 0.15));
            dm.saveToFile("data.txt");
        }
        graph.addEdge("Edirne", "Kırklareli", 65);
        graph.addEdge("Edirne", "Lüleburgaz", 90);
        graph.addEdge("Kırklareli", "Lüleburgaz", 60);
        graph.addEdge("Lüleburgaz", "Çorlu", 50);
        graph.addEdge("Çorlu", "İstanbul", 110);
        graph.addEdge("Edirne", "İstanbul", 240);
    }

    public static void main(String[] args) {
        DataManager dm = DataManager.getInstance();
        Graph graph = new Graph();
        initializeDefaultData(dm, graph);
        RoutePlanner planner = new RoutePlanner(new DijkstraRouteStrategy());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== ELEKTRİKLİ ARAÇ ŞARJ VE ROTA PLANLAMA ===");
            System.out.println("1. Araçları ve Menzillerini Listele");
            System.out.println("2. Şarj İstasyonlarını Listele");
            System.out.println("3. Yeni Şarj İstasyonu Ekle (Kalıcı Kayıt)");
            System.out.println("4. Yeni Elektrikli Araç Ekle (Kalıcı Kayıt)");
            System.out.println("5. Rota Planla (Dijkstra / Maliyet / Süre)");
            System.out.println("0. Çıkış");
            System.out.print("Seçiminiz: ");
            String secim = scanner.nextLine().trim();

            if (secim.equals("1")) {
                System.out.println("\n--- Kayıtlı Araçlar ---");
                for (int i = 0; i < dm.vehicles.size(); i++) {
                    ElectricCar v = dm.vehicles.get(i);
                    System.out.printf("%d. %s | Kapasite: %.1f kWh | Menzil: %.1f km%n", (i + 1), v.getBrand(), v.getBatteryCapacity(), v.getRange());
                }
            } else if (secim.equals("2")) {
                System.out.println("\n--- Şarj İstasyonları ---");
                for (int i = 0; i < dm.stations.size(); i++) {
                    Station s = dm.stations.get(i);
                    String hiz = s.isFastCharge() ? "Hızlı (DC)" : "Normal (AC)";
                    System.out.printf("%d. %s | Fiyat: %.2f TL/kWh | Tip: %s%n", (i + 1), s.getName(), s.getCostPerKwh(), hiz);
                }
            } else if (secim.equals("3")) {
                System.out.print("İstasyon Adı: ");
                String isim = scanner.nextLine();
                System.out.print("kWh Başına Maliyet (TL): ");
                double fiyat = Double.parseDouble(scanner.nextLine());
                System.out.print("Hızlı Şarj var mı? (E/H): ");
                boolean hizli = scanner.nextLine().trim().equalsIgnoreCase("E");
                dm.addStation(new Station(isim, fiyat, hizli));
                dm.saveToFile("data.txt");
                System.out.println("İstasyon eklendi ve data.txt dosyasına kaydedildi.");
            } else if (secim.equals("4")) {
                System.out.print("Araç Markası/Modeli: ");
                String marka = scanner.nextLine();
                System.out.print("Batarya Kapasitesi (kWh): ");
                double kapasite = Double.parseDouble(scanner.nextLine());
                System.out.print("km Başına Tüketim (kWh - örn 0.18): ");
                double tuketim = Double.parseDouble(scanner.nextLine());
                dm.addVehicle(new ElectricCar(marka, kapasite, tuketim));
                dm.saveToFile("data.txt");
                System.out.println("Araç eklendi ve data.txt dosyasına kaydedildi.");
            } else if (secim.equals("5")) {
                System.out.println("\nHaritadaki Noktalar: " + String.join(", ", graph.edges.keySet()));
                System.out.print("Başlangıç Noktası (örn: Edirne): ");
                String start = scanner.nextLine().trim();
                System.out.print("Varış Noktası (örn: İstanbul): ");
                String end = scanner.nextLine().trim();

                System.out.println("\nStrateji Seçin:");
                System.out.println("1. Dijkstra (Mesafe ve Araç Menzil Kontrolü)");
                System.out.println("2. Maliyet Odaklı Strateji");
                System.out.println("3. Süre Odaklı Strateji (Hızlı Şarj Öncelikli)");
                System.out.print("Seçim (1/2/3): ");
                String stSecim = scanner.nextLine().trim();

                ElectricCar aktifArac = dm.vehicles.isEmpty() ? null : dm.vehicles.get(0);
                System.out.print("Varsayılan araç (" + (aktifArac != null ? aktifArac.getBrand() : "Yok") + ") kullanılsın mı? (E/H): ");
                String stArac = scanner.nextLine().trim();
                if (stArac.equalsIgnoreCase("H") && !dm.vehicles.isEmpty()) {
                    for (int i = 0; i < dm.vehicles.size(); i++) {
                        System.out.printf("%d. %s (Menzil: %.1f km)%n", (i + 1), dm.vehicles.get(i).getBrand(), dm.vehicles.get(i).getRange());
                    }
                    System.out.print("Araç numarası: ");
                    int vIdx = Integer.parseInt(scanner.nextLine()) - 1;
                    if (vIdx >= 0 && vIdx < dm.vehicles.size()) {
                        aktifArac = dm.vehicles.get(vIdx);
                    }
                }

                if (stSecim.equals("1")) {
                    planner.setStrategy(new DijkstraRouteStrategy());
                    System.out.println("\nSonuç: " + planner.plan(start, end, aktifArac, graph));
                } else if (stSecim.equals("2")) {
                    planner.setStrategy(new CostOptimizedStrategy());
                    System.out.println("\nSonuç: " + planner.plan(start, end, aktifArac, dm.stations));
                } else if (stSecim.equals("3")) {
                    planner.setStrategy(new TimeOptimizedStrategy());
                    System.out.println("\nSonuç: " + planner.plan(start, end, aktifArac, dm.stations));
                } else {
                    System.out.println("Geçersiz strateji seçimi.");
                }
            } else if (secim.equals("0")) {
                dm.saveToFile("data.txt");
                System.out.println("Veriler kaydedildi. Çıkış yapılıyor...");
                break;
            } else {
                System.out.println("Geçersiz seçim, tekrar deneyin.");
            }
        }
    }
}
