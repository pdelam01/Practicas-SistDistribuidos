/**
 * *****************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2012 José María Foces Morán,
 * instructor.
 *
 * Transfers.java
 *
 * SINGLE RESPONSIBILITY OF THIS CLASS: Static utility methods for java object
 * i/o with extended semnatics such as output flushing, etc. used across the
 * whole C/S project
 *
 *****************************************************************************
 */
package asdfileservice.transfers;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chema
 */
public class Transfers {

    public static String protoReceiveString(ObjectInputStream input) {

        String r = null;

        try {
            r = input.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;
    }

    public static void protoSendString(ObjectOutputStream output, String str) {


        try {
            output.writeUTF(str);
            output.flush();
        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public static long protoReceiveLong(ObjectInputStream input) {

        long r = 0;

        try {
            r = input.readLong();
        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;
    }

    public static void protoSendLong(ObjectOutputStream output, long x) {

        try {
            output.writeLong(x);
        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void protoReceiveBytes(ObjectInputStream input, byte[] b) {

        try {

            input.readFully(b);

        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public static boolean protoSendBytes(ObjectOutputStream output, byte[] b) {
        boolean r = false;

        try {

            output.write(b);
            output.flush();

            r = true;

        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;
    }

    public static boolean handleLocalFileWrite(String fileName, byte[] b) {
        boolean r = false;

        try {

            FileOutputStream fos = new FileOutputStream(fileName);

            fos.write(b);
            fos.close();

            r = true;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;
    }

    public static byte[] handleLocalFileRead(String fileName) {

        byte[] b = null;

        File f = new File(fileName);

        if (f.exists()) {

            long size = f.length();
            b = new byte[(int) size];

            try {

                FileInputStream fis = new FileInputStream(fileName);
                fis.read(b);

                fis.close();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return b;
    }

    public static void handleProtocolError(ObjectInputStream input, ObjectOutputStream output) {

        try {
            input.close();
            output.close();
            System.err.print("SERVER: Unknown protocol command received");
            System.err.println("CommandDispatcher returning");

        } catch (IOException ex) {
            Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void periodicKeepAlive() {
        int i = 0;

        /*
         * Keep this thread running for 20 * 5000 ms = 100 sec, keepalive
         * message will be printed every 5 sec
         */

        while (i < 20) {

            System.out.println("This is worker thread #" + Thread.currentThread().getId());
            System.out.flush();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Transfers.class.getName()).log(Level.SEVERE, null, ex);
            }

            i = i + 1;

        }


    }
}
