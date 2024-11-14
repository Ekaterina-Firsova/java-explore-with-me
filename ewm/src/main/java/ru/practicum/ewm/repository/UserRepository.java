package ru.practicum.ewm.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.entity.User;
import org.springframework.data.domain.Page;

import java.net.ContentHandler;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Page<User> findAllByIdIn(List<Long> ids, Pageable pageable);

    List<User> findByIdIn(List<Long> ids, Sort sort);

    boolean existsByEmail(String email);
}
