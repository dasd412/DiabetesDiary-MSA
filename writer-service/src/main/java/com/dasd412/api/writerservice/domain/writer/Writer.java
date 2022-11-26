package com.dasd412.api.writerservice.domain.writer;

import com.dasd412.api.writerservice.domain.BaseTimeEntity;
import com.dasd412.api.writerservice.domain.EntityId;
import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "Writer", uniqueConstraints = @UniqueConstraint(columnNames = {"writer_id", "name"}))
public class Writer extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "writer_id", columnDefinition = "bigint default 0", nullable = false, unique = true)
    private Long writerId;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 작성자 이메일. github의 경우 null일 수 있다.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * 작성자의 비밀 번호. OAuth 로그인의 경우 필요 없다.
     * Form Login 방식의 경우 암호화되서 db에 저장된다.
     */
    private String password;

//    @Enumerated(EnumType.STRING)
//    private Role role;

    /**
     * OAuth 로그인일 경우 provider가 누구인지
     */
    private String provider;

    /**
     * OAuth provider의 id
     */
    private String providerId;

    @ElementCollection
    private final List<Long>diaryIds=new ArrayList<>();

//
//    @OneToOne
//    @JoinColumn(name = "profile_id")
//    private Profile profile;

    public Writer() {
    }

    //todo 나중에 지울 생성자
    public Writer(String name, String email) {
        this.name = name;
        this.email = email;
    }

//    /**
//     * @param email          이메일 (github의 경우 null일 수 있다.)
//     * @param password       암호화된 비밀 번호 (OAuth 로그인의 경우 null과 마찬가지)
//     * @param provider       OAuth 제공자 (Form Login의 경우 null)
//     * @param providerId     OAuth 제공자 식별자 (Form Login의 경우 null)
//     */
//    @Builder
//    public Writer(EntityId<Writer, Long> writerEntityId, String name, String email, String password, Role role, String provider, String providerId) {
//        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
//        this.writerId = writerEntityId.getId();
//        this.name = name;
//        this.email = email;
//        this.password = password;
//        this.role = role;
//        this.provider = provider;
//        this.providerId = providerId;
//    }

    public Long getId() {
        return writerId;
    }

    public String getName() {
        return name;
    }

    private void modifyName(String name) {
        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    private void modifyEmail(String email) {
        //CHECK Email
        this.email = email;
    }

//    public Role getRole() {
//        return role;
//    }
//
//    private void modifyRole(Role role) {
//        this.role = role;
//    }
//
//    public List<DiabetesDiary> getDiaries() {
//        return new ArrayList<>(diaries);
//    }

    public String getPassword() {
        return password;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

//    public void setProfile(Profile profile) {
//        this.profile = profile;
//    }
//
//    public Profile getProfile() {
//        return profile;
//    }
//
//    public void addDiary(DiabetesDiary diary) {
//        this.diaries.add(diary);
//        /* 무한 루프 방지 */
//        if (diary.getWriter() != this) {
//            diary.makeRelationWithWriter(this);
//        }
//    }

//    /**
//     * 연관 관계 제거 시에만 사용
//     *
//     * @param diary 작성자가 작성했던 혈당 일지
//     */
//    public void removeDiary(DiabetesDiary diary) {
//        checkArgument(this.diaries.contains(diary), "this writer does not have the diary");
//        this.diaries.remove(diary);
//    }

//    @Override
//    public String toString() {
//        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
//                .append("id", writerId)
//                .append("name", name)
//                .append("email", email)
//                .append("role", role)
//                .append("provider", provider)
//                .toString();
//    }

    @Override
    public int hashCode() {
        return Objects.hash(writerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Writer target = (Writer) obj;
        return Objects.equals(this.writerId, target.writerId);
    }

//    public void update(String name, String email, Role role) {
//        modifyName(name);
//        modifyEmail(email);
//        modifyRole(role);
//    }


}
