package com.schoolsystem.common;

import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
@Getter
public abstract class CommonEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommonEntity that = (CommonEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
