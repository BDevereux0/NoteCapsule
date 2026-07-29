# NoteCapsule PRD Interview Session

- Started: 2026-07-23 14:23 EDT
- PRD state at start: no `docs/PRD.md`
- Prior PRD session logs: none found
- Current section: complete draft review
- Section status: awaiting final approval

## Initial decision inventory

### Confirmed source evidence

- The questionnaire is the current saved source of truth.
- NoteCapsule is intended for a single user to capture memories about a loved one and email them.
- The current flow begins with a scheduled notification and a private expiring link, then writing, review, approval, and email delivery.
- The project is intended for personal use and as a software-engineering learning project.

### Candidate decisions

- Capturing memories daily may help the user preserve details that would otherwise be forgotten.
- Emailing the memory may benefit the loved one by sharing those memories with them.
- Learning goals motivate the project but may belong in project-success notes rather than product requirements.

### Source conflicts

- The questionnaire selects React; the older build plan and README describe a JavaFX desktop application.
- The questionnaire selects MySQL; the older build plan selects SQLite.
- The questionnaire selects Maven; the older build plan selects Gradle.
- The questionnaire defers voice-to-text, photos, RAG, and agent workflows beyond v1; the README lists them as core or prominent features.
- The questionnaire says Docker Compose is the v1 installation/run method; the older build plan treats Docker as a future stretch and describes desktop packaging.

### Unresolved

- The concrete user problem and current alternative.
- The value to the memory recipient.
- Whether MVP and v1 are the same release.
- Private-link lifetime, access, reuse, and consumption behavior.
- Draft recovery, retention, failed-email recovery, and repeat-notification behavior.

## Section 1 — Problem and motivation

### Purpose

Establish why NoteCapsule should exist, whose problem it solves, and why solving that problem is worthwhile.

### Question cluster 1

1. What happens today when you want to preserve a memory about a loved one—what do you currently do, and what tends to go wrong or feel difficult?
2. What should the loved one gain from receiving these memories: connection now, a record to revisit later, or something else?
3. If NoteCapsule succeeds as a product, what meaningful change should it create for you beyond teaching you software engineering?

Why these matter: the sources describe the workflow and the learning motivation, but not the underlying human problem. A PRD needs that problem so later feature choices can be judged by whether they solve it.

### Response

The user explained that the current Gmail workflow is repetitive enough to discourage
recording memories while they are fresh. The memories are intended to accumulate so the
loved one can revisit them at meaningful milestones.

### Confirmed summary

NoteCapsule reduces the repetitive Gmail workflow so memories are more likely to be
recorded while fresh. The loved one accumulates memories to revisit at meaningful
milestones.

### Approved Section 1

NoteCapsule reduces the repetitive Gmail workflow so memories are more likely to be
recorded while fresh. The loved one accumulates memories to revisit at meaningful
milestones.

### Approval

The user explicitly approved Section 1.

## Section 2 — Goals and non-goals

### Purpose

Define what each planned release must accomplish and protect the product from scope
expansion.

### Recovered confirmed decisions

- MVP and v1 are different releases.
- MVP: Discord link → write → review → email.
- V1 adds draft recovery, saved memories, overdue email notifications, keyword search,
  and AWS deployment accessible only to the user.
- V1 prepares the product for later AI features through saved memories and keyword
  search, but contains no AI.
- Out of v1: full authentication, voice-to-text, other AI features, agent workflows,
  RAG/semantic search, photos, mobile support, multi-user support, and access by the
  general public.
- Comprehension prompts must be labeled “Learning check:”.

### Approved Section 2

- MVP and v1 are different releases.
- MVP: Discord link → write → review → email.
- V1 adds draft recovery, saved memories, overdue email notifications, keyword search,
  and AWS deployment accessible only to me.
- V1 prepares the product for later AI features through saved memories and keyword
  search, but contains no AI.
- Out of v1: full authentication, voice-to-text, other AI features, agent workflows,
  RAG/semantic search, photos, mobile support, multi-user support, and access by the
  general public.
- Label comprehension prompts as “Learning check:”.

### Approval

The user explicitly approved Section 2. The original multi-user exclusion was later
superseded by the approved revision recorded under Section 3.

## Recovery note

This progress was backfilled on 2026-07-23 after the previous patch runtime failed with:
`bwrap: loopback: Failed RTM_NEWADDR: Operation not permitted`.

## Section 3 — Users and context

### Purpose

Describe who uses NoteCapsule, who receives its output, and the environment in which the
flow must work.

### Status

Interviewing.

### Question cluster 1

1. For v1, are you the only person who operates NoteCapsule, while the loved one only
   receives emails and never signs into or opens the app?
2. Does v1 support one configured loved-one recipient at a time, with you able to replace
   that recipient in settings, or should it support choosing among multiple saved
   recipients?
3. Because v1 will be on AWS but is not for the public, from where should you be able to
   use it: only your Linux computer, or any desktop browser on a device you control? Mobile
   remains out of scope either way. Also, does “accessible only to me” describe the intended
   audience for now, with the exact access-control mechanism left for a later requirements
   section?

Why these matter: Section 3 defines roles and usage context. The sources confirm a
single-user product and a preset recipient, but they do not settle whether multiple
recipients can be configured or reconcile the older local-only/Linux context with the
new AWS decision. The final part avoids silently treating “no full authentication” as “no
access protection.”

### Response

The user said v1 will have at least two “users”: the user themself and a fake user whose
generated memories will exercise features such as keyword search and later AI. V1 lets a
user configure the pipeline by setting the recipient and the reminder time. The AWS-hosted
instance will be accessible only to the user and will exist only briefly—long enough to
learn hosting—before being taken down.

The user did not understand the learning check connecting “single-user” with “one fixed
recipient forever” and asked what the two concepts had to do with each other.

### Interviewer correction

The learning check was poorly phrased because it conflated two independent roles:
application operators and email recipients. A product can have one operator who changes
the configured recipient over time. The question should not have implied that single-user
operation determines recipient cardinality.

### Conflict requiring clarification

The approved Section 2 explicitly places multi-user support out of v1. The phrase “fake
user” may mean either a genuine second application account/profile, which would revise
that approved boundary, or synthetic test memories/a test persona controlled by the sole
operator, which would not necessarily constitute multi-user support. This distinction is
unresolved.

### Question cluster 2

1. Is the fake user a real second NoteCapsule account/profile with separate settings,
   recipient, reminder schedule, and memories, or is it synthetic memory data that you
   manage while remaining the app’s only operator?
2. When you “set up the pipeline,” does v1 store exactly one active recipient and one
   reminder schedule per real account/profile, both changeable in settings?

Why these matter: a genuine second account changes the already approved v1 boundary and
introduces profile separation requirements. Synthetic data for testing search does not
necessarily do that. The pipeline answer determines whether settings belong globally to
the application or separately to each profile.

### Response

The user said NoteCapsule should support signup-created profiles rather than be hardwired for
one person. After clarification, the user confirmed basic multi-profile support for v1. Each
profile has isolated memories, recipient configuration, reminder settings, and drafts. Full
authentication remains out of scope; the sole human operator selects profiles from a visible
list during the temporary AWS learning deployment.

### Approved Section 3

V1 supports multiple isolated profiles controlled by one human operator. A profile is a
separate container for memories and settings, not proof of an authenticated identity. The
visible profile list is accepted only for the temporary non-public deployment. The user
explicitly approved Section 3 and revised Section 2 so basic multi-profile support is in v1.

## Section 4 — Product overview

### Approved decisions

A profile must configure a recipient and reminder time before its scheduled pipeline runs.
Discord sends the normal reminder link. Optional overdue email notifications are separate.
The operator writes or resumes a draft, reviews the content and recipients, explicitly
approves delivery, and then chooses whether to retain the successfully delivered memory.
Only retained memories are searchable. The user explicitly approved Section 4.

