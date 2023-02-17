package com.thf.junsunshell.utils;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import com.thf.junsunshell.R;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static final String TAG = "JunSUnShell";
    private static String su;

    public Util(String su) {
        Util.su = su;
    }

    public static class SuCommandException extends Exception {
        public SuCommandException(String message) {
            super(message);
        }
    }

    /*
    public static String sudoForResult(Context context, String command) throws SuCommandException {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;

        if (command == null || "".equals(command)) {
            throw new SuCommandException("Command is empty");
        }


        String suCmd = context.getString(R.string.su);
        try {
            Process su = Runtime.getRuntime().exec(suCmd);
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            outputStream.writeBytes(command + "\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                throw new SuCommandException("InterruptedException: " + e.getMessage());
            }
            //res = readFully(response);




        } catch (IOException e) {
            throw new SuCommandException("IOException: " + e.getMessage());
        } finally {
            // Closer.closeSilently(outputStream, response);
        }
        return res;
    }

    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }
    */

    public List<String> sudoForResult(String command) throws SuCommandException {

        if (command == null || "".equals(command)) {
            throw new SuCommandException("command is empty");
        }
        if (su == null || "".equals(su)) {
            throw new SuCommandException("su is not set");
        }

        List<String> output = new ArrayList<String>();

        class ReadLog implements Runnable {
            private BufferedReader br;

            ReadLog(BufferedReader br) {
                this.br = br;
            }

            @Override
            public void run() {
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        output.add(line);
                    }
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        }
        ;

        try {

            Process exec;

            exec = Runtime.getRuntime().exec(su);
            DataOutputStream dataOutputStream = new DataOutputStream(exec.getOutputStream());
            dataOutputStream.writeBytes(command + "\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            Thread.sleep(10);

            Thread t1 = new Thread(new ReadLog(in));
            t1.start();

            BufferedReader er = new BufferedReader(new InputStreamReader(exec.getErrorStream()));
            Thread.sleep(10);

            Thread t2 = new Thread(new ReadLog(er));
            t2.start();

            t1.join();
            t2.join();

            exec.waitFor();

        } catch (IOException ex) {
            throw new SuCommandException("IOException: " + ex.getMessage());
        } catch (InterruptedException e) {
            throw new SuCommandException("InterruptedException: " + e.getMessage());
        }

        return output;
    }
}
