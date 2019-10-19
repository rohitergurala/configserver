package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Books;
import com.mycompany.myapp.service.dto.BooksDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2019-10-07T01:53:57+0400",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_202 (Oracle Corporation)"
)
@Component
public class BooksMapperImpl implements BooksMapper {

    @Override
    public Books toEntity(BooksDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Books books = new Books();

        books.setId( dto.getId() );
        books.setName( dto.getName() );
        books.setPrice( dto.getPrice() );
        books.setAuthor( dto.getAuthor() );
        books.setDescription( dto.getDescription() );

        return books;
    }

    @Override
    public BooksDTO toDto(Books entity) {
        if ( entity == null ) {
            return null;
        }

        BooksDTO booksDTO = new BooksDTO();

        booksDTO.setId( entity.getId() );
        booksDTO.setName( entity.getName() );
        booksDTO.setPrice( entity.getPrice() );
        booksDTO.setAuthor( entity.getAuthor() );
        booksDTO.setDescription( entity.getDescription() );

        return booksDTO;
    }

    @Override
    public List<Books> toEntity(List<BooksDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Books> list = new ArrayList<Books>( dtoList.size() );
        for ( BooksDTO booksDTO : dtoList ) {
            list.add( toEntity( booksDTO ) );
        }

        return list;
    }

    @Override
    public List<BooksDTO> toDto(List<Books> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<BooksDTO> list = new ArrayList<BooksDTO>( entityList.size() );
        for ( Books books : entityList ) {
            list.add( toDto( books ) );
        }

        return list;
    }
}
