import java.util.Locale;
import java.util.Scanner;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

/**
 * Launches the JavaFX simulator. Run with --console to keep using the original
 * Newtonian command-line workflow.
 */
public class Main extends Application {
    private static final String NEWTONIAN = "Newtonian gravity";
    private static final String RELATIVISTIC = "Relativistic (Schwarzschild RK4)";

    private final GridPane parameterGrid = new GridPane();
    private final TextArea results = new TextArea();
    private final ComboBox<String> modePicker = new ComboBox<>();
    private final BlackHoleCanvas blackHoleCanvas = new BlackHoleCanvas();
    private final Label viewportStatus = new Label();

    private TextField massField;
    private TextField particleMassField;
    private TextField xField;
    private TextField yField;
    private TextField vxField;
    private TextField vyField;
    private TextField timeField;
    private TextField stepField;
    private TextField radiusField;
    private TextField radialVelocityField;
    private TextField angularVelocityField;

    public static void main(String[] args) {
        if (args.length > 0 && "--console".equals(args[0])) {
            runNewtonianConsole();
            return;
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        modePicker.getItems().addAll(NEWTONIAN, RELATIVISTIC);
        modePicker.setValue(NEWTONIAN);
        modePicker.setOnAction(event -> showSelectedParameters());

        Label heading = new Label("Black Hole Simulator");
        heading.getStyleClass().add("heading");
        Label description = new Label("Orbital dynamics laboratory · Newtonian and Schwarzschild models");
        description.getStyleClass().add("muted");
        VBox title = new VBox(4, heading, description);
        Label versionBadge = new Label("2-D LABORATORY");
        versionBadge.getStyleClass().add("badge");
        HBox header = new HBox(18, title, versionBadge);
        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(title, Priority.ALWAYS);
        header.setPadding(new Insets(22, 26, 18, 26));

        parameterGrid.setHgap(12);
        parameterGrid.setVgap(8);
        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setPercentWidth(55);
        ColumnConstraints inputColumn = new ColumnConstraints();
        inputColumn.setHgrow(Priority.ALWAYS);
        parameterGrid.getColumnConstraints().addAll(labelColumn, inputColumn);

        Label controlTitle = new Label("SIMULATION CONTROLS");
        controlTitle.getStyleClass().add("panel-title");
        Label modelLabel = new Label("MODEL");
        modelLabel.getStyleClass().add("control-label");
        modePicker.setMaxWidth(Double.MAX_VALUE);
        VBox modelControl = new VBox(6, modelLabel, modePicker);

        Button runButton = new Button("Run simulation");
        runButton.getStyleClass().add("primary-button");
        runButton.setDefaultButton(true);
        runButton.setOnAction(event -> runSelectedSimulation());
        Button resetButton = new Button("Reset values");
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setOnAction(event -> showSelectedParameters());
        HBox buttons = new HBox(10, runButton, resetButton);
        buttons.setAlignment(Pos.CENTER_LEFT);

        results.setEditable(false);
        results.setWrapText(true);
        results.setPrefRowCount(10);
        results.setPromptText("Results and conservation checks will appear here.");
        Label resultTitle = new Label("RUN OUTPUT");
        resultTitle.getStyleClass().add("control-label");

        VBox controlContent = new VBox(15,
                controlTitle, modelControl, new Separator(), parameterGrid, buttons,
                new Separator(), resultTitle, results);
        controlContent.getStyleClass().add("control-pane");
        ScrollPane controlScroll = new ScrollPane(controlContent);
        controlScroll.setFitToWidth(true);
        controlScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        controlScroll.getStyleClass().add("control-scroll");
        controlScroll.setPrefViewportWidth(345);
        controlScroll.setMinWidth(320);

        viewportStatus.getStyleClass().add("viewport-status");
        StackPane visualFrame = new StackPane(blackHoleCanvas);
        visualFrame.getStyleClass().add("visual-frame");
        visualFrame.setMinSize(500, 480);
        blackHoleCanvas.widthProperty().bind(visualFrame.widthProperty());
        blackHoleCanvas.heightProperty().bind(visualFrame.heightProperty());
        HBox visualTop = new HBox(10, new Label("ORBIT VIEW"), viewportStatus);
        visualTop.getStyleClass().add("visual-header");
        HBox.setHgrow(viewportStatus, Priority.ALWAYS);
        viewportStatus.setAlignment(Pos.CENTER_RIGHT);
        Label legend = new Label("● particle     ◯ event horizon     — orbit guide     |     schematic 2-D preview, not to scale");
        legend.getStyleClass().add("legend");
        VBox visualColumn = new VBox(10, visualTop, visualFrame, legend);
        visualColumn.getStyleClass().add("visual-column");
        VBox.setVgrow(visualFrame, Priority.ALWAYS);

        HBox dashboard = new HBox(18, visualColumn, controlScroll);
        dashboard.setPadding(new Insets(0, 26, 24, 26));
        HBox.setHgrow(visualColumn, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(dashboard);
        root.getStyleClass().add("root-pane");

        showSelectedParameters();
        Scene scene = new Scene(root, 1120, 720);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("Black Hole Simulator");
        stage.setMinWidth(900);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    private void showSelectedParameters() {
        parameterGrid.getChildren().clear();
        if (NEWTONIAN.equals(modePicker.getValue())) {
            addSection(0, "BLACK HOLE");
            massField = addField(1, "Mass", "10", "solar masses");
            addSection(2, "PARTICLE / INITIAL STATE");
            particleMassField = addField(3, "Particle mass", "1", "kg");
            xField = addField(4, "X position", "100000000", "m");
            yField = addField(5, "Y position", "0", "m");
            vxField = addField(6, "X velocity", "0", "m/s");
            vyField = addField(7, "Y velocity", "3600000", "m/s");
            addSection(8, "INTEGRATION");
            timeField = addField(9, "Simulation time", "20", "s");
            stepField = addField(10, "Time step", "0.01", "s");
        } else {
            addSection(0, "SCHWARZSCHILD SPACETIME");
            massField = addField(1, "Mass M", "1", "geometric units");
            addSection(2, "INITIAL FOUR-VELOCITY");
            radiusField = addField(3, "Radius r", "6", "M");
            radialVelocityField = addField(4, "Radial uʳ", "0.07", "dr/dτ");
            angularVelocityField = addField(5, "Angular uφ", "0.04", "dφ/dτ");
            addSection(6, "RK4 INTEGRATION");
            timeField = addField(7, "Proper time τ", "10", "");
            stepField = addField(8, "Step h", "0.00001", "");
        }
        updatePreview();
    }

    private void addSection(int row, String title) {
        Label sectionTitle = new Label(title);
        sectionTitle.getStyleClass().add("section-title");
        parameterGrid.add(sectionTitle, 0, row, 2, 1);
    }

    private TextField addField(int row, String label, String defaultValue, String unit) {
        Label name = new Label(label.toUpperCase());
        name.getStyleClass().add("field-label");
        TextField field = new TextField(defaultValue);
        field.getStyleClass().add("parameter-field");
        field.setPromptText(unit);
        field.textProperty().addListener((observable, oldValue, newValue) -> updatePreview());
        GridPane.setHgrow(field, Priority.ALWAYS);
        parameterGrid.add(name, 0, row);
        parameterGrid.add(field, 1, row);
        return field;
    }

    private void updatePreview() {
        boolean newtonian = NEWTONIAN.equals(modePicker.getValue());
        viewportStatus.setText(newtonian ? "NEWTONIAN · INITIAL STATE" : "SCHWARZSCHILD · INITIAL STATE");
        double px = newtonian ? safeNumber(xField, 1) : safeNumber(radiusField, 6);
        double py = newtonian ? safeNumber(yField, 0) : 0;
        blackHoleCanvas.update(newtonian, px, py);
    }

    private static double safeNumber(TextField field, double fallback) {
        if (field == null) return fallback;
        try {
            double value = Double.parseDouble(field.getText().trim());
            return Double.isFinite(value) ? value : fallback;
        } catch (NumberFormatException exception) {
            return fallback;
        }
    }

    private void runSelectedSimulation() {
        try {
            if (NEWTONIAN.equals(modePicker.getValue())) {
                results.setText(runNewtonian(
                        positive(massField, "Black-hole mass"),
                        positive(particleMassField, "Particle mass"),
                        number(xField, "Initial x position"),
                        number(yField, "Initial y position"),
                        number(vxField, "Initial x velocity"),
                        number(vyField, "Initial y velocity"),
                        positive(timeField, "Simulation time"),
                        positive(stepField, "Time step")));
            } else {
                results.setText(runRelativistic(
                        positive(massField, "Mass M"),
                        positive(radiusField, "Initial radius"),
                        number(radialVelocityField, "Initial radial velocity"),
                        number(angularVelocityField, "Initial angular velocity"),
                        positive(timeField, "Proper time"),
                        positive(stepField, "RK4 step")));
            }
        } catch (IllegalArgumentException exception) {
            results.setText("Input error: " + exception.getMessage());
        }
    }

    private static String runNewtonian(double mass, double particleMass, double x, double y, double vx, double vy, double time, double dt) {
        double distance = Math.hypot(x, y);
        if (distance == 0) throw new IllegalArgumentException("The initial position cannot be at the origin.");
        Black_Hole blackHole = new Black_Hole(mass);
        particle p = new particle(x, y, vx, vy, particleMass);
        p.updateAcceleration(blackHole);
        double schwarzschildRadius = blackHole.getSchwarzschildRadius();
        if (distance <= schwarzschildRadius) throw new IllegalArgumentException("The particle begins inside the event horizon.");
        int steps = checkedSteps(time, dt);
        for (int i = 0; i < steps; i++) {
            double oldX = p.x;
            double oldY = p.y;
            p.update_Motion(blackHole, dt);
            if (!Double.isFinite(p.x) || !Double.isFinite(p.y)) throw new IllegalArgumentException("The calculation became unstable. Use a smaller time step.");
            double dx = p.x - oldX;
            double dy = p.y - oldY;
            double movementSquared = dx * dx + dy * dy;
            double closestFraction = movementSquared == 0 ? 0 : -(oldX * dx + oldY * dy) / movementSquared;
            closestFraction = Math.max(0, Math.min(1, closestFraction));
            double closestDistance = Math.hypot(oldX + closestFraction * dx, oldY + closestFraction * dy);
            if (closestDistance <= schwarzschildRadius) {
                return String.format(Locale.US,
                        "Newtonian gravity\n\nEvent horizon crossed between %.6f s and %.6f s.\nSchwarzschild radius: %.6e m",
                        i * dt, (i + 1) * dt, schwarzschildRadius);
            }
        }
        return String.format(Locale.US,
                "Newtonian gravity\n\nSteps: %,d\nSchwarzschild radius: %.6e m\nInitial distance: %.6e m\nEscape velocity at start: %.6e m/s\nFinal position: (%.6e, %.6e) m\nFinal velocity: (%.6e, %.6e) m/s",
                steps, schwarzschildRadius, distance, blackHole.escapeVelocity(distance), p.x, p.y, p.vx, p.vy);
    }

    private static String runRelativistic(double mass, double radius, double radialVelocity, double angularVelocity, double properTime, double h) {
        if (radius <= 2.0 * mass) throw new IllegalArgumentException("Initial radius must be outside the event horizon (r > 2M).");
        State s = new State();
        s.t = 0;
        s.r = radius;
        s.phi = 0;
        s.ur = radialVelocity;
        s.uphi = angularVelocity;
        double f = 1.0 - 2.0 * mass / radius;
        s.ut = Math.sqrt((1.0 + s.ur * s.ur / f + s.uphi * s.uphi * radius * radius) / f);
        int steps = checkedSteps(properTime, h);
        double initialEnergy = f * s.ut;
        double initialAngularMomentum = radius * radius * s.uphi;
        for (int i = 0; i < steps; i++) {
            s = RK4_Integrator.step(s, mass, h);
            if (!isFinite(s)) throw new IllegalArgumentException("The RK4 calculation became unstable. Use a smaller step size.");
            if (s.r <= 2.0 * mass) {
                return String.format(Locale.US,
                        "Relativistic Schwarzschild RK4\n\nEvent horizon crossed near proper time τ = %.6f.\nInitial specific energy: %.9f\nInitial specific angular momentum: %.9f",
                        (i + 1) * h, initialEnergy, initialAngularMomentum);
            }
        }
        f = 1.0 - 2.0 * mass / s.r;
        double finalEnergy = f * s.ut;
        double finalAngularMomentum = s.r * s.r * s.uphi;
        double normalization = s.ut * s.ut * f - s.ur * s.ur / f - s.uphi * s.uphi * s.r * s.r;
        return String.format(Locale.US,
                "Relativistic Schwarzschild RK4\n\nSteps: %,d\nFinal coordinate time t: %.8f\nFinal radius r: %.8f M\nFinal angle φ: %.8f rad\nFinal uʳ: %.8f\nInitial / final specific energy: %.10f / %.10f\nInitial / final specific angular momentum: %.10f / %.10f\nFour-velocity normalization: %.10f",
                steps, s.t, s.r, s.phi, s.ur, initialEnergy, finalEnergy, initialAngularMomentum, finalAngularMomentum, normalization);
    }

    private static int checkedSteps(double duration, double step) {
        double rawSteps = duration / step;
        if (!Double.isFinite(rawSteps) || rawSteps > 5_000_000) throw new IllegalArgumentException("This run exceeds 5,000,000 steps. Reduce its duration or increase the step size.");
        return (int) rawSteps;
    }

    private static boolean isFinite(State state) {
        return Double.isFinite(state.t) && Double.isFinite(state.r) && Double.isFinite(state.phi)
                && Double.isFinite(state.ut) && Double.isFinite(state.ur) && Double.isFinite(state.uphi);
    }

    private static double positive(TextField field, String name) {
        double value = number(field, name);
        if (value <= 0) throw new IllegalArgumentException(name + " must be positive.");
        return value;
    }

    private static double number(TextField field, String name) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (!Double.isFinite(value)) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " must be a finite number.");
        }
    }

