# NoteCapsule Milestones

This roadmap turns `docs/PRD.md` into an implementation sequence for the MVP, v1, and v2.
The PRD remains the source of truth if this roadmap and the PRD ever disagree.

## How to use this roadmap

- Complete milestones in order unless a milestone explicitly says it can run in parallel.
- Deliver each milestone through one or more small feature branches and reviewed pull requests.
- Keep GitHub Actions green and add meaningful tests with each behavior.
- Do not pull later-release features into an earlier release.
- Treat each release gate as required work, not optional cleanup.

## Release overview

| Release | Outcome | Final evidence |
|---|---|---|
| MVP | One operator follows a scheduled Discord link, writes and reviews a text memory, approves it, and sends it by email without authentication. | Core-flow demonstration, seven consecutive scheduled days locally without a critical failure, and a brief verified AWS deployment that is then removed. |
| v1 | Local authenticated accounts receive isolated drafts, recipients, schedules, recovery, saved memories, search, and privacy-safe telemetry. | Recorded end-to-end demonstration, cross-account rejection evidence, and a new seven-day local notification run. |
| v2 | The local product adds stronger sign-in options and opt-in, source-grounded AI over saved memories. | Recorded Create memory from date demonstration and a passing RAG evaluation. |

# MVP

## M0 — Engineering foundation

**Outcome:** The Spring Boot and React application can be built, tested, and run consistently.

**Deliverables:**

- Create a Java 21 Spring Boot backend built with Maven.
- Create a React and TypeScript frontend built with Vite and npm.
- Establish a local MySQL development database and versioned schema migrations.
- Add development configuration without committing credentials or tokens.
- Add backend and frontend checks to GitHub Actions.
- Document the local startup and test commands.
- Add a pull-request template and require branch, review, test, and merge workflow.

**Completion checks:**

- A new checkout can start the frontend, backend, and database from documented steps.
- Backend and frontend tests run in CI.
- No credential, session value, reminder token, or private content is committed or logged.

## M1 — Direct write, review, and fake delivery

**Outcome:** The operator can complete the core flow without relying on an email provider.

**Deliverables:**

- Build a nonblank text-memory form.
- Store one unfinished draft and restore its latest confirmed content.
- Add a review page that displays the complete text and selected recipient.
- Require an explicit **Approve & Send** action.
- Invalidate approval when the text or recipient changes.
- Add a fake email sender and visible success state.
- Prevent repeated clicks from creating duplicate deliveries.
- Add unit tests for validation, approval invalidation, and duplicate-send protection.

**Completion checks:**

- Writing, reviewing, approving, and fake sending works end to end.
- Cancel or Back to editing preserves the draft.
- No send can occur without current approval.

## M2 — Recipient setup and real email delivery

**Outcome:** The operator can configure a recipient and deliver an approved memory through email.

**Deliverables:**

- Add local recipient settings with name and validated email address.
- Preselect the configured recipient for a new draft.
- Integrate a development inbox before enabling real SMTP delivery.
- Keep SMTP credentials outside Git.
- Show sending, confirmed success, confirmed failure, and unknown-delivery states.
- On confirmed failure, offer Retry sending, Back to editing, and Keep for later.
- On an unknown outcome, warn that resending may create a duplicate and require an explicit choice.
- Preserve the draft and delivery-attempt history after failed or uncertain delivery.

**Completion checks:**

- The reviewed recipient always matches the delivery recipient.
- A confirmed success cannot be resubmitted through repeated controls.
- Provider errors do not expose credentials, raw responses, or stack traces.
- Failure and uncertain delivery are covered by automated tests.

## M3 — Scheduling, private links, and Discord reminders

**Outcome:** A scheduled Discord notification opens the correct unfinished draft or a new memory.

**Deliverables:**

- Add local configuration for reminder weekdays, one reminder time, and a time zone.
- Implement scheduling against an injected clock so time behavior is deterministic in tests.
- Send a Discord notification at the scheduled occurrence.
- Generate an unguessable private token with a three-hour lifetime.
- Ensure tokens and private data never appear in logs.
- Open the existing unfinished draft or create a new draft with the configured recipient.
- Reject invalid, expired, or superseded links without disclosing private details.
- Preserve direct access to writing when Discord is unavailable.

**Completion checks:**

- Boundary tests cover time zones, selected weekdays, token expiration, and repeat scheduler runs.
- One scheduled occurrence does not create duplicate notifications or drafts.
- A Discord outage does not prevent direct writing, review, or sending.

## M4 — MVP recovery and safety hardening

