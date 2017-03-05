import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALAudioPlayer;
import com.aldebaran.qi.helper.proxies.ALAutonomousLife;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;


public class MainApp {


    static ALTextToSpeech tts;
    static String lastQr = "";
    static int number = 0;

    public static void main(String[] args) {


        String robotUrl = "tcp://192.168.90.99:9559";
        Application application = new Application(args, robotUrl);
        application.start();

        try {

            tts = new ALTextToSpeech(application.session());

            ALAutonomousLife autonomousLife = new ALAutonomousLife(application.session());
            autonomousLife.stopAll();

            ALMotion motion = new ALMotion(application.session());
            motion.wakeUp();


            //Moving mov = new Moving(application);

           /* BarcodeReader barcodeReader = new BarcodeReader(application.session());
            barcodeReader.run();*/

            VideoCapturing capturing = new VideoCapturing(application.session());
            Thread video = new Thread(capturing);

            capturing.addListener(new VideoCapturing.OnQRListener() {
                @Override
                public void onQRScanned(String content) {
                    try {
                        if (content != null) {
                            if(!content.equals(lastQr)){
                                if (number < 3) {
                                    tts.say("Načten qr kód:" + content);
                                    lastQr = content;
                                    number++;
                                    if (number == 3) {
                                        tts.say("Tak a je to");
                                        ALAudioPlayer player = new ALAudioPlayer(application.session());
                                        tts.say("Nyní poklekněte před vaším novým pánem");
                                        player.playFile("/tmp/hrom.mp3");

                                    }
                                }

                            }
                        }
                    } catch (CallError callError) {
                        callError.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            video.start();

           /* State state = new State();
            state.found=true;


            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Tracker tracker = new Tracker(application.session(), mov,state);
                    try {
                        tracker.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
*/


            //work(state,mov);


//            Thread.sleep(20000);
//            tracker.clean();

            while(true)
            {
                Thread.sleep(1000);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
