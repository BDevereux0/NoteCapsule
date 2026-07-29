# Senior Software Engineering Review: NoteCapsule PRD and Milestones

Reviewed documents:

- `docs/PRD.md`
- `docs/Milestones_codex.md`

## Overall assessment

The product direction is strong and unusually thoughtful about failure recovery, privacy,
release boundaries, and human approval. However, implementation should not begin without
resolving several high-risk ambiguities. The statement that there are “no open product
questions” is too optimistic.

## Highest-priority findings

### 1. MVP access control is not sufficiently defined

The MVP has no authentication, relies on a bearer link, permits direct access, and is briefly
deployed to AWS (`PRD.md`, lines 9 and 82). Meanwhile, NoteCapsule is described as “never a
public website.”

Questions that remain:

- Can anyone who discovers the AWS URL open the direct-writing or settings pages?
- Does the bearer token authorize only one draft, or the whole application?
- Can a token be reused during its three-hour lifetime?
- Is it consumed on opening, approval, successful delivery, or supersession?
- Is the AWS deployment network-restricted, or merely unadvertised?

A private URL is not an access-control model. This should be resolved before M3 or AWS
deployment.

### 2. “Unknown delivery” lacks a safe system contract

The PRD correctly distinguishes confirmed failure from unknown outcome (`PRD.md`, line 61), but
the behavior remains incomplete.

After an SMTP timeout, the message may already have been accepted. Disabling the button prevents
repeated clicks but cannot prevent this distributed-systems failure. “Resend and save a copy”
also does not explain what the saved copy represents.

Before M2, define:

- What evidence produces confirmed success, confirmed failure, or unknown?
- Whether each attempt has a stable delivery identifier.
- How an interrupted send is reconciled after restart.
- How delivery attempts appear to the user.
- Whether an explicitly acknowledged resend counts as an acceptable duplicate.

This is one of the hardest parts of the product and deserves a dedicated design note and
failure-state tests.

### 3. Post-send retention has an unresolved crash state

The user chooses whether to retain a memory only after confirmed delivery (`PRD.md`, line 23;
`Milestones_codex.md`, line 267).

What happens if delivery succeeds and then the browser closes, the backend crashes, or the user
never answers the retention prompt? Immediate default deletion risks losing content before an
informed decision; indefinite temporary retention contradicts the default no-retention promise.

Define a durable `delivered-awaiting-retention-choice` state, its recovery behavior, and any
automatic deletion deadline.

### 4. MVP draft behavior is inconsistent across sections

The PRD presents autosave as a v1 feature, but MVP milestones already store and recover confirmed
drafts:

- V1 “adds” autosave and recovery (`PRD.md`, line 11).
- MVP M1 stores an unfinished draft (`Milestones_codex.md`, line 51).
- MVP M4 persists confirmed draft content across restart and network loss
  (`Milestones_codex.md`, line 114).

The likely distinction is manual or explicit persistence in MVP versus timed autosave in v1, but
the documents do not say that. Specify exactly when MVP content becomes “confirmed.”

### 5. The seven-day MVP gate is weaker than the claimed outcome

The PRD says sending after every notification is unnecessary (`PRD.md`, line 92). That means the
run primarily verifies scheduling, not the full reminder-to-email workflow.

Recommended evidence:

- Seven consecutive days of correct notification scheduling.
- A defined number of complete real sends.
- At least one restart-recovery exercise.
- At least one controlled delivery-failure exercise.

Otherwise, the release can pass without sustained evidence for its most consequential behavior.

## Important engineering concerns

### 6. Authentication requirements are below a safe baseline

V1 requires only a nonblank password (`PRD.md`, line 27). Even for a local product, that conflicts
with the document’s security posture.

The technical design should also address:

- CSRF protection for cookie-authenticated mutations.
- Cookie `Secure`, `HttpOnly`, and `SameSite` behavior.
- Session invalidation after account deletion or credential changes.
- Throttling that cannot be used to permanently lock out a targeted username.
- Concurrent sessions and whether users can revoke them.

### 7. Indefinite metadata retention is unnecessarily rigid

Metadata is retained forever and cannot be cleared (`PRD.md`, line 65), while the product
emphasizes user control and privacy. “Anonymous” metadata is also not automatically anonymous
just because obvious identifiers were removed; timestamps and behavior patterns may remain
linkable.

Define the actual purpose, fields, aggregation level, retention period, and user visibility.
Prefer a bounded period unless indefinite retention is essential.

### 8. Search and retention terminology is ambiguous

Operational metadata excludes recipient identity, yet saved-memory search displays recipient
names and can match email addresses (`PRD.md`, lines 63 and 68).

This can be coherent if there are two distinct stores:

- User-retained memory records containing selected personal fields.
- Content-free operational telemetry.

Name these separately and define what bulk clearing and account deletion do to each.

### 9. Scheduling needs more edge-case requirements

The injected clock and time-zone tests are good, but M3 should explicitly cover:

- Daylight-saving gaps and repeated local times.
- Backend downtime during the scheduled instant.
- Startup catch-up behavior.
- Schedule or time-zone changes near a pending occurrence.
- Multiple application instances during AWS deployment.
- Database transaction failure after notification delivery.
- Discord accepting a webhook while the response is lost.

Without these decisions, “one scheduled occurrence does not create duplicate notifications” is
not fully testable.

### 10. V2 is too broad for one release

V2 combines MFA, OIDC account linking, Google Calendar and Gmail authorization, photo handling,
local generation, embeddings, Qdrant, semantic search, RAG, and chat. Each of identity linking,
Google integrations, and grounded RAG could constitute a release.

A safer split would be:

- v2.0: local embeddings, semantic search, and grounded answers.
- v2.1: memory chat and evaluation hardening.
- v2.2: Create memory from date and Google integrations.
- v2.3: MFA and OIDC.

This reduces simultaneous security boundaries and makes failures easier to diagnose.

## Roadmap improvements

The roadmap is logically ordered and maps well to the PRD, but several milestones are too large
for the promised “small feature branches.”

Add explicit engineering design checkpoints before:

- M2: delivery state machine and idempotency.
- M3: scheduler, token, and occurrence model.
- M6: authentication threat model and session design.
- M7: authorization matrix for every resource and action.
- M11: retention lifecycle and crash recovery.
- M13: telemetry schema and privacy review.
- M15–M16: identity-linking and OAuth threat models.
- M18: vector deletion, re-indexing, and account-isolation model.

Also add browser-level end-to-end tests. The current roadmap emphasizes unit and integration
tests, but critical requirements—navigation warnings, approval invalidation, disabled send
controls, session cookies, autosave indicators, and cross-account UI behavior—need tests across
React and Spring Boot together.

## Documentation-quality observations

- Stable requirement IDs would improve traceability. Milestones could reference `FR-*`, `QR-*`,
  and acceptance criteria rather than duplicating prose.
- “There are no open product questions” in `PRD.md`, line 98, should be replaced with the
  unresolved items above or softened to “No known scope questions; implementation discovery may
  surface additional decisions.”
- Section 10 contains substantial confirmed V2 requirements rather than only questions and
  assumptions. Those requirements belong in the relevant functional and quality sections.
- “Secure,” “normally,” “private,” and “anonymous” need measurable definitions or explicit
  design references.

## Recommendation

Approve the overall direction, but request changes before implementation. The immediate blockers
are:

1. MVP access control.
2. Delivery uncertainty.
3. Retention-after-send recovery.
4. The MVP/v1 draft boundary.

Once those four are resolved, M0 can begin with substantially less rewrite risk.
