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
**Answer:** NoteCapsule is a web-based application that lets you write or use voice-to-text to record a memory, then approve and send it to an email address of your choice.

**1.2 What is the smallest version you'd be proud to use yourself (your MVP)?**
*Hint:* The build plan's "walking skeleton" is: type a memory → approve it → it emails. Is
that your MVP, or does yours need more to feel real?
**Answer:** The MVP is a Discord notification containing a private expiring link that opens a memory form, lets me write a memory, review and approve it, and then send it by email. Full user authentication is out of the MVP but planned for the finished product.

**1.3 What is explicitly _out_ of scope for v1?**
*Why it matters:* Naming what you're *not* building is how projects actually finish.
**Answer:** Full authentication, RAG/semantic search, voice-to-text, AI memory features, agent workflows, photo attachments, mobile app, public hosting, multi-user support, and automatic saving files to Desktop are out of scope for v1. V1 is single-user only. Basic download/export support may be added later.

---

## Section 2 — The core user flow

**2.1 Walk through a normal day of using NoteCapsule, step by step.**
*Hint:* App reminds you at 8pm → you open it → you type or speak a memory → you attach a
photo → you approve → it emails. Write *your* version.
**Answer:** The user sets a reminder time in the app. At that time, NoteCapsule sends a Discord notification with a private expiring link. The user clicks the link from a browser that can reach the local app, types the memory, reviews and approves the message, and then the backend sends the email. Mobile link support is not required for v1.

**2.2 Who receives the emails, and does the user pick the recipient per memory or set it once?**
**Answer:** The recipient is the loved one the memory is meant for. The user presets the recipient email address in a settings page, and approved memories are sent to that saved address.

**2.3 What's the approval step for — catching mistakes, privacy, both?** *(This affects the UI.)*
**Answer:** The approval step is for double-checking the memory content and confirming it is going to the intended recipient before the email is sent. Later, when voice-to-text is enabled, approval will also verify transcription accuracy. When image support is added, approval will include checking that images are appropriate, correctly sized, and placed well.

---

## Section 3 — Tech stack

> The build plan assumes **Java 21 + Spring Boot + JavaFX + SQLite + Gradle**. These
> questions confirm that's really what you want.

**3.1 Language & framework: sticking with Java + Spring Boot?**
*Ask the AI:* what does Spring Boot give a desktop app, and is it overkill here? (It's a
great learning vehicle for dependency injection either way.)
**Answer:** Yes. The backend will use Java 21 with Spring Boot. Spring Boot will provide the REST API, dependency injection, configuration, scheduling, email services, persistence integration, and testing support for the React frontend.

**3.2 Desktop UI toolkit: JavaFX, Swing, or something else?**
*Hint:* JavaFX is the modern default for Java desktop UIs. Have the AI compare JavaFX vs Swing.
**Answer:** The frontend will use React with TypeScript instead of JavaFX or Swing. I will build the React components, HTML structure, and CSS by hand without a CSS framework such as Tailwind, Bootstrap, or Material UI.

**3.3 How will Spring (the backend "brain") and JavaFX (the UI) fit together?**
*Why it matters:* This is the one genuinely tricky integration in the stack. Ask the AI to
explain the common pattern (Spring manages beans/services; JavaFX controllers pull them in).
**Answer:** Spring Boot and React will run as separate servers. Spring Boot will expose REST APIs for memories, settings, reminders, tokens, and email sending. React will call the Spring Boot API directly using JSON over HTTP. CORS will be configured in Spring Boot so the React frontend can access the backend during development. Later, production/local deployment can be revisited, but v1 will keep the frontend and backend separate.

**3.4 Build tool: Gradle or Maven?** *(Either is fine — pick one and learn it well.)*
**Answer:** The Spring Boot backend will use Maven because I have used it before. The React frontend will use Node.js with npm and Vite for package management, local development, and frontend builds.

**3.5 Local storage: SQLite? Plain files? Something else?**
*Hint:* SQLite is a single local file, zero server, perfect for a desktop app.
**Answer:** The app will use a local MySQL database for storage because I am familiar with MySQL and can inspect/manage it with DBeaver. Spring Boot will store memories, settings, reminder schedules, magic-link tokens, recipient configuration, and email send status in MySQL. Database credentials will be kept out of Git.

---

## Section 4 — Platforms & distribution

**4.1 Which OS must v1 run on?** *(You said Linux — confirm.)*
**Answer:** V1 will run on my Linux computer.

