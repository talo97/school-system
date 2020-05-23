package com.schoolsystem.user;

        import com.schoolsystem.common.CommonServiceImpl;
        import com.schoolsystem.common.EnumUserGroup;
        import com.schoolsystem.parent.EntityParent;
        import com.schoolsystem.parent.ServiceParent;
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
    public Optional<EntityUser> findByLogin(String login) {
        return repository.findByLogin(login);
    }

    @Override
    public EntityUser save(UserPostDTO userDTO, EnumUserType userType) {
        EntityUser user;
        user = modelMapper.map(userDTO, EntityUser.class);
        user.setUserType(userType);
        this.save(user);
        return user;
    }
}
