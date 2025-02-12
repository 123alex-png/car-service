package service;

import com.car.maintenance.model.Owner;
import com.car.maintenance.repository.OwnerRepository;
import com.car.maintenance.service.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 使用 Mockito 进行单元测试
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository; // Mock Repository，避免操作真实数据库

    @InjectMocks
    private OwnerService ownerService; // 注入 Mock 依赖

    private Owner owner1;
    private Owner owner2;

    @BeforeEach
    void setUp() {
        owner1 = new Owner(1L, "张三", "A12345");
        owner2 = new Owner(2L, "张三", "C99999");
    }

    @Test
    void testCreateOwner() {
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner1);

        Owner savedOwner = ownerService.saveOwner("张三", "A12345");

        assertNotNull(savedOwner);
        assertEquals("张三", savedOwner.getName());

        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    void testGetOwnerById() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner1));

        Owner foundOwner = ownerService.getOwnerById(1L);

        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOwnersByName() {
        when(ownerRepository.findByName("张三")).thenReturn(List.of(owner1, owner2));

        List<Owner> owners = ownerService.getOwnersByName("张三");

        assertEquals(2, owners.size());
        assertEquals("张三", owners.get(0).getName());

        verify(ownerRepository, times(1)).findByName("张三");
    }
}
