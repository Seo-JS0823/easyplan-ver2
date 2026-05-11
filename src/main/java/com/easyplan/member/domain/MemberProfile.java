package com.easyplan.member.domain;

import java.time.LocalDate;

import com.easyplan._shared.domain.BaseEntity;
import com.easyplan._shared.util.NonNull;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfile extends BaseEntity {
	
	@Embedded
	@AttributeOverride(
			name = "nickname",
			column = @Column(name = "profile", nullable = false, unique = true)
	)
	private ProfileAddress profile;
	
	@Column(name = "introduction")
	private String introduction;
	
	@Column(name = "image_url")
	private String imageUrl;
	
	@Column(name = "birthdate")
	private LocalDate birthdate;
	
	@Column(name = "visibility", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private ProfileVisibility visibility;
	
	static MemberProfile create(Nickname nickname, String introduction, LocalDate birthdate) {
		MemberProfile profile = new MemberProfile();
		profile.profile = new ProfileAddress(nickname.nickname());
		profile.introduction = introduction;
		profile.birthdate = birthdate;
		profile.visibility = ProfileVisibility.PUBLIC;
		return profile;
	}
	
	// ===== Behavior =====
	
	void changeProfileAddress(ProfileAddress profile) {
		if(NonNull.eq(this.profile, profile)) {
			return;
		}
		
		this.profile = profile;
	}
	
	void changeIntroduction(String introduction) {
		if(NonNull.eq(this.introduction, introduction)) {
			return;
		}
		
		this.introduction = introduction;
	}
	
	void changeImageUrl(String imageUrl) {
		if(NonNull.eq(this.imageUrl, imageUrl)) {
			return;
		}
		
		this.imageUrl = imageUrl;
	}
	
	void changeVisibility(ProfileVisibility visibility) {
		if(this.visibility == visibility) {
			return;
		}
		
		this.visibility = NonNull.require(visibility, "공개 범위를 설정해주세요.");
	}
	
	void changeBirthdate(LocalDate birthdate) {
		if(NonNull.eq(this.birthdate, birthdate)) {
			return;
		}
		this.birthdate = birthdate;
	}
}
