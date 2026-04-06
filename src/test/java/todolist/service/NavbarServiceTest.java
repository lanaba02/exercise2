package todolist.service;

import todolist.dto.UsuarioData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class NavbarServiceTest {

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
    public void servicioObtenerUsuarioPorId() {
        // GIVEN
        // A user in the database
        Long usuarioId = addUsuarioBD();

        // WHEN
        // We retrieve the user by ID
        UsuarioData usuarioRecuperado = usuarioService.findById(usuarioId);

        // THEN
        // The user data is correctly retrieved and matches the registered user
        assertThat(usuarioRecuperado).isNotNull();
        assertThat(usuarioRecuperado.getId()).isEqualTo(usuarioId);
        assertThat(usuarioRecuperado.getNombre()).isEqualTo("Lana Barisic");
        assertThat(usuarioRecuperado.getEmail()).isEqualTo("lana@umh.es");
    }

    @Test
    public void servicioObtenerUsuarioNombrePorId() {
        // GIVEN
        // A user in the database
        Long usuarioId = addUsuarioBD();

        // WHEN
        // We retrieve the user by ID
        UsuarioData usuarioRecuperado = usuarioService.findById(usuarioId);

        // THEN
        // The user name is correctly retrieved for displaying in navbar
        assertThat(usuarioRecuperado.getNombre()).isNotNull();
        assertThat(usuarioRecuperado.getNombre()).isNotEmpty();
        assertThat(usuarioRecuperado.getNombre()).isEqualTo("Lana Barisic");
    }

    @Test
    public void servicioObtenerUsuarioPorIdNoExistente() {
        // WHEN
        // We try to retrieve a user that doesn't exist
        UsuarioData usuarioRecuperado = usuarioService.findById(999L);

        // THEN
        // The result is null
        assertThat(usuarioRecuperado).isNull();
    }

    @Test
    public void servicioVerificaQueUsuarioTieneEmail() {
        // GIVEN
        // A user in the database
        Long usuarioId = addUsuarioBD();

        // WHEN
        // We retrieve the user by ID
        UsuarioData usuarioRecuperado = usuarioService.findById(usuarioId);

        // THEN
        // The user has a valid email
        assertThat(usuarioRecuperado.getEmail()).isNotNull();
        assertThat(usuarioRecuperado.getEmail()).isNotEmpty();
        assertThat(usuarioRecuperado.getEmail()).isEqualTo("lana@umh.es");
    }

    @Test
    public void servicioLoginExitosoParaNavbarAutenticacion() {
        // GIVEN
        // A user in the database
        addUsuarioBD();

        // WHEN
        // We try to log in with correct credentials
        UsuarioService.LoginStatus loginStatus = usuarioService.login("lana@umh.es", "1234");

        // THEN
        // The login is successful (needed for navbar to display)
        assertThat(loginStatus).isEqualTo(UsuarioService.LoginStatus.LOGIN_OK);

        // AND we can retrieve the user to display in navbar
        UsuarioData usuarioRecuperado = usuarioService.findByEmail("lana@umh.es");
        assertThat(usuarioRecuperado).isNotNull();
        assertThat(usuarioRecuperado.getNombre()).isEqualTo("Lana Barisic");
    }
}

