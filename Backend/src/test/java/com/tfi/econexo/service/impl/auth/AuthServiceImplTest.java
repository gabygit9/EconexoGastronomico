package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.auth.donor.DonorRegistrationDTO;
import com.tfi.econexo.dto.auth.donor.DonorResponseDTO;
import com.tfi.econexo.dto.auth.logistics.DriverRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.DriverResponseDTO;
import com.tfi.econexo.dto.auth.logistics.VehicleRegistrationDTO;
import com.tfi.econexo.dto.auth.logistics.VehicleResponseDTO;
import com.tfi.econexo.dto.auth.ngo.NgoRegistrationDTO;
import com.tfi.econexo.dto.auth.ngo.NgoResponseDTO;
import com.tfi.econexo.exception.ConflictException;
import com.tfi.econexo.mappers.*;
import com.tfi.econexo.model.auth.Role;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.model.location.Neighborhood;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.model.logistics.Vehicle;
import com.tfi.econexo.model.logistics.VehicleType;
import com.tfi.econexo.model.ngo.Ngo;
import com.tfi.econexo.service.DonorService;
import com.tfi.econexo.service.DriverService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    private DriverService driverService;

    @Mock
    private DonorMapper donorMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private NgoMapper ngoMapper;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private VehicleMapper vehicleMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    Role role;
    Neighborhood alberdi;
    UserSec user;

    DonorRegistrationDTO donorDTO;
    DonorResponseDTO donorResponseDTO;
    Donor donor;

    NgoRegistrationDTO ngoDTO;
    NgoResponseDTO ngoResponseDTO;
    Ngo ngo;

    DriverRegistrationDTO driverDto;
    DriverResponseDTO driverResponseDTO;
    Driver driver;

    VehicleRegistrationDTO vehicleDto;
    VehicleResponseDTO vehicleResponseDTO;
    Vehicle vehicle;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRole("DONOR");

        alberdi = new Neighborhood();
        alberdi.setId(1L);
        alberdi.setName("alberdi");

        user = new UserSec();
        user.setId(1L);

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
                1L,
                "PENDING");

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
                1L,
                "PENDING"
        );

        ngo = new Ngo();
        ngo.setTaxId(ngoResponseDTO.taxId());
        ngo.setLegalPersonalityNumber(ngoResponseDTO.legalPersonalityNumber());

        vehicleDto = new VehicleRegistrationDTO(
                VehicleType.CAR,
                true,
                1000,
                "AA123CC",
                "www.url.com",
                "www.url1.com",
                LocalDate.of(2029, 4,12)
        );

        vehicleResponseDTO = new VehicleResponseDTO(
                1L,
                VehicleType.CAR,
                true,
                1000,
                "AA123CC",
                LocalDate.of(2029, 4,12)
        );

        driverDto = new DriverRegistrationDTO(
                "Ana",
                "Perez",
                "235039876522",
                LocalDate.of(1990, 4, 12),
                "ana@mail.com",
                "Pass123",
                "www.url.com",
                LocalDate.of(2027, 5, 30),
                vehicleDto,
                "354325543223",
                "Obispo Trejo",
                "440",
                "PB",
                "A",
                -32.00393,
                -64.999332,
                1L);

        driverResponseDTO = new DriverResponseDTO(
                1L,
                "Ana",
                "Perez",
                "ana@mail.com",
                "223443442140",
                "344215534343",
                LocalDate.of(1990,5,30),
                String.valueOf(RegistrationStatus.PENDING),
                LocalDate.of(2027,5,30),
                "Obispo Trejo",
                "440",
                "PB",
                "A",
                "Nueva Córdoba",
                List.of(vehicleResponseDTO));

        driver = new Driver();
        driver.setTaxId(driverResponseDTO.taxId());

        vehicle = new Vehicle();
        vehicle.setDriver(driver);
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

        assertThrows(ConflictException.class, () -> authService.registerDonor(donorDTO));
    }

    @Test
    void registerDonor_ThrowsException_WhenTaxIdExists(){
        when(donorService.findByTaxId(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.registerDonor(donorDTO));
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
    @Test
    void registerDriver_WhenAgeIsLessThan18_ThrowsIllegalArgumentException() {
        DriverRegistrationDTO dto = new DriverRegistrationDTO("Ana",
                "Perez",
                "235039876522",
                LocalDate.now().minusYears(17),
                "ana@mail.com",
                "Pass123",
                "www.url.com",
                LocalDate.of(1990, 4, 12),
                vehicleDto,
                "354325543223",
                "Obispo Trejo",
                "440",
                "PB",
                "A",
                -32.00393,
                -64.999332,
                1L);

        assertThrows(IllegalArgumentException.class, () -> authService.registerDriver(dto));
    }

    @Test
    void registerDriver_WhenTaxIdExists_ThrowsConflictException() {
        when(driverService.findByTaxId(anyString())).thenReturn(Optional.of(driver));

        assertThrows(ConflictException.class, () -> authService.registerDriver(driverDto));

        verify(userService, never()).save(any());
    }
    //Caso Camino Feliz: Datos válidos. Debe verificar que se llamen a los métodos .save() del UserService y del DriverRepository exactamente una vez.
    @Test
    void registerDriver_WhenDataIsCorrect_VerifySaveOnce() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(driverService.findByTaxId(anyString())).thenReturn(Optional.empty());
        when(neighborhoodService.findById(anyLong())).thenReturn(Optional.of(alberdi));
        when(roleService.findByName(anyString())).thenReturn(Optional.of(role));
        when(driverMapper.toEntity(any(), any(), any())).thenReturn(driver);
        when(driverMapper.toResponseDTO(any())).thenReturn(driverResponseDTO);
        when(vehicleMapper.toEntity(any())).thenReturn(vehicle);

        DriverResponseDTO response = authService.registerDriver(driverDto);

        verify(driverService).save(any(Driver.class));
        assertNotNull(response);
        assertEquals(driverResponseDTO.taxId(), response.taxId());
    }


}