**Outcome:** The MVP preserves work and handles interruptions without unsafe delivery.

**Deliverables:**

- Persist confirmed draft content across refresh, backend restart, and temporary network loss.
- Keep unsaved text visible after a save failure and warn before destructive navigation.
- Distinguish content state from delivery-attempt state.
- Add actionable, privacy-safe error messages.
- Add keyboard access, visible labels and focus, and textual status indicators.
- Verify ordinary local pages respond within two seconds, excluding provider delay.
- Add integration tests for restart recovery and delivery idempotency.
- Add a privacy-focused logging review.

**Completion checks:**

- No tested recovery path loses confirmed content.
- NoteCapsule never sends without approval, to an unreviewed recipient, or twice due to repeated controls.
- Logs and errors contain no memory text, recipient identity, credentials, authorization data, or private tokens.

## M5 — MVP readiness and AWS learning deployment

**Outcome:** The MVP is proven locally and during a short-lived AWS deployment.

**Deliverables:**

- Record a demonstration of the complete Discord-link-to-email flow.
- Run notifications locally for seven consecutive scheduled days.
- Keep a dated run log with notification outcome and any defect classification.
- Restart the run after a critical failure and its correction.
- Deploy the MVP briefly to AWS with secrets stored outside the application configuration.
- Verify the core flow in AWS and document an explanation of the deployment.
- Remove the AWS deployment and verify that its billable resources are gone.

**Critical MVP failures:**

- Loss of confirmed draft content.
- Sending without current approval.
- Sending to an unreviewed recipient.
- Duplicate delivery caused by NoteCapsule.
- Incorrect delivery.

**Release gate:**

- The demonstration passes.
- Seven consecutive scheduled local days pass without a critical failure.
- The AWS flow is verified and explained.
- The temporary AWS resources are removed.

# v1

## M6 — Authentication and session security

**Outcome:** Users can create accounts and securely sign in and out.

**Deliverables:**

- Add signup with a unique username and nonblank password.
- Store passwords using a secure one-way password hash.
- Add login and logout with a secure server-side session cookie.
- Expire sessions after 30 idle minutes or eight total hours.
- Return the same message for an unknown username and a wrong password.
- Add deterministic throttling or temporary blocking for repeated login failures.
- Require login before a reminder link can open protected content.

**Completion checks:**

- Logout invalidates the server session.
- Expired or invalid sessions cannot access protected routes.
- Passwords and session data never appear in logs, telemetry, or errors.
- Automated tests cover session expiration and repeated-login-failure behavior.

## M7 — Account isolation and authorization

**Outcome:** Every account has an isolated profile and can access only its own data.

**Deliverables:**

- Associate drafts, memories, recipients, settings, schedules, links, history, and metadata with an owning account.
- Check ownership on every protected backend request.
- Reject altered identifiers without revealing whether another account's record exists.
- Bind scheduled and overdue links to their owning account.
- Create separate real-user and test-user accounts and credentials.
- Add negative authorization tests across every protected resource type.

**Completion checks:**

- Cross-account access succeeds zero times in the automated authorization suite.
- Changing a URL, request body, or identifier cannot expose or modify another account's data.
- Private failure responses do not disclose another account or resource.

## M8 — Address book, schedules, and Discord preferences

**Outcome:** Each account controls its recipients and reminder behavior.

**Deliverables:**

- Add address-book entries with names and validated email addresses.
- Require a default recipient for reminders.
- Allow one or more recipients on each memory.
- Let the user choose weekdays, one shared reminder time, and a detected but correctable time zone.
- Make Discord optional per account.
- Preserve direct writing, sending, saving, and search when Discord is disabled.
- Ensure the review step displays every recipient before approval.

**Completion checks:**

- Account settings never affect another account.
- A recipient change invalidates approval and saves immediately.
- Reminder calculations use the account's confirmed time zone.

## M9 — Autosave and draft recovery

**Outcome:** An account's single unfinished draft is reliably saved and recovered.

**Deliverables:**

- Enforce at most one unfinished draft per account.
- Save text within two seconds after typing pauses.
- Save recipient changes immediately.
- Trigger a final save on focus loss or navigation.
- Display Saving, Saved, and Save failed states.
- Restore the latest confirmed autosave after refresh, restart, or temporary network failure.
- Keep text visible after save failure and warn before navigation.
- Add Cancel memory with Keep editing and Discard draft options.
- On discard, retain only a content-free, non-personal event.

**Completion checks:**

- Recovery tests pass across refresh, backend restart, and simulated network loss.
- A failed save cannot silently discard visible text.
- Discard removes private draft content.

