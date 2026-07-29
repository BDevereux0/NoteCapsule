# NoteCapsule Milestones

Derived from [`PRD.md`](./PRD.md). Each milestone is a build section — do them in order
within a release, and finish a release's milestones before starting the next release.
Every milestone lists the PRD requirements it must satisfy so you can check completion
against the document itself, not against this summary.

Stack per PRD §10: React, MySQL, Maven. (JavaFX, SQLite, and Gradle are scrapped —
ignore any older planning docs that still reference them.)

---

## MVP — unauthenticated core flow (PRD §2, §6, §8, §9)

MVP has no authentication. Goal: one operator, one working Discord-to-email loop that
survives failure, proven over a real 7-day run, then briefly deployed to AWS.

1. **M0 — Project scaffold**
   React frontend + Maven-built backend + MySQL, wired together and runnable locally.
   No product behavior yet — just a skeleton that boots.

2. **M1 — Scheduled Discord notification**
   A scheduler posts a private Discord link at the configured time (§4). No login
   concept yet (MVP has none), but the link is the entry point to the flow.

3. **M2 — Memory capture form**
   Following the link opens a form where the operator writes a text memory (§4, §5).
   Memories are text-only and nonblank (§6, Configuration/memories/links).

4. **M3 — Review screen**
   Show the complete memory text and the recipient before send (§5: "Review shows
   complete content and every recipient"). Back to editing preserves content and
   invalidates any prior approval.

5. **M4 — Approve & Send gate**
   Only Approve & Send authorizes delivery (§6, Delivery/retention/search). Disabled
   while sending, shows progress, and repeated activation is guarded against
   double-send (§5, §7).

6. **M5 — Email delivery**
   Send the approved memory by email to the recipient (§2, §4).

7. **M6 — Delivery outcome handling**
   Distinguish confirmed failure from unknown outcome (§6). Confirmed failure offers
   Retry sending / Back to editing / Keep for later. Unknown outcome warns that the
   original may have succeeded and offers Resend or Resend and save a copy — never an
   automatic retry (§5, §6).

8. **M7 — Preservation after failure/uncertain delivery**
   Confirmed drafts and delivery attempt history survive refresh, restart, and
   temporary network loss (§5, §7). Editing after a failure requires fresh approval.

9. **M8 — Seven-day local readiness run**
   Run the full scheduled flow locally for seven consecutive days without a critical
   failure (§8, §9).

10. **M9 — AWS learning deployment**
    Deploy briefly to AWS, verify and explain the core flow, then remove the
    deployment (§8, §9). This closes out MVP.

---

## V1 — authenticated, multi-account, local (PRD §2, §6, §8, §9)

V1 is local, adds accounts, and has no AWS or AI scope. Build in this order because
auth and isolation (V1-1/V1-2) are load-bearing for everything after them.

1. **V1-1 — Auth foundation**
   Signup, login, logout with unique username + nonblank password, secure one-way
   password hashing, server-side session cookie. Sessions expire after 30 idle
   minutes or 8 total hours; logout invalidates the server session. Unknown username
   and wrong password return the same message; repeated failures trigger deterministic
   throttling/blocking (§6, Authentication and authorization).

2. **V1-2 — Authorization & account isolation**
   Every protected request verifies ownership; altered identifiers never expose
   another account's data (§6). This is what the V1 demo (§9) is required to prove:
   signup through cross-account rejection.

3. **V1-3 — Address book & recipients**
   Address-book entries with name + valid email. A default recipient is required for
   reminders; each memory may select one or more recipients (§6, §2).

4. **V1-4 — Schedule & timezone configuration**
   Accounts select reminder weekdays, one shared time, and a detected/confirmable/
   correctable time zone (§6, §5).

5. **V1-5 — Account-owned reminder links**
   Reminder links require login (§6). A scheduled Discord link opens the owning
   account's unfinished draft, or a new form with its default recipient (§4). Links
   last three hours, use unguessable tokens, are never logged, and fail without
   disclosing private details (§5, §6).

6. **V1-6 — Autosave & recovery**
   Text autosaves ~2s after typing pauses; recipient changes save immediately; focus
   loss/navigation triggers a final save. Saving/Saved/Save failed states are visible.
   Refresh, restart, or temporary network failure restores the latest confirmed
   autosave; failed saves keep text visible and warn before navigation (§5, §7).

7. **V1-7 — Draft lifecycle**
   At most one unfinished draft per account (§6). Cancel memory offers Keep editing or
   Discard draft; a discarded draft leaves only a content-free, non-personal event
   (§5, §6).

8. **V1-8 — Overdue recovery links**
   When a Discord link expires with unfinished work, send one enabled overdue email
   with a recovery link valid until the next scheduled link. New occurrences,
   delivery, or discard invalidate earlier applicable links (§5, §6).

9. **V1-9 — Retention choice for saved memories**
   Retention after delivery is opt-in. Retained operational metadata excludes
   content, recipient identity, usernames, and other personal data, but may include
   times, outcomes, counts, and error categories (§6).

10. **V1-10 — Keyword search**
    Search all searchable retained fields, scoped to the authenticated account only.
    Results show the matching paragraph, date, and recipient name; email addresses may
    be searchable but are never previewed (§6).

11. **V1-11 — Telemetry & quality guardrails**
    Logs, telemetry, and errors exclude private content, identities, credentials,
    authorization data, and tokens; errors stay actionable without secrets, stack
    traces, or raw provider responses (§7). Ordinary pages and searches respond within
    two seconds excluding provider delays; core flows are keyboard-accessible with
    visible labels/focus/textual status (§7).

12. **V1-12 — Bulk clearing & account deletion**
    Bulk clearing of all saved memories (no individual deletion in V1). Account
    deletion removes personal/linkable data and leaves only anonymous metadata, which
    is retained indefinitely and cannot be manually cleared otherwise (§6).

13. **V1-13 — Recorded demo + new 7-day local run**
    Record signup through cross-account rejection. Run a fresh 7-day local
    notification run; it restarts (after correction) only for confirmed-content loss,
    sending without approval/to an unreviewed recipient, duplicate email, cross-account
    leakage, or private data in logs/telemetry — not for cosmetic defects. Sending on
    every notification is not required (§9). This closes out V1.

---

## V2 — local, opt-in AI (PRD §2, §6, §8, §9, §10)

V2 stays local. AI is off by default and never blocks the non-AI workflow. Build
MFA/OIDC first since they touch the same auth surface as V1; build the AI pipeline in
data order (source access → generation → embeddings → retrieval → chat).

1. **V2-1 — MFA & OpenID Connect**
   Add multi-factor authentication and OIDC login on top of the V1 auth system (§8).

2. **V2-2 — Create memory from date**
   Uses the confirmed account time zone; independently authorized read-only Calendar
   and Gmail access; explicit selection of events, messages, and manually
   selected/uploaded photos. Gmail exposes only selection metadata/snippets first,
   fetching full bodies after selection. One unavailable source doesn't block others;
   Maps Timeline and general Google Photos library search are excluded. Temporary
   Google data/photos are deleted after completion, cancellation, or abandoned
   recovery. Refresh tokens are encrypted server-side, never exposed to React, and
   revoked/deleted on disconnect (§10).

3. **V2-3 — Local generation service**
   Local Python service using Ollama `llama3.1:8b` produces a local summary from
   selected sources; "Use as draft" hands off into the normal draft flow, which still
   requires its own separate Approve & Send (§10).

4. **V2-4 — Embeddings & vector storage**
   Only saved memories are embedded. `nomic-embed-text:v1.5` and Qdrant are controlled
   initial placeholders; any replacement must stay local, pass evaluation, preserve
   account filtering and deletion, and support full re-indexing (§10).

5. **V2-5 — Semantic, source-linked RAG search**
   Retrieval is account-filtered; every generated claim links to supplied sources; an
   unsupported answer says no source was found (§10).

6. **V2-6 — Memory chat**
   Chat over the RAG pipeline. AI is cancellable, times out after two minutes with no
   automatic retry, and never blocks non-AI use (§10).

7. **V2-7 — AI-aware deletion & re-indexing**
   Deletion removes embeddings along with the underlying memory; replacement
   embedding/vector components support complete re-indexing (§10).

8. **V2-8 — Recorded demo**
   Record a Create memory from date demonstration and run at least ten known-source
   RAG questions. The correct source must appear in the top five for at least eight of
   them, every generated claim must link to supplied sources, and cross-account
   retrieval must occur zero times (§9). This closes out V2.
