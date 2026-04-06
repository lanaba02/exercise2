# To Do List app

ToDoList app using Spring Boot and Thymeleaf templates.

## Requirements

You need install on your system:

- Java 8 SDK
- Maven 3.6+

## Project Management

Track project progress and tasks on our Trello board: [Trello Board](https://trello.com/b/KSF1IcaP/e2-to-do-list-app)

## Repositories & Images

- **GitHub Repository**: [GitHub Repository](https://github.com/lanaba02/exercise2)
- **DockerHub Image**: 


## Ejecución

You can run the app using the goal `run` from Maven's _plugin_ 
on Spring Boot:

```
$ ./mvn spring-boot:run 
```   

You can already create a `jar` file and run it:

```
$ ./mvn package
$ java -jar target/todolist-inicial-0.0.1-SNAPSHOT.jar
```

Once the app is running, you can open your favourite browser and connect to:

- [http://localhost:8080/login](http://localhost:8080/login)

---

## Technical Documentation

### Menubar Implementation

#### Overview
The responsive navigation bar (menubar) provides context-aware navigation based on authentication state. It dynamically displays different elements for authenticated and unauthenticated users, ensuring a seamless user experience across the application.

#### New Classes & Methods

##### Controller Layer
- **`HomeController.java`**: Manages the about page endpoint
  - `about(Model model)`: Returns the about page view, with conditional user data based on authentication status

#### New Thymeleaf Templates/Fragments

##### `fragments.html` - Navbar Fragment
The reusable navbar fragment containing:
- **`navbar` fragment**: Dynamic navigation bar with conditional rendering based on user authentication
- **`head` fragment**: Meta tags and Bootstrap CSS integration for responsive design
- **`javascript` fragment**: Required jQuery, Popper, and Bootstrap JavaScript libraries

#### Implementation Details

The menubar uses **conditional rendering** in Thymeleaf to display different navigation elements:

```html
<!-- Tasks link - only visible if user is logged in -->
<li class="nav-item" th:if="${usuario != null}">
    <a class="nav-link" th:href="@{/usuarios/{id}/tareas(id=${usuario.id})}">Tasks</a>
</li>

<!-- If user is NOT logged in: show Login and Registration links -->
<li class="nav-item" th:if="${usuario == null}">
    <a class="nav-link" href="/login">Login</a>
</li>
```

This ensures unauthenticated users see login/registration links, while authenticated users see task management options and a user dropdown menu with logout functionality.

#### Tests Implemented

##### `NavbarControllerTest.java` (6 test cases)
- **`navbarShowsWhenUserIsLoggedInOnAboutPage()`**: Validates navbar displays user info and dropdown when authenticated
- **`navbarShowsLoginRegistrationLinksWhenUserNotLoggedInOnAboutPage()`**: Confirms login/registration links appear for non-authenticated users
- **`navbarHasTasksLinkWhenUserIsLoggedIn()`**: Ensures Tasks link visible only for logged-in users
- **`navbarHasLogoutOptionInDropdown()`**: Verifies logout functionality in user dropdown
- **`navbarAppearsOnTasksPage()`**: Confirms navbar consistency across pages
- **`navbarDoesNotAppearOnLoginPage()` & `navbarDoesNotAppearOnRegistrationPage()`**: Validates navbar excluded from auth pages

#### Key Features
1. **Responsive Design**: Bootstrap grid system for mobile compatibility
2. **Conditional Navigation**: Dynamically adapts based on authentication state
3. **Fragment Reusability**: Reduces code duplication across templates
4. **User Dropdown Menu**: Quick access to account and logout options
5. **Accessibility**: HTML5 semantic markup and proper Bootstrap classes

---

### About Page Implementation

#### Overview
The about page displays application information and is accessible to all users (authenticated and non-authenticated). It integrates the navbar fragment to provide consistent navigation throughout the application.

#### New Thymeleaf Templates

##### `about.html`
The about page template that displays:
- Application name and version information
- Developer information
- Release date
- Integrated navbar for consistent navigation

#### Tests Implemented

##### `AboutPageTest.java`
- **`getAboutDevuelveNombreAplicacion()`**: Verifies the about page returns the application name "ToDoList"

#### Key Features
1. **Public Access**: Available to all users without authentication requirements
2. **Navbar Integration**: Maintains consistent navigation across the app
3. **Information Display**: Shows application metadata and developer credits

---

### User List Implementation

#### Overview
The user list feature provides a public endpoint `/registered` that displays a table of all registered users in the system, showing their identifier (ID) and email address. This feature is accessible to all users without authentication requirements.

#### New Classes & Methods

##### Controller Layer
- **`HomeController.java`**: Added `registeredUsers()` method
  - `registeredUsers(Model model)`: Retrieves all users from service and passes them to the view

##### Service Layer
- **`UsuarioService.java`**: Added `findAll()` method
  - `findAll()`: Returns all registered users as `Iterable<UsuarioData>`

#### New Thymeleaf Templates

##### `registered.html`
The user list template that displays:
- Table with user ID and email columns
- Responsive Bootstrap table design
- Empty state message when no users are registered
- Integrated navbar for consistent navigation

#### Implementation Details

The user list uses a simple table structure to display user information:

```html
<table class="table table-striped table-hover">
    <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Email</th>
        </tr>
    </thead>
    <tbody>
        <tr th:each="usuario : ${usuarios}">
            <td th:text="${usuario.id}"></td>
            <td th:text="${usuario.email}"></td>
        </tr>
    </tbody>
</table>
```

#### Tests Implemented

##### `UsuarioServiceTest.java`
- **`servicioFindAllUsuarios()`**: Verifies the `findAll()` method returns all registered users correctly

##### `RegisteredUsersControllerTest.java` (3 comprehensive test cases)
- **`registeredUsersPageShowsUserList()`**: Validates that the page displays all users with their IDs and emails
- **`registeredUsersPageShowsEmptyMessageWhenNoUsers()`**: Confirms empty state message appears when no users exist
- **`registeredUsersPageHasTableStructure()`**: Ensures proper table HTML structure is rendered

#### Key Features
1. **Public Access**: No authentication required to view registered users
2. **Complete User List**: Shows all registered users in the system
3. **Responsive Design**: Bootstrap table adapts to different screen sizes
4. **Empty State Handling**: Displays appropriate message when no users exist
5. **Simple Data Display**: Shows only ID and email as specified in requirements

---

### User Description Implementation

#### Overview
The user description feature provides detailed information for individual users accessible from the user list. Each user in the registered users table has a "View Details" link that leads to a dedicated page showing all user information except the password. The path follows the pattern `/registered/{id}`.

#### New Classes & Methods

##### Controller Layer
- **`HomeController.java`**: Added `userDescription()` method
  - `userDescription(@PathVariable Long id, Model model)`: Handles `/registered/{id}` requests, retrieves user by ID, and redirects to user list if user doesn't exist

#### New Thymeleaf Templates

##### `userDescription.html`
The user description template that displays:
- User ID, email, name, and birth date
- Excludes password for security
- Formatted birth date display with fallback for null values
- Navigation back to user list
- Integrated navbar for consistent navigation

##### Updated `registered.html`
Enhanced the user list template with:
- Added "Description" column header
- "View Details" button for each user linking to `/registered/{id}`

#### Implementation Details

The user description page uses a Bootstrap card layout to display user information:

```html
<dl class="row">
    <dt class="col-sm-4">ID:</dt>
    <dd class="col-sm-8" th:text="${usuario.id}"></dd>
    <dt class="col-sm-4">Email:</dt>
    <dd class="col-sm-8" th:text="${usuario.email}"></dd>
    <dt class="col-sm-4">Name:</dt>
    <dd class="col-sm-8" th:text="${usuario.nombre}"></dd>
    <dt class="col-sm-4">Birth Date:</dt>
    <dd class="col-sm-8" th:text="${usuario.fechaNacimiento != null ? #temporals.format(usuario.fechaNacimiento, 'dd/MM/yyyy') : 'Not specified'}"></dd>
</dl>
```

#### Tests Implemented

##### `RegisteredUsersControllerTest.java` (4 additional test cases)
- **`userDescriptionPageShowsUserDetails()`**: Validates that user details are displayed correctly (excluding password)
- **`userDescriptionPageRedirectsForNonExistentUser()`**: Ensures non-existent users redirect to user list
- **`registeredUsersPageHasDescriptionLinks()`**: Confirms "View Details" links are present in user list
- **`registeredUsersPageHasTableStructure()`**: Updated to verify the new Description column

#### Key Features
1. **Secure Data Display**: Shows all user fields except password
2. **Error Handling**: Redirects to user list for non-existent users
3. **Responsive Design**: Bootstrap card layout adapts to screen sizes
4. **Date Formatting**: Proper date display with null handling
5. **Navigation**: Easy return to user list with back button
6. **Consistent UI**: Maintains navbar integration across pages

---

### Admin User Implementation

#### Overview
The admin user feature allows registration of a single administrator user who has special privileges and different login redirection behavior. Admin users are redirected to the user list page after login, while regular users are redirected to their task pages. Only one administrator can exist in the system.

#### New Classes & Methods

##### Model Layer
- **`Usuario.java`**: Added `admin` field
  - `Boolean admin`: Indicates if user has administrator privileges (defaults to false)

##### DTO Layer
- **`UsuarioData.java`**: Added `admin` field
  - `Boolean getAdmin()` / `setAdmin(Boolean admin)`: Admin status accessor methods

- **`RegistroData.java`**: Added `admin` field
  - `Boolean getAdmin()` / `setAdmin(Boolean admin)`: Admin registration flag

##### Service Layer
- **`UsuarioService.java`**: Added `existsAdmin()` method
  - `existsAdmin()`: Checks if an administrator user already exists in the system

##### Controller Layer
- **`LoginController.java`**: Updated registration and login logic
  - `registroForm()`: Passes `adminExists` flag to conditionally show admin checkbox
  - `registroSubmit()`: Validates admin registration (only one admin allowed)
  - `loginSubmit()`: Redirects admin users to `/registered`, regular users to `/usuarios/{id}/tareas`

#### New Thymeleaf Templates/Fragments

##### Updated `formRegistro.html`
Enhanced registration form with:
- Conditional admin checkbox that only appears when no administrator exists
- Bootstrap form-check styling for the checkbox
- Error handling for admin registration attempts when admin already exists

#### Implementation Details

The admin checkbox is conditionally displayed using Thymeleaf:

```html
<div class="form-group" th:if="${!adminExists}">
    <div class="form-check">
        <input id="admin" class="form-check-input" name="admin" type="checkbox"
               th:field="*{admin}"/>
        <label for="admin" class="form-check-label">
            Register as administrator
        </label>
    </div>
</div>
```

Admin login redirection logic:

```java
// Redirect admin users to user list, regular users to their tasks
if (Boolean.TRUE.equals(usuario.getAdmin())) {
    return "redirect:/registered";
} else {
    return "redirect:/usuarios/" + usuario.getId() + "/tareas";
}
```

#### Tests Implemented

##### `UsuarioServiceTest.java` (3 additional test cases)
- **`servicioExistsAdminReturnsFalseWhenNoAdmin()`**: Verifies false when no admin exists
- **`servicioExistsAdminReturnsTrueWhenAdminExists()`**: Verifies true when admin exists
- **`servicioRegistroUsuarioAdmin()`**: Tests admin user registration

##### `AdminRegistrationControllerTest.java` (6 comprehensive test cases)
- **`registroFormShowsAdminCheckboxWhenNoAdminExists()`**: Verifies checkbox appears when no admin
- **`registroFormHidesAdminCheckboxWhenAdminExists()`**: Verifies checkbox hidden when admin exists
- **`registroSubmitCreatesAdminUserWhenNoAdminExists()`**: Tests successful admin registration
- **`registroSubmitRejectsAdminRegistrationWhenAdminExists()`**: Tests rejection when admin exists
- **`loginRedirectsAdminToUserList()`**: Verifies admin login redirects to user list
- **`loginRedirectsRegularUserToTasks()`**: Verifies regular user login redirects to tasks

#### Key Features
1. **Single Admin Constraint**: Only one administrator can exist in the system
2. **Conditional UI**: Admin checkbox only appears when no administrator exists
3. **Special Login Flow**: Admin users are redirected to user management page
4. **Secure Registration**: Prevents multiple admin registrations
5. **Backward Compatibility**: Regular users continue to work as before
6. **Error Handling**: Clear error messages for invalid admin registration attempts

---

### User Listing and User Description Protection Implementation

#### Overview
The user listing and user description pages are now protected and only accessible to administrator users. Non-admin users attempting to access these pages receive an HTTP 401 Unauthorized error with an appropriate error message. This ensures that sensitive user information is only accessible to authorized administrators.

#### New Classes & Methods

##### Controller Layer Exceptions
- **`UsuarioNoAdminException.java`**: Custom exception for admin access violations
  - Returns HTTP 401 Unauthorized with message "Insufficient permissions - Administrator access required"

##### Controller Layer
- **`HomeController.java`**: Updated with admin authentication checks
  - `comprobarUsuarioAdmin()`: Validates that current user is logged in and has admin privileges
  - Updated `registeredUsers()` and `userDescription()` methods with admin checks

#### Implementation Details

The protection mechanism uses a custom exception that leverages Spring's `@ResponseStatus` annotation:

```java
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason="Insufficient permissions - Administrator access required")
public class UsuarioNoAdminException extends RuntimeException {
}
```

Admin validation logic checks both authentication and authorization:

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

#### Tests Implemented

##### `RegisteredUsersControllerTest.java` (6 additional test cases)
- **`registeredUsersPageReturnsUnauthorizedForNonAdminUser()`**: Verifies 401 response for logged-in non-admin users
- **`registeredUsersPageReturnsUnauthorizedForNotLoggedInUser()`**: Verifies 401 response for unauthenticated users
- **`userDescriptionPageReturnsUnauthorizedForNonAdminUser()`**: Verifies 401 response for non-admin access to user details
- **`userDescriptionPageReturnsUnauthorizedForNotLoggedInUser()`**: Verifies 401 response for unauthenticated access to user details
- Updated existing tests to include admin authentication

#### Key Features
1. **Admin-Only Access**: User listing and description pages require administrator privileges
2. **Proper HTTP Status**: Returns 401 Unauthorized for unauthorized access attempts
3. **Clear Error Messages**: Provides descriptive error messages for insufficient permissions
4. **Authentication + Authorization**: Checks both login status and admin role
5. **Consistent Protection**: Both `/registered` and `/registered/{id}` endpoints are protected
6. **Graceful Error Handling**: Uses Spring's exception handling mechanism for clean error responses

#### Security Considerations
- **Authentication Required**: Users must be logged in to access protected pages
- **Role-Based Access**: Only users with `admin = true` can access user management features
- **HTTP Status Compliance**: Proper 401 status code for unauthorized access
- **Error Message Clarity**: Clear indication of required permissions
- **No Information Leakage**: Unauthorized users receive error responses, not redirects

---

### Blocking Users by Admin Implementation

#### Overview
The blocking users feature allows administrators to disable or enable user accounts directly from the user management interface. When a user's account is disabled, they cannot log in and will receive an error message indicating their account is disabled. This provides administrators with a way to temporarily or permanently restrict user access.

#### New Classes & Methods

##### Model Layer
- **`Usuario.java`**: Added `enabled` field
  - `Boolean enabled`: Indicates if user account is active (defaults to true)

##### DTO Layer
- **`UsuarioData.java`**: Added `enabled` field
  - `Boolean getEnabled()` / `setEnabled(Boolean enabled)`: Enabled status accessor methods

- **`RegistroData.java`**: Added `enabled` field
  - `Boolean getEnabled()` / `setEnabled(Boolean enabled)`: Enabled status during registration (defaults to true)

##### Service Layer
- **`UsuarioService.java`**: Added `toggleUserEnabled()` method
  - `toggleUserEnabled(Long usuarioId)`: Toggles the enabled status of a user

##### Controller Layer
- **`HomeController.java`**: Added `toggleUserStatus()` method
  - `toggleUserStatus(@PathVariable Long id)`: Handles POST request to `/registered/{id}/toggle-status` to toggle user enabled status
  - Protected with admin check via `comprobarUsuarioAdmin()`

- **`LoginController.java`**: Updated `loginSubmit()` method
  - Checks if user is enabled before allowing login
  - Shows error message if account is disabled

#### Updated Thymeleaf Templates

##### `registered.html` Enhanced
Added to the user list table:
- **Status Column**: Shows "Enabled" (green badge) or "Disabled" (red badge) for each user
- **Actions Column**: Contains toggle buttons to enable/disable users
  - "Disable" button appears for enabled users (red)
  - "Enable" button appears for disabled users (green)
- **Implementation uses Thymeleaf conditionals**: `th:if` and `th:unless` to show appropriate buttons

#### Implementation Details

The toggle button implementation in the template:

```html
<td>
    <span th:if="${usuario.enabled}" class="badge bg-success">Enabled</span>
    <span th:unless="${usuario.enabled}" class="badge bg-danger">Disabled</span>
</td>
<td>
    <form th:action="@{/registered/{id}/toggle-status(id=${usuario.id})}" method="post" style="display:inline;">
        <button th:if="${usuario.enabled}" type="submit" class="btn btn-danger btn-sm">Disable</button>
        <button th:unless="${usuario.enabled}" type="submit" class="btn btn-success btn-sm">Enable</button>
    </form>
</td>
```

Login validation logic:

```java
// Check if user is enabled
if (!Boolean.TRUE.equals(usuario.getEnabled())) {
    model.addAttribute("error", "User account is disabled. Contact administrator.");
    return "formLogin";
}
```

#### Tests Implemented

##### `UsuarioServiceTest.java` (2 additional test cases)
- **`servicioToggleUserEnabled()`**: Tests toggling user enabled status from true to false
- **`servicioToggleUserEnabledTwice()`**: Tests toggling user status back to enabled

##### `BlockingUsersControllerTest.java` (7 comprehensive test cases)
- **`disabledUserCannotLogin()`**: Verifies disabled users cannot login
- **`enabledUserCanLogin()`**: Verifies enabled users can login normally
- **`registeredUsersPageShowsUserStatus()`**: Confirms status badges appear in user list
- **`adminCanDisableUser()`**: Tests admin can disable a user
- **`adminCanEnableUser()`**: Tests admin can enable a disabled user
- **`nonAdminCannotToggleUserStatus()`**: Verifies non-admin users cannot toggle status
- **`registeredUsersPageShowsToggleButtons()`**: Confirms toggle buttons appear in table

#### Key Features
1. **Admin-Controlled Access**: Only administrators can enable/disable users
2. **Visual Status Indicators**: Color-coded badges show user enabled/disabled status
3. **One-Click Toggle**: Simple buttons to toggle user status without page navigation
4. **Login Protection**: Disabled users cannot log into the system
5. **Clear Error Messages**: Users see appropriate message when account is disabled
6. **Instant Updates**: Status changes are reflected immediately upon admin action
7. **Persistent State**: Enabled/disabled status is saved to the database

#### Security Considerations
- **Admin-Only Protection**: Toggle endpoint is protected with admin check
- **Database Persistence**: Status changes are stored permanently
- **Login Validation**: Login process checks enabled status on every attempt
- **User Feedback**: Clear error messages guide users when blocked
- **Audit Trail**: Status changes affect user experience directly and visibly

---

## Repositories & Images

- **GitHub Repository**: [lanaba02/exercise2](https://github.com/lanaba02/exercise2)
- **DockerHub Image**: To be updated
