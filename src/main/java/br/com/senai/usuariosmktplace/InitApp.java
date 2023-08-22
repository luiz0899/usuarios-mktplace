package br.com.senai.usuariosmktplace;

import br.com.senai.usuariosmktplace.core.domain.Usuario;
import br.com.senai.usuariosmktplace.core.service.UsuarioService;

public class InitApp {

	public static void main(String[] args) {
		
		System.out.println(new UsuarioService().removerAcentoDo("José da "));
		System.out.println(new UsuarioService().fracionar("José da silva alburque dos anjo e braga "));
		System.out.println(new UsuarioService().gerarHashDa("4444"));
		System.out.println(new UsuarioService().gerarLoginPor("luiz henrique pereira corrêa"));
		
		UsuarioService serviceUsu = new UsuarioService() ;
		
		Usuario usuario = new Usuario("", "4444","luiz henrique pereira corrêa");
		
		serviceUsu.salvar(usuario);
		
		System.out.println("aq");
		
		
		
	}

}
