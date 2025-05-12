import java.util.Random;
import java.util.List;
import java.util.ArrayList;

class SimuladorFallos extends Thread {
    private Grafo grafo;
    private ColaAlertas alertas;
    private boolean ejecutando = true;

    public SimuladorFallos(Grafo grafo, ColaAlertas alertas) {
        this.grafo = grafo;
        this.alertas = alertas;
    }

    public void detener() {
        ejecutando = false;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (ejecutando) {
            try {
                Thread.sleep(2000); // Simular fallos cada 2 segundos
                // Convertir el conjunto de nodos en una lista
                List<String> nodos = new ArrayList<>(grafo.getNodos());
                String nodo = nodos.get(random.nextInt(nodos.size())); // Seleccionar un nodo aleatorio
                alertas.agregarAlerta("Fallo detectado en el nodo: " + nodo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}