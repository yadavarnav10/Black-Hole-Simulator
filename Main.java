import java.util.*;
public class Main {
    public static void main(String[] args) {
        System.out.println("Enter mass of Black Hole (Solar Masses):");
        Scanner sc = new Scanner(System.in);
        double mass = sc.nextDouble();
        System.out.println("Enter distance from center of Black Hole (meters):");
        double distance = sc.nextDouble();  
        if (mass <= 0|| distance <= 0) {
            System.out.println("INVALID INPUT. Mass and distance must be positive numbers.");
            sc.close();
            return;
        }
        Black_Hole blackHole = new Black_Hole(mass);
        double schwarzschildRadius = blackHole.getSchwarzschildRadius();
        double gravitationalAcceleration = blackHole.gravitationalAcceleration(distance);
        double escapeVelocity = blackHole.escapeVelocity(distance);

        System.out.println("Schwarzschild Radius: " + schwarzschildRadius + " meters");
        System.out.println("Gravitational Acceleration: " + gravitationalAcceleration + " m/s^2");
        System.out.println("Escape Velocity: " + escapeVelocity + " m/s");
        sc.close();
    }
}