# Recovery Runbook

This document outlines the steps to recover from a failure in the system.

## Steps

1. Detection
2. Diagnosis
3. Recovery
4. Verification


## Scenario 1: Old outbox event with **PENDING** status
### Detection
Identify the outbox events with **PENDING** status on the `outbox_table` older than expected publication time.

### Diagnosis
Check the logs and system metrics to identify any potential issues that may have caused the outbox events to remain in a **PENDING** status.
Confirm the publisher instances are running and healthy, RabbitMQ is functioning properly, and there are no network issues affecting communication between components.

### Recovery
1. Fix the underlying issue and let the publisher normally retry.
2. Monitor the system to ensure the issue is resolved and no new issues arise.
3. Don't change the status blindly.

### Verification
After the recovery steps are completed, verify that the system is functioning as expected and that the issue has been resolved. Check the logs and system metrics to ensure that there are no new issues and that the system is stable.
