package fw.apigateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "bikeapp-user-service", url="https://bikeapp-user-service.azurewebsites.net") //TODO: change url

public interface UserClientProxy {

    @GetMapping(value = "/api/user/check")
    boolean check(@RequestHeader("Authorization") String token);

}
