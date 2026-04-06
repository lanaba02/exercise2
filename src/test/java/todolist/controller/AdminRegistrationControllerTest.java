package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.RegistroData;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class AdminRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    /**
     * Helper method to add a user to the database
     */
    Long addUsuarioBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("lana@umh.es");
        usuario.setNombre("Lana Barisic");
        usuario.setPassword("1234");
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
        UsuarioData nuevoAdmin = usuarioService.registrar(admin);
        return nuevoAdmin.getId();
    }

    @Test
    public void registroFormShowsAdminCheckboxWhenNoAdminExists() throws Exception {
        // WHEN, THEN
        // The registration form shows the admin checkbox when no admin exists
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register as administrator")));
    }

    @Test
    public void registroFormHidesAdminCheckboxWhenAdminExists() throws Exception {
        // GIVEN
        // An admin user exists
        addAdminBD();

        // WHEN, THEN
        // The registration form does NOT show the admin checkbox
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Register as administrator"))));
    }

    @Test
    public void registroSubmitCreatesAdminUserWhenNoAdminExists() throws Exception {
        // WHEN
        // Submit registration form with admin checkbox checked
        this.mockMvc.perform(post("/registro")
                .param("email", "admin@umh.es")
                .param("password", "admin123")
                .param("nombre", "Administrator")
                .param("admin", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // THEN
        // Admin user is created
        UsuarioData admin = usuarioService.findByEmail("admin@umh.es");
        assert admin != null;
        assert admin.getAdmin() == true;
    }

    @Test
    public void registroSubmitRejectsAdminRegistrationWhenAdminExists() throws Exception {
        // GIVEN
        // An admin user already exists
        addAdminBD();

        // WHEN, THEN
        // Submit registration form with admin checkbox checked fails
        this.mockMvc.perform(post("/registro")
                .param("email", "admin2@umh.es")
                .param("password", "admin456")
                .param("nombre", "Admin 2")
                .param("admin", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("An administrator already exists in the system")));
    }

    @Test
    public void loginRedirectsAdminToUserList() throws Exception {
        // GIVEN
        // An admin user exists
        addAdminBD();

        // WHEN, THEN
        // Login as admin redirects to user list
        this.mockMvc.perform(post("/login")
                .param("eMail", "admin@umh.es")
                .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/registered"));
    }

    @Test
    public void loginRedirectsRegularUserToTasks() throws Exception {
        // GIVEN
        // A regular user exists
        Long userId = addUsuarioBD();

        // WHEN, THEN
        // Login as regular user redirects to tasks page
        this.mockMvc.perform(post("/login")
                .param("eMail", "lana@umh.es")
                .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/" + userId + "/tareas"));
    }
}