**4.2 Do you want cross‑platform (Windows/Mac) now, later, or never?**
*Context:* Most workplaces are Windows/Mac, so it's worth trying at least once — but not urgent.
**Answer:** Later versions may support Windows and macOS.

**4.3 How will people install it?** *(e.g. AppImage, `.deb`, a run script — see Phase 8.)*
**Answer:** NoteCapsule will be installed and run locally using Docker Compose.

---

## Section 5 — Data & privacy

**5.1 What data does a memory contain?** *(text, audio, photos, timestamp, recipient, tags…?)*
**Answer:** A v1 memory contains text, with its date, time, recipient, approval status, and email delivery status stored as metadata.

**5.2 These are personal family memories. Should everything stay **local‑only** by default?**
*Why it matters:* This drives your AI choices in Section 7 (local models vs cloud APIs) and
whether you ever add cloud sync.
**Answer:** Memory content will not be permanently stored by default. The user may opt to save a memory in NoteCapsule’s configured local folder; otherwise, its content is deleted after sending.

**5.3 Do you need backups, and where would they live?**
**Answer:** NoteCapsule will not provide automatic backups in v1. Users who download memories are responsible for backing up those files using their preferred system.

---

## Section 6 — Email & notifications

**6.1 How are emails sent — SMTP via Gmail, a service like Mailtrap for dev, something else?**
*Hint:* Use a fake/dev inbox while building so you don't spam a real person. Ask the AI about
Gmail app passwords vs. an SMTP testing service.
**Answer:** V1 will use Mailpit to capture emails locally during development and Gmail SMTP with an app password to deliver approved memories to actual recipient addresses.

**6.2 Where do the email credentials live so they're **never committed to Git**?**
**Answer:** V1 credentials will be supplied through Docker Compose secrets backed by files excluded from Git. When deployed to AWS, credentials will move to AWS Secrets Manager.

**6.3 Notifications for an overdue memory: Discord webhook, desktop notification, email, or several?**
*Hint:* The build plan uses the **Strategy pattern** so you can support more than one.
**Answer:** V1 will support overdue notifications through a Discord webhook, email, or both, based on the user’s settings. Email reminders will be sent to the user, not the memory recipient.

**6.4 What's the "overdue" threshold and can the user change it?** *(README says ~30 min.)*
**Answer:** A memory becomes overdue 30 minutes after its scheduled reminder by default. The user can change this threshold in settings.

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
**Answer:** AI features will be limited to the local version. Voice-to-text will use the original OpenAI Whisper model through a local Python service. Spring Boot will send recorded audio to the service and return the transcript for user review and approval.

**7.2 Text AI (polish a memory, summarize, etc.): local via Ollama, a cloud model, or none for v1?**
**Answer:** V1 will not include text AI. A later local version will use a Python service connected to Ollama’s `llama3.1:8b` model to analyze grammar and tone and suggest improvements. Suggestions will never change the memory automatically and must be accepted by the user.

**7.3 Search: is keyword search enough, or do you want semantic/RAG search?**
*Hint:* The build plan says ship keyword search first and only add RAG if you truly need it.
Ask the AI when RAG is actually worth it.
**Answer:** V1 will provide keyword search across memories the user has opted to save in NoteCapsule’s configured local folder. A later local edition will add RAG so users can search semantically and chat with Llama about their saved memories.

**7.4 If you do RAG: local embeddings + local vector store, or a hosted one?**
*(Given 5.2, local likely fits the privacy goal — talk it through.)*
**Answer:** The local AI edition will use local embeddings and a local vector store. Saved memory text and embeddings will not be sent to a hosted AI or vector database.

**7.5 What telemetry do you want, and does it stay **on‑device**?** *(feature usage, latency…)*
**Answer:** The local AI edition will keep on-device traces of agent runs, including model and prompt versions, tool calls, RAG retrieval metadata, approval outcomes, user acceptance or rejection, latency, token usage, errors, and retries. Routine telemetry will exclude memory text, audio, transcripts, prompts, and model responses, and sensitive debug tracing will require explicit opt-in.

### Agent boundaries & safety *(the handoff from "product idea" to "agent architecture")*

> These questions turn "add an AI agent" into a set of real design decisions. If you're
> building the draft-assistant agent from Phase 7C, answering these *is* the design. This is
> exactly the kind of thinking that separates a thoughtful AI feature from a risky one.

