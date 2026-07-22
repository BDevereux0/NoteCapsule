# NoteCapsule — From Questionnaire to a Guided PRD Skill

You finished the [project questionnaire](./PROJECT_QUESTIONNAIRE.md). The next step is turning those decisions into a **Product Requirements Document (PRD)**.

Rather than asking Codex to write the PRD in one pass, this guide shows you how to create a Codex skill that will:

- explain each part of a PRD;
- ask you questions about the product;
- identify missing or conflicting decisions;
- draft only from answers you confirm; and
- help you understand and review the result before writing `docs/PRD.md`.

The goal is both a useful PRD and a better understanding of how product requirements are created.

---

## 1. What is a PRD?

A **Product Requirements Document** describes:

- what you are building;
- who it is for;
- why it matters;
- how it should behave from the user's point of view; and
- what counts as done.

It serves a different purpose from the other planning documents in this repository:

| Document | Purpose |
|----------|---------|
| **Questionnaire** | Helps you make and record product decisions. |
| **PRD** | Turns those decisions into clear, buildable requirements. |
| **Build plan / technical design** | Explains how you will implement the product. |
| **Tickets / PR slices** | Breaks requirements into small units of work. |

### Why NoteCapsule should have one

A PRD provides:

1. **Scope control** — It records what belongs in the current release and what waits.
2. **Shared context** — It keeps important decisions available after a conversation ends.
3. **AI guardrails** — It gives Codex a defined product boundary during implementation.
4. **Reviewable reasoning** — It exposes unclear behavior before that uncertainty becomes code.
5. **Acceptance criteria** — It provides a way to demonstrate that the intended product works.

A useful PRD is specific about user-visible behavior, honest about open questions, and clear about what is out of scope. It should not be a copy of the questionnaire, a detailed code design, or a collection of decisions made silently by AI.

---

## 2. What the NoteCapsule PRD should cover

Based on the questionnaire, the main product loop is:

> NoteCapsule reminds the user to record a memory. The reminder provides a private, expiring link to a form where the user writes, reviews, and approves the memory before NoteCapsule emails it to a configured recipient.

A sensible PRD outline for this project is:

1. **Problem and motivation** — Who needs NoteCapsule, and what problem does it solve?
2. **Goals and non-goals** — What must the current release accomplish, and what waits?
3. **Users and context** — Who is the user, who is the recipient, and where is the app used?
4. **Product overview** — What is the main product loop?
5. **User journeys** — What happens during reminders, writing, approval, sending, and recovery?
6. **Functional requirements** — What must settings, reminders, links, drafts, approval, email, storage, and search do?
7. **Quality requirements** — What reliability, privacy, security, and error-handling behavior matters?
8. **Release boundaries** — What belongs in the MVP, v1, and later releases?
9. **Success criteria** — How will you demonstrate that the release works?
10. **Open questions and assumptions** — What still needs a decision?

Keep the PRD focused on product behavior. Detailed framework choices, database schemas, package layouts, and CI configuration belong in the build plan or a technical design.

### Decisions the skill should notice

The questionnaire is detailed, but some topics still require clarification. For example:

- Do “MVP” and “v1” mean the same release?
- How long does a private link remain valid, and what action consumes it?
- Who can reach the link, and does possession of the link grant access?
- What happens to a draft if the browser or app closes?
- When is memory content deleted after sending?
- Where are opted-in saved memories stored and searched?
- What happens after an expired link or failed email?

There are also older conflicts such as React versus JavaFX and MySQL versus SQLite. The skill should detect these by reading the project documents rather than relying only on this list.

When sources disagree, use this order:

1. The latest decision you explicitly confirm and save in a project document.
2. `docs/PROJECT_QUESTIONNAIRE.md`.
3. Older assumptions in `docs/BUILD_PLAN.md` or `README.md`.

---

## 3. How the guided PRD skill should behave

The most important part of the skill is its workflow. It should act like an interviewer and tutor, not a one-pass document generator.

For each PRD section, the skill should:

1. Explain briefly what the section is for.
2. Point out the questionnaire answers that apply.
3. Ask one to three focused questions at a time.
4. Explain why each question matters when the answer is not obvious.
5. Summarize your answers as product decisions and ask you to confirm them.
6. Draft that section using only confirmed decisions.
7. Ask you to review, question, or explain the draft in your own words.
8. Revise until you approve the section, then move to the next one.

The skill should keep a short progress list so you can see which sections are complete, in progress, or still blocked by questions.

### Required guardrails

The skill must:

- read the questionnaire, build plan, README, and existing PRD before drafting;
- identify conflicts instead of silently choosing a source;
- never invent a product decision;
- record unresolved decisions under Open questions;
- keep questionnaire non-goals out of the current release;
- distinguish product requirements from implementation details;
- avoid asking a large batch of questions all at once;
- make sure you understand and approve each section;
- show the complete PRD for review before writing it; and
- write `docs/PRD.md` only after you explicitly approve it.

