# NoteCapsule Product Requirements Document

## 1. Problem and motivation

NoteCapsule reduces the repetitive Gmail workflow so memories about loved ones are more likely to be recorded while fresh. Recipients accumulate those memories to revisit at meaningful milestones. The project also supports software-engineering learning, but learning goals do not replace product requirements.

## 2. Goals and non-goals

The MVP sends a scheduled Discord notification with a private link, lets the operator write a text memory, shows the memory and recipient for review, requires approval, sends it by email, and preserves it after failed or uncertain delivery. MVP has no authentication.

V1 adds signup, login, logout, authorization, isolated accounts, address books, multiple recipients, configurable schedules and time zones, autosave and recovery, overdue recovery links, optional saved memories, account-isolated keyword search, telemetry, bulk clearing, and account deletion. V1 contains no AI.

V2 is local and adds opt-in AI: Create memory from date, local generation, embeddings, vector storage, semantic search, source-linked RAG, memory chat, MFA, and OpenID Connect. The non-AI workflow remains fully usable.

NoteCapsule is never a public website. Deferred nice-to-haves are voice-to-text, grammar and tone suggestions, account email verification, password recovery, Remember me, advanced password policies, experimental login-abuse detection, general photo attachments, and mobile support.

## 3. Users and context

The operator writes memories and controls delivery. Recipients receive email but do not operate NoteCapsule through it. MVP has one unauthenticated operator. In v1, each authenticated account is an isolated profile with separate drafts, memories, recipients, settings, history, search, and metadata. Real and fake test users use separate accounts and credentials. V1 is local.

## 4. Product overview

An authenticated v1 user configures recipients, reminder weekdays, one reminder time, a time zone, and optional Discord access. A scheduled Discord link requires login and opens the owning account’s unfinished draft or a new form with its default recipient. NoteCapsule autosaves. The user reviews all text and recipients before Approve & Send. After confirmed delivery, the user chooses whether to retain the memory. Unretained content is deleted while permitted operational metadata remains. An unfinished occurrence may receive one enabled overdue email with a recovery link.

## 5. User journeys

- Signup uses a unique username and nonblank password. The account configures an address book, default recipient, schedule, time zone, and optional Discord connection.
- Each scheduled Discord link lasts three hours. Login and ownership checks precede access.
- Text saves within two seconds after typing pauses; recipient changes save immediately; focus loss or navigation triggers a final save. Saving, Saved, and Save failed are visible.
- Refresh, restart, or temporary network failure restores the latest confirmed autosave. Failed saves keep text visible and warn before navigation.
- Review shows complete content and every recipient. Back to editing preserves content and invalidates approval. Cancel memory offers Keep editing or Discard draft.
- Approve & Send is disabled while sending and shows progress.
- Confirmed failure offers Retry sending, Back to editing, or Keep for later. Editing preserves attempt history and requires fresh approval.
- Unknown delivery warns that the original may have succeeded and resending may duplicate it. It offers Resend or Resend and save a copy.
- When a Discord link expires with unfinished work, one enabled overdue email supplies a link valid until the next scheduled Discord link. New occurrences, delivery, or discard invalidate earlier applicable links.

## 6. Functional requirements

### Authentication and authorization

- V1 provides signup, login, and logout with unique username/password and a secure server-side session cookie.
- Sessions expire after 30 idle minutes or eight total hours. Logout invalidates the server session.
- Every protected request verifies ownership; altered identifiers never expose another account.
- Reminder links require login.
- Passwords use secure one-way hashes and never appear in logs, telemetry, or errors.
- Unknown usernames and wrong passwords return the same message.
- Repeated failures trigger deterministic throttling or temporary blocking.

### Configuration, memories, and links

- Discord is optional; direct writing, sending, saving, and search remain available without it.
- Address-book entries contain names and valid email addresses. A default is required for reminders, while each memory may select one or more recipients.
- Accounts select weekdays, one shared time, and a detected, confirmable, correctable time zone.
- V1 memories are text-only, nonblank, and have no product maximum unless delivery requires one.
- Each account has at most one unfinished draft.
- Scheduled links last three hours; tokens are unguessable, never logged, and fail without disclosing private details.

