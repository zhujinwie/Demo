package zsy.jt.com.cct2.utils;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LogThread extends Thread {

    String filePath ="/sdcard/cct_ylpd_log.txt"; // Log文件路径

    @Override
    public void run() {
        super.run();
        FileOutputStream os = null;
        try{
            Process p = Runtime.getRuntime().exec("logcat -v threadtime");
            final InputStream is = p.getInputStream();
           try {
               os = new FileOutputStream(filePath);
               int len = 0;
               byte[] buf = new byte[1024];
               while (-1 != (len = is.read(buf))) {
                   os.write(buf, 0, len);
                   os.flush();
               }
           }catch (Exception e){
                e.printStackTrace();
           }finally {
                if( null != os){
                    try{
                        os.close();
                        os = null;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
           }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
