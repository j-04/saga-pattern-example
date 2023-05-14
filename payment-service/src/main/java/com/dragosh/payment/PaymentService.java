package com.dragosh.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    public PaymentService(PaymentRepository paymentRepository, RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
    }

    private static final String ACCOUNT_SERVICE_URL = "http://localhost:8081/accounts/";

    @Transactional
    public Payment makePayment(Long fromAccountId, Long toAccountId, BigDecimal amount) throws PaymentException {
        Payment payment = new Payment();
        payment.setFromAccountId(fromAccountId);
        payment.setToAccountId(toAccountId);
        payment.setAmount(amount);

        Account fromAccount = restTemplate.getForObject(ACCOUNT_SERVICE_URL + fromAccountId, Account.class);
        Account toAccount = restTemplate.getForObject(ACCOUNT_SERVICE_URL + toAccountId, Account.class);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account with id: " + fromAccountId);
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        restTemplate.put(ACCOUNT_SERVICE_URL, fromAccount);
        restTemplate.put(ACCOUNT_SERVICE_URL, toAccount);

        paymentRepository.save(payment);

        return payment;
    }

    @Transactional(readOnly = true)
    public Payment getPayment(Long id) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            throw new PaymentNotFoundException("Payment not found with id: " + id);
        }
        return optionalPayment.get();
    }
}