## Section 5 — User journeys

### Approved decisions

NoteCapsule automatically saves drafts and reopens a profile’s unfinished draft. V1 does not
encrypt draft or saved-memory content at rest. Review provides “Back to editing,” preserving
text. “Cancel memory” offers “Keep editing” and “Discard draft”; discard permanently deletes
the unfinished memory.

Invalid or expired links explain the problem and allow direct website access or an applicable
new link. A failed delivery visibly alerts the operator and preserves the same outstanding
memory until delivery or explicit discard. Available actions are “Retry sending,” “Back to
editing,” and “Keep for later.” Editing resumes autosave, preserves failed-attempt history,
and requires fresh review and approval. Each scheduled memory produces at most one overdue
email. After successful delivery or retry, the operator chooses whether to retain the memory.
The user explicitly approved Section 5.

## Section 6 — Functional requirements

### Confirmed profile, recipient, and schedule requirements

A v1 profile requires a username; uniqueness is not enforced. Discord connection and bot
authorization are optional and authorize reminders, not NoteCapsule access. Profiles without
Discord may still write, resume, review, send, save, and search memories directly. Each
profile has an address book containing recipient names and valid email addresses. A default
recipient is required for the scheduled pipeline, while each memory may select one or more
recipients. Review shows all selected recipients. A profile selects weekdays and one shared
reminder time. NoteCapsule detects the browser time zone, displays it for confirmation, and
allows manual correction. The user explicitly confirmed these requirements.

### Confirmed reminder-link lifecycle

Each selected schedule occurrence sends one Discord link valid for three hours. It opens the
profile’s existing unfinished draft or an empty form with the default recipient prefilled. If
the memory remains unfinished when that link expires, NoteCapsule sends exactly one overdue
email with a separate recovery link. That link remains valid until the next scheduled Discord
link. A new scheduled link invalidates links from earlier occurrences. Successful delivery or
explicit discard invalidates applicable active links sooner. Invalid links explain the issue
and allow the operator to continue through NoteCapsule directly. The user explicitly confirmed
this lifecycle.

### Question cluster 6 — Memory composition and drafts

The user decided that a memory must contain at least one non-whitespace character. V1 has no
product-defined maximum memory length unless an email-delivery limitation requires a safe
limit. Each profile may have only one unfinished draft at a time.

After reviewing the autosave options and performance tradeoffs, the user selected debounced
autosaving. NoteCapsule saves text shortly after the operator pauses typing, saves recipient
changes immediately, and performs a final save when the form loses focus or the operator
navigates away. The form visibly reports “Saving,” “Saved,” and “Save failed” states.

### Confirmed summary

- A memory must contain at least one non-whitespace character before review.
- V1 has no product-defined maximum length unless email delivery requires a safe limit.
- Text uses debounced autosave; recipient changes save immediately.
- A final save occurs on focus loss or navigation.
- The form shows “Saving,” “Saved,” and “Save failed.”
- Each profile has at most one unfinished draft.

### Confirmation

The user explicitly confirmed the complete memory validation, autosave, save-status, and
single-draft summary.

## Resume checkpoint — 2026-07-23

Sections 1 through 5 are approved. Section 6 remains interviewing. Question cluster 6 is
confirmed. Resume with the next functional-requirements cluster.

### Question cluster 7 — Review, approval, and duplicate-send protection

1. On the review screen, should NoteCapsule show the full memory text and every selected
   recipient name and email address, with “Back to editing,” “Cancel memory,” and one final
   “Approve & Send” action?
2. After “Approve & Send” is pressed, should the action become disabled and show a sending
   state until the attempt succeeds or fails, so double-clicks cannot start duplicate sends?
3. If NoteCapsule loses contact with the email service after submitting the message and cannot
   tell whether it was accepted, should it show “Delivery status unknown” and block automatic
   retry until the operator deliberately chooses what to do?

Why these matter: approval is the authorization boundary for an irreversible action. The UI
must make the exact content and audience clear, prevent ordinary duplicate submissions, and
avoid silently retrying when the first attempt may already have reached the email provider.

Recommendation: show the complete content and recipient list; use one guarded send action;
and treat an uncertain outcome differently from a confirmed failure.

### Response

The user confirmed that review shows the complete memory and every selected recipient’s name
and email address, with “Back to editing,” “Cancel memory,” and one final “Approve & Send”
action. After approval, the send action becomes disabled and the interface shows visible
progress, potentially using a popup or playful spinner/filling-circle animation. The exact
visual treatment remains a UI design choice.

The user requested more detail before deciding how NoteCapsule handles an uncertain email
delivery result.

### Confirmed direction

- Review shows the complete memory and all selected recipient names and email addresses.
- Review provides “Back to editing,” “Cancel memory,” and one “Approve & Send” action.
- After activation, “Approve & Send” is disabled while the attempt is in progress.
- The interface displays visible sending progress; the exact animation is not fixed by the PRD.
- If NoteCapsule cannot verify the outcome, it offers “Resend” and “Resend and save a copy.”
- Either resend is a deliberate operator action because the first attempt may already have succeeded.
- “Resend and save a copy” retains the memory under the profile for later access and keyword search after the resend attempt succeeds.

### Clarification and confirmation

The user clarified that the intended uncertain-delivery actions are “Resend” and “Resend and
save a copy.” This resolves the earlier typo. The user explicitly selected those actions for
an email whose outcome NoteCapsule cannot verify. Before offering them, NoteCapsule must warn
that it cannot determine whether the original email went through and that resending may create
a duplicate.

### Confirmed cluster 7 summary

- Review displays the complete memory and every selected recipient name and email address.
- Review provides “Back to editing,” “Cancel memory,” and one “Approve & Send” action.
- While sending, the action is disabled and visible progress is shown.
- If delivery cannot be verified, NoteCapsule warns that the original email may have gone
  through and that resending may create a duplicate.
- The operator chooses “Resend” or “Resend and save a copy.”
- A saved copy is retained under the profile for later access and keyword search after a
  successful resend; a further failed or unknown attempt remains preserved for recovery.

### Confirmation

The user explicitly confirmed this routine, including the indeterminate-delivery warning.

### Question cluster 8 — Retention and keyword search

1. When the operator declines to save a successfully delivered memory, should NoteCapsule
   immediately delete its text after recording only non-content delivery information such as
   time, recipients, and outcome? Or should it delete the entire record, including metadata?
2. For a saved memory, should keyword search examine only the memory text, or also recipient
   names and email addresses?
3. Should search results remain strictly within the currently selected profile and show a
   short excerpt, sent date, and recipients before the operator opens the full memory?

Why these matter: the earlier save choice controls content retention, but the PRD still needs
to define what remains after deletion and what searchable data may reveal. Profile-scoped
search preserves the isolation already approved for v1.

Recommendation: delete unsaved text immediately after confirmed delivery while retaining
minimal delivery metadata; search saved memory text and recipient names, but not email
addresses; and keep results profile-scoped with a concise preview.

### Response

The user decided that when memory content is not retained, NoteCapsule should still preserve
telemetry and non-content metadata. The user wants that metadata-preservation rule applied
across the product. Saved-memory search should search everything available. The user asked
for clarification of what it means for results to remain strictly within the selected profile.

### Confirmed direction

- Deleting or declining to retain memory content does not delete applicable telemetry and
  non-content metadata.
- This metadata-retention principle applies across product flows, subject to privacy and
  secret-handling boundaries that must be defined.
- Keyword search examines every retained, searchable field available to the selected search
  scope, including memory text, recipient information, and retained metadata.
- Keyword search is restricted to the currently selected profile. A profile cannot search or
  view another profile’s saved memories.

### Confirmation

The user explicitly confirmed profile-scoped search because users must not be able to search
other people’s memories.

### Question cluster 9 — Metadata and telemetry boundaries

