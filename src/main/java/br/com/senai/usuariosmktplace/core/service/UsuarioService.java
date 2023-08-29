package br.com.senai.usuariosmktplace.core.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import br.com.senai.usuariosmktplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmktplace.core.dao.FactoryDao;
import br.com.senai.usuariosmktplace.core.domain.Usuario;
import jakarta.annotation.PostConstruct;

@Service 
public class UsuarioService {
	
	@Autowired
	private FactoryDao factoryDao ;
	
	private DaoUsuario dao;
	
	@PostConstruct
	public void inicializar() {
		
		this.dao = factoryDao.getDaoUsuario();
		
	}
	
	public String resetSenha(String login) {
		
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login),
				"O login é obrigatorio ");
		
		Usuario usuarioEncontrado = dao.buscarPor(login);
			
		Preconditions.checkNotNull(usuarioEncontrado,"Não foi encontrado"
				+ "usúario vinculado ao login");
		
		String novaSenha = this.gerarRandom();
		
		usuarioEncontrado.setSenha(novaSenha);
	
		dao.altera(usuarioEncontrado);
	
		return novaSenha ;
	}
	
	public Usuario criarPor(String nomeCompleto , String senha ) {
		
		this.validar(nomeCompleto, senha);
		String login = gerarLoginPor(nomeCompleto);
		String senhaCriptografada = gerarHashDa(senha);
		Usuario novoUsuario = new Usuario(login, senhaCriptografada, nomeCompleto);
		
		this.dao.inserir(novoUsuario);
		
		Usuario usuarioSalvo = dao.buscarPor(login) ;
		
		return usuarioSalvo;
		
	}
	
	public Usuario atualizarPor(String login , String nomeCompleto ,
								String senhaAntiga ,String senhaNova ) {
		
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login),
					" O login é obrigatorio para atualização");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(senhaAntiga ),
					" A senha antiga é obrigatoria para atualização");
		
		this.validar(nomeCompleto ,senhaNova);
		
		Usuario usuarioSalvo = dao.buscarPor(login);
		
		Preconditions.checkNotNull(usuarioSalvo,
					"não foi encontrado usúario vinculado ao login");
		
		String senhaAntigaCriptografada = gerarHashDa(senhaAntiga);
		
		boolean isSenhaValida = senhaAntigaCriptografada.equals(usuarioSalvo.getSenha());
		
		Preconditions.checkArgument(isSenhaValida);
		
		Preconditions.checkArgument(!senhaAntiga.equals(senhaNova),
					"A senha nova não pode ser igual a anterior");
		
		String senhaNovaCriptografada = gerarHashDa(senhaNova);
		
		Usuario usuarioAlterado =new Usuario(login, senhaNovaCriptografada, nomeCompleto);
		
		this.dao.altera(usuarioAlterado);
		
		usuarioAlterado = dao.buscarPor(login);
		
		return usuarioAlterado ;
		
		
	}
	

	@SuppressWarnings("unused")
	private Usuario buscarPor(String  login ) {
		
		Preconditions.checkArgument(!Strings.isNullOrEmpty(login),
									"O login é obrigatorio ");
		
		Usuario usuarioEncontrado = dao.buscarPor(login);
		
		Preconditions.checkNotNull(usuarioEncontrado,"Não foi encontrado"
												+ "usúario vinculado ao login");
		return usuarioEncontrado ;
	
	}
	
	private String removerAcentoDo (String nomeCompleto ){
		return Normalizer.normalize(nomeCompleto, Normalizer.Form.NFD)
									.replaceAll("[^\\p{ASCII}]" , "");
	}

	private List<String> fracionar(String nomeCompleto ){
		List<String> nomeFracionado = new ArrayList<String>();
		if (!Strings.isNullOrEmpty(nomeCompleto)){
			nomeCompleto = nomeCompleto.trim();
			String [] partesDoNome = nomeCompleto.split(" ") ;
			
			for (String parte : partesDoNome) {
				
				boolean isNaoContemArtigo = !parte.equalsIgnoreCase("de") 
						&& !parte.equalsIgnoreCase("e")
						&& !parte.equalsIgnoreCase("dos")
						&& !parte.equalsIgnoreCase("da")
						&& !parte.equalsIgnoreCase("das");
				
				if(isNaoContemArtigo) {
					nomeFracionado.add(parte.toLowerCase().trim());
				}
	
			}
			
		}
		return nomeFracionado;
	}
	
	private String gerarLoginPor (String nomeCompleto ) {
		
		nomeCompleto = removerAcentoDo(nomeCompleto);
		List<String> partesDoNome = fracionar(nomeCompleto);
		
		String loginGerado = null ;
		Usuario usuarioEncontrado = null ;
	
		if (!partesDoNome.isEmpty()) {
			
			for(int i = 1; i < partesDoNome.size(); i++) {
						
				loginGerado = partesDoNome.get(0) + "." + partesDoNome.get(i);
				usuarioEncontrado = dao.buscarPor(loginGerado);
				
				if (usuarioEncontrado == null ) {
					if (loginGerado.length() > 50) {
						loginGerado = loginGerado.substring(0,40);
					}
					
					return loginGerado ;
				}		
			
			}
			
			int proximoSequencial = 0;
			String loginDisponivel = null ;
					
			while (usuarioEncontrado != null) {
				loginDisponivel = loginGerado + ++proximoSequencial ;
				usuarioEncontrado = dao.buscarPor(loginDisponivel);
			}
			
			loginGerado = loginDisponivel ;
			
		}
		
		return loginGerado ;
		
	}	
	
	private String gerarRandom() {
		
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		Random r = new Random();
		String c = "" ;
		
		for (int i = 0 ; i < 10 ; i++ ){
			
			 c += alphabet.charAt(r.nextInt(alphabet.length()));
		}
		
		return c ;
		
	}
	
	private String gerarHashDa(String senha) {
        
		return new DigestUtils(MessageDigestAlgorithms.SHA3_256).digestAsHex(senha);
        
    }
	
	@SuppressWarnings("deprecation")
	private void validar(String senha ) {
								 
		boolean isSenhaValida = !Strings.isNullOrEmpty(senha) 
								&& senha.length() >= 6 
								&& senha.length() <= 15 ;
						
		Preconditions.checkArgument(isSenhaValida , "A senha é obrigatoria deve "
													+ "conter entre 6 e 15 caracteres .");
								 
		boolean isContemLetra = CharMatcher.inRange('a', 'z').countIn(senha.toLowerCase()) > 0;
		boolean isContemNumero =  CharMatcher.inRange('0', '9').countIn(senha) > 0;
		boolean isCaracterInvalido = !CharMatcher.javaLetterOrDigit().matchesAllOf(senha);
		
		Preconditions.checkArgument(isContemNumero 
									&& isContemLetra 
									&& ! isCaracterInvalido,
									"A senha deve conter letra e numeros "
									+ "e não pode aver espaços .");
		
	}
	
	private void validar(String nomeCompleto , String senha) {
		
		List<String> partesDoNome = fracionar(nomeCompleto) ;
		boolean isNomeCompleto = partesDoNome.size() > 1 ;
		boolean isNomeValido = !Strings.isNullOrEmpty(nomeCompleto) && isNomeCompleto 
																   && nomeCompleto.length() >= 5
																   && nomeCompleto.length() <= 120 ;
																   
	   Preconditions.checkArgument(isNomeValido,"O nome é obrigatori e deve"
	   											+ " conter entre 5 e 120 caracteres "
	   											+ "e conter sobrenome também ");	
	   this.validar(senha);
	   
	}
	
}
