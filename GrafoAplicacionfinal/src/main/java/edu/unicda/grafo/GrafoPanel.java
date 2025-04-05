/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.unicda.grafo;

/**
 *
 * @author Luisv
 */




import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.*;
import java.util.List;

public class GrafoPanel extends JPanel {
    private Grafo grafo;
    private final Map<Integer, Point> posiciones;
    private final Map<Integer, Color> coloresVertices;
    private final Map<String, List<Color>> coloresAristas;
    private final Random random;
    private static final int NODE_SIZE = 30;
    private static final int EDGE_OFFSET = 15;
    private static final int LOOP_RADIUS = 40;
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color NODE_BORDER_COLOR = Color.BLACK;
    private static final Color EDGE_LABEL_COLOR = Color.BLACK;
    private static final BasicStroke EDGE_STROKE = new BasicStroke(2f);
    private static final BasicStroke NODE_STROKE = new BasicStroke(2f);
    private Integer verticeSeleccionado = null;
    private Point offsetArrastre = null;
    private final Color COLOR_SELECCION = new Color(255, 100, 100);
    private double scale = 1.0;
    private final Point translate = new Point(0, 0);
    private Point lastDragPoint; 

    
     public enum AlgoritmoDibujo {
        CIRCULAR, FORCE_DIRECTED, JERARQUICO 
    }
     private AlgoritmoDibujo algoritmoActual = AlgoritmoDibujo.CIRCULAR;
    private final Map<Integer, Point> posicionesManuales = new HashMap<>();

    // 3. Métodos nuevos/modificados:

    // ----- Método para cambiar algoritmo -----
    public void setAlgoritmoDibujo(AlgoritmoDibujo algoritmo) {
        this.algoritmoActual = algoritmo;
        calcularPosiciones();
        repaint();
    }

    public GrafoPanel(Grafo grafo) {
        this.grafo = grafo;
        this.posiciones = new HashMap<>();
        this.coloresVertices = new HashMap<>();
        this.coloresAristas = new HashMap<>();
        this.random = new Random();
        setPreferredSize(new Dimension(800, 600));
        asignarColores();
        addMouseListeners(); 
    }
    // Nuevos métodos
private void addMouseListeners() {
    addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            seleccionarVertice(e.getPoint());
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            verticeSeleccionado = null;
            repaint();
        }
    });

    addMouseMotionListener(new MouseAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (verticeSeleccionado != null) {
                Point nuevaPos = new Point(
                    e.getX() - offsetArrastre.x,
                    e.getY() - offsetArrastre.y
                );
                posicionesManuales.put(verticeSeleccionado, nuevaPos);
                repaint();
            }
        }
    });
}

private void seleccionarVertice(Point click) {
    for (Map.Entry<Integer, Point> entry : posiciones.entrySet()) {
        if (distancia(click, entry.getValue()) <= NODE_SIZE/2) {
            verticeSeleccionado = entry.getKey();
            offsetArrastre = new Point(
                click.x - entry.getValue().x,
                click.y - entry.getValue().y
            );
            return;
        }
    }
    verticeSeleccionado = null;
}