1. When a memory is deleted or not saved, may retained metadata include recipient names and
   email addresses, or only operational facts such as profile, timestamps, delivery outcome,
   attempt count, and error category?
2. When an unfinished draft is explicitly discarded, should NoteCapsule retain only a
   “draft discarded” event with time and profile, with no memory text or recipient list?
3. Should v1 retain this telemetry and metadata indefinitely unless the profile itself is
   deleted, or should the operator have a way to clear it?

Why these matter: metadata can still be personal data. “Delete the memory” should reliably
remove its content, while telemetry should retain only what is necessary to diagnose product
behavior. A deletion rule also needs a retention endpoint.

Recommendation: retain operational facts but omit recipient names and addresses from deleted
items; retain only a content-free discard event for drafts; and let the operator clear
telemetry per profile even if v1 otherwise keeps it indefinitely.

### Response

The user decided that retained metadata must exclude recipient names and email addresses and
contain operational facts only. After learning that a content-free discard event can help
distinguish deliberate deletion from autosave failure and measure abandoned drafts, the user
revised the decision: retain the discard event, but include no personal data. Operational
metadata is retained and cannot be cleared by the operator.

### Confirmed direction

- Retained metadata for unsaved or deleted memories contains operational facts only.
- It excludes memory content, recipient names, and recipient email addresses.
- Explicitly discarding an unfinished draft deletes its content but retains a content-free,
  non-personal operational event recording that a discard occurred.
- The discard event contains no memory text, recipient information, username, or other
  personal data.
- Retained operational metadata cannot be manually cleared in v1.
- When a profile is deleted, its personal data and linkable metadata are removed. Only
  anonymized operational metadata that cannot be linked back to the profile or person remains.

### Confirmation

The user explicitly confirmed the metadata boundaries, the retained non-personal discard
event, indefinite non-clearable operational metadata, and anonymization after profile deletion.

### Question cluster 10 — Deletion controls and search results

1. Does v1 include an operator action to delete an entire profile? If so, should it require a
   strong confirmation that lists the memories, drafts, address book, settings, and personal
   metadata that will be permanently removed?
2. May the operator permanently delete an individually saved memory later, while retaining
   only its non-personal operational metadata under the rules just confirmed?
3. Should search results show a short matching excerpt, sent date, and recipient names before
   opening the full saved memory? Email addresses would still be searchable but need not be
   displayed in the result preview.

Why these matter: we have defined what happens after deletion, but not whether v1 exposes
those deletion actions. Search also needs a result format that helps the operator identify a
match without opening every memory or unnecessarily displaying email addresses.

Recommendation: include both deletion actions with explicit confirmation, and show the
concise search preview described above.

### Response

The user confirmed that v1 allows deletion of an entire profile. The user said operators
should be able to clear memories if they choose. Search results should show the paragraph
containing the matched keyword, the memory date, and the recipient name.

### Confirmed direction

- V1 provides a profile-deletion action with a strong confirmation describing the personal
  data that will be permanently removed.
- Search results show the paragraph containing the keyword match, the memory date, and the
  recipient name.
- Email addresses remain searchable but are not displayed in the result preview.
- V1 provides one bulk action to clear all saved memories belonging to the selected profile.
- V1 does not support deleting saved memories individually.
- Clearing saved memories requires explicit confirmation, deletes their content, and retains
  only anonymized operational metadata under the confirmed retention rules.

### Confirmation

The user explicitly confirmed that v1 supports clearing all saved memories for the selected
profile, rather than individual saved-memory deletion.

### Section 6 draft review — learning check

The user explained that operational metadata is retained because it provides useful information,
such as how many email attempts fail for a user. The user explained that preventing one profile
from searching another profile’s memories is a security requirement.

### Assessment

The security explanation is complete: profile-scoped search prevents one profile from exposing
another profile’s private memory content. The telemetry explanation reveals a necessary nuance.
Measuring failures for a particular active profile requires operational metadata to remain linked
to that profile while it exists. Recipient identities and memory content remain excluded. When
the profile is deleted, the retained operational metadata is anonymized and can no longer be
linked to the deleted profile or person. The user confirmed this clarification: operational metadata remains linked to an active profile
for profile-specific reliability analysis, excludes memory content and recipient identities,
and becomes unlinkable anonymous metadata when the profile is deleted.

### Approval

The user explicitly approved the complete Section 6 draft with this clarification.

### Section status

Approved.

## Section 7 — Quality requirements

### Purpose

Define observable standards for reliability, privacy, security, usability, and recovery. These
requirements determine whether NoteCapsule can be trusted during ordinary use and failures.

### Relevant confirmed decisions

- Drafts autosave with visible status and must survive interruption.
- Sending requires explicit approval and duplicate-send protection.
- Unknown delivery outcomes warn the operator before a deliberate resend.
- Profiles isolate memories and search results, but v1 has no authentication.
- Memory content is not encrypted at rest.
- The intended readiness exercise includes seven consecutive days of personal use.

### Question cluster 1 — Reliability and recovery standards

1. During the seven-day v1 trial, should any lost saved draft, email sent without explicit
   approval, email sent to an unreviewed recipient, or duplicate caused by NoteCapsule count
   as a critical failure that resets the trial?
2. After a browser refresh, application restart, or temporary network interruption, should
   the operator be able to return to the selected profile and recover the most recently
   confirmed autosaved state without manually reconstructing the memory?
3. If an autosave fails, should NoteCapsule keep the unsaved text visible, show “Save failed,”
   and warn before navigation rather than allowing the operator to believe it is safely stored?

Why these matter: a seven-day trial is meaningful only if failures have explicit definitions.
Recovery should promise the last state NoteCapsule actually confirmed as saved, while keeping
unsaved text visible prevents a transient failure from becoming silent data loss.

Recommendation: yes to all three.

### Response

The user confirmed recovery of the most recently confirmed autosaved state after refresh,
restart, or temporary network interruption. If autosave fails, NoteCapsule keeps unsaved text
visible, shows “Save failed,” and warns before navigation.

The user asked what the seven-day trial refers to. The phrase came from the existing project
readiness goal of seven consecutive days of successful personal use, but calling it a trial
and defining reset conditions were not yet confirmed. Question 1 remains unresolved.

### Confirmed direction

- After a browser refresh, application restart, or temporary network interruption, the
  operator can recover the most recently confirmed autosaved state.
- An autosave failure keeps unsaved text visible, displays “Save failed,” and warns before
  navigation.
- V1 readiness includes seven consecutive scheduled days of successful personal use.
- Losing a confirmed draft, sending without explicit approval, sending to an unreviewed
  recipient, or producing a duplicate email because of NoteCapsule is a critical failure that
  resets the seven-day run after the defect is corrected.
- Cosmetic issues do not reset the run.

### Confirmation

The user explicitly confirmed the seven-consecutive-day readiness test and its critical-failure
boundary.

### Question cluster 2 — Privacy, secrets, and safe errors

1. Should NoteCapsule prohibit memory text, recipient email addresses, reminder-link tokens,
   Discord authorization data, and email credentials from appearing in application logs or
   retained telemetry?
2. Because v1 has no authentication and does not encrypt stored content, should the interface
   visibly warn that anyone who can access the deployment may select a profile and read its
   drafts or saved memories?
3. Should user-facing errors explain what failed and what the operator can do next without
   exposing credentials, private tokens, stack traces, or raw provider responses?

Why these matter: the confirmed telemetry rules already exclude personal data, but logs and
error messages are separate ways sensitive information can leak. V1 also needs to describe
its access limitation honestly instead of implying that visible profiles provide privacy.

Recommendation: yes to all three.

### Response

The user confirmed that NoteCapsule must prohibit memory text, recipient email addresses,
reminder-link tokens, Discord authorization data, and email credentials from application logs
and retained telemetry. User-facing errors must explain the failure and recovery action without
exposing credentials, private tokens, stack traces, or raw provider responses.

