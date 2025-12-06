package com.ticketblock.entity;

import com.ticketblock.entity.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a user in the ticketing system.
 * 
 * This entity implements Spring Security's UserDetails interface to support
 * authentication and authorization. Users can have roles (USER, ORGANIZER, ADMIN)
 * and can own tickets or organize events. The email field serves as the unique
 * username for authentication.
 */
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Integer userId;

    public static final int MAX_EMAIL_LENGTH = 50;
    public static final int MAX_NAME_LENGTH = 50;

    @Column(unique = true, nullable = false, length = MAX_EMAIL_LENGTH)
    private String email;
    @Column(nullable = false) //default length is  255
    private String password;
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String firstName;
    @Column(nullable = false, length = MAX_NAME_LENGTH)
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner")
    private Set<Ticket> tickets;

    @OneToMany(mappedBy = "organizer")
    private Set<Event> events;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));

    }

    @Override
    public String getUsername() {
        return email;
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
        return true;
    }
}
