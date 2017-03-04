import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALColorBlobDetection;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.helper.proxies.ALTracker;

import java.util.ArrayList;

public class BlobTracker {
    private Session session;

    public BlobTracker(Session session) {
        this.session = session;
    }

    public void run() throws Exception
    {
        ALTracker tracker = new ALTracker(this.session);
        ALTextToSpeech tts = new ALTextToSpeech(this.session);

        ALColorBlobDetection alColorBlobDetection = new ALColorBlobDetection(this.session);
        alColorBlobDetection.setColor(255, 0, 0, 70);
//        alColorBlobDetection.setObjectProperties(10, .05f, "Unknown");
        alColorBlobDetection.subscribe("RedBlobSuscribe");

        tracker.setMode("Move");
        tracker.trackEvent("ALTracker/ColorBlobDetected");

        ALMemory alMemory = new ALMemory(this.session);
        long barcodeSubscribeId = 0;
        barcodeSubscribeId = alMemory.subscribeToEvent("ALTracker/ColorBlobDetected", new EventCallback<ArrayList>() {
            @Override
            public void onEvent(ArrayList arg0) throws InterruptedException, CallError {
                // 1 means the sensor has been pressed
                tts.say("Vidím červenou.");
                System.out.println("Event fired!");
                System.out.println(arg0);
            }
        });
    }
}