The user rejected an in-product warning about unauthenticated profile access because v1 has
one human operator. If additional human users are introduced, authentication will be required,
making that warning unnecessary for the intended v1 context.

### Confirmed summary

- Sensitive content, personal recipient data, tokens, authorization data, and credentials are
  prohibited from logs and telemetry.
- Errors are understandable and actionable without exposing sensitive technical details.
- V1 does not display an unauthenticated-access warning in the interface.
- Deployment for additional human operators is prohibited until authentication is added.

### Confirmation

The user explicitly confirmed these privacy, secret-handling, and safe-error decisions.

### Question cluster 3 — Responsiveness and usability

1. After the operator pauses typing, how quickly should autosave begin? Recommendation: within
   two seconds, with “Saving” shown as soon as the request starts.
2. For the expected personal dataset, should ordinary pages and keyword-search results normally
   respond within two seconds, excluding delays caused by Discord or email providers?
3. Should every core flow be usable with a keyboard and provide visible labels, focus states,
   and readable error/status text rather than communicating state through color or animation
   alone?

Why these matter: “shortly” and “responsive” are difficult to test without practical targets.
The final requirement ensures that the playful sending animation remains optional decoration
and that essential state is still understandable and keyboard-accessible.

### Response

The user confirmed that autosave begins within two seconds after typing pauses and shows
“Saving” when the save request starts. The user requested explanations of the page/search
response-time target and keyboard/accessibility behavior before deciding them.

### Confirmed direction

- Autosave begins within two seconds after the operator pauses typing.
- “Saving” appears when the save request begins.
- Ordinary NoteCapsule pages and searches over its own stored data normally respond within two
  seconds. This excludes external email and Discord delays.
- Keyword search uses NoteCapsule’s stored data and does not call an external search service.
- Core flows support keyboard navigation, visible focus, labeled controls, and readable status
  text; essential state is not communicated through color or animation alone.

### Confirmation

After clarification, the user accepted the internal response-time target and keyboard-support
requirements and stated that search will not run off-system.

### Question cluster 4 — External outages and access protection

1. Because v1 is temporarily hosted on AWS, should internet access be required, with no offline
   mode? If Discord is unavailable, should the operator still be able to visit NoteCapsule
   directly and write the scheduled memory?
2. If the email provider is unavailable, should all drafting, autosave, review, and “Keep for
   later” behavior remain usable, with sending recoverable when the provider returns?
3. Should reminder links use unguessable private tokens, never expose those tokens in logs or
   telemetry, and reject altered, expired, or previously invalidated tokens without revealing
   profile or memory details?

Why these matter: NoteCapsule depends on external services, but their outages should not cause
data loss or block work that does not require them. Private links are temporary access
credentials, so their secrecy and failure behavior are security requirements even in the
single-operator deployment.

Recommendation: yes to all three.

### Response

The user confirmed that temporary AWS-hosted v1 requires internet access and has no offline
mode. If Discord is unavailable, the operator can enter NoteCapsule directly. If the email
provider is unavailable, drafting, autosave, review, and recovery remain usable; the operator
may keep the memory for later or explicitly discard it. Private reminder links use unguessable
tokens, keep tokens out of logs and telemetry, and reject altered, expired, or invalidated
tokens without revealing profile or memory details.

### Confirmed summary

- AWS-hosted v1 requires internet access and provides no offline mode.
- A Discord outage does not prevent direct use of the memory flow.
- An email-provider outage does not prevent drafting, saving, or review.
- During an email outage, the operator may keep the memory for later or discard it through
  the already confirmed deletion flow.
- Reminder tokens are unguessable, never logged, and fail without disclosing private details.

### Confirmation

The user explicitly confirmed the external-outage and private-link protection requirements.

### Section 7 draft review

The user said the Section 7 draft is good. For the learning check, the user correctly explained
that recovery promises the most recently confirmed autosave because saving occurs after the
operator pauses typing. The user then questioned whether learning checks should test the
overall idea of each section rather than an isolated requirement.

### Interviewer correction

The criticism is correct. The autosave question tests a narrow behavior but does not
demonstrate understanding of why quality requirements exist or how they differ from functional
requirements. Going forward, each section review will use a section-level “Learning check:”
about the section’s overall purpose and important tradeoffs. A narrow learning check will be
used only when a consequential decision needs separate verification. Section 7 content is
accepted, but final approval awaits the revised section-level learning check.

### Revised learning-check response

The user said quality requirements ensure memories are good to go and tested and cover the
user experience, such as keyboard usage.

### Assessment

Partially complete. The response correctly identifies testable readiness and usability. The
phrase “memories are good to go” needs correction: quality requirements do not judge whether
the user wrote a good memory. They constrain how dependably, securely, quickly, and accessibly
NoteCapsule performs its functions. The user then identified keyboard usage as the quality example and explained that it
supports existing features rather than independently using or defining the system. This
demonstrates the distinction: functional requirements define product capabilities, while
quality requirements constrain how reliably and accessibly those capabilities operate.

### Approval

The user accepted the Section 7 draft and completed the revised section-level learning check.
Section 7 is explicitly approved.

### Section status

Approved.

## Section 8 — Release boundaries

### Purpose

Separate the MVP, v1, and later ideas so implementation can stop at a deliberate shipping
line without future features quietly entering the current release.

### Confirmed release evidence

- MVP and v1 are different releases.
- MVP is Discord reminder link → write → review → email.
- V1 adds multiple isolated profiles, draft recovery, address books and multiple recipients,
  saved memories, overdue email recovery, keyword search, telemetry, and a temporary AWS
  deployment operated by one person.
- Full authentication, AI, voice-to-text, agent workflows, semantic search/RAG, photos, mobile
  support, and public access are outside v1.
- Additional human operators require authentication.

### Question cluster 1 — Shipping sequence and post-v1 direction

1. Is the MVP an internal milestone you build and verify before continuing to v1, or should it
   be treated as a separately deployed release?
2. Is the temporary AWS deployment part of completing v1, after which that deployment is taken
   down rather than maintained as a production service?
3. After v1, which named release comes next: an authenticated multi-user edition, the local-only
   AI edition, or should the PRD leave their order undecided?

Why these matter: a milestone and a supported release carry different expectations. The PRD
also should not imply an ongoing cloud service if AWS exists only as a learning exercise.
Leaving later-release order open is valid when no sequence has been chosen.

Recommendation: treat MVP as an internal milestone, make temporary AWS deployment part of v1
completion, and leave the order of later editions undecided unless you already have a firm plan.

### Response

The user defined MVP completion by the seven-consecutive-day testing window. V1 follows with
the additional confirmed requirements, including keyword search and the AWS deployment. The
AWS deployment remains live only long enough to learn AWS deployment and is then taken down.
After v1, v2 is a local deployment that begins the AI portion of the project.

### Confirmed direction

- MVP is the core reminder-link, writing, review, and email flow and is complete only after
  passing the seven-consecutive-day readiness test.
- V1 adds the confirmed non-AI requirements such as multi-profile behavior, draft recovery,
  saved memories, overdue recovery, keyword search, and telemetry.
- Temporary AWS deployment is part of v1 completion and lasts only as long as needed for the
  hosting-learning exercise. It is not an ongoing production service.
- V2 follows v1, returns to local deployment, and begins the AI portion of NoteCapsule.

### Question cluster 2 — V2 and later boundaries

1. In v2, should every AI capability remain opt-in and disabled by default, so the complete
   non-AI workflow still works without enabling AI?
2. Because v2 is local and still intended for one human operator, should authentication remain
   deferred until NoteCapsule is deployed for additional human operators?
3. Should photos, mobile support, and public hosting remain unassigned future possibilities
   rather than commitments to v2?

Why these matter: “begins the AI portion” should not accidentally commit v2 to every AI idea
at once. Keeping non-AI behavior intact creates a safe fallback, while an explicit future list
prevents unrelated features from entering v2 without a new scope decision.