## M10 — Overdue recovery links

**Outcome:** An unfinished scheduled occurrence can be recovered from one controlled overdue email.

**Deliverables:**

- Detect an unfinished occurrence after its scheduled link expires.
- Send one overdue email when the account enables it.
- Make the recovery link valid until the next scheduled Discord link.
- Invalidate applicable earlier links when a new occurrence begins, delivery completes, or the draft is discarded.
- Keep overdue notifications separate from recipient delivery email.
- Prevent repeat scheduler executions from sending duplicate overdue emails.

**Completion checks:**

- Time-based tests cover expiration, next-occurrence boundaries, delivery, and discard.
- At most one enabled overdue email is sent for an unfinished occurrence.
- Expired or invalidated recovery links reveal no private details.

## M11 — Post-send retention and operational metadata

**Outcome:** Users explicitly choose whether memory content is retained after confirmed delivery.

**Deliverables:**

- Prompt after confirmed delivery to retain or delete the memory content.
- Default to no permanent content retention.
- Keep permitted operational metadata indefinitely.
- Exclude content, recipient identity, usernames, and other personal data from retained operational metadata.
- Preserve times, outcomes, counts, and safe error categories when useful.
- Keep delivery-attempt history consistent with confirmed, failed, and uncertain outcomes.

**Completion checks:**

- Unretained content is deleted after the user's post-send choice.
- Retained metadata cannot reconstruct the memory, recipient identity, or account identity.
- Automated tests verify both retain and delete paths.

## M12 — Saved-memory search and clearing

**Outcome:** Users can find and manage only the memories they opted to retain.

**Deliverables:**

- Add account-isolated keyword search over all searchable retained fields.
- Show the matching paragraph, memory date, and recipient name in each result.
- Allow email addresses to match without displaying them in previews.
- Keep normal stored-data searches within the two-second target.
- Add bulk clearing of all saved memories.
- Do not add individual memory deletion in v1.
- Ensure bulk clearing does not remove permitted anonymous operational metadata.

**Completion checks:**

- Search never returns another account's memory.
- Cleared memories disappear from search and cannot be recovered through normal product access.
- Search and clearing behavior have integration and authorization tests.

## M13 — Privacy-safe telemetry and account deletion

**Outcome:** v1 records useful operational signals without retaining private or linkable data.

**Deliverables:**

- Record only approved operational events, timings, outcomes, counts, and error categories.
- Exclude memory content, recipient identity, usernames, credentials, authorization data, and tokens.
- Add account deletion that removes all personal and linkable data.
- Leave only anonymous operational metadata after deletion.
- Add tests and a manual audit for logs, errors, and telemetry.

**Completion checks:**

- Test fixtures containing distinctive private values produce no matches in captured logs or telemetry.
- Account deletion makes login impossible and removes all account-owned personal data.
- Remaining metadata cannot be linked back to the deleted account.

## M14 — v1 acceptance run

**Outcome:** The complete v1 behavior is demonstrated and proven through sustained local use.

**Deliverables:**

- Record one end-to-end demonstration covering signup, login, setup, reminder delivery, draft interruption and recovery, review, sending, optional retention, keyword search, bulk clearing, logout, and rejected cross-account access.
- Run a new seven-consecutive-day local notification period after all v1 features are complete.
- Record failures and whether each one meets the PRD's restart rule.

**Run-restarting failures:**

- Loss of confirmed content.
- Sending without approval.
- Sending to an unreviewed recipient.
- Duplicate delivery caused by NoteCapsule.
- Cross-account data exposure.
- Private information in logs or telemetry.

**Release gate:**

- The recorded demonstration passes.
- Cross-account rejection is shown.
- Seven consecutive notification days pass locally without a run-restarting failure.
- Cosmetic defects are recorded but do not restart the run.

# v2

## M15 — MFA and OpenID Connect

**Outcome:** Accounts can use stronger authentication without weakening existing authorization.

**Deliverables:**

- Add MFA enrollment, verification, recovery, and removal flows.
- Add OpenID Connect login and account-linking behavior.
- Preserve username/password login unless a later approved product decision removes it.
- Keep all existing session limits and ownership checks.
- Store provider credentials and refresh tokens encrypted server-side and never expose them to React.
- Revoke and delete provider tokens on disconnect and account deletion.

**Completion checks:**

- MFA and OpenID Connect flows have success, failure, cancellation, and recovery tests.
- External identity cannot bypass account isolation.
- Sensitive authentication data does not appear in client responses, logs, errors, or telemetry.

