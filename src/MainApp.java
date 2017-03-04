import com.aldebaran.qi.Application;

public class MainApp {
    public static void main(String[] args) {
        String robotUrl = "tcp://192.168.90.99:9559";
        Application application = new Application(args, robotUrl);
        application.start();
        try {
            BarcodeReader barcodeReader = new BarcodeReader(application.session());
            barcodeReader.run();

//            TouchSubscriber touchSubscriber = new TouchSubscriber(application.session());
//            touchSubscriber.run();

            application.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
