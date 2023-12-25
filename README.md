# Data security final project
The repository presents the final project on the data security course conducted at the Warsaw University of Technology. The project was about
creating a secure app that would enable users to safely create notes/make some notes public and create encrypted notes with passwords.
Aside from having the possibility to register or log in, the user should also have the possibility to get back the password in case it is gone.

Technology stack:
For creating the final version of the application I used the following libraries/frameworks:
- Java 17
- SpringBoot
- Spring Security(Implemented security using form login and sessions)

To create the front side of the project I used the following technologies:
- React
- Nginx

The application enables users to have an encrypted connection to the server which goes through https. I used
the library OpenSSL for creating self-signed certificates and wrote some nginx configuration to have it.

## How to run the application
The application can be run in two ways. The first way is by using docker - in the root of the project we simply have to type 
in docker-compose up and the whole project will be configured and available on https://localhost. You can also run the application 
on the developer servers by typing the ./gradlew bootRun in Java project and npm start in React project. 

## Some photos of the application
![Screenshot 2023-12-25 at 19 32 34](https://github.com/aidian3k/secure-markdown/assets/93425971/3a6545ca-8ce7-451e-b94d-3adbe6c383f6)
![Screenshot 2023-12-25 at 19 34 56](https://github.com/aidian3k/secure-markdown/assets/93425971/2a9eb262-22fb-4a1f-81fc-cb4ccabf5e75)
![Screenshot 2023-12-25 at 19 32 48](https://github.com/aidian3k/secure-markdown/assets/93425971/e3e67efc-bc51-46e9-b023-4d0ba6871e18)
![Screenshot 2023-12-25 at 19 33 32](https://github.com/aidian3k/secure-markdown/assets/93425971/3e8aaa15-908c-4724-9021-a34322acde17)
![Screenshot 2023-12-25 at 19 33 47](https://github.com/aidian3k/secure-markdown/assets/93425971/6ac4cd6e-a256-48c4-bad3-1f79cb8a0d2b)
![Screenshot 2023-12-25 at 19 34 15](https://github.com/aidian3k/secure-markdown/assets/93425971/aa88b980-b62b-4cf2-9c22-4b673598b1cc)
