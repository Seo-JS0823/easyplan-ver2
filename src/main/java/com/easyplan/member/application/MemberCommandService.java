package com.easyplan.member.application;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easyplan._shared.domain.Email;
import com.easyplan._shared.domain.PublicId;
import com.easyplan.member.application.provided.MemberCommand;
import com.easyplan.member.application.provided.MemberFinder;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.application.required.EmailSender;
import com.easyplan.member.application.required.MemberRepository;
import com.easyplan.member.domain.Member;
import com.easyplan.member.domain.MemberStatus;
import com.easyplan.member.domain.Nickname;
import com.easyplan.member.domain.PasswordEncoder;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;
import com.easyplan.member.domain.request.MemberAccountRequest;
import com.easyplan.member.domain.request.MemberRegisterRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest.NicknameUpdate;
import com.easyplan.member.domain.request.MemberUpdateRequest.NotificationSetting;
import com.easyplan.member.domain.request.MemberUpdateRequest.PasswordUpdate;
import com.easyplan.member.domain.request.MemberUpdateRequest.ProfileDetail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService implements MemberCommand {
	private final MemberRepository repo;

	private final MemberFinder finder;
	
	private final PasswordEncoder passwordEncoder;
	
	private final MemberPolicy policy;
	
	private final EmailSender emailSender;
	
	@Override
	public Member register(MemberRegisterRequest memberRegister) {
		finder.checkDuplicateEmail(new Email(memberRegister.email()));
		policy.validateNickname(new Nickname(memberRegister.nickname()));
		finder.checkDuplicateNickname(new Nickname(memberRegister.nickname()));
		
		Member member = Member.register(memberRegister, passwordEncoder, policy);
		
		emailSender.sendJoinVerification(member.getEmail().address());
		
		return repo.save(member);
	}

	@Override
	public Member changeNickname(PublicId publicId, NicknameUpdate nicknameUpdate) {
		finder.checkDuplicateNickname(new Nickname(nicknameUpdate.nickname()));
		
		return consumerExecute(publicId, member -> {
			member.changeNickname(new Nickname(nicknameUpdate.nickname()), policy);
		});
	}

	@Override
	public Member changePassword(PublicId publicId, PasswordUpdate passwordUpdate) {
		return consumerExecute(publicId, member -> {
			member.changePassword(passwordUpdate.rawPassword(), passwordUpdate.newPassword(), passwordEncoder);
		});
	}

	@Override
	public Member changeProfileDetail(PublicId publicId, ProfileDetail profileDetail) {
		return consumerExecute(publicId, member -> {
			member.changeProfileDetail(profileDetail);
		});
	}

	@Override
	public Member changeNotificationSetting(PublicId publicId, NotificationSetting notificationSetting) {
		return consumerExecute(publicId, member -> {
			member.changeNotificationSetting(notificationSetting);
		});
	}
	
	@Override
	public Member activate(PublicId publicId) {
		return consumerExecute(publicId, member -> {
			member.activate();
		});
	}
	
	@Override
	public Member deactivate(PublicId publicId, MemberAccountRequest.MemberDeactivate memberDeactive) {
		return consumerExecute(publicId, member -> {
			if(member.verifyPassword(memberDeactive.currentPassword(), passwordEncoder)) {
				member.deactivate(policy);				
			} else {
				throw new MemberException(MemberExceptionCode.VERIFY_PASSWORD_FAIL);
			}
		});
	}
	
	@Override
	public Member recover(PublicId publicId, MemberAccountRequest.MemberRecover memberRecover) {
		return consumerExecute(publicId, member -> {
			member.recover();
		});
	}

	@Override
	public void cleanUpDeactivatedMembers(Instant deletionDeadline) {
		List<Member> deactivatedMembers = repo.findByDeactivatedMembers(MemberStatus.DEACTIVATED, deletionDeadline);
		
		if(deactivatedMembers.isEmpty()) return;
		
		repo.deleteAllInBatch(deactivatedMembers);
	}

	@Override
	public boolean emailVerify(String token) {
		Email email = new Email(emailSender.verifyToken(token));
		
		Member member = finder.findOptionalByEmail(email)
				.orElseThrow(() -> new MemberException(MemberExceptionCode.MEMBER_NOT_FOUND));
		
		member.activate();
		
		if(member.getStatus() != MemberStatus.ACTIVE) {
			return false;
		}
		
		return true;
	}
	
	private Member consumerExecute(PublicId publicId, Consumer<Member> action) {
		Member member = finder.findByPublicId(publicId);
		action.accept(member);
		return repo.save(member);
	}
}
