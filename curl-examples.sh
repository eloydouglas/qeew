# lista tudo
curl -X GET http://localhost:8080/queues

# lista um tipo de fila
curl -X GET http://localhost:8080/queues/payment

curl -X POST http://localhost:8080/queues/payment \
     -H "Content-Type: application/json" \
     -d '{"priority": 1}'



curl -X POST http://localhost:8080/queues/payment \
     -H "Content-Type: application/json" \
     -d '{"priority": 1}'