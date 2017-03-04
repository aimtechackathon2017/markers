import com.aldebaran.qi.Application;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;

public class MainApp {
    public static void main(String[] args) {
        String robotUrl = "tcp://192.168.90.99:9559";
        Application application = new Application(args, robotUrl);
        application.start();

        Moving mov = new Moving(application);
       // mov.sedniSi();
        try {
            ALAutonomousLife autonomousLife = new ALAutonomousLife(application.session());
            autonomousLife.stopAll();

            ALMotion motion = new ALMotion(application.session());
            motion.wakeUp();

            Tracker tracker = new Tracker(application.session());

            Navigation navigation = new Navigation(application.session());
            navigation.setTracker(tracker);
            tracker.setNavigation(navigation);

            tracker.run();

//            BarcodeReader barcodeReader = new BarcodeReader(application.session());
//            barcodeReader.run();

//            TouchSubscriber touchSubscriber = new TouchSubscriber(application.session());
//            touchSubscriber.run();

            Thread.sleep(20000);

            tracker.clean();
//            application.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
