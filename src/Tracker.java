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
    private long eventId;
    private Navigation navigation = null;

    public Tracker(Session session) {
        this.session = session;

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

    public void setNavigation(Navigation navigation)
    {
        this.navigation = navigation;
    }

    public void run() throws Exception
    {
        if (this.tracker == null)
        {
            return;
        }

        this.tracker.track(this.targetName);

        this.memory = new ALMemory(this.session);
        this.eventId = memory.subscribeToEvent("ALTracker/TargetDetected", new EventCallback<ArrayList>() {
            @Override
            public void onEvent(ArrayList arg0) throws InterruptedException, CallError {
                System.out.println("Target Detected!");
//                System.out.println(arg0);

                try {
                    clean();
                    navigation.walkStraight();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void clean() throws Exception
    {
        this.memory.unsubscribeToEvent(this.eventId);
    }
}