private double distancia(Point a, Point b) {
    return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
}


    public void actualizarGrafo(Grafo nuevoGrafo) {
        this.grafo = nuevoGrafo;
        calcularPosiciones();
        asignarColores();
        repaint();
    }

  private void calcularPosiciones() {
    posiciones.clear();
    
    switch (algoritmoActual) {
        case CIRCULAR:
            // Algoritmo circular original (ideal para grafos pequeños)
            if (!grafo.getVertices().isEmpty()) {
                int centroX = getPreferredSize().width / 2;
                int centroY = getPreferredSize().height / 2;
                int radio = Math.min(centroX, centroY) - 5;
                double angulo = 2 * Math.PI / grafo.getVertices().size();
                int i = 0;
                
                for (int vertice : grafo.getVertices()) {
                    // Respeta posiciones manuales si existen
                    if (posicionesManuales.containsKey(vertice)) {
                        posiciones.put(vertice, posicionesManuales.get(vertice));
                    } else {
                        int x = centroX + (int)(radio * Math.cos(i * angulo));
                        int y = centroY + (int)(radio * Math.sin(i * angulo));
                        posiciones.put(vertice, new Point(x, y));
                    }
                    i++;
                }
            }
            break;
            
        case FORCE_DIRECTED:
            // Versión simplificada (para grafos medianos)
            int padding = 10;
            int anchoDisponible = getWidth() - 2 * padding;
            int altoDisponible = getHeight() - 2 * padding;
            
            for (int vertice : grafo.getVertices()) {
                if (!posicionesManuales.containsKey(vertice)) {
                    int x = padding + random.nextInt(anchoDisponible);
                    int y = padding + random.nextInt(altoDisponible);
                    posiciones.put(vertice, new Point(x, y));
                } else {
                    posiciones.put(vertice, posicionesManuales.get(vertice));
                }
            }
            break;
            
        case JERARQUICO:
            // Algoritmo para grafos dirigidos acíclicos (DAGs)
            Map<Integer, Integer> niveles = new HashMap<>();
            Queue<Integer> cola = new LinkedList<>();
            
            // 1. Encontrar raíces (vértices sin aristas entrantes)
            Set<Integer> raices = new HashSet<>(grafo.getVertices());
            for (int v : grafo.getVertices()) {
                for (Grafo.Arista a : grafo.getVecinos(v)) {
                    raices.remove(a.destino);
                }
            }
            
            // 2. Asignar niveles usando BFS
            for (int raiz : raices) {
                niveles.put(raiz, 0);
                cola.add(raiz);
            }
            
            while (!cola.isEmpty()) {
                int actual = cola.poll();
                for (Grafo.Arista arista : grafo.getVecinos(actual)) {
                    if (!niveles.containsKey(arista.destino) || niveles.get(arista.destino) < niveles.get(actual) + 1) {
                        niveles.put(arista.destino, niveles.get(actual) + 1);
                        cola.add(arista.destino);
                    }
                }
            }
            
            // 3. Posicionar por niveles y orden
            int espacioHorizontal = getWidth() / (Collections.max(niveles.values()) + 2);
            Map<Integer, List<Integer>> nodosPorNivel = new HashMap<>();
            
            for (int vertice : grafo.getVertices()) {
                int nivel = niveles.getOrDefault(vertice, 0);
                nodosPorNivel.computeIfAbsent(nivel, k -> new ArrayList<>()).add(vertice);
            }
            
            for (Map.Entry<Integer, List<Integer>> entry : nodosPorNivel.entrySet()) {
                int nivel = entry.getKey();
                List<Integer> nodos = entry.getValue();
                int espacioVertical = getHeight() / (nodos.size() + 1);
                
                for (int i = 0; i < nodos.size(); i++) {
                    int vertice = nodos.get(i);
                    int x = espacioHorizontal * (nivel + 1);
                    int y = espacioVertical * (i + 1);
                    posiciones.put(vertice, new Point(x, y));
                }
            }
            break;
    }
    }

    private void asignarColores() {
        coloresVertices.clear();
        for (int vertice : grafo.getVertices()) {
            coloresVertices.put(vertice, generarColorAleatorio());
        }
        
        coloresAristas.clear();
        for (int origen : grafo.getVertices()) {
            for (Grafo.Arista arista : grafo.getVecinos(origen)) {
                String claveArista = origen + "-" + arista.destino + "-" + arista.id;
                coloresAristas.putIfAbsent(claveArista, new ArrayList<>());
                coloresAristas.get(claveArista).add(generarColorAleatorio());
            }
        }
    }

    private Color generarColorAleatorio() {
        return new Color(random.nextInt(206) + 50, 
                        random.nextInt(206) + 50, 
                        random.nextInt(206) + 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
          g2d.translate(translate.x, translate.y);
        g2d.scale(scale, scale);
        
        // Dibujar fondo
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        if (grafo != null && !grafo.getVertices().isEmpty()) {
            calcularPosiciones();
            dibujarAristas(g2d);
            dibujarVertices(g2d);
        }
    }
    
     public void zoomIn() {
        scale *= 1.2;
        repaint();
    }

    public void zoomOut() {
        scale = Math.max(0.5, scale / 1.2); // Límite mínimo de zoom
        repaint();
    }

    public void resetZoom() {
        scale = 1.0;
        translate.setLocation(0, 0);
        repaint();
    }

    private void dibujarAristas(Graphics2D g2d) {
        // Agrupar aristas por origen y destino
        Map<String, List<Grafo.Arista>> aristasAgrupadas = new HashMap<>();
        
        for (int origen : grafo.getVertices()) {
            for (Grafo.Arista arista : grafo.getVecinos(origen)) {
                String clave = origen + "-" + arista.destino;
                aristasAgrupadas.computeIfAbsent(clave, k -> new ArrayList<>()).add(arista);
            }
        }
        
        // Dibujar cada grupo de aristas
        for (Map.Entry<String, List<Grafo.Arista>> entry : aristasAgrupadas.entrySet()) {
            String[] partes = entry.getKey().split("-");
            int origen = Integer.parseInt(partes[0]);
            int destino = Integer.parseInt(partes[1]);
            List<Grafo.Arista> aristas = entry.getValue();
            
            Point p1 = posiciones.get(origen);
            
            if (origen == destino) {
                // Dibujar lazos
                for (int i = 0; i < aristas.size(); i++) {
                    String claveArista = origen + "-" + destino + "-" + aristas.get(i).id;
                    Color color = coloresAristas.get(claveArista).get(0);
                    g2d.setColor(color);
                    dibujarLazo(g2d, p1, i, aristas.get(i).arist, aristas.size());
                }
            } else {
                // Dibujar aristas normales
                Point p2 = posiciones.get(destino);
                for (int i = 0; i < aristas.size(); i++) {
                    String claveArista = origen + "-" + destino + "-" + aristas.get(i).id;
                    Color color = coloresAristas.get(claveArista).get(0);
                    g2d.setColor(color);
                    dibujarAristaCurva(g2d, p1, p2, i, aristas.get(i).arist, aristas.size());
                }
            }
        }
    }

    private void dibujarLazo(Graphics2D g2d, Point p, int indiceLazo, int peso, int totalLazos) {
        double angle = Math.PI / 4;
        double offsetAngle = (indiceLazo * Math.PI/8) - ((totalLazos-1) * Math.PI/16);
        int x = p.x;
        int y = p.y;
        int radius = LOOP_RADIUS + indiceLazo * 5;
        
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x + radius * Math.cos(angle + offsetAngle), 
                   y + radius * Math.sin(angle + offsetAngle));
        
        path.curveTo(
            x + radius * 1.5 * Math.cos(angle + offsetAngle + Math.PI/4), 
            y + radius * 1.5 * Math.sin(angle + offsetAngle + Math.PI/4),
            x + radius * 1.5 * Math.cos(angle + offsetAngle + 3*Math.PI/4), 
            y + radius * 1.5 * Math.sin(angle + offsetAngle + 3*Math.PI/4),
            x + radius * Math.cos(angle + offsetAngle + Math.PI), 
            y + radius * Math.sin(angle + offsetAngle + Math.PI)
        );
        
        path.curveTo(
            x + radius * 1.5 * Math.cos(angle + offsetAngle + 5*Math.PI/4), 
            y + radius * 1.5 * Math.sin(angle + offsetAngle + 5*Math.PI/4),
            x + radius * 1.5 * Math.cos(angle + offsetAngle + 7*Math.PI/4), 
            y + radius * 1.5 * Math.sin(angle + offsetAngle + 7*Math.PI/4),
            x + radius * Math.cos(angle + offsetAngle), 
            y + radius * Math.sin(angle + offsetAngle)
        );
        
        g2d.setStroke(EDGE_STROKE);
        g2d.draw(path);
        
        // Dibujar peso
        g2d.setColor(EDGE_LABEL_COLOR);
        FontMetrics fm = g2d.getFontMetrics();
        String pesoStr = String.valueOf(peso);
        int tx = (int)(x + (radius + 10) * Math.cos(angle + offsetAngle + Math.PI/2));
        int ty = (int)(y + (radius + 10) * Math.sin(angle + offsetAngle + Math.PI/2));
        g2d.drawString(pesoStr, tx - fm.stringWidth(pesoStr)/2, ty);
    }

    private void dibujarAristaCurva(Graphics2D g2d, Point p1, Point p2, int indiceArista, int peso, int totalAristas) {
    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    double distancia = Math.sqrt(dx*dx + dy*dy);
    
    // Ajuste para curvas más pronunciadas
    double factorCurvatura = 0.002; // Aumentamos de 0.3 a 0.4 (valor original)
    double offsetBase = 30 + (totalAristas * 5); // Offset base dinámico
    double offset = (indiceArista * offsetBase) - ((totalAristas - 1) * offsetBase/2);
    
    // Punto de control más alejado para mayor curvatura
    double cx = (p1.x + p2.x) / 2.0 - (dy * factorCurvatura * offset);
    double cy = (p1.y + p2.y) / 2.0 + (dx * factorCurvatura * offset);
    
    // Grosor de línea variable
    g2d.setStroke(new BasicStroke(1.5f + (indiceArista * 0.3f)));
    
    // Dibujar curva Bézier cuadrática
    Path2D.Double path = new Path2D.Double();
    path.moveTo(p1.x, p1.y);
    path.quadTo(cx, cy, p2.x, p2.y);
    g2d.draw(path);
    
    // Flecha para dirección (opcional)
    if (totalAristas > 1) {
        dibujarFlecha(g2d, cx, cy, p2.x, p2.y);
    }
    
    // Etiqueta de peso desplazada
    g2d.setColor(EDGE_LABEL_COLOR);
    FontMetrics fm = g2d.getFontMetrics();
    String pesoStr = String.valueOf(peso);
    int labelX = (int)(cx - fm.stringWidth(pesoStr)/2 - (dy/distancia * 10));
    int labelY = (int)(cy + fm.getAscent()/2 + (dx/distancia * 10));
    g2d.drawString(pesoStr, labelX, labelY);
}

