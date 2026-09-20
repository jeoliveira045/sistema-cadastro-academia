package monokai.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service("listUserDetailService")
class ListUserDetailService implements UserDetailsService{

    private final PasswordEncoder passwordEncoder;
    Map<String, UserDetails> userMap = new ConcurrentHashMap<>();

    public ListUserDetailService(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userMap.put("us1", new ConcreteUserDetails("us1", this.passwordEncoder.encode("123")));
        this.userMap.put("us2", new ConcreteUserDetails("us2", this.passwordEncoder.encode("456")));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String usernameUserDetails = userMap
                                        .keySet()
                                        .stream()
                                        .filter(user -> user.equals(username))
                                        .findAny()
                                        .orElseThrow(() -> new UsernameNotFoundException("usuario não encotrado"));
        return userMap.get(usernameUserDetails);
    }

}


class ConcreteUserDetails implements UserDetails{
    String username;
    String password;

    public ConcreteUserDetails(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> {
            return "ADMIN";
        });
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


}