# Contributing to EduBridge

Thank you for your interest in contributing to EduBridge! This document provides guidelines and instructions for contributing.

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Workflow](#development-workflow)
4. [Coding Standards](#coding-standards)
5. [Testing](#testing)
6. [Pull Request Process](#pull-request-process)
7. [Architecture Guidelines](#architecture-guidelines)

## Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Collaborate openly
- Report any unacceptable behavior

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

### Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/edubridge/edubridge.git
   cd edubridge
   ```

2. **Start infrastructure services**
   ```bash
   docker-compose up -d
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run a service**
   ```bash
   cd auth-service
   mvn spring-boot:run
   ```

## Development Workflow

### Branching Strategy

We follow **Git Flow**:

- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/*` - New features
- `bugfix/*` - Bug fixes
- `hotfix/*` - Urgent production fixes
- `release/*` - Release preparation

### Creating a Feature Branch

```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name
```

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `style` - Code style (formatting)
- `refactor` - Code refactoring
- `test` - Adding tests
- `chore` - Maintenance tasks

**Examples:**
```
feat(auth): add JWT refresh token flow

Implemented refresh token mechanism to allow users to obtain
new access tokens without re-authenticating.

Closes #123
```

```
fix(assessment): correct scoring calculation for MCQs

Fixed bug where partial credit was not being awarded correctly
for multiple-choice questions with multiple correct answers.

Fixes #456
```

## Coding Standards

### Java Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use Lombok to reduce boilerplate
- Maximum line length: 120 characters
- Use meaningful variable and method names

### Key Principles

1. **SOLID Principles** - Follow object-oriented design principles
2. **DRY** - Don't Repeat Yourself
3. **KISS** - Keep It Simple, Stupid
4. **YAGNI** - You Aren't Gonna Need It

### Code Organization

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/com/edubridge/service/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # Data access
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── dto/             # Data transfer objects
│   │   │   ├── mapper/          # Entity-DTO mappers
│   │   │   ├── config/          # Configuration classes
│   │   │   └── exception/       # Custom exceptions
│   │   └── resources/
│   │       ├── db/migration/    # Flyway migrations
│   │       └── application.yml
│   └── test/
│       ├── java/                # Unit tests
│       └── resources/           # Test resources
```

### Naming Conventions

- **Classes:** PascalCase (e.g., `UserService`)
- **Methods:** camelCase (e.g., `getUserById`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
- **Packages:** lowercase (e.g., `com.edubridge.auth`)

### Documentation

- Add Javadoc for public classes and methods
- Include `@param`, `@return`, and `@throws` tags
- Explain complex logic with inline comments

Example:
```java
/**
 * Generates a personalized learning path for a student based on their skill vector.
 *
 * @param studentId the unique identifier of the student
 * @param subject the subject for which to generate the path
 * @return a list of recommended lessons in order
 * @throws ResourceNotFoundException if the student is not found
 */
public List<Lesson> generateLearningPath(UUID studentId, String subject) {
    // Implementation
}
```

## Testing

### Test Coverage

- Aim for **>80% code coverage**
- Write unit tests for all business logic
- Write integration tests for API endpoints
- Use Testcontainers for database tests

### Testing Structure

```java
@SpringBootTest
class UserServiceTest {
    
    @Autowired
    private UserService userService;
    
    @MockBean
    private UserRepository userRepository;
    
    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
            .name("John Doe")
            .email("john@example.com")
            .build();
        
        // When
        User user = userService.registerUser(request);
        
        // Then
        assertNotNull(user.getId());
        assertEquals("John Doe", user.getName());
    }
}
```

### Running Tests

```bash
# Unit tests
mvn test

# Integration tests
mvn verify -P integration-tests

# With coverage
mvn test jacoco:report
```

## Pull Request Process

### Before Submitting

1. **Update from develop**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout feature/your-feature
   git rebase develop
   ```

2. **Run tests**
   ```bash
   mvn clean verify
   ```

3. **Check code style**
   ```bash
   mvn spotless:check
   ```

4. **Update documentation** if needed

### Submitting a PR

1. **Push your branch**
   ```bash
   git push origin feature/your-feature
   ```

2. **Create PR on GitHub**
   - Use a descriptive title
   - Fill out the PR template
   - Link related issues
   - Add screenshots for UI changes

3. **PR Checklist**
   - [ ] Tests pass
   - [ ] Code follows style guide
   - [ ] Documentation updated
   - [ ] No merge conflicts
   - [ ] Descriptive commit messages

### Review Process

- At least **one approval** required
- Address all review comments
- Keep PRs focused and small (<500 lines)
- Be responsive to feedback

## Architecture Guidelines

### Service Design

- Each service should be **independent**
- Communicate via REST APIs
- Use **DTOs** for API contracts
- Never expose entities directly

### Database

- Use **Flyway** for migrations
- One migration per logical change
- Never modify existing migrations
- Include rollback scripts

### Security

- Never commit secrets or API keys
- Use environment variables
- Hash passwords with BCrypt
- Validate all inputs
- Implement proper authorization

### Performance

- Use caching where appropriate (Redis)
- Optimize database queries
- Implement pagination for lists
- Monitor performance metrics

### Error Handling

- Use custom exceptions
- Return consistent error responses
- Log errors appropriately
- Don't expose stack traces in production

## Questions?

If you have questions, please:

1. Check existing documentation
2. Search closed issues
3. Ask in discussions
4. Create a new issue

Thank you for contributing to EduBridge! 🎓
