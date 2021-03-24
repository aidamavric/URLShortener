# URl Shortener

## Getting Started
UrlShortener exposes APIs for registering account, registering URLs and fetching statistic of visits per one account.

### Run
In order to run the application apply the command <i> java -jar URLShortener.jar</i>

### Guides
In order to call APIs you will need any REST client, for example Postman. You can import example of postman project from:
<i>src/main/resources/postman/URL_shortener.postman_collection.json</i>.

* Before using any other API, you need to call <i>http://localhost:8080/account</i> with body containing
account ID you want to register (as it is enclosed in the postman collection example).

* After registering and getting generated password you need to set Authorization header for every other API call with Basic Auth method (chosen from dropdown in case you use Postman as a client) and set your account ID and provided password.

* For registering URL you need to call: <i>http://localhost:8080/register</i> with body containing
URL you want to register and redirectType (optional). Default redirection type is 302.

* You can fetch statistic of visits of all links per one account by calling <i>http://localhost:8080/statistic/{AccountId}</i>
where {AccountId} is a path parameter you use to set account ID you want to fetch statistic for.

* After obtaining a short URL, you can access it through any browser and you will be immediately redirected to your
  original URL.
