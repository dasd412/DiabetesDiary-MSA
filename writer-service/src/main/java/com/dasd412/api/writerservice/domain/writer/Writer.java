package com.dasd412.api.writerservice.domain.writer;

import com.dasd412.api.writerservice.common.utils.email.EmailChecker;
import com.dasd412.api.writerservice.domain.BaseTimeEntity;
import com.dasd412.api.writerservice.domain.authority.WriterAuthority;
import lombok.Builder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "Writer", uniqueConstraints = @UniqueConstraint(columnNames = {"writer_id", "name"}))
public class Writer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    /**
     * OAuth 로그인일 경우 provider가 누구인지
     */
    private String provider;

    /**
     * OAuth provider의 id
     */
    private String providerId;

    @ElementCollection
    private final List<Long> diaryIds = new ArrayList<>();

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<WriterAuthority> writerAuthorities = new HashSet<>();

    public Writer() {
    }

    /**
     * @param email      이메일 (github의 경우 null일 수 있다.)
     * @param password   암호화된 비밀 번호 (OAuth 로그인의 경우 null과 마찬가지)
     * @param provider   OAuth 제공자 (Form Login의 경우 null)
     * @param providerId OAuth 제공자 식별자 (Form Login의 경우 null)
     */
    @Builder
    public Writer(String name, String email, String password, String provider, String providerId) {
        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
        checkArgument(EmailChecker.checkEmail(email), "String must be format of email.");
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
    }

    public Long getId() {
        return writerId;
    }

    public String getName() {
        return name;
    }

    public void modifyName(String name) {
        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void modifyEmail(String email) {
        checkArgument(EmailChecker.checkEmail(email), "String must be format of email.");
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public Set<WriterAuthority> getWriterAuthorities() {
        return writerAuthorities;
    }

    public void addWriterAuthority(WriterAuthority writerAuthority) {
        this.writerAuthorities.add(writerAuthority);
    }

    public void addDiary(Long diaryId) {
        this.diaryIds.add(diaryId);
    }

    public void removeDiary(Long diaryId) {
        this.diaryIds.remove(diaryId);
    }

    public void removeWriterAuthorities(WriterAuthority writerAuthority) {
        checkArgument(this.writerAuthorities.contains(writerAuthority), "writer has not that authority.");
        this.writerAuthorities.remove(writerAuthority);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", writerId)
                .append("name", name)
                .append("email", email)
                .append("provider", provider)
                .toString();
    }

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
}
