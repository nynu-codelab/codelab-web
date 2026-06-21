package cn.edu.nynu.selab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 南阳师范学院软件工程实验室 - 后端启动类
 *
 * @author NYNU SE Lab
 */
@SpringBootApplication
@MapperScan("cn.edu.nynu.selab.**.mapper")
public class SelabApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelabApplication.class, args);
    }

}
