# Senior Engineering Review: NoteCapsule PRD (Claude)

Reviewed: `docs/PRD.md`, cross-checked against `docs/MILESTONES_claude.md` and
`docs/Milestones_codex.md` for whether roadmap detail already resolves a given PRD gap.

Note: `docs/codex_prd_review.md` already reviews this PRD in depth. This review was done
independently rather than by reading that file first-to-last and reacting to it. Several
findings converge with it, which is a useful signal — where two independent reviews land
on the same blocker, treat it as high-confidence, not duplicated noise. This review also
surfaces a few gaps the other one doesn't (notably durability/backup, and scheduler
determinism), and pushes back in one place (password policy).

## Overall assessment

This is an unusually disciplined PRD for a personal project — it takes failure modes,
approval integrity, and account isolation seriously, and the prose is precise about *who*
can do *what*. That's rare and worth preserving as you iterate.

The weakness is that precision in prose doesn't always translate to precision in
mechanism. Several requirements describe the desired *outcome* ("unknown delivery is
never automatically retried," "anonymous metadata") without describing the *evidence or
data model* that would let an engineer implement it correctly or a test verify it. The
document also occasionally states a requirement as an "open question or assumption" in
§10 when it has already been decided — which will confuse whoever reads §10 looking for
things that are still undecided.

None of this requires a rewrite. It requires about five design decisions before M1/M2 of
the roadmap, and a handful of wording tightenings that cost an afternoon.

## What's working well

- **Approval integrity is well specified.** "Back to editing invalidates approval,"
  "repeated activation is guarded," and the confirmed/failed/unknown three-way split
  (§5, §6) are exactly the right shape for a system whose worst failure is an
  irreversible email. Keep this pattern as the template for every other state machine
  you add later (overdue links, retention choice, AI generation).
- **Release boundaries are legible.** MVP → v1 → v2 each add one coherent capability
  (unauth flow → accounts → AI), and §8 keeps AI out of v1 entirely. This is a sane
  sequencing decision and the roadmap docs follow it faithfully.
- **Privacy is treated as a first-class requirement, not an afterthought** — content
  exclusion from logs/telemetry/metadata is stated up front in §7, not bolted on later.

## Blocking issues — resolve before the relevant milestone begins

### 1. MVP's only access control is an unguessable URL

MVP has no authentication (§2, §9) but is deployed to AWS (§8) and the product is
elsewhere described as "never a public website" (§2). A private link is a bearer
credential, not an access-control model: anyone who obtains it — via a proxy log,
Referer header, shoulder-surf, or shared Discord channel — has the same access as the
operator, for as long as the link (or the app root, if it's reachable directly) stays
open.

Before M9/AWS deployment (`MILESTONES_claude.md`) or M5 (`Milestones_codex.md`), decide
and write down:
- Does the token gate only the initial draft-open action, or every subsequent request
  for that session?
- Is the app reachable at all without a valid token (i.e., is there a root URL that
  serves *anything*)?
- Is the AWS deployment network-restricted (security group allowlist, VPN), or is
  "briefly deployed and then removed" the entire mitigation?

A time-boxed deployment reduces exposure window, it doesn't remove the need for an
access-control decision.

### 2. "Unknown delivery" has no defined detection mechanism

§6 correctly distinguishes confirmed failure from unknown outcome, and §5 defines the
user-facing choices (Resend / Resend and save a copy). But nothing in the PRD defines
**what evidence produces which state.** This matters because the implementation is not
free to interpret it however is convenient — the categories drive different, and
differently risky, user-facing behavior.

Concretely, decide:
- What counts as confirmed success — an HTTP 2xx with a provider message ID, or just "the
  request didn't throw"?
- What counts as confirmed failure — a provider error response — versus unknown — a
  timeout, connection reset, or process crash mid-request?
- If using a transactional provider (SES, Postmark, SendGrid), most timeouts *are*
  resolvable by re-querying the provider's delivery API by message ID before ever
  showing "unknown" to the user. Is that reconciliation step in scope, or is "unknown"
  the permanent terminal state whenever the synchronous call doesn't complete cleanly?
- Does every send attempt get a stable ID that a resend's "save a copy" can reference,
  so "duplicate" is a table lookup rather than a user-trusted claim?

This is the single hardest piece of engineering in the whole product and deserves its
own short design note before M2/M6.

### 3. No durability or backup requirement for the thing the product exists to protect

The entire premise (§1) is that memories about loved ones are precious and easily lost
if not captured. Yet nothing in §6–§9 requires a backup, export, or disaster-recovery
story for the MySQL database holding them. "V1 is local" (§3) means the single point of
failure is one machine's disk. A dropped table, a bad migration, or a failed drive
silently defeats the product's entire purpose in a way no functional requirement in this
document currently guards against.

Add a requirement — even a minimal one for v1 (e.g., "retained memories must be
exportable/dumpable on demand," or "a documented backup procedure exists and has been
exercised at least once") — before treating retention (§6, V1-9) as done. This doesn't
need to be sophisticated; it needs to exist.

### 4. Post-delivery retention choice has an undefined abandonment state

Retention is opt-in and decided "after confirmed delivery" (§4, §6). If the browser
closes, the process crashes, or the user simply never answers between send and the next
session, the memory sits in limbo: not yet retained, not yet deleted. Immediate
auto-delete risks discarding content before an informed choice; open-ended waiting
silently becomes de facto indefinite retention, which contradicts the "retention is
opt-in" framing.

Define a durable, named state (e.g. `delivered_awaiting_retention_choice`), what happens
to it on next login, and — if you want a hard default — an explicit timeout after which
the default (delete) applies, stated as a number, not left implicit.

### 5. The flagship 7-day gate doesn't have to exercise the flagship flow

§9 explicitly says "sending after every notification is not required" for both the MVP
and v1 seven-day runs. Combined with §8's framing of the run as proof of the "core
Discord-to-email flow," the release gate as written can pass on schedule-firing alone,
without a single real send exercised during the whole week.

Tighten the gate to require, within the seven days: a minimum number of complete real
sends, at least one restart-recovery exercise, and at least one deliberately triggered
failure (confirmed and unknown) — not just "the notification arrived." Otherwise the
most consequential behavior in the product ships with the weakest evidence bar.

## Significant issues — resolve before the relevant milestone

### 6. Scheduler determinism gaps: DST and wall-clock edge cases

§6 requires "a detected, confirmable, correctable time zone" and weekday/time
scheduling, and the roadmap (both milestone docs) sensibly calls for an injected clock
and boundary tests. But the PRD itself never states the *policy* for the classic
recurring-scheduler edge cases, so "boundary tests" has nothing authoritative to test
against:
- Spring-forward: the configured wall-clock time doesn't exist on the transition day.
- Fall-back: the configured wall-clock time occurs twice.
- Backend downtime spanning the scheduled instant — does it fire late on restart, or
  is that occurrence simply skipped?
- A schedule or time zone edit that happens after the day's link has already been
  generated but before the next one is due.

Pick a rule for each (a common, defensible default: on a nonexistent time, fire at the
next valid instant that day; on a duplicate time, fire once, on the first occurrence;
on missed-during-downtime, fire once on restart if still within the same scheduled day,
otherwise skip) and put it in §6, not just in test names.

### 7. §10 mixes settled requirements with actual open questions

§10 is titled "Open questions and assumptions" and opens with "There are no open product
questions," but the rest of the section is a dense block of concrete, decided V2
requirements (time zone handling for Create-from-date, Gmail metadata-then-body
sequencing, refresh-token custody, the embedding/vector-store replacement contract).
These are fine requirements — they just aren't questions, and burying them in a section
whose header promises the opposite makes them easy for a future reader to miss or
mistrust ("is this actually decided, or is it a working assumption?").

Move the decided material into §6/§10-as-renamed-"V2 technical requirements," and reserve
an actual open-questions section for things that are genuinely unresolved (see items 1–4
above, which are better candidates for this section than anything currently in it).

### 8. Recurring soft qualifiers reduce testability

Words like "may" (§6: "email addresses may be searchable"), "normally" (§7: "normally
respond within two seconds"), and "secure" (§6, §7, used seven times without a definition)
appear throughout. Each instance is a place where an engineer and a QA reviewer could
reasonably disagree about whether a behavior satisfies the requirement. This isn't
pedantry — it's the difference between an acceptance criterion and a description.

Two cheap fixes: (a) replace "may" with a firm yes/no wherever the PRD author actually
knows the answer (the email-address-search example reads like a decided "yes" that
picked up a hedge in editing); (b) for "secure" and "normally," either give a concrete
definition inline or point at a named design doc/threat model that will define it — don't
leave the word to carry the requirement by itself.

### 9. The v1 password floor is set lower than the product's own security posture implies

§5 requires only a "nonblank password." Given the document is otherwise careful about
hashing, session limits, throttling, and cross-account isolation, an unbounded-weakness
password is the softest point in the whole authentication story, and it's the *first*
thing an attacker touches. This doesn't require "advanced password policies" (correctly
deferred per §2) — a defensible middle ground is a minimum length (e.g., 8–12 characters)
with no composition rules, which stops trivial single-character passwords without adding
UX friction or contradicting the deferral of advanced policies.

### 10. Indefinite, unclearable metadata needs a defined schema, not just a promise

§6 says retained operational metadata "excludes content, recipient identity, usernames,
and other personal data" and is "retained indefinitely and cannot be manually cleared,"
even by account deletion (which leaves "only anonymous metadata"). Two follow-on
problems: first, "anonymous" isn't a property a field either has or doesn't — timestamps
and behavioral counts can still be linkable in aggregate, even with obvious identifiers
stripped, though the risk here is modest since this is a single-user local product, not
a multi-tenant SaaS with a real re-identification adversary. Second, "indefinite and
never clearable" is a strong, permanent commitment for a product that otherwise treats
user control as a core value — worth confirming that's intentional rather than a default
that was never revisited.

Write the actual field list (event type, timestamp, outcome, error category, counts —
whatever it ends up being) into §6 once, and treat that as the definition of
"anonymous" for this product, rather than relying on the adjective to do the work.

### 11. V2 bundles four independent risk surfaces into one release

V2 combines MFA, OIDC account linking, external OAuth integrations (Calendar/Gmail),
local AI generation, embeddings/vector storage, and RAG/chat (§2, §10). Each of
(a) identity/session changes, (b) third-party OAuth data handling, and (c) local AI
correctness/grounding is a release-sized effort with its own failure modes and its own
acceptance evidence (§9 already implicitly agrees, since it demands separate evidence for
Create-from-date and RAG). Consider formally splitting V2 into ordered sub-releases (identity
hardening → Google integrations → generation/embeddings → retrieval/chat) so a slip or
security finding in one doesn't hold the others hostage, and so each has its own gate
rather than one combined V2 gate covering six different subsystems.

## Minor / polish

- **Stable requirement IDs** (`FR-1`, `QR-3`, etc.) in the PRD would let the milestone
  docs reference requirements instead of re-paraphrasing them — right now a PRD wording
  change requires manually re-checking every milestone doc for drift.
- **Email bounce/validation handling is unspecified.** §6 requires "valid email
  addresses" for address-book entries — is that syntactic validation only, or is a hard
  bounce after send expected to route into the confirmed-failure state? Worth one
  sentence to close the loop with item 2 above.
- The phrase "There are no open product questions" (§10) should go regardless of the
  §10 restructuring in item 7 — at minimum soften to "no open questions beyond those
  tracked separately," since items 1–4 in this review are exactly the kind of thing that
  sentence claims don't exist.

## Where the roadmap already answers a PRD gap (pull this back into the PRD)

`Milestones_codex.md` M1/M4 imply the MVP/v1 draft-persistence distinction is "explicit
save-and-restore on confirmed content" for MVP vs. "timed autosave" for v1 — the PRD
(§2, §11) never states this itself, only the roadmap does. Since both roadmap docs say
the PRD wins in any conflict, this distinction should live in the PRD's own text (§6,
Configuration/memories/links), not only be inferable from milestone deliverables.

## Recommendation

Approve the direction. Before implementation continues past current scaffolding, resolve
items 1–5 (they change data model and API shape, so are expensive to retrofit) and
items 6–7 (they're roadmap-blocking for M3/§10 respectively). Items 8–11 and the minor
section are cheap wording/schema fixes that can land alongside the milestones they touch
rather than blocking start.
