package com.mycompany.myapp.web.rest;
import com.mycompany.myapp.service.BooksService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import com.mycompany.myapp.service.dto.BooksDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Books.
 */
@RestController
@RequestMapping("/api")
public class BooksResource {

    private final Logger log = LoggerFactory.getLogger(BooksResource.class);

    private static final String ENTITY_NAME = "reportingserviceBooks";

    private final BooksService booksService;

    public BooksResource(BooksService booksService) {
        this.booksService = booksService;
    }

    /**
     * POST  /books : Create a new books.
     *
     * @param booksDTO the booksDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new booksDTO, or with status 400 (Bad Request) if the books has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/books")
    public ResponseEntity<BooksDTO> createBooks(@RequestBody BooksDTO booksDTO) throws URISyntaxException {
        log.debug("REST request to save Books : {}", booksDTO);
        if (booksDTO.getId() != null) {
            throw new BadRequestAlertException("A new books cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BooksDTO result = booksService.save(booksDTO);
        return ResponseEntity.created(new URI("/api/books/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /books : Updates an existing books.
     *
     * @param booksDTO the booksDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated booksDTO,
     * or with status 400 (Bad Request) if the booksDTO is not valid,
     * or with status 500 (Internal Server Error) if the booksDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/books")
    public ResponseEntity<BooksDTO> updateBooks(@RequestBody BooksDTO booksDTO) throws URISyntaxException {
        log.debug("REST request to update Books : {}", booksDTO);
        if (booksDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BooksDTO result = booksService.save(booksDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, booksDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /books : get all the books.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of books in body
     */
    @GetMapping("/books")
    public ResponseEntity<List<BooksDTO>> getAllBooks(Pageable pageable) {
        log.debug("REST request to get a page of Books");
        Page<BooksDTO> page = booksService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/books");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /books/:id : get the "id" books.
     *
     * @param id the id of the booksDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the booksDTO, or with status 404 (Not Found)
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<BooksDTO> getBooks(@PathVariable String id) {
        log.debug("REST request to get Books : {}", id);
        Optional<BooksDTO> booksDTO = booksService.findOne(id);
        return ResponseUtil.wrapOrNotFound(booksDTO);
    }

    /**
     * DELETE  /books/:id : delete the "id" books.
     *
     * @param id the id of the booksDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBooks(@PathVariable String id) {
        log.debug("REST request to delete Books : {}", id);
        booksService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/books?query=:query : search for the books corresponding
     * to the query.
     *
     * @param query the query of the books search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/books")
    public ResponseEntity<List<BooksDTO>> searchBooks(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Books for query {}", query);
        Page<BooksDTO> page = booksService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/books");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
