import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALAudioPlayer;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracker {
    private Session session;
    private ALMemory memory;
    private ALTracker tracker = null;
    private String targetName = "";
    private long eventId=0;
    private Moving moving = null;

    private int scanningCounter = 0;
    private int scanningCounterLimit = 3;
    public final float distanceOffset = 0.3f;
    State state;
    boolean foundMark = false;
    ALAudioPlayer audioPlayer;


    public Tracker(Session session, Moving moving, State f) {
        this.session = session;
        this.state =f;
        try {
            this.tracker = new ALTracker(session);
            this.moving = moving;

            this.targetName = "RedBall";
            float size = 0.06f;
            String effector = "None";
            tracker.setEffector(effector);
            tracker.registerTarget(this.targetName, size);
            tracker.setMode("Head");

            audioPlayer=new ALAudioPlayer(session);



            Float[] positionsVals = new Float[]{-0.3f, -0.3f, 0f, 0.1f, 0.1f, 0.3f};
            List<Float> positionsVal = Arrays.asList(positionsVals);
            ArrayList<Float> positions = new ArrayList<Float>(positionsVal);

            tracker.setRelativePosition(positions);

            Runnable scanning = new Runnable() {
                @Override
                public void run() {
                    while (true)
                    if(!foundMark){
                        System.out.println("skenuji");
                        moving.scanHorizontByHead();

                        if (scanningCounter++ >= scanningCounterLimit)
                        {
                            moving.turnLeft(90);
                            scanningCounter = 0;
                        }
                    }else{
                        try {
                            System.out.println("nehledam ");
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Thread thread= new Thread(scanning);
            thread.start();
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


        this.tracker.track(this.targetName);

        this.memory = new ALMemory(this.session);
        this.eventId = memory.subscribeToEvent("ALTracker/TargetDetected", new EventCallback<ArrayList>() {
            @Override
            public void onEvent(ArrayList arg0) throws InterruptedException, CallError {
                System.out.println("Target Detected!");
                System.out.println(arg0);
                scanningCounter = 0;

                try {
                    clean();
                    List<Float> headAngle = moving.getHeadAngle();
                    List<Float> targetDistance = tracker.getTargetPosition(2    );

                    //ArrayList<Float> targetDistance = (ArrayList<Float>)tracker.getRelativePosition();
                    Float walkingDistance = targetDistance.get(0) - distanceOffset;
                    if (walkingDistance >= 0.3f)
                    {
                        tracker.pointAt("RArm",targetDistance,0,1.0f);
                        if(MainApp.release)moving.tts.say("Hele, támhle je, mrška! Jdu " );
                        moving.tts.say(Math.floor(targetDistance.get(0) * 100.0) / 100.0 + " metrů.");
                        moving.walk(0, 0, headAngle.get(0));
                        foundMark=true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    audioPlayer.playFile("/tmp/t.mp3");
                                } catch (CallError callError) {
                                    callError.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        moving.walk(targetDistance.get(0) - distanceOffset, 0, 0);
                        foundMark=false;
                        run();
                    }else{
                        moving.tts.say("Jsem dost blízko.");
                        moving.tts.say( Math.floor(targetDistance.get(0) * 100.0) / 100.0 + " metrů.");

                        Thread.sleep(10000);
                        foundMark=false;
                        moving.turnLeft(90);
                        run();

                    }
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
        try {
            if(eventId!=0){
                this.memory.unsubscribeToEvent(this.eventId);
                eventId=0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}