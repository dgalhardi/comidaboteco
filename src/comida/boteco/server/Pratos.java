package comida.boteco.server;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Pratos {
	
	@Id
	private Integer id;
	private String nome;
	private String endereco;
	private Double preco;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public Double getPreco() {
		return preco;
	}
	public void setPreco(Double preco) {
		this.preco = preco;
	}


}
