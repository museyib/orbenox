| Failure | Expected behavior | Mechanism |
| --- | --- | --- |
| RabbitMQ unavailable | Outbox retains event | Outbox |
| RabbitMQ restored | Event eventually published | Scheduled publisher |
| PostgreSQL unavailable | Transaction fails safely | Transaction |
| PostgreSQL restored | Consumer retries | RabbitMQ retry |
| Consumer crashes | Message redelivered | RabbitMQ |
| Duplicate delivery | Business effect once | Inbox |
| Broker ACK + process crash | Event republished | Outbox |
| Publisher crashes | Another instance continues | ShedLock + outbox |
| Multiple consumers | No duplicate business effect | Inbox |
| Persistent consumer failure | DLQ | RabbitMQ |
| Service restart | Pending work resumes | Durable state |
