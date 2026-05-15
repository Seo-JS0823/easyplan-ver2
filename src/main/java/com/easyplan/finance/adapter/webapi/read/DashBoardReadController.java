package com.easyplan.finance.adapter.webapi.read;

import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.easyplan._global.security.MemberDetails;
import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.response.GlobalResponse;
import com.easyplan.finance.adapter.webapi.response.LedgerDashboardResponse;
import com.easyplan.finance.application.usecase.FinanceManagementQuery;
import com.easyplan.finance.application.usecase.FinanceQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/summarize/")
public class DashBoardReadController {
	
	private final FinanceQuery financeQuery;
	
	private final FinanceManagementQuery financeManagement;
	
	@GetMapping("/{ledgerPublicId}")
	public GlobalResponse<LedgerDashboardResponse> dashboardSummary(
			@AuthenticationPrincipal MemberDetails member,
			@PathVariable String ledgerPublicId,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
		
		Long memberId = member.getMemberId();
		PublicId ledgerPID = new PublicId(ledgerPublicId);
		
		LedgerDashboardResponse response = new LedgerDashboardResponse(
				financeQuery.getNetWorthSummary(memberId, ledgerPID),
				financeQuery.getMonthlyCashSummary(memberId, ledgerPID, period),
				financeManagement.getMultiPeriodBudgetSummary(memberId, ledgerPID, period)
		);
		
		return GlobalResponse.ok(response);
	}
	
}
