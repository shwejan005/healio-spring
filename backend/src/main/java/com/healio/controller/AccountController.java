package com.healio.controller;

import com.healio.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getUserAnalytics(@RequestParam String userId) {
        return ResponseEntity.ok(accountService.getUserAnalytics(userId));
    }

    @GetMapping("/mood-stats")
    public ResponseEntity<Map<String, Object>> getMoodStats(@RequestParam String userId) {
        return ResponseEntity.ok(accountService.getMoodStats(userId));
    }
}
