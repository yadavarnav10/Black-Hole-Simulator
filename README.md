# Black Hole Simulator

This project now has one JavaFX entry point with two selectable models:

- **Newtonian gravity** uses the existing velocity-Verlet `particle` simulation in SI units.
- **Relativistic (Schwarzschild RK4)** uses the existing `State`, `RelativisticSim`, and `RK4_Integrator` classes in geometric units (`G = c = 1`).

## One-time JavaFX setup

The project uses JavaFX 21.0.12 for the installed JDK 21. Run this in PowerShell from the project folder:

```powershell
.\scripts\setup-javafx.ps1
```

## Run the GUI

```powershell
.\scripts\run-gui.ps1
```

The first GUI keeps the scope deliberately small: choose a model, enter its physical initial conditions, run it, and inspect the final state and conservation checks. The next visualization step can build a plotted trajectory from the same two runners.

## Keep using the original console model

```powershell
javac --module-path .\lib\javafx-sdk-21.0.12\lib --add-modules javafx.controls Main.java Black_Hole.java particle.java State.java RelativisticSim.java RK4_Integrator.java
java --module-path .\lib\javafx-sdk-21.0.12\lib --add-modules javafx.controls Main --console
```
