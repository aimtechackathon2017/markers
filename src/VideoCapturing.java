import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALVideoDevice;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.List;
import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;


/**
 * Třída VideoCapturing
 * <p>
 * Created on 04.03.2017.
 *
 * @author Pavel Přibáň pribanp@students.zcu.cz
 */
public class VideoCapturing implements Runnable {

    private Session session;

    private ALVideoDevice video;

    private String moduleName;

    public static final String IMAGES_FOLDER = "images/";

    private static final int HEIGHT = 480;
    private static final int WIDTH = 640;

    private final int topCamera = 0;
    private final int resolution = 2; // 640 x 480
    private final int colorspace = 11; // RGB
    private final int frameRate = 20; // FPS

    private JLabel label;

    public VideoCapturing(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        createImageFolder();
        createGui();
        try {
            video  = new ALVideoDevice(session);
            moduleName = video.subscribeCamera("VideoDemo", topCamera, resolution, colorspace, frameRate);
            System.out.format("subscribed with id: %s", moduleName);

            BufferedImage image;
            for (int i = 0; i < 200; i++) {
                image = getVideo();
                label.setIcon(new ImageIcon(image));
            }

            release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BufferedImage getVideo() throws Exception {
        java.util.List<Object> image = (java.util.List<Object>) video.getImageRemote(moduleName);
        ByteBuffer buffer = (ByteBuffer)image.get(6);
        byte[] rawData = buffer.array();
        BufferedImage myImage = createRGBImage(rawData, WIDTH, HEIGHT);


        try {

            File outputfile = new File("images/" + System.currentTimeMillis() + "image.jpg");
            ImageIO.write(myImage, "jpg", outputfile);

            String charset = "UTF-8"; // or "ISO-8859-1"
            Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            /*try {
                System.out.println("Data read from QR Code: "
                        + readQRCode(outputfile.getPath(), charset, hintMap));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }



        return myImage;
    }

    private static BufferedImage createRGBImage(byte[] bytes, int width, int height) {
        DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
        ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        return new BufferedImage(cm, Raster.createInterleavedRaster(buffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null), false, null);
    }


    private void createImageFolder() {
        File f = new File(IMAGES_FOLDER);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    private void createGui() {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        label = new JLabel();
        label.setSize(WIDTH,HEIGHT);
        label.setMinimumSize(new Dimension(WIDTH,HEIGHT));
        label.setPreferredSize(new Dimension(WIDTH,HEIGHT));


        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                release();
                /*try {
                    notifyAll();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }*/

                e.getWindow().dispose();
            }
        });


//        while (run){
//            System.out.print("");
//        }
    }


    public void release() {
        try {
            video.unsubscribe(moduleName);

        } catch (CallError callError) {
            callError.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e1) {

        }
        System.out.println("Video subscribe released");
    }
}
