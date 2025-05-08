package com.atos.piam.lms.common;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Contract for a generic dto & apiDto mapper.
 *
 * @param <D> - DTO type parameter.
 * @param <A> - API DTO type parameter.
 */

public interface EntityMapper<A, D> {
    D toDto(A apiDto);

    A toApiDto(D dto);

    List<D> toDto(List<A> apiDtoList);

    List<A> toApiDto(List<D> dtoList);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget D dto, A apiDto);
}
