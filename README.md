# NoteCapsule

This is a desktop Application designed that helps people capture memories about their children or loved ones daily. Users can write or orate memories to the program, and those memories are sent to an e-mail of your choice.

## Features

## Architecture

### Frontend
- React
- TypeScript
- Vite

### Backend
- Java 21
- Spring Boot
- Maven

### Database
- MySQL

### AI Services (Optional)
- Sentence Transformers
- Ollama / OpenAI (future)
- RAG (future)

### Development
- Frontend runs separately from the backend
- Frontend talks to the backend via REST APIs
- Backend talks to MySQL

## Getting Started

### Requirements

- Java 21
- Node.js
- Maven
- MySQL

### Backend

- `./mvnw spring-boot:run  (Starts the backend)
- `./mvnw test (runs backend tests)
- `./mvnw clean package (builds the application and packages into a JAR)

### Frontend

- `npm install (installs frontend dependencies)
- `npm run dev  (starts frontend (dev) server)
- `npm run build (builds the production frontend)


### Core

- Schedule a time to write the memory.
- Voice-to-text automation (optional; uses ai model)
- Memory, via audio or text, is presented for authorization. Once approved, email sent!
- Attach photos
- Save drafts
- Search and review previously written memories (maybe RAG)
- Push notifications, via discord or some other medium if the memory goes undone > 30 mins.


## Local Development

### Environment Variables

NoteCapsule requires environment variables to connect to the database.

Create a `.env` file in the project root containing:

```env
DB_URL=<database-url>
DB_USERNAME=<database-username>
DB_PASSWORD=<database-password>
```

The `.env` file contains sensitive information and must **not** be committed to Git.

### Maven Environment Script

Use `mvn-env.sh` when running Maven commands that require the environment variables.

The script loads the variables from `.env` into the process environment and then executes the supplied Maven command.

Make the script executable:

```bash
chmod u+x mvn-env.sh
```

### Run Tests

```bash
./mvn-env.sh test
```

### Run the Application

```bash
./mvn-env.sh spring-boot:run
```

### Other Maven Commands

Any Maven command can be passed through the script:

```bash
./mvn-env.sh <maven-command>
```

For example:

```bash
./mvn-env.sh clean test
```


### Future
 
- Docker 
- Maybe a mobile version

## AI Engineering Features
- Must be turned on by user
- Prompt engineering
- Tool Calling
- Agent workflows
- Telemetry
- Retrieval Augmented Generation (RAG)
- 

## Motivation

For my personal use, to learn industry practices, and to share with the world!

## Getting Started

Coming soon!

## Roadmap

Coming soon!

## Contributing

Coming soon!

## License

MIT