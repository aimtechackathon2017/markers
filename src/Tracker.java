import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALNavigation;
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
    public final float distanceOffset = 0.8f;

    public Tracker(Session session, Moving moving) {
        this.session = session;

        try {
            this.tracker = new ALTracker(session);
            this.moving = moving;

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

    public void run() throws Exception
    {
        if (this.tracker == null)
        {
            return;
        }

        this.moving.standUp();
//        this.moving.lookDown();

        this.memory = new ALMemory(this.session);
        this.eventId = memory.subscribeToEvent("ALTracker/TargetDetected", new EventCallback<ArrayList>() {
            @Override
            public void onEvent(ArrayList arg0) throws InterruptedException, CallError {
                System.out.println("Target Detected!");
//                System.out.println(arg0);

                try {
                    clean();
                    List<Float> headAngle = moving.getHeadAngle();
                    List<Float> targetDistance = tracker.getTargetPosition();
                    Float walkingDistance = targetDistance.get(0) - distanceOffset;
                    System.out.println("Distance: " + targetDistance.get(0));
                    if (walkingDistance >= 1f)
                    {
                        moving.walk(0, 0, headAngle.get(0));
                        moving.walk(targetDistance.get(0) - distanceOffset, 0, 0);
                        run();
                    }
                    else
                    {
                        System.out.println("I am too close!");
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

    public void clean() throws Exception
    {
        if (this.eventId != 0)
        {
            this.memory.unsubscribeToEvent(this.eventId);
            this.eventId = 0;
        }
    }
}