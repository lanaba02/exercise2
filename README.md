# To Do List app

ToDoList app using Spring Boot and Thymeleaf templates.

## Version
**1.1.0** - Released: 8/4/2026

## Requirements

You need install on your system:

- Java 8 SDK
- Maven 3.6+

## Project Management

Track project progress and tasks on our Trello board: [Trello Board](https://trello.com/b/KSF1IcaP/e2-to-do-list-app)

## Repositories & Images

- **GitHub Repository**: [GitHub Repository](https://github.com/lanaba02/exercise2)
- **DockerHub Image**: [DockerHub Image](https://hub.docker.com/r/lanabarisic/p2-todolist)


Once the app is running, you can open your favourite browser and connect to:

- [http://localhost:8080/login](http://localhost:8080/login)

---

## Technical Documentation

### Overview
This document outlines the evolution of the ToDoList application from version 1.0.1 to 1.1.0, detailing the implementation of new features including responsive navigation, user management system, admin functionality, and user blocking capabilities. The application now supports comprehensive user administration with role-based access control and account management features.

### New Classes and Methods Implemented

#### Model Layer
- **`Usuario.java`**: Enhanced with `admin` (Boolean) and `enabled` (Boolean) fields for administrator privileges and account status management

#### DTO Layer
- **`UsuarioData.java`**: Added `getAdmin()`/`setAdmin()` and `getEnabled()`/`setEnabled()` methods
- **`RegistroData.java`**: Added `admin` and `enabled` fields for registration data handling

#### Service Layer
- **`UsuarioService.java`**: 
  - `findAll()`: Retrieves all registered users
  - `existsAdmin()`: Checks for existing administrator users
  - `toggleUserEnabled(Long usuarioId)`: Toggles user account status

#### Controller Layer
- **`HomeController.java`**: 
  - `about(Model model)`: About page endpoint
  - `registeredUsers(Model model)`: User listing with admin protection
  - `userDescription(@PathVariable Long id, Model model)`: Individual user details with admin protection
  - `toggleUserStatus(@PathVariable Long id)`: User blocking/unblocking functionality
  - `comprobarUsuarioAdmin()`: Admin privilege validation

- **`LoginController.java`**: Updated with admin registration logic and login redirection based on user roles

- **`UsuarioNoAdminException.java`**: Custom exception for unauthorized admin access (HTTP 401)

### New Thymeleaf Templates Added

#### Core Templates
- **`about.html`**: Application information page with version details
- **`registered.html`**: User management interface with status indicators and action buttons
- **`userDescription.html`**: Detailed user profile display (excluding passwords)

#### Enhanced Templates
- **`fragments.html`**: Updated with responsive navbar fragment containing conditional navigation
- **`formRegistro.html`**: Enhanced with conditional admin checkbox for registration
- **`formLogin.html`**: Updated with account status validation

### Tests Implemented

#### Controller Tests
- **`NavbarControllerTest.java`** (6 tests): Validates responsive navigation behavior across authentication states
- **`AboutPageTest.java`** (1 test): Verifies about page content and accessibility
- **`RegisteredUsersControllerTest.java`** (13 tests): Comprehensive testing of user listing, description pages, and admin protection
- **`AdminRegistrationControllerTest.java`** (6 tests): Tests admin user registration constraints and login redirection
- **`BlockingUsersControllerTest.java`** (7 tests): Validates user blocking functionality and login restrictions

#### Service Tests
- **`UsuarioServiceTest.java`** (6 additional tests): Tests user retrieval, admin existence checking, and account status toggling

### Key Implementation Details

#### Admin User System
The application now supports a single administrator user with elevated privileges. Admin registration is restricted through a conditional checkbox that only appears when no administrator exists. Upon login, administrators are redirected to the user management page (`/registered`) while regular users proceed to their task lists.

#### User Management Protection
User listing and description pages are protected with role-based access control. Non-admin users receive HTTP 401 Unauthorized responses with clear error messages. The protection mechanism uses a custom exception class with Spring's `@ResponseStatus` annotation.

#### User Blocking System
Administrators can toggle user account status directly from the user management interface. Disabled users cannot log in and receive appropriate error messages. The system uses visual status indicators (green/red badges) and one-click toggle buttons for efficient account management.

### Interesting Source Code Example

One particularly elegant implementation is the admin privilege validation method in `HomeController.java`:

```java
private void comprobarUsuarioAdmin() {
    Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
    if (idUsuarioLogeado == null) {
        throw new UsuarioNoAdminException();
    }
    UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
    if (usuario == null || !Boolean.TRUE.equals(usuario.getAdmin())) {
        throw new UsuarioNoAdminException();
    }
}
```

This method elegantly combines authentication and authorization checks in a single, reusable function. It first verifies user authentication by checking the session, then validates admin privileges. The use of `Boolean.TRUE.equals()` ensures null-safe comparison, preventing potential NullPointerExceptions. The custom exception leverages Spring's error handling to return proper HTTP status codes and user-friendly error messages, maintaining clean separation of concerns between business logic and error presentation.
