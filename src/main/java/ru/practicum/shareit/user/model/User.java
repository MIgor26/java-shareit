package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Оставляю переопределение метода как пример того, как нужно переопределять данный метод в Hibernate
    // Дело в том, что у Hibernate свой механизм и могут быть конфликты.
    // Это касается только моделей, которые соответствуют таблицам в базе данных.
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object== null || Hibernate.getClass(this) != Hibernate.getClass(object)) return false;
        User user = (User) object;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
