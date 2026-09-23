package monokai.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Usuario {

    private Long id;
    private String username;
    private String senha;
    private List<Role> roles;
}
