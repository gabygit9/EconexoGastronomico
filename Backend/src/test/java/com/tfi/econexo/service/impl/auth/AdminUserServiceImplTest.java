package com.tfi.econexo.service.impl.auth;

import com.tfi.econexo.dto.auth.admin.UserAdminResponseDTO;
import com.tfi.econexo.model.auth.Role;
import com.tfi.econexo.model.auth.UserSec;
import com.tfi.econexo.model.donation.donor.Donor;
import com.tfi.econexo.model.enums.RegistrationStatus;
import com.tfi.econexo.model.logistics.Driver;
import com.tfi.econexo.model.ngo.Ngo;
import com.tfi.econexo.repository.auth.UserRepository;
import com.tfi.econexo.repository.donation.DonorRepository;
import com.tfi.econexo.repository.logistics.DriverRepository;
import com.tfi.econexo.repository.ngo.NgoRepository;
import com.tfi.econexo.utils.notification.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceImplTest {

    @Mock private DonorRepository donorRepository;
    @Mock private NgoRepository ngoRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmailService emailService;

    @InjectMocks private AdminUserServiceImpl adminUserServiceImpl;

    UserAdminResponseDTO userDto;
    Donor donor;
    Ngo ngo;
    Driver driver;


    @BeforeEach
    public void setUp(){
        userDto = new UserAdminResponseDTO(1L,"name","email","role","status", LocalDateTime.now(), null, null, null, null, null);

        donor = new Donor();
        donor.setTradeName("Hornito Santiagueño");
        donor.setUser(new UserSec("donor@mail.com", "llllllll", true, true, true, true, Set.of(new Role("DONOR", Set.of()))));
        donor.getUser().setId(1L);
        donor.setCreatedDate(LocalDateTime.now().minusDays(5));
        donor.setStatus(RegistrationStatus.APPROVED);

        ngo = new Ngo();
        ngo.setNgoName("Caritas");
        ngo.setUser(new UserSec("ngo@mail.com", "llllllll", true, true, true, true, Set.of(new Role("NGO", Set.of()))));
        ngo.getUser().setId(2L);
        ngo.setCreatedDate(LocalDateTime.now().minusDays(2));
        ngo.setStatus(RegistrationStatus.APPROVED);

        driver = new Driver();
        driver.setFirstName("Max");
        driver.setLastName("Carrasco");
        driver.setUser(new UserSec("driver@mail.com", "llllllll", true, true, true, true, Set.of(new Role("DRIVER", Set.of()))));
        driver.getUser().setId(3L);
        driver.setCreatedDate(LocalDateTime.now());
        driver.setStatus(RegistrationStatus.APPROVED);
    }

    @Test
    public void testGetAllRegisteredUsers_happyPath() {
        when(donorRepository.findAll()).thenReturn(List.of(donor));
        when(driverRepository.findAll()).thenReturn(List.of(driver));
        when(ngoRepository.findAll()).thenReturn(List.of(ngo));

        List<UserAdminResponseDTO> users = adminUserServiceImpl.getAllRegisteredUsers();

        assertEquals(3, users.size());
        assertEquals("Hornito Santiagueño", users.get(2).name());
        assertEquals("ngo@mail.com", users.get(1).email());
        assertEquals("DRIVER", users.get(0).userType());
    }

    @Test
    public void getAllRegisteredUsers_WhenRepositoriesAreEmpty_ReturnAnEmptyList(){
        when(donorRepository.findAll()).thenReturn(List.of());
        when(driverRepository.findAll()).thenReturn(List.of());
        when(ngoRepository.findAll()).thenReturn(List.of());

        List<UserAdminResponseDTO> users = adminUserServiceImpl.getAllRegisteredUsers();

        assertTrue(users.isEmpty());
    }

    @Test
    public void testUpdateUserStatusByRole_Donor() {
        donor.setStatus(RegistrationStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(donor.getUser()));
        when(donorRepository.findByUser_Email(anyString())).thenReturn(Optional.of(donor));

        adminUserServiceImpl.updateUserStatus(1L, RegistrationStatus.SUSPENDED);

        verify(donorRepository, times(1)).save(any());
        assertEquals(RegistrationStatus.SUSPENDED, donor.getStatus());
    }

    @Test
    public void testUpdateUserStatusByRole_Ngo() {
        ngo.setStatus(RegistrationStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(ngo.getUser()));
        when(ngoRepository.findByUser_Email(anyString())).thenReturn(Optional.of(ngo));

        adminUserServiceImpl.updateUserStatus(1L, RegistrationStatus.SUSPENDED);

        verify(ngoRepository, times(1)).save(any());
        assertEquals(RegistrationStatus.SUSPENDED, ngo.getStatus());
    }

    @Test
    public void testUpdateUserStatusByRole_Driver() {
        driver.setStatus(RegistrationStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(driver.getUser()));
        when(driverRepository.findByUser_Email(anyString())).thenReturn(Optional.of(driver));

        adminUserServiceImpl.updateUserStatus(1L, RegistrationStatus.SUSPENDED);

        verify(driverRepository, times(1)).save(any());
        assertEquals(RegistrationStatus.SUSPENDED, driver.getStatus());
    }

    @Test
    public void testUpdateUserStatus_WhenUserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> adminUserServiceImpl.updateUserStatus(1L, RegistrationStatus.SUSPENDED));
        verifyNoInteractions(donorRepository, driverRepository, ngoRepository);
    }

    @Test
    public void updateUserStatus_WhenUserRoleIsAdmin_ShouldThrowIllegalArgumentException() {
        UserSec admin = new UserSec("admin@mail.com", "llllllll", true, true, true, true, Set.of(new Role("ADMIN", Set.of())));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(admin));

        assertThrows(IllegalArgumentException.class,
                () -> adminUserServiceImpl.updateUserStatus(1L, RegistrationStatus.SUSPENDED));
    }

    @Test
    public void updateUserStatus_WhenUserExistsButHisProfileDoesntExist() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(donor.getUser()));
        when(donorRepository.findByUser_Email(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> adminUserServiceImpl.updateUserStatus(1L, RegistrationStatus.SUSPENDED));

        verify(donorRepository, never()).save(any());
    }

    @Test
    public void testUpdateUserStatus_WhenApproved_ShouldTriggerEmail_Donor(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(donor.getUser()));
        when(donorRepository.findByUser_Email(anyString())).thenReturn(Optional.of(donor));

        adminUserServiceImpl.updateUserStatus(donor.getUser().getId(), RegistrationStatus.APPROVED);

        assertEquals(RegistrationStatus.APPROVED, donor.getStatus());
        verify(donorRepository, times(1)).save(donor);

        verify(emailService, times(1)).sendApprovalEmail(donor.getUser().getEmail(), donor.getTradeName(), "DONOR");
    }

    @Test
    public void testUpdateUserStatus_WhenApproved_ShouldTriggerEmail_Ngo(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(ngo.getUser()));
        when(ngoRepository.findByUser_Email(anyString())).thenReturn(Optional.of(ngo));

        adminUserServiceImpl.updateUserStatus(ngo.getUser().getId(), RegistrationStatus.APPROVED);

        assertEquals(RegistrationStatus.APPROVED, ngo.getStatus());
        verify(ngoRepository, times(1)).save(ngo);

        verify(emailService, times(1)).sendApprovalEmail(ngo.getUser().getEmail(), ngo.getNgoName(), "NGO");
    }

    @Test
    public void testUpdateUserStatus_WhenApproved_ShouldTriggerEmail_Driver(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(driver.getUser()));
        when(driverRepository.findByUser_Email(anyString())).thenReturn(Optional.of(driver));

        adminUserServiceImpl.updateUserStatus(driver.getUser().getId(), RegistrationStatus.APPROVED);

        assertEquals(RegistrationStatus.APPROVED, driver.getStatus());
        verify(driverRepository, times(1)).save(driver);

        verify(emailService, times(1)).sendApprovalEmail(driver.getUser().getEmail(), driver.getFirstName() + " " + driver.getLastName(), "DRIVER");
    }

    @Test
    public void testUpdateUserStatus_WhenRejected_ShouldNotSendEmail(){
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(donor.getUser()));
        when(donorRepository.findByUser_Email(anyString())).thenReturn(Optional.of(donor));

        adminUserServiceImpl.updateUserStatus(donor.getUser().getId(), RegistrationStatus.REJECTED);

        verify(emailService, never()).sendApprovalEmail(any(), any(), any());

    }


}