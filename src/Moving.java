import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import java.util.ArrayList;

/**
 * Created by Malkol on 4.3.2017.
 */
public class Moving {

    Application application;
    ALRobotPosture posture;
    ALMotion motion;

    public Moving(Application application) {
        this.application = application;
         posture = null;
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
    public void moveHead() {
        //  posture.goToPosture("Sit",1.0f);

        // LLeg motion
        //http://doc.aldebaran.com/java/2-1/com/aldebaran/qi/helper/proxies/ALMotion.html
        //Name of the chain. Could be: "Head", "LArm", "RArm", "LLeg", "RLeg", "Torso"
        String effector   = "Head";
        int space       =  2;
        int axisMask    = 63; // control all the effector's axes
        boolean isAbsolute = false;

        //kterym smerem a jak rychle x,y,z, a Vx....

        ArrayList<Float> f = new ArrayList<Float>();
        f.add(new Float(0.0));
        f.add(new Float(0.00));
        f.add(new Float(0.0));
        f.add(new Float(0.0));
        f.add(new Float(0.0));
        f.add(new Float(0.8));


        float timeList   = 2.0f; // seconds
        try {
            motion.positionInterpolation(effector, space, f,
                    axisMask, timeList, isAbsolute);
        } catch (CallError callError) {
            callError.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
