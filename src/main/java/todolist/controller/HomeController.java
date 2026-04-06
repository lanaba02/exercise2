package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import todolist.controller.exception.UsuarioNoAdminException;

@Controller
public class HomeController {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

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

    @GetMapping("/about")
    public String about(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();

        // If user is logged in, pass user information to the template
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("usuario", usuario);
            model.addAttribute("estaLogeado", true);
        } else {
            model.addAttribute("estaLogeado", false);
        }

        return "about";
    }

    @GetMapping("/registered")
    public String registeredUsers(Model model) {
        comprobarUsuarioAdmin();
        Iterable<UsuarioData> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "registered";
    }

    @GetMapping("/registered/{id}")
    public String userDescription(@PathVariable Long id, Model model) {
        comprobarUsuarioAdmin();
        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            return "redirect:/registered";
        }
        model.addAttribute("usuario", usuario);
        return "userDescription";
    }

    @PostMapping("/registered/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id) {
        comprobarUsuarioAdmin();
        usuarioService.toggleUserEnabled(id);
        return "redirect:/registered";
    }
}