Recommendation: yes to all three.

### Response

The user confirmed that every v2 AI feature is opt-in. Authentication is not included in v2.
The user said public hosting belongs only to v1, while photos and mobile support are deferred
as “nice to haves.”

### Confirmed direction

- Every v2 AI feature is opt-in and disabled until the operator enables it.
- The established non-AI workflow remains available in v2.
- Authentication is not part of v2.
- Photos and mobile support are uncommitted nice-to-haves, not v2 requirements.
- V2 is local and is not cloud-hosted.

### Terminology conflict

Earlier decisions put access by the general public outside v1 and limited the temporary AWS
deployment to one human operator. The user’s phrase “public hosting will only be part of v1”
may mean internet-hosted AWS infrastructure rather than public access by other people. The user confirmed that “public hosting” meant temporary internet-accessible AWS
infrastructure, not public access. NoteCapsule is never intended to be a public website.

### Confirmation

The user explicitly confirmed the MVP, v1, v2, AWS, authentication, AI opt-in, and deferred
feature boundaries. The Section 8 interview is complete and ready for draft review.

### Section 8 review correction — authentication

The user corrected the release boundary: authentication belongs in v1. This conflicts with
previously approved requirements stating that v1 has no authentication, uses a visible profile
list, and requires authentication only before deployment for additional human operators. The
conflict affects Sections 2, 3, 6, 7, and 8 and must be resolved explicitly before Section 8
can be approved.

### Unresolved authentication model

The user decided that v1 provides account signup, login, and logout. Each authenticated account
is itself the profile; there is no separate profile layer inside an account. The user said the
AWS deployment does not need login as an access boundary because it “will not be reachable.”
V1 uses server-side session authentication. MFA is deferred to v2. The user described an
OAuth 2.0 login addition for v2; because OAuth 2.0 alone is delegated authorization rather
than an identity protocol, the candidate requirement is OpenID Connect login built on OAuth
2.0.

The user directed that no further product consideration be given to AWS. The deployment is
only a short learning exercise, is ended after that exercise, and does not receive PRD
requirements for network architecture, access controls, uptime, or long-term operation. V1
authentication remains a product capability independent of the temporary deployment.

### Authentication revision cluster 1 — Account credentials and recovery

1. Since each account is now the profile, what uniquely identifies an account at signup and
   login: a unique email address, a unique username, or both?
2. Does v1 include “Forgot password” recovery through a single-use, expiring email link?
3. Should a session end after a period of inactivity, and should v1 offer “Remember me”?

Why these matter: authentication requires an unambiguous login identifier, a recovery path
that does not expose passwords, and a defined session lifetime. These decisions replace the
previous allowance for ambiguous duplicate profile names.

Recommendation: use a unique email address for login plus a non-unique display name; include
email password reset; expire sessions after 30 minutes of inactivity; and omit “Remember me”
from v1.

### Response

The user was unsure whether the login identifier should be a username or email and asked for
standard practice, noting prior experience with username login. Password recovery is excluded
from v1 and deferred as a nice-to-have. The user answered yes to the compound session question,
so session expiration is confirmed but “Remember me” still requires a separate answer.

### Confirmed direction

- V1 does not include forgot-password recovery.
- Password recovery is a deferred nice-to-have.
- V1 sessions expire after inactivity; the exact timeout and absolute lifetime remain to be
  confirmed.
- V1 login uses a unique username and password.
- Authentication uses a server-side session represented by a secure browser cookie.
- Sessions expire after 30 minutes of inactivity and after an absolute maximum of eight hours.
- V1 does not provide “Remember me.” Persistent login is a deferred nice-to-have.
- Adding an account email address and email verification is deferred. Recipient addresses
  remain separate from any future account identity email.

### Confirmation

The user explicitly confirmed the v1 login identifier and session lifetime and deferred
account email verification, password recovery, and “Remember me” as nice-to-haves.

### Authentication revision cluster 2 — Passwords, authorization, and reminder links

1. Should v1 require passwords of at least 15 characters, allow at least 64 characters, permit
   spaces and passphrases, and avoid forced symbol/number rules or periodic password changes?
2. Should logout immediately invalidate the server session, and should every protected request
   verify that the authenticated account owns the requested memories, drafts, recipients,
   settings, and metadata?
3. When a Discord or overdue-email reminder link is opened without an active session, should
   NoteCapsule require login and then continue to that account’s memory flow, rather than
   treating possession of the link as sufficient access?

Why these matter: modern password guidance favors length and compromised-password defenses
over brittle composition rules. Authentication is incomplete without authorization checks on
every account-owned resource. Requiring login for reminder links changes the earlier direct
link behavior but prevents a forwarded or stolen link from bypassing v1 authentication.

Recommendation: yes to all three.

### Response

The user stated that the proposed password-policy behavior is a nice-to-have, requested an
explanation of session invalidation and per-request ownership checks, and confirmed that an
unauthenticated reminder-link visitor must log in before continuing. The user emphasized that
authentication is not part of MVP and begins only in v1.

### Confirmed direction

- MVP has no account authentication.
- V1 introduces signup, login, logout, and server-side sessions.
- V1 reminder links require authentication before continuing to the owning account’s memory
  flow.
- V1 has no product-defined minimum password length or composition rule beyond requiring a
  nonblank password at signup.
- Password-length, passphrase, composition, and compromised-password policies are deferred
  nice-to-haves.
- Logout immediately invalidates the server-side session.
- Every protected request verifies that the authenticated account owns the requested memories,
  drafts, recipients, settings, delivery history, and link destination.
- Requests for another account’s resources are rejected even if an identifier or URL is
  manually altered.

### Confirmation

After clarification, the user explicitly confirmed that v1 includes both authentication and
backend-enforced authorization.

### Authentication revision cluster 3 — Credential and login safety

1. Should passwords be stored only as secure one-way password hashes, never as readable text,
   and never appear in logs, telemetry, or error messages?
2. Should failed login responses use the same generic message for an unknown username and a
   wrong password, so the response does not reveal which accounts exist?
3. Should v1 slow or temporarily block repeated failed login attempts against an account to
   reduce automated password guessing?

Why these matter: these are protections for the authentication capability itself, not rules
about how users construct passwords. A database leak should not reveal readable passwords,
login errors should not provide an account directory, and unlimited guesses undermine even
a strong password.

Recommendation: yes to all three.

### Response

The user confirmed secure one-way password storage, generic login errors, and slowing or
temporarily blocking repeated failed login attempts. The user also requested a nice-to-have
linear-regression model to detect suspicious login behavior.

### Confirmed v1 requirements

- Passwords are stored only as secure one-way password hashes and never appear in logs,
  telemetry, or user-facing errors.
- Unknown usernames and incorrect passwords produce the same generic login response.
- Repeated failed login attempts trigger deterministic throttling or temporary blocking.

### Deferred nice-to-have

An experimental model may evaluate login telemetry for suspicious patterns. Linear regression
may be explored for learning, but the deferred requirement does not mandate it because login
abuse detection may fit classification or anomaly-detection approaches better. Any model must
use non-sensitive operational data and supplement, never replace, deterministic v1 rate
limiting.

### Confirmation

The user explicitly confirmed the three v1 credential and login-safety requirements and added
the model experiment as a nice-to-have.

### Consolidated authentication revision approval

The user explicitly approved the complete authentication revision. It supersedes every earlier
requirement describing v1 as unauthenticated or using a visible profile-selection list.

#### Approved cross-section effects

- Section 2: authentication remains outside MVP but is included in v1.
- Section 3: each authenticated account is the profile; the real and fake users are separate
  accounts rather than selectable profiles inside one operator session.
- Section 6: v1 provides signup, login, logout, server-side sessions, ownership authorization,
  authenticated reminder-link continuation, credential protection, and login throttling.
