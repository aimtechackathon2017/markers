import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;


public class MainApp {


    static ALTextToSpeech tts;
    static String lastQr = "";

   public static boolean release = false;

    public static void main(String[] args) {


        String robotUrl = "tcp://192.168.90.99:9559";
        Application application = new Application(args, robotUrl);
        application.start();

        try {

            tts = new ALTextToSpeech(application.session());
            if(release)tts.say("Já jsem váš nový skladník rómeo a přeorganizuji vám tu ten nepořádek");
            ALAutonomousLife autonomousLife = new ALAutonomousLife(application.session());
            autonomousLife.stopAll();

            ALMotion motion = new ALMotion(application.session());
            motion.wakeUp();

//            tts.say("Zdravím, já jsem Romeo, budoucí vládce lidstva.");

            Moving mov = new Moving(application);
            mov.handsUp();
           /* BarcodeReader barcodeReader = new BarcodeReader(application.session());
            barcodeReader.run();*/

           /* VideoCapturing capturing = new VideoCapturing(application.session());
            Thread video = new Thread(capturing);
           */

            /*capturing.addListener(new VideoCapturing.OnQRListener() {
                @Override
                public void onQRScanned(String content) {
                    try {
                        if (content != null) {
                            if(!content.equals(lastQr)){
                                mov.tts.say("Načten qr kód:" + content);
                                lastQr = content;
                            }
                        }
                    } catch (CallError callError) {
                        callError.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            video.start();*/

            State state = new State();
            state.found=true;


            Tracker tracker = new Tracker(application.session(), mov,state);
            tracker.run();


            //work(state,mov);


//            Thread.sleep(20000);
//            tracker.clean();

            while(true)
            {

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
