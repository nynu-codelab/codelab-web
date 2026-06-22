package cn.edu.nynu.codelab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 南阳师范学院Code Lab 实验室 - 后端启动类
 *
 * @author NYNU Code Lab
 */
@SpringBootApplication
@MapperScan("cn.edu.nynu.codelab.**.mapper")
@EnableScheduling
public class CodelabApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodelabApplication.class, args);
    }

}