- Section 7: secure credential storage, generic login failures, session expiration, immediate
  logout invalidation, and ownership enforcement are quality requirements.
- Section 8: v1 introduces session authentication; v2 adds MFA and OpenID Connect.

The earlier visible-profile list, duplicate-profile-name behavior, and no-authentication v1
requirements are superseded.

### PRD skill learning-check revision

The user observed that learning checks should zoom out to the overall idea of each section. The
repository skill previously required only that the user “demonstrate understanding in their own
words,” which did not constrain the level of the check. The user approved and Codex applied a
skill revision requiring exactly one labeled, section-level learning check focused on the
section’s purpose, relationship to neighboring sections, or contribution to the product. The
revision prohibits isolated requirements and implementation details as the main section check,
limits narrow follow-ups to meaningful misunderstandings, and instructs the interviewer to
accept concise demonstrations of the core idea. Standard skill validation passed.

### Section 8 learning check and approval

The user explained that release boundaries prevent scope creep and clarify what will not be built in each version. This concise answer demonstrates the overall purpose of Section 8 under the revised learning-check standard.

### Approval

The user approved the authentication revision and accepted the revised Section 8 content. The section-level learning check is complete. Section 8 is approved.

## Section 9 — Success criteria

### Purpose

Define observable evidence that each release works and is ready to call complete. Product acceptance criteria remain distinct from learning evidence, even though both matter to the project.

### Relevant confirmed decisions

- MVP must pass seven consecutive scheduled days without a critical failure.
- V1 adds authentication, isolated accounts, draft recovery, saved memories, overdue recovery, keyword search, telemetry, and a brief AWS learning deployment.
- Critical failures include losing a confirmed draft, sending without approval, sending to an unreviewed recipient, or creating a duplicate because of NoteCapsule.
- AWS success means learning to deploy and then ending the deployment, not operating a service.

### Question cluster 1 — V1 completion evidence

1. Should v1 require one recorded end-to-end demonstration covering signup, login, account setup, reminder delivery, draft interruption and recovery, review, sending, optional saving, keyword search, bulk memory clearing, logout, and rejection of access to another account?
2. After adding v1 features, should the complete v1 product pass its own new seven-consecutive-day use period, or is the seven-day test only an MVP gate?
3. Should AWS learning success require that you can deploy v1, verify the main flow in AWS, explain the deployment in your own words, and then remove the deployment?

Why these matter: MVP success does not prove that later account, search, retention, and authorization behavior works. A recorded demonstration makes the full v1 boundary reviewable, while the second question determines whether extended real use must be repeated after v1 changes. The AWS criterion measures the intended learning outcome without creating an ongoing service commitment.

Recommendation: yes to the recorded demonstration and AWS evidence; run a new seven-day period for v1 because its new persistence and authentication behavior can introduce failures that the MVP test could not expose.

### Response

The user confirmed that v1 requires a recorded end-to-end demonstration of its full product
boundary and must pass a new seven-consecutive-day usage period after v1 features are added.
The user corrected the AWS boundary: only MVP is deployed to AWS. V1, v2, and all currently
planned future releases have no AWS involvement.

### Confirmed direction

- V1 requires a recorded end-to-end demonstration covering its confirmed capabilities and
  rejection of cross-account access.
- V1 must pass its own new seven-consecutive-day usage period.
- Only MVP is deployed to AWS for the temporary learning exercise.
- V1, v2, and currently planned future releases are local and have no AWS scope.
- Earlier statements assigning the AWS exercise to v1 are superseded.

### Remaining question

The MVP seven-day readiness run occurs locally. After it passes, the MVP is deployed
separately and briefly to AWS. The AWS exercise is required for MVP completion. After the local seven-day run passes, the
operator deploys MVP to AWS, verifies the core flow, explains the deployment in their own
words, and removes the deployment. Only then is MVP complete.

### Confirmation

The user explicitly confirmed the local-first sequence and required AWS learning checkpoint
for MVP completion.

### Question cluster 2 — V1 critical failures and product outcome

1. During the v1 seven-day run, should unauthorized access to another account, cross-account
   search results, loss of saved memories, or personal data appearing in telemetry count as
   critical failures that reset the run?
2. Beyond technical correctness, should success require completing and sending every scheduled
   memory during each seven-day run, demonstrating that NoteCapsule actually supports the daily
   habit rather than merely functioning in isolated tests?

Why these matter: v1 introduces account isolation, retained content, search, and telemetry, so
its critical-failure definition must expand beyond MVP delivery behavior. Requiring completion
of scheduled memories connects release success to the human problem identified in Section 1.

Recommendation: yes to both.

### Response

The user confirmed that unauthorized cross-account access, cross-account search results, lost
saved memories, and personal data in telemetry are critical v1 failures that reset the
seven-day run. The user rejected requiring completion and delivery of every scheduled memory.
The seven-day runs require the scheduled notifications to occur correctly; end-to-end memory
completion and sending are demonstrated separately.

### Confirmed summary

- V1 critical failures include account-isolation violations, cross-account search leakage,
  loss of saved memories, and personal data appearing in telemetry.
- A critical failure resets the v1 seven-day run after correction.
- Each seven-day run requires the scheduled notifications to occur correctly.
- The operator is not required to write and send a memory after every notification.
- Recorded end-to-end demonstrations provide separate evidence for composition and delivery.

### Confirmation

The user explicitly confirmed these v1 critical-failure and notification-success boundaries.

### Section 9 learning check and approval

The user confirmed the Section 9 draft and explained that success criteria test whether the system features function correctly. The interviewer clarified that success criteria provide observable proof for complete journeys and critical outcomes rather than requiring every implementation detail to be tested individually. The answer demonstrates the section purpose.

### Approval

The user explicitly approved Section 9. The section-level learning check is complete. Section 9 is approved.

## Section 10 — Open questions and assumptions

### Purpose

Keep intentional deferrals, remaining conflicts, and operating assumptions visible so readers do not mistake them for settled current-release requirements.

### Audit result

No unanswered current-release behavior question remains in the interview. Historical conflict labels in the session are resolved by later confirmed decisions. The remaining items are intentional deferrals and outdated technical-source conflicts rather than blockers to the PRD.

### Question cluster 1 — Final open-item classification

1. Should the React versus JavaFX, MySQL versus SQLite, and Maven versus Gradle disagreements be recorded as technical-document conflicts to resolve in the build plan, rather than product questions in the PRD? The questionnaire currently selects React, MySQL, and Maven.
2. Should the exact v2 AI feature set remain an open future decision, with only opt-in AI, MFA, and OpenID Connect confirmed as v2 direction?
3. Should account email verification, password recovery, Remember me, advanced password policy, experimental login-abuse detection, photos, and mobile support remain intentionally deferred nice-to-haves with no promised release date?

Why these matter: implementation-source conflicts should not pollute a product document, while future ideas should remain visible without becoming accidental commitments. This final audit separates true current-release gaps from deliberate deferrals.

Recommendation: yes to all three.

### Response

The user requested clarification of the React/JavaFX, MySQL/SQLite, and Maven/Gradle conflict.
The user stated that AI is the main point of v2, especially tool calling, embeddings, a vector
database, and RAG. The user confirmed that account email verification, password recovery,
Remember me, advanced password policy, experimental login-abuse detection, photos, and mobile
support remain deferred nice-to-haves without promised release dates.

### Confirmed direction

- Deferred nice-to-haves have no promised release date.
- V2 is centered on AI and specifically targets tool calling, embeddings, vector storage, and
  retrieval-augmented generation.
- V2 completion requires at least one opt-in tool-calling workflow.
- V2 completion also requires semantic retrieval using embeddings, a vector database, and RAG.
- The exact tool workflow, model providers, embedding model, and vector-store implementation
  remain future v2 design decisions.

### Confirmation

