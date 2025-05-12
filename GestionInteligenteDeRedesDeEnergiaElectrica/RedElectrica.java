import java.util.*;
import java.util.concurrent.*;

// Clase principal y única
public class RedElectrica {

    // ---------- Modelos ----------
    static class Nodo {
        String nombre;
        double consumo;

        Nodo(String nombre) {
            this.nombre = nombre;
            this.consumo = 0;
        }
    }

    static class Grafo {
        Map<String, Nodo> nodos = new HashMap<>();
        Map<String, Map<String, Double>> adyacencias = new HashMap<>();

        void agregarNodo(String nombre) {
            nodos.put(nombre, new Nodo(nombre));
            adyacencias.put(nombre, new HashMap<>());
        }

        void agregarArista(String origen, String destino, double peso) {
            adyacencias.get(origen).put(destino, peso);
        }

        void desconectar(String origen, String destino) {
            if (adyacencias.containsKey(origen)) {
                adyacencias.get(origen).remove(destino);
            }
        }

        Map<String, Double> getVecinos(String nodo) {
            return adyacencias.get(nodo);
        }

        Set<String> getNodos() {
            return nodos.keySet();
        }

        Nodo getNodo(String nombre) {
            return nodos.get(nombre);
        }

        Map<String, Map<String, Double>> getAdyacencias() {
            return adyacencias;
        }
    }

    // ---------- Historia 1: Dijkstra ----------
    static class Dijkstra {
        static Map<String, Double> calcularRuta(Grafo grafo, String inicio) {
            Map<String, Double> distancias = new HashMap<>();
            Set<String> visitados = new HashSet<>();
            PriorityQueue<String> cola = new PriorityQueue<>(Comparator.comparing(distancias::get));

            for (String nodo : grafo.getNodos()) distancias.put(nodo, Double.POSITIVE_INFINITY);
            distancias.put(inicio, 0.0);
            cola.add(inicio);

            while (!cola.isEmpty()) {
                String actual = cola.poll();
                if (!visitados.add(actual)) continue;

                for (Map.Entry<String, Double> vecino : grafo.getVecinos(actual).entrySet()) {
                    double nuevaDistancia = distancias.get(actual) + vecino.getValue();
                    if (nuevaDistancia < distancias.get(vecino.getKey())) {
                        distancias.put(vecino.getKey(), nuevaDistancia);
                        cola.add(vecino.getKey());
                    }
                }
            }
            return distancias;
        }
    }

    // ---------- Historia 2: Floyd-Warshall ----------
    static class FloydWarshall {
        static double[][] calcularTodoPar(Grafo grafo, List<String> nodos) {
            int n = nodos.size();
            double[][] dist = new double[n][n];
            for (int i = 0; i < n; i++) Arrays.fill(dist[i], Double.POSITIVE_INFINITY);
            for (int i = 0; i < n; i++) dist[i][i] = 0;

            for (int i = 0; i < n; i++) {
                String u = nodos.get(i);
                for (Map.Entry<String, Double> e : grafo.getVecinos(u).entrySet()) {
                    int j = nodos.indexOf(e.getKey());
                    dist[i][j] = e.getValue();
                }
            }

            for (int k = 0; k < n; k++)
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        if (dist[i][k] + dist[k][j] < dist[i][j])
                            dist[i][j] = dist[i][k] + dist[k][j];

            return dist;
        }
    }

