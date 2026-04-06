package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class RegisteredUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private UsuarioService usuarioService;

    /**
     * Helper method to add an admin user to the database and mock login
     */
    void loginAsAdmin() {
        UsuarioData admin = new UsuarioData();
        admin.setId(1L);
        admin.setEmail("admin@umh.es");
        admin.setNombre("Administrator");
        admin.setPassword("admin123");
        admin.setAdmin(true);

        // Mock the ManagerUserSession to return the admin user ID
        when(managerUserSession.usuarioLogeado()).thenReturn(1L);
        // Mock the UsuarioService to return the admin user
        when(usuarioService.findById(1L)).thenReturn(admin);
    }

    @Test
    public void registeredUsersPageShowsUserList() throws Exception {
        // GIVEN
        // Admin user is logged in
        loginAsAdmin();

        // Mock findAll to return users
        UsuarioData user1 = new UsuarioData();
        user1.setId(2L);
        user1.setEmail("lana@umh.es");
        user1.setNombre("Lana Barisic");

        UsuarioData user2 = new UsuarioData();
        user2.setId(3L);
        user2.setEmail("richard@umh.es");
        user2.setNombre("Richard Stallman");

        when(usuarioService.findAll()).thenReturn(Arrays.asList(user1, user2));

        // WHEN, THEN
        // The registered users page is requested and contains both users
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Registered Users"),  // Page title
                        containsString("lana@umh.es"),        // First user email
                        containsString("richard@umh.es"),     // Second user email
                        containsString("2"),                  // First user ID
                        containsString("3")                   // Second user ID
                )));
    }

    @Test
    public void registeredUsersPageShowsEmptyMessageWhenNoUsers() throws Exception {
        // GIVEN
        // Admin user is logged in
        loginAsAdmin();

        // Mock findAll to return empty list
        when(usuarioService.findAll()).thenReturn(Collections.emptyList());

        // WHEN, THEN
        // The registered users page is requested and shows empty message
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Registered Users"),     // Page title
                        containsString("No registered users found.") // Empty message
                )));
    }

    @Test
    public void registeredUsersPageHasTableStructure() throws Exception {
        // GIVEN
        // Admin user is logged in
        loginAsAdmin();

        // Mock findAll to return a user
        UsuarioData user = new UsuarioData();
        user.setId(2L);
        user.setEmail("lana@umh.es");
        user.setNombre("Lana Barisic");
        when(usuarioService.findAll()).thenReturn(Collections.singletonList(user));

        // WHEN, THEN
        // The registered users page contains proper table structure
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("<table"),     // Table element
                        containsString("<thead"),     // Table header
                        containsString("<tbody"),     // Table body
                        containsString("ID"),         // ID column header
                        containsString("Email"),      // Email column header
                        containsString("Description") // Description column header
                )));
    }

    @Test
    public void userDescriptionPageShowsUserDetails() throws Exception {
        // GIVEN
        // Admin user is logged in
        loginAsAdmin();

        // Mock findById to return a user
        UsuarioData user = new UsuarioData();
        user.setId(2L);
        user.setEmail("lana@umh.es");
        user.setNombre("Lana Barisic");
        user.setPassword("1234");
        when(usuarioService.findById(2L)).thenReturn(user);

        // WHEN, THEN
        // The user description page shows user details (excluding password)
        this.mockMvc.perform(get("/registered/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("User Description"),  // Page title
                        containsString("lana@umh.es"),        // User email
                        containsString("Lana Barisic"),      // User name
                        containsString("2"),                  // User ID
                        not(containsString("1234"))          // Password should NOT be shown
                )));
    }

    @Test
    public void userDescriptionPageRedirectsForNonExistentUser() throws Exception {
        // GIVEN
        // Admin user is logged in
        loginAsAdmin();

        // Mock findById to return null for non-existent user
        when(usuarioService.findById(999L)).thenReturn(null);

        // WHEN, THEN
        // Accessing a non-existent user redirects to user list
        this.mockMvc.perform(get("/registered/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registered"));
    }

    @Test
    public void registeredUsersPageHasDescriptionLinks() throws Exception {
        // GIVEN
        // Admin user is logged in
        loginAsAdmin();

        // Mock findAll to return a user
        UsuarioData user = new UsuarioData();
        user.setId(2L);
        user.setEmail("lana@umh.es");
        user.setNombre("Lana Barisic");
        when(usuarioService.findAll()).thenReturn(Collections.singletonList(user));

        // WHEN, THEN
        // The registered users page contains description links
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("View Details"),      // Link text
                        containsString("/registered/2")       // Link URL
                )));
    }

    @Test
    public void registeredUsersPageReturnsUnauthorizedForNonAdminUser() throws Exception {
        // GIVEN
        // A regular user is logged in (not admin)
        UsuarioData regularUser = new UsuarioData();
        regularUser.setId(2L);
        regularUser.setEmail("user@umh.es");
        regularUser.setNombre("Regular User");
        regularUser.setAdmin(false);

        when(managerUserSession.usuarioLogeado()).thenReturn(2L);
        when(usuarioService.findById(2L)).thenReturn(regularUser);

        // WHEN, THEN
        // Accessing registered users page returns 401 Unauthorized
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void registeredUsersPageReturnsUnauthorizedForNotLoggedInUser() throws Exception {
        // GIVEN
        // No user is logged in
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        // Accessing registered users page returns 401 Unauthorized
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void userDescriptionPageReturnsUnauthorizedForNonAdminUser() throws Exception {
        // GIVEN
        // A regular user is logged in (not admin)
        UsuarioData regularUser = new UsuarioData();
        regularUser.setId(2L);
        regularUser.setEmail("user@umh.es");
        regularUser.setNombre("Regular User");
        regularUser.setAdmin(false);

        when(managerUserSession.usuarioLogeado()).thenReturn(2L);
        when(usuarioService.findById(2L)).thenReturn(regularUser);

        // WHEN, THEN
        // Accessing user description page returns 401 Unauthorized
        this.mockMvc.perform(get("/registered/3"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void userDescriptionPageReturnsUnauthorizedForNotLoggedInUser() throws Exception {
        // GIVEN
        // No user is logged in
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        // Accessing user description page returns 401 Unauthorized
        this.mockMvc.perform(get("/registered/3"))
                .andExpect(status().isUnauthorized());
    }
}
