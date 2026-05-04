package com.easyplan.member.adapter.scheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.easyplan._shared.time.UTC;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.application.required.MemberDeletionScheduler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberDeletionSchedulerImpl implements MemberDeletionScheduler {
	private final MemberCommand memberCommand;

	@Override
	@Scheduled(cron = "0 0 3 * * *")
	public void cleanUpDeactivatedMembers() {
		Instant deletionDeadline = UTC.nowSecond().minus(3, ChronoUnit.DAYS);
		
		memberCommand.cleanUpDeactivatedMembers(deletionDeadline);
	}
	
	
}
