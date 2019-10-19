package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.service.BooksService;
import com.mycompany.myapp.domain.Books;
import com.mycompany.myapp.repository.BooksRepository;
//import com.mycompany.myapp.repository.search.BooksSearchRepository;
import com.mycompany.myapp.service.dto.BooksDTO;
import com.mycompany.myapp.service.mapper.BooksMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Books.
 */
@Service
public class BooksServiceImpl implements BooksService {

    private final Logger log = LoggerFactory.getLogger(BooksServiceImpl.class);

    private final BooksRepository booksRepository;

    private final BooksMapper booksMapper;

//    private final BooksSearchRepository booksSearchRepository;

    public BooksServiceImpl(BooksRepository booksRepository, BooksMapper booksMapper/*, BooksSearchRepository booksSearchRepository*/) {
        this.booksRepository = booksRepository;
        this.booksMapper = booksMapper;
//        this.booksSearchRepository = booksSearchRepository;
    }

    /**
     * Save a books.
     *
     * @param booksDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public BooksDTO save(BooksDTO booksDTO) {
        log.debug("Request to save Books : {}", booksDTO);
        Books books = booksMapper.toEntity(booksDTO);
        books = booksRepository.save(books);
        BooksDTO result = booksMapper.toDto(books);
//        booksSearchRepository.save(books);
        return result;
    }

    /**
     * Get all the books.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<BooksDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Books");
        return booksRepository.findAll(pageable)
            .map(booksMapper::toDto);
    }


    /**
     * Get one books by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    public Optional<BooksDTO> findOne(String id) {
        log.debug("Request to get Books : {}", id);
        return booksRepository.findById(id)
            .map(booksMapper::toDto);
    }

    /**
     * Delete the books by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(String id) {
        log.debug("Request to delete Books : {}", id);        booksRepository.deleteById(id);
//        booksSearchRepository.deleteById(id);
    }

    /**
     * Search for the books corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    public Page<BooksDTO> search(String query, Pageable pageable) {
        /*log.debug("Request to search for a page of Books for query {}", query);
        return booksSearchRepository.search(queryStringQuery(query), pageable)
            .map(booksMapper::toDto);*/
        return null;
    }
}
