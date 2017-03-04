import com.aldebaran.qi.Application;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

public class MainApp {
    public static void main(String[] args) {
        String robotUrl = "tcp://192.168.90.99:9559";
        float maxFractionSpeed = 0.8f;

        Application application = new Application(args, robotUrl);
        application.start();
        try {
            ALAutonomousLife alAutonomousLife = new ALAutonomousLife(application.session());
            alAutonomousLife.stopAll();

            ALMotion alMotion = new ALMotion(application.session());
            alMotion.wakeUp();

//            ALRobotPosture alRobotPosture = new ALRobotPosture(application.session());
//            alRobotPosture.goToPosture("StandInit", maxFractionSpeed);

//            BarcodeReader barcodeReader = new BarcodeReader(application.session());
//            barcodeReader.run();

            BlobTracker blobTracker = new BlobTracker(application.session());
            blobTracker.run();

//            TouchSubscriber touchSubscriber = new TouchSubscriber(application.session());
//            touchSubscriber.run();

            Thread.sleep(20000);
//            application.run();
//            alRobotPosture.goToPosture("Sit", maxFractionSpeed);
            alMotion.rest();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
