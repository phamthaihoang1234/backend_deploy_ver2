package vn.fs.api;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;




import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProvinceApiTest {

    private static final String EXTERNAL_API_URL_PROVINCES = "https://provinces.open-api.vn/api/p";
    private static final String EXTERNAL_API_URL_DISTRICTS = "https://provinces.open-api.vn/api/p";

    private static final String EXTERNAL_API_URL_WARDS = "https://provinces.open-api.vn/api/d";

    private static final String EXTERNAL_API_URL_PROVINCE = "https://provinces.open-api.vn/api/p/";
    private static final String EXTERNAL_API_URL_DISTRICT = "https://provinces.open-api.vn/api/d/";

    private static final String EXTERNAL_API_URL_WARD = "https://provinces.open-api.vn/api/w/";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProvinceApi provincesController;

    @Test
    public void testGetProvinces() {
        // Mock the response from the external API
        String mockApiResponse = "Mocked response from the API";
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        // Set up the mock behavior for the RestTemplate
        when(restTemplate.getForEntity(EXTERNAL_API_URL_PROVINCES, String.class))
                .thenReturn(mockResponseEntity);

        // Call the method under test
        ResponseEntity<?> result = provincesController.getProvinces();

        // Verify the expected behavior
        verify(restTemplate, times(1)).getForEntity(EXTERNAL_API_URL_PROVINCES, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockApiResponse, result.getBody());
    }

    @Test
    public void testGetDistricts() {
        Long districtCode = 123L;
        String mockApiResponse = "Mocked response for districts";
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(EXTERNAL_API_URL_DISTRICTS + '/' + districtCode + "?depth=2", String.class))
                .thenReturn(mockResponseEntity);

        ResponseEntity<?> result = provincesController.getDistricts(districtCode);

        verify(restTemplate, times(1)).getForEntity(EXTERNAL_API_URL_DISTRICTS + '/' + districtCode + "?depth=2", String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockApiResponse, result.getBody());
    }

    @Test
    public void testGetWards() {
        Long wardCode = 456L;
        String mockApiResponse = "Mocked response for wards";
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(EXTERNAL_API_URL_WARDS + '/' + wardCode + "?depth=2", String.class))
                .thenReturn(mockResponseEntity);

        ResponseEntity<?> result = provincesController.getWards(wardCode);

        verify(restTemplate, times(1)).getForEntity(EXTERNAL_API_URL_WARDS + '/' + wardCode + "?depth=2", String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockApiResponse, result.getBody());
    }

    @Test
    public void testGetDistrict() {
        Long districtId = 789L;
        String mockApiResponse = "Mocked response for district";
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(EXTERNAL_API_URL_DISTRICT + districtId, String.class))
                .thenReturn(mockResponseEntity);

        ResponseEntity<?> result = provincesController.getDistrict(districtId);

        verify(restTemplate, times(1)).getForEntity(EXTERNAL_API_URL_DISTRICT + districtId, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockApiResponse, result.getBody());
    }

    @Test
    public void testGetProvince() {
        Long provinceId = 987L;
        String mockApiResponse = "Mocked response for province";
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(EXTERNAL_API_URL_PROVINCE + provinceId, String.class))
                .thenReturn(mockResponseEntity);

        ResponseEntity<?> result = provincesController.getProvince(provinceId);

        verify(restTemplate, times(1)).getForEntity(EXTERNAL_API_URL_PROVINCE + provinceId, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockApiResponse, result.getBody());
    }

    @Test
    public void testGetWard() {
        Long wardId = 654L;
        String mockApiResponse = "Mocked response for ward";
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockApiResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(EXTERNAL_API_URL_WARD + wardId, String.class))
                .thenReturn(mockResponseEntity);

        ResponseEntity<?> result = provincesController.getWard(wardId);

        verify(restTemplate, times(1)).getForEntity(EXTERNAL_API_URL_WARD + wardId, String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockApiResponse, result.getBody());
    }
}
