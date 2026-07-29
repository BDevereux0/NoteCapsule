# NoteCapsule MVP Task Checklist

This checklist turns `docs/PRD.md` into small, ordered pull requests. It also incorporates the
MVP risks identified in `docs/codex_prd_review.md`. The PRD remains the product source of truth.

## How to use this checklist

- Treat each `PR-###` item as one pull request.
- Aim to explain the purpose, main code path, tests, and tradeoffs in ten minutes.
- If a PR grows beyond one behavior or needs more than a few closely related tests, split it
  before review.
- Keep documentation, schema changes, backend behavior, and frontend behavior separate unless
  combining them is necessary to demonstrate one thin vertical slice.
- Every code PR must add or update the smallest meaningful test at the same time.
- Use the pull-request template to record what you wrote, how AI helped, what you can explain,
  and what remains unclear.
- Do not begin a task whose `Blocked by` items are incomplete.
- Items marked **Decision gate** must update the PRD or a linked design note with an explicitly
  approved decision; the checklist does not silently resolve them.

## MVP boundary

The MVP covered here is:

> One unauthenticated operator receives a scheduled Discord notification containing a private,
> expiring link; writes a nonblank text memory; reviews the complete memory and recipient;
> explicitly approves it; and sends it by email. Confirmed work survives relevant failures, the
> flow is proven locally for seven days, and the MVP is briefly deployed to AWS and removed.

Authentication, multiple accounts, address books, multiple recipients, autosave-after-pause,
overdue recovery email, saved-memory search, AI, voice-to-text, photos, mobile support, and
long-lived public hosting are not MVP tasks.

## 0. Resolve MVP blockers before implementation

- [ ] **PR-001 — Define the MVP access-control contract.** **Decision gate.**
  - Decide which pages are reachable without a bearer token, what one token authorizes, whether
    it is reusable, what invalidates it, and how the temporary AWS deployment is network
    restricted.
  - Specify safe behavior for invalid, expired, and superseded tokens.
  - Update `docs/PRD.md` and remove the review ambiguity without adding v1 authentication.
  - **Done when:** every MVP route can be classified as public, direct-local-only, or
    token-authorized, and token lifecycle behavior is testable.

- [ ] **PR-002 — Define the delivery-outcome state machine.** **Decision gate.**
  - Define the evidence for pending, confirmed success, confirmed failure, and unknown outcome.
  - Define a stable delivery-attempt identifier, restart reconciliation, attempt history, and
    the meaning of an operator-authorized resend.
  - Clarify whether an acknowledged resend after an unknown outcome is an acceptable duplicate
    for the MVP success gate.
  - **Done when:** every send outcome and allowed next action can be drawn as a finite state
    transition and tested.

- [ ] **PR-003 — Define the MVP draft persistence boundary.** **Decision gate.**
  - Resolve the conflict between MVP recovery and v1 autosave.
  - Define exactly what action makes draft content “confirmed” in MVP, when it is persisted, and
    what refresh, backend restart, browser closure, and temporary network failure restore.
  - **Done when:** MVP persistence behavior is distinct from v1 timed autosave and has objective
    acceptance criteria.

- [ ] **PR-004 — Define post-send content retention for MVP.** **Decision gate.**
  - Decide what happens to content after confirmed delivery and after a crash or browser closure
    before any retention choice is completed.
  - If the MVP does not offer retention choice, state its deletion behavior explicitly.
  - If it does, define the durable awaiting-choice state, recovery path, and deletion deadline.
  - **Done when:** confirmed delivery can never cause accidental indefinite retention or
    premature loss contrary to the approved product behavior.

- [ ] **PR-005 — Strengthen the seven-day readiness evidence.** **Decision gate.**
  - Define the number of complete real sends required during the run.
  - Include at least one restart-recovery exercise and one controlled delivery-failure exercise,
    or document a confirmed alternative.
  - Define the dated evidence recorded each day and the restart rule after a critical failure.
  - **Done when:** the readiness run proves more than scheduler uptime.

