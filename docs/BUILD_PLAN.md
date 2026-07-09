# NoteCapsule — Build Plan

A phased, A‑to‑Z plan for taking NoteCapsule from a scratchpad README to a working
desktop application. This is **not a strict schedule** — it's a recommended order of
operations. Each phase is designed to be a **hands‑on coding experience** *and* an
**AI‑accelerated learning experience**. Those two things are in tension, and managing
that tension well is the whole point.

> **Read this first, then fill out [`PROJECT_QUESTIONNAIRE.md`](./PROJECT_QUESTIONNAIRE.md).**
> The questionnaire locks down the details this plan assumes. Do that before Phase 1.

---

## The one rule that makes or breaks this project

**AI is your tutor and your pair, not your ghostwriter.**

If you let Claude or Codex write entire features while you watch, you'll ship faster and
learn almost nothing. If you refuse to use AI at all, you'll move slowly and miss the
single most valuable skill of your generation of engineers. The goal is the middle:
**use AI to learn faster than you could alone, while still writing the code that matters
with your own hands.**

A practical way to think about it — put every task into one of four modes:

| Mode | What it means | Use it for |
|------|----------------|------------|
| **Scaffold** | AI generates boilerplate you'd only copy‑paste anyway | Project setup, config files, build wiring, repetitive DTOs |
| **Tutor** | AI *explains*, you write the code | New concepts (DI, JPA, generics), understanding an error |
| **Pair** | AI suggests, you decide and type | Core business logic, tricky algorithms |
| **Review** | You write, AI critiques | Every non‑trivial change before it becomes a PR |

**The anti‑pattern:** "Claude, build the scheduling feature." For anything that's the
*substance* of the app, don't do this. Instead: "Explain how Spring's `@Scheduled`
works and show me a tiny example, then let me write the real thing and review it."

A good gut check: **if you couldn't re‑explain the code you just committed in a PR review,
you didn't learn it — go back to Tutor mode.**

---

## Assumed tech stack (confirm in the questionnaire)

You said Java/Spring and a Linux desktop app, so this plan is written against:

- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot (for the application container, dependency injection, and services)
- **Desktop UI:** JavaFX
- **Storage:** SQLite (local, file‑based) via Spring Data JPA
- **Build tool:** Gradle (or Maven — your call)
- **Testing:** JUnit 5 + Mockito + AssertJ
- **CI:** GitHub Actions
- **AI (opt‑in, later):** provider‑neutral; leaning local/offline (whisper.cpp or Vosk for
  speech‑to‑text, Ollama for text) with cloud APIs (e.g. Grok STT) as an option

> ⚠️ **A note on Spring + desktop:** Spring Boot is usually a *web/backend* framework, and
> JavaFX is the *desktop UI*. They coexist fine — Spring runs as the "brain" (DI, services,
> data, scheduling) and JavaFX is the "face." Wiring them together is itself a great
> learning exercise. The questionnaire will make sure this is really what you want before
> you commit.

---

## Cross‑cutting habits (start these in Phase 0, keep them forever)

These aren't phases — they run through *every* phase. This is what makes it feel like a
real job instead of a class project.

- **Branch → PR → review → merge.** Never commit straight to `main`. Every unit of work is
  a branch and a pull request. A reviewer goes over every PR — just like on a real team — and
  you'll also set up an **automated code reviewer** (CodeRabbit, GitHub Copilot review, or
  Claude‑based) so you get a second set of eyes on every change.
- **CI on every PR.** GitHub Actions builds the project and runs the tests. A red build
  does not get merged. Ever.
- **Tests grow with features.** Not 100% coverage — *meaningful* coverage of the logic that
  would actually break.
- **Small PRs.** A 2,000‑line PR is un‑reviewable and un‑learnable. Aim for PRs you could
  walk someone through in 10 minutes.
