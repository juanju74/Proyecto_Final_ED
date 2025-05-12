

import java.util.*;

public class FloydWarshall {

    public static double[][] calcularTodoPar(Grafo grafo, List<String> nodos) {
        int n = nodos.size();
        double[][] dist = new double[n][n];

        // Inicializar la matriz de distancias
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            dist[i][i] = 0; // La distancia de un nodo a sí mismo es 0
        }

        // Rellenar la matriz con las distancias iniciales basadas en las aristas del grafo
        for (int i = 0; i < n; i++) {
            String u = nodos.get(i);
            Map<String, Double> vecinos = grafo.getVecinos(u); // Obtener los vecinos del nodo actual
            for (Map.Entry<String, Double> entrada : vecinos.entrySet()) {
                String v = entrada.getKey(); // Nodo destino
                double peso = entrada.getValue(); // Peso de la arista
                int j = nodos.indexOf(v); // Índice del nodo destino en la lista de nodos
                if (j != -1) {
                    dist[i][j] = peso;
                }
            }
        }

        // Aplicar el algoritmo de Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Double.POSITIVE_INFINITY && dist[k][j] != Double.POSITIVE_INFINITY) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }

        return dist; // Retornar la matriz de distancias mínimas
    }
}