## 1. Repository and build foundation

- [ ] **PR-006 — Document the current architecture and developer commands.**
  - Correct obsolete JavaFX, SQLite, and Gradle assumptions for the MVP implementation.
  - Document Java 21, Spring Boot/Maven, React/TypeScript/Vite, MySQL, and separate dev servers.
  - Add exact local build, test, and start commands; never include real credentials.
  - **Done when:** a new contributor can explain and start each application layer.

- [ ] **PR-007 — Add a backend smoke test.**
  - Add the Spring Boot test dependency and one context-start test.
  - **Done when:** `./mvnw test` runs locally and fails if the application cannot start.

- [ ] **PR-008 — Add the frontend test runner and one smoke test.**
  - Configure the chosen React test runner and test the current root component.
  - **Done when:** one documented npm command runs the frontend test suite.

- [ ] **PR-009 — Add backend CI.**
  - Add a GitHub Actions job for the supported JDK and Maven tests.
  - Cache only safe build dependencies.
  - **Done when:** the backend check runs on pull requests.

- [ ] **PR-010 — Add frontend CI.**
  - Add install, test, type-check, and production-build steps using the lockfile.
  - **Done when:** frontend failures independently block the frontend CI job.

- [ ] **PR-011 — Add safe local configuration templates.**
  - Add placeholder configuration for database, Discord, and SMTP values.
  - Extend `.gitignore` for secret files and document how local values are supplied.
  - **Done when:** repository search finds no real secret and the application reports missing
    required configuration without printing it.

- [ ] **PR-012 — Add local MySQL orchestration.**
  - Add the smallest Docker Compose database service and non-secret example variables.
  - Add a health check and startup instructions.
  - **Done when:** MySQL starts locally and can be stopped without deleting user data by default.

- [ ] **PR-013 — Connect Spring Boot to MySQL.**
  - Add the JDBC/JPA dependencies and environment-backed connection configuration.
  - Add a focused application-start integration test.
  - **Done when:** the backend starts against the local database without hard-coded credentials.

- [ ] **PR-014 — Add versioned schema migrations.**
  - Add the migration tool and an initial minimal schema/version marker.
  - Do not design later tables in this PR.
  - **Done when:** a clean database migrates on startup and the migration is repeatable.

## 2. First vertical slice: write, review, approve, fake send

- [ ] **PR-015 — Add the text-memory domain model and validation.**
  - Represent an MVP text draft and reject blank content.
  - Add unit tests for blank, whitespace-only, and valid text.
  - **Done when:** validation is independent of HTTP, React, and SMTP.

- [ ] **PR-016 — Add the email-sender port and fake implementation.**
  - Define the narrow delivery interface and a fake/logging implementation.
  - Ensure the fake does not write actual memory or recipient data to normal logs.
  - **Done when:** a unit test proves the fake records a call without network access.

- [ ] **PR-017 — Add the draft application service.**
  - Create/update the one MVP draft through constructor-injected dependencies.
  - Keep HTTP and persistence details out of the core service.
  - **Done when:** service tests cover creation, update, and blank-content rejection.

- [ ] **PR-018 — Add draft HTTP endpoints.**
  - Expose only the create/read/update operations required by the first slice.
  - Return validation errors in a stable, private response shape.
  - **Done when:** controller tests cover valid input, invalid input, and missing draft behavior.

- [ ] **PR-019 — Add the memory editor UI.**
  - Add a labeled text area, validation feedback, and a continue-to-review action.
  - **Done when:** a component test proves blank text cannot advance and valid text can.

- [ ] **PR-020 — Connect the editor to the draft API.**
  - Save through the explicit MVP confirmation behavior chosen in PR-003.
  - Show saving, success, and actionable failure feedback.
  - **Done when:** a mocked API test covers successful and failed confirmation.

