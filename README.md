# Next Tracking Number Generator
This project is created with Java Language and Spring Boot Framework and hosted on AWS(With API Gateway, Lambda(Serverless) and Cloudfront).

## Available APIs
1. GET - /ping - Returns "Hello, World!" if the application is successfully running
2. GET - /api/v1/next-tracking-number - Generated and returns a tracking number and created timestamp. The tracking number is generated based on following method
        a. Current Time in ms + (Random Number * 1000).
        b. Converting the value from Step a to Base 36, this is done to be able to represent it in a compact alphanumeric format, reducing the size of the ID.
        c. If the value from Step b has a length of more than 16 characters, it is truncated to 16 characters length.
        d. Now to ensure the uniqueness we add Origin Country ID + Destination Country ID + value from Step c + Weight from API Request Parameters.
        e. Finally, all the case of all characters are changed to Uppercase, length is truncated to 16 characters length and any character other than A-Z and 0-9 are replaced with 'X'.

## application.properties
1. Currently the configuration is set to run on port 9091, please change the port based on your usage(file location - src\main\resources\application.properties).
2. The Logging level is set to WARN, please change it per your usage.

The project folder also includes a `template.yml` file. You can use this [SAM](https://github.com/awslabs/serverless-application-model) file to deploy the project to AWS Lambda and Amazon API Gateway or test in local with the [SAM CLI](https://github.com/awslabs/aws-sam-cli). 

## Pre-Requisites
* Java 17 or more
* [Gradle](https://gradle.org/) or [Maven](https://maven.apache.org/)

## Building the project
### To build the application locally
1. Clone the Git Repository
2. Run the following command, if you are using Maven, this will get you the zip file that will be help you deploy and run on AWS Services(this is through SAM Deployment)

```
mvn spring-boot::run

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  13.141 s
[INFO] Finished at: 2025-01-09T19:22:33+05:30
[INFO] ------------------------------------------------------------------------
```

## To test the application wunning on AWS Services

1. Ping API

```
$ curl --location 'https://pwcwxkognl.execute-api.eu-north-1.amazonaws.com/dev/ping'

{
    "pong": "Hello, World!"
}

```

2. Tracking Number Generator API

```
$ curl --location 'https://pwcwxkognl.execute-api.eu-north-1.amazonaws.com/dev/api/v1/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19%3A29%3A32%2B08%3A00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox%20Logistics&customer_slug=redbox-logistics'

{
    "tracking_number": "MYIDM5PE3M4C1X23",
    "created_at": "2025-01-09T13:54:19.150976644Z"
}
```

### Validation rules for Tracking Number Generator API (for the purpose of this Code Test all fields are assumed mandatory)
1. origin_country_id  - is mandatory and has to be 2 characters length with Uppercase alphabet.
2. destination_country_id  - is mandatory and has to be 2 characters length with Uppercase alphabet.
3. weight - is mandatory and has to be greater than 0(zero).
4. created_at - is mandatory and has to be in RFC 3339 format.
5. customer_name - is mandatory and cannot be blank.
6. customer_slug - is mandatory and checks for slug-case/kebab-case.

## Deployed API Links
1. Ping API - https://pwcwxkognl.execute-api.eu-north-1.amazonaws.com/dev/ping
2. Tracking Number Generator API - https://pwcwxkognl.execute-api.eu-north-1.amazonaws.com/dev/api/v1/next-tracking-number?origin_country_id=<origin_country_id>&destination_country_id=<destination_country_id>&weight=<weight>&created_at=<created_at>&customer_id=<customer_id>&customer_name=<customer_name>&customer_slug=<customer_slug> (an example is mentioned in the curl command above)

### Testing
#### Unit Testing
We have created two classes to write and run Unit Test Cases -
1. StreamLambdaHandlerTest - Has test cases to check the serverless connection with AWS Lambda..
2. TrackingNumGenControllerTest - Has test cases to check the business logic, request and response, if they are working as expected.

#### Sanity/End to End testing
Has been done through Postman.

#### Further Testing
1. We can run a performance and load test on the API to check for lags and update the system configurations to improve the performance.
2. Run multiple test cases with different ramp up periods with a tool like JMeter.