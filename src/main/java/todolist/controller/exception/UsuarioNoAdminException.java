package todolist.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason="Insufficient permissions - Administrator access required")
public class UsuarioNoAdminException extends RuntimeException {
}
