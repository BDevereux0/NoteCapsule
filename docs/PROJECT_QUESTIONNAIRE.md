# NoteCapsule — Project Questionnaire

This is a **guided scoping document**. You (the student) don't need to know all the answers
up front — that's the point. You're going to fill it out **with an AI's help**, and by the
end you'll have made every important decision about NoteCapsule *on purpose* instead of by
accident.

---

## How to use this document

1. Open your AI CLI (Codex or Claude) **in this repository** so it can see the README and
   the [build plan](./BUILD_PLAN.md).
2. Paste the **prompt below** to kick things off.
3. Answer the questions **in the chat.** The AI will explain anything you don't understand,
   push back on vague answers, and help you decide.
4. As you settle each answer, **write it into this file** under the question (fill the
   `**Answer:**` blanks). Commit it. This becomes the source of truth the build plan assumes.

> Filling this out *is* the first real task of the project. Take it seriously and it'll save
> you from expensive rewrites later.

### 📋 Prompt to paste into Codex / Claude

> You are helping me, a computer‑science student, scope a personal project called
> **NoteCapsule** (a desktop app for capturing daily memories about loved ones and emailing
> them). Read the `README.md` and `docs/BUILD_PLAN.md` in this repo for context.
>
> Walk me through the questionnaire in `docs/PROJECT_QUESTIONNAIRE.md` **one section at a
> time.** For each question:
> - Ask me the question in plain language.
> - If I seem unsure or give a vague answer, **explain the concept and the trade‑offs**
>   before I decide — teach me, don't just accept "I don't know."
> - Where there's a sensible default for a solo learning project, **recommend one and say
>   why**, but let me make the final call.
> - When I land on an answer, restate it crisply so I can paste it into the doc.
>
> Go slowly. Prioritize me *understanding* each decision over finishing fast. Start with
> Section 1.

---

## Section 1 — Vision & scope

**1.1 In one sentence, what does NoteCapsule do, and who is it for?**
*Why it matters:* If you can't say it in a sentence, the scope is still fuzzy.
**Answer:** _____

**1.2 What is the smallest version you'd be proud to use yourself (your MVP)?**
*Hint:* The build plan's "walking skeleton" is: type a memory → approve it → it emails. Is
that your MVP, or does yours need more to feel real?
**Answer:** _____

**1.3 What is explicitly _out_ of scope for v1?**
*Why it matters:* Naming what you're *not* building is how projects actually finish.
**Answer:** _____

---

## Section 2 — The core user flow

**2.1 Walk through a normal day of using NoteCapsule, step by step.**
*Hint:* App reminds you at 8pm → you open it → you type or speak a memory → you attach a
photo → you approve → it emails. Write *your* version.
**Answer:** _____

**2.2 Who receives the emails, and does the user pick the recipient per memory or set it once?**
**Answer:** _____

**2.3 What's the approval step for — catching mistakes, privacy, both?** *(This affects the UI.)*
**Answer:** _____

---

## Section 3 — Tech stack

> The build plan assumes **Java 21 + Spring Boot + JavaFX + SQLite + Gradle**. These
> questions confirm that's really what you want.

**3.1 Language & framework: sticking with Java + Spring Boot?**
*Ask the AI:* what does Spring Boot give a desktop app, and is it overkill here? (It's a
great learning vehicle for dependency injection either way.)
**Answer:** _____

**3.2 Desktop UI toolkit: JavaFX, Swing, or something else?**
*Hint:* JavaFX is the modern default for Java desktop UIs. Have the AI compare JavaFX vs Swing.
**Answer:** _____

**3.3 How will Spring (the backend "brain") and JavaFX (the UI) fit together?**
*Why it matters:* This is the one genuinely tricky integration in the stack. Ask the AI to
explain the common pattern (Spring manages beans/services; JavaFX controllers pull them in).
**Answer:** _____

**3.4 Build tool: Gradle or Maven?** *(Either is fine — pick one and learn it well.)*
**Answer:** _____

**3.5 Local storage: SQLite? Plain files? Something else?**
*Hint:* SQLite is a single local file, zero server, perfect for a desktop app.
**Answer:** _____

---

## Section 4 — Platforms & distribution

**4.1 Which OS must v1 run on?** *(You said Linux — confirm.)*
**Answer:** _____

**4.2 Do you want cross‑platform (Windows/Mac) now, later, or never?**
*Context:* Most workplaces are Windows/Mac, so it's worth trying at least once — but not urgent.
**Answer:** _____

**4.3 How will people install it?** *(e.g. AppImage, `.deb`, a run script — see Phase 8.)*
**Answer:** _____

---

## Section 5 — Data & privacy

**5.1 What data does a memory contain?** *(text, audio, photos, timestamp, recipient, tags…?)*
**Answer:** _____

**5.2 These are personal family memories. Should everything stay **local‑only** by default?**
*Why it matters:* This drives your AI choices in Section 7 (local models vs cloud APIs) and
whether you ever add cloud sync.
**Answer:** _____

**5.3 Do you need backups, and where would they live?**
**Answer:** _____

---

## Section 6 — Email & notifications

**6.1 How are emails sent — SMTP via Gmail, a service like Mailtrap for dev, something else?**
*Hint:* Use a fake/dev inbox while building so you don't spam a real person. Ask the AI about
Gmail app passwords vs. an SMTP testing service.
**Answer:** _____

**6.2 Where do the email credentials live so they're **never committed to Git**?**
**Answer:** _____

