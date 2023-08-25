package br.com.senai.usuariosmktplace;

import br.com.senai.usuariosmktplace.core.domain.Usuario;
import br.com.senai.usuariosmktplace.core.service.UsuarioService;

public class InitApp {

	public static void main(String[] args) {
		
		UsuarioService service = new UsuarioService() ;
		
		service.criarPor("luiz henrique pereira", "luiz1234");
		
		Usuario usuario = new Usuario("luiz.henrique", "luiz123", "luiz henrique");
		System.out.println(usuario.getLogin());
		
		System.out.println("aqui");
		
	}

}
