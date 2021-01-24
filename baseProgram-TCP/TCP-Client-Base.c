
/**
 *******************************************************************
 * All rights reserved (C) 2018-2019 by Jose Maria Foces Moran and
 * José María Foces Vivancos
 *
 * TCP-Client.c
 *
 * Stream-socket based client for basic TCP protocol analysis
 * Sends a string to a TCP server and checks the echo response
 * v 1.1.0
 *******************************************************************
 */

#include <stdio.h>
#include <stdlib.h>

#include <fcntl.h>
#include <memory.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>

#include <signal.h>
#include <errno.h>
#include <sys/time.h>

#define MAXRESP 1024
#define TRUE 1
#define FALSE 0
#define DEFAULTPORT 50001

int sendRequest(int sock, char *message, int nbytes){

    ssize_t n = write(sock, message, nbytes);

    return n;

}

int receiveResponse(int sock, char *message, int nbytes){

    ssize_t n = read(sock, message, nbytes);

    //Naïve attempt to turn message into a string
    message[nbytes] = '\0';

    return n;

}

void runCS_Protocol(int sock){

    int iterations=0;

    while(iterations<5){
        static char messageToSend[] = "Hello world :-)";

        sendRequest(sock, messageToSend, strlen(messageToSend));

        printf("Message to send:\n'%s'\n", messageToSend);
        fflush(stdout);

        //An additional byte for storing the null character
        char *received = malloc(MAXRESP+1);
        receiveResponse(sock, received, MAXRESP);

        printf("Message received:\n'%s'\n", received);
        
        sleep(5);

        fflush(stdout);
    }   

}

int connectToServer(int sock, char *ipAddress, short int port){

    //Socket address for server
    struct sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    server.sin_addr.s_addr = inet_addr(ipAddress);

    /*
     * Assumming the server's welcome socket is in the LISTEN state, the
     * following call will causes the local TCP/IP stack to initiate the
     * 3-way handshake by sending a SYN packet, wait for the ACK-SYN and
     * send the final ACK. From this moment on, the connection is in the
     * ESTABLISHED state.
     */
    int r = connect(sock, (struct sockaddr *) &server, sizeof (server));

    printf("Return value of connect() = %d\n", r);
    fflush(stdout);

    return r;

}

int createSocket(){

    //Create client socket of the STREAM TYPE (TCP)
    //Last parameter is always 0
    int sock = socket(AF_INET, SOCK_STREAM, 0);

    //Create a loacl socket-address for the client itself
    struct sockaddr_in client;
    client.sin_family = AF_INET;
    client.sin_port = INADDR_ANY;
    client.sin_addr.s_addr = INADDR_ANY;

    /* Bind a local address to the new socket
    * In the present stream socket case, this is binding is not
    * necessary since the upcoming to connect() will implicitly
    * accomplish the binding of a local address to the Socket
    */

    bind(sock, (struct sockaddr *) &client, sizeof (client));

    return sock;

}

void client(char *ip, int port){

    int sock = createSocket();

    int r;

        if ((r = connectToServer(sock, ip, port)) != 0){
            fprintf(stderr, "Connection to server failed\n");
            exit(r);
        };

    runCS_Protocol(sock);

    close(sock);

}

int main(int argc, char** argv){

    if (argc == 3){

        client(argv[1], atoi(argv[2]));

    }else{
        printf("$ client  <server ip address> <TCP port number>");
        printf("\nExiting.\n");
    }

}