**7.6 What tools/actions is the AI allowed to call?** *(read recent memories? suggest tags?
draft an email? nothing else?)*
**Answer:** The local agent may search and read opted-in saved memories, analyze grammar and tone, suggest revisions or drafts, group and count memory metadata, create timelines and graphs, and cite its source memories. It may not send emails, delete or alter memories, change recipients, or modify settings.

**7.7 Which actions ALWAYS require an explicit human approval and can never be automated?**
*Strong recommendation for this app: sending an email to a loved one is irreversible — the
agent may draft, but only a human clicks send. Talk through why with the AI.*
**Answer:** Sending an email, changing application settings, and modifying or deleting a saved memory always require explicit human approval and can never be automated by the agent.

**7.8 What data is the AI allowed to see, and is any of it allowed to leave the machine?**
*(Ties back to 5.2 — personal family memories. A local model keeps everything on-device.)*
**Answer:** Local AI may access the current draft or recorded audio when the user invokes an AI feature, opted-in saved memories, and the minimum metadata required for the request. AI data will remain on-device, and the agent may not scan unrelated files or access other data.

**7.9 When the AI fails, is slow, or returns garbage, what should happen?**
*Hint: it must **degrade gracefully** — the app still works with AI off. Never let a flaky
model break the core "write and email a memory" flow.*
**Answer:** The user may interrupt an AI process at any time. Input will be saved temporarily so it is not lost during a failure, and temporary files will be cleaned up after recovery, cancellation, or a successful retry. The app will explain why the operation failed when known and allow the user to try again. AI failures will not block the core workflow.

**7.10 Which AI features are explicitly *not* in your first AI release?**
*Why it matters: Section 7 lists a lot. Deferring most of it is a decision, not a failure —
name what waits for later so Phase 7 ships one good feature instead of sprawling into a second
giant capstone. (See the Phase 7 "definition of done" in the build plan.)*
**Answer:** The first local AI release will include only voice-to-text through the Python Whisper service. Llama tone and grammar suggestions, RAG, semantic search, memory chat, agent tool calling, timelines, and graphs will wait for later releases.

---

## Section 8 — Engineering practices

**8.1 Git workflow: confirm branch → PR → review → merge, no direct commits to `main`.**
**Answer:** Every change will be made on a feature branch and submitted through a pull request. The change will be reviewed and tested, and it may be merged only after all required checks pass. No changes will be committed directly to `main`.

**8.2 Testing: comfortable committing to unit tests from Phase 1 and a test‑first (TDD) phase?**
*(The build plan makes scheduling/overdue logic the TDD phase — a great first TDD experience.)*
**Answer:** Unit testing will begin in Phase 1. Because TDD is new to me, I will learn it through small, guided red-green-refactor cycles. Scheduling and overdue-reminder logic will be developed test-first, and I will make sure I understand what each test proves rather than adding tests only to increase coverage.

**8.3 CI: GitHub Actions to build + test every PR — agreed?**
**Answer:** GitHub Actions will build and test every pull request. Changes may be merged only after the required CI checks pass. The workflow will be expanded incrementally to validate both the Spring Boot backend and React frontend as they are developed.

**8.4 Automated code review: which tool?** *(CodeRabbit, GitHub Copilot review, Claude‑based…)*
Ask the AI to compare the free options for a solo repo.
**Answer:** GitHub Copilot will provide automated pull-request reviews. I will apply for GitHub Education student verification to obtain Copilot Student access. Until that access is active, I will review pull requests manually and rely on GitHub Actions checks. Copilot feedback will be treated as advice, and I will verify and understand its suggestions before making changes.

---

## Section 9 — Definition of done & success

**9.1 How will you know v1 is "done"?** *(Concrete: "I use it daily for a week and it never fails.")*
**Answer:** V1 is done when it reliably completes the full MVP flow defined in Section 1.2: a Discord notification provides a private expiring link that opens the memory form, allows me to write, review, and approve a memory, and then sends it by email. I must successfully use this flow for seven consecutive days without a critical failure, lost draft, or incorrectly delivered email.

**9.2 What do you personally most want to have *learned* by the end?**
*Why it matters:* This is a learning project first. Naming the learning goal keeps you honest
about using AI as a tutor, not a ghostwriter.
**Answer:** For v1, I want to improve my software-engineering practices rather than only learn how to connect Spring Boot and React, since I have built projects with that stack before. I want hands-on experience with exception handling, input validation, Docker, TDD, CI, AI-assisted code review, and the other engineering tools selected in this questionnaire.

