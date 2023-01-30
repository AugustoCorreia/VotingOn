

## This repository is to answer the Technical  Assessment "Guide Voting"

#### Description

A Simple CRUD and voting way.

Example for test:
###Create Guide
```
curl --location --request POST 'localhost:8080/v1/guide' \
--header 'Content-Type: application/json' \
--data-raw '{
"title":"title"
}'
```

###Open Guide
```
curl --location --request PATCH 'localhost:8080/v1/guide/{guideId}/open?time={timeToExpire}' 
```

###Get Guide Result
```
curl --location --request GET 'localhost:8080/v1/guide/{guideId}/result'
```

###To Vote on Guide
```
curl --location --request POST 'localhost:8080/v1/guide/vote' \
--header 'Content-Type: application/json' \
--data-raw '{
    "cpf":"63802368053",
    "vote":"SIM", [You can Use only 
    "guideId":2
}'
```

#### How to execute
- Clone this repository
- Configure on your IDE the Java Version 17
- Install Docker 
- Run in the terminal Command (prefer open IDE terminal to take the right path) "docker compose up -d"
- Start The application on main class [VotingOnApplication.class]
- After starts access http://localhost:8080/swagger-ui/index.html for documentation.


####By Augusto Correia