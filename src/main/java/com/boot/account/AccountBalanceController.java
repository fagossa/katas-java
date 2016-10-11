package com.boot.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.boot.core.DataNotFoundException;
import com.boot.core.InsufficientFundsException;
import com.boot.transaction.TransactionRepository;
import com.boot.transaction.TransactionTO;
import com.boot.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.boot.transaction.TransactionStatus.*;

@RestController
@RequestMapping("/account")
public class AccountBalanceController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountsRepository accountRepository;

    @RequestMapping(value = "addMoney", method = RequestMethod.POST)
    public TransactionTO transferMoneyToAccount(@RequestBody DepositMoneyMessage depositMoneyMessage) {
        final Optional<AccountTO> maybeAccount = Optional
                .ofNullable(accountRepository.findByAccountNumber(depositMoneyMessage.getAccountTO().getAccountNumber()));

        final TransactionTO.TransactionBuilder transactionBuilder = maybeAccount
                .map(accountTO ->
                            new TransactionTO.TransactionBuilder()
                                    .withRandomId()
                                    .withAccount(accountTO)
                                    .withTransactionStatus(APPROVED)
                                    .withDate(OffsetDateTime.now(ZoneOffset.UTC))
                                    .withReason("Add Money")
                                    .withType(TransactionType.CREDIT)

                ).orElseThrow(() -> new DataNotFoundException("account nof found"));

        final TransactionTO transactionTO = transactionBuilder.build();
        transactionTO.getAccountTO().setAccountBalance(transactionTO.getAccountTO().getAccountBalance().add(depositMoneyMessage.getAmmount()));
        accountRepository.save(transactionTO.getAccountTO());
        transactionRepository.save(transactionTO);
        return transactionTO;
    }

    @RequestMapping(value = "debitMoney", method = RequestMethod.POST)
    public TransactionTO transferMoneyBetweenAccounts(@RequestBody DebitMoneyMessage debitMoneyMessage) {
        final Optional<AccountTO> maybeAccount = Optional
                .ofNullable(accountRepository.findByAccountNumber(debitMoneyMessage.getAccountTO().getAccountNumber()));

        final TransactionTO.TransactionBuilder transactionBuilder = maybeAccount
                .map(accountTO ->
                        new TransactionTO.TransactionBuilder()
                                .withRandomId()
                                .withAccount(accountTO)
                                .withDate(OffsetDateTime.now(ZoneOffset.UTC))
                                .withReason(debitMoneyMessage.getReason())
                                .withType(TransactionType.DEBIT)
                ).orElseThrow(() -> new DataNotFoundException("account nof found"));

        if (debitMoneyMessage.transferAmountLowerThanAccountBalance()) {
            transactionBuilder.withTransactionStatus(REJECTED);
            transactionRepository.save(transactionBuilder.build());
            throw new InsufficientFundsException("insufficient funds");
        } else {
            final TransactionTO transactionTO = transactionBuilder.build();
            final BigDecimal newAmount = transactionTO.getAccountTO().getAccountBalance().subtract(debitMoneyMessage.getAmount());
            transactionTO.getAccountTO().setAccountBalance(newAmount);
            accountRepository.save(transactionTO.getAccountTO());
            transactionRepository.save(transactionTO);
            return transactionTO;
        }
    }

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getAccountBalance(String accountNumber) {
        return Optional.ofNullable(accountRepository
                .findByAccountNumber(accountNumber)).map(AccountTO::getAccountBalance)
                .orElseThrow(() -> new DataNotFoundException("account nof found"));
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public AccountTO getAccount(String accountNumber) {
        return Optional.ofNullable(accountRepository
                .findByAccountNumber(accountNumber))
                .orElseThrow(() -> new DataNotFoundException("account nof found"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {EmptyResultDataAccessException.class, InsufficientFundsException.class, DataNotFoundException.class})
    public void badRequest() {}
}