- **Write a real PR description — and prove you learned.** Every PR uses
  [`.github/PULL_REQUEST_TEMPLATE.md`](../.github/PULL_REQUEST_TEMPLATE.md), which forces a
  short **Learning & AI use** section: *what you wrote yourself*, *what AI helped with and in
  which mode*, *what you can now explain*, and *one thing you still don't fully understand*.
  This is the single most important habit in the whole project. It turns "AI is my tutor, not
  my ghostwriter" from a nice idea into something a reviewer can actually see and discuss in
  every review — and it builds the habit of understanding your own code instead of just
  running it, which is the difference between a coder and an engineer.

---

## Phases

### Phase 0 — Foundations & first scaffold *(AI does the setup; you learn the terrain)*

**Goal:** A repo you can build and run, and an understanding of what all the pieces are.

**You build (hands‑on):**
- Fill out the [questionnaire](./PROJECT_QUESTIONNAIRE.md) with Codex.
- Install the toolchain: JDK 21, IntelliJ IDEA (Community is fine), Git.
- Repo hygiene: `.gitignore`, the [PR template](../.github/PULL_REQUEST_TEMPLATE.md),
  branch protection on `main`.
- **A throwaway warm‑up PR to learn the flow with zero code risk:** branch, make a tiny
  docs edit (e.g. fill in one questionnaire answer), open a PR, get it reviewed, respond to a
  comment, and merge. Do this *before* any real code so the GitHub PR mechanics are muscle
  memory by the time the code actually matters.

**AI does (Scaffold mode):** Let Claude/Codex generate the initial Spring Boot + JavaFX
skeleton — a project that launches an empty window. This is the "foundation" AI is *good*
at and that you'd learn little from typing by hand.

**Learn (Tutor mode — ask AI to explain each of these):**
- What is Inversion of Control (IoC) and dependency injection, and why does Spring exist?
- What's in the generated project? Walk through the folder structure until *nothing* is a
  mystery box.
- What's a build tool (Gradle/Maven) actually doing?

**Design concept:** Layered architecture. Set up packages now so you have a home for
everything later: `ui`, `service`, `repository` (or `persistence`), `domain`, `config`.

**Definition of done:** `./gradlew run` opens a window. First PR: the scaffold. Once it's
reviewed, turn on branch protection + a required status check.

---

### Phase 1 — Walking skeleton: memory → approve → email *(the thin vertical slice)*

**Goal:** The narrowest possible end‑to‑end path that actually works. Type a memory,
approve it, and an email really sends. Ugly is fine. *Working* is the point.

**You build (Pair mode — this is core; you write it):**
- A minimal JavaFX screen: a text area + a "Submit" button.
- An approval step: show the memory, "Approve & Send" / "Cancel."
- A `Memory` domain object and an `EmailSender` **interface**.
- **Build the fake sender first.** Implement `EmailSender` as a `LoggingEmailSender` that
  just prints "would send: …" and records the call. Get the *entire* flow — type → approve →
  "sent" — working against the fake. Only *then* add a real `SmtpEmailSender`.
- **Now wire up real SMTP,** using a dev inbox (Mailtrap, or a throwaway Gmail with an app
  password) so you're not spamming a real address while iterating.

> **Why fake-first?** SMTP credentials, Gmail app passwords, and provider quirks are exactly
> the kind of yak-shaving that stalls a first vertical slice for two days. Building the fake
> first means your walking skeleton *works end-to-end in an hour*, and — because both are the
> same interface — it's also your first real taste of why we program to interfaces (Phase 1's
> whole lesson) and how you test without touching the network.

