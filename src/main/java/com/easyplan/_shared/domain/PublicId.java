package com.easyplan._shared.domain;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PublicId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String publicId;
	
	public PublicId(String publicId) {
		if(publicId == null || publicId.isBlank()) {
			throw new IllegalArgumentException("publicId는 필수입니다.");
		}
		
		this.publicId = publicId;
	}
	
	public static PublicId create() {
		return new PublicId(UUID.randomUUID().toString());
	}
	
	public String publicId() {
		return publicId;
	}
}
