import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.proxies.ALPhotoCapture;
import com.aldebaran.qi.helper.proxies.ALVideoDevice;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;

import java.awt.color.ColorSpace;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;


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
    public static final String CHAR_SET = "UTF-8"; // or "ISO-8859-1"
    Map<EncodeHintType, ErrorCorrectionLevel> hintMap;

    private static final int HEIGHT = 960;//240;//480;
    private static final int WIDTH = 1280;//320;//640;

    private final int topCamera = 0;
    private final int resolution = 3;//320 x240 // 640 x 480
    private final int colorspace = 	11;//11 // RGB
    private final int frameRate = 5; // FPS

    private JLabel label;

    private boolean run;

    public VideoCapturing(Session session) {
        this.session = session;
        hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        run = true;
        addListener(content -> {
            if (content != null) {
                System.out.println("QRCode:" + content);
            }
        });
    }

    public void stop() {
        this.run = false;
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
            while (run){
                image = getVideo();
                label.setIcon(new ImageIcon(image));
                try {
                    /*File outputfile = new File("images/" + System.currentTimeMillis() + "image.jpg");
                    ImageIO.write(myImage, "jpg", outputfile);*/

                    //String qrCode = readQRCode(outputfile.getPath(),CHAR_SET,hintMap);
                    String qrCode = readQRCode(image,CHAR_SET,hintMap);
                    if (qrCode != null) {
                        //System.out.println("QRCode:" + qrCode);
                    }
                    //new Thread(()->{
                        callListeners(qrCode);
                    //}).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<OnQRListener> listenerList = new ArrayList<>();

    public void addListener(OnQRListener listener) {
        this.listenerList.add(listener);
    }

    public interface OnQRListener {
        void onQRScanned(String content);
    }

    private void callListeners(String content) {
        for (OnQRListener listener : listenerList) {
            listener.onQRScanned(content);
        }
    }

    public BufferedImage getVideo() throws Exception {
        java.util.List<Object> image = (java.util.List<Object>) video.getImageRemote(moduleName);
        ByteBuffer buffer = (ByteBuffer)image.get(6);
        byte[] rawData = buffer.array();
        BufferedImage myImage = createRGBImage(rawData, WIDTH, HEIGHT);



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

    public static String readQRCode(BufferedImage bufferedImage, String charSet, Map hintMap) {
        try {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(bufferedImage)));
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
            return qrCodeResult.getText();

           // BinaryBitmap binaryBitmap1 = new BinaryBitmap(new GlobalHistogramBinarizer())
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }


    }


    public static String readQRCode(String filePath, String charset, Map hintMap)
            throws FileNotFoundException, IOException, NotFoundException {
        try {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
            return qrCodeResult.getText();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return "Cannot decode Barcode";

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
