
# What?
Example implementation for a loadbalancer that helps migrating from an existing C8 cluster to a new C8 cluster (currently only working for PublishMessage API)

# How to run?

Start clusters
```bash 
docker-compose -f docker-compose-core-old.yaml -p old up -d
docker-compose -f docker-compose-core-new.yaml -p new up -d
```

start application with migration.createNewInstancesTarget=old (default)
```bash
curl -X POST http://localhost:8080/process/create -H "Content-Type: application/json" -d '{"myId": "old1"}'
curl -X POST http://localhost:8080/process/create -H "Content-Type: application/json" -d '{"myId": "old2"}'
```

restart application with migration.createNewInstancesTarget=new
```bash
curl -X POST http://localhost:8080/process/create -H "Content-Type: application/json" -d '{"myId": "new1"}'
curl -X POST http://localhost:8080/process/create -H "Content-Type: application/json" -d '{"myId": "new2"}'
```

send message via publish or correlated

```bash
curl -X POST http://localhost:8080/process/publish/old1/MyMessage
curl -X POST http://localhost:8080/process/publish/old2/MyMessage
curl -X POST http://localhost:8080/process/publish/new1/MyMessage
curl -X POST http://localhost:8080/process/publish/new2/MyMessage

# FIXME: Correlate Endpoint not working currently
#curl -X POST http://localhost:8080/process/correlate/old2/MyMessage
#curl -X POST http://localhost:8080/process/correlate/new2/MyMessage
```
