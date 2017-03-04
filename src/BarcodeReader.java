import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALBarcodeReader;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

import java.util.ArrayList;

public class BarcodeReader {
    private Session session;

    public BarcodeReader(Session session)
    {
        this.session = session;
    }

    public void run() throws Exception
    {
        ALMemory memory;
        ALTextToSpeech tts = new ALTextToSpeech(this.session);
        long barcodeSubscribeId = 0;

//        ALBarcodeReader alBarcodeReader = new ALBarcodeReader(this.session);
//        System.out.println(alBarcodeReader.getMethodList());

        memory = new ALMemory(this.session);
        barcodeSubscribeId = memory.subscribeToEvent("BarcodeReader/BarcodeDetected", new EventCallback<ArrayList>() {
            @Override
            public void onEvent(ArrayList arg0) throws InterruptedException, CallError {
                // 1 means the sensor has been pressed
                tts.say("Vidím QR kód.");
                System.out.println("Event fired!");
                System.out.println(arg0);
            }
        });
//        Thread.sleep(20000);
//        memory.unsubscribeToEvent(barcodeSubscribeId);
    }
}
