import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALNavigation;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.helper.proxies.ALTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracker {
    private Session session;
    private ALMemory memory;
    private ALTracker tracker = null;
    private String targetName = "";
    private long eventId = 0;
    private Moving moving = null;
    private ALTextToSpeech textToSpeech = null;
    public final float distanceOffset = 0.3f;
    public boolean markerFound = false;
    public float markerAngle = -10f;

    public Tracker(Session session, ALTextToSpeech textToSpeech) {
        this.session = session;
        this.textToSpeech = textToSpeech;

        try {
            this.tracker = new ALTracker(session);

            this.targetName = "RedBall";
            float size = 0.06f;
            String effector = "None";
            tracker.setEffector(effector);
            tracker.registerTarget(this.targetName, size);
            tracker.setMode("Head");

            Float[] positionsVals = new Float[]{-0.3f, -0.3f, 0f, 0.1f, 0.1f, 0.3f};
            List<Float> positionsVal = Arrays.asList(positionsVals);
            ArrayList<Float> positions = new ArrayList<Float>(positionsVal);

            tracker.setRelativePosition(positions);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setMoving(Moving moving)
    {
        this.moving = moving;
    }

    public void run() throws Exception
    {
        if (this.tracker == null)
        {
            return;
        }

//        this.moving.standUp();
//        this.moving.lookDown();

        this.memory = new ALMemory(this.session);
        this.eventId = memory.subscribeToEvent("ALTracker/TargetDetected", new EventCallback<ArrayList>() {
            @Override
            public void onEvent(ArrayList arg0) throws InterruptedException, CallError {
                if (markerFound)
                {
                    return;
                }

//                System.out.println(arg0);

                try {
                    clean();
                    List<Float> headAngle = moving.getHeadAngle();
                    if (headAngle.get(0) == markerAngle)
                    {
                        return;
                    }

                    System.out.println("Target Detected!");
//                    textToSpeech.say("Vidím značku.");

                    markerAngle = headAngle.get(0);
                    List<Float> targetDistance = tracker.getTargetPosition();
                    Float walkingDistance = targetDistance.get(0) - distanceOffset;
                    System.out.println("Distance: " + targetDistance.get(0));
                    if (walkingDistance >= 0f)
                    {
//                        textToSpeech.say("Jdu ke značce.");
                        markerFound = true;
                        moving.walk(0, 0, markerAngle);
                        moving.walk(targetDistance.get(0) - distanceOffset, 0, 0);
                        markerFound = false;
                        markerAngle = -10f;
                        run();
                    }
                    else
                    {
                        System.out.println("I am too close!");
//                        textToSpeech.say("Jsem moc blízko. Vzdálenost " + targetDistance.get(0));
                        markerFound = false;
                        markerAngle = -10f;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        this.tracker.track(this.targetName);

//        this.moving.scanHorizontByHead();
    }

    public void clean()
    {
        if (this.eventId != 0)
        {
            try {
                this.memory.unsubscribeToEvent(this.eventId);
                this.eventId = 0;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}