import java.util.*;
public class Main {
    public static void main(String[] args) {
        System.out.println("Enter mass of Black Hole (Solar Masses):");
        Scanner sc = new Scanner(System.in);
        double mass = sc.nextDouble();
        System.out.println("Enter mass of particle (kg):");
        double mass_p = sc.nextDouble();
        System.out.println("Enter distance from center of Black Hole (meters):");
        double distance = sc.nextDouble();  
        if (mass <= 0|| distance <= 0|| mass_p <= 0) {
            System.out.println("INVALID INPUT. Mass and distance must be positive numbers.");
            sc.close();
            return;
        }
        particle p= new particle(0, 0, 0, 0, mass_p);
        Black_Hole blackHole = new Black_Hole(mass);
        double schwarzschildRadius = blackHole.getSchwarzschildRadius();
        double gravitationalAcceleration = blackHole.gravitationalAcceleration(distance);
        double escapeVelocity = blackHole.escapeVelocity(distance);
        double gravitationalForce = p.gravitationalForce(blackHole, distance);
        double gravitationalAcceleration_P = p.gravitationalAcceleration(blackHole, distance);

        System.out.println("Schwarzschild Radius: " + schwarzschildRadius + " meters");
        System.out.println("Gravitational Acceleration: " + gravitationalAcceleration + " m/s^2");
        System.out.println("Escape Velocity: " + escapeVelocity + " m/s");
        System.out.println("Gravitational Force on particle: " + gravitationalForce + " N");
        System.out.println("Gravitational Acceleration (of particle): " + gravitationalAcceleration_P + " m/s^2");
        sc.close();
    }
}