package com.devsuperior.bds04.services;

import java.io.Serializable;
import java.util.Optional;

import javax.management.AttributeNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds04.dto.RoleDTO;
import com.devsuperior.bds04.dto.UserDTO;
import com.devsuperior.bds04.entities.Role;
import com.devsuperior.bds04.entities.User;
import com.devsuperior.bds04.repositories.RoleRepository;
import com.devsuperior.bds04.repositories.UserRepository;


@Service
public class UserService implements UserDetailsService, Serializable {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(UserService.class); 
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@Transactional(readOnly = true) 
	public Page<UserDTO> findAllPaged(Pageable pageable){
		Page<User> list = repository.findAll(pageable); 
		return list.map( x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) throws AttributeNotFoundException {
		Optional<User> obj = repository.findById(id); 
		User entity = obj.orElseThrow( () -> new AttributeNotFoundException("Ops! Usuário não cadastrado") );
		return new UserDTO(entity);
	}

	
	@Transactional
	public UserDTO insert(UserDTO dto) {
		
		User entity = new User(); 
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity); 
		return new UserDTO(entity);
	}
	
	private void copyDtoToEntity(UserDTO dto, User entity) {

		entity.setEmail( dto. getEmail() );
		
		entity.getRoles().clear();
		
		for(RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = repository.findByEmail(username); 
		
		if(user == null) {
			logger.error("User Not Found: "+username); 
			throw new UsernameNotFoundException("Email Not Found!");
		}
		logger.info("User Found: "+username); 
		return user;
	}
	
}
