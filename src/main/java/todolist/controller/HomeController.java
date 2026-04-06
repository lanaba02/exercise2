package todolist.controller;

import todolist.authentication.ManagerUserSession;
import todolist.dto.UsuarioData;
import todolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class HomeController {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

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
        Iterable<UsuarioData> usuarios = usuarioService.findAll();
        model.addAttribute("usuarios", usuarios);
        return "registered";
    }

    @GetMapping("/registered/{id}")
    public String userDescription(@PathVariable Long id, Model model) {
        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            return "redirect:/registered";
        }
        model.addAttribute("usuario", usuario);
        return "userDescription";
    }
}