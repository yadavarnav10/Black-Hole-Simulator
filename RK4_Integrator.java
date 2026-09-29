// Runge-Kutta 4th order integrator for relativistic particle simulation 
public class RK4_Integrator
{
    // Adds a scaled derivative to a state.
    // Used to calculate intermediate RK4 states.
    private static State addScaled(State a, State b, double scale) // result= a + scale * b
    {
        State result = new State();

        result.t = a.t + scale * b.t;
        result.r = a.r + scale * b.r;
        result.phi = a.phi + scale * b.phi;

        result.ut = a.ut + scale * b.ut;
        result.ur = a.ur + scale * b.ur;
        result.uphi = a.uphi + scale * b.uphi;

        return result;
    }

    // Advances the particle's state by one RK4 timestep.
    public static State step(State s, double M, double h) // s: current state, M: mass of the central object, h: timestep
    {
        // First slope: derivatives at the current state
        State k1 = RelativisticSim.derivatives(s, M);

        // Second slope: derivatives at the midpoint using k1
        State k2 = RelativisticSim.derivatives(
            addScaled(s, k1, h / 2.0), M);

        // Third slope: derivatives at the midpoint using k2
        State k3 = RelativisticSim.derivatives(
            addScaled(s, k2, h / 2.0), M);

        // Fourth slope: derivatives at the endpoint using k3
        State k4 = RelativisticSim.derivatives(
            addScaled(s, k3, h), M);

        // Combine the four slopes to calculate the new state
        // y(t + h) = y(t) + (h/6)(k1 + 2*k2 + 2*k3 + k4)
        State next = new State();

        next.t = s.t + (h / 6.0) *
            (k1.t + 2*k2.t + 2*k3.t + k4.t);

        next.r = s.r + (h / 6.0) *
            (k1.r + 2*k2.r + 2*k3.r + k4.r);

        next.phi = s.phi + (h / 6.0) *
            (k1.phi + 2*k2.phi + 2*k3.phi + k4.phi);

        next.ut = s.ut + (h / 6.0) *
            (k1.ut + 2*k2.ut + 2*k3.ut + k4.ut);

        next.ur = s.ur + (h / 6.0) *
            (k1.ur + 2*k2.ur + 2*k3.ur + k4.ur);

        next.uphi = s.uphi + (h / 6.0) *
            (k1.uphi + 2*k2.uphi + 2*k3.uphi + k4.uphi);

        return next;
    }
}
