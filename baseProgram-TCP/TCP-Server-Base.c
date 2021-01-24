/**
 *************************************************************************************
 * All rights reserved (C) 2018-2019 by Jose Maria Foces Moran and
 * José María Foces Vivancos
 *
 * TCP-Server.c
 *
 * Stream-socket based server for basic TCP protocol analysis
 * Server responds to requests sent by clientAddress
 *
 * v 1.3.2
 * · Improved the naming of a few functions nd variables
 * · Solved problem printing IP/Port of clientAddress the first time
 * accept() is called
 * · Added the capability of sending the date in a printable string
 * · Refactored some functions' signature seeking more self-consistency
 * ·
 *************************************************************************************
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
#include <time.h>

#define REQSHUTDOWN "Shutdown server"
#define REQDATE "Send the date"
#define RESPSHUTDOWN "Server is closing now"
#define RESPDATE "Server's date follows: "
#define RESPOTHER "Unknown request "

#define MAXREQSIZE 1024
#define MAXRESP 1024
#define TRUE 1
#define FALSE 0
#define DEFAULTPORT 50001

//For listen() socket function:
#define BACKLOG 10

int sendResponse(char* response, int delegateSocket) {

    /*
     * Use socket delegateSocket for sending the response to the clientAddress
     */
    int n = send(delegateSocket, response, strlen(response), 0);

    printf("%u bytes were sent to the clientAddress\n", n);

    return n;
}

char *getRequest(int delegateSocket) {

    static char request[MAXREQSIZE];
    int nbytes;

    nbytes = recv(delegateSocket, request, MAXREQSIZE, 0);

    /* Turn the request into a C string assumming the stream actually contains
     * ASCII characters only
     */
    request[nbytes] = (char) '\0';

    printf("Request received with a size of %u bytes:\n\t'%s'\n", nbytes, request);

    return request;
}

void computeResponse(char *response, const char *request) {

    if(strncmp(request, REQSHUTDOWN, strlen(REQSHUTDOWN)) == 0) {

        strcpy(response, RESPSHUTDOWN);

    } else if(strncmp(request, REQDATE, strlen(REQDATE)) == 0) {

        //Obtain the cureent wall-clock time in binary form
        time_t t = time(NULL);
        //Store the printable string version of t into response
        ctime_r(&t, response);

    } else {

        strcpy(response, RESPOTHER);

    }

}

int createConfigWelcomeSocket(int port) {

    /*
     * Create a TCP (stream) socket using the Internet address family
     */
    int welcomeSocket;
    welcomeSocket = socket(AF_INET, SOCK_STREAM, 0); //Last arg is always 0

    /*
     * Create a socket address for the stream socket
     */
    struct sockaddr_in wcreatecreateConfigWelcomeSocket;

    //Clear the contents of wcreatecreateConfigWelcomeSocket
    bzero(&wcreatecreateConfigWelcomeSocket, sizeof (wcreatecreateConfigWelcomeSocket));

    //Set the socket address family equal to Internet address
    wcreatecreateConfigWelcomeSocket.sin_family = AF_INET;

    //Set the TCP port to be used for this socket
    wcreatecreateConfigWelcomeSocket.sin_port = htons(port);

    /*************************************************************************************
     * Last part of specifying a host address for this welcome socket consists
     * of indicating which of the IP addresses assigned to this host we wish
     * to have this socket listen on; we can have the socket listen
     * on all the IP addresses assigned to this host like right below here:
     *************************************************************************************/

    wcreatecreateConfigWelcomeSocket.sin_addr.s_addr = INADDR_ANY;

    /*
     * Bind the newly created address to the welcome socket
     */
    bind(welcomeSocket, (struct sockaddr *) &wcreatecreateConfigWelcomeSocket, sizeof (wcreatecreateConfigWelcomeSocket));


    /*************************************************************************************
     * So far we have created a stream socket and assigned a local address to it
     * Now, we wish to mark the socket descriptor just created as a Welcome Socket
     * (A server socket) by calling function listen()
     *
     * The BACKLOG actual parameter represents the length of pending-connection queue of
     * this socket
     *
     * This call turns the stream socket into a full server socket (Welcome socket)
     *************************************************************************************/

    listen(welcomeSocket, BACKLOG); //

    return welcomeSocket;

}


void printClient (struct sockaddr_in clientAddress){

  printf("Client IP %s\tClient port %hu\n", inet_ntoa(clientAddress.sin_addr), ntohs(clientAddress.sin_port));

}


void server(int port) {

    // Create and configure a Welcome Socket (Stream Server Socket, TCP)
    int welcomeSocket = createConfigWelcomeSocket(port);

    struct sockaddr_in clientAddress;
    unsigned int addressLength;

    //The delegate socket
    int delegateSocket;

    //Reserve static memory for storing the response
    static char response[MAXRESP];

    /**
     *************************************************************************************
     * This is the server's loop (An iterative server):
     * 1. Listen for connection requests received on the welcome socket
     * 2. When a client connects with us, the accept() function returns
     *    a delegate socket
     * 3. call getRequest() (If request is REQSHUTDOWN, exit server)
     * 4. call sendResponse()
     * 5. close data socket delegateSocket
     * 6. Repeat loop
     *************************************************************************************/

    while (TRUE) {

        printf("Server loop restarted\n\n");

        /*
         * Before calling accept(), load addressLength with the actual storage size
         * dedicated to the client address
         */

        addressLength = sizeof(clientAddress);


        /*
         * Call accept() to have the welcome socket extract the first connection in the backlog
         * When accept() returns it will return a delegate socket and addressLength will
         * contain the actual number of bytes filled by accept() in clientAddress
         */

        delegateSocket = accept(welcomeSocket, (struct sockaddr *) &clientAddress, &addressLength);

        printClient(clientAddress);

        char* request = getRequest(delegateSocket);

        computeResponse(response, request);

        sendResponse(response, delegateSocket);

            //Break the server loop if the response is RESPSHUTDOWN
            if (strncmp(response, RESPSHUTDOWN, strlen(RESPSHUTDOWN)) == 0) {
                break;
            }

        close(delegateSocket);
    }

    close(welcomeSocket);

    printf("Server exiting after shutdown request was received\n");

}

int main(int argc, char** argv) {


    if (argc == 1){

        printf("server starting on default port (%u)\n", DEFAULTPORT);
        server(DEFAULTPORT);

    }else if (argc == 2){

        int port = atoi(argv[1]);

        printf("server starting on port (%u)\n", port);
        server(port);

    }else{

        printf("$ server [TCP port number]");

    }

    printf("\nServer exiting.\n");

}