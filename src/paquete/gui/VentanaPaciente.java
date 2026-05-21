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
 * Formulario completo con secciones Mañana / Tarde / Noche.
 *
 * @author Isa (paquete.gui)
 */
public class VentanaPaciente extends JFrame {

    // --- Paleta de colores ---
    private static final Color AZUL_OSCURO    = new Color(26,  82, 118);
    private static final Color TURQUESA       = new Color(23, 165, 184);
    private static final Color TURQUESA_CLARO = new Color(178, 224, 232);
    private static final Color BLANCO         = Color.WHITE;
    private static final Color GRIS_SUAVE     = new Color(240, 246, 248);
    private static final Color TEXTO_OSCURO   = new Color(26,  82, 118);
    private static final Color TEXTO_GRIS     = new Color(127, 140, 141);
    private static final Color VERDE_OK       = new Color(175, 240, 212);

    // Colores secciones
    private static final Color SEC_MAÑANA  = new Color(232, 245, 250);
    private static final Color SEC_TARDE   = new Color(225, 245, 240);
    private static final Color SEC_NOCHE   = new Color(235, 232, 250);

    // --- Campos paciente ---
    private JTextField campoNombre;
    private JSpinner   campoEdad;

    // --- Mañana ---
    private JTextField campoGlucosaAyunas;
    private JTextField campoCarbDesayuno;
    private JTextField campoGlucosaPostDesayuno;

    // --- Tarde ---
    private JTextField campoGlucosaComida;
    private JTextField campoCarbComida;
    private JTextField campoGlucosaPostComida;

    // --- Noche ---
    private JTextField campoGlucosaCena;
    private JTextField campoCarbCena;
    private JTextField campoGlucosaPostCena;

    private JButton botonEnviar;
    private final AgentController agentePaciente;

    public VentanaPaciente(AgentController agentePaciente) {
        this.agentePaciente = agentePaciente;
        configurarVentana();
        construirUI();
        javax.swing.ToolTipManager.sharedInstance().setInitialDelay(100);
        javax.swing.ToolTipManager.sharedInstance().setDismissDelay(10000);
        javax.swing.ToolTipManager.sharedInstance().setEnabled(true);
        javax.swing.UIManager.put("ToolTip.font", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        javax.swing.UIManager.put("ToolTip.background", new java.awt.Color(26, 82, 118));
        javax.swing.UIManager.put("ToolTip.foreground", java.awt.Color.WHITE);
        javax.swing.UIManager.put("ToolTip.border", javax.swing.BorderFactory.createLineBorder(new java.awt.Color(23, 165, 184), 1));
    }

    // -----------------------------------------------------------------------
    // VENTANA
    // -----------------------------------------------------------------------
    private void configurarVentana() {
        setTitle("DiaPredict - Panel de Control Médico");
        setSize(900, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(GRIS_SUAVE);
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        JPanel norte = new JPanel(new BorderLayout());
        norte.add(crearCabecera(),    BorderLayout.NORTH);
        norte.add(crearBarraEstado(), BorderLayout.SOUTH);
        add(norte, BorderLayout.NORTH);

        // Formulario con scroll
        JPanel formulario = crearFormulario();
        JScrollPane scroll = new JScrollPane(formulario);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(560, 680));

        // Panel lateral
        JPanel lateral = crearPanelLateral();

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(GRIS_SUAVE);
        centro.add(scroll, BorderLayout.WEST);
        centro.add(lateral, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    // -----------------------------------------------------------------------
    // CABECERA
    // -----------------------------------------------------------------------
    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(210, 230, 240), 0, getHeight(), new Color(234, 246, 250));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Símbolos médicos sutiles
                g2.setColor(new Color(23, 165, 184, 18));
                g2.setFont(new Font("SansSerif", Font.BOLD, 90));
                g2.drawString("+", getWidth() - 80, 85);
                g2.setColor(new Color(26, 82, 118, 15));
                g2.setFont(new Font("SansSerif", Font.BOLD, 65));
                g2.drawString("♥", 14, getHeight() - 8);
                // Línea turquesa abajo
                g2.setColor(TURQUESA);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setPreferredSize(new Dimension(560, 120));
        cabecera.setBorder(BorderFactory.createEmptyBorder(16, 20, 12, 20));

        JLabel logoLabel;
        try {
            java.net.URL urlLogo = getClass().getResource("logo.png");
            if (urlLogo != null) {
                ImageIcon iconoOriginal = new ImageIcon(urlLogo);
                Image imgEscalada = iconoOriginal.getImage().getScaledInstance(260, 72, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(imgEscalada));
            } else {
                logoLabel = new JLabel("DiaPredict");
                logoLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
                logoLabel.setForeground(AZUL_OSCURO);
            }
        } catch (Exception e) {
            logoLabel = new JLabel("DiaPredict");
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
            logoLabel.setForeground(AZUL_OSCURO);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("SISTEMA DE PREDICCIÓN DE DIABETES");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitulo.setForeground(new Color(120, 150, 160));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        cabecera.add(Box.createVerticalGlue());
        cabecera.add(logoLabel);
        cabecera.add(Box.createVerticalStrut(6));
        cabecera.add(subtitulo);
        cabecera.add(Box.createVerticalGlue());
        return cabecera;
    }

    // -----------------------------------------------------------------------
    // BARRA DE ESTADO
    // -----------------------------------------------------------------------
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(TURQUESA);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        barra.setOpaque(false);

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
            @Override public Dimension getPreferredSize() { return new Dimension(11, 11); }
        };
        punto.setOpaque(false);

        JLabel lblEstado = new JLabel("Agentes JADE conectados y listos");
        lblEstado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblEstado.setForeground(BLANCO);

        barra.add(punto);
        barra.add(lblEstado);
        return barra;
    }

