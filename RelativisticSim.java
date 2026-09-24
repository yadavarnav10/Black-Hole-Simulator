public class RelativisticSim 
{
    public static State derivatives(State s, double M) //function to calculate the derivatives of the state variables
    {
        State derivative = new State();
        derivative.t = s.ut;//dt/dtau = ut
        derivative.r = s.ur;//dr/dtau = ur
        derivative.phi = s.uphi;//dphi/dtau = uphi
        double f = 1.0 - (2.0 * M / s.r);//Schwarzschild metric factor f(r) = 1 - 2M/r
        //Geodesic equation for the time component u^t: du^t/dtau = -2M*u^t*u^r/(r^2*f)
        derivative.ut =  -(2.0 * M * s.ut * s.ur) / (s.r * s.r * f);
         // Radial geodesic equation:du^r/dτ = -M*f*(u^t)^2/r^2 + M*(u^r)^2/(r^2*f) + r*f*(u^phi)^2
        derivative.ur = -(M * f / (s.r * s.r)) * s.ut * s.ut + (M / (s.r * s.r * f)) * s.ur * s.ur + s.r * f * s.uphi * s.uphi;
        //Angular component of the geodesic equation: du^phi/dtau = -2u^r*u^phi/r
        derivative.uphi = -(2.0 / s.r) * s.ur * s.uphi;
        return derivative;
    }
}