- [ ] **PR-021 — Add the review read model and endpoint.**
  - Return the complete confirmed text and configured recipient needed for review.
  - **Done when:** the endpoint never returns a send-authorized state merely because review was
    opened.

- [ ] **PR-022 — Add the review UI.**
  - Show complete memory text and recipient, plus Back to editing and Approve & Send controls.
  - **Done when:** a component test proves the visible review data matches the API response.

- [ ] **PR-023 — Add approval-state rules.**
  - Represent explicit approval and invalidate it after content or recipient changes.
  - Add focused state-transition unit tests.
  - **Done when:** stale approval cannot authorize a delivery.

- [ ] **PR-024 — Send through the fake sender after current approval.**
  - Add the application-service command and endpoint.
  - Reject unapproved and stale-approved requests.
  - **Done when:** service tests prove only current approval invokes the sender.

- [ ] **PR-025 — Prevent repeated send activation.**
  - Disable the UI action while a request is active and guard duplicate commands server-side.
  - **Done when:** rapid repeated activation creates one fake delivery attempt.

- [ ] **PR-026 — Show fake-delivery progress and success.**
  - Add sending and confirmed-success UI states with text, not color alone.
  - **Done when:** a browser/component test walks editor → review → approve → fake success.

## 3. Recipient settings and durable MVP drafts

- [ ] **PR-027 — Add the single-recipient schema migration.**
  - Store the MVP recipient name and email separately from draft content.
  - **Done when:** migration tests or startup verification succeed on a clean database.

- [ ] **PR-028 — Add recipient validation and repository tests.**
  - Validate nonblank name and email syntax at the backend boundary.
  - **Done when:** tests cover valid storage and invalid rejection without exposing raw SQL errors.

- [ ] **PR-029 — Add recipient settings API.**
  - Support reading and updating the one MVP recipient.
  - **Done when:** API tests cover initial-unconfigured, valid update, and invalid update states.

- [ ] **PR-030 — Add recipient settings UI.**
  - Add labeled name/email fields and visible saved/error status.
  - **Done when:** component tests cover valid save and validation failure.

- [ ] **PR-031 — Preselect the configured recipient in a new draft.**
  - Block review/send with an actionable message when recipient setup is incomplete.
  - **Done when:** service tests prove the reviewed and deliverable recipient is the configured
    recipient.

- [ ] **PR-032 — Add the MVP draft schema migration.**
  - Store one unfinished draft and only the fields required by approved MVP behavior.
  - **Done when:** the schema enforces or supports the single-draft invariant.

- [ ] **PR-033 — Add the persistent draft repository.**
  - Implement save/load using MySQL behind the existing application boundary.
  - **Done when:** an integration test persists and reloads confirmed content.

- [ ] **PR-034 — Restore the confirmed draft on page refresh.**
  - Load the latest confirmed draft when the editor opens.
  - **Done when:** a browser-level test refreshes and sees the confirmed content.

- [ ] **PR-035 — Recover the confirmed draft after backend restart.**
  - Add a targeted integration test that restarts the application or repository boundary.
  - **Done when:** confirmed content survives and unconfirmed behavior matches PR-003.

- [ ] **PR-036 — Protect visible edits after a save failure.**
  - Keep the operator’s text in the browser and show actionable retry/navigation warning.
  - **Done when:** a browser test simulates a failed save and proves the text remains visible.

## 4. Real delivery and recovery

- [ ] **PR-037 — Add the delivery-attempt schema migration.**
  - Persist the identifiers and states approved in PR-002.
  - Keep attempt metadata separate from private memory content.
  - **Done when:** legal state values and draft/attempt relationships are enforced.

- [ ] **PR-038 — Implement the delivery state machine.**
  - Encode only the allowed transitions from PR-002.
  - **Done when:** table-driven unit tests cover every legal and rejected transition.

