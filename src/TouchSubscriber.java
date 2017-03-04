import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

public class TouchSubscriber {
    private Session session;

    public TouchSubscriber(Session session) {
        this.session = session;
    }

    public void run() throws Exception
    {
        ALMemory memory;
        ALTextToSpeech tts;
        memory = new ALMemory(session);
//        tts = new ALTextToSpeech(session);

        // Subscribe to FrontTactilTouched event,
        // create an EventCallback expecting a Float.
        final long frontTactilSubscriptionId = memory.subscribeToEvent(
                "FrontTactilTouched", new EventCallback<Float>() {
                    @Override
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        // 1 means the sensor has been pressed
                        if (arg0 > 0) {
                            System.out.println("FrontTactilTouched");
//                            tts.say("ouch!");
                        }
                    }
                });
        // Subscribe to RearTactilTouched event,
        // create an EventCallback expecting a Float.
        memory.subscribeToEvent("RearTactilTouched",
                new EventCallback<Float>() {
                    @Override
                    public void onEvent(Float arg0)
                            throws InterruptedException, CallError {
                        if (arg0 > 0) {
                            if (frontTactilSubscriptionId > 0) {
                                System.out.println("FrontTactilTouched");

//                                tts.say("I'll no longer say ouch");
                                // Unsubscribing from FrontTactilTouched event
                                memory.unsubscribeToEvent(frontTactilSubscriptionId);
                            }
                        }
                    }
                });
    }
}
