package com.dasd412.api.diaryservice.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 복합키 엔티티의 Id를 생성할 때, 타입 정확성을 위해 만든 보조 클래스
 */
public class EntityId<R, Long> {

    /**
     * R에는 엔티티 객체의 클래스 정보를 넣고 Long 에는 해당 엔티티의 식별자를 넣습니다.
     */
    private final Class<R> reference;

    private final Long id;

    /**
     * @param reference 객체의 클래스 정보
     * @param id        객체의 식별자
     */
    private EntityId(Class<R> reference, Long id) {
        this.reference = reference;
        this.id = id;
    }

    public static <R, Long> EntityId<R, Long> of(Class<R> reference, Long id) {
        checkNotNull(reference, "entity reference must be provided");
        checkNotNull(id, "entity id must be provided");

        return new EntityId<>(reference, id);
    }


    public Long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EntityId<?, ?> target = (EntityId<?, ?>) obj;
        return Objects.equals(reference, target.reference) && Objects
                .equals(id, target.id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("reference", reference.getSimpleName())
                .append("entityId", id)
                .toString();
    }
}
