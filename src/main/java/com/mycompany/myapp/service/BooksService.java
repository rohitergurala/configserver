package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.BooksDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Books.
 */
public interface BooksService {

    /**
     * Save a books.
     *
     * @param booksDTO the entity to save
     * @return the persisted entity
     */
    BooksDTO save(BooksDTO booksDTO);

    /**
     * Get all the books.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<BooksDTO> findAll(Pageable pageable);


    /**
     * Get the "id" books.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<BooksDTO> findOne(String id);

    /**
     * Delete the "id" books.
     *
     * @param id the id of the entity
     */
    void delete(String id);

    /**
     * Search for the books corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<BooksDTO> search(String query, Pageable pageable);
}
