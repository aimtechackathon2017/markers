import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;

public class MainApp {

    private static ALTextToSpeech tts;


    public static void main(String[] args) throws Exception {
        String robotUrl = "tcp://192.168.90.99:9559";
        Application application = new Application(args, robotUrl);
        application.start();
        tts = new ALTextToSpeech(application.session());

        ALAutonomousLife autonomousLife = new ALAutonomousLife(application.session());
        autonomousLife.stopAll();

        Moving mov = new Moving(application);
      //  mov.motion.up
      //  tts.say("Říká maminka: Pepíčku, co děláš? Pepíček odpoví: Ale jen zatloukám hřebík. Tak ať se nebouchneš do prstu. Šílíš?!! Nejsem blbej, drží mi ho Alenka.");
        //mov.sedniSi();
       mov.lookDown();
       Thread.sleep(5000);
        while(true){
            mov.scanHorizontByHead();
            //tts.say("proskenováno");
        }


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
