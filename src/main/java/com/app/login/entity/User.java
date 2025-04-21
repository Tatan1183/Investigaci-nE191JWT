package com.app.login.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "tbl_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // --- Constructor sin argumentos (requerido por JPA) ---
    public User() {
    }

    // --- Constructor para usar en el registro ---
    public User(String nombre, String apellido, String email, String password, Role role) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password; // La contraseña ya debe venir codificada
        this.role = role;
    }

    // --- Getters y Setters para todos los campos ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() { // Getter para Role
        return role;
    }

    public void setRole(Role role) { // Setter para Role
        this.role = role;
    }

    // --- Métodos de UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {// Manejo de caso null
            System.out.println("DEBUG: User role is NULL, returning empty authorities.");
            return List.of();
        }
        String authorityString = "ROLE_" + role.name();
        System.out.println("DEBUG: User role is " + role.name() + ", returning authority: " + authorityString);
        return List.of(new SimpleGrantedAuthority(authorityString));
    }

    @Override
    public String getUsername() {
        return email; // Usas email como username
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
