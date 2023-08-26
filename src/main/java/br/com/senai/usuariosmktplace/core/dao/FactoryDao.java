package br.com.senai.usuariosmktplace.core.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import br.com.senai.usuariosmktplace.core.dao.postgres.DaoPostgresUsuarios;

@Service
public class FactoryDao {

	@Bean
	public DaoUsuario getDaoUsuario() {	
		return new DaoPostgresUsuarios();
		
	}
	
	
}
