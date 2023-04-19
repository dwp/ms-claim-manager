# ms-claim-manager

micro-service to support save and resume. The main purposes of this service are to support online HTML application process for PIP2 questionnaire. 

## Dependency

the API stores and queries user's claim details from a Mongo database. To be able to successfully start the application, the application must be 
able to connect to a Mongo instance at start up. 

## rest api

the api is built from the [openapi-spec.yaml](api-spec/openapi-spec.yaml)

## running the application

this is a standard SpringBoot application with all the configuration items held in `src/main/resources/application.yml` and bundled 
into the project at build.

```bash
mvn clean verify
```
to build and vulnerability check
```bash
sh run-local.sh # this calls mvn spring-boot:run with some environment variables set

or

mvn spring-boot:run

or

java -jar target/ms-claim-manager-<artifactId>.jar
```
to run

## Configuration elements

All configuration listed in `src/main/resources/application.yml` and follows the standard spring convention for yml file notation.  
The custom setup configured with the following section and can be overridden (either on the command line or by environment variables).

```yaml
app:
  claim:
    active-duration: 93

encryption:
  kms-override: http://localhost:4549
  data-key: arn:address

feature:
  encryption:
    data:
      enabled: true
```
* `app.claim.active-duration` = the number of days a claim is valid for
* `encryption.kms-override` = override kms url e.g. http://localhost:4599
* `encryption.data-key` = aws KMS arn
* `feature.encryption.data.enabled` = enable data encryption/decryption onBeforeSave and onAfterLoad event. 

## Data analytics plugin

### dependency (data analytics)

data plugin captures mongo change from a specific database triggered by a micro-service instance

```xml
   <dependency>
      <groupId>uk.gov.dwp.health</groupId>
      <artifactId>mongo-changestream-data-stater</artifactId>
      <version>${dwp-mongo-change-stream-starter.version}</version>
   </dependency>
```
message broker publishes change to a designated queue

```xml 
  <dependency>
     <groupId>uk.gov.dwp.health.integration</groupId>
     <artifactId>message-broker-integration-autoconfigure</artifactId>
    <version>${dwp.message-broker.version}</version>
  </dependency>
```

### configuration variables (data analytics)

```yaml
- FEATURE_DATA_CHANGESTREAM_ENABLED=true
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_COLLECTION=claim
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_ROUTING_KEY=pip.claim.mgr.stream
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_DATABASE=pip-apply-claim-mgr
- UK_GOV_DWP_HEALTH_CHANGESTREAM_CHANNELS[0]_SKIP_DECRYPTION=true
- UK_GOV_DWP_HEALTH_INTEGRATION_OUTBOUND_TOPIC_EXCHANGE=stream-topic
- UK_GOV_DWP_HEALTH_INTEGRATION_SNS_ENDPOINT_OVERRIDE=http://localstack:4566
- UK_GOV_DWP_HEALTH_INTEGRATION_SQS_ENDPOINT_OVERRIDE=http://localstack:4566
- UK_GOV_DWP_HEALTH_INTEGRATION_AWS_REGION=us-east-1
- UK_GOV_DWP_HEALTH_INTEGRATION_MESSAGING_TYPE=aws
```

## Docker

The docker image built on the distroless base image
