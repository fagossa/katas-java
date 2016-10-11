package com.boot;

import com.boot.account.AccountTO;
import com.boot.account.DepositMoneyMessage;
import com.boot.transaction.TransactionTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.Charset;

interface DataFactory {

    BigDecimal tenThousandsDollars = new BigDecimal(10_000);

    BigDecimal hundredDollars = new BigDecimal(100);

    BigDecimal tenDollars = new BigDecimal(10);

    default String entityAsJson(Object entity) throws JsonProcessingException {
        return anObjectMapper().writeValueAsString(entity);
    }

    default TransactionTO responseAsTransactionFrom(MvcResult result) throws java.io.IOException {
        return anObjectMapper().readValue(result.getResponse().getContentAsString(), TransactionTO.class);
    }

    default DepositMoneyMessage newSendMoneyMessage(BigDecimal amountToAdd, AccountTO anAccount) {
        DepositMoneyMessage depositMoneyMessage = new DepositMoneyMessage();
        depositMoneyMessage.setAccountTO(anAccount);
        depositMoneyMessage.setAmmount(amountToAdd);
        return depositMoneyMessage;
    }
    
    default ObjectMapper anObjectMapper() {
        ObjectMapper anObjectMapper = new ObjectMapper();
        anObjectMapper.registerModule(new JavaTimeModule());
        return anObjectMapper;
    }

    AccountTO anAccount = new AccountTO("1", "1234", "Fidel Castro", BigDecimal.valueOf(565));

    MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")
    );

}
