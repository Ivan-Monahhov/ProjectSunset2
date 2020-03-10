/**
 * Created by Ivan on 7/4/2019.
 */
public class Loc
{
    boolean blackbox = false;
    double longitude;
    double lattitude;

    public String toString()
    {
        return "Blackbox "+blackbox + " at "+lattitude+ ":"+longitude;
    }
}
