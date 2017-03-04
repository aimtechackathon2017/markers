import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

public class MainApp{
   static ALTextToSpeech tts;

  /* public static void work(State state, Moving mov) throws InterruptedException, CallError {
        state.found = false;
        while (!state.found) {
            mov.scanHorizontByHead();
        }
        work(state,mov);
    }*/
    public static void main(String[] args) throws Exception {

        String robotUrl = "tcp://192.168.90.99:9559";
        Application application = new Application(args, robotUrl);
        application.start();
        tts = new ALTextToSpeech(application.session());

        try {
            ALAutonomousLife autonomousLife = new ALAutonomousLife(application.session());
            autonomousLife.stopAll();

            ALMotion motion = new ALMotion(application.session());
            motion.wakeUp();


            Moving mov = new Moving(application);

           /* BarcodeReader barcodeReader = new BarcodeReader(application.session());
            barcodeReader.run();*/

           /* VideoCapturing capturing = new VideoCapturing(application.session());
            Thread video = new Thread(capturing);
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
