# API Gateway

## Running the Application

To run the application, run the following command:

```bash
docker-compose up
```

This requires that the instance is configure with the correct environment variables.
This service requires the variables:

- EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka
- JWT_SECRET=averylongsecretkeythatissecureenoughforhmacsha256algorithm
- JWT_EXPIRATION=86400000

A full example .env file is provided in the .env.example file in the root directory.
## Testing the Application

To test the application, run the following command:

```bash
curl http://localhost:8080/actuator/health
```