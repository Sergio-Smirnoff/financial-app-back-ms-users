# ms-users — messaging and jobs

CloudEvents 1.0, Kafka binary mode, via `commons-messaging`. Topic name = `ce_type`. Outbox, `OutboxRelay` and DLT conventions: parent `.ai/references/ARCHITECTURE.md` — not repeated here.

## Published

| ce_type / topic | when emitted | payload fields |
|---|---|---|
| `users.user.registered` | Successful user registration (`RegisterUserUseCaseImpl`) | userId, email, firstName, lastName |

*GOTCHA*: `UserRegisteredPayload` carries **no timestamp field** to prevent Jackson Instant deserialization errors on consumers.

## Consumed
None. ms-users consumes no Kafka events.

## Scheduled jobs

| Job | Trigger / Cron | What it does |
|---|---|---|
| `OutboxRelay.publishOutboxEvents` | `*/2 * * * * *` (every 2s) | Polls `outbox_event` for unsent events and publishes to Kafka |
