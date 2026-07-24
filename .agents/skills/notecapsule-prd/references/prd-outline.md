# NoteCapsule PRD Outline

Use this outline as an interview map, not a form to fill automatically. Ask only the questions needed to turn confirmed decisions into clear, testable product behavior. Keep technical implementation details in the build plan or a later technical design.

## 1. Problem and motivation

**Purpose:** Establish why NoteCapsule should exist, whose problem it solves, and why the problem is worth solving.

Clarify:

- the primary user's problem and current alternative;
- the value of capturing and sending memories regularly;
- the intended recipient and how the recipient benefits;
- the personal-use and learning motivations without confusing them with product behavior.

## 2. Goals and non-goals

**Purpose:** Define what the current release must accomplish and protect it from attractive scope expansion.

Clarify:

- the outcomes required of the current release;
- whether MVP and v1 name the same release;
- measurable goals rather than a feature inventory;
- explicit non-goals and deferred ideas.

Check especially that authentication, voice-to-text, AI features, agent workflows, RAG or semantic search, photos, mobile support, public hosting, multi-user support, and other deferred ideas do not enter the current release without an explicit scope decision.

## 3. Users and context

**Purpose:** Describe who interacts with the product, who receives its output, and the environment in which the flow must work.

Clarify:

- primary user and memory recipient roles;
- single-user constraints;
- Linux and local-network/browser context;
- where recipient and reminder settings are configured;
- whether the user must use the same machine or another reachable device.

## 4. Product overview

**Purpose:** Give readers a short mental model of the complete product loop before presenting detailed requirements.

Clarify the sequence:

1. configure recipient and reminder;
2. receive a notification;
3. open a private expiring link;
4. write or resume a memory;
5. review content and recipient;
6. approve sending;
7. observe success or recover from failure;
8. retain or delete content according to the user's choice.

Do not add steps that are not confirmed.

## 5. User journeys

**Purpose:** Describe end-to-end behavior from the user's perspective, including recovery paths that a happy-path summary hides.

Interview separately as needed:

- first-time setup;
- normal scheduled memory flow;
- overdue reminder flow;
- draft interruption and resumption;
- approval, cancellation, and editing;
- successful sending;
- failed email and retry;
- invalid, consumed, or expired link;
- opting to save or delete a sent memory.

For each journey, identify trigger, user actions, system feedback, completion state, and recoverable failures.

## 6. Functional requirements

**Purpose:** Turn confirmed journeys into unambiguous, verifiable statements of what the product must do.

Group requirements by behavior:

### Settings and recipients

- recipient configuration and validation;
- reminder schedule and overdue threshold;
- notification channel choices;
- local storage/export preferences.

### Reminders and notifications

- scheduling behavior;
- Discord, email, or both;
- notification contents without exposing sensitive memory data;
- overdue and repeat-notification behavior.

### Private links and access

- validity duration;
- whether possession grants access;
- token consumption, reuse, revocation, and expiration;
- behavior for invalid or expired links;
- local access boundaries.

### Memory composition and drafts

- supported current-release content;
- required and optional fields;
- validation and length constraints, if any;
- autosave or explicit save;
- browser/app closure and draft recovery;
- concurrent or duplicate drafts.

### Review and approval

- information shown before sending;
- editing and cancellation;
- recipient confirmation;
- exact action that authorizes irreversible email delivery;
- prevention of duplicate sends.

### Email delivery

- development versus real delivery behavior where it affects the user;
- success feedback;
- failure feedback, retry, and delivery-status recording;
- what happens if delivery state is uncertain.

### Retention, saved memories, and search

- default deletion timing after successful delivery;
- opt-in save behavior and storage location from the user's perspective;
- metadata retained when content is deleted;
- current-release keyword search only if confirmed in scope;
- download/export behavior only if confirmed in scope.

Use stable identifiers such as `FR-1` only after the requirements are settled enough that identifiers help review.

## 7. Quality requirements

**Purpose:** State observable constraints on reliability, privacy, security, usability, and failure handling that determine whether the product is safe and dependable.

Clarify:

- reliability expectations for seven-day use;
- no lost drafts and no incorrectly addressed or duplicate emails;
- private-link secrecy, expiration, and safe logging;
- credential and sensitive-content handling as product constraints;
- input validation and understandable errors;
- accessibility or usability expectations;
- local-only and offline limitations;
- acceptable recovery after process, network, or email failure.

Avoid prescribing frameworks or infrastructure unless the choice is itself a confirmed product constraint.

## 8. Release boundaries

**Purpose:** Make the shipping line explicit so later ideas cannot quietly become current commitments.

Create a clear separation among:

- current release;
- next release or local AI edition, if defined;
- later possibilities;
- explicitly out-of-scope items.

Resolve inconsistencies between questionnaire scope and the older build plan rather than copying both. Note dependencies or migration concerns without designing their implementation.

## 9. Success criteria

**Purpose:** Define observable evidence that the release solves its intended problem and is ready to call complete.

Clarify:

- the end-to-end acceptance demonstration;
- seven consecutive days of successful personal use;
- what counts as a critical failure, lost draft, or incorrect delivery;
- any product outcomes beyond implementation completion;
- which learning goals belong in project success notes rather than product acceptance criteria.

Prefer criteria that can be demonstrated or measured.

## 10. Open questions and assumptions

**Purpose:** Keep unresolved decisions visible so readers know where the PRD is incomplete and do not mistake guesses for requirements.

For each item, record:

- the unresolved question or conflict;
- why it matters;
- current evidence or competing sources;
- owner, if known;
- decision needed by, if known.

Review at least these likely gaps unless already confirmed:

- whether MVP and v1 are identical;
- private-link lifetime, access model, and consumption;
- draft behavior after browser or application closure;
- content deletion timing after sending;
- opted-in memory storage and search boundaries;
- expired-link and failed-email recovery;
- notification repetition;
- React versus older JavaFX assumptions;
- MySQL versus older SQLite assumptions;
- Maven versus older Gradle assumptions.

Do not force all future-facing questions to closure. Resolve only what is necessary to describe the current release clearly.
