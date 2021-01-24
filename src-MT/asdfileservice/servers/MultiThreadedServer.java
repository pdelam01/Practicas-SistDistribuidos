/**
 * *****************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2014 José María Foces Morán,
 * instructor.
 *
 * MultiThreadedServer.java
 *
 * SINGLE RESPONSIBILITY OF THIS CLASS:
 * Using a ServerSocket has a single instance of RequestResponseHandler run on a
 * new Thread and waits for it to invoke notify() when it hase completed a new
 * TCP connection, then it proceeds to create a new thread, pass it the 
 * ServerSocket instance, etc. The RequestResponseHandler instance is
 * responsible for processing of the C/S RR protocol
 *
 *****************************************************************************
 */
package asdfileservice.servers;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadedServer implements Server {

    private ServerSocket welcomeSocket = null; //The welcome socket
    private File fileStorageBaseDir = null;

    public MultiThreadedServer(int port, File fileStorageBaseDir) {

        this.fileStorageBaseDir = fileStorageBaseDir;

        /*
         * Create a new ServerSocket instance on the specified port
         */
        try {

            welcomeSocket = new ServerSocket(port);

        } catch (IOException ex) {
            Logger.getLogger(ASDFileServerFactory.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }

    }


    public void serverMainLoop() {
        /* 
         * The object on which we will be delegating the low level protocol 
         * handling and the resulting Request/Responses
         *
         * This instance of RequestResponseHandler is the single instance used
         * across all the successive clients: We are using a SINGLE OBJECT
         * MULTITHREADING model
         */
        RequestResponseHandler rs = new RequestResponseHandler(welcomeSocket, fileStorageBaseDir);

        while (true) {

            Thread t = new Thread(rs);

            System.out.println("\t· Next upcoming client thread created: " + t.getName());
            System.out.flush();

            t.start();

            System.out.println("\t· Server is waiting for a new client's connection to complete");
            System.out.flush();

            /*
            * Wait for a notification coming from the single-instance of RequestResponseHandler that
            * is being run by thread t, when that notification arrives, we are sure that a new TCP
            * connection was completed, then the loop iterates again which causes a new thread to be
            * created and started which effectively translates into servicing a newly arrived client
            */
            synchronized (rs) {
                try {
                    rs.wait();
                    System.out.println("\t· Server has been notified client " + t.getName() + " has established a new connection");
                    System.out.flush();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MultiThreadedServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

    }

}
