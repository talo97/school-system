package com.schoolsystem.user;

import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.common.EnumUserGroup;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceUserImpl extends CommonServiceImpl<EntityUser, DaoUser> implements ServiceUser {

    private final ModelMapper modelMapper;

    @Autowired
    public ServiceUserImpl(DaoUser repository, ModelMapper modelMapper) {
        super(repository);
        this.modelMapper = modelMapper;
    }

    @Override
    public Optional<EntityUser> getByLogin(String login) {
        return repository.findByLogin(login);
    }

    @Override
    public Optional<EntityUser> save(UserPostDTO userDTO) {
        //TODO::test, change later
        Optional<EntityUser> user;
        user = Optional.of(modelMapper.map(userDTO, EntityUser.class));
        user.get().setUserGroup(EnumUserGroup.DEFAULT);
        this.save(user.get());
        return user;
    }
}
