/**
 * **************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2012 José María Foces Morán,
 * instructor.
 *
 * UploadClient.java
 *
 * REFERENCE SOLUTION to a simple FileUpload client to the File Server type 0
 *
 * This program is just a quick-and-dirty proof of concept, it should be
 * properly designed by using a a design process similar to that used for the
 * Server implementations belonging to this project
 *
 **************************************************************************
 */
package asdfileservice.clients;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadClient {

    private final static String COMMAND_DOWNLOAD = "Download byte array REQ";
    private final static String COMMAND_DOWNLOAD_ACK = "Download ACK";
    private final static String COMMAND_DOWNLOAD_NACK = "Download NACK";
    private final static String COMMAND_UPLOAD = "Upload byte array REQ";
    private final static String COMMAND_UPLOAD_ACK = "Upload ACK";
    private final static String COMMAND_UPLOAD_NACK = "Upload NACK";
    private final static String COMMAND_FILELIST = "Fetch File List REQ";
    private final static String COMMAND_FILELIST_ACK = "Fetch File List ACK";
    private final static String COMMAND_FILELIST_NACK = "Fetch File List NACK";
    private final static String COMMAND_SUCCESSFUL = "Command successful";
    private final static String COMMAND_NOT_SUCCESSFUL = "Command not successful";

    void start() {

        Socket s = null;

        try {

            /*
             * Make sure that the file exists and that it contains 20MB of data
             * for example
             * 
             */
            String localFn = "/tmp/prueba.txt";
            File file = new File(localFn);


            FileInputStream fis = new FileInputStream(localFn);

            //Create array b with file size
            byte b[] = new byte[(int) file.length()];
            //Request b.length bytes read from stream
            fis.read(b);

            /*
             * Light check of file contents
             */
            System.out.println("Client status: File length is " + b.length + " bytes");
            System.out.println("First 35 chars from file:");

            for (int i = 0; i < 35; i++) {
                System.out.print((char) b[i]);
                System.out.flush();
            }


            /*
             * Set proper IP or capture from command line
             */
            s = new Socket("127.0.0.1", 60001);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());



            oos.writeUTF(COMMAND_UPLOAD); //Send protocol "upload" operation
            oos.flush();

            String fn = "UPLOADED_FILE-PDLH.txt";
            oos.writeUTF(fn); //Send file name to use for uploaded file
            oos.flush();

            oos.writeLong(b.length); //Send file size
            oos.flush();

            System.out.println("Client status: Waiting for server's ok");
            System.out.flush();

            /*
             * Read the server's response to our file upload request:
             */
            ObjectInputStream ios = new ObjectInputStream(s.getInputStream());
            String resp = ios.readUTF();

            if (resp.compareTo(COMMAND_UPLOAD_ACK) != 0) {
                System.err.println("Request not accepted by server. Finishing.");
                System.exit(-2);
            }

            System.out.println("Client status: Upload accepted, sending " + b.length + " bytes");
            System.out.flush();


            oos.write(b); //b es el array. escribimos en el flujo de datos, vaciamos y leemos
            oos.flush();

            System.out.println("File " + fn + " transferred to server.");
            System.out.flush();


            resp = ios.readUTF();

            if (resp.compareToIgnoreCase(COMMAND_SUCCESSFUL) != 0) {
                System.err.println("Command not successful. Finishing.");
                System.exit(-2);
            }


            ios.close();
            oos.close();
            s.close();


        } catch (UnknownHostException ex) {
            Logger.getLogger(UploadClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UploadClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {

        new UploadClient().start();

    }
}