- [ ] **PR-039 — Add the Mailpit development service.**
  - Add local SMTP capture to Docker Compose and document its web inbox.
  - **Done when:** a non-production smoke check can deliver a harmless test message.

- [ ] **PR-040 — Add the SMTP sender adapter.**
  - Map provider results to confirmed success, confirmed failure, or unknown using PR-002.
  - Sanitize exceptions before they cross the adapter boundary.
  - **Done when:** adapter tests use a fake SMTP boundary and cover all three outcomes.

- [ ] **PR-041 — Select fake or SMTP delivery by configuration.**
  - Keep the fake as the safe default for automated tests.
  - **Done when:** configuration tests prove the correct implementation is injected.

- [ ] **PR-042 — Persist delivery attempts around sending.**
  - Record pending before the external call and the resulting state after it.
  - **Done when:** interruption tests preserve enough state to apply PR-002 recovery behavior.

- [ ] **PR-043 — Add confirmed-failure actions.**
  - Offer Retry sending, Back to editing, and Keep for later.
  - Editing must require fresh review and approval while preserving attempt history.
  - **Done when:** service and UI tests cover each action.

- [ ] **PR-044 — Add unknown-delivery actions.**
  - Warn that the original may have succeeded and require explicit acknowledgment before resend.
  - Implement the exact resend choices approved in PR-002.
  - **Done when:** unknown outcomes are never automatically retried.

- [ ] **PR-045 — Recover interrupted delivery state after restart.**
  - Reconcile pending attempts according to PR-002 without silently resending.
  - **Done when:** a restart integration test produces the approved safe state.

- [ ] **PR-046 — Implement post-success retention behavior.**
  - Implement only the deletion/retention contract approved in PR-004.
  - **Done when:** success, browser-close, and backend-restart tests prove the content lifecycle.

- [ ] **PR-047 — Add the Gmail SMTP production profile.**
  - Read the app password and sender configuration from ignored secret inputs.
  - Keep Mailpit as the development path.
  - **Done when:** configuration validation fails safely and never logs credential values.

## 5. Scheduling, private links, and Discord

- [ ] **PR-048 — Add reminder-settings schema and domain validation.**
  - Store MVP weekdays, one reminder time, and one confirmed time zone.
  - **Done when:** unit tests cover selected/unselected days and invalid time zones.

- [ ] **PR-049 — Add reminder settings API.**
  - Read and update the MVP schedule.
  - **Done when:** API tests cover valid configuration and actionable validation errors.

- [ ] **PR-050 — Add reminder settings UI.**
  - Provide keyboard-accessible weekday, time, and time-zone controls.
  - **Done when:** component tests prove saved values round-trip.

- [ ] **PR-051 — Add the scheduled-occurrence model.**
  - Represent one logical occurrence independently of scheduler invocations.
  - Add a uniqueness rule that supports duplicate prevention.
  - **Done when:** repeated creation attempts resolve to one occurrence.

- [ ] **PR-052 — Build due-occurrence calculation test-first.**
  - Inject `Clock`; cover before, at, and after the configured time on selected and unselected
    weekdays.
  - **Done when:** pure unit tests require no Spring context or real waiting.

- [ ] **PR-053 — Define and test DST behavior.**
  - Implement the approved behavior for skipped and repeated local times.
  - **Done when:** tests cover both a spring-forward gap and fall-back repetition.

- [ ] **PR-054 — Define and test scheduler catch-up behavior.**
  - Implement the approved behavior for backend downtime and startup after the scheduled instant.
  - **Done when:** tests cover on-time, late startup, and too-late startup.

- [ ] **PR-055 — Add the scheduler trigger.**
  - Have Spring poll for due occurrences and delegate to the tested domain/service logic.
  - **Done when:** repeated polling cannot create duplicate occurrences.

