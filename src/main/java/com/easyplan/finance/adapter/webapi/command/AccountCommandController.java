package com.easyplan.finance.adapter.webapi.command;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan.finance.application.usecase.FinanceCommand;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountCommandController {
	private final FinanceCommand financeCommand;
}
