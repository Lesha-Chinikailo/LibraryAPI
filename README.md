This project is Java Internship Entry Assignment

Start the project you need to 
1) run docker desktop
2) run several commands:
  - ./gradlew build
  - docker-compose up --build

The project is ready to go

Url swagger for services:
book-service: http://localhost:8081/books/swagger-ui/index.html#/

library-service: http://localhost:8082/records/swagger-ui/index.html#/

Endpoints in book-service:
GET http://localhost:8989/books/{id} - get a book by id

GET http://localhost:8989/books/isbn/{isbn} - get a book by isbn

GET http://localhost:8989/books/ - get all books

GET http://localhost:8989/books/freeBooks - get all free books

POST http://localhost:8989/books/create - create a book

PUT http://localhost:8989/books/{id} - edit book information

DELETE http://localhost:8989/books/{id} - delete a book by id

Endpoints in book-service:
GET http://localhost:8989/records/free - get all free books

GET http://localhost:8989/records/ - get all books

GET http://localhost:8989/records/free/ids - get all free book ids

POST http://localhost:8989/records - add a book in database

PUT http://localhost:8989/records/take{id} - take a book from thelibrary

PUT http://localhost:8989/records/return{id} - return a book in the library

Endpoints in auth-service:
POST http://localhost:8989/auth/register - register a user

GET http://localhost:8989/auth/token - get jwt token for registered user

GET http://localhost:8989/auth/validate - token validity check


Demo data is located at:

book-service: book-service/src/main/resources/db/data/book-insert-data.csv

library-service: library-service/src/main/resources/db/data/book-record-insert-data.csv
