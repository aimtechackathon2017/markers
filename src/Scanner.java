import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

public class Scanner {
    private Session session = null;
    private Tracker tracker = null;
    private Moving moving = null;
    private ALTextToSpeech textToSpeech = null;

    public Scanner(Session session, Tracker tracker, Moving moving, ALTextToSpeech textToSpeech) {
        this.session = session;
        this.tracker = tracker;
        this.moving = moving;
        this.textToSpeech = textToSpeech;
    }

    public void run()
    {
        this.tracker.markerFound = false;
        this.tracker.markerAngle = -10f;

        try {
//            this.textToSpeech.say("Skenuji.");
            System.out.println("Scanning");
            this.tracker.run();
            this.moving.scanHorizontByHead();
            this.tracker.clean();

            if (!this.tracker.markerFound)
            {
                this.moving.turnLeft(90);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
