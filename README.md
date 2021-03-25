# URl Shortener

## Getting Started
UrlShortener exposes APIs to register account, register URL and fetch statistic of URL visits per one account.
Application is written in Java, using Spring framework and based on Spring Boot starter module.
Dependencies are packaged as a Maven project: run <i> mvn install </i> to build executable jar with all dependencies.

### Run
To run the application apply the command: <i> java -jar URLShortener-1.0.0.jar</i>

### Guides
In order to call APIs any REST client is required, for example Postman. Import Postman collection example from <i>src/main/resources/postman/URL_shortener.postman_collection.json</i>.
API is also accessible through Swagger-UI on URL: <i><b> http://localhost:8080/swagger.html </b></i>.

* Before using any other API, call <i><b>http://localhost:8080/account</b></i> with body containing
account ID to register. Authorization header is not needed for this call.

* After registering and getting generated password, set Authorization header for every other API call with Basic Auth method.
In case of using Postman as client: chose Basic Auth as type of Authorization and type Username (AccountId) and Password.
In Swagger-UI click on Authorize button on the top right side and type Username (AccountId) and Password. Once credentials are filled in,
Swagger will use them for all requests that require authorization.

* To register URL call: <i><b>http://localhost:8080/register</b></i> with body containing
URL to register and redirectType (optional). Default redirection type is 302.

* To fetch statistic of visits of all links per one account call: <i><b>http://localhost:8080/statistic/{AccountId}</b></i>
where {AccountId} is a path parameter used to set account ID to fetch statistic for.

* After obtaining a short URL, it can be accessed through any browser and immediately redirected to originally registered URL.
