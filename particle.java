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
    public double gravitationalForce(Black_Hole bh) {
        double r = Math.sqrt((x * x) + (y * y));
        double force = (Black_Hole.G * mass * bh.getMass() * Black_Hole.SOLAR_MASS) / (r * r);
        return force;
    }
    public double gravitationalAcceleration(Black_Hole bh) {
        double r = Math.sqrt((x * x) + (y * y));
        double acceleration = -(Black_Hole.G * bh.getMass() * Black_Hole.SOLAR_MASS) / (r * r);
        return acceleration;
    }
    public void updateAcceleration(Black_Hole bh) {
            double r = Math.sqrt((x * x) + (y * y));
            ax = -(Black_Hole.G * bh.getMass() * Black_Hole.SOLAR_MASS * x) / (r * r * r);
            ay = -(Black_Hole.G * bh.getMass() * Black_Hole.SOLAR_MASS * y) / (r * r * r);
    }
  public void update_Motion(Black_Hole bh, double dt) {
       double old_ax = ax;
       double old_ay = ay;
       // Update position using current velocity and acceleration
         x += vx * dt + 0.5 * old_ax * dt * dt;// x=x + vx * dt + 0.5 * old_ax * dt * dt
         y += vy * dt + 0.5 * old_ay * dt * dt;// y=y + vy*dt + 0.5*old_ax*dt*dt
       // Update acceleration based on new position
       updateAcceleration(bh);
         // Update velocity using average of old and new acceleration
         vx += 0.5 * (old_ax + ax) * dt;
         vy += 0.5 * (old_ay + ay) * dt;
    }
}
