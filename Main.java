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
        System.out.println("Enter initial velocity of particle (vx, vy):");
        double vx = sc.nextDouble();
        double vy = sc.nextDouble();
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
        particle p= new particle(x, y, vx, vy, mass_p);// Create a particle object with the given parameters
        Black_Hole blackHole = new Black_Hole(mass);
        p.updateAcceleration(blackHole);// Update initial acceleration based on the black hole's gravity
        double schwarzschildRadius = blackHole.getSchwarzschildRadius();
        double gravitationalAcceleration = blackHole.gravitationalAcceleration(distance);
        double escapeVelocity = blackHole.escapeVelocity(distance);
        double gravitationalForce = p.gravitationalForce(blackHole);
        double gravitationalAcceleration_P = p.gravitationalAcceleration(blackHole);
        if (distance <= schwarzschildRadius) {
            System.out.println("The particle is already inside the event horizon of the black hole.");
            sc.close();
            return;
        }
        System.out.println("Schwarzschild Radius: " + schwarzschildRadius + " meters");
        System.out.println("Gravitational Acceleration: " + gravitationalAcceleration + " m/s^2");
        System.out.println("Escape Velocity: " + escapeVelocity + " m/s");
        System.out.println("Gravitational Force on particle: " + gravitationalForce + " N");
        System.out.println("Gravitational Acceleration (of particle): " + gravitationalAcceleration_P + " m/s^2");
        System.out.println("Simulating motion of particle for " + time + " seconds with time step of " + dt + " seconds...");
        int steps = (int) (time / dt);// Calculate the number of steps based on total time and time step
        // Simulate the motion of the particle over the specified time period
        for (int i = 0; i < steps; i++) {
            double old_x=p.x;
            double old_y=p.y;// Store the old position of the particle before updating its motion
            p.update_Motion(blackHole, dt);
            double new_x=p.x;
            double new_y=p.y;// Store the new position of the particle after updating its motion
            double dx=new_x - old_x;
            double dy=new_y - old_y;// Calculate the change in position of the particle
            double change_in_distance_square = (dx * dx) + (dy * dy);//to determine closest approach to the black hole
            double closest_t= -(old_x * dx + old_y * dy) / change_in_distance_square; //fraction of the motion where the closest approach occurs
            if(closest_t< 0) closest_t=0;
            if(closest_t> 1) closest_t=1;// 0<= closest_t <= 1, to ensure the point of closest approach is within the segment of motion
            double closest_x=old_x + closest_t * dx;
            double closest_y=old_y + closest_t * dy;
            double closest_distance= Math.sqrt(closest_x*closest_x+closest_y*closest_y);
            if (closest_distance <= schwarzschildRadius) {
                System.out.println("Particle has crossed the event horizon between "+(i*dt)+" and "+((i+1)*dt)+" seconds.");
                sc.close();
                return;
            }
        }
        System.out.println("Final position of particle: (" + p.x + ", " + p.y + ")");
        System.out.println("Final velocity of particle: (" + p.vx + ", " + p.vy + ")");
        sc.close();
    }
}