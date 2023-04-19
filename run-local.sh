#!/bin/bash

AWS_ENCRYPTION_MESSAGE_DATA_KEY_ID=alias/test_event_request_id \
APP_CLAIM_ACTIVE_DURATION=90 \
mvn spring-boot:run 


