import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;

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
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


       // mov.sedniSi();

     

       /* try {
            BarcodeReader barcodeReader = new BarcodeReader(application.session());
            barcodeReader.run();

//            TouchSubscriber touchSubscriber = new TouchSubscriber(application.session());
//            touchSubscriber.run();

            application.run();
        } catch (Exception e) {
            e.printStackTrace();
        }*/


    }
}
