/**
 * ***************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2014 José María Foces Morán,
 * instructor.
 *
 * RequestResponseHandler.java
 *
 * SINGLE RESPONSIBILITY OF THIS CLASS: Concurrently manage each successive
 * upcoming client connection
 * 
 * Wrap the delegate socket obtained from the Welcome Socket into
 * an ObjectOutputStream and an InputObjectStream and hand them to a Controller
 * object
 *
 * By using the delegate socket resulting from each successive TCP connection completion,
 * it creates the downstream and the upstream and calls a new command 
 * dispatcher to take care of the specific client commands received
 * ****************************************************************************
 */
package asdfileservice.servers;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestResponseHandler implements Runnable {

    ServerSocket welcomeSocket = null;
    File fileStorageBaseDir = null;


    public RequestResponseHandler(ServerSocket welcomeSocket, File fileStorageBaseDir) {

        this.welcomeSocket = welcomeSocket;
        this.fileStorageBaseDir = fileStorageBaseDir;

    }

    /*
     * 
     * The object i/o streams are created wrapping the delegate socket, all is
     * ready for performing the application level protocol dialog with the 
     * client. The client-server protocol is made up of messages that have been
     * established by the protocol/software designer. We refer to those messages
     * as commands (protocol commands). The CommandDispatcher instance will 
     * receive commands from the client by using object ois and send the
     * responses by using object oos
     * 
     */
    public void run() {

        Socket delegateSocket = null;
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;

        try {

            synchronized(this){
                delegateSocket = welcomeSocket.accept();
                notify();                
            }
                        
            ois = new ObjectInputStream(delegateSocket.getInputStream());
            oos = new ObjectOutputStream(delegateSocket.getOutputStream());

        } catch (IOException ex) {
            Logger.getLogger(RequestResponseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Controller().dispatch(ois, oos, fileStorageBaseDir);

    }
}
