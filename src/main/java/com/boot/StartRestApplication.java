package com.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.boot.account.AccountTO;
import com.boot.account.AccountsRepository;
import com.boot.transaction.TransactionRepository;

import java.math.BigDecimal;
import java.util.Arrays;

@SpringBootApplication
public class StartRestApplication implements CommandLineRunner {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(StartRestApplication.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }

    @Override
    public void run(String... arg0) throws Exception {
        AccountTO accountTO = accountsRepository.findByAccountNumber("1234");
        if (accountTO == null) {
            accountsRepository.insert(new AccountTO("1", "1234", "Fidel Castro", BigDecimal.valueOf(10000)));
            accountsRepository.insert(new AccountTO("2", "456", "Samuel Castro", BigDecimal.valueOf(1000)));
            accountsRepository.insert(new AccountTO("3", "377", "Santiago Castro", BigDecimal.valueOf(500)));
        }
    }
}
