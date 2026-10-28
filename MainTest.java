public class MainTest {

    // 1. Singleton Testi
    public static void testDataManagerSingleton() {
        DataManager manager1 = DataManager.getInstance();
        DataManager manager2 = DataManager.getInstance();
        assert manager1 == manager2 : "Singleton testi başarısız!";
    }

    // 2. Araç Menzil Testi (Kapsülleme ve hesaplama)
    public static void testElectricCarRange() {
        ElectricCar car = new ElectricCar("Tesla", 100.0, 0.2);
        assert Math.abs(car.getRange() - 500.0) < 0.001 : "Menzil testi başarısız!";
    }

    // 3. İstasyon Oluşturma Testi
    public static void testStationCreation() {
        Station station = new Station("Istasyon A", 5.5, true);
        assert station.getName().equals("Istasyon A") : "İstasyon adı hatalı!";
        assert station.isFastCharge() : "Hızlı şarj durumu hatalı!";
    }

    // 4. Maliyet Optimizasyonu Stratejisi Testi
    public static void testCostStrategy() {
        CostOptimizedStrategy strategy = new CostOptimizedStrategy();
        String result = strategy.calculateRoute("A", "B", null, null);
        assert result.contains("en düşük maliyetle") : "Maliyet stratejisi hatalı!";
    }

    // 5. Süre Optimizasyonu Stratejisi Testi
    public static void testTimeStrategy() {
        TimeOptimizedStrategy strategy = new TimeOptimizedStrategy();
        String result = strategy.calculateRoute("A", "B", null, null);
        assert result.contains("en kısa süreyle") : "Süre stratejisi hatalı!";
    }

    // 6. Route Planner (Context) Strateji Değişimi Testi
    public static void testRoutePlannerStrategySwitch() {
        RoutePlanner planner = new RoutePlanner(new CostOptimizedStrategy());
        String res1 = planner.plan("A", "B", null, null);
        planner.setStrategy(new TimeOptimizedStrategy());
        String res2 = planner.plan("A", "B", null, null);
        assert !res1.equals(res2) : "Strateji değişimi başarısız!";
    }

    // 7. Graf Düğüm/Ayrıt Ekleme Testi
    public static void testGraphAddEdge() {
        Graph graph = new Graph();
        graph.addEdge("A", "B", 10.0);
        assert graph.edges.containsKey("A") : "Graf düğüm ekleme başarısız!";
        assert graph.edges.get("A").get(0).target.equals("B") : "Graf hedef düğüm hatalı!";
    }

    // 8. Dijkstra Algoritması Rota ve Değer Testi
    public static void testDijkstraRouteCalculation() {
        Graph graph = new Graph();
        graph.addEdge("A", "B", 5.0);
        graph.addEdge("B", "C", 10.0);
        graph.addEdge("A", "C", 20.0);

        DijkstraRouteStrategy strategy = new DijkstraRouteStrategy();
        String result = strategy.calculateRoute("A", "C", null, graph);
        assert result.equals("En ideal rota: A -> B -> C (Değer: 15)") : "Dijkstra hesaplama hatası: " + result;
    }

    public static void main(String[] args) {
        testDataManagerSingleton();
        testElectricCarRange();
        testStationCreation();
        testCostStrategy();
        testTimeStrategy();
        testRoutePlannerStrategySwitch();
        testGraphAddEdge();
        testDijkstraRouteCalculation();
        System.out.println("Tebrikler! 8/8 Birim Testi (Unit Test) Başarıyla Geçti.");
    }
}