I also want reviews to identify architectural, testing, security, reliability, and maintainability blind spots.

V2 will have two editions. A non-AI edition will be hosted on AWS for a day or two so I can learn cloud deployment. A separate local-only edition will contain the AI features described in Section 7.

**9.3 Which phase are you most nervous about, and what's the first small step to de‑risk it?**
**Answer:** Phases 6 and 7 will be the most challenging because there is a great deal to learn and implement. Before beginning them, I will find GitHub repositories with similar features and study how they structure and test their implementations. I will use these repositories as learning references, verify their licenses before adapting code, and avoid copying anything I do not understand.

TDD will also be challenging because it is new to me. Before applying it to NoteCapsule, I will work with ChatGPT to build a simple calculator application using red-green-refactor cycles. This exercise will help me understand the TDD workflow before I use it for NoteCapsule's scheduling and overdue-reminder logic.

### Proving the learning *(understanding it, not just shipping it)*

**9.4 For each big topic (Spring/DI, testing & TDD, CI, the agent loop), how will you *prove*
you learned it — not just that the code works?**
*Hint: the PR template's "What I can explain" field is your running evidence. A topic you
can't explain in a review, you haven't learned yet.*
**Answer:** I will use the PR template's "What I can explain" section as a running record of my learning. My main proof of understanding will be explaining each major concept in my own words through a concrete example or analogy, followed by showing where that concept appears in NoteCapsule's code.

For example, I can explain vectors and matrix multiplication using a 2D video game: the available actions include jumping and moving along the x-axis, the input vector represents the player's commands, and the result describes where the sprite ends up. I will apply the same approach to Spring and dependency injection, testing and TDD, CI, and the agent loop.

Before merging work involving a major topic, I should be able to explain what the code does, why it was designed that way, how it was tested, and where the limits of my analogy are without relying on AI-generated wording.

**9.5 What concepts should you be able to explain clearly from memory, without notes?**
*(e.g. "why dependency injection makes code testable," "the tool-calling loop," "red-green-
refactor.") Name them now; they're your study list.*
**Answer:** I should be able to explain the complete NoteCapsule flow from a React user action through the Spring Boot API, application logic, MySQL persistence, and external services. On the React side, I should understand components, props, state, hooks, forms, validation, API requests, TypeScript types, error handling, and how frontend responsibilities are separated.

On the backend, I should understand dependency injection and inversion of control; layered architecture; controllers, services, repositories, and domain objects; REST APIs; input validation; exception handling; persistence; secure expiring tokens; scheduling; and testable time.

I should be able to explain the red-green-refactor TDD cycle, unit and integration testing, mocking and fakes, Git branches and pull requests, CI checks, Docker images and containers, Docker Compose, volumes, networks, configuration, and secrets.

I should understand how SMTP email delivery works, how Mailpit captures development emails, how Gmail SMTP delivers real emails, and how the application handles and records delivery failures.

I should be able to recognize and explain the design patterns used in the project: dependency injection and IoC, layered architecture, Repository, Service Layer, Strategy, Observer when appropriate, Builder, Adapter, Facade when appropriate, feature flags, clock injection, and the tool-calling agent loop with human approval. I should also be able to explain why each pattern fits its use case and why patterns should not be forced where they do not add value.

For the later local AI edition, I should understand model adapters, structured output, timeouts, retries, fallbacks, local voice transcription, embeddings, vector search, RAG, source citations, telemetry, and the boundaries that prevent an agent from taking irreversible actions without human approval.

**9.6 When AI hands you code you don't understand, what's your rule?**
*Recommendation: you don't merge it. Drop to Tutor mode and understand it first, or write it
yourself. Decide your personal rule now, before you're tired and tempted.*
**Answer:** I will write the code myself and use AI when I encounter a problem, treating it like a faster, interactive version of Stack Overflow. I will ask AI to explain the problem, present multiple possible solutions, and compare their tradeoffs rather than asking it to complete the project for me.

I will choose the approach and implement or adapt it myself. If AI provides code I do not understand, I will not commit or merge it. I will ask questions, reduce it to smaller pieces, test it, and make sure I can explain what it does, why it works, and how it can fail before accepting it.

---

*When every `**Answer:**` above is filled in, commit this file and start Phase 0 in the
[build plan](./BUILD_PLAN.md). You're no longer guessing — you're building.*