- [ ] **PR-056 — Add private-token generation and hashed storage.**
  - Generate cryptographically unguessable tokens and avoid storing/logging the raw token beyond
    what the approved design requires.
  - **Done when:** tests cover uniqueness and demonstrate redacted logging.

- [ ] **PR-057 — Add three-hour token expiry.**
  - Evaluate expiry through the injected clock.
  - **Done when:** boundary tests cover just before, exactly at, and after expiry.

- [ ] **PR-058 — Add invalidation and supersession rules.**
  - Implement the token lifecycle approved in PR-001.
  - **Done when:** tests cover reuse, successful delivery, discard, and newer occurrence as
    applicable to that decision.

- [ ] **PR-059 — Add the token-resolution endpoint.**
  - Resolve valid tokens to the correct unfinished draft or a new draft with configured recipient.
  - Return one privacy-safe failure shape for invalid/expired/superseded cases.
  - **Done when:** endpoint tests prove tokens cannot grant broader access than PR-001 permits.

- [ ] **PR-060 — Add token-entry frontend routing.**
  - Open the correct editor state from a reminder link and show a safe invalid-link page.
  - **Done when:** browser tests cover valid and rejected links.

- [ ] **PR-061 — Add the Discord notification adapter.**
  - Send only the approved reminder text and private link through an injected client.
  - Sanitize failures and never log the token-bearing URL.
  - **Done when:** adapter tests cover accepted, rejected, and unknown webhook outcomes.

- [ ] **PR-062 — Connect occurrences to Discord delivery.**
  - Record notification state around the webhook call and apply the approved lost-response rule.
  - **Done when:** repeated scheduler runs do not intentionally emit a second notification for one
    occurrence.

- [ ] **PR-063 — Preserve direct writing when Discord is unavailable.**
  - Show an actionable notification failure without disabling editor, review, or email delivery.
  - **Done when:** an integration/browser test completes a direct fake-send flow during Discord
    failure.

## 6. MVP safety and end-to-end verification

- [ ] **PR-064 — Add global privacy-safe API errors.**
  - Convert expected validation/provider errors to actionable messages.
  - Exclude stack traces, raw provider responses, memory text, recipient identity, credentials,
    and tokens.
  - **Done when:** representative API tests assert both usefulness and redaction.

- [ ] **PR-065 — Add logging redaction tests and review notes.**
  - Capture logs for draft, token, Discord, and SMTP failure paths.
  - Document the reviewed log fields.
  - **Done when:** automated assertions find no prohibited private values.

- [ ] **PR-066 — Add the critical approval/recipient browser test.**
  - Cover edit → review → edit recipient/content → stale approval rejected → re-review → send.
  - **Done when:** the real React/Spring boundary cannot send unreviewed data.

- [ ] **PR-067 — Add the repeated-control delivery test.**
  - Exercise repeated browser activation plus repeated backend requests for one approval.
  - **Done when:** NoteCapsule creates no unintended duplicate delivery.

- [ ] **PR-068 — Add the refresh/restart recovery browser test.**
  - Cover the confirmation boundary from PR-003 through refresh and backend restart.
  - **Done when:** confirmed content is recovered and visible.

- [ ] **PR-069 — Add the delivery-failure recovery browser test.**
  - Exercise confirmed failure, unknown outcome, editing, fresh approval, and chosen resend path.
  - **Done when:** every state displays the approved next actions and no automatic resend occurs.

- [ ] **PR-070 — Complete the accessibility pass.**
  - Add visible labels/focus, keyboard operation, sensible focus movement, and textual asynchronous
    status to all MVP screens.
  - **Done when:** the complete core flow works without a mouse and does not depend on color or
    animation alone.

- [ ] **PR-071 — Add an ordinary-page performance check.**
  - Measure local settings, editor, draft load, and token-resolution requests without provider
    delay.
  - **Done when:** the documented check normally stays within the PRD’s two-second requirement.