The user explicitly confirmed both required v2 AI outcomes. This also revises the approved
Section 8 boundary so the named AI outcomes are mandatory rather than merely directional.
- React, MySQL, and Maven are the confirmed current technical choices.
- JavaFX, SQLite, and Gradle are scrapped; their remaining build-plan references are obsolete
  technical documentation to correct separately.
- The PRD will describe only user-visible web-application behavior and will not prescribe these
  implementation choices unless they create a product constraint.

### Technical conflict resolution

The user explicitly confirmed React, MySQL, and Maven and rejected JavaFX, SQLite, and Gradle.
This resolves the source conflict for PRD purposes without authorizing edits to the build plan.

### Section 10 draft review

The user rejected leaving any item unknown. Section 10 is not approved. The interview will
resolve every future product-behavior question listed for v2. Pure implementation choices will
remain outside the PRD but may be recorded as technical-plan decisions if the user wants zero
project ambiguity.

### Resolution cluster 1 — V2 tool-calling workflow

1. Should the required tool-calling workflow be a draft assistant that receives the current
   memory, may retrieve relevant saved memories and recipient information, and proposes an
   approval-ready revision?
2. Should the assistant be permanently prohibited from sending email, deleting memories,
   changing account settings, or taking any other irreversible action, leaving final review
   and Approve and Send to the operator?
3. When the assistant uses saved memories, should its result identify and link the source
   memories so the operator can verify what influenced the suggestion?

Why these matter: tool calling needs a concrete user outcome and an authority boundary. Source
links make retrieved personal memories reviewable and reduce the risk that generated content is
accepted without understanding where it came from.

Recommendation: yes to all three.

### Response

The user rejected a memory-drafting agent as the primary tool-calling workflow and preferred an
agent related to operating the system, such as responding to suspicious login activity or
generating reports from telemetry and metadata. The user is open to workflow suggestions.

### Recommended direction

Use an operations analyst agent. It queries non-personal operational telemetry through narrow
read-only tools, investigates patterns in login failures, delivery failures, autosave failures,
and notification behavior, and produces evidence-linked reports with recommended actions.
Deterministic security controls such as v1 login throttling continue to enforce safety without
depending on an AI model.

### Resolution cluster 2 — Operations-agent authority

1. Should the required v2 tool-calling workflow be this operations analyst agent?
2. Should it run only when the operator requests a report or on a configured schedule, rather
   than sitting in the real-time login path?
3. Should it remain read-only: it may generate reports and recommend actions but may not block
   accounts, change thresholds, delete data, send messages, or modify settings?

Why these matter: a system agent provides a concrete tool-calling use case, but placing a
non-deterministic model in the login path could weaken availability and security. Read-only
analysis preserves human control over operational changes.

Recommendation: yes to all three.

### Response

The user selected a different required v2 tool-calling workflow: Create memory from date. The
operator selects a date; authorizes Google with minimum read-only access; retrieves Google
Calendar events and relevant Gmail messages for that date; manually selects or uploads photos;
sends selected information to an LLM for a short daily summary; previews the result; and must
approve it before saving. Google Maps Timeline is excluded. The feature must not assume broad
Google Photos library search and instead uses manual photo selection or upload. OAuth tokens
must never be exposed to React.

The requested Spring-service separation, environment variables, setup instructions, error
handling implementation, and test structure belong in the technical plan. The PRD retains the
observable integration failures and the security constraint that OAuth tokens remain
server-side. This workflow supersedes the proposed operations analyst as the required v2 agent.

### Resolution cluster 3 — Date scope, source selection, and saved outcome

1. After retrieval, must the operator explicitly select which calendar events and Gmail
   messages are sent to the LLM, just as photos are manually selected?
2. Should the chosen date use the account confirmed time zone, with calendar events included
   when they overlap that local date and Gmail messages included by their sent or received time
   on that date?
3. After approval, should the generated summary become an unfinished memory draft that enters
   the normal recipient review and sending flow, or a saved memory that is retained without
   sending?

Why these matter: explicit source selection limits unnecessary disclosure to the model. A date
needs time-zone and inclusion rules. The final state determines whether this feature creates
content for the existing send workflow or acts as a private journaling import.

Recommendation: require explicit source selection, use the confirmed account time zone, and
create an unfinished draft so the existing review, recipient, and delivery protections still
apply.

### Response

The user confirmed explicit selection of Calendar events and Gmail messages before LLM use,
manual photo selection or upload, and account-time-zone rules for the chosen date. The user
confirmed that the generated message must be approved. The exact relationship between approving
the AI output and the existing Approve and Send action still requires clarification.

### Confirmed direction

- Only operator-selected Calendar events, Gmail messages, and photos are sent to the LLM.
- The date is interpreted in the account confirmed time zone.
- Calendar events qualify when they overlap that local date.
- Gmail messages qualify by sent or received time on that local date.
- The LLM output is a short daily-summary preview and cannot be accepted silently.
- The operator must approve the generated message.

### Remaining approval question

Should v2 use two explicit authorization steps: Use as draft accepts the AI summary into the
normal autosaved memory flow, and the later Approve and Send action separately authorizes email
delivery?

Recommendation: yes. Accepting AI-generated text and sending an email are different decisions,
and neither should imply the other.

### Response

The user explicitly confirmed two separate authorization steps. Use as draft accepts the AI
summary into the normal autosaved memory editor. Approve and Send later authorizes email
delivery after review of the final text and recipients. Neither approval implies the other.

### Resolution cluster 4 — Google-data minimization and retention

1. Before selection, should Gmail results show only enough information to choose messages, such
   as sender, recipients, subject, time, and a short snippet, and retrieve full message bodies
   only for messages the operator selects?
2. For selected Calendar events, should the model receive the title, start and end time,
   location, description, and attendee names, while excluding attendee email addresses unless
   the operator explicitly includes them?
3. After the summary is accepted as a draft or the flow is discarded, should NoteCapsule delete
   the temporary Google source data and photo uploads, retaining only the resulting draft and
   non-personal operational metadata? Google connections must be disconnectable, and OAuth
   tokens remain protected server-side.

Why these matter: read-only access still exposes private data. Fetching full content only after
selection and deleting temporary imports limits what NoteCapsule and the LLM receive and retain.
OAuth tokens are credentials and require stronger handling than ordinary telemetry.

Recommendation: yes to all three.

### Response

The user confirmed all Google-data minimization and retention requirements. Gmail initially
shows selection metadata and snippets and fetches full bodies only for selected messages.
Calendar attendee email addresses are excluded unless explicitly included. Temporary Google
data and uploaded photos are deleted after the generated summary is accepted or the flow is
discarded. Google is disconnectable, and OAuth tokens remain protected server-side.

### Resolution cluster 5 — Local model and RAG behavior

The questionnaire already selects a local Python service connected to Ollama llama3.1:8b,
local embeddings, and a local vector store, with saved memory text and embeddings never sent
to hosted AI or a hosted vector database.

1. Should those existing choices be binding v2 requirements, including llama3.1:8b as the
   initial generation model?
2. Should embeddings and vector search remain isolated by authenticated account so retrieval
   can never return another account memory?
3. When RAG or memory chat uses saved memories, should every response identify and link the
   source memories it relied on, and clearly say when no supporting memory was found?

Why these matter: selecting local AI resolves the model-hosting privacy boundary. Account-level
filtering must apply before retrieval, not only when displaying results. Citations let the
operator verify generated claims against the actual memories.

Recommendation: yes to all three.

### Response

The user confirmed that embeddings and vector retrieval remain isolated by authenticated
account. The user also approved source-linked RAG responses: each answer identifies the saved
memories actually provided to the model, links to those memories, and explicitly states when
no supporting memory was found. The earlier yes did not confirm the proposed local model stack.

### Confirmed direction

