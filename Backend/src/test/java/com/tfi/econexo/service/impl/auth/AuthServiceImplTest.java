package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.NgoRegistrationDTO;
import com.tfi.econexo.dto.NgoResponseDTO;
import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.entity.donation.Donor;
import com.tfi.econexo.entity.location.Neighborhood;
import com.tfi.econexo.entity.ngo.Ngo;
import com.tfi.econexo.entity.security.Role;
import com.tfi.econexo.entity.security.UserSec;
import com.tfi.econexo.exception.ConflictException;
import com.tfi.econexo.mappers.DonorMapper;
import com.tfi.econexo.mappers.NgoMapper;
import com.tfi.econexo.mappers.UserMapper;
import com.tfi.econexo.repository.location.NeighborhoodRepository;
import com.tfi.econexo.service.DonorService;
import com.tfi.econexo.service.NeighborhoodService;
import com.tfi.econexo.service.NgoService;
import com.tfi.econexo.service.auth.RoleService;
import com.tfi.econexo.service.auth.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private DonorService donorService;

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @Mock
    private NgoService ngoService;

    @Mock
    private NeighborhoodService neighborhoodService;

    @Mock
    private DonorMapper donorMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private NgoMapper ngoMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    Role role;
    Neighborhood alberdi;
    DonorRegistrationDTO donorDTO;
    DonorResponseDTO donorResponseDTO;
    Donor donor;
    NgoRegistrationDTO ngoDTO;
    NgoResponseDTO ngoResponseDTO;
    Ngo ngo;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRole("DONOR");

        alberdi = new Neighborhood();
        alberdi.setId(1L);
        alberdi.setName("alberdi");

        donorDTO = new DonorRegistrationDTO(
        "test@mail.com",
        "12345678",
        "Hornito Santiagueño",
        "Hornito Alimentos SRL",
        "30712345678",
        "351155123456",
        "Av. Hipólito Yrigoyen",
        "450",
        "PB",
        "A",
        "RESTAURANT",
        -31.4233,
        -69.3214,
        1L);

        donorResponseDTO = new DonorResponseDTO(
                1L,
                "test@mail.com",
                "Hornito Santiagueño",
                "Hornito Alimentos SRL",
                "30712345678",
                "351155123456",
                "Av. Hipólito Yrigoyen",
                "450",
                "PB",
                "A",
                1L);

        donor = new Donor();
        donor.setTaxId("30712345678");
        donor.setLegalName("Hornito Alimentos SRL");

        ngoDTO = new NgoRegistrationDTO(
                "Comedor Caritas Felices",
                "22589875879",
                "87889987",
                "María Gómez",
                "Bv. San Juan",
                "111",
                "1",
                "A",
                "3542343455",
                1L,
                40.444,
                23.000,
                "test@mail.com",
                "12345678",
                "SHELTER");

        ngoResponseDTO = new NgoResponseDTO(
                1L,
                "test@email.com",
                "Comedor Caritas Felices",
                "87889987",
                "22589875879",
                "María Gómez",
                "3542343455",
                "Bv. San Juan",
                "111",
                "1",
                "A",
                1L
        );

        ngo = new Ngo();
        ngo.setTaxId(ngoResponseDTO.taxId());
        ngo.setLegalPersonalityNumber(ngoResponseDTO.legalPersonalityNumber());
    }

    @Test
    void registerDonor_success() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(donorService.findByTaxId(anyString())).thenReturn(false);
        when(roleService.findByName(anyString())).thenReturn(Optional.of(role));
        when(neighborhoodService.findById(anyLong())).thenReturn(Optional.of(alberdi));
        when(donorMapper.toEntity(any(), any(), any())).thenReturn(donor);
        when(donorMapper.toResponseDTO(any())).thenReturn(donorResponseDTO);

        DonorResponseDTO response = authService.registerDonor(donorDTO);

        verify(donorService).save(any(Donor.class));
        assertNotNull(response);
        assertEquals(donorResponseDTO.legalName(), response.legalName());
        assertEquals(donorResponseDTO.taxId(), response.taxId());
    }

    @Test
    void registerDonor_ThrowsException_WhenEmailExists() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(new UserSec()));

        assertThrows(IllegalArgumentException.class, () -> authService.registerDonor(donorDTO));
    }

    @Test
    void registerDonor_ThrowsException_WhenTaxIdExists(){
        when(donorService.findByTaxId(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.registerDonor(donorDTO));
    }

    @Test
    void registerDonor_ThrowsException_WhenRoleNotFound(){
        when(roleService.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.registerDonor(donorDTO));
    }

    @Test
    void registerDonor_ThrowsException_WhenNeighborhoodNotFound(){
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(donorService.findByTaxId(anyString())).thenReturn(false);
        when(roleService.findByName(anyString())).thenReturn(Optional.of(role));
        when(neighborhoodService.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.registerDonor(donorDTO));
    }

    @Test
    void registerNgo_success() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(ngoService.findByTaxId(anyString())).thenReturn(Optional.empty());
        when(ngoService.findByLegalPersonalityNumber(anyString())).thenReturn(Optional.empty());
        when(neighborhoodService.findById(anyLong())).thenReturn(Optional.of(alberdi));
        when(roleService.findByName(anyString())).thenReturn(Optional.of(role));
        when(ngoMapper.toEntity(any(), any(), any())).thenReturn(ngo);
        when(ngoMapper.toResponseDTO(any())).thenReturn(ngoResponseDTO);


        NgoResponseDTO response = authService.registerNgo(ngoDTO);

        verify(ngoService).save(any(Ngo.class));
        assertNotNull(response);
        assertEquals(ngoResponseDTO.legalPersonalityNumber(), response.legalPersonalityNumber());
        assertEquals(ngoResponseDTO.taxId(), response.taxId());
    }

    @Test
    void registerNgo_ThrowsException_WhenEmailExists() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(new UserSec()));

        assertThrows(ConflictException.class, () -> authService.registerNgo(ngoDTO));

        verify(userService, never()).save(any());
        verify(ngoService, never()).save(any());
    }

    @Test
    void registerNgo_WhenTaxIdExists_ThrowsConflictException(){
        when(ngoService.findByTaxId(anyString())).thenReturn(Optional.of(ngo));

        assertThrows(ConflictException.class, () -> authService.registerNgo(ngoDTO));

        verify(userService, never()).save(any());
    }

    @Test
    void registerNgo_WhenLegalPersonalityNumberExists_ThrowsConflictException(){
        when(ngoService.findByLegalPersonalityNumber(anyString())).thenReturn(Optional.of(ngo));

        assertThrows(ConflictException.class, () -> authService.registerNgo(ngoDTO));

        verify(userService, never()).save(any());
        verify(ngoService, never()).save(any());
    }
}