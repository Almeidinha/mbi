package com.msoft.mbi.cube.multi.renderers.linkHTML;

public class LinkHTMLSVGColuna extends LinkHTML {

	private String				id;
	private Integer				largura;
	private Integer				altura;
	private String				hint		= "";
	private String				classe;

	public LinkHTMLSVGColuna() {
		super();
	}
	
	public LinkHTMLSVGColuna(String id, String classe) {
		super();
		this.id = id;
		this.classe = classe;
	}

	public LinkHTMLSVGColuna(String id, String classe, String hint, Integer altura, Integer largura) {
		this(id, classe);
		this.altura = altura;
		this.largura = largura;
		this.hint = hint;
	}
	
	public LinkHTMLSVGColuna(String classe, String hint,  Integer altura, Integer largura) {
		this.classe = classe;
		this.hint = hint;
		this.altura = altura;
		this.largura = largura;
	}

	@Override
	public String toString() {
		StringBuilder sbImagem = new StringBuilder();
		String altura = this.altura != null ? " height:" + this.altura + "px;" : "";
		String largura = this.largura != null ? " width:" + this.largura + "px;" : "";
		String id = this.id != null? " id='"+ this.id +"'" : " ";
		sbImagem.append("<div").append(id).append("' class='").append(this.classe).append("' style='margin: auto; padding: 0px 2px; cursor: pointer;").append(altura).append(largura).append("'").append(" title='").append(this.hint).append("'");
		
		for(String param : this.getParametros().keySet()) {
			sbImagem.append(" ").append(param).append("='").append(this.getParametros().get(param)).append("'");
		}
		
		sbImagem.append("></div>");
		return sbImagem.toString();
	}
}
