# Async execution performance test

A simple test, tracing the async execution performance of Activiti / Camunda process engines.

## Test cases

The test cases use service tasks that perform noop operations with the following definition:

```
<serviceTask activiti:async="true" activiti:expression="${true}"/>
```

### async-parallel

Tests parallel execution of async tasks

![parallel execution test](https://raw.githubusercontent.com/Nikku/process-engine-async-test/master/src/main/resources/async-parallel.png)

### async-sequential

Tests parallel execution of async tasks

![sequential execution test](https://raw.githubusercontent.com/Nikku/process-engine-async-test/master/src/main/resources/async-sequential.png)


## Execute the tests

Configure the database to use via

* `db.connection.url`
* `db.connection.username`
* `db.connection.password`
* `db.connection.driver`

Execute a test via

```
mvn exec:java -Dexec.mainClass="de.nixis.engine.test.Main" -Ddb.connection.password=my-pass
```