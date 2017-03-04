import java.util.ArrayList;
import java.util.List;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.aldebaran.qi.helper.proxies.ALVisualSpaceHistory;

/**
 * Created by Malkol on 4.3.2017.
 */
public class Moving {

    Application application;
    ALRobotPosture posture;
    public ALMotion motion;

    float DIAMETR=2f;//2.0857f;
    float LEFT_MAX =DIAMETR;
    float RIGHT_MAX = -DIAMETR;
    float UP_MIN=0.330041f;
    float UP_CENTER=0f;
    float UP_MAX=-0.449073f;
    float UP_STEP = .3f;
    float scanTime=0.6f;


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


    public void sitDown(){
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
            motion.setStiffnesses("HeadYaw", 1.0f);
            motion.setStiffnesses("HeadPitch", 1.0f);
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
            motion.setStiffnesses("HeadPitch", 0.0f);
            motion.setStiffnesses("HeadYaw", 0.0f);


        } catch (CallError callError) {
            callError.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void lookDown(){
        headPositionTop = UP_MIN;
        headPositionLeft = 0;
        moveToPosition(scanTime,true);

        // moveToPosition(scanTime,false);

    }



    public void scanHorizontByHead(){


        headPositionTop=UP_MAX;
        headPositionLeft=RIGHT_MAX;
        int div = 5;

        try {

            float divF = Math.abs(RIGHT_MAX-LEFT_MAX)/div;
            for (int i = 0; i < div; i++) {
                headPositionLeft+=divF;
                moveToPosition(scanTime,false);
                Thread.sleep(500);
            }
            headPositionLeft=0;
            moveToPosition(scanTime,false);
/*


*/

        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }



    public void standUp()
    {
        try {
            motion.killAll();
            posture.goToPosture("Stand", 1.0f);

        } catch (CallError callError) {
            callError.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void crouch()
    {
        try {
            motion.killAll();
            posture.goToPosture("Crouch", 1.0f);

        } catch (CallError callError) {
            callError.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void turnLeft(float degrees)
    {
        float radians = (3.14f * degrees) / 180;
        try {
            motion.moveTo(0f,0f,radians);
        } catch (CallError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 	Walking forward
     *	@param x – velocity along X-axis, in meters per second. Use negative values for backward motion
     *	@param y – velocity along Y-axis, in meters per second. Use positive values to go to the left
     *	@param theta – velocity around Z-axis, in radians per second. Use negative values to turn clockwise.
     *
     */
    public void walk(float x, float y, float theta)
    {
        try {
            motion.moveTo(x, y, theta);
        } catch (CallError e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void handsUp()
    {
        List<String> waveJoints = new ArrayList<String>();
        waveJoints.add("RShoulderPitch");
        waveJoints.add("LShoulderPitch");

        ArrayList<Float> angleList=new ArrayList<Float>();
        angleList.add(0.0f);
        angleList.add(0.0f);

        try {
            motion.setAngles(waveJoints, angleList, 0.1f);
        } catch (CallError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void handsDown()
    {
        List<String> waveJoints = new ArrayList<String>();
        waveJoints.add("RShoulderPitch");
        waveJoints.add("LShoulderPitch");

        ArrayList<Float> angleList=new ArrayList<Float>();
        angleList.add(1.0f);
        angleList.add(1.0f);

        try {
            motion.setAngles(waveJoints, angleList, 0.1f);
        } catch (CallError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<Float> getHeadAngle() throws Exception
    {
        return this.motion.getAngles("HeadYaw", true);
    }
    

}