    // -----------------------------------------------------------------------
    // FORMULARIO COMPLETO
    // -----------------------------------------------------------------------
    private JPanel crearFormulario() {
        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));
        contenedor.setBackground(GRIS_SUAVE);
        contenedor.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Datos del paciente
        contenedor.add(crearTarjetaPaciente());
        contenedor.add(Box.createVerticalStrut(12));

        // Sección Mañana
        contenedor.add(crearSeccion("Mañana", SEC_MAÑANA, AZUL_OSCURO, new String[][]{
            {"Glucosa en ayunas (mg/dL)", "Ej: 95.0"},
            {"Carbohidratos desayuno (g)", "Ej: 45.0"},
            {"Glucosa post-desayuno (mg/dL)", "Ej: 130.0"}
        }, 0));
        contenedor.add(Box.createVerticalStrut(10));

        // Sección Tarde
        contenedor.add(crearSeccion("Tarde", SEC_MAÑANA, new Color(22, 130, 100), new String[][]{
            {"Glucosa antes comida (mg/dL)", "Ej: 110.0"},
            {"Carbohidratos comida (g)", "Ej: 85.0"},
            {"Glucosa post-comida (mg/dL)", "Ej: 185.0"}
        }, 1));
        contenedor.add(Box.createVerticalStrut(10));

        // Sección Noche
        contenedor.add(crearSeccion("Noche", SEC_MAÑANA, new Color(80, 60, 150), new String[][]{
            {"Glucosa antes cena (mg/dL)", "Ej: 120.0"},
            {"Carbohidratos cena (g)", "Ej: 50.0"},
            {"Glucosa post-cena (mg/dL)", "Ej: 140.0"}
        }, 2));
        contenedor.add(Box.createVerticalStrut(16));

        // Botón
        botonEnviar = crearBoton();
        JPanel panelBoton = new JPanel(new BorderLayout());
        panelBoton.setOpaque(false);
        panelBoton.add(botonEnviar, BorderLayout.CENTER);
        contenedor.add(panelBoton);

        configurarBoton();
        return contenedor;
    }

    // -----------------------------------------------------------------------
    // TARJETA DATOS PACIENTE
    // -----------------------------------------------------------------------
    private JPanel crearTarjetaPaciente() {
        JPanel tarjeta = crearTarjetaBase(BLANCO);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Título
        JPanel tit = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tit.setOpaque(false);
        tit.add(crearIcono("persona"));
        JLabel lblTit = new JLabel("Datos del Paciente");
        lblTit.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTit.setForeground(TURQUESA);
        tit.add(lblTit);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        tarjeta.add(tit, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(TURQUESA_CLARO);
        gbc.gridy = 1; gbc.insets = new Insets(2, 0, 10, 0);
        tarjeta.add(sep, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridwidth = 1;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.4;
        tarjeta.add(crearEtiquetaConIcono("Nombre del paciente", "id"), gbc);
        campoNombre = crearCampoTexto("Ej: María García");
        campoNombre.setToolTipText("Nombre completo del paciente");
        gbc.gridx = 1; gbc.weightx = 0.6;
        tarjeta.add(campoNombre, gbc);

        // Edad
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.4;
        tarjeta.add(crearEtiquetaConIcono("Edad (años)", "calendario"), gbc);
        campoEdad = new JSpinner(new SpinnerNumberModel(30, 1, 120, 1));
        estilizarSpinner(campoEdad);
        gbc.gridx = 1; gbc.weightx = 0.6;
        tarjeta.add(campoEdad, gbc);

        return tarjeta;
    }

    // -----------------------------------------------------------------------
    // SECCIÓN (MAÑANA / TARDE / NOCHE)
    // -----------------------------------------------------------------------
    private JPanel crearSeccion(String titulo, Color colorFondo, Color colorAccento, String[][] campos, int seccion) {
        JPanel tarjeta = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorFondo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(0, 0, 0, 10));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Título sección
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTit.setForeground(colorAccento);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        tarjeta.add(lblTit, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(colorAccento.getRed(), colorAccento.getGreen(), colorAccento.getBlue(), 60));
        gbc.gridy = 1; gbc.insets = new Insets(2, 0, 10, 0);
        tarjeta.add(sep, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridwidth = 1;

        // Campos de la sección
        for (int i = 0; i < campos.length; i++) {
            gbc.gridx = 0; gbc.gridy = i + 2; gbc.weightx = 0.45;
            JLabel lbl = new JLabel(campos[i][0]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lbl.setForeground(TEXTO_OSCURO);
            tarjeta.add(lbl, gbc);

            JTextField campo = crearCampoTexto(campos[i][1]);
            campo.setToolTipText(obtenerTooltip(seccion, i));
            gbc.gridx = 1; gbc.weightx = 0.55;
            tarjeta.add(campo, gbc);

            // Asignar campo a variable correcta
            asignarCampo(seccion, i, campo);
        }

        return tarjeta;
    }

    private String obtenerTooltip(int seccion, int indice) {
        if (seccion == 0) { // Mañana
            if (indice == 0) return "<html><b>Glucosa en ayunas</b><br>Medición tras al menos 8h sin comer.<br>Rango normal: 70-100 mg/dL<br>Prediabetes: 100-125 mg/dL<br>Diabetes: ≥ 126 mg/dL</html>";
            if (indice == 1) return "<html><b>Carbohidratos desayuno</b><br>Total de carbohidratos ingeridos.<br>Ej: pan (25g), leche (12g), fruta (15g)<br>Recomendado: 30-60g por comida</html>";
            if (indice == 2) return "<html><b>Glucosa post-desayuno</b><br>Medición 2 horas después del desayuno.<br>Rango normal: < 140 mg/dL<br>Elevado: 140-199 mg/dL</html>";
        } else if (seccion == 1) { // Tarde
            if (indice == 0) return "<html><b>Glucosa antes de comer</b><br>Medición justo antes de la comida.<br>Rango normal: 70-130 mg/dL<br>Objetivo en diabéticos: < 130 mg/dL</html>";
            if (indice == 1) return "<html><b>Carbohidratos comida</b><br>Total de carbohidratos del almuerzo.<br>Ej: arroz (45g), pan (25g), legumbres (20g)<br>Recomendado: 45-75g por comida</html>";
            if (indice == 2) return "<html><b>Glucosa post-comida</b><br>Medición 2 horas después del almuerzo.<br>Rango normal: < 140 mg/dL<br>Elevado: 140-199 mg/dL</html>";
        } else { // Noche
            if (indice == 0) return "<html><b>Glucosa antes de cenar</b><br>Medición justo antes de la cena.<br>Rango normal: 70-130 mg/dL<br>Objetivo en diabéticos: < 130 mg/dL</html>";
            if (indice == 1) return "<html><b>Carbohidratos cena</b><br>Total de carbohidratos de la cena.<br>Ej: pasta (40g), verduras (10g)<br>Recomendado: 30-45g (cena ligera)</html>";
            if (indice == 2) return "<html><b>Glucosa post-cena</b><br>Medición 2 horas después de cenar.<br>Rango normal: < 140 mg/dL<br>Elevado: 140-199 mg/dL</html>";
        }
        return "";
    }

    private void asignarCampo(int seccion, int indice, JTextField campo) {
        if (seccion == 0) { // Mañana
            if (indice == 0) campoGlucosaAyunas = campo;
            else if (indice == 1) campoCarbDesayuno = campo;
            else if (indice == 2) campoGlucosaPostDesayuno = campo;
        } else if (seccion == 1) { // Tarde
            if (indice == 0) campoGlucosaComida = campo;
            else if (indice == 1) campoCarbComida = campo;
            else if (indice == 2) campoGlucosaPostComida = campo;
        } else { // Noche
            if (indice == 0) campoGlucosaCena = campo;
            else if (indice == 1) campoCarbCena = campo;
            else if (indice == 2) campoGlucosaPostCena = campo;
        }
    }

    // -----------------------------------------------------------------------
    // HELPERS DE ESTILO
    // -----------------------------------------------------------------------
    private JPanel crearPanelLateral() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(26, 82, 118), 0, getHeight(), new Color(23, 165, 184));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        panel.setPreferredSize(new Dimension(340, 680));

        // Título
        JLabel titulo = new JLabel("Guía de uso");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 60));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(14));

        // Sección Mañana
        panel.add(crearBloqueLateral("Mañana", new Color(232, 245, 250, 40),
            new String[]{
                "Glucosa en ayunas: tras 8h sin comer",
                "Normal: 70-100 mg/dL",
                "Carbohidratos desayuno: lo que comes",
                "Glucosa post-desayuno: 2h después",
                "Normal post: < 140 mg/dL"
            }
        ));
        panel.add(Box.createVerticalStrut(12));

        // Sección Tarde
        panel.add(crearBloqueLateral("Tarde", new Color(225, 245, 240, 40),
            new String[]{
                "Glucosa antes comida: justo antes",
                "Normal: 70-130 mg/dL",
                "Carbohidratos comida: almuerzo completo",
                "Glucosa post-comida: 2h después",
                "Normal post: < 140 mg/dL"
            }
        ));
        panel.add(Box.createVerticalStrut(12));

        // Sección Noche
        panel.add(crearBloqueLateral("Noche", new Color(235, 232, 250, 40),
            new String[]{
                "Glucosa antes cena: justo antes",
                "Normal: 70-130 mg/dL",
                "Carbohidratos cena: ligera, 30-45g",
                "Glucosa post-cena: 2h después",
                "Normal post: < 140 mg/dL"
            }
        ));
        panel.add(Box.createVerticalStrut(16));

        // Nota informativa
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(255, 255, 255, 40));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep2);
        panel.add(Box.createVerticalStrut(12));

        JLabel nota = new JLabel("<html><div style='width:290px'>"
            + "<b style='color:white'>¿Cómo medir la glucosa?</b><br><br>"
            + "<span style='color:#ccecf0'>Glucómetro en el dedo índice.<br>"
            + "Anota en mg/dL antes o 2h<br>después de cada comida.</span>"
            + "</div></html>");
        nota.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nota);

        return panel;
    }

    private JPanel crearBloqueLateral(String titulo, Color fondoColor, String[] items) {
        JPanel bloque = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
            }
        };
        bloque.setLayout(new BoxLayout(bloque, BoxLayout.Y_AXIS));
        bloque.setOpaque(false);
        bloque.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        bloque.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(255, 255, 255, 220));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloque.add(lblTitulo);
        bloque.add(Box.createVerticalStrut(6));

        for (String item : items) {
            JLabel lbl = new JLabel("· " + item);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lbl.setForeground(new Color(255, 255, 255, 180));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            bloque.add(lbl);
            bloque.add(Box.createVerticalStrut(2));
        }

        return bloque;
    }

    private JPanel crearTarjetaBase(Color color) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(0, 0, 0, 10));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 14, 14));
            }
        };
    }

    private JPanel crearIcono(String tipo) {
        JPanel ic = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setOpaque(false);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TURQUESA);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2, cy = getHeight() / 2;
                switch (tipo) {
                    case "persona": g2.drawOval(cx-4,cy-7,8,8); g2.drawArc(cx-6,cy+1,12,8,0,180); break;
                    case "id": g2.drawRoundRect(cx-6,cy-5,12,10,2,2); g2.drawLine(cx-3,cy-2,cx+3,cy-2); g2.drawLine(cx-3,cy+1,cx+1,cy+1); break;
                    case "calendario": g2.drawRoundRect(cx-6,cy-5,12,11,2,2); g2.drawLine(cx-3,cy-7,cx-3,cy-4); g2.drawLine(cx+3,cy-7,cx+3,cy-4); g2.drawLine(cx-6,cy-1,cx+6,cy-1); break;
                }
            }
            @Override public Dimension getPreferredSize() { return new Dimension(16, 16); }
        };
        ic.setOpaque(false);
        return ic;
    }

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
            BorderFactory.createLineBorder(TURQUESA_CLARO, 1, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        campo.setBackground(BLANCO);
        return campo;
    }

    private void estilizarSpinner(JSpinner spinner) {
        spinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TURQUESA_CLARO, 1, true),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
    }

    private JButton crearBoton() {
        JButton btn = new JButton("Enviar para Predicción de Riesgo") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, AZUL_OSCURO.darker(), getWidth(), getHeight(), TURQUESA.darker());
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, AZUL_OSCURO.brighter(), getWidth(), getHeight(), TURQUESA.brighter());
                } else {
                    gp = new GradientPaint(0, 0, AZUL_OSCURO, getWidth(), getHeight(), TURQUESA);
                }
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                // Flecha
                g2.setColor(BLANCO);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = 24, cy = getHeight() / 2;
                g2.drawLine(cx - 6, cy, cx + 6, cy);
                g2.drawLine(cx + 6, cy, cx + 2, cy - 4);
                g2.drawLine(cx + 6, cy, cx + 2, cy + 4);
                // Texto
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(500, 46));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
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

            // Validar nombre
            String nombre = campoNombre.getText().trim();
            if (nombre.isEmpty()) { mostrarError("Por favor, introduce el nombre del paciente."); return; }

            int edad = (int) campoEdad.getValue();

            // Parsear todos los campos numéricos
            double glucosaAyunas, carbDesayuno, glucosaPostDesayuno;
            double glucosaComida, carbComida, glucosaPostComida;
            double glucosaCena, carbCena, glucosaPostCena;

            try {
                glucosaAyunas       = parsear(campoGlucosaAyunas);
                carbDesayuno        = parsear(campoCarbDesayuno);
                glucosaPostDesayuno = parsear(campoGlucosaPostDesayuno);
                glucosaComida       = parsear(campoGlucosaComida);
                carbComida          = parsear(campoCarbComida);
                glucosaPostComida   = parsear(campoGlucosaPostComida);
                glucosaCena         = parsear(campoGlucosaCena);
                carbCena            = parsear(campoCarbCena);
                glucosaPostCena     = parsear(campoGlucosaPostCena);
            } catch (NumberFormatException ex) {
                mostrarError("Algún valor numérico no es válido.\nUsa formato decimal con punto (ej: 98.5).");
                return;
            }

            // Array completo — orden exacto que espera AgentePaciente
            // [0] ventana | [1] nombre | [2] edad
            // [3] glucosaAyunas | [4] carbDesayuno | [5] glucosaPostDesayuno
            // [6] glucosaComida | [7] carbComida | [8] glucosaPostComida
            // [9] glucosaCena | [10] carbCena | [11] glucosaPostCena
            Object[] datos = {
                this, nombre, edad,
                glucosaAyunas, carbDesayuno, glucosaPostDesayuno,
                glucosaComida, carbComida, glucosaPostComida,
                glucosaCena, carbCena, glucosaPostCena
            };

            try {
                agentePaciente.putO2AObject(datos, false);
                botonEnviar.setEnabled(false);
                botonEnviar.setText("Procesando...");
            } catch (StaleProxyException ex) {
                mostrarError("Error al comunicarse con el AgentePaciente.\nComprueba que JADE está activo.");
                ex.printStackTrace();
            }
        });
    }

    private double parsear(JTextField campo) {
        return Double.parseDouble(campo.getText().trim().replace(",", "."));
    }

    // -----------------------------------------------------------------------
    // POPUP RESULTADO
    // -----------------------------------------------------------------------
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
                ? "<html><div style='text-align:left;width:210px;'>"
                + "<b>Recomendaciones:</b><br>"
                + "&#8226; Acuda a su médico lo antes posible<br>"
                + "&#8226; Reduzca el consumo de azúcar<br>"
                + "&#8226; Evite comidas procesadas y bebidas azucaradas<br>"
                + "&#8226; Realice ejercicio moderado diario"
                + "</div></html>"
                : "<html><div style='text-align:left;width:210px;'>"
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

            // Cabecera
            JPanel cabDialog = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g); g.setColor(AZUL_OSCURO); g.fillRect(0,0,getWidth(),getHeight());
                }
            };
            cabDialog.setPreferredSize(new Dimension(300, 52));
            cabDialog.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
            JPanel txtsCab = new JPanel(); txtsCab.setLayout(new BoxLayout(txtsCab, BoxLayout.Y_AXIS)); txtsCab.setOpaque(false);
            JLabel lTit = new JLabel("DiaPredict"); lTit.setFont(new Font("SansSerif", Font.BOLD, 13)); lTit.setForeground(BLANCO);
            JLabel lSub = new JLabel("Resultado del análisis"); lSub.setFont(new Font("SansSerif", Font.PLAIN, 10)); lSub.setForeground(new Color(200,220,240));
            txtsCab.add(lTit); txtsCab.add(lSub);
            cabDialog.add(txtsCab, BorderLayout.CENTER);

            // Cuerpo
            JPanel cuerpo = new JPanel() {
                @Override protected void paintComponent(Graphics g) { super.paintComponent(g); g.setColor(colorFondo); g.fillRect(0,0,getWidth(),getHeight()); }
            };
            cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
            cuerpo.setBorder(BorderFactory.createEmptyBorder(22,20,18,20));
            cuerpo.setOpaque(false);

            JPanel circulo = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int cx = getWidth()/2, cy = getHeight()/2, r = 28;
                    g2.setColor(colorCirc); g2.fillOval(cx-r,cy-r,r*2,r*2);
                    g2.setColor(colorBorde); g2.setStroke(new BasicStroke(2f)); g2.drawOval(cx-r,cy-r,r*2,r*2);
                    g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (esAlto) { g2.drawLine(cx,cy-11,cx,cy+2); g2.fillOval(cx-2,cy+7,5,5); }
                    else { g2.drawLine(cx-10,cy+1,cx-2,cy+10); g2.drawLine(cx-2,cy+10,cx+12,cy-7); }
                }
                @Override public Dimension getPreferredSize() { return new Dimension(72,72); }
            };
            circulo.setOpaque(false); circulo.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel lblResultado = new JLabel(etiqueta, SwingConstants.CENTER);
            lblResultado.setFont(new Font("SansSerif", Font.BOLD, 15)); lblResultado.setForeground(colorTitulo); lblResultado.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel lblConsejo = new JLabel("<html><div style='text-align:center;width:200px'>"+consejo+"</div></html>", SwingConstants.CENTER);
            lblConsejo.setFont(new Font("SansSerif", Font.PLAIN, 11)); lblConsejo.setForeground(colorTexto); lblConsejo.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel lblRecomendaciones = new JLabel(recomendaciones);
            lblRecomendaciones.setFont(new Font("SansSerif", Font.PLAIN, 11)); lblRecomendaciones.setForeground(colorTexto); lblRecomendaciones.setAlignmentX(Component.CENTER_ALIGNMENT);

            cuerpo.add(circulo); cuerpo.add(Box.createVerticalStrut(10));
            cuerpo.add(lblResultado); cuerpo.add(Box.createVerticalStrut(6));
            cuerpo.add(lblConsejo); cuerpo.add(Box.createVerticalStrut(10));
            cuerpo.add(lblRecomendaciones);

            // Botón aceptar
            JPanel panelBoton = new JPanel(new GridBagLayout()) {
                @Override protected void paintComponent(Graphics g) { super.paintComponent(g); g.setColor(colorBorde); g.fillRect(0,0,getWidth(),getHeight()); }
            };
            panelBoton.setOpaque(false); panelBoton.setPreferredSize(new Dimension(300,42));
            panelBoton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JLabel lblAceptar = new JLabel("Aceptar"); lblAceptar.setFont(new Font("SansSerif", Font.BOLD, 13)); lblAceptar.setForeground(BLANCO);
            panelBoton.add(lblAceptar);
            panelBoton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseClicked(java.awt.event.MouseEvent e) { dialog.dispose(); }
            });

            dialog.getRootPane().setBorder(BorderFactory.createLineBorder(colorBorde, 1));
            dialog.add(cabDialog, BorderLayout.NORTH);
            dialog.add(cuerpo, BorderLayout.CENTER);
            dialog.add(panelBoton, BorderLayout.SOUTH);
            dialog.setVisible(true);

            botonEnviar.setEnabled(true);
            botonEnviar.setText("Enviar para Predicción de Riesgo");
        });
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.WARNING_MESSAGE);
    }
}