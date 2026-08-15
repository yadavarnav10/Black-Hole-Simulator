import java.util.*;
public class Main {
    public static void main(String[] args) {
        System.out.println("Enter mass of Black Hole (Solar Masses):");
        Scanner sc = new Scanner(System.in);
        double mass = sc.nextDouble();
        System.out.println("Enter mass of particle (kg):");
        double mass_p = sc.nextDouble();
        System.out.println("Enter position of particle (x, y) (black hole is at the origin):");
        double x = sc.nextDouble();
        double y = sc.nextDouble();
        double distance = Math.sqrt((x * x) + (y * y));
        if (mass <= 0|| mass_p <= 0|| distance <= 0) {
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
        particle p= new particle(x, y, 0, 0, mass_p);
        Black_Hole blackHole = new Black_Hole(mass);
        p.updateAcceleration(blackHole);
        double schwarzschildRadius = blackHole.getSchwarzschildRadius();
        double gravitationalAcceleration = blackHole.gravitationalAcceleration(distance);
        double escapeVelocity = blackHole.escapeVelocity(distance);
        double gravitationalForce = p.gravitationalForce(blackHole);
        double gravitationalAcceleration_P = p.gravitationalAcceleration(blackHole);
        System.out.println("Schwarzschild Radius: " + schwarzschildRadius + " meters");
        System.out.println("Gravitational Acceleration: " + gravitationalAcceleration + " m/s^2");
        System.out.println("Escape Velocity: " + escapeVelocity + " m/s");
        System.out.println("Gravitational Force on particle: " + gravitationalForce + " N");
        System.out.println("Gravitational Acceleration (of particle): " + gravitationalAcceleration_P + " m/s^2");
        System.out.println("Simulating motion of particle for " + time + " seconds with time step of " + dt + " seconds...");
        int steps = (int) (time / dt);
        for (int i = 0; i < steps; i++) {
            p.update_Motion(blackHole, dt);
            double new_distance = Math.sqrt((p.x * p.x) + (p.y * p.y));
            if (new_distance <= schwarzschildRadius) {
                System.out.println("Particle has crossed the event horizon at time " + (i * dt) + " seconds.");
                break;
            }
        }
        System.out.println("Final position of particle: (" + p.x + ", " + p.y + ")");
        System.out.println("Final velocity of particle: (" + p.vx + ", " + p.vy + ")");
        sc.close();
    }
}