### Delivery, retention, and search

- Only Approve & Send authorizes delivery; repeated activation is guarded.
- Confirmed failure and unknown outcome are distinct; uncertain delivery is never automatically retried.
- Retention after delivery is opt-in.
- Retained operational metadata excludes content, recipient identity, usernames, and other personal data, but may include times, outcomes, counts, and error categories.
- A discarded draft leaves only a content-free, non-personal event.
- Metadata is retained indefinitely and cannot be manually cleared.
- Account deletion removes personal/linkable data and leaves only anonymous metadata.
- V1 supports bulk clearing of all saved memories, not individual deletion.
- Keyword search uses all searchable retained fields but only within the authenticated account. Results show the matching paragraph, date, and recipient name; email addresses may be searchable but are not previewed.

## 7. Quality requirements

- Confirmed drafts survive refresh, restart, and temporary network loss.
- NoteCapsule never sends without current approval, to an unreviewed recipient, or twice because of repeated controls.
- Logs, telemetry, and errors exclude private content, identities, credentials, authorization data, and tokens.
- Errors are actionable without secrets, stack traces, or raw provider responses.
- Ordinary pages and stored-data searches normally respond within two seconds, excluding provider delays.
- Core flows support keyboards, visible labels/focus, and textual status rather than color or animation alone.
- V1 has no offline mode. Discord outages do not block direct use; email outages do not block drafting, review, keeping for later, or explicit discard.

## 8. Release boundaries

MVP includes the core Discord-to-email flow, recovery, a local seven-day readiness run, and then a required brief AWS learning deployment. MVP has no authentication.

V1 is local and adds authentication, authorization, isolated accounts, address books, schedules, autosave, overdue recovery, saved memories, keyword search, telemetry, bulk clearing, and deletion. It has no AWS or AI scope.

V2 remains local and adds MFA, OpenID Connect, Create memory from date, local AI, embeddings, vector storage, semantic search, source-linked RAG, and memory chat. AI is off by default.

## 9. Success criteria

MVP completes after the core flow is demonstrated, notifications work for seven consecutive scheduled days locally without critical failure, the MVP is deployed briefly to AWS, its core flow is verified and explained, and the deployment is removed.

V1 requires a recorded demonstration of signup through cross-account rejection and a new seven-day local notification run. A run restarts after correction if NoteCapsule loses confirmed content, sends without approval or to unreviewed recipients, duplicates email, leaks cross-account data, or places private information in logs or telemetry. Cosmetic defects do not restart it. Sending after every notification is not required.

V2 requires a recorded Create memory from date demonstration and at least ten known-source RAG questions. The correct source must appear in the top five for at least eight, every generated claim must link to supplied sources, and cross-account retrieval must occur zero times.

## 10. Open questions and assumptions

There are no open product questions.

Create memory from date uses the confirmed account time zone; independently authorized read-only Calendar and Gmail access; explicit selection of events, messages, and manually selected/uploaded photos; local summary generation; Use as draft; and a separate Approve & Send. Gmail initially exposes only selection metadata/snippets and fetches full bodies after selection. Temporary Google data/photos are deleted after completion, cancellation, or abandoned recovery. One unavailable source does not block others. Maps Timeline and general Google Photos library search are excluded. Refresh tokens are encrypted server-side, never exposed to React, and revoked/deleted on disconnect.

V2 uses a local Python service and Ollama `llama3.1:8b`. Only saved memories are embedded. Retrieval is account-filtered, sources are linked, unsupported answers say no source was found, and deletion removes embeddings. AI is cancellable, times out after two minutes without automatic retry, and never blocks non-AI use.

`nomic-embed-text:v1.5` and Qdrant are controlled initial placeholders. Replacements must remain local, pass evaluation, preserve account filtering and deletion, and support complete re-indexing.

React, MySQL, and Maven are current. JavaFX, SQLite, and Gradle are scrapped; obsolete references must be corrected separately.
