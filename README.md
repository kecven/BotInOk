# BotInOk

Botinok Client is an open-source project that automates LinkedIn interactions using Java, Spring Boot, and Playwright. It helps users to connect with potential contacts and apply for job positions on LinkedIn.

## Features

- Automated LinkedIn connections
- Automated job applications
- Configurable search and connection parameters
- Supports multiple LinkedIn accounts
- Uses Playwright for browser automation

## Prerequisites

- Java 21
- Gradle 8
- Git

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/kecven/BotInOk.git
cd BotInOk
```

### Build the Project

```bash
./gradlew build
```

### Run the Application
For linux:
```bash
java --module-path ./libs/linux-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar build/libs/BotInOk-0.3.0.jar
```

For Windows:
```bash
java --module-path ./libs/windows-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar build/libs/BotInOk-0.3.0.jar
```

For MacOs:
```bash
java --module-path ./libs/macos-javafx-sdk-21.0.5/lib --add-modules javafx.controls -jar build/libs/BotInOk-0.3.0.jar
```

## Configuration

The application can be configured using the `application.properties` file located in the `src/main/resources` directory. You can set various parameters such as the path to the Playwright state folder, headless browser mode, and more.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Playwright](https://playwright.dev/)
- [JavaFX](https://openjfx.io/)
