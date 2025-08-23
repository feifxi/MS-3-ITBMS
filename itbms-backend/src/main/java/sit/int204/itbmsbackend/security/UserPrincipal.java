package sit.int204.itbmsbackend.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sit.int204.itbmsbackend.entities.Role;
import sit.int204.itbmsbackend.entities.User;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {
    private final User user;
    private final Integer id;
    private final String email;
    private final String password;
    private final Set<Role> roles = new LinkedHashSet<>();
    private final boolean isEnabled;

    public UserPrincipal(User user) {
        this.user = user;
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.isEnabled = user.getStatus() != null && user.getStatus().equalsIgnoreCase("ACTIVE");
        this.roles.addAll(user.getRoles());
    }

    // Spring Security will use these method for authentication
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
