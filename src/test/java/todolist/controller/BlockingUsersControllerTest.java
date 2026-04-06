package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class BlockingUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Helper method to add a regular user to the database
     */
    Long addRegularUserBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@umh.es");
        usuario.setNombre("Regular User");
        usuario.setPassword("user123");
        usuario.setAdmin(false);
        usuario.setEnabled(true);
        UsuarioData nuevoUsuario = usuarioService.registrar(usuario);
        return nuevoUsuario.getId();
    }

    /**
     * Helper method to add an admin user to the database
     */
    Long addAdminBD() {
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@umh.es");
        admin.setNombre("Administrator");
        admin.setPassword("admin123");
        admin.setAdmin(true);
        admin.setEnabled(true);
        UsuarioData nuevoAdmin = usuarioService.registrar(admin);
        return nuevoAdmin.getId();
    }

    /**
     * Helper method to mock admin login - creates admin user in database
     */
    void loginAsAdmin() {
        // Create and save admin user in database
        UsuarioData admin = new UsuarioData();
        admin.setEmail("admin@umh.es");
        admin.setNombre("Administrator");
        admin.setPassword("admin123");
        admin.setAdmin(true);
        admin.setEnabled(true);
        UsuarioData savedAdmin = usuarioService.registrar(admin);

        // Mock the session to return the admin ID
        when(managerUserSession.usuarioLogeado()).thenReturn(savedAdmin.getId());
    }

    @Test
    public void disabledUserCannotLogin() throws Exception {
        // GIVEN
        // A disabled user in the database
        Long userId = addRegularUserBD();
        usuarioService.toggleUserEnabled(userId);

        // WHEN, THEN
        // Attempt to login with disabled user shows error message
        this.mockMvc.perform(post("/login")
                .param("eMail", "user@umh.es")
                .param("password", "user123"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User account is disabled. Contact administrator.")));
    }

    @Test
    public void enabledUserCanLogin() throws Exception {
        // GIVEN
        // An enabled user in the database
        Long userId = addRegularUserBD();

        // WHEN, THEN
        // Login with enabled user redirects to tasks page
        this.mockMvc.perform(post("/login")
                .param("eMail", "user@umh.es")
                .param("password", "user123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/" + userId + "/tareas"));
    }

    @Test
    public void registeredUsersPageShowsUserStatus() throws Exception {
        // GIVEN
        // Admin is logged in
        loginAsAdmin();

        // Create a regular user
        UsuarioData user = new UsuarioData();
        user.setEmail("user@umh.es");
        user.setNombre("Regular User");
        user.setPassword("user123");
        user.setEnabled(true);
        usuarioService.registrar(user);

        // WHEN, THEN
        // User list shows both enabled and disabled status badges
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Enabled"),
                        containsString("Disable")
                )));
    }

    @Test
    public void adminCanDisableUser() throws Exception {
        // GIVEN
        // Admin is logged in
        loginAsAdmin();

        // Create and save a regular user
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@umh.es");
        usuario.setNombre("Regular User");
        usuario.setPassword("user123");
        usuario.setEnabled(true);
        UsuarioData savedUser = usuarioService.registrar(usuario);
        Long userId = savedUser.getId();

        // WHEN
        // Admin clicks disable button
        this.mockMvc.perform(post("/registered/" + userId + "/toggle-status"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registered"));

        // THEN
        // User is now disabled
        UsuarioData userAfter = usuarioService.findById(userId);
        assert userAfter.getEnabled() == false;
    }

    @Test
    public void adminCanEnableUser() throws Exception {
        // GIVEN
        // Admin is logged in
        loginAsAdmin();

        // Create and save a disabled user
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@umh.es");
        usuario.setNombre("Regular User");
        usuario.setPassword("user123");
        usuario.setEnabled(true);
        UsuarioData savedUser = usuarioService.registrar(usuario);
        Long userId = savedUser.getId();

        // Disable the user first
        usuarioService.toggleUserEnabled(userId);

        // WHEN
        // Admin clicks enable button
        this.mockMvc.perform(post("/registered/" + userId + "/toggle-status"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registered"));

        // THEN
        // User is now enabled
        UsuarioData userAfter = usuarioService.findById(userId);
        assert userAfter.getEnabled() == true;
    }

    @Test
    public void nonAdminCannotToggleUserStatus() throws Exception {
        // GIVEN
        // Non-admin user is logged in (or not logged in)
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        // Non-admin cannot access toggle endpoint
        this.mockMvc.perform(post("/registered/2/toggle-status"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void registeredUsersPageShowsToggleButtons() throws Exception {
        // GIVEN
        // Admin is logged in
        loginAsAdmin();

        // Create a regular user
        UsuarioData user = new UsuarioData();
        user.setEmail("user@umh.es");
        user.setNombre("Regular User");
        user.setPassword("user123");
        user.setEnabled(true);
        usuarioService.registrar(user);

        // WHEN, THEN
        // User list shows disable button for enabled users
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Disable"),
                        containsString("Enable")
                )));
    }
}








