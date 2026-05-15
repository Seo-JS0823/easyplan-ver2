package com.easyplan.finance.application.usecase.response.query;

// 순자산, 총자산, 총부채 응답 DTO
public record LedgerAssetSummary(long totalAsset, long totalLiabilities, long netWorth) {
	
}
