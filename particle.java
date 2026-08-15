public class particle {
    double x,y, vx, vy, ax, ay, mass;// position, velocity, acceleration, mass of the particle
    public particle(double x, double y, double vx, double vy, double mass) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.ax = 0;
        this.ay = 0;
    }
    public double gravitationalForce(Black_Hole bh, double distance) {
        double force = (Black_Hole.G * mass * bh.getMass() * Black_Hole.SOLAR_MASS) / (distance * distance);
        return force;
    }
    public double gravitationalAcceleration(Black_Hole bh, double distance) {
        double acceleration = (Black_Hole.G * bh.getMass() * Black_Hole.SOLAR_MASS) / (distance * distance);
        return acceleration;
    }
}