- Retrieval filtering occurs by authenticated account before memories reach the model.
- RAG and memory-chat output cite and link every source memory included in model context.
- If retrieval finds no supporting memory, the response says so rather than presenting an
  unsupported answer as memory-derived.
- V2 binds to a local Python AI service, Ollama, llama3.1:8b for generation, local embeddings,
  and local vector storage.
- Saved memory text and embeddings are never sent to hosted AI or hosted vector databases.
- nomic-embed-text:v1.5 is the initial embedding-model placeholder.
- Pre-release research may replace it with another local embedding model that preserves the
  privacy boundary and passes the retrieval evaluation.
- Replacing the embedding model requires rebuilding all vectors from canonical saved-memory
  text; vectors from different models are never mixed.

### Confirmation

The user explicitly confirmed the local AI stack and correctly identified the need for a
separate embedding model in addition to the text-generation model.

### V2 technical placeholders

- nomic-embed-text:v1.5 is the initial local embedding-model placeholder.
- Qdrant is the initial local vector-database placeholder.
- Research may replace either before v2 release.
- A replacement embedding model must remain local, pass the retrieval evaluation, and trigger
  a full vector rebuild from canonical saved-memory text.
- A replacement vector database must remain local, persist vectors, enforce account-filtered
  retrieval, and support complete deletion and re-indexing.
- These placeholders resolve the starting implementation without making vendor identity a
  permanent product requirement.

### Resolution cluster 6 — Semantic search and memory chat

1. Should semantic search operate only on memories the account explicitly saved, returning
   matching excerpts, dates, recipients, and links to the full source memories?
2. Should memory chat answer questions only from retrieved saved memories, cite every memory
   supplied to the model, and say that it lacks support when retrieval finds none?
3. When a saved memory is cleared or an account is deleted, must its embeddings be removed
   immediately enough that subsequent searches and chats cannot retrieve it?

Why these matter: these rules define what RAG can know, how users verify it, and whether content
deletion also applies to derived vector data.

Recommendation: yes to all three.

### Response

Awaiting user response.

### Resolution cluster 7 — Google authorization and partial failures

1. Should Calendar and Gmail be connected incrementally and independently, requesting Calendar
   events read-only access only when Calendar is enabled and Gmail read-only access only when
   Gmail is enabled?
2. Should NoteCapsule store refresh tokens encrypted on the server so connections persist,
   while Disconnect Google revokes the grant and deletes the stored tokens?
3. If one Google source fails or is not authorized, should Create memory from date identify the
   unavailable source and allow the operator to continue with the other authorized sources and
   manually selected photos?

Why these matter: incremental authorization minimizes consent. Persistent connections need
refresh-token protection and a complete disconnect path. Partial-source recovery prevents a
Gmail outage from blocking Calendar and photo use.

Recommended scopes: Calendar events read-only and Gmail read-only. Google tokens remain
server-side and never reach React.

Recommendation: yes to all three.

### Response

The user confirmed independent incremental authorization for Calendar and Gmail, encrypted
server-side refresh-token storage, token revocation and deletion on disconnect, and graceful
continuation when one Google source is unavailable. Calendar uses events read-only access;
Gmail uses messages read-only access. OAuth tokens never reach React.

### Resolution cluster 8 — AI timeout, cancellation, and recovery

1. Should the operator be able to cancel any AI operation immediately, with generation timing
   out after two minutes and offering Retry or Return to draft rather than retrying
   automatically?
2. During generation or failure, should NoteCapsule preserve the selected source choices and
   current draft temporarily, never overwrite an existing draft, and clean temporary Google
   data and photos after success, explicit cancellation, or abandoning recovery?
3. Should AI failures always leave the complete non-AI workflow usable and display a safe,
   actionable explanation without exposing prompts, source content, tokens, or raw model errors?

Why these matter: local models can be slow or unavailable. A bounded, cancellable operation
prevents the AI feature from trapping the operator, while temporary recovery avoids forcing
source selection to be repeated after a transient failure.

Recommendation: yes to all three.

### Response

The user confirmed the two-minute AI timeout, immediate cancellation, no automatic retry,
recovery of selected sources and current draft, protection against overwriting an existing
draft, temporary-data cleanup, safe errors, and uninterrupted access to the non-AI workflow.

### Resolution cluster 9 — V2 telemetry and completion evidence

1. Should on-device AI telemetry record model and prompt versions, tool names, source-memory
   identifiers, approval outcomes, latency, token usage, errors, retries, and cancellation,
   while excluding memory text, Gmail and Calendar content, photos, prompts, model responses,
   OAuth tokens, and recipient identities?
2. Should RAG completion require a fixed evaluation set of at least 10 questions with known
   source memories, with the correct source appearing in the top five results for at least 8
   questions, every generated claim linked to an included source, and zero cross-account
   retrieval?
3. Should Create memory from date completion require a recorded demonstration using Calendar,
   Gmail, and a manually selected photo; explicit source selection; summary generation; Use as
   draft; editing; final review; and Approve and Send, plus graceful continuation when one
   Google source is unavailable?

Why these matter: v2 needs observable evidence, not only a feature list. The retrieval threshold
is small enough for a student project but catches obvious RAG failure. The end-to-end
demonstration proves the tool workflow, privacy choices, approvals, and recovery behavior.

Recommendation: yes to all three.

### Response

The user confirmed all three v2 telemetry and completion-evidence requirements. V2 keeps
content-free on-device AI telemetry, passes the defined 10-question RAG evaluation with zero
cross-account retrieval, and records the complete Create memory from date demonstration and
partial-source failure path.

### Resolution cluster 10 — Remaining v2 AI feature boundary

The questionnaire also assigns local Whisper voice-to-text and Ollama grammar and tone
suggestions to a later local AI edition. To leave no release ambiguity:

1. Is local voice-to-text through the Python Whisper service required for v2, or deferred
   beyond v2?
2. Are opt-in grammar and tone suggestions required for v2, or deferred beyond v2?
3. Is memory chat over saved memories a required v2 feature in addition to semantic search and
   source-linked RAG answers?

Why these matter: all three appear in earlier planning sources, but only semantic retrieval and
Create memory from date have been made explicit v2 commitments in this interview.

Recommendation: require memory chat because its behavior and evaluation are already defined;
defer voice-to-text and grammar or tone assistance unless you intentionally want a larger v2.

### Response

The user moved local voice-to-text and grammar and tone suggestions to uncommitted
nice-to-haves. Source-linked memory chat remains a required v2 feature alongside semantic
search, RAG, and Create memory from date.

### Confirmation

The v2 AI feature boundary is fully resolved:

- Required: Create memory from date, local generation, semantic search, embeddings, local
  vector storage, source-linked RAG, memory chat, MFA, and OpenID Connect.
- Nice-to-have without a release commitment: voice-to-text and grammar or tone suggestions.

No product-behavior question remains open. The embedding model and vector database are
controlled technical placeholders with confirmed replacement constraints, not unresolved
product requirements.

### Section 10 learning check and approval

The user explained that the final section prevents confusion by clearly labeling deferred ideas and controlled placeholders. This concise answer demonstrates the section purpose.

### Approval

The user explicitly approved the Section 10 decisions and completed the section-level learning check. Section 10 is approved. All ten PRD sections are approved. The next step is to assemble and display the complete PRD for final review. docs/PRD.md remains unwritten pending explicit approval of that exact complete draft.

## Progress

- 1. Problem and motivation — approved
- 2. Goals and non-goals — approved
- 3. Users and context — approved
- 4. Product overview — approved
- 5. User journeys — approved
- 6. Functional requirements — approved
- 7. Quality requirements — approved
- 8. Release boundaries — approved
- 9. Success criteria — approved
- 10. Open questions and assumptions — approved

## Deferred current-release items

- Full authentication
- Voice-to-text and other AI features
- Agent workflows
- RAG and semantic search
- Photo attachments
- Mobile application/support
- Public hosting
- Automatic Desktop-file saving