    // ---------- Historia 3: Bellman-Ford ----------
    static class BellmanFord {
        static boolean tieneCicloNegativo(Grafo grafo, String origen) {
            Map<String, Double> dist = new HashMap<>();
            for (String nodo : grafo.getNodos()) dist.put(nodo, Double.POSITIVE_INFINITY);
            dist.put(origen, 0.0);

            for (int i = 0; i < grafo.getNodos().size() - 1; i++) {
                for (String u : grafo.getNodos()) {
                    for (Map.Entry<String, Double> v : grafo.getVecinos(u).entrySet()) {
                        if (dist.get(u) + v.getValue() < dist.get(v.getKey())) {
                            dist.put(v.getKey(), dist.get(u) + v.getValue());
                        }
                    }
                }
            }

            for (String u : grafo.getNodos()) {
                for (Map.Entry<String, Double> v : grafo.getVecinos(u).entrySet()) {
                    if (dist.get(u) + v.getValue() < dist.get(v.getKey())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // ---------- Historia 4: Simulación de fallos ----------
    static class SimuladorFallos extends Thread {
        Grafo grafo;

        SimuladorFallos(Grafo grafo) {
            this.grafo = grafo;
        }

        public void run() {
            long endTime = System.currentTimeMillis() + 90000;
            Random rand = new Random();
            List<String> nodos = new ArrayList<>(grafo.getNodos());

            while (System.currentTimeMillis() < endTime) {
                try {
                    Thread.sleep((rand.nextInt(5) + 3) * 1000);
                    String origen = nodos.get(rand.nextInt(nodos.size()));
                    String destino = nodos.get(rand.nextInt(nodos.size()));
                    if (grafo.getVecinos(origen).containsKey(destino)) {
                        grafo.desconectar(origen, destino);
                        System.out.println("⚠️ Fallo simulado: desconectado " + origen + " -> " + destino);
                        new ReconexionAutomatica(grafo, origen, destino, 5).start();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ---------- Historia 5: Cola de alertas ----------
    static class Alerta {
        String mensaje;
        Alerta(String mensaje) { this.mensaje = mensaje; }
        String getMensaje() { return mensaje; }
    }

    static class ColaAlertas {
        Queue<Alerta> cola = new ConcurrentLinkedQueue<>();
        void agregarAlerta(String mensaje) { cola.add(new Alerta(mensaje)); }
        Alerta procesarAlerta() { return cola.poll(); }
        boolean hayAlertas() { return !cola.isEmpty(); }
    }

    // ---------- Historia 6: Reintentos ----------
    static class ReconexionAutomatica extends Thread {
        Grafo grafo;
        String origen, destino;
        double peso;

        ReconexionAutomatica(Grafo grafo, String origen, String destino, double peso) {
            this.grafo = grafo;
            this.origen = origen;
            this.destino = destino;
            this.peso = peso;
        }

        public void run() {
            int reintentos = 3;
            while (reintentos-- > 0) {
                try {
                    Thread.sleep(4000);
                    grafo.agregarArista(origen, destino, peso);
                    System.out.println("🔁 Reconectado " + origen + " -> " + destino);
                    break;
                } catch (Exception e) {
                    System.out.println("❌ Error al reconectar.");
                }
            }
        }
    }

    // ---------- Historia 7: Lista enlazada ----------
    static class ListaEnlazada {
        class NodoLista {
            Nodo nodo;
            NodoLista siguiente;
            NodoLista(Nodo nodo) { this.nodo = nodo; }
        }

        NodoLista cabeza = null;
        void agregar(Nodo nodo) {
            NodoLista nuevo = new NodoLista(nodo);
            nuevo.siguiente = cabeza;
            cabeza = nuevo;
        }

        void imprimir() {
            NodoLista actual = cabeza;
            while (actual != null) {
                System.out.println("🟢 Nodo activo: " + actual.nodo.nombre);
                actual = actual.siguiente;
            }
        }
    }

    // ---------- Historia 8: Simulación de consumo ----------
    static class SimuladorConsumo extends Thread {
        Grafo grafo;
        SimuladorConsumo(Grafo grafo) { this.grafo = grafo; }

        public void run() {
            Random rand = new Random();
            while (true) {
                try {
                    Thread.sleep(5000);
                    for (String nombre : grafo.getNodos()) {
                        double consumo = rand.nextDouble() * 100;
                        grafo.getNodo(nombre).consumo = consumo;
                        System.out.println("🔄 " + nombre + " consumo actualizado: " + consumo + " kW");
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public static void mostrarMenu() {
        System.out.println("\n===== MENÚ DE GESTIÓN DE RED ELÉCTRICA =====");
        System.out.println("1. Calcular ruta de menor pérdida (Dijkstra)");
        System.out.println("2. Análisis global de red (Floyd-Warshall)");
        System.out.println("3. Detectar ciclos negativos (Bellman-Ford)");
        System.out.println("4. Iniciar simulación de fallos");
        System.out.println("5. Iniciar simulación de consumo");
        System.out.println("6. Ver y procesar alertas");
        System.out.println("7. Ver nodos activos");
        System.out.println("8. Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ---------- Interfaz de usuario ----------

    public static void iniciarMenu(Grafo grafo) {
        Scanner scanner = new Scanner(System.in);
        ColaAlertas alertas = new ColaAlertas();
        ListaEnlazada lista = new ListaEnlazada();
        boolean fallosIniciados = false;
        boolean consumoIniciado = false;

        SimuladorFallos simuladorFallos = new SimuladorFallos(grafo);
        SimuladorConsumo simuladorConsumo = new SimuladorConsumo(grafo);

        for (String nombre : grafo.getNodos())
            lista.agregar(grafo.getNodo(nombre));

        while (true) {
            mostrarMenu();
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    Map<String, Double> distancias = Dijkstra.calcularRuta(grafo, "A");
                    System.out.println("🔍 Ruta mínima desde A:");
                    distancias.forEach((k, v) -> System.out.println(" - A -> " + k + ": " + v));
                    break;

                case "2":
                    List<String> nodos = new ArrayList<>(grafo.getNodos());
                    double[][] fw = FloydWarshall.calcularTodoPar(grafo, nodos);
                    System.out.println("🌐 Floyd-Warshall:");
                    for (int i = 0; i < nodos.size(); i++)
                        for (int j = 0; j < nodos.size(); j++)
                            System.out.println(" - " + nodos.get(i) + " -> " + nodos.get(j) + ": " + fw[i][j]);
                    break;

                case "3":
                    boolean ciclo = BellmanFord.tieneCicloNegativo(grafo, "A");
                    System.out.println("🔎 ¿Ciclo negativo detectado? " + ciclo);
                    break;

                case "4":
                    if (!fallosIniciados) {
                        simuladorFallos.start();
                        fallosIniciados = true;
                        System.out.println("⚠️ Simulación de fallos iniciada.");
                    } else {
                        System.out.println("⚠️ Ya está corriendo la simulación de fallos.");
                    }
                    break;

                case "5":
                    if (!consumoIniciado) {
                        simuladorConsumo.start();
                        consumoIniciado = true;
                        System.out.println("🔁 Simulación de consumo iniciada.");
                    } else {
                        System.out.println("🔁 Simulación de consumo ya está activa.");
                    }
                    break;

                case "6":
                    if (alertas.hayAlertas()) {
                        while (alertas.hayAlertas())
                            System.out.println("🚨 Alerta: " + alertas.procesarAlerta().getMensaje());
                    } else {
                        System.out.println("✅ No hay alertas pendientes.");
                        System.out.print("¿Desea generar una alerta manual? (s/n): ");
                        String resp = scanner.nextLine();
                        if (resp.equalsIgnoreCase("s")) {
                            System.out.print("Ingrese mensaje de alerta: ");
                            String msg = scanner.nextLine();
                            alertas.agregarAlerta(msg);
                            System.out.println("✅ Alerta agregada.");
                        }
                    }
                    break;

                case "7":
                    lista.imprimir();
                    break;

                case "8":
                    System.out.println("👋 Cerrando sistema...");
                    if (consumoIniciado) simuladorConsumo.interrupt();
                    return;

                default:
                    System.out.println("❌ Opción inválida.");
            }
        }
    }

    // ---------- MAIN ----------
    
    public static void main(String[] args) {
         Grafo grafo = new Grafo();
        grafo.agregarNodo("A");
        grafo.agregarNodo("B");
        grafo.agregarNodo("C");
            grafo.agregarArista("A", "B", 4);
            grafo.agregarArista("B", "A", -3);
            grafo.agregarArista("B", "C", 3);
            grafo.agregarArista("A", "C", 10);
    
            iniciarMenu(grafo);
    }
}