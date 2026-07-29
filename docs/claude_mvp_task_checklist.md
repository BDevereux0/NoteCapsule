# NoteCapsule — MVP Task Checklist (PR-sized)

Source docs: [`PRD.md`](./PRD.md), [`MILESTONES_claude.md`](./MILESTONES_claude.md),
[`prd_claude_review.md`](./prd_claude_review.md). Scope is **MVP only** (through M9 —
AWS learning deployment). V1/V2 are out of scope for this checklist.

`BUILD_PLAN.md` asks for PRs "you could walk someone through in 10 minutes." Each item
below is sized to that: one class + its test, one endpoint, one screen, one config
change — not a whole milestone at once. Milestones from `MILESTONES_claude.md` are kept
as section headers so you can still check completion against the PRD requirements they
cite.

**Current repo state this checklist starts from:** backend is a bare Spring Boot
`spring-boot-starter-web` skeleton (no JPA/MySQL yet); frontend is a bare Vite+React
scaffold; the two aren't wired together; there's no CI workflow yet. So M0 below starts
from scratch, not from "already done."

## Legend

- 🔧 = normal code PR
- 📝 = design-note PR (short doc, no code) — **resolves a specific blocking/significant
  issue from `prd_claude_review.md`**, referenced by number, and unblocks the code PRs
  that follow it. Treat these as real PRs: small, reviewed, merged before the dependent
  code lands.
- 🚦 = operational/manual step, not a PR (deployment, running the 7-day soak) — listed so
  the sequence stays complete, but there's nothing to review.

---

## M0 — Project scaffold

1. 🔧 **CI workflow.** GitHub Actions: on every PR, `mvn -B -q verify` for the backend and
   `npm ci && npm run build` for the frontend. Nothing to test yet — this is a compile/
   build gate, wired now so every PR from here on is covered (`BUILD_PLAN.md`'s CI
   checkpoint, pulled forward since M0 has no tests to key off yet).
2. 🔧 **MySQL + JPA wiring.** Add `spring-boot-starter-data-jpa` and a MySQL driver to
   `pom.xml`; add local datasource config (`application-local.properties`, gitignored
   credentials); confirm the app boots and connects with `ddl-auto=none` and no entities
   yet.
3. 🔧 **First vertical thread.** One `GET /api/health` endpoint returning a static JSON
   body — proves the backend serves something real, gives the frontend PR something to
   call.
4. 🔧 **Frontend ↔ backend wiring.** Vite dev-server proxy (`/api` → `localhost:8080`);
   `App.tsx` fetches `/api/health` and renders the result. This is the "runnable locally"
   proof for M0's definition of done.
5. 🔧 **README Getting Started.** Replace the "Coming soon" placeholder with real local
   run instructions (start MySQL, `./mvnw spring-boot:run`, `npm run dev`).

---

## Design note — scheduler policy (before M1)

6. 📝 **Scheduler edge-case policy.** Resolves review item **#6 (Scheduler determinism
   gaps)**. Pick and write down the rule for: spring-forward (configured time doesn't
   exist that day), fall-back (configured time occurs twice), missed occurrence during
   backend downtime (fire late on restart vs. skip), and a schedule/timezone edit that
   lands between "link generated" and "next link due." One page in `docs/`, referenced
   from PRD §6. This is what M1's tests get written against.

---

## M1 — Scheduled Discord notification

7. 🔧 **`OccurrenceEvaluator` (pure logic, TDD).** Given a `ReminderSchedule` (weekday,
   time, timezone) and an injected `java.time.Clock`, decide "is this instant the
   scheduled occurrence?" — including the DST/missed-occurrence rules from item 6.
   Write the tests first; no I/O, no Spring context. This is `BUILD_PLAN.md`'s
   clock-injection lesson, pulled forward from Phase 3 because MVP already needs it.
