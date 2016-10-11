package com.boot;

import com.boot.account.DepositMoneyMessage;
import com.boot.core.DockerRule;
import com.boot.transaction.TransactionTO;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static com.boot.transaction.TransactionStatus.APPROVED;
import static com.boot.transaction.TransactionType.CREDIT;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.http.HttpStatus.OK;

@WebIntegrationTest(randomPort = true)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StartRestApplication.class)
public class AccountBalanceIntegrationTest implements DataFactory {

    @ClassRule
    public static DockerRule mongoRule =
            DockerRule.builder()
                    .image("xebia/my_mongo")
                    .needsPull(false)
                    .ports("27017")
                    .waitForLog("now connect")
                    .build();
    @Value("${local.server.port}")
    int port;

    @Before()
    public void setUp() throws IOException {
        RestAssured.port = port;
    }

    @Test
    public void should_return_account_if_exist() throws Exception {
        // given
        String name = "Fidel Castro";
        final RequestSpecification aRequest = given().param("accountNumber", "1234");

        // when
        final Response response = aRequest.when().get("/account");

        // then
        String value = response.then().assertThat().statusCode(OK.value())
                .extract().path("customerName");
        assertThat(value).as(name);
    }

    @Test
    public void should_return_account_balance() throws Exception {
        // given
        final RequestSpecification aRequest = given().param("accountNumber", "1234");

        aRequest.
                when().get("/account/balance")
                .then()
                .assertThat().statusCode(OK.value()).body(greaterThan("0"));
    }


    @Test
    public void should_add_money_to_existing_account() throws Exception {
        // given some data to send
        DepositMoneyMessage depositMoneyMessage = newSendMoneyMessage(tenDollars, anAccount);
        String payload = entityAsJson(depositMoneyMessage);

        // given an expected new balance
        anAccount.getAccountBalance().add(tenDollars);

        Response balance = given().body(payload).with().contentType(JSON).
                when().post("/account/addMoney").
                then().contentType(JSON)
                .assertThat().statusCode(OK.value()).extract().response();
        final TransactionTO insertedTransaction =
                anObjectMapper().readValue(balance.asString(), TransactionTO.class);

        // then
        assertThat(insertedTransaction.getTransactionStatus()).isEqualTo(APPROVED);
        assertThat(insertedTransaction.getType()).isEqualTo(CREDIT);
    }

}