## M16 — Create memory from date integrations

**Outcome:** A user can assemble source material from a date and turn a local summary into a draft.

**Deliverables:**

- Use the account's confirmed time zone to interpret the chosen date.
- Add independently authorized, read-only Calendar and Gmail connections.
- Show Gmail selection metadata or snippets before fetching a selected message's full body.
- Allow explicit selection of calendar events, Gmail messages, and manually selected or uploaded photos.
- Continue when one source is unavailable.
- Generate the summary locally.
- Add **Use as draft** as a separate action from **Approve & Send**.
- Delete temporary Google data and photos after completion, cancellation, or abandoned recovery.
- Exclude Maps Timeline, general Google Photos library search, and automatic sending.

**Completion checks:**

- Nothing is imported until the user explicitly selects it.
- Source failure does not block available sources.
- Temporary source data is cleaned up on every terminal path.
- A recorded demonstration shows date selection through Use as draft, review, and separate approval.

## M17 — Local AI service and safe execution

**Outcome:** Optional local AI can fail without disrupting the non-AI application.

**Deliverables:**

- Add a local Python AI service connected to Ollama `llama3.1:8b`.
- Keep every AI feature opt-in and off by default.
- Add cancellation and a two-minute timeout.
- Do not automatically retry an AI operation.
- Preserve user input during failure or cancellation and clean up temporary files afterward.
- Return understandable failures without model internals or private data.
- Keep the entire non-AI workflow usable when the AI service is stopped.

**Completion checks:**

- Disabled AI causes no model calls.
- Timeout, cancellation, malformed output, and unavailable-model tests pass.
- AI failure never blocks writing, review, sending, saved-memory search, or deletion.

## M18 — Embeddings and account-isolated vector storage

**Outcome:** Saved memories are indexed locally and remain isolated by account.

**Deliverables:**

- Generate embeddings only for opted-in saved memories.
- Begin with `nomic-embed-text:v1.5` as the replaceable embedding model.
- Begin with Qdrant as the replaceable local vector store.
- Apply account filtering to every index, retrieval, update, and deletion operation.
- Remove embeddings when saved memories are cleared or the account is deleted.
- Support complete re-indexing so models or stores can be replaced.
- Keep memory text and embeddings off hosted AI and vector services.

**Completion checks:**

- Unsaved and deleted memories cannot be retrieved.
- Cross-account vector retrieval occurs zero times in tests.
- A full re-index produces a usable, complete index without duplicate records.

## M19 — Semantic search, source-linked RAG, and memory chat

**Outcome:** Users can ask questions about their saved memories and verify every answer against sources.

**Deliverables:**

- Add semantic search over the authenticated account's saved memories.
- Retrieve account-filtered sources before generation.
- Add memory chat that answers only from supplied sources.
- Link every generated claim to its supporting saved memory.
- Say that no source was found when the retrieved evidence does not support an answer.
- Preserve keyword search and the complete non-AI workflow.
- Add a small evaluation dataset of known-source questions.

**Completion checks:**

- At least ten known-source RAG questions are evaluated.
- The correct source appears in the top five results for at least eight questions.
- Every generated claim links to a supplied source.
- Cross-account retrieval occurs zero times.
- Unsupported questions produce a no-source response instead of an invented answer.

## M20 — v2 release acceptance

**Outcome:** v2's authentication, integration, and AI boundaries are demonstrated as one stable local release.

**Deliverables:**

- Record the complete Create memory from date demonstration.
- Run and save the RAG evaluation results.
- Demonstrate that AI is off by default.
- Demonstrate cancellation, timeout, unavailable-AI fallback, and continued non-AI operation.
- Demonstrate MFA and OpenID Connect without cross-account access.
- Audit deletion of saved memories, embeddings, temporary Google data, and disconnected provider tokens.

**Release gate:**

- The Create memory from date demonstration passes.
- At least eight of ten known-source questions retrieve the correct source in the top five.
- Every generated claim is source-linked.
- Cross-account retrieval occurs zero times.
- The non-AI workflow remains fully usable with all AI services disabled.

# Explicitly deferred beyond v2

The following items are not part of these milestones unless the PRD is revised and approved:

- Voice-to-text.
- Grammar and tone suggestions.
- Account email verification and password recovery.
- Remember me and advanced password policies.
- Experimental login-abuse detection beyond v1 throttling or temporary blocking.
- General photo attachments outside Create memory from date.
- Mobile support.
- Public website hosting.
- Maps Timeline and general Google Photos library search.