    /** A deliberately schematic renderer: it is a viewport, not a photorealistic model. */
    private static final class BlackHoleCanvas extends Canvas {
        private boolean newtonian = true;
        private double particleAngle;

        private BlackHoleCanvas() {
            super(650, 520);
            widthProperty().addListener((observable, oldValue, newValue) -> draw());
            heightProperty().addListener((observable, oldValue, newValue) -> draw());
        }

        private void update(boolean isNewtonian, double x, double y) {
            newtonian = isNewtonian;
            particleAngle = Math.atan2(y, x);
            draw();
        }

        private void draw() {
            double width = getWidth();
            double height = getHeight();
            if (width <= 0 || height <= 0) return;

            GraphicsContext g = getGraphicsContext2D();
            g.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#101b2d")), new Stop(1, Color.web("#050914"))));
            g.fillRect(0, 0, width, height);
            drawStars(g, width, height);

            double cx = width / 2.0;
            double cy = height / 2.0;
            double horizon = Math.min(width, height) * 0.115;
            double orbitX = Math.min(width * 0.41, height * 0.55);
            double orbitY = orbitX * 0.55;

            g.save();
            g.setGlobalAlpha(0.35);
            g.setStroke(Color.web("#5886bd"));
            g.setLineWidth(1.1);
            g.setLineDashes(5, 8);
            g.strokeOval(cx - orbitX, cy - orbitY, orbitX * 2, orbitY * 2);
            g.restore();

            for (int layer = 6; layer >= 1; layer--) {
                double scale = 1.0 + layer * 0.12;
                g.setFill(Color.rgb(247, 126, 36, 0.025 * (7 - layer)));
                g.fillOval(cx - horizon * 2.9 * scale, cy - horizon * 0.82 * scale,
                        horizon * 5.8 * scale, horizon * 1.64 * scale);
            }
            g.setFill(new LinearGradient(0, cy - horizon, 0, cy + horizon, false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#4a190b")), new Stop(0.32, Color.web("#e47b25")),
                    new Stop(0.5, Color.web("#f9c46a")), new Stop(0.68, Color.web("#d85d18")),
                    new Stop(1, Color.web("#30110b"))));
            g.fillOval(cx - horizon * 2.75, cy - horizon * 0.72, horizon * 5.5, horizon * 1.44);

            g.setFill(Color.web("#020306"));
            g.fillOval(cx - horizon, cy - horizon, horizon * 2, horizon * 2);
            g.setStroke(Color.web("#f4a144"));
            g.setGlobalAlpha(0.9);
            g.setLineWidth(1.5);
            g.strokeOval(cx - horizon * 1.04, cy - horizon * 1.04, horizon * 2.08, horizon * 2.08);
            g.setGlobalAlpha(1.0);

            double markerX = cx + Math.cos(particleAngle) * orbitX;
            double markerY = cy + Math.sin(particleAngle) * orbitY;
            g.setFill(Color.web("#66d8ff"));
            g.fillOval(markerX - 5, markerY - 5, 10, 10);
            g.setStroke(Color.web("#bcefff"));
            g.setLineWidth(1);
            g.strokeOval(markerX - 9, markerY - 9, 18, 18);

            g.setFill(Color.web("#d7e7ff"));
            g.setFont(javafx.scene.text.Font.font("Segoe UI", 11));
            g.fillText(newtonian ? "NEWTONIAN PREVIEW" : "RELATIVISTIC PREVIEW", 18, height - 22);
            g.setFill(Color.web("#b1c5e5"));
            g.fillText("event horizon", cx - horizon * 0.52, cy + horizon + 20);
        }

