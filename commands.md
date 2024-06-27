### run Redis in docker container
docker run -p 6379:6379 --name jo_redis -d redis

### use Apache Bench for bechmarking
ab -n 10 -c 2 -v 3 http://localhost:8072/banking/api/v1/cards/contact-info

### keycloak: run docker container
docker run -d -p 7080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:25.0.1 start-dev

### open keycloak admin console
http:localhost:7080 (admin, pw see docker cmd above)

### get config from keycloak
http://localhost:7080/realms/master/.well-known/openid-configuration