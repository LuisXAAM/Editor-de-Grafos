package edu.unicda.grafo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GrafoAplicacionGUI extends JFrame {
    private Grafo grafo;
    private GrafoPanel panelVisualizacion;
    private JTextField txtVertice, txtOrigen, txtDestino, txtPeso;
    private JButton btnAgregarVertice, btnAgregarArista, btnLimpiar;
    private JLabel lblEstado;
    private TablaGrafo tablaGrafo;
    private TablaVecinos tablaVecinos;
     private TablaAristasCompacta tablaAristas;

    public GrafoAplicacionGUI() {
        grafo = new Grafo();
        configurarInterfaz();
    }

    private void configurarInterfaz() {
        setTitle("Editor de Grafos Avanzado");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 240, 240));

        // Panel principal (BorderLayout)
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

        // --- 1. Panel superior (controles + zoom) ---
        JPanel topPanel = new JPanel(new BorderLayout());

        // Panel de controles (agregar vértices/aristas)
        JPanel panelControles = crearPanelControles();
        topPanel.add(panelControles, BorderLayout.CENTER);

        // Panel de zoom
        JPanel zoomPanel = crearPanelZoom();
        topPanel.add(zoomPanel, BorderLayout.SOUTH);

        // --- 2. Panel central (visualización + tablas) ---
        JSplitPane centerPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        centerPanel.setDividerLocation(400);

        // Panel de visualización del grafo
        panelVisualizacion = new GrafoPanel(grafo);
        JScrollPane scrollPane = new JScrollPane(panelVisualizacion);
        centerPanel.setTopComponent(scrollPane);

        
        // --- 3. Panel de tablas HORIZONTALES ---
        JPanel panelTablas = new JPanel(new GridLayout(1, 3, 10, 0)); // 1 fila, 3 columnas, 10px de gap
        
        // Tabla de vecinos (izquierda)
        tablaVecinos = new TablaVecinos();
        JPanel panelVecinos = new JPanel(new BorderLayout());
        panelVecinos.setBorder(BorderFactory.createTitledBorder("Relación de Vecinos"));
        panelVecinos.add(new JScrollPane(tablaVecinos), BorderLayout.CENTER);

        // Tabla de conjuntos (centro)
        tablaGrafo = new TablaGrafo();
        JPanel panelConjuntos = new JPanel(new BorderLayout());
        panelConjuntos.setBorder(BorderFactory.createTitledBorder("Conjuntos del Grafo"));
        panelConjuntos.add(new JScrollPane(tablaGrafo), BorderLayout.CENTER);

        // Tabla de aristas (derecha)
        tablaAristas = new TablaAristasCompacta();
        JPanel panelAristas = new JPanel(new BorderLayout());
        panelAristas.setBorder(BorderFactory.createTitledBorder("Aristas por Vértice"));
        panelAristas.add(new JScrollPane(tablaAristas), BorderLayout.CENTER);

        // Añadir las tablas al panel horizontal
        panelTablas.add(panelVecinos);
        panelTablas.add(panelConjuntos);
        panelTablas.add(panelAristas);

        centerPanel.setBottomComponent(panelTablas);

        // --- 4. Barra de estado ---
        lblEstado = new JLabel(" Listo. Agregue vértices y aristas.");
        lblEstado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblEstado.setBackground(new Color(240, 240, 240));
        lblEstado.setOpaque(true);

        // --- Ensamblar todo ---
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(lblEstado, BorderLayout.SOUTH);

        add(mainPanel);

        // Configuración final
        pack();
        setSize(1200, 800); // Aumenté el tamaño para mejor visualización
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(220, 220, 220));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Sección para agregar vértices
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("AGREGAR VÉRTICE"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(new JLabel("Vértice:"), gbc);

        gbc.gridx = 1;
        txtVertice = new JTextField(10);
        panel.add(txtVertice, gbc);

        gbc.gridx = 2;
        btnAgregarVertice = new JButton("Agregar");
        btnAgregarVertice.addActionListener(this::agregarVertice);
        panel.add(btnAgregarVertice, gbc);

        // Sección para agregar aristas
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("AGREGAR ARISTA"), gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 4;
        panel.add(new JLabel("Origen:"), gbc);

        gbc.gridx = 1;
        txtOrigen = new JTextField(10);
        panel.add(txtOrigen, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Destino:"), gbc);

        gbc.gridx = 3;
        txtDestino = new JTextField(10);
        panel.add(txtDestino, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("ARISTA:"), gbc);

        gbc.gridx = 1;
        txtPeso = new JTextField(10);
        panel.add(txtPeso, gbc);

        gbc.gridx = 2;
        btnAgregarArista = new JButton("Agregar Arista");
        btnAgregarArista.addActionListener(this::agregarArista);
        panel.add(btnAgregarArista, gbc);

        gbc.gridx = 3;
        btnLimpiar = new JButton("Limpiar Grafo");
        btnLimpiar.addActionListener(e -> limpiarGrafo());
        panel.add(btnLimpiar, gbc);

        return panel;
    }

    private JPanel crearPanelZoom() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnZoomIn = new JButton("+");
        btnZoomIn.setFont(new Font("Arial", Font.BOLD, 14));
        btnZoomIn.addActionListener(e -> panelVisualizacion.zoomIn());

        JButton btnZoomOut = new JButton("-");
        btnZoomOut.setFont(new Font("Arial", Font.BOLD, 14));
        btnZoomOut.addActionListener(e -> panelVisualizacion.zoomOut());

        JButton btnResetZoom = new JButton("Reset Zoom");
        btnResetZoom.addActionListener(e -> panelVisualizacion.resetZoom());

        panel.add(btnZoomOut);
        panel.add(btnResetZoom);
        panel.add(btnZoomIn);

        return panel;
    }

    private void agregarVertice(ActionEvent e) {
        try {
            int vertice = Integer.parseInt(txtVertice.getText().trim());
            
            if (grafo.getVertices().contains(vertice)) {
                lblEstado.setText(" El vértice " + vertice + " ya existe.");
                return;
            }
            
            grafo.agregarVertice(vertice);
            actualizarComponentes();
            txtVertice.setText("");
            lblEstado.setText(" Vértice " + vertice + " agregado correctamente.");
        } catch (NumberFormatException ex) {
            mostrarError("Ingrese un número válido para el vértice");
        }
    }

    private void agregarArista(ActionEvent e) {
        try {
            int origen = Integer.parseInt(txtOrigen.getText().trim());
            int destino = Integer.parseInt(txtDestino.getText().trim());
            int peso = txtPeso.getText().trim().isEmpty() ? 1 : Integer.parseInt(txtPeso.getText().trim());
            
            if (!grafo.getVertices().contains(origen)) {
                lblEstado.setText(" Error: El vértice origen " + origen + " no existe.");
                return;
            }
            
            if (!grafo.getVertices().contains(destino)) {
                lblEstado.setText(" Error: El vértice destino " + destino + " no existe.");
                return;
            }
            
            grafo.agregarArista(origen, destino, peso);
            actualizarComponentes();
            
            txtOrigen.setText("");
            txtDestino.setText("");
            txtPeso.setText("");
            
            lblEstado.setText(String.format(" Arista agregada: %d → %d (Peso: %d)", origen, destino, peso));
            
        } catch (NumberFormatException ex) {
            mostrarError("Ingrese números válidos para origen, destino y peso");
        }
    }

    private void limpiarGrafo() {
        grafo = new Grafo();
        actualizarComponentes();
        lblEstado.setText(" Grafo reiniciado. Listo para nuevos elementos.");
    }

    private void actualizarComponentes() {
        panelVisualizacion.actualizarGrafo(grafo);
        tablaGrafo.actualizar(grafo);
        tablaVecinos.actualizar(grafo);
        tablaAristas.actualizar(grafo);

    }

    private void mostrarError(String mensaje) {
        lblEstado.setText(" Error: " + mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            GrafoAplicacionGUI app = new GrafoAplicacionGUI();
            app.setVisible(true);
        });
    }
}