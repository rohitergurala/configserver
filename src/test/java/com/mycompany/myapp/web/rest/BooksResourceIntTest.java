package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.ReportingserviceApp;

import com.mycompany.myapp.domain.Books;
import com.mycompany.myapp.repository.BooksRepository;
import com.mycompany.myapp.repository.search.BooksSearchRepository;
import com.mycompany.myapp.service.BooksService;
import com.mycompany.myapp.service.dto.BooksDTO;
import com.mycompany.myapp.service.mapper.BooksMapper;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BooksResource REST controller.
 *
 * @see BooksResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReportingserviceApp.class)
public class BooksResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private BooksRepository booksRepository;

    @Autowired
    private BooksMapper booksMapper;

    @Autowired
    private BooksService booksService;

    /**
     * This repository is mocked in the com.mycompany.myapp.repository.search test package.
     *
     * @see com.mycompany.myapp.repository.search.BooksSearchRepositoryMockConfiguration
     */
    @Autowired
    private BooksSearchRepository mockBooksSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restBooksMockMvc;

    private Books books;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BooksResource booksResource = new BooksResource(booksService);
        this.restBooksMockMvc = MockMvcBuilders.standaloneSetup(booksResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Books createEntity() {
        Books books = new Books()
            .name(DEFAULT_NAME)
            .price(DEFAULT_PRICE)
            .author(DEFAULT_AUTHOR)
            .description(DEFAULT_DESCRIPTION);
        return books;
    }

    @Before
    public void initTest() {
        booksRepository.deleteAll();
        books = createEntity();
    }

    @Test
    public void createBooks() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();

        // Create the Books
        BooksDTO booksDTO = booksMapper.toDto(books);
        restBooksMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(booksDTO)))
            .andExpect(status().isCreated());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate + 1);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBooks.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testBooks.getAuthor()).isEqualTo(DEFAULT_AUTHOR);
        assertThat(testBooks.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).save(testBooks);
    }

    @Test
    public void createBooksWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = booksRepository.findAll().size();

        // Create the Books with an existing ID
        books.setId("existing_id");
        BooksDTO booksDTO = booksMapper.toDto(books);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBooksMockMvc.perform(post("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(booksDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeCreate);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(0)).save(books);
    }

    @Test
    public void getAllBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        // Get all the booksList
        restBooksMockMvc.perform(get("/api/books?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    public void getBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(books.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    public void getNonExistingBooks() throws Exception {
        // Get the books
        restBooksMockMvc.perform(get("/api/books/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Update the books
        Books updatedBooks = booksRepository.findById(books.getId()).get();
        updatedBooks
            .name(UPDATED_NAME)
            .price(UPDATED_PRICE)
            .author(UPDATED_AUTHOR)
            .description(UPDATED_DESCRIPTION);
        BooksDTO booksDTO = booksMapper.toDto(updatedBooks);

        restBooksMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(booksDTO)))
            .andExpect(status().isOk());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);
        Books testBooks = booksList.get(booksList.size() - 1);
        assertThat(testBooks.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBooks.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testBooks.getAuthor()).isEqualTo(UPDATED_AUTHOR);
        assertThat(testBooks.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).save(testBooks);
    }

    @Test
    public void updateNonExistingBooks() throws Exception {
        int databaseSizeBeforeUpdate = booksRepository.findAll().size();

        // Create the Books
        BooksDTO booksDTO = booksMapper.toDto(books);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBooksMockMvc.perform(put("/api/books")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(booksDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Books in the database
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(0)).save(books);
    }

    @Test
    public void deleteBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);

        int databaseSizeBeforeDelete = booksRepository.findAll().size();

        // Delete the books
        restBooksMockMvc.perform(delete("/api/books/{id}", books.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Books> booksList = booksRepository.findAll();
        assertThat(booksList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Books in Elasticsearch
        verify(mockBooksSearchRepository, times(1)).deleteById(books.getId());
    }

    @Test
    public void searchBooks() throws Exception {
        // Initialize the database
        booksRepository.save(books);
        when(mockBooksSearchRepository.search(queryStringQuery("id:" + books.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(books), PageRequest.of(0, 1), 1));
        // Search the books
        restBooksMockMvc.perform(get("/api/_search/books?query=id:" + books.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(books.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Books.class);
        Books books1 = new Books();
        books1.setId("id1");
        Books books2 = new Books();
        books2.setId(books1.getId());
        assertThat(books1).isEqualTo(books2);
        books2.setId("id2");
        assertThat(books1).isNotEqualTo(books2);
        books1.setId(null);
        assertThat(books1).isNotEqualTo(books2);
    }

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BooksDTO.class);
        BooksDTO booksDTO1 = new BooksDTO();
        booksDTO1.setId("id1");
        BooksDTO booksDTO2 = new BooksDTO();
        assertThat(booksDTO1).isNotEqualTo(booksDTO2);
        booksDTO2.setId(booksDTO1.getId());
        assertThat(booksDTO1).isEqualTo(booksDTO2);
        booksDTO2.setId("id2");
        assertThat(booksDTO1).isNotEqualTo(booksDTO2);
        booksDTO1.setId(null);
        assertThat(booksDTO1).isNotEqualTo(booksDTO2);
    }
}