**Suggested PR slices** (an example of how to chop a phase — you'll get a feel for this):
1. `Memory` domain object + `EmailSender` interface + `LoggingEmailSender` + a unit test.
2. The GitHub Actions CI workflow (see the CI checkpoint below) — set it up as soon as there's a
   test to run, so every PR from here on is gated.
3. The JavaFX submit/approve screen wired to the fake sender.
4. The real `SmtpEmailSender` + dev-inbox config.

**Learn:**
- How a JavaFX event handler calls a service, which calls a sender — the **flow of control
  through layers**.
- Dependency injection for real: the UI shouldn't `new` up an `EmailSender`; Spring injects
  it. Ask AI *why* that matters (spoiler: testing, swapping implementations).

**Design patterns to try:**
- **Dependency Injection** (Spring gives you this — use constructor injection).
- **Service layer** — UI stays dumb; logic lives in a service.
- **Repository** — even if it's just an in‑memory list for now, hide storage behind an interface.

**Testing focus — your first tests:**
- Unit‑test the service that decides "is this memory valid to send?"
- Learn **Mockito**: mock the `EmailSender` so the test doesn't actually send email.
- This is the payoff of DI — you literally cannot test cleanly without it.

**CI checkpoint:** Set up the **GitHub Actions** workflow now: on every PR, build + run
tests. From here on, a failing build blocks merge. Add branch protection requiring the check
to pass, and wire up the **automated PR reviewer**.

**Definition of done:** On a real machine, typing a memory and approving it sends an email,
and there's at least one meaningful passing test. 🎉 This is your first "it's alive" moment.

---

### Phase 2 — Persistence: drafts and saved memories *(make it survive a restart)*

**Goal:** Memories and drafts are stored locally and survive closing the app.

**You build (Pair mode):**
- SQLite + Spring Data JPA. Turn `Memory` into a JPA entity.
- A real repository (now backed by the database, behind the same interface from Phase 1 —
  notice how little the rest of the app changes! That's the Repository pattern earning its keep).
- Save‑draft / resume‑draft flow.

**Learn:**
- JPA entities, repositories, and what an ORM is doing under the hood (and where it bites you).
- Transactions at a basic level.
- Optional but valuable: **Flyway** for database migrations — how real apps evolve a schema.

**Design patterns:** Repository (for real now). Discuss with AI: how does swapping the
in‑memory repo for a JPA repo *not* break the service layer? That's the lesson.

**Testing focus:** Repository/persistence tests against a **temporary SQLite file** (create it
in the test, delete it after) rather than in‑memory H2. It's tempting to reach for H2 because
it's the common tutorial default — but H2 and SQLite differ in subtle ways (types, SQL
dialect), and a test that passes against a *different* database than production teaches you a
false lesson. **Test against the database you actually ship.** Also learn the difference
between a **unit test** and an **integration test** here — this is your first integration test.

**Definition of done:** Write a memory, close the app, reopen it, your drafts are still there.

---

### Phase 3 — Scheduling & notifications *(the TDD phase — do this one test‑first)*

**Goal:** Schedule a time to write; if a memory goes undone past a threshold (your README
says ~30 min), get nudged (Discord webhook, desktop notification, or email).

**This is your Test‑Driven Development phase.** The core logic here — "given the schedule
and the current time, is a memory due? overdue? how overdue?" — is **pure, deterministic
business logic**, which makes it the *perfect* first TDD experience. TDD on UI or email is
awkward; TDD on this is a joy.

**How to run the TDD loop (this is the skill to build):**
1. Sit down with Claude/Codex and **break the feature into small behaviors**: "a memory
   scheduled for 9:00 is not overdue at 9:15," "is overdue at 9:31," etc. Have the AI help
   you *enumerate the test cases* — but **you write the tests.**
2. **Red:** write one failing test.
3. **Green:** write the *minimum* code to pass it.
4. **Refactor:** clean it up, tests stay green.
5. Repeat. Let the design *emerge* from the tests.

> Ask the AI to **list the tests it thinks you need and explain the edge cases**, then you
> write them. That's the sweet spot: you learn what good test coverage looks like without
> outsourcing the actual thinking.

**Learn:**
- **TDD red‑green‑refactor** as a rhythm.
- Spring's `@Scheduled` for the background check.
- **A huge testability lesson:** don't call `LocalDateTime.now()` directly — inject a
  `java.time.Clock`. Now you can control "what time is it" in tests. This one trick will
  make you look senior. Ask AI why hidden time/randomness is the enemy of testable code.

**Design patterns to try:**
- **Strategy** — a `NotificationChannel` interface with `DiscordChannel`, `DesktopChannel`,
  `EmailChannel` implementations. The app picks one (or several) without `if/else` sprawl.
- **Observer** (optional) — "memory overdue" as an event that notifiers subscribe to.

**Definition of done:** A well‑tested "is it overdue?" component (written test‑first), and a
real nudge fires when you blow past your window.

---

> ## 🏁 v1 candidate — you have a shippable app here
>
> Stop and notice: after Phase 3 you have an app that reminds you, lets you write and approve
> a memory, emails it, saves drafts, and nudges you when you're late. **That is a complete,
> useful v1** you could actually use daily for a week — which is a real milestone worth
> celebrating — you've shipped a complete, working desktop app, end to end.
>
> Everything after this — photos, search, and the AI work — is **v1.1 and beyond.** Consider
> tagging a `v1.0` release here. Knowing *when to ship* and drawing a line around scope is
> a genuinely hard skill in its own right; a lot of side projects sprawl forever and never ship
> anything — drawing the line here is how you avoid that trap.

---

### Phase 4 — Photos & richer memories

**Goal:** Attach photos to a memory.

**You build (Pair mode):**
- File picker, validation (type, size), storage of the file + a reference in the DB.

**Learn:** File I/O, input validation, why you store a *path/reference* not the blob (usually).

**Design pattern to try:** **Builder** — a `Memory` with optional text, optional audio,
optional photos is a textbook Builder case. Compare it to a 6‑argument constructor and feel
the difference.

**Testing focus:** Validation logic (reject the 2GB file, the `.exe` masquerading as a photo).

**Definition of done:** A memory can carry photos, and bad input is rejected gracefully.

---

### Phase 5 — Search & review *(keyword first, RAG optional later)*

**Goal:** Find and re‑read past memories.

**You build:**
- **Start simple:** keyword search (SQL `LIKE` or SQLite full‑text search). Ship this first.
- **Then optionally** upgrade to semantic search / RAG with local embeddings (that's Phase 7D,
  after you've learned the AI fundamentals in Phase 6).

**Learn:** Why you build the simple version first and only reach for RAG if keyword search
genuinely isn't enough. (Great habit: **don't add ML where a `WHERE` clause will do.**)

**Design pattern to try:** **Strategy** again — a `SearchStrategy` interface with
`KeywordSearch` and (later) `SemanticSearch`. This sets up the RAG work cleanly.

**Testing focus:** Search returns the right memories; ranking behaves.

**Definition of done:** You can find a memory from three weeks ago in a couple seconds.

---

### Phase 6 — AI Fundamentals Lab *(learn the machinery before you build features with it)*

**Goal:** Before you wire AI into NoteCapsule, spend one focused phase learning how to
program *against* an AI model at all. This is the phase that turns "I used an AI API once"
into "I understand how to build reliable software on top of a non-deterministic component" —
which is the real engineering skill behind the buzzword "agentic AI development."

This is a **lab**, not a feature. Build these as small, standalone exercises in an `ailab`
package **inside this repo** (not a throwaway repo) — so the work goes through the normal
PR/CI/template flow and stays as visible learning evidence. Do them with AI in **Tutor** mode —
you want to understand every piece, because everything in Phase 7 is built on it.

**You build (small exercises, each its own tiny PR):**
1. **Call one model and get text back.** Local via Ollama, or a cloud API — your pick from
   the questionnaire. Just prompt → response, printed to the console.
2. **Structured output.** Make the model return JSON (e.g. `{ "suggestedTags": [...] }`) and
   parse it into a Java object. Learn how *often* models return almost-but-not-quite-valid
   JSON, and how you defend against it.
3. **Reliability engineering.** Add a timeout, a retry with backoff, and a fallback for when
   the model is down or slow. **This is the part tutorials skip and real jobs require.**
4. **Observe it.** Log latency, token usage (if applicable), and success/failure for each
   call. This is your first taste of **telemetry** — you can't improve what you can't see.
5. **Test it without the model.** Put the model behind an interface and write unit tests
   against a *fake* that returns canned responses (including a malformed one and a timeout).

**Learn:**
- **Prompt engineering fundamentals** — how phrasing, examples, and structure change output.
- **Non-determinism as an engineering problem** — the mental shift from "a function returns
  the same thing every time" to "a component returns *something plausible* every time, and my
  job is to make the surrounding system robust to that." This shift is the whole game.
- Structured output, retries/timeouts/fallbacks, and basic observability.
- Handling secrets (API keys) **without committing them** — env vars / a `.gitignore`'d config.

**Design pattern to try:** **Adapter** — wrap the model behind a clean `LanguageModel`
interface now, so Phase 7 can swap local ↔ cloud freely and test against a fake.

**Definition of done:** A tiny program that reliably asks a model for structured data,
survives the model being slow or wrong, logs what happened, and is covered by tests that
never touch a real model. You now understand the tool before you build the product with it.

---

### Phase 7 — Opt‑in AI features *(the payoff — build the agent, staged, all off by default)*

**Goal:** The AI features from the README, **all off by default** and toggleable by the user,
built on the fundamentals from Phase 6. This is a big area, so it's **broken into small,
independently shippable sub-phases** — do them in order, each is its own PR (or few).

**The rule that never changes:** every AI feature is behind a user toggle and defaults to
**off**. Someone who never turns any of it on has an app that works exactly like the end of
Phase 3. Designing for "off" is a real skill — practice it here.

#### 7A — Transcription adapter (voice-to-text)
The gentlest on-ramp. Whatever you picked (local whisper.cpp / Vosk for offline+free, or a
cloud API like Grok STT), hide it behind a `TranscriptionService` interface — exactly like the
`LanguageModel` adapter from Phase 6. `LocalWhisperTranscriber` and `GrokTranscriber` become
swappable implementations. **Ramp it fake-first, like Phase 1's email:** start with a
`FakeTranscriber` returning canned text, then transcribe a *saved audio file*, and only then
wire up live microphone capture — desktop audio + STT SDKs are fiddly and deserve their own
step. **Done when:** you speak a memory and it appears as text; tests cover your logic with a
fake transcriber.

#### 7B — Prompted memory polish
Optional AI assist: "clean up / expand this memory." Pure application of Phase 6's structured-
output + reliability work. **Done when:** a user can opt in to polish a draft, the original is
never lost, and it degrades gracefully when the model is unavailable.

#### 7C — Tool-calling agent *(the heart of "agentic AI")*
This is where "agent workflow" stops being a buzzword and becomes real code. Build **one
concrete, well-scoped agent workflow**:

> **The draft-assistant agent:** given a draft memory, the agent may call *tools* you expose —
> `getRecentMemories()`, `getRecipientPreferences()`, `suggestTags()` — and then produce an
> approval-ready email draft. **It may never send the email itself.** Sending always requires
> the explicit human "Approve & Send" from Phase 1.

That last sentence is the most important design decision in the whole project: **the agent
proposes, the human disposes.** Human-in-the-loop for any irreversible action (like emailing a
loved one) is exactly the kind of judgment that separates careful AI development from careless
AI development. Ask your
AI tutor to explain the tool-calling loop (model asks to call a tool → your code runs it →
result goes back to the model), then *write that loop yourself* — it's the core of agentic dev.
**Start with a one-tool warm-up:** a fake model requests one fake tool, your dispatcher runs it
and feeds the result back, all unit-tested — get *that* loop green before building the
three-tool draft assistant. **Done when:** the agent produces a good draft using at least two
tools, cannot take any irreversible action on its own, and its tool-dispatch logic is
unit-tested with a faked model.

#### 7D — RAG search (semantic memory retrieval)
Now, if keyword search from Phase 5 isn't enough, upgrade the `SemanticSearch` strategy: local
embeddings + a small vector store, retrieving relevant past memories. Bonus: those same
retrieval tools can feed 7C's agent ("what did I write about her birthday last year?").
**Done when:** a semantic query finds memories keyword search would miss, behind the same
`SearchStrategy` interface.

#### 7E — Telemetry & simple evals
Graduate Phase 6's per-call logging into a small on-device view: feature usage, latency,
failure rate. Then the senior move — a tiny **eval**: a handful of saved inputs with expected
qualities, so when you change a prompt you can tell whether you made things better or worse
instead of guessing. **Done when:** you can answer "is my new prompt better than the old one?"
with data, not vibes.

**Design patterns across Phase 7:**
- **Adapter** — every provider's SDK behind your own clean interface (from Phase 6).
- **Strategy** — user picks local vs cloud, keyword vs semantic, at runtime.
- **Facade** (optional) — one `AiFeatures` entry point so the UI has a simple surface.
- **Feature flags** — everything AI is behind an on/off switch, default off.

**Testing focus (all of Phase 7):** Test *your* logic — adapter mapping, the tool-dispatch
loop, toggle behavior — with the model/transcriber **mocked**. Never hit a real model in unit
tests. This is why Phase 6 made you build against a fake first.

**Definition of done (phase):** At least one AI feature ships behind a toggle, and — if agentic
AI is one of your learning goals — 7C's tool-calling agent loop is implemented. Any sub-phase
you skip is *explicitly deferred* (write down what you cut and why), not silently dropped. A
user who opts into none of it still has the exact Phase 3 app. **Don't let Phase 7 quietly
become a second giant capstone** — ship one AI feature well, then decide what's next.

---

### Phase 8 — Packaging, secrets, and distribution *(make it a real app someone installs)*

**Goal:** Ship a Linux desktop artifact you can double‑click.

**You build:**
- Package with **`jpackage`** into a Linux artifact (AppImage / `.deb`).
- Proper config & secrets management (email credentials, any API keys) — none of it in Git.
- A real `README` "Getting Started" and "Getting the app" section (replace the "Coming soon!"
  placeholders).

**Learn:** How Java apps get distributed, the difference between "runs on my machine" and
"installs on someone else's," where config lives on Linux.

**Cross‑platform note (your call):** If you want, use this phase to also produce a Windows/Mac
build. Not required — but most workplaces run Windows/Mac, so it's worth *trying* cross‑platform
at least once so it's not scary later.

**Definition of done:** You hand someone the installer, they run it, and it just works.

---

### Phase 9 — Stretch & future *(from the README's "Future")*

Pick what excites you:
- **Docker** — containerize any server‑side pieces; learn the tooling.
- **Cloud sync / backup** of memories.
- **Mobile companion.**
- Deeper **agent workflows** and **telemetry** dashboards.

By now you'll be choosing these yourself and scoping them like an engineer, which is the
real graduation.

---

## Design patterns you'll have touched (a checklist to be proud of)

By the end you'll have used, *in real code you wrote and can explain*:

- [ ] Dependency Injection / IoC (throughout)
- [ ] Layered architecture (UI / service / repository / domain)
- [ ] Repository pattern (Phases 1–2)
- [ ] Service layer (Phase 1)
- [ ] Strategy (notifications, search, AI provider)
- [ ] Observer (optional, Phase 3)
- [ ] Builder (Phase 4)
- [ ] Adapter (Phases 6–7)
- [ ] Facade (optional, Phase 7)
- [ ] Feature flags (Phase 7)
- [ ] Clock injection / testable time (Phase 3) — the sleeper hit
- [ ] Tool-calling / agent loop with human-in-the-loop approval (Phase 7C) — the one to be proudest of

Don't force a pattern where it doesn't fit — that's its own anti‑pattern. But when one *does*
fit, name it out loud in your PR ("used Strategy here because…"). Naming your decisions is
what makes you sound senior.

---

## The review loop

1. You pick up the next phase (or a slice of it) and open a branch.
2. You use AI in the right mode (Tutor/Pair/Review — not Ghostwriter).
3. You open a small PR with a real description and passing CI.
4. The automated reviewer comments; you address it.
5. A reviewer goes through it; you discuss; you revise.
6. Merge. Repeat.

The measure of success isn't "the app is done." It's that at any point, **you can explain
every line you merged.** Build that way and the app *and* the engineer both ship.
