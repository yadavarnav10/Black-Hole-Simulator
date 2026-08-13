public class Main {
    public static void main(String[] args) {
        Black_Hole blackHole = new Black_Hole(10); // 10 solar masses
        double schwarzschildRadius = blackHole.getSchwarzschildRadius();
        double gravitationalAcceleration = blackHole.gravitationalAcceleration(1e7); // at 10 million meters
        double escapeVelocity = blackHole.escapeVelocity(1e7); // at 10 million meters

        System.out.println("Schwarzschild Radius: " + schwarzschildRadius + " meters");
        System.out.println("Gravitational Acceleration: " + gravitationalAcceleration + " m/s^2");
        System.out.println("Escape Velocity: " + escapeVelocity + " m/s");
    }
}