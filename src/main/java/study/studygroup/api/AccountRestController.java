package study.studygroup.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.studygroup.account.AccountRepository;
import study.studygroup.domain.Account;

@RestController
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountRepository accountRepository;

    @GetMapping("/api/account/{id}")
    public Account getAccount(@PathVariable("id") Long id) {
        return accountRepository.findById(id).get();
    }
}
