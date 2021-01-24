~~How to run the program~~

After compiling both programs, open two terminals:

1º terminal: ./tcp-server
2º terminal: ./nc <IP-SERVER> <PORT-SERVER>

On terminal 2:
  -Sending "Shotdown server" will cause the server to stop. Server will answer your request.
  -Every other sentence will cause the server to answer you by sending "Unknown request".
