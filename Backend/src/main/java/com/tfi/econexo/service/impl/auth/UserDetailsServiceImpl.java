package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.auth.AuthLoginRequestDTO;
import com.tfi.econexo.dto.auth.AuthResponseDTO;
import com.tfi.econexo.entity.security.UserSec;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Traer user de la base de datos
        UserSec userSec = userRepository.findUserEntityByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //Crear lista para los permisos
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        //Traer roles y convertirlos en SimpleGrantedAuthorities
        userSec.getRolesList()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole()))));

        //Traer permisos y convertirlos en SimpleGrantedAuthorities
        userSec.getRolesList().stream()
                .flatMap(role -> role.getPermissionsList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        return new User(
                userSec.getEmail(),
                userSec.getPassword(),
                userSec.isEnabled(),
                userSec.isAccountNonExpired(),
                userSec.isAccountNonLocked(),
                userSec.isCredentialNonExpired(),
                authorityList);
    }

    public AuthResponseDTO loginUser(@Valid AuthLoginRequestDTO authLoginRequest) {

        String username = authLoginRequest.email();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = null;
        if (authentication != null) {
            accessToken = jwtUtils.createToken(authentication);
        }

        return new AuthResponseDTO(username, "Login successful", accessToken, true);
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = this.loadUserByUsername(username);

        if(userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
