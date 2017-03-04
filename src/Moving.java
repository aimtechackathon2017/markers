import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.helper.proxies.ALVisualSpaceHistory;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;

/**
 * Created by Malkol on 4.3.2017.
 */
public class Moving {

    Application application;
    ALRobotPosture posture;
    ALMotion motion;

    float LEFT_MAX = 2.0857f;
    float RIGHT_MAX = -2.0857f;
    float UP_MIN=0.330041f;
    float UP_CENTER=0f;
    float UP_MAX=-0.449073f;
    float UP_STEP = .3f;
    float scanTime=3f;


    float headPositionTop=0;
    float headPositionLeft=LEFT_MAX;

    ALTextToSpeech tts;
    ALVisualSpaceHistory vsh;



    public Moving(Application application) {
        this.application = application;
         posture = null;


        try {
            tts = new ALTextToSpeech(application.session());
            vsh = new ALVisualSpaceHistory(application.session());

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            this.posture = new ALRobotPosture(application.session());
            this.motion = new ALMotion(application.session());


        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    public void sedniSi(){
        try {
            posture.goToPosture("Sit",1f);
        } catch (CallError callError) {
            callError.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void moveToPosition(float time, boolean vertical){
        System.out.println("moving to "+headPositionTop+ "   "+headPositionLeft);




        try {
            motion.setStiffnesses("Head", 1.0f);
            if(!vertical){
                ArrayList<Float> top = new ArrayList<>();
                top.add(new Float(headPositionLeft));
                ArrayList<Float> topT = new ArrayList<>();
                topT.add(new Float(time));
                motion.angleInterpolation("HeadYaw",top,topT,true);
            }
            else {
                ArrayList<Float> left = new ArrayList<>();
                left.add(new Float(headPositionTop));
                ArrayList<Float> leftT = new ArrayList<>();
                leftT.add(new Float(time));
                motion.angleInterpolation("HeadPitch",left,leftT,true);
            }

        } catch (CallError callError) {
            callError.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }





    public void scanHorizontByHead(){

            headPositionTop=UP_CENTER;
            headPositionLeft=RIGHT_MAX;
            moveToPosition(scanTime,false);
            headPositionLeft=LEFT_MAX;
            moveToPosition(scanTime,false);

    }


}
