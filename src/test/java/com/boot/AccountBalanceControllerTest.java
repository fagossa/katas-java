package com.boot;

import com.boot.account.*;
import com.boot.transaction.TransactionRepository;
import com.boot.transaction.TransactionStatus;
import com.boot.transaction.TransactionTO;
import com.boot.transaction.TransactionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static com.boot.transaction.TransactionType.DEBIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@SpringApplicationConfiguration(classes = StartRestApplication.class)
public class AccountBalanceControllerTest implements DataFactory {

    @Mock
    AccountsRepository accountRepository;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    AccountBalanceController accountBalanceController;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(accountBalanceController).build();
    }

    @Test
    public void should_return_account_if_exist() throws Exception {
        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber()))
                .thenReturn(anAccount);
        // then
        mockMvc.perform(get("/account").param("accountNumber", anAccount.getAccountNumber())
                .accept(APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName", is(anAccount.getCustomerName())));
        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));
    }

    @Test
    public void should_fail_if_account_not_exist() throws Exception {
        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber()))
                .thenReturn(null);
        // then
        mockMvc.perform(get("/account").param("accountNumber", anAccount.getAccountNumber())
                .accept(APPLICATION_JSON)).andExpect(status().isBadRequest());

        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));
    }

    @Test
    public void should_return_balance_if_account_exist() throws Exception {
        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber()))
                .thenReturn(anAccount);
        // then
        mockMvc.perform(get("/account/balance").param("accountNumber", anAccount.getAccountNumber())
                .accept(APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(is(anAccount.getAccountBalance().toString())))
                .andDo(print());
        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));
    }

    @Test
    public void should_fail_when_getting_balance_if_account_not_exist() throws Exception {
        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(null);
        // then
        mockMvc.perform(get("/account/balance").param("accountNumber", anAccount.getAccountNumber())
                .accept(APPLICATION_JSON)).andExpect(status().isBadRequest());
        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));
    }

    @Test
    public void should_add_money_to_existing_account() throws Exception {
        // given some data to send
        DepositMoneyMessage depositMoneyMessage = newSendMoneyMessage(tenDollars, anAccount);

        // given the sent data as json
        String accountAsJson = entityAsJson(depositMoneyMessage);

        // given an expected new balance
        final BigDecimal newBalance = anAccount.getAccountBalance().add(tenDollars);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);


        final MvcResult result = mockMvc.perform(post("/account/addMoney").contentType(APPLICATION_JSON_UTF8)
                .content(accountAsJson)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        // then
        final TransactionTO insertedTransaction = responseAsTransactionFrom(result);

        assertThat(insertedTransaction.getTransactionStatus(), is(TransactionStatus.APPROVED));
        assertThat(insertedTransaction.getType(), is(TransactionType.CREDIT));
        assertThat(insertedTransaction.getAccountTO().getAccountBalance(), is(newBalance));

        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));
    }

    @Test
    public void should_fail_when_adding_money_if_account_not_exist() throws Exception {
        // given some data to send
        DepositMoneyMessage depositMoneyMessage = newSendMoneyMessage(tenDollars, anAccount);

        // given the sent data as json
        String accountAsJson = entityAsJson(depositMoneyMessage);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(null);
        // then
        mockMvc.perform(post("/account/addMoney").contentType(APPLICATION_JSON_UTF8)
                .content(accountAsJson)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));
    }

    @Test
    public void should_fail_to_debit_money_because_insufficient_founds() throws Exception {
        // given some data to send
        DebitMoneyMessage debitMoneyMessage = new DebitMoneyMessage.GetMoneyBuilder()
                .withAccountTO(anAccount)
                .withAmount(DataFactory.tenThousandsDollars)
                .withReason("Test money insufficient")
                .build();

        // given the sent data as json
        String accountAsJson = entityAsJson(debitMoneyMessage);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);

        // when some money is send
        mockMvc.perform(post("/account/debitMoney").contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(accountAsJson)
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));

        // no calls to accountRepository::save ever done
        verify(accountRepository, times(0)).save(any(AccountTO.class));

        // then a call to accountRepository::save
        verify(transactionRepository, times(1)).save(any(TransactionTO.class));
    }

    @Test
    public void should_add_money_to_account() throws Exception {
        // given some data to send
        DebitMoneyMessage debitMoneyMessage = new DebitMoneyMessage.GetMoneyBuilder()
                .withAccountTO(anAccount)
                .withAmount(hundredDollars)
                .withReason("Test money")
                .build();

        // given a new expected balance
        BigDecimal expectedBalance = anAccount.getAccountBalance().subtract(hundredDollars);

        // given the send data as json
        String accountAsJson = entityAsJson(debitMoneyMessage);

        when(accountRepository.findByAccountNumber(anAccount.getAccountNumber())).thenReturn(anAccount);

        // when some money is send
        final MvcResult result = mockMvc.perform(
                post("/account/debitMoney").contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(accountAsJson)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then a call to accountRepository::findByAccountNumber
        verify(accountRepository, times(1)).findByAccountNumber(any(String.class));

        // no calls to accountRepository::save ever done
        verify(accountRepository, times(1)).save(any(AccountTO.class));

        //then the save method is called
        verify(accountRepository).save(any(AccountTO.class));

        // then the transaction as an object
        final TransactionTO insertedTransaction = responseAsTransactionFrom(result);

        assertThat(insertedTransaction.getReason(), is(debitMoneyMessage.getReason()));
        assertThat(insertedTransaction.getType(), is(DEBIT));
        assertThat(insertedTransaction.getAccountTO().getAccountBalance(), is(expectedBalance));
    }

}
