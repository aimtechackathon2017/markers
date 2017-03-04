import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALNavigation;

public class Navigation {
    private Session session;
    private ALNavigation navigation = null;
    private Tracker tracker = null;

    public Navigation(Session session) {
        this.session = session;

        try {
            this.navigation = new ALNavigation(session);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setTracker(Tracker tracker)
    {
        this.tracker = tracker;
    }

    public void walkStraight() throws Exception
    {
        this.navigation.navigateTo(5f, 0f);
        if (this.tracker == null)
        {
            return;
        }

        this.tracker.run();
    }
}
