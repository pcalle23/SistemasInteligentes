package paquete.gui;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Interfaz gráfica principal de DiaPredict.
 * Diseño médico profesional: blanco y azul, con icono médico dibujado en Java2D.
 *
 * @author Isa (paquete.gui)
 */
public class VentanaPaciente extends JFrame {

    // --- Paleta de colores médica ---
    private static final Color AZUL_OSCURO  = new Color(26,  82, 118);
    private static final Color AZUL_MEDIO   = new Color(41, 128, 185);
    private static final Color AZUL_CLARO   = new Color(214, 234, 248);
    private static final Color BLANCO       = Color.WHITE;
    private static final Color GRIS_SUAVE   = new Color(245, 247, 250);
    private static final Color TEXTO_OSCURO = new Color(30,  39,  46);
    private static final Color TEXTO_GRIS   = new Color(127, 140, 141);

    // --- Campos del formulario ---
    private JTextField campoNombre;
    private JSpinner   campoEdad;
    private JTextField campoGlucosa;
    private JTextField campoCarbohidratos;
    private JButton    botonEnviar;

    private final AgentController agentePaciente;

    public VentanaPaciente(AgentController agentePaciente) {
        this.agentePaciente = agentePaciente;
        configurarVentana();
        construirUI();
    }

    // -----------------------------------------------------------------------
    // VENTANA
    // -----------------------------------------------------------------------
    private void configurarVentana() {
        setTitle("DiaPredict - Panel de Control Médico");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(GRIS_SUAVE);
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        add(crearCabecera(),   BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
    }

    // -----------------------------------------------------------------------
    // CABECERA con degradado y cruz médica en Java2D
    // -----------------------------------------------------------------------
    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AZUL_OSCURO, getWidth(), getHeight(), AZUL_MEDIO);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        cabecera.setPreferredSize(new Dimension(480, 130));
        cabecera.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        // Icono de cruz médica dibujado con Java2D
        JPanel iconoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int r  = 28;
                // Círculo blanco translúcido de fondo
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                // Cruz médica
                g2.setColor(BLANCO);
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx, cy - 14, cx, cy + 14);
                g2.drawLine(cx - 14, cy, cx + 14, cy);
                // Borde del círculo
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            }
            @Override
            public Dimension getPreferredSize() { return new Dimension(70, 70); }
        };
        iconoPanel.setOpaque(false);

        // Textos de cabecera
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));

        JLabel titulo = new JLabel("DiaPredict");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(BLANCO);

        JLabel subtitulo = new JLabel("Panel de Control Médico");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(200, 220, 240));

        JLabel version = new JLabel("Sistema de Predicción de Diabetes  ·  v1.0");
        version.setFont(new Font("SansSerif", Font.PLAIN, 10));
        version.setForeground(new Color(160, 195, 225));

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(subtitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(version);

        cabecera.add(iconoPanel, BorderLayout.WEST);
        cabecera.add(textos,     BorderLayout.CENTER);
        return cabecera;
    }

    // -----------------------------------------------------------------------
    // FORMULARIO dentro de tarjeta blanca con esquinas redondeadas
    // -----------------------------------------------------------------------
    private JPanel crearFormulario() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(GRIS_SUAVE);
        contenedor.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JPanel tarjeta = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLANCO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(0, 0, 0, 15));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Subtítulo sección
        JLabel seccion = new JLabel("Datos del Paciente");
        seccion.setFont(new Font("SansSerif", Font.BOLD, 13));
        seccion.setForeground(AZUL_OSCURO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        tarjeta.add(seccion, gbc);

        // Separador azul
        JSeparator sep = new JSeparator();
        sep.setForeground(AZUL_CLARO);
        gbc.gridy = 1; gbc.insets = new Insets(2, 0, 14, 0);
        tarjeta.add(sep, gbc);
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridwidth = 1;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.4;
        tarjeta.add(crearEtiqueta("Nombre del paciente"), gbc);
        campoNombre = crearCampoTexto("Ej: María García");
        gbc.gridx = 1; gbc.weightx = 0.6;
        tarjeta.add(campoNombre, gbc);

        // Edad
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.4;
        tarjeta.add(crearEtiqueta("Edad (años)"), gbc);
        campoEdad = new JSpinner(new SpinnerNumberModel(30, 1, 120, 1));
        estilizarSpinner(campoEdad);
        gbc.gridx = 1; gbc.weightx = 0.6;
        tarjeta.add(campoEdad, gbc);

        // Glucosa
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.4;
        tarjeta.add(crearEtiqueta("Glucosa en ayunas (mg/dL)"), gbc);
        campoGlucosa = crearCampoTexto("Ej: 98.5");
        gbc.gridx = 1; gbc.weightx = 0.6;
        tarjeta.add(campoGlucosa, gbc);

        // Carbohidratos
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.4;
        tarjeta.add(crearEtiqueta("Carbohidratos última comida (g)"), gbc);
        campoCarbohidratos = crearCampoTexto("Ej: 45.0");
        gbc.gridx = 1; gbc.weightx = 0.6;
        tarjeta.add(campoCarbohidratos, gbc);

        // Botón
        botonEnviar = crearBoton();
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(22, 0, 0, 0);
        tarjeta.add(botonEnviar, gbc);

        contenedor.add(tarjeta, BorderLayout.CENTER);
        configurarBoton();
        return contenedor;
    }

    // -----------------------------------------------------------------------
    // HELPERS DE ESTILO
    // -----------------------------------------------------------------------
    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXTO_OSCURO);
        return lbl;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField campo = new JTextField(16) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXTO_GRIS);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 8, getHeight() / 2 + 5);
                }
            }
        };
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        campo.setForeground(TEXTO_OSCURO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        campo.setBackground(BLANCO);
        return campo;
    }

    private void estilizarSpinner(JSpinner spinner) {
        spinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
    }

    private JButton crearBoton() {
        JButton btn = new JButton("Enviar para Prediccion de Riesgo") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(AZUL_OSCURO);
                } else if (getModel().isRollover()) {
                    g2.setColor(AZUL_MEDIO.brighter());
                } else {
                    g2.setColor(AZUL_MEDIO);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BLANCO);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(380, 42));
        btn.setForeground(BLANCO);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // -----------------------------------------------------------------------
    // LÓGICA DEL BOTÓN
    // -----------------------------------------------------------------------
    private void configurarBoton() {
        botonEnviar.addActionListener((ActionEvent e) -> {

            String nombre = campoNombre.getText().trim();
            if (nombre.isEmpty()) {
                mostrarError("Por favor, introduce el nombre del paciente.");
                return;
            }

            int edad = (int) campoEdad.getValue();

            double glucosa;
            try {
                glucosa = Double.parseDouble(campoGlucosa.getText().trim().replace(",", "."));
            } catch (NumberFormatException ex) {
                mostrarError("El valor de glucosa no es valido.\nUsa formato numerico (ej: 98.5).");
                return;
            }

            double carbohidratos;
            try {
                carbohidratos = Double.parseDouble(campoCarbohidratos.getText().trim().replace(",", "."));
            } catch (NumberFormatException ex) {
                mostrarError("El valor de carbohidratos no es valido.\nUsa formato numerico (ej: 45.0).");
                return;
            }

            // Se incluye 'this' en [0] para que el AgentePaciente tenga la referencia
            // de la ventana y pueda llamar a mostrarResultado() cuando Weka responda.
            // Orden del array: [0] ventana | [1] nombre | [2] edad | [3] glucosa | [4] carbohidratos
            Object[] datosPaciente = { this, nombre, edad, glucosa, carbohidratos };

            try {
                agentePaciente.putO2AObject(datosPaciente, false);
                botonEnviar.setEnabled(false);
                botonEnviar.setText("Procesando...");
            } catch (StaleProxyException ex) {
                mostrarError("Error al comunicarse con el AgentePaciente.\nComprueba que JADE esta activo.");
                ex.printStackTrace();
            }
        });
    }

    // -----------------------------------------------------------------------
    // MÉTODOS PÚBLICOS PARA EL AGENTE
    // -----------------------------------------------------------------------

    /**
     * Muestra el resultado de la predicción en un dialogo emergente.
     * Debe ser llamado desde AgentePaciente usando SwingUtilities.invokeLater().
     *
     * Ejemplo:
     *   SwingUtilities.invokeLater(() -> ventana.mostrarResultado("Riesgo: ALTO"));
     *
     * @param mensaje Resultado generado por AgentePredictor (Weka)
     */
    public void mostrarResultado(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this, mensaje,
                "Resultado de Prediccion - DiaPredict",
                JOptionPane.INFORMATION_MESSAGE
            );
            botonEnviar.setEnabled(true);
            botonEnviar.setText("Enviar para Prediccion de Riesgo");
        });
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.WARNING_MESSAGE);
    }
}
