package com.msoft.mbi.cube.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Conexao {

  public static ResultSet getConnection() {

    try {
            Class.forName("com.informix.jdbc.IfxDriver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:informix-sqli:lndb03:60009:DATABASE=pasa;INFORMIXSERVER=cslpasasoc;", "gil", "pv14er");
      
      //Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
      //Connection con = DriverManager.getConnection("jdbc:informix-sqli:10.15.10.11:5800:DATABASE=v1000tt;INFORMIXSERVER=logix10soc;", "desenv", "logixpb");
      
      //String sql = "SELECT Fornecedores.Pa�s, Fornecedores.Regi�o, Fornecedores.Cidade, Fornecedores.NomeDaEmpresa as NomeDaEmpresaFor, Categorias.NomeDaCategoria, Produtos.NomeDoProduto, Clientes.NomeDaEmpresa as NomeDaEmpresaCli, Funcion�rios.Nome, Funcion�rios.Cargo, Transportadoras.NomeDaEmpresa, Pedidos.DataDoPedido, [Detalhes do Pedido].Pre�oUnit�rio, [Detalhes do Pedido].Quantidade, [Detalhes do Pedido].Desconto FROM Categorias INNER JOIN (Clientes INNER JOIN ((Fornecedores INNER JOIN Produtos ON Fornecedores.C�digoDoFornecedor = Produtos.C�digoDoFornecedor) INNER JOIN ((Transportadoras INNER JOIN (Funcion�rios INNER JOIN Pedidos ON Funcion�rios.C�digoDoFuncion�rio = Pedidos.C�digoDoFuncion�rio) ON Transportadoras.C�digoDaTransportadora = Pedidos.Via) INNER JOIN [Detalhes do Pedido] ON Pedidos.N�meroDoPedido = [Detalhes do Pedido].N�meroDoPedido) ON Produtos.C�digoDoProduto = [Detalhes do Pedido].C�digoDoProduto) ON Clientes.C�digoDoCliente = Pedidos.C�digoDoCliente) ON Categorias.C�digoDaCategoria = Produtos.C�digoDaCategoria order by 1,2,3,4,5,6,7,8,9,10,11";
      //String sql = "SELECT Fornecedores.Pa�s, Fornecedores.Regi�o, Fornecedores.Cidade, Fornecedores.NomeDaEmpresa as NomeDaEmpresaFor, Categorias.NomeDaCategoria, Produtos.NomeDoProduto, Clientes.NomeDaEmpresa as NomeDaEmpresaCli, Funcion�rios.Nome, Funcion�rios.Cargo, Transportadoras.NomeDaEmpresa, Pedidos.DataDoPedido, [Detalhes do Pedido].Pre�oUnit�rio, [Detalhes do Pedido].Quantidade, [Detalhes do Pedido].Desconto FROM Categorias INNER JOIN (Clientes INNER JOIN ((Fornecedores INNER JOIN Produtos ON Fornecedores.C�digoDoFornecedor = Produtos.C�digoDoFornecedor) INNER JOIN ((Transportadoras INNER JOIN (Funcion�rios INNER JOIN Pedidos ON Funcion�rios.C�digoDoFuncion�rio = Pedidos.C�digoDoFuncion�rio) ON Transportadoras.C�digoDaTransportadora = Pedidos.Via) INNER JOIN [Detalhes do Pedido] ON Pedidos.N�meroDoPedido = [Detalhes do Pedido].N�meroDoPedido) ON Produtos.C�digoDoProduto = [Detalhes do Pedido].C�digoDoProduto) ON Clientes.C�digoDoCliente = Pedidos.C�digoDoCliente) ON Categorias.C�digoDaCategoria = Produtos.C�digoDaCategoria ";
      //String sql = "select apont_horas.data data, apont_horas.h_inicio h_inicio,apont_horas.h_fim h_fim,apont_horas.num_docum num_docum,funci.nom_funci nom_funci,SUM(apont_horas.qte_horas) qte_horas,SUM(funci.qtd_horas_dia) qtd_horas_dia from funci funci, apont_horas apont_horas where funci.cod_funci=apont_horas.num_matricula AND (      (      funci.nom_funci = 'FERNANDO JO�O BORGES' or funci.nom_funci = 'ZILEI CAROLINA DA SILVEIR' or funci.nom_funci = 'ALEXANDRE ALTAIR DE MELO' or funci.nom_funci = 'DECIO HEINZELMANN LUCKOW'   )   and (apont_horas.data >= '2007-07-01' and apont_horas.data <= '2007-07-31')) group by apont_horas.data,apont_horas.h_inicio,apont_horas.h_fim,apont_horas.num_docum,funci.nom_funci";
      String sql = "SELECT apont_horas.data data, apont_horas.h_inicio h_inicio,apont_horas.h_fim h_fim,apont_horas.num_docum num_docum,funci.nom_funci nom_funci,SUM(apont_horas.qte_horas) qte_horas,SUM(funci.qtd_horas_dia) qtd_horas_dia from funci funci, apont_horas apont_horas where funci.cod_funci=apont_horas.num_matricula AND ((funci.nom_funci = 'FERNANDO JO�O BORGES' or funci.nom_funci = 'ZILEI CAROLINA DA SILVEIR' or funci.nom_funci = 'ALEXANDRE ALTAIR DE MELO' or funci.nom_funci = 'ALEXANDRO MEIER' or funci.nom_funci = 'DECIO HEINZELMANN LUCKOW') ) AND (apont_horas.data >= '2008-03-01' and apont_horas.data <= '2008-03-31') group by apont_horas.data,         apont_horas.h_inicio,         apont_horas.h_fim,         apont_horas.num_docum,         funci.nom_funci";
      
      
      //String sql = "select pedidos.cod_empresa cod_empresa,1 Fixo,pedidos.num_pedido num_pedido,pedidos.cod_cliente cod_cliente,pedidos.pct_comissao pct_comissao,pedidos.num_pedido_repres num_pedido_repres,pedidos.dat_emis_repres dat_emis_repres,pedidos.cod_nat_oper cod_nat_oper,pedidos.cod_transpor cod_transpor,pedidos.cod_consig cod_consig,pedidos.ies_finalidade ies_finalidade,pedidos.ies_frete ies_frete,pedidos.ies_preco ies_preco,pedidos.cod_cnd_pgto cod_cnd_pgto,pedidos.pct_desc_financ pct_desc_financ,pedidos.ies_embal_padrao ies_embal_padrao,pedidos.ies_tip_entrega ies_tip_entrega,pedidos.ies_aceite ies_aceite,pedidos.ies_sit_pedido ies_sit_pedido,pedidos.dat_pedido dat_pedido,pedidos.num_pedido_cli num_pedido_cli,pedidos.pct_desc_adic pct_desc_adic,pedidos.num_list_preco num_list_preco,pedidos.cod_repres cod_repres,pedidos.cod_repres_adic cod_repres_adic,pedidos.dat_alt_sit dat_alt_sit,pedidos.dat_cancel dat_cancel,pedidos.cod_tip_venda cod_tip_venda,pedidos.cod_motivo_can cod_motivo_can,pedidos.dat_ult_fatur dat_ult_fatur,pedidos.cod_moeda cod_moeda,pedidos.ies_comissao ies_comissao,pedidos.pct_frete pct_frete,pedidos.cod_tip_carteira cod_tip_carteira,pedidos.num_versao_lista num_versao_lista,pedidos.cod_local_estoq cod_local_estoq, SUM(pedidos.num_versao_lista) num_versao_lista from pedidos pedidos group by pedidos.cod_empresa,pedidos.num_pedido,pedidos.cod_cliente,pedidos.pct_comissao,pedidos.num_pedido_repres,pedidos.dat_emis_repres,pedidos.cod_nat_oper,pedidos.cod_transpor,pedidos.cod_consig,pedidos.ies_finalidade,pedidos.ies_frete,pedidos.ies_preco,pedidos.cod_cnd_pgto,pedidos.pct_desc_financ,pedidos.ies_embal_padrao,pedidos.ies_tip_entrega,pedidos.ies_aceite,pedidos.ies_sit_pedido,pedidos.dat_pedido,pedidos.num_pedido_cli,pedidos.pct_desc_adic,pedidos.num_list_preco,pedidos.cod_repres,pedidos.cod_repres_adic,pedidos.dat_alt_sit,pedidos.dat_cancel,pedidos.cod_tip_venda,pedidos.cod_motivo_can,pedidos.dat_ult_fatur,pedidos.cod_moeda,pedidos.ies_comissao,pedidos.pct_frete,pedidos.cod_tip_carteira,pedidos.num_versao_lista,pedidos.cod_local_estoq";

      PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

      return ps.executeQuery();

    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

}
