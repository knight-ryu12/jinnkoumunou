package net.chromaryu.fakeai.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by midgard on 17/04/08.
 */
public class commandReader extends Thread {
    private BufferedReader buf;
    commandReader() {
        this.buf = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        try {
            String s = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
