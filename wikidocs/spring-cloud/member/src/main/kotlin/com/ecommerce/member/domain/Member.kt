package com.ecommerce.member.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "members")
class Member(
    @Column(nullable = false, unique = true)
    var email: String, // email 변경 가능성 있음

    @Column(nullable = false)
    var name: String, // 이름 변경 가능성 있음

    @Column(nullable = false)
    var address: String, // 주소 변경 가능성 있음
) : BaseEntity() { // 공통 속성 상속

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null // id는 DB가 생성하므로 주 생성자에서 제외
}