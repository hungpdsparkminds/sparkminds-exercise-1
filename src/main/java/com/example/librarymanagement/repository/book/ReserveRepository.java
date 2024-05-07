package com.example.librarymanagement.repository.book;

import com.example.librarymanagement.model.entity.book.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {
}
