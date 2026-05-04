package com.easyplan._global.infra.jpa;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;

/*
 * MappedSuperclass
 * 모든 JPA Entity가 상속받아 사용할 클래스로
 * IDENTITY로 생성되는 Long id, 생성/수정 시간을 공통으로 관리한다.
 * 
 * BaseEntity Class 자체로 테이블이 생성되거나 JPA 엔티티가 되지는 않지만
 * 이 Class에 선언된 필드를 [상속해서] 사용하게 해줄 수 있는 어노테이션이다.
 */
@MappedSuperclass

/*
 * EntityListeners(AuditingEntityListener.class)
 * JPA Auditing 기능을 활성화시킨다.
 * @CreatedDate, @LastModifiedDate 를 자동으로 채워주기 위해 필요하다.
 * 
 * JPA Auditing이란 감시라는 뜻으로 데이터가 "누가, 언제" 수행했는지를 자동으로 기록한다.
 * 
 * AuditingEntityListener는 그 중에서도 언제 생성되었고 언제 수정되었는지를 기록하는데
 * 해당 엔티티의 Persist, Update 이벤트를 감시해서 이벤트가 발생하면
 * @CreatedDate에는 현재 시간을, @LastModifiedDate에는 수정 시간을 자동으로 넣게된다.
 * 
 * 이후 채워진 데이터가 포함된 SQL로 DB에 전송된다.
 */
@EntityListeners(AuditingEntityListener.class)
@Getter
@ToString
public abstract class BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	/*
	 * 엔티티의 최초 저장 시 자동으로 시간이 세팅되며
	 * Insert 시점에만 값이 들어간다.
	 */
	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

	/*
	 * 엔티티가 수정될 때 마다 자동으로 시간이 갱신된다
	 * Update 시점에만 값이 변경된다.
	 */
  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
	
	@Override
  public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
      
    /*
     * Hibernate Proxy 대응
     * 
     * JPA는 LAZY 로딩 시 실제 객체 대신 Proxy 객체를 사용한다.
     * 그래서 단순하게 getClass() 를 이용해 비교하게 되면 서로 다른 클래스로 판단될 수 있다.
     * 
     * Proxy 라면 실제 엔티티 클래스를 꺼내서 비교한다.
     */
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		
		Class<?> thisEffetctiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		
		if(thisEffetctiveClass != oEffectiveClass)
      return false;
		
		BaseEntity that = (BaseEntity) o;
		
    /*
     * ID를 기준으로 동등성 비교
     * ID가 Null인 경우는 영속화 되지 않은 상태이므로 false 반환
     */
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
  	return this instanceof HibernateProxy
  			? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
  			: getClass().hashCode();
  }
}
