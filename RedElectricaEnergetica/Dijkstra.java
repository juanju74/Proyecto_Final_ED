

import java.util.*;

public class Dijkstra {

    public static Map<String, Double> calcularRuta(Grafo grafo, String inicio) {
        Map<String, Double> dist = new HashMap<>(); // Distancias mínimas desde el nodo de inicio
        Map<String, Boolean> visitados = new HashMap<>(); // Nodos visitados
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>(Comparator.comparingDouble(NodoDistancia::getDistancia));

        // Inicializar las distancias a infinito y los nodos como no visitados
        for (String nodo : grafo.getNodos()) {
            dist.put(nodo, Double.POSITIVE_INFINITY);
            visitados.put(nodo, false);
        }
        dist.put(inicio, 0.0); // La distancia al nodo de inicio es 0
        cola.add(new NodoDistancia(inicio, 0.0)); // Agregar el nodo de inicio a la cola de prioridad

        // Algoritmo de Dijkstra
        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            String nodoActual = actual.getNodo();

            if (visitados.get(nodoActual)) {
                continue; // Si ya fue visitado, saltar
            }
            visitados.put(nodoActual, true);

            // Relajar las aristas del nodo actual
            Map<String, Double> vecinos = grafo.getVecinos(nodoActual);
            for (Map.Entry<String, Double> entrada : vecinos.entrySet()) {
                String vecino = entrada.getKey();
                double peso = entrada.getValue();

                if (!visitados.get(vecino) && dist.get(nodoActual) + peso < dist.get(vecino)) {
                    dist.put(vecino, dist.get(nodoActual) + peso);
                    cola.add(new NodoDistancia(vecino, dist.get(vecino)));
                }
            }
        }

        return dist; // Retornar las distancias mínimas desde el nodo de inicio
    }

    // Clase auxiliar para manejar los nodos y sus distancias en la cola de prioridad
    private static class NodoDistancia {
        private String nodo;
        private double distancia;

        public NodoDistancia(String nodo, double distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        public String getNodo() {
            return nodo;
        }

        public double getDistancia() {
            return distancia;
        }
    }
}