- [ ] **PR-072 — Document and run the local MVP demonstration.**
  - Use a repeatable script/checklist for settings → scheduled Discord link → write → review →
    approve → real delivery → approved retention outcome.
  - **Done when:** evidence includes the build/version, date, observed outcome, and no exposed
    secrets or private content.

## 7. Seven-day readiness run

These are operational tasks, not pull requests. Fixes discovered during the run must each use a
new, narrowly scoped PR and must restart the run when the approved gate requires it.

- [ ] **RUN-001 — Prepare the dated readiness log.**
  - Record application version, scheduled time, notification result, required real-send days,
    recovery/failure exercise results, and defect classification.

- [ ] **RUN-002 — Complete readiness day 1.**
- [ ] **RUN-003 — Complete readiness day 2.**
- [ ] **RUN-004 — Complete readiness day 3.**
- [ ] **RUN-005 — Complete readiness day 4.**
- [ ] **RUN-006 — Complete readiness day 5.**
- [ ] **RUN-007 — Complete readiness day 6.**
- [ ] **RUN-008 — Complete readiness day 7.**
- [ ] **RUN-009 — Confirm the local release gate.**
  - Verify seven consecutive scheduled days, required real sends, restart recovery, controlled
    failure evidence, and zero unresolved critical failures.

## 8. Brief AWS learning deployment

Keep these PRs provider-service-specific and small after choosing the AWS deployment design. The
deployment must obey the access-control contract from PR-001 and is temporary, not public product
hosting.

- [ ] **PR-073 — Add the AWS deployment design note.**
  - Identify services, network boundary, DNS/TLS choice if applicable, secrets path, data
    lifecycle, estimated cost, verification steps, and complete teardown inventory.
  - **Done when:** another engineer could identify every billable resource and explain how the
    unauthenticated MVP is protected.

- [ ] **PR-074 — Add container builds for backend and frontend.**
  - Keep the images separate if that preserves the local architecture.
  - **Done when:** both build reproducibly and contain no baked-in secrets.

- [ ] **PR-075 — Add production container configuration.**
  - Supply runtime configuration and health checks without committing values.
  - **Done when:** the full stack starts in a production-like local test.

- [ ] **PR-076 — Add AWS infrastructure for compute and networking.**
  - Create only the compute/network resources approved in PR-073.
  - **Done when:** validation/plan output is reviewable without deploying.

- [ ] **PR-077 — Add AWS data and secrets resources.**
  - Create only the database/storage and Secrets Manager resources required by the MVP.
  - Define deletion behavior explicitly.
  - **Done when:** validation/plan output shows encryption and no secret plaintext.

- [ ] **PR-078 — Add deployment and verification instructions.**
  - Document versioned deploy, health check, core-flow verification, log redaction check, rollback,
    and teardown.
  - **Done when:** the instructions reference every resource from PR-073.

- [ ] **AWS-001 — Deploy the reviewed MVP version.**
- [ ] **AWS-002 — Verify and record the protected core flow.**
  - Confirm Discord link access, draft recovery, review/approval, real delivery, and privacy-safe
    logs; record learning evidence without private content.
- [ ] **AWS-003 — Remove the deployment.**
  - Execute the reviewed teardown and remove temporary secrets/data according to policy.
- [ ] **AWS-004 — Verify teardown and billing state.**
  - Check every item in the teardown inventory and confirm that no unintended billable resource,
    public endpoint, credential, or retained private data remains.

## MVP complete

- [ ] All decision gates are reflected in the approved PRD/design notes.
- [ ] All merged code PRs passed backend and frontend CI.
- [ ] The recorded local end-to-end demonstration passed.
- [ ] Seven consecutive local scheduled days passed under the strengthened evidence rules.
- [ ] No critical failure remains unresolved.
- [ ] The brief AWS core-flow verification passed.
- [ ] AWS resources, secrets, endpoints, and temporary private data were removed and verified.
- [ ] The release is tagged only after the final evidence is reviewed.
