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
 * Diseño médico profesional: blanco y azul, iconos Java2D en cada campo,
 * barra de estado de agentes y dialogo de resultado personalizado.
 *
 * @author Isa (paquete.gui)
 */
public class VentanaPaciente extends JFrame {

    // --- Paleta de colores ---
    private static final Color AZUL_OSCURO  = new Color(26,  82, 118);
    private static final Color AZUL_MEDIO   = new Color(41, 128, 185);
    private static final Color AZUL_CLARO   = new Color(214, 234, 248);
    private static final Color AZUL_ESTADO  = new Color(20,  67,  96);
    private static final Color BLANCO       = Color.WHITE;
    private static final Color GRIS_SUAVE   = new Color(245, 247, 250);
    private static final Color TEXTO_OSCURO = new Color(30,  39,  46);
    private static final Color TEXTO_GRIS   = new Color(127, 140, 141);
    private static final Color VERDE_OK     = new Color(39, 174,  96);

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
        setSize(480, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(GRIS_SUAVE);
    }

    private void construirUI() {
        setLayout(new BorderLayout());

        // Panel norte: cabecera + barra de estado
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(crearCabecera(),    BorderLayout.NORTH);
        norte.add(crearBarraEstado(), BorderLayout.SOUTH);

        add(norte,           BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
    }

    // -----------------------------------------------------------------------
    // CABECERA con estetoscopio dibujado en Java2D
    // -----------------------------------------------------------------------
    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AZUL_OSCURO);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        cabecera.setPreferredSize(new Dimension(480, 110));
        cabecera.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        // Icono: círculo con estetoscopio dibujado en Java2D
        JPanel iconoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int r  = 30;

