package sit.int204.itbmsbackend.utils;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import sit.int204.itbmsbackend.dtos.PageDTO;


import java.util.List;

public class ListMapper {
    private static final ListMapper listMapper = new ListMapper();

    private ListMapper() {}
    public static ListMapper getInstance() {
        return listMapper;
    }

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        return source.stream().map(entity -> modelMapper.map(entity, targetClass)).toList();
    }

    public <S, T> PageDTO<T> toPageDTO(Page<S> source, Class<T> targetClass, ModelMapper modelMapper) {
        PageDTO<T> page = modelMapper.map(source, PageDTO.class);
        page.setContent(mapList(source.getContent(), targetClass, modelMapper));
        return page;
    }
}
