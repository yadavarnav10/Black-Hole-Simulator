public class Black_Hole {
    public static final double G = 6.67430e-11; // in m^3 kg^-1 s^-2
    public static final double c = 299792458; // in m/s
    public static final double SOLAR_MASS = 1.989e30; // in kg
    private double mass; // in kg
    public static final double conversion_factor = G / (c * c); // to convert mass to geometric units
    public static final double GEO_Solar_Mass = conversion_factor * SOLAR_MASS; // in geometric units

    public Black_Hole(double m) { //m is in solar masses
        mass = m;
    }

    public double getMass() {
        return mass;
    }
    public double getSchwarzschildRadius() {
        double schwarzschild_radius= (2*mass*SOLAR_MASS*G)/(c*c);
        return schwarzschild_radius;
    }
    public double gravitationalAcceleration(double r) {
        double acceleration = (G * mass * SOLAR_MASS) / (r * r);
        return acceleration;
    }
    public double escapeVelocity(double r) {
        double velocity = Math.sqrt((2 * G * mass * SOLAR_MASS) / r);
        return velocity;
    }
}
