package org.example;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {

    public void executeFlow()
    {
        RestTemplate restTemplate =new RestTemplate();

        try {
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            WebhookRequest request = new WebhookRequest();
            request.setName("John Doe");
            request.setRegNo("REG12347");
            request.setEmail("john@example.com");

            WebhookResponse response = restTemplate.postForObject(url, request, WebhookResponse.class);

            if (response == null) {
                System.out.println("No responssse received");
                return;
            }


            String sqlQuery =
                    "SELECT p.AMOUNT AS SALARY, " +
                            "e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME, " +
                            "YEAR(CURDATE()) - YEAR(e.DOB) AS AGE, " +
                            "d.DEPARTMENT_NAME " +
                            "FROM PAYMENTS p, EMPLOYEE e, DEPARTMENT d " +
                            "WHERE p.EMP_ID = e.EMP_ID " +
                            "AND e.DEPARTMENT = d.DEPARTMENT_ID " +
                            "AND DAY(p.PAYMENT_TIME) != 1 " +
                            "AND p.AMOUNT = (SELECT MAX(AMOUNT) " +
                            "FROM PAYMENTS " +
                            "WHERE DAY(PAYMENT_TIME) != 1)";

            FinalQueryRequest finalQueryRequest = new FinalQueryRequest();

            finalQueryRequest.setFinalQuery(sqlQuery);
            org.springframework.http.HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.set("Authorization", response.getAccessToken());

            HttpEntity<FinalQueryRequest> entity = new HttpEntity<>(finalQueryRequest, headers);

            ResponseEntity<String> response2 = restTemplate.exchange(response.getWebhook(), HttpMethod.POST, entity, String.class);

            System.out.println("Status Code : " + response2.getStatusCode());
            System.out.println("Response : " + response2.getBody());

        } catch (Exception e) {
            System.out.println("Error while calling API");
            e.printStackTrace();
        }


    }

}
