### run Redis in docker container
docker run -p 6379:6379 --name jo_redis -d redis

### use Apache Bench for bechmarking
ab -n 10 -c 2 -v 3 http://localhost:8072/banking/api/v1/cards/contact-info
