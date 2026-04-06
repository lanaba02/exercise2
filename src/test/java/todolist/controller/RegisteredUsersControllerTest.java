package todolist.controller;

import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql")
public class RegisteredUsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

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
    public void registeredUsersPageShowsUserList() throws Exception {
        // GIVEN
        // Two users in the database
        Long usuarioId1 = addUsuarioBD();

        UsuarioData usuario2 = new UsuarioData();
        usuario2.setEmail("richard@umh.es");
        usuario2.setNombre("Richard Stallman");
        usuario2.setPassword("5678");
        UsuarioData nuevoUsuario2 = usuarioService.registrar(usuario2);
        Long usuarioId2 = nuevoUsuario2.getId();

        // WHEN, THEN
        // The registered users page is requested and contains both users
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("Registered Users"),  // Page title
                        containsString("lana@umh.es"),        // First user email
                        containsString("richard@umh.es"),     // Second user email
                        containsString(usuarioId1.toString()), // First user ID
                        containsString(usuarioId2.toString())  // Second user ID
                )));
    }

    @Test
    public void registeredUsersPageShowsEmptyMessageWhenNoUsers() throws Exception {
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
        // A user in the database
        addUsuarioBD();

        // WHEN, THEN
        // The registered users page contains proper table structure
        this.mockMvc.perform(get("/registered"))
                .andExpect(status().isOk())
                .andExpect(content().string(allOf(
                        containsString("<table"),     // Table element
                        containsString("<thead"),     // Table header
                        containsString("<tbody"),     // Table body
                        containsString("ID"),         // ID column header
                        containsString("Email")       // Email column header
                )));
    }
}
