package com.sendero.backend.service;
import com.sendero.backend.dto.AuthRequest;
import com.sendero.backend.dto.AuthResponse;
import com.sendero.backend.dto.RegisterRequest;
import com.sendero.backend.model.User;
import com.sendero.backend.repository.UserRepository;
import com.sendero.backend.security.JwtUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.ConstructorParameters;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditoriaService auditoriaService;
    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            auditoriaService.registrar(
                    request.getUsername(),
                    "REGISTRO_FALLIDO",
                    "User",
                    null,
                    "Nombre de usuario ya en uso"
            );
            throw new RuntimeException("El nombre de usuario ya está en uso.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            auditoriaService.registrar(
                    request.getUsername(),
                    "REGISTRO_FALLIDO",
                    "User",
                    null,
                    "Correo electrónico ya en uso"
            );
            throw new RuntimeException("El correo ya está en uso.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        auditoriaService.registrar(
                user.getUsername(),
                "REGISTRO",
                "User",
                user.getId().toString(),
                "Usuario registrado exitosamente"
        );
        return jwtUtil.generateToken(user.getUsername());
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (Exception ex) {
            auditoriaService.registrar(
                    request.getUsername(),
                    "LOGIN_FALLIDO",
                    "User",
                    null,
                    "Credenciales inválidas"
            );
            throw new RuntimeException("Credenciales inválidas");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        String token = jwtUtil.generateToken(user.getUsername());

        auditoriaService.registrar(
                user.getUsername(),
                "LOGIN",
                "User",
                user.getId().toString(),
                "Inicio de sesión exitoso"
        );

        return new AuthResponse(token);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role.toUpperCase());
    }

    public User updateUser(Integer id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        return userRepository.save(user);
    }
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        userRepository.deleteById(id);

        auditoriaService.registrar(
                user.getUsername(),
                "ELIMINAR",
                "User",
                id.toString(),
                "Usuario eliminado: " + user.getUsername()
        );
    }


    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
