import java.util.*;

public class BellmanFord {

    public static boolean tieneCicloNegativo(Grafo grafo, String inicio) {
        Map<String, Double> dist = new HashMap<>();
        
        // Inicializar las distancias a infinito
        for (String nodo : grafo.getNodos()) {
            dist.put(nodo, Double.POSITIVE_INFINITY);
        }
        dist.put(inicio, 0.0);

        int n = grafo.getNodos().size();

        // Relajación de las aristas (n-1) veces
        for (int i = 0; i < n - 1; i++) {
            for (String u : grafo.getNodos()) {
                Map<String, Double> vecinos = grafo.getVecinos(u); // Obtener los vecinos del nodo actual
                for (Map.Entry<String, Double> entrada : vecinos.entrySet()) {
                    String v = entrada.getKey(); // Nodo destino
                    double peso = entrada.getValue(); // Peso de la arista
                    if (dist.get(u) + peso < dist.get(v)) {
                        dist.put(v, dist.get(u) + peso);
                    }
                }
            }
        }

        // Comprobar ciclos negativos
        for (String u : grafo.getNodos()) {
            Map<String, Double> vecinos = grafo.getVecinos(u); // Obtener los vecinos del nodo actual
            for (Map.Entry<String, Double> entrada : vecinos.entrySet()) {
                String v = entrada.getKey(); // Nodo destino
                double peso = entrada.getValue(); // Peso de la arista
                if (dist.get(u) + peso < dist.get(v)) {
                    return true; // Hay ciclo negativo
                }
            }
        }

        return false; // No hay ciclo negativo
    }
}