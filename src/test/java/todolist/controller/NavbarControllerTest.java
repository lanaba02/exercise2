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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class NavbarControllerTest {

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

    @Test
    public void navbarShowsWhenUserIsLoggedInOnAboutPage() throws Exception {
        // GIVEN
        // A user in the database
        Long usuarioId = addUsuarioBD();

        // When user is logged in
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // The about page is requested and contains the navbar with username dropdown
        this.mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("ToDoList"),  // Brand name
                        containsString("dropdown"),   // Dropdown element
                        containsString("Lana Barisic") // Username
                )));
    }

    @Test
    public void navbarShowsLoginRegistrationLinksWhenUserNotLoggedInOnAboutPage() throws Exception {
        // GIVEN
        // No user is logged in
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        // The about page is requested and contains login and registration links
        this.mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("ToDoList"),     // Brand name
                        containsString("/login"),       // Login link
                        containsString("/registro")     // Registration link
                )));
    }

    @Test
    public void navbarHasTasksLinkWhenUserIsLoggedIn() throws Exception {
        // GIVEN
        // A user in the database
        Long usuarioId = addUsuarioBD();

        // When user is logged in
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // The about page contains the Tasks link when user is logged in
        this.mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tasks")));
    }

    @Test
    public void navbarHasLogoutOptionInDropdown() throws Exception {
        // GIVEN
        // A user in the database
        Long usuarioId = addUsuarioBD();

        // When user is logged in
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // The about page contains logout link in the dropdown
        this.mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("/logout"),      // Logout link
                        containsString("Log out")       // Logout text
                )));
    }

    @Test
    public void navbarAppearsOnTasksPage() throws Exception {
        // GIVEN
        // A user in the database with tasks
        Long usuarioId = addUsuarioBD();

        // When user is logged in
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        // WHEN, THEN
        // The tasks page contains the navbar
        String url = "/usuarios/" + usuarioId.toString() + "/tareas";
        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("ToDoList"),  // Brand name
                        containsString("Tasks"),     // Tasks link
                        containsString("Lana Barisic") // Username
                )));
    }

    @Test
    public void navbarDoesNotAppearOnLoginPage() throws Exception {
        // WHEN, THEN
        // The login page does NOT contain the navbar
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("navbar"))));
    }

    @Test
    public void navbarDoesNotAppearOnRegistrationPage() throws Exception {
        // WHEN, THEN
        // The registration page does NOT contain the navbar
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("navbar"))));
    }
}


