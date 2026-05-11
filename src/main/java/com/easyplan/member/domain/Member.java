package com.easyplan.member.domain;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan._shared.domain.Email;
import com.easyplan._shared.domain.PublicId;
import com.easyplan._shared.util.NonNull;
import com.easyplan.member.application.provided.MemberPolicy;
import com.easyplan.member.domain.exception.MemberException;
import com.easyplan.member.domain.exception.MemberExceptionCode;
import com.easyplan.member.domain.request.MemberRegisterRequest;
import com.easyplan.member.domain.request.MemberUpdateRequest;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NaturalIdCache
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Member extends BaseEntity {
	
	// 이메일
	@Embedded
	@AttributeOverride(
			name = "address",
			column = @Column(name = "email", nullable = false, unique = true, updatable = false)
	)
	private Email email;
	
	// 외부 노출용 검색키
	@NaturalId
	@Embedded
	@AttributeOverride(
			name = "publicId",
			column = @Column(name = "member_public_id", nullable = false, unique = true, updatable = false, length = 40)
	)
	private PublicId memberPublicId;
	
	// 닉네임
	@Embedded
	@AttributeOverride(
			name = "nickname",
			column = @Column(name = "nickname", nullable = false, unique = true, length = 10)
	)
	private Nickname nickname;
	
	// 패스워드 해시
	@Embedded
	@AttributeOverride(
			name = "passwordHash",
			column = @Column(name = "password_hash", nullable = false)
	)
	private PasswordHash passwordHash;
	
	// 멤버 상태
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 12)
	private MemberStatus status;
	
	// 멤버 권한
	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private MemberRole role;
	
	// 마지막 로그인 시간
	@Column(name = "last_login_at")
	private Instant lastLoginAt;
	
	@Column(name = "deletion_date")
	private Instant deletionDate;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private MemberProfile profile;
	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private MemberSetting setting;
	
	public static Member register(MemberRegisterRequest memberRegister, PasswordEncoder passwordEncoder, MemberPolicy policy) {
		policy.validateNickname(new Nickname(memberRegister.nickname()));
		
		Member member = new Member();
		
		member.memberPublicId = PublicId.create();
		member.email = new Email(memberRegister.email());
		member.nickname = new Nickname(memberRegister.nickname());
		member.passwordHash = new PasswordHash(passwordEncoder.encode(memberRegister.password()));
		member.status = MemberStatus.PENDING;
		member.role = MemberRole.PENDING;
		
		member.profile = MemberProfile.create(
				member.nickname,
				memberRegister.introduction(),
				memberRegister.birthdate()
		);
		
		member.setting = MemberSetting.create(
				NonNull.require(memberRegister.zoneId(), ""),
				memberRegister.pushNotification(),
				memberRegister.emailNotification()
		);
		
		return member;
	}
	
	// ===== Behavior =====
	public void loginAtUpdate(Instant now) {
		this.lastLoginAt = now;
	}
	
	public void changeNickname(Nickname nickname, MemberPolicy policy) {
		isActive();
		
		if(NonNull.eq(this.nickname, nickname)) {
			return;
		}
		
		policy.validateNickname(nickname);
		
		this.nickname = nickname;
		
		this.profile.changeProfileAddress(new ProfileAddress(nickname.nickname()));
	}
	
	public void changePassword(String rawPassword, String newPassword, PasswordEncoder passwordEncoder) {
		isActive();
		
		if(!verifyPassword(rawPassword, passwordEncoder)) {
			throw new MemberException(MemberExceptionCode.FORBIDDEN_PASSWORD_UPDATE);
		}
		
		if(passwordEncoder.matches(newPassword, this.passwordHash.passwordHash())) {
			return;
		}
		
		this.passwordHash = new PasswordHash(passwordEncoder.encode(newPassword));
	}
	
	public boolean verifyPassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.matches(password, this.passwordHash.passwordHash());
	}
	
	public void changeRole(MemberRole role) {
		isActive();
		
		this.role = role;
	}
	
	public void changeImageUrl(String imageUrl) {
		isActive();
		
		this.profile.changeImageUrl(imageUrl);
	}
	
	public void changeProfileDetail(String introduction, LocalDate birthdate, ProfileVisibility visibility) {
		isActive();
		
		this.profile.changeBirthdate(birthdate);
		this.profile.changeIntroduction(introduction);
		this.profile.changeVisibility(visibility);
	}
	
	public void changeProfileDetail(MemberUpdateRequest.ProfileDetail memberProfile) {
		isActive();
		
		this.profile.changeBirthdate(memberProfile.birthdate());
		this.profile.changeIntroduction(memberProfile.introduction());
		this.profile.changeVisibility(memberProfile.visibility());
	}
	
	public void changeNotificationSetting(boolean pushNotification, boolean emailNotification) {
		isActive();
		
		this.setting.changeNotification(pushNotification, emailNotification);
	}
	
	public void changeNotificationSetting(MemberUpdateRequest.NotificationSetting memberSetting) {
		isActive();
		
		this.setting.changeNotification(
				memberSetting.pushNotification(),
				memberSetting.emailNotification()
		);
	}
	
	public void activate() {
		isPending();
		
		this.status = MemberStatus.ACTIVE;
		this.role = MemberRole.MEMBER;
	}
	
	public void deactivate(MemberPolicy policy) {
		isActive();
		
		this.status = MemberStatus.DEACTIVATED;
		this.role = MemberRole.PENDING;
		this.deletionDate = policy.deletionDate();
	}
	
	public void recover() {
		if(this.status != MemberStatus.DEACTIVATED || this.deletionDate == null) {
			throw new IllegalArgumentException("회원탈퇴 대기 상태가 아닙니다.");
		}
		
		this.status = MemberStatus.ACTIVE;
		this.role = MemberRole.MEMBER;
		this.deletionDate = null;
	}
	
	// ===== Guard =====
	public void isActive() {
		if(this.status != MemberStatus.ACTIVE) {
			throw new IllegalStateException("활성화된 회원이 아닙니다.");
		}
	}
	
	public void isPending() {
		if(this.status != MemberStatus.PENDING) {
			throw new IllegalStateException("회원가입 대기 상태가 아닙니다.");
		}
	}
}