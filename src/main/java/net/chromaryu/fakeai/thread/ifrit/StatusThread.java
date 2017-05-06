package net.chromaryu.fakeai.thread.ifrit;

import net.chromaryu.fakeai.fakeai;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZ;
import org.tukaani.xz.XZOutputStream;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.TimerTask;

/**
 * Created by midgard on 17/04/28.
 */
public class StatusThread extends TimerTask{
    @Override
    public void run() {
        // Ready for Some Encryption.
        //StringBuilder sb = new StringBuilder();
        try {
            String msg = "msg:"+fakeai.atomicInteger.get()+ " requestMySQL:"+fakeai.respNum.get();
            byte[] enc = fakeai.cipher.update(msg.getBytes("UTF-8"));
            byte[] xzenc;
            try(ByteArrayOutputStream baos = new ByteArrayOutputStream();XZOutputStream xzos = new XZOutputStream(baos,new LZMA2Options(8), XZ.CHECK_SHA256)){
                xzos.write(Base64.getEncoder().encode(enc));
                xzos.flush();
                xzos.finish();
                xzenc = baos.toByteArray();
            }
            System.out.println(Base64.getEncoder().encodeToString(xzenc)); // Something.xz
            //fakeai.tw.sendDirectMessage("Chromaryu", Base64.getEncoder().encodeToString(enc));

            //sb.append(fakeai.tw.getRateLimitStatus().containsKey())
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
