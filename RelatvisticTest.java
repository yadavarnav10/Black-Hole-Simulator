public class RelatvisticTest {
    public static void main(String[] args) {
        double M = 1.0; // Mass of the central object
        double h = 0.00001; // Timestep
        int steps = 20000*50; // Number of steps to simulate
        State s = new State(); // Initial state
        s.t = 0.0;
        s.r = 6.0; // Initial radial position
        s.phi = 0.0; // Initial angular position
        s.ur = 0.07; // Initial radial component of four-velocity
        s.uphi = 0.04;//Math.sqrt(M / Math.pow(s.r, 3))/ Math.sqrt(1.0 - 3.0 * M / s.r);; // Initial angular component of four-velocity
        double f = 1.0 - 2.0 * M / s.r;
        s.ut = Math.sqrt((1.0 + s.ur * s.ur / f + s.uphi * s.uphi * s.r * s.r ) / f); // Calculate the time component of the four-velocity
        System.out.println("Initial Specific energy: " + f * s.ut);
        System.out.println("Initial Specific angular momentum: " + s.r * s.r * s.uphi);
        // Simulate the motion of the particle
        for (int i = 0; i < steps; i++) {
            s = RK4_Integrator.step(s, M, h);
            if (i % 1000 == 0) {
                f = 1.0 - 2.0 * M / s.r;
             System.out.println("Step: " + i + "  r: " + s.r + "  ur: " + s.ur +"  phi: " + s.phi+" 4 velocity Normalization: " + (s.ut * s.ut * f - s.ur * s.ur / f - s.uphi * s.uphi * s.r * s.r));
            }
            
        }
        f = 1.0 - 2.0 * M / s.r;
         double E = f * s.ut;
        double L = s.r * s.r * s.uphi;// Calculate specific energy and angular momentum to verify conservation
        // Output the final state after simulation
        System.out.println("Final state after " + steps + " steps:");
        System.out.println("Final coordinate time: " + s.t);
        System.out.println("Final radius: " + s.r);
        System.out.println("Final angle: " + s.phi);
        System.out.println("Final radial velocity: " + s.ur);
        System.out.println("Final time velocity: " + s.ut);
        System.out.println("Final angular velocity: " + s.uphi);
        System.out.println("Final Specific energy: " + E);
        System.out.println("Final Specific angular momentum: " + L);
    }
}
