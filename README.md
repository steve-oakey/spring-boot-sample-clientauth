Spring Boot Client Auth Sample
==============================

This demonstrates how to run a web server configured with
SSL mutual authentication and configure a `RestTemplate`
to make authenticated requests.

Instructions
------------

### Running
If you want to run and test the application in a browser, you will need
to:

1. Import `client.pfx` into your browser as a client certificate.
2. (Optional) Import `ca.pem` into your browser as a trusted CA. This
will avoid the security exception when running the sample.

Launch the application using Maven:

```
mvn spring-boot:run
```

Visit `https://localhost:8443/whoami` in your browser. You will be
presented with the user name and authorities as JSON.

### Testing
The test case `ApplicationTest` demonstrates the usage of the
`RestTemplate` bean configured for client authentication by
making a request to the web server and asserting the client information
returned.

The test case can be run from an IDE or using `mvn clean test`