                // Círculo gris translúcido
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                // Estetoscopio simplificado: arco + línea + círculo
                g2.setColor(BLANCO);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Auriculares (arco superior)
                g2.drawArc(cx - 10, cy - 18, 20, 14, 0, 180);
                // Tubo bajando
                g2.drawLine(cx - 10, cy - 11, cx - 10, cy + 2);
                g2.drawLine(cx + 10, cy - 11, cx + 10, cy + 2);
                // Curva unión
                g2.drawArc(cx - 10, cy - 2, 20, 14, 180, 180);
                // Línea central hacia el diafragma
                g2.drawLine(cx, cy + 12, cx, cy + 20);
                // Diafragma (círculo)
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(cx - 6, cy + 18, 12, 12);
                g2.setColor(new Color(255, 255, 255, 120));
                g2.fillOval(cx - 6, cy + 18, 12, 12);
            }
            @Override
            public Dimension getPreferredSize() { return new Dimension(76, 76); }
        };
        iconoPanel.setOpaque(false);

        // Textos
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

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
    // BARRA DE ESTADO: punto verde + texto
    // -----------------------------------------------------------------------
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(AZUL_ESTADO);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        barra.setOpaque(false);

        // Punto verde animado (dibujado)
        JPanel punto = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(VERDE_OK);
                g2.fillOval(1, 1, 9, 9);
            }
            @Override
            public Dimension getPreferredSize() { return new Dimension(11, 11); }
        };
        punto.setOpaque(false);

        JLabel lblEstado = new JLabel("Agentes JADE conectados y listos");
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblEstado.setForeground(new Color(180, 220, 200));

        barra.add(punto);
        barra.add(lblEstado);
        return barra;
    }

    // -----------------------------------------------------------------------
    // FORMULARIO con iconos Java2D en cada campo
    // -----------------------------------------------------------------------
    private JPanel crearFormulario() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(GRIS_SUAVE);
        contenedor.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JPanel tarjeta = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BLANCO);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(0, 0, 0, 12));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Título sección con icono de persona
        JPanel tituloSeccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        tituloSeccion.setOpaque(false);
        JPanel iconoPersona = crearIcono("persona");
        JLabel lblSeccion = new JLabel("Datos del Paciente");
        lblSeccion.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSeccion.setForeground(AZUL_OSCURO);
        tituloSeccion.add(iconoPersona);
        tituloSeccion.add(lblSeccion);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        tarjeta.add(tituloSeccion, gbc);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(AZUL_CLARO);
        gbc.gridy = 1; gbc.insets = new Insets(2, 0, 14, 0);
        tarjeta.add(sep, gbc);
        gbc.insets = new Insets(7, 0, 7, 0);
        gbc.gridwidth = 1;

        // --- Nombre ---
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.42;
        tarjeta.add(crearEtiquetaConIcono("Nombre", "id"), gbc);
        campoNombre = crearCampoTexto("Ej: María García");
        gbc.gridx = 1; gbc.weightx = 0.58;
        tarjeta.add(campoNombre, gbc);

        // --- Edad ---
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.42;
        tarjeta.add(crearEtiquetaConIcono("Edad (años)", "calendario"), gbc);
        campoEdad = new JSpinner(new SpinnerNumberModel(30, 1, 120, 1));
        estilizarSpinner(campoEdad);
        gbc.gridx = 1; gbc.weightx = 0.58;
        tarjeta.add(campoEdad, gbc);

        // --- Glucosa ---
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.42;
        tarjeta.add(crearEtiquetaConIcono("Glucosa (mg/dL)", "gota"), gbc);
        campoGlucosa = crearCampoTexto("Ej: 98.5");
        gbc.gridx = 1; gbc.weightx = 0.58;
        tarjeta.add(campoGlucosa, gbc);

        // --- Carbohidratos ---
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.42;
        tarjeta.add(crearEtiquetaConIcono("Carbohidratos (g)", "grafica"), gbc);
        campoCarbohidratos = crearCampoTexto("Ej: 45.0");
        gbc.gridx = 1; gbc.weightx = 0.58;
        tarjeta.add(campoCarbohidratos, gbc);

        // --- Botón ---
        botonEnviar = crearBoton();
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        tarjeta.add(botonEnviar, gbc);

        contenedor.add(tarjeta, BorderLayout.CENTER);
        configurarBoton();
        return contenedor;
    }

    // -----------------------------------------------------------------------
    // HELPERS DE ESTILO
    // -----------------------------------------------------------------------

    /** Crea un mini-panel con un icono Java2D según el tipo */
    private JPanel crearIcono(String tipo) {
        JPanel ic = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AZUL_OSCURO);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                switch (tipo) {
                    case "persona":
                        g2.drawOval(cx - 4, cy - 7, 8, 8);
                        g2.drawArc(cx - 6, cy + 1, 12, 8, 0, 180);
                        break;
                    case "id":
                        g2.drawRoundRect(cx - 6, cy - 5, 12, 10, 2, 2);
                        g2.drawLine(cx - 3, cy - 2, cx + 3, cy - 2);
                        g2.drawLine(cx - 3, cy + 1, cx + 1, cy + 1);
                        break;
                    case "calendario":
                        g2.drawRoundRect(cx - 6, cy - 5, 12, 11, 2, 2);
                        g2.drawLine(cx - 3, cy - 7, cx - 3, cy - 4);
                        g2.drawLine(cx + 3, cy - 7, cx + 3, cy - 4);
                        g2.drawLine(cx - 6, cy - 1, cx + 6, cy - 1);
                        break;
                    case "gota":
                        g2.drawLine(cx, cy - 7, cx, cy - 7);
                        int[] xp = {cx, cx - 5, cx + 5};
                        int[] yp = {cy - 7, cy + 4, cy + 4};
                        g2.drawPolyline(xp, yp, 3);
                        g2.drawArc(cx - 5, cy, 10, 6, 0, -180);
                        break;
                    case "grafica":
                        g2.drawLine(cx - 6, cy + 5, cx - 6, cy - 6);
                        g2.drawLine(cx - 6, cy + 5, cx + 6, cy + 5);
                        g2.drawPolyline(
                            new int[]{cx - 4, cx - 1, cx + 2, cx + 5},
                            new int[]{cy + 2, cy - 3, cy,     cy - 5}, 4);
                        break;
                }
            }
            @Override
            public Dimension getPreferredSize() { return new Dimension(16, 16); }
        };
        ic.setOpaque(false);
        return ic;
    }

    /** Etiqueta con icono a la izquierda */
    private JPanel crearEtiquetaConIcono(String texto, String tipoIcono) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        panel.add(crearIcono(tipoIcono));
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXTO_OSCURO);
        panel.add(lbl);
        return panel;
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

                // Icono de envío (flecha)
                g2.setColor(BLANCO);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = 24, cy = getHeight() / 2;
                g2.drawLine(cx - 6, cy, cx + 6, cy);
                g2.drawLine(cx + 6, cy, cx + 2, cy - 4);
                g2.drawLine(cx + 6, cy, cx + 2, cy + 4);

                // Texto centrado
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(380, 44));
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
            // Orden: [0] ventana | [1] nombre | [2] edad | [3] glucosa | [4] carbohidratos
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
    // RESULTADO: JDialog personalizado rojo/verde
    // -----------------------------------------------------------------------

    /**
     * Muestra el resultado en un JDialog personalizado.
     * Rojo si contiene "ALTO", verde si contiene "BAJO".
     * Llamar desde AgentePaciente con SwingUtilities.invokeLater().
     *
     * Ejemplo:
     *   SwingUtilities.invokeLater(() -> ventana.mostrarResultado("ALTO RIESGO DE DIABETES"));
     */
    public void mostrarResultado(String mensaje) {
        SwingUtilities.invokeLater(() -> {

            boolean esAlto = mensaje.toUpperCase().contains("ALTO");

            Color colorFondo  = esAlto ? new Color(253, 237, 236) : new Color(234, 250, 241);
            Color colorBorde  = esAlto ? new Color(231,  76,  60) : new Color( 39, 174,  96);
            Color colorCirc   = esAlto ? new Color(250, 219, 216) : new Color(213, 245, 227);
            Color colorTitulo = esAlto ? new Color(146,  43,  33) : new Color( 26,  92,  50);
            Color colorTexto  = esAlto ? new Color(192,  57,  43) : new Color( 30, 132,  73);
            String etiqueta   = esAlto ? "RIESGO ALTO" : "RIESGO BAJO";
            String consejo    = esAlto
                ? "Se recomienda consultar con su médico urgentemente."
                : "Sus valores están dentro de los rangos saludables.";
            String recomendaciones = esAlto
                ? "<html><div style=\'text-align:left;width:210px;\'>"
                + "<b>Recomendaciones:</b><br>"
                + "&#8226; Acuda a su médico lo antes posible<br>"
                + "&#8226; Reduzca el consumo de azúcar<br>"
                + "&#8226; Evite comidas procesadas y bebidas azucaradas<br>"
                + "&#8226; Realice ejercicio moderado diario"
                + "</div></html>"
                : "<html><div style=\'text-align:left;width:210px;\'>"
                + "<b>Recomendaciones:</b><br>"
                + "&#8226; Mantenga su dieta y hábitos actuales<br>"
                + "&#8226; Continúe con revisiones periódicas<br>"
                + "&#8226; Realice actividad física regularmente<br>"
                + "&#8226; Mantenga un peso saludable"
                + "</div></html>";

            JDialog dialog = new JDialog(this, "Resultado del Análisis", true);
            dialog.setUndecorated(true);
            dialog.setSize(300, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            // Cabecera azul
            JPanel cabDialog = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(AZUL_OSCURO);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            cabDialog.setPreferredSize(new Dimension(290, 52));
            cabDialog.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            JPanel txtsCab = new JPanel();
            txtsCab.setLayout(new BoxLayout(txtsCab, BoxLayout.Y_AXIS));
            txtsCab.setOpaque(false);
            JLabel lTit = new JLabel("DiaPredict");
            lTit.setFont(new Font("SansSerif", Font.BOLD, 13));
            lTit.setForeground(BLANCO);
            JLabel lSub = new JLabel("Resultado del análisis");
            lSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
            lSub.setForeground(new Color(200, 220, 240));
            txtsCab.add(lTit);
            txtsCab.add(lSub);
            cabDialog.add(txtsCab, BorderLayout.CENTER);

            // Cuerpo
            JPanel cuerpo = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(colorFondo);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
            cuerpo.setBorder(BorderFactory.createEmptyBorder(22, 20, 18, 20));
            cuerpo.setOpaque(false);

            // Círculo con símbolo Java2D
            JPanel circulo = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int cx = getWidth() / 2, cy = getHeight() / 2, r = 28;
                    g2.setColor(colorCirc);
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                    g2.setColor(colorBorde);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                    g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (esAlto) {
                        g2.drawLine(cx, cy - 11, cx, cy + 2);
                        g2.fillOval(cx - 2, cy + 7, 5, 5);
                    } else {
                        g2.drawLine(cx - 10, cy + 1, cx - 2, cy + 10);
                        g2.drawLine(cx - 2, cy + 10, cx + 12, cy - 7);
                    }
                }
                @Override
                public Dimension getPreferredSize() { return new Dimension(72, 72); }
            };
            circulo.setOpaque(false);
            circulo.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblResultado = new JLabel(etiqueta, SwingConstants.CENTER);
            lblResultado.setFont(new Font("SansSerif", Font.BOLD, 15));
            lblResultado.setForeground(colorTitulo);
            lblResultado.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblConsejo = new JLabel(
                "<html><div style='text-align:center;width:200px'>" + consejo + "</div></html>",
                SwingConstants.CENTER);
            lblConsejo.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblConsejo.setForeground(colorTexto);
            lblConsejo.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblRecomendaciones = new JLabel(recomendaciones);
            lblRecomendaciones.setFont(new Font("SansSerif", Font.PLAIN, 11));
            lblRecomendaciones.setForeground(colorTexto);
            lblRecomendaciones.setAlignmentX(Component.CENTER_ALIGNMENT);

            cuerpo.add(circulo);
            cuerpo.add(Box.createVerticalStrut(10));
            cuerpo.add(lblResultado);
            cuerpo.add(Box.createVerticalStrut(6));
            cuerpo.add(lblConsejo);
            cuerpo.add(Box.createVerticalStrut(10));
            cuerpo.add(lblRecomendaciones);

            // Botón Aceptar
            JPanel panelBoton = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(colorBorde);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            panelBoton.setOpaque(false);
            panelBoton.setPreferredSize(new Dimension(290, 42));
            panelBoton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JLabel lblAceptar = new JLabel("Aceptar");
            lblAceptar.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblAceptar.setForeground(BLANCO);
            panelBoton.add(lblAceptar);
            panelBoton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) { dialog.dispose(); }
            });

            dialog.getRootPane().setBorder(BorderFactory.createLineBorder(colorBorde, 1));
            dialog.add(cabDialog,  BorderLayout.NORTH);
            dialog.add(cuerpo,     BorderLayout.CENTER);
            dialog.add(panelBoton, BorderLayout.SOUTH);
            dialog.setVisible(true);

            botonEnviar.setEnabled(true);
            botonEnviar.setText("Enviar para Prediccion de Riesgo");
        });
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.WARNING_MESSAGE);
    }
}