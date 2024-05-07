package com.example.librarymanagement.repository.book;

import com.example.librarymanagement.model.entity.book.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
