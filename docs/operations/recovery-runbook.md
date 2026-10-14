# Recovery Runbook

This document outlines the steps to recover from a failure in the system.

## Steps

1. Detection
2. Diagnosis
3. Recovery
4. Verification


## Scenario 1: Old outbox event with **PENDING** status
### Detection
Identify the outbox events with **PENDING** status on the `outbox_event` older than expected publication time.

### Diagnosis
Check the logs and system metrics to identify any potential issues that may have caused the outbox events to remain in a **PENDING** status.
Confirm the publisher instances are running and healthy, RabbitMQ is functioning properly, and there are no network issues affecting communication between components.

### Recovery
1. Fix the underlying issue and let the publisher normally retry.
2. Monitor the system to ensure the issue is resolved and no new issues arise.
3. Don't change the status blindly.

### Verification
After the recovery steps are completed, verify that the system is functioning as expected and that the issue has been resolved. Check the logs and system metrics to ensure that there are no new issues and that the system is stable.

## Scenario 2: A message reaches a DLQ

### Detection
The shared messaging configuration defines the dead-letter exchange `dead.letter.exchange` and separate dead-letter queues (DLQs):

- `document.posted.dlq`
- `stock.posted.dlq`
- `notification.dlq`

The separate queues are useful because different consumers can have different recovery requirements.

### Diagnosis
Open the [RabbitMQ Management UI](http://localhost:15673) from your local Compose setup. Go to **Queues and Streams**, select the relevant DLQ, and inspect the available message information. Depending on the UI and message state, examine:

- The original event payload and event ID.
- The routing key and exchange.
- The `x-death` header, if present, for dead-letter history.
- The exception or failure reason in consumer logs.
- The correlation ID.
- The consumer's inbox record, if the consumer uses one.

Remember that `x-death` records dead-letter history; it is not a complete count of every listener invocation or retry attempt.

Consider why the message failed before deciding whether to replay it. For example, if a stock-processing message failed repeatedly:

- If PostgreSQL was temporarily unavailable, retrying after the database recovers may be appropriate.
- If the payload is malformed, replaying it without correcting the cause will probably fail again.
- If the message represents an invalid business operation, it may require a business decision rather than a technical retry.

Distinguish technical exceptions, which should follow the configured retry/DLQ policy, from business-rule failures that the consumer deliberately handles and reports through a failure event. Not every business failure should be sent to a DLQ.

### Recovery
For an initial iteration, inspect and replay messages manually through the RabbitMQ Management UI in your local environment. Treat this as a controlled diagnostic procedure, not the final production recovery interface.

For a more repeatable approach, create a small, restricted operator recovery tool that:

1. Identifies the original DLQ message.
2. Shows the event ID, consumer, failure reason, and original routing information.
3. Requires an explicit operator action.
4. Republishes the original event with its original event ID.
5. Records who initiated the replay, when, and why.
6. Verifies that the message was accepted and subsequently processed.

The original event ID is critical. Do not generate a new ID just because you are replaying a failed delivery; doing so could bypass inbox deduplication and apply the same stock change twice.

Do not purge a DLQ as a recovery action. First establish why its messages accumulated and whether they represent recoverable work.

### Verification
Confirm that the replayed message was accepted and subsequently processed, and check the relevant consumer logs and inbox record, if used. Verify that the original event ID was preserved and that replay did not apply the same operation twice.

## Scenario 3: RabbitMQ confirms publication, but the database update fails

This scenario demonstrates why the outbox and inbox patterns complement each other.

### Diagnosis
The failure sequence is:

1. The publisher reads a **PENDING** event while a PostgreSQL transaction holds the selected row lock.
2. RabbitMQ confirms the message, meaning the broker has accepted the publication.
3. The database transaction fails, so the **PUBLISHED** update rolls back and the row remains **PENDING**.
4. The publisher sends the event again, making a duplicate delivery possible and expected in this failure mode.
5. The consumer inbox deduplicates the delivery so the same event ID does not produce the same business effect twice.

### Recovery
Generally, do not intervene manually. Once the database is healthy, let the publisher retry. The duplicate is an expected consequence of at-least-once delivery.

### Verification
The inbox design is safe only if its transaction boundary matches the business operation. For the stock consumer, verify that:

- Recording the inbox event and applying the stock mutation participate in the same local database transaction.
- If stock processing fails technically, both operations roll back together.
- If the event was already committed, a duplicate delivery does not apply the stock mutation again.
- A business-rule failure is recorded and reported consistently with the chosen failure-event semantics.

This is particularly important for stock. If an inbox record commits before the stock mutation, a later retry could be incorrectly discarded even though the stock update never happened.
