import com.aldebaran.qi.Application;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

public class MainApp {
    public static void main(String[] args) {
        String robotUrl = "tcp://192.168.90.99:9559";
        Application application = new Application(args, robotUrl);
        application.start();

        try {
            ALAutonomousLife autonomousLife = new ALAutonomousLife(application.session());
            autonomousLife.stopAll();

            ALMotion motion = new ALMotion(application.session());
            motion.wakeUp();

            Moving mov = new Moving(application);
            mov.standUp();

            ALTextToSpeech textToSpeech = new ALTextToSpeech(application.session());

            Tracker tracker = new Tracker(application.session(), textToSpeech);
            tracker.setMoving(mov);

//            tracker.run();


//            Thread.sleep(20000);
//            tracker.clean();
            Scanner scanner = new Scanner(application.session(), tracker, mov, textToSpeech);
            while(true)
            {
                scanner.run();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
