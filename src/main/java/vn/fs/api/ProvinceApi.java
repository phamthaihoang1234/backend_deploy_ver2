package vn.fs.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ProvinceApi {


    private static final String EXTERNAL_API_URL_PROVINCES = "https://provinces.open-api.vn/api/p";
    private static final String EXTERNAL_API_URL_DISTRICTS = "https://provinces.open-api.vn/api/p";

    private static final String EXTERNAL_API_URL_WARDS = "https://provinces.open-api.vn/api/d";

    private static final String EXTERNAL_API_URL_PROVINCE = "https://provinces.open-api.vn/api/p/";
    private static final String EXTERNAL_API_URL_DISTRICT = "https://provinces.open-api.vn/api/d/";

    private static final String EXTERNAL_API_URL_WARD = "https://provinces.open-api.vn/api/w/";



    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/provinces")
    public ResponseEntity<?> getProvinces() {
        ResponseEntity<String> response = restTemplate.getForEntity(EXTERNAL_API_URL_PROVINCES, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/districts/{code}")
    public ResponseEntity<?> getDistricts(@PathVariable("code") Long code) {

        ResponseEntity<String> response = restTemplate.getForEntity(EXTERNAL_API_URL_DISTRICTS+'/'+code+"?depth=2", String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/wards/{code}")
    public ResponseEntity<?> getWards(@PathVariable("code") Long code) {
        ResponseEntity<String> response = restTemplate.getForEntity(EXTERNAL_API_URL_WARDS+'/'+code+"?depth=2", String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/district/{id}")
    public ResponseEntity<?> getDistrict(@PathVariable("id") Long id) {
        ResponseEntity<String> response = restTemplate.getForEntity(EXTERNAL_API_URL_DISTRICT+id, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/province/{id}")
    public ResponseEntity<?> getProvince(@PathVariable("id") Long id) {
        ResponseEntity<String> response = restTemplate.getForEntity(EXTERNAL_API_URL_PROVINCE+id, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    @GetMapping("/ward/{id}")
    public ResponseEntity<?> getWard(@PathVariable("id") Long id) {
        ResponseEntity<String> response = restTemplate.getForEntity(EXTERNAL_API_URL_WARD+id, String.class);
        return ResponseEntity.ok(response.getBody());
    }


}
