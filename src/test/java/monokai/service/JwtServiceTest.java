package monokai.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "chave-secreta-de-teste-com-minimo-256-bits!!";
    private static final long EXPIRATION = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
    }

    @Test
    void deveGerarTokenComSucesso() {
        UserDetails user = buildUser("joao.silva", "ADMIN");

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
    }

    @Test
    void tokenDeveConterUsernameCorreto() {
        UserDetails user = buildUser("joao.silva", "ADMIN");

        String token = jwtService.generateToken(user);
        Claims claims = parseToken(token);

        assertThat(claims.getSubject()).isEqualTo("joao.silva");
    }

    @Test
    void tokenDeveConterRolesCorretas() {
        UserDetails user = buildUser("joao.silva", "ADMIN", "PROFESSOR");

        String token = jwtService.generateToken(user);
        Claims claims = parseToken(token);

        List<String> roles = claims.get("roles", List.class);
        assertThat(roles).containsExactlyInAnyOrder("ADMIN", "PROFESSOR");
    }

    @Test
    void tokenDeveConterDataDeExpiracaoFutura() {
        UserDetails user = buildUser("joao.silva", "ADMIN");

        String token = jwtService.generateToken(user);
        Claims claims = parseToken(token);

        assertThat(claims.getExpiration()).isInTheFuture();
    }

    @Test
    void tokenDeveConterDataDeEmissao() {
        UserDetails user = buildUser("joao.silva", "ADMIN");

        String token = jwtService.generateToken(user);
        Claims claims = parseToken(token);

        assertThat(claims.getIssuedAt()).isNotNull();
    }

    @Test
    void deveLancarExcecaoComTokenInvalido() {
        SecretKey outraChave = Keys.hmacShaKeyFor(
                "outra-chave-completamente-diferente-256bits!!".getBytes(StandardCharsets.UTF_8));

        UserDetails user = buildUser("joao.silva", "ADMIN");
        String token = jwtService.generateToken(user);

        assertThatThrownBy(() ->
                Jwts.parser().verifyWith(outraChave).build().parseSignedClaims(token))
                .isInstanceOf(Exception.class);
    }

    private UserDetails buildUser(String username, String... roles) {
        List<SimpleGrantedAuthority> authorities = List.of(roles).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new User(username, "senha", authorities);
    }

    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}
