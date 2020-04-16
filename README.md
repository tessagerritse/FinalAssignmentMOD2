# FinalAssignmentMOD2

## How to run the file exchanger

- Clone or download the repository
- Go to the root directory
- To start the server execute: $ java -jar ./build/libs/NUM2-server.jar
- To start the client execute: $ java -jar ./build/libs/NUM2-client.jar <hostname of the server>
- Now a menu of possible commands will be displayed
- Just answer as indicated and hit return to activate your command

### Notes

- The first time the server is started it will create an empty directory "FilesOnServer/" in the home directory of the system where the server is running. In that directory, the server will save your uploaded files
- The client will automatically create a "FilesOnClient/" directory in your home directory. Place files in that directory in order to upload them to the server
