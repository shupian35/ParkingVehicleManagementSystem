package com.shupian.pvms;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
@MapperScan("com.shupian.pvms.mapper")
@ServletComponentScan
public class ParkingVehicleManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingVehicleManagementSystemApplication.class, args);
    }

}