If you ask it to “just write the whole PRD,” it should remind you of the guided workflow and begin with the first question cluster.

---

## 4. Use Codex to create the skill

The skill should live with the project:

```text
.agents/skills/notecapsule-prd/
  SKILL.md
  agents/
    openai.yaml
  references/
    prd-outline.md
```

`SKILL.md` contains the workflow and guardrails. The reference file contains the PRD outline so the main skill stays concise. The skill does not need scripts or other documentation for its first version.

### Step 1: Start the skill creator

Open Codex at the repository root and invoke:

```text
$skill-creator
```

### Step 2: Describe the skill you want

Use the following prompt as a starting point. Read it first and change anything that does not match how you want the skill to behave.

```text
Create a repository-scoped skill named notecapsule-prd under
.agents/skills/notecapsule-prd/.

Its purpose is to guide me through creating or updating docs/PRD.md for
NoteCapsule. This is a learning workflow, not a one-pass PRD generator.

Read these project sources while designing the skill:
- docs/PROJECT_QUESTIONNAIRE.md
- docs/BUILD_PLAN.md
- README.md
- docs/PRD_GUIDE.md
- docs/PRD.md, if it exists

The skill must:
- teach the purpose of each PRD section briefly;
- interview me one section and one small question cluster at a time;
- explain why important questions matter;
- identify missing or conflicting decisions without inventing answers;
- summarize my answers and ask for confirmation;
- draft each section only from confirmed decisions;
- ask me to review and understand each section before continuing;
- keep future and out-of-scope features out of the current release;
- track progress and unresolved questions;
- show the complete draft before writing it; and
- write docs/PRD.md only after my explicit approval.

Put the detailed NoteCapsule PRD outline in references/prd-outline.md rather
than duplicating it in SKILL.md. Initialize the skill using the standard skill
creator workflow, generate its standard metadata, and validate it when done.

Before creating files, restate the intended workflow and ask me whether I want
to change any of its guardrails.
```

### Step 3: Review what Codex proposes

Before approving creation, make sure you can answer:

- What requests will trigger this skill?
- What files will it read?
- What happens when information is missing?
- How does it prevent one-pass PRD generation?
- When is it allowed to write `docs/PRD.md`?

If any answer is unclear, ask Codex to revise the design before it creates the files.

### Step 4: Inspect and validate the skill

After Codex creates it:

1. Read `SKILL.md` yourself.
2. Check that the guardrails above appear as actual instructions.
3. Ask Codex to explain how each file contributes to the workflow.
4. Confirm that the skill creator's structural validation passes.

Then test behavior, not just file structure:

| Test request | Expected response |
|--------------|-------------------|
| “Write the entire PRD now.” | Begins the guided interview instead of generating the full document. |
| “Make links expire after 24 hours.” | Asks whether 24 hours is a confirmed decision and explains the tradeoff. |
| “Add voice-to-text to the MVP.” | Identifies the questionnaire scope conflict before changing anything. |
| “Help with the next PRD section.” | Explains the section, asks a small question cluster, and waits for answers. |

If a test fails, ask Codex to update the skill and run the test again.

---

## 5. Use the skill to create the PRD

Start a new Codex task in the repository and invoke:

```text
$notecapsule-prd

Help me create the initial NoteCapsule PRD. Follow the guided workflow and
start with the first section. Do not write docs/PRD.md until I approve the
complete draft.
```

From there:

1. Answer one question cluster at a time.
2. Ask for an explanation when you do not understand why a decision matters.
3. Correct any summary that does not reflect what you meant.
4. Review each drafted section before approving it.
5. Leave a question open rather than guessing when you are not ready to decide.
6. At the end, read the complete PRD and explain the main product journey in your own words.
7. Approve writing `docs/PRD.md` only when the document matches your decisions.

After the file is written, review the Git diff and open a documentation PR using `.github/PULL_REQUEST_TEMPLATE.md`. Write the Learning and AI use section in your own words.

---

## 6. Definition of done

This step is complete when:

- [ ] The skill exists under `.agents/skills/notecapsule-prd/` and passes validation.
- [ ] It asks focused questions and does not generate a one-pass PRD.
- [ ] It identifies conflicts and open questions without inventing answers.
- [ ] You can explain the purpose of each major PRD section.
- [ ] `docs/PRD.md` contains only decisions and wording you reviewed and approved.
- [ ] The PRD clearly separates the current release from future ideas.
- [ ] A documentation PR records what you learned and how AI helped.

The PRD does not need to answer every future product question. It needs to describe the current release clearly enough to guide implementation and help you recognize suggestions that do not match the product you decided to build.
