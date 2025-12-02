package com.envyguard.backend.service;

import com.envyguard.backend.dto.LoginRequest;
import com.envyguard.backend.dto.LoginResponse;
import com.envyguard.backend.entity.User;
import com.envyguard.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para manejar autenticación y registro de usuarios.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param email Email del usuario
     * @param password Contraseña sin encriptar
     * @param firstName Nombre del usuario
     * @param lastName Apellido del usuario
     * @return Usuario creado
     * @throws IllegalArgumentException Si el email ya existe
     */
    @Transactional
    public User register(String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    /**
     * Autentica un usuario y genera un token JWT.
     *
     * @param request Solicitud de login con email y password
     * @return LoginResponse con token JWT y datos del usuario
     * @throws org.springframework.security.core.AuthenticationException Si las credenciales son inválidas
     */
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
