import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALMotion;
import com.aldebaran.qi.helper.proxies.ALRobotPosture;

import java.util.ArrayList;

/**
 * Created by Malkol on 4.3.2017.
 */
public class HeadMover{
    ALMotion motion;
    Application application;
    public HeadMover(Application app) throws Exception {

        this.motion = new ALMotion(app.session());
        this.application=app;
    }





}
