# http-comparator

Use `sbt run` to start the program. The server is hosted on port 9090.
IP and port can be configured in application.conf

Use `sbt test` to run the unit tests

Use `sbt it:test` to run the integration tests.


# Known issues:
Running it:test multiple time in the same sbt console causes some tests
to fail. This is because the server is closed on only on jvm exit.
To fix, need to make each individual test self contained.

