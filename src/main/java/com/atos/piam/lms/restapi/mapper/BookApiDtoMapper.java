package com.atos.piam.lms.restapi.mapper;
import org.mapstruct.*;

import com.atos.piam.lms.common.EntityMapper;
import com.atos.piam.lms.restapi.apidto.BookApiDto;
import com.atos.piam.lms.service.dto.Book;

/**
 * Mapper for the entity {@link BookApiDto} and its DTO {@link Book}.
 */
@Mapper(componentModel = "spring")
public interface BookApiDtoMapper extends EntityMapper<BookApiDto, Book> {
    

}