        private static void drawStars(GraphicsContext g, double width, double height) {
            for (int i = 0; i < 105; i++) {
                double x = (i * 83.0 + 37) % width;
                double y = (i * 47.0 + 19) % height;
                double size = i % 9 == 0 ? 2.0 : 1.0;
                g.setFill(i % 6 == 0 ? Color.web("#8fb8e8") : Color.web("#e7f0ff"));
                g.setGlobalAlpha(i % 9 == 0 ? 0.9 : 0.48);
                g.fillOval(x, y, size, size);
            }
            g.setGlobalAlpha(1.0);
        }
    }

    /** Preserved Newtonian command-line workflow, including its in-step horizon check. */
    private static void runNewtonianConsole() {
        System.out.println("Enter mass of Black Hole (Solar Masses):");
        Scanner sc = new Scanner(System.in);
        double mass = sc.nextDouble();
        System.out.println("Enter mass of particle (kg):");
        double massP = sc.nextDouble();
        System.out.println("Enter position of particle (x, y) (black hole is at the origin):");
        double x = sc.nextDouble();
        double y = sc.nextDouble();
        System.out.println("Enter initial velocity of particle (vx, vy):");
        double vx = sc.nextDouble();
        double vy = sc.nextDouble();
        double distance = Math.sqrt(x * x + y * y);
        if (mass <= 0 || massP <= 0 || distance <= 0) {
            System.out.println("INVALID INPUT. Mass and distance must be positive numbers.");
            sc.close();
            return;
        }
        System.out.println("Enter amount of time to simulate (seconds):");
        double time = sc.nextDouble();
        System.out.println("Enter time step for simulation (seconds):");
        double dt = sc.nextDouble();
        if (dt <= 0 || time <= 0) {
            System.out.println("INVALID INPUT. Time and time step must be positive numbers.");
            sc.close();
            return;
        }
        particle p = new particle(x, y, vx, vy, massP);
        Black_Hole blackHole = new Black_Hole(mass);
        p.updateAcceleration(blackHole);
        double schwarzschildRadius = blackHole.getSchwarzschildRadius();
        double gravitationalAcceleration = blackHole.gravitationalAcceleration(distance);
        double escapeVelocity = blackHole.escapeVelocity(distance);
        double gravitationalForce = p.gravitationalForce(blackHole);
        double gravitationalAccelerationP = p.gravitationalAcceleration(blackHole);
        if (distance <= schwarzschildRadius) {
            System.out.println("The particle is already inside the event horizon of the black hole.");
            sc.close();
            return;
        }
        System.out.println("Schwarzschild Radius: " + schwarzschildRadius + " meters");
        System.out.println("Gravitational Acceleration: " + gravitationalAcceleration + " m/s^2");
        System.out.println("Escape Velocity: " + escapeVelocity + " m/s");
        System.out.println("Gravitational Force on particle: " + gravitationalForce + " N");
        System.out.println("Gravitational Acceleration (of particle): " + gravitationalAccelerationP + " m/s^2");
        System.out.println("Simulating motion of particle for " + time + " seconds with time step of " + dt + " seconds...");
        int steps = (int) (time / dt);
        for (int i = 0; i < steps; i++) {
            double oldX = p.x;
            double oldY = p.y;
            p.update_Motion(blackHole, dt);
            double dx = p.x - oldX;
            double dy = p.y - oldY;
            double movementSquared = dx * dx + dy * dy;
            double closestFraction = movementSquared == 0 ? 0 : -(oldX * dx + oldY * dy) / movementSquared;
            closestFraction = Math.max(0, Math.min(1, closestFraction));
            double closestDistance = Math.hypot(oldX + closestFraction * dx, oldY + closestFraction * dy);
            if (closestDistance <= schwarzschildRadius) {
                System.out.println("Particle has crossed the event horizon between " + (i * dt) + " and " + ((i + 1) * dt) + " seconds.");
                sc.close();
                return;
            }
        }
        System.out.println("Final position of particle: (" + p.x + ", " + p.y + ")");
        System.out.println("Final velocity of particle: (" + p.vx + ", " + p.vy + ")");
        sc.close();
    }
}
