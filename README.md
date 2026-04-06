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

## Repositories & Images

- **GitHub Repository**: [lanaba02/exercise2](https://github.com/lanaba02/exercise2)
- **DockerHub Image**: To be updated