// Método auxiliar para dibujar flechas
private void dibujarFlecha(Graphics2D g2d, double x1, double y1, double x2, double y2) {
    double angle = Math.atan2(y2 - y1, x2 - x1);
    int arrowSize = 8;
    
    Path2D.Double arrow = new Path2D.Double();
    arrow.moveTo(x2, y2);
    arrow.lineTo(x2 - arrowSize * Math.cos(angle - Math.PI/6), 
                y2 - arrowSize * Math.sin(angle - Math.PI/6));
    arrow.lineTo(x2 - arrowSize * Math.cos(angle + Math.PI/6), 
                y2 - arrowSize * Math.sin(angle + Math.PI/6));
    arrow.closePath();
    
    g2d.fill(arrow);
}

    private void dibujarVertices(Graphics2D g2d) {
   for (Map.Entry<Integer, Point> entry : posiciones.entrySet()) {
            int vertice = entry.getKey();
            Point p = entry.getValue();
            
            // Relleno del nodo
            g2d.setColor(coloresVertices.get(vertice));
            g2d.fillOval(p.x - NODE_SIZE/2, p.y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            
            // Borde del nodo
            g2d.setColor(NODE_BORDER_COLOR);
            g2d.setStroke(NODE_STROKE);
            g2d.drawOval(p.x - NODE_SIZE/2, p.y - NODE_SIZE/2, NODE_SIZE, NODE_SIZE);
            
            // Etiqueta del nodo
            g2d.setColor(NODE_BORDER_COLOR);
            FontMetrics fm = g2d.getFontMetrics();
            String label = String.valueOf(vertice);
            g2d.drawString(label, p.x - fm.stringWidth(label)/2, p.y + fm.getAscent()/2 - 2);
        }
    }
}