8. 🔧 **`DiscordNotifier` interface + `LoggingDiscordNotifier` fake.** Fake-first, same
   pattern as `BUILD_PLAN.md`'s `EmailSender`: the fake just logs "would post link" and
   records the call.
9. 🔧 **`@Scheduled` job wiring.** Ties the evaluator + real `Clock` + fake notifier
   together on a fixed interval; test with a fixed/controlled clock so the test doesn't
   depend on wall-clock time.
10. 🔧 **Real `DiscordWebhookNotifier`.** Actual webhook POST; webhook URL from an env var,
    never committed.

---

## Design note — MVP access-control model (before M2)

11. 📝 **Access-control model for the bearer link.** Resolves review item **#1 (MVP's only
    access control is an unguessable URL)**. Decide and write down: does the token gate
    only the initial draft-open, or every subsequent request in that session? Is
    anything reachable without a valid token? Is a token single-use, or reusable within
    its 3-hour window? What happens on an expired/invalid token (§6: "fail without
    disclosing private details" — decide what that response actually looks like). This
    is what the M2 token-validation PR implements.

---

## M2 — Memory capture form

12. 🔧 **`Occurrence`/token entity + generation.** Entity for the link (unguessable token,
    `created_at`, `expires_at` = +3h) created when M1's job fires; repository + unit
    test for token generation/expiry math.
13. 🔧 **Token validation.** Endpoint/filter that validates a token per the access-control
    decisions from item 11: exists, unexpired, correct failure response when not.
14. 🔧 **`Memory` entity + draft endpoint.** `Memory` (text, recipient, status=draft) +
    JPA repository + `POST /api/memories/draft` tied to the occurrence/token.
15. 🔧 **Capture form screen.** Text area + read-only default recipient + Save button,
    calls the draft endpoint; client-side nonblank check.
16. 🔧 **Memory validity service + tests.** The "is this memory valid to send?" service
    from `BUILD_PLAN.md`'s Phase 1 lesson — nonblank text, recipient present — unit
    tested directly (no HTTP).

---

## M3 — Review screen

17. 🔧 **Review endpoint.** `GET /api/memories/{id}` returns full text + recipient for
    review.
18. 🔧 **Review screen.** Shows complete text + recipient; "Back to editing" and
    "Approve & Send" buttons (Send itself stubbed until M4).
19. 🔧 **Approval-invalidation on edit.** Backend flag + test: "Back to editing" preserves
    content but invalidates any prior approval.
20. 🔧 **Cancel-memory flow.** "Keep editing" vs. "Discard draft"; discard endpoint clears
    the draft down to the content-free event required by §6.

---

## M4 — Approve & Send gate

21. 🔧 **Approve endpoint with double-send guard.** `POST /api/memories/{id}/approve`
    performs a guarded state transition (draft→approved) so a second concurrent call is
    a no-op/conflict, not a second approval. Unit test the guard directly (fire two
    "concurrent" calls, assert only one transition happens) — this is the part that
    actually has to work, not just the disabled button.
22. 🔧 **Approve & Send button.** Disabled while in flight, shows progress, re-disables
    after a successful click — client-side UX on top of the server-side guard from item
    21.

---

## Design note — delivery outcome contract (before M5)

23. 📝 **Delivery outcome evidence contract.** Resolves review item **#2 ("Unknown
    delivery" has no defined detection mechanism)** — described in the review as "the
    single hardest piece of engineering in the whole product." Decide: what counts as
    confirmed success (2xx + provider message ID, vs. "didn't throw")? What's confirmed
    failure (provider error response) vs. unknown (timeout/connection reset/crash
    mid-request)? Does every attempt get a stable ID that a later "save a copy" can
    reference, so duplicate-detection is a table lookup? This is what M5's
    `DeliveryAttempt` model and M6's outcome screens implement.

---

## M5 — Email delivery

24. 🔧 **`EmailSender` interface + `LoggingEmailSender` fake.** Fake-first, wire the full
    approve → "sent" flow against the fake; unit test.
25. 🔧 **`DeliveryAttempt` entity.** Attempt ID, memory ID, status
    (pending/confirmed/failed/unknown), timestamp, error category — the schema decided
    in item 23.
26. 🔧 **Real `SmtpEmailSender`.** Real SMTP against a dev inbox (Mailtrap or a throwaway
    Gmail app password); credentials via env vars, not committed.
27. 🔧 **Wire approve → send → record outcome.** Approve endpoint calls the sender,
    persists a `DeliveryAttempt`, and maps exceptions/timeouts to
    confirmed-failure-vs-unknown per item 23's contract.

---

## M6 — Delivery outcome handling

28. 🔧 **Outcome status endpoint.** Exposes the three-way state (confirmed / failed /
    unknown) for a memory's latest attempt.
29. 🔧 **Confirmed-failure screen.** Retry sending / Back to editing / Keep for later,
    each wired to its endpoint.
30. 🔧 **Unknown-outcome screen.** Resend / Resend-and-save-a-copy, with the "may have
    already sent, resending may duplicate it" warning copy from §5.
31. 🔧 **Resend endpoint using the attempt-ID scheme.** Uses item 23's stable IDs so
    "save a copy" is a lookup, not a guess — never an automatic retry.
32. 🔧 **Failure-path tests.** Fake `EmailSender` that throws distinct
    exception types for confirmed-failure vs. unknown; assert the right state lands.

---

## M7 — Preservation after failure/uncertain delivery

33. 🔧 **Restart-survival integration test.** Write a memory + attempt, kill the Spring
    context, reload, assert both are still present — against the **real MySQL**, not
    H2 (`BUILD_PLAN.md`: test against the database you actually ship).
34. 🔧 **Resume-in-progress on load.** Frontend detects an existing draft/failed memory
    for the current token and resumes it instead of showing a blank form.
35. 🔧 **Fresh-approval-after-edit test.** Confirms editing content after a failure resets
    the `approved` flag (extends item 19's invalidation logic to the failure path).

---

## M8 — Seven-day local readiness run

36. 📝 **Readiness-run checklist doc.** Resolves review item **#5 (the 7-day gate doesn't
    have to exercise the flagship flow)**. Write down the actual bar before running the
    week: a minimum number of complete real sends, at least one restart-recovery
    exercise, at least one deliberately triggered confirmed-failure, and one
    deliberately triggered unknown-outcome — not just "the notification arrived on
    schedule."
37. 🚦 **Run the 7 days.** Execute against the checklist from item 36. Not a PR.
38. 🔧 **Results write-up.** Short doc summarizing the run against the checklist, with
    evidence (logs/screenshots) — the artifact that actually closes M8's definition of
    done in `MILESTONES_claude.md` §9.

---

## M9 — AWS learning deployment

39. 🔧 **Deploy config, network-restricted per item 11.** Dockerfile/deploy notes +
    whatever network restriction (security group allowlist, etc.) item 11 decided the
    AWS deployment needs — "briefly deployed and removed" reduces the exposure window,
    it doesn't replace the access-control decision (review item #1).
40. 🚦 **Deploy, verify, and explain the core flow on AWS.** Manual step, not a PR.
41. 🚦 **Tear down the deployment.**
42. 🔧 **Closing summary doc.** What was deployed, what was verified, confirmation the
    deployment was removed — closes MVP per PRD §9.

---

## Deferred out of this checklist (V1 scope, not MVP)

Review items #3 (backup/durability), #4 (post-delivery retention limbo), #9 (password
floor), #10 (metadata schema) are tagged in `prd_claude_review.md` against V1
requirements (retention, auth) that don't exist yet in MVP — MVP has no auth and no
opt-in retention choice, only "preserve on failure" (M7). Pick these up when V1 task
planning starts. Items #7 (§10 doc structure) and #8 (soft qualifiers) are cheap PRD
wording fixes that can land whenever, independent of this sequence.
