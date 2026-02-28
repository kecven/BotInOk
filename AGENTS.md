# Repository Guidelines

## Project Structure & Module Organization
Core code lives in `src/main/java/digital/moveto/botinok`.
- `BotinokClientApplication` is the JavaFX + Spring Boot entry point.
- `client/` contains LinkedIn automation, Playwright integration, UI scenes, and app config.
- `model/` contains domain entities, DTOs, repositories, and services.
- `src/main/resources` holds runtime configuration (`application.properties`), CSV data, and UI images.
- `libs/*-javafx-sdk-21.0.5` stores OS-specific JavaFX binaries used when running the packaged JAR.
- `installer/` contains prebuilt distributable archives.

## Build, Test, and Development Commands
Use the Gradle wrapper from repository root:
- `./gradlew clean build` compiles, runs tests, and builds artifacts.
- `./gradlew test` runs the JUnit test suite only.
- `./gradlew bootJar` builds the executable Spring Boot JAR in `build/libs/`.
- `./gradlew run` starts the app in development mode (requires JavaFX setup).
- `./start.sh` runs the Linux flow used by this repo (pull, build, launch JAR).

Example packaged run (Linux):
`java --module-path ./libs/linux-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar build/libs/BotInOk-0.4.0.jar`

## Coding Style & Naming Conventions
- Java 21 toolchain, 4-space indentation, UTF-8 source files.
- Follow package-by-feature grouping already used (`client.*`, `model.*`).
- Classes/interfaces: `PascalCase`; methods/fields: `camelCase`; constants: `UPPER_SNAKE_CASE`.
- Keep Spring service/repository names explicit (`AccountService`, `CompanyRepository`).
- Prefer small, focused methods for bot actions; isolate selectors/URLs in utility/config classes.

## Testing Guidelines
- Framework: JUnit 5 (`useJUnitPlatform()` in `build.gradle`).
- Place tests under `src/test/java` mirroring main package structure.
- Name test classes `*Test` (example: `ContactServiceTest`) and test methods by behavior.
- Cover new business rules in `model/service` and automation decision logic in `client/linkedin`.

## Commit & Pull Request Guidelines
- Current history favors short imperative subjects (`add ...`, `update ...`, `improve ...`).
- Prefer: `<area>: <imperative summary>` (example: `client/linkedin: tighten connect filtering`).
- Keep commits focused; avoid mixing refactors with behavior changes.
- PRs should include: purpose, key changes, test evidence (`./gradlew test` output), and screenshots/GIFs for UI updates.

## Security & Configuration Tips
- Do not commit personal LinkedIn session data or local runtime files under `~/.botinok/`.
- Review `src/main/resources/application.properties` before running; override sensitive/local values via environment-specific configuration.