**6.3 Notifications for an overdue memory: Discord webhook, desktop notification, email, or several?**
*Hint:* The build plan uses the **Strategy pattern** so you can support more than one.
**Answer:** _____

**6.4 What's the "overdue" threshold and can the user change it?** *(README says ~30 min.)*
**Answer:** _____

---

## Section 7 — AI features (all opt‑in, off by default)

> Your README lists voice‑to‑text, RAG search, prompt engineering, tool calling, agent
> workflows, and telemetry. You've been playing with **Ollama and local models**, and cloud
> options like **Grok's speech‑to‑text API** exist too. This section is about choosing
> deliberately. **Every AI feature must be toggleable and off by default.**

**7.1 Voice‑to‑text: local (whisper.cpp / Vosk) or a cloud API (e.g. Grok STT)?**
*Trade‑offs to talk through with the AI:*
- **Local:** free, fully offline, private (great for family memories), but heavier setup and
  depends on your machine's power.
- **Cloud:** easy, high quality, but costs money, needs internet, and sends audio off‑device.
*Design note:* Per the build plan, hide whichever you pick behind a `TranscriptionService`
interface so you can switch — or support both.
**Answer:** _____

**7.2 Text AI (polish a memory, summarize, etc.): local via Ollama, a cloud model, or none for v1?**
**Answer:** _____

**7.3 Search: is keyword search enough, or do you want semantic/RAG search?**
*Hint:* The build plan says ship keyword search first and only add RAG if you truly need it.
Ask the AI when RAG is actually worth it.
**Answer:** _____

**7.4 If you do RAG: local embeddings + local vector store, or a hosted one?**
*(Given 5.2, local likely fits the privacy goal — talk it through.)*
**Answer:** _____

**7.5 What telemetry do you want, and does it stay **on‑device**?** *(feature usage, latency…)*
**Answer:** _____

### Agent boundaries & safety *(the handoff from "product idea" to "agent architecture")*

> These questions turn "add an AI agent" into a set of real design decisions. If you're
> building the draft-assistant agent from Phase 7C, answering these *is* the design. This is
> exactly the kind of thinking that separates a thoughtful AI feature from a risky one.

**7.6 What tools/actions is the AI allowed to call?** *(read recent memories? suggest tags?
draft an email? nothing else?)*
**Answer:** _____

**7.7 Which actions ALWAYS require an explicit human approval and can never be automated?**
*Strong recommendation for this app: sending an email to a loved one is irreversible — the
agent may draft, but only a human clicks send. Talk through why with the AI.*
**Answer:** _____

**7.8 What data is the AI allowed to see, and is any of it allowed to leave the machine?**
*(Ties back to 5.2 — personal family memories. A local model keeps everything on-device.)*
**Answer:** _____

**7.9 When the AI fails, is slow, or returns garbage, what should happen?**
*Hint: it must **degrade gracefully** — the app still works with AI off. Never let a flaky
model break the core "write and email a memory" flow.*
**Answer:** _____

**7.10 Which AI features are explicitly *not* in your first AI release?**
*Why it matters: Section 7 lists a lot. Deferring most of it is a decision, not a failure —
name what waits for later so Phase 7 ships one good feature instead of sprawling into a second
giant capstone. (See the Phase 7 "definition of done" in the build plan.)*
**Answer:** _____

---

## Section 8 — Engineering practices

**8.1 Git workflow: confirm branch → PR → review → merge, no direct commits to `main`.**
**Answer:** _____

**8.2 Testing: comfortable committing to unit tests from Phase 1 and a test‑first (TDD) phase?**
*(The build plan makes scheduling/overdue logic the TDD phase — a great first TDD experience.)*
**Answer:** _____

**8.3 CI: GitHub Actions to build + test every PR — agreed?**
**Answer:** _____

**8.4 Automated code review: which tool?** *(CodeRabbit, GitHub Copilot review, Claude‑based…)*
Ask the AI to compare the free options for a solo repo.
**Answer:** _____

---

## Section 9 — Definition of done & success

**9.1 How will you know v1 is "done"?** *(Concrete: "I use it daily for a week and it never fails.")*
**Answer:** _____

**9.2 What do you personally most want to have *learned* by the end?**
*Why it matters:* This is a learning project first. Naming the learning goal keeps you honest
about using AI as a tutor, not a ghostwriter.
**Answer:** _____

**9.3 Which phase are you most nervous about, and what's the first small step to de‑risk it?**
**Answer:** _____

### Proving the learning *(understanding it, not just shipping it)*

**9.4 For each big topic (Spring/DI, testing & TDD, CI, the agent loop), how will you *prove*
you learned it — not just that the code works?**
*Hint: the PR template's "What I can explain" field is your running evidence. A topic you
can't explain in a review, you haven't learned yet.*
**Answer:** _____

**9.5 What concepts should you be able to explain clearly from memory, without notes?**
*(e.g. "why dependency injection makes code testable," "the tool-calling loop," "red-green-
refactor.") Name them now; they're your study list.*
**Answer:** _____

**9.6 When AI hands you code you don't understand, what's your rule?**
*Recommendation: you don't merge it. Drop to Tutor mode and understand it first, or write it
yourself. Decide your personal rule now, before you're tired and tempted.*
**Answer:** _____

---

*When every `**Answer:**` above is filled in, commit this file and start Phase 0 in the
[build plan](./BUILD_PLAN.md). You're no longer guessing — you're building.*
