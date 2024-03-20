package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.util.BIUtil;

import java.util.Iterator;
import java.util.List;

public class FilterJsonTree {

    private Filters filters;
    private FiltersFunction filtersFunction;
    private StringBuilder tree;
    private List<String> openNodes;
    private String display = "none";
    private boolean showTree;
    private int indicatorIndex;
    private int panelIndex;
    private boolean displayEditorLink = true;
    private int graphCode;
    private boolean isFilterGraphic = false;

    

    public FilterJsonTree(Filters f, FiltersFunction filtrosFuncao, String nos, int indiceIndicador, int indicePainel, boolean exibirLinkEditor, int graphCode) throws BIException {
        this.filters = f;
        this.filtersFunction = filtrosFuncao;
        this.openNodes = BIUtil.stringToList(nos);
        this.indicatorIndex = indiceIndicador;
        this.panelIndex = indicePainel;
        this.displayEditorLink = exibirLinkEditor;
        this.graphCode = graphCode;
        this.isFilterGraphic = graphCode != 0;
        buildTree();
    }

    public FilterJsonTree(Filters f, FiltersFunction filtrosFuncao, int indiceIndicador, int indicePainel, int graphCode) throws BIException {
        this(f, filtrosFuncao, "", indiceIndicador, indicePainel, true, graphCode);
    }

    public FilterJsonTree(Filters f, FiltersFunction filtrosFuncao, boolean mostrar_toda_arvore, int indiceIndicador, int indicePainel, boolean exibirLinkEditor, int graphCode) throws BIException {
        this.filters = f;
        this.indicatorIndex = indiceIndicador;
        this.panelIndex = indicePainel;
        this.showTree = mostrar_toda_arvore;
        this.displayEditorLink = exibirLinkEditor;
        this.graphCode = graphCode;
        this.filtersFunction = filtrosFuncao;
        this.isFilterGraphic = graphCode != 0;
        buildTree();
    }

    public FilterJsonTree(Filters f, boolean mostrar_toda_arvore) throws BIException {
        this.filters = f;
        this.showTree = mostrar_toda_arvore;
        buildTree();
    }

    public FilterJsonTree() throws BIException {
        buildTree();
    }

    public String toString() {
        return this.tree.toString();
    }

    private void buildTree() throws BIException {
        tree = new StringBuilder();
        int treeLevel = 0;
        String display = (this.showTree || (this.openNodes != null && this.openNodes.contains("2"))) ? "" : "none";

        tree.append("<table id='tabela_arvore_filtro' style='width:90%; margin: auto; padding-top: 25px;'>\n")
                .append("	<tr>\n");

        if (this.displayEditorLink) {
            appendFilterLink(treeLevel);
        } else {
            appendFilterWithoutLink(treeLevel);
        }

        tree.append("	 </tr>\n")
                .append("  <tr> \n")
                .append("		<td class='filtro'> \n")
                .append("			<div id='bloco1' style='position:relative; width:100%; z-index:1;'>\n")
                .append("				<table>\n")
                .append("					<tr> \n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("'>\n")
                .append("							<div style='width: 20px;'></div>\n")
                .append("						</td>\n")
                .append("            			<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>\n")
                .append("							<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>\n")
                .append("						</td>\n");

        appendDimensionFilter(treeLevel);

        tree.append("					</tr>").append("\n")
                .append("					<tr> ").append("\n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                .append("							<div style='width: 20px;'></div>").append("\n")
                .append("						</td>").append("\n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>E</td>").append("\n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("'> ").append("\n");

        buildDimensionFilter(filters.getDimensionFilter(), "1", treeLevel);

        tree.append("					</tr>\n")
                .append("					<tr> \n")
                .append("						<td class='filtro'>\n")
                .append("							<div style='width: 20px;'></div>\n")
                .append("						</td>\n")
                .append("            			<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>\n")
                .append("							<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>\n")
                .append("						</td>\n");

        appendMetricFilters(treeLevel, display);

        if (!this.isFilterGraphic) {
            this.buildNonAggregatedMetric(treeLevel);
        }

        this.appendFilterFunction(treeLevel);

        tree.append("				</table>\n")
                .append("			</div>\n")
                .append("		</td>\n")
                .append("	</tr>\n")
                .append("</table>\n");

    }

    private void appendFilterFunction(int treeLevel) throws BIException {
        tree.append("			<tr>").append("\n")
                .append("				<td class='filtro'>").append("\n")
                .append("					<div style='width: 20px;'></div>").append("\n")
                .append("				</td>").append("\n");
        if (filters.getMetricSqlFilter() == null || filters.getMetricSqlFilter().isEmpty()) {
            tree.append("				<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>E</td>").append("\n");
        } else {
            tree.append("				<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>&nbsp;</td>").append("\n");
        }
        tree.append("			</tr>").append("\n")
                .append("			<tr> ").append("\n")
                .append("				<td class='filtro'>").append("\n")
                .append("					<div style='width: 20px;'></div>").append("\n")
                .append("				</td>").append("\n")
                .append("				<td class='filtro' style='vertical-align:top;'>").append("\n")
                .append("					<img src='imagens/line_bottom_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_bottom'>").append("\n")
                .append("				</td>").append("\n")
                .append("				<td class='filtro'>").append("\n")
                .append("					<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                .append("					<a href='javascript:navegacao(\"3\"); mostra_esconde(\"bloco3\")' title='Abrir a estrutura de filtros nesse nível'><span>Fun&ccedil;&otilde;es</span></a>").append("\n")
                .append("					<a href='javascript:filter(\"3\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                .append("						<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                .append("					</a>").append("\n")
                .append("				</td> ").append("\n")
                .append("			</tr>");

        this.buildFilterFunction(treeLevel);
    }

    private void buildNonAggregatedMetric(int treeLevel) throws BIException {

        tree.append("          <tr>").append("\n")
                .append("            <td class='filtro'>").append("\n")
                .append("				<div style='width: 20px;'></div>").append("\n")
                .append("			</td>").append("\n");
        if (filters.getMetricFilters() == null || filters.getMetricFilters().isEmpty()) {
            tree.append("				<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>E</td>").append("\n");
        } else {
            tree.append("				<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>&nbsp;</td>").append("\n");
        }
        tree.append("			</tr>").append("\n")
                .append("			<tr> ").append("\n")
                .append("				<td class='filtro'>").append("\n")
                .append("					<div style='width: 20px;'></div>").append("\n")
                .append("				</td>").append("\n")
                .append("            	<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>").append("\n")
                .append("					<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>").append("\n")
                .append("				</td>").append("\n");
        if (filters.getMetricSqlFilter() == null || filters.getMetricSqlFilter().size() <= 0) {
            tree.append("				<td class='filtro'>").append("\n")
                    .append("					<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'><span>Métrica não agregada (aplicada na SQL)</span>").append("\n")
                    .append("						<a href='javascript:filter(\"4\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("							<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("						</a>").append("\n")
                    .append("					</td> ").append("\n")
                    .append("				</tr>").append("\n");
        } else {
            tree.append("				<td class='filtro'>").append("\n")
                    .append("				<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append("				<a href='javascript:navegacao(\"4\"); mostra_esconde(\"bloco4\")' title='Abrir a estrutura de filtros nesse nível'><span>Métrica não agregada (aplicada na SQL)</span></a>&nbsp;").append("\n")
                    .append("				<a href='javascript:filter(\"4\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("					<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("				</a>").append("\n")
                    .append("			</td> ").append("\n")
                    .append("          </tr>").append("\n")
                    .append("          <tr> ").append("\n")
                    .append("				<td class='filtro'>").append("\n")
                    .append("					<div style='width: 20px;'></div>").append("\n")
                    .append("				</td>").append("\n")
                    .append("				<td class='filtro_").append(this.getColor(treeLevel)).append("'  align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg' >E</td>").append("\n")
                    .append("				<td class='filtro'> ").append("\n");

            display = (this.showTree || (this.openNodes != null && this.openNodes.contains("4"))) ? "" : "none";

            tree.append("					<div id='bloco4' style='position:relative; width:100%; z-index:1; display:").append(display).append("'> ").append("\n")
                    .append("						<table>").append("\n");

            buildMetricSqlFilter(treeLevel);

            tree.append("						</table>").append("\n");
            tree.append("					</div>").append("\n");
            tree.append("				</td>").append("\n");
            tree.append("			</tr>").append("\n");
        }

    }

    private void appendFilterLink(int treeLevel) {
        tree.append("	 	<td style='width:94%;' class='filtro'>\n")
                .append("			<a href='javascript:navegacao(\"1\"); mostra_esconde(\"bloco1\")' title='Abrir a estrutura de filtros nesse nível'>Filters</a>\n")
                .append("			<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>\n")
                .append("		</td>\n")
                .append("    	<td style='width:6%;' class='filtro'>\n")
                .append("			<a href='javascript:alterarFiltros(").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(")'>\n")
                .append("				<img src='imagens/icone_incluir_pequeno.gif' style='width:15px; height:17px; text-align:baseline' title='Alterar apenas os filtros'>\n")
                .append("			</a>\n")
                .append("		</td>\n");
    }

    private void appendFilterWithoutLink(int treeLevel) {
        tree.append("	 	<td class='filtro'>\n")
                .append("			<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>\n")
                .append("			<a href='javascript:navegacao(\"1\"); mostra_esconde(\"bloco1\")' title='Abrir a estrutura de filtros nesse nível'>Filters</a>\n")
                .append("		</td>\n");
    }

    private void appendDimensionFilter(int treeLevel) {
        if (filters.getDimensionFilter() != null && (filters.getDimensionFilter().hasFilters() > 0 || filters.getDimensionFilter().getCondition() != null || filters.getDimensionFilter().isMacro())) {
            tree.append("						<td class='filtro'>\n")
                    .append("							<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>\n")
                    .append("							<a href='javascript:navegacao(\"1\"); mostra_esconde(\"arvore_filtro1\")' title='Abrir a estrutura de filtros nesse nível'><span>Dimens&otilde;es</a>&nbsp;</span>\n")
                    .append("							<a href='javascript:filter(\"1\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>\n")
                    .append("							</a>\n")
                    .append("						</td>\n");
        } else {
            tree.append("							<td class='filtro'>\n")
                    .append("							<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'><span>Dimens&otilde;es</span>\n")
                    .append("							<a href='javascript:filter(\"1\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>\n")
                    .append("							</a>\n")
                    .append("						</td>\n");
        }
    }

    private void appendMetricFilters(int treeLevel, String display) throws BIException {
        if (filters.getMetricFilters() == null || filters.getMetricFilters().size() <= 0) {
            tree.append("						<td class='filtro'> <img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title='&raquo;' id='id_minus_top'><span>Métricas</span>\n")
                    .append("							<a href='javascript:filter(\"2\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>\n")
                    .append("							</a>\n")
                    .append("						</td> \n")
                    .append("					</tr>\n");
        } else {
            tree.append("						<td class='filtro'> <img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>\n")
                    .append("							<a href='javascript:navegacao(\"2\"); mostra_esconde(\"bloco2\")' title='Abrir a estrutura de filtros nesse nível'><span>Métricas</span></a>&nbsp;\n")
                    .append("							<a href='javascript:filter(\"2\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>\n")
                    .append("							</a>\n")
                    .append("						</td> \n")
                    .append("					</tr>\n")
                    .append("          			<tr>\n")
                    .append("			            <td class='filtro'>\n")
                    .append("							<div style='width: 20px;'></div>\n")
                    .append("						</td>\n")
                    .append("            			<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg' >E</td>\n")
                    .append("						<td class='filtro'> \n")
                    .append("							<div id='bloco2' style='position:relative; width:100%; z-index:1; display:").append(display).append("'> \n")
                    .append("								<table>\n");

            buildMetricFilters(treeLevel);

            tree.append("								</table>\n")
                    .append("							</div>\n")
                    .append("						</td>\n")
                    .append("					</tr>\n");
        }
    }



    private void buildFilterFunction(int treeLevel) throws BIException {
        if (this.filtersFunction != null && this.filtersFunction.getFilters() != null) {
            int index = 0;
            Iterator<FilterFunction> i = this.filtersFunction.getFilters().iterator();
            display = (this.showTree || (this.openNodes != null && this.openNodes.contains("3"))) ? "" : "none";

            while (i.hasNext()) {
                FilterFunction filtroFuncao = i.next();

                tree.append("			<tr>").append("\n")
                        .append("				<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                        .append("					<div style='width: 20px;'></div>").append("\n")
                        .append("				</td>").append("\n")
                        .append("				<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                        .append("					<div style='width: 20px;'></div>").append("\n")
                        .append("				</td>").append("\n")
                        .append("             	<td>").append("\n")
                        .append("					<div id='bloco3' style='position:relative; width:100%; z-index:1; display:").append(display).append("'> ").append("\n")
                        .append("						<table>").append("\n")
                        .append("							<tr>").append("\n")
                        .append("								<td>&nbsp;</td>").append("\n")
                        .append("								<td class='filtro' style='vertical-align:top;'>").append("\n")
                        .append("									<img src='imagens/line_bottom_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_bottom'>").append("\n")
                        .append("									<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>")
                        .append(filtroFuncao.getFieldTitle()).append(" ").append(filtroFuncao.getOperator().getDescription()).append(" ").append(filtroFuncao.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                        .append("									<a href='javascript:filter(\"func").append(index).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>").append("\n")
                        .append("										<img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>").append("\n")
                        .append("									</a>").append("\n")
                        .append("                         			<a href='javascript:excluiFiltro(\"func").append(index).append("\", ").append(this.graphCode).append(")'>").append("\n")
                        .append("										<img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>").append("\n")
                        .append("									</a>")
                        .append("								</td>").append("\n")
                        .append("							</tr>").append("\n")
                        .append("						</table>").append("\n")
                        .append("					</div>").append("\n")
                        .append("				</td>").append("\n")
                        .append("			</tr>").append("\n");
                index++;
            }
        }
    }

    private void buildMetricSqlFilter(int treeLevel) throws BIException {
        MetricFilter filtro_metrica;
        String link_metrica = "4";

        for (int index = 0; index < filters.getMetricSqlFilter().size(); index++) {
            filtro_metrica = filters.getMetricSqlFilter().get(index);
            filtro_metrica = new MetricTextFilter(filtro_metrica);

            tree.append("							<tr>").append("\n")
                    .append("								<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                    .append("									<div style='width: 20px;'></div>").append("\n")
                    .append("								</td>").append("\n");

            if (index == (filters.getMetricSqlFilter().size() - 1)) {
                tree.append("								<td class='filtro_").append(this.getColor(treeLevel)).append("' style='vertical-align:top;'>").append("\n")
                        .append("									<img src='imagens/line_bottom_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_bottom'>").append("\n")
                        .append("								</td>").append("\n");
            } else {
                tree.append("								<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>").append("\n")
                        .append("									<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>").append("\n")
                        .append("								</td>").append("\n");
            }

            tree.append("								<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                    .append("									<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>")
                    .append(filtro_metrica.getField().getTitle()).append(" ").append(filtro_metrica.getOperator().getDescription()).append(" ").append(filtro_metrica.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                    .append("									<a href='javascript:filter(\"").append(link_metrica).append("-").append((index + 1)).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>").append("\n")
                    .append("										<img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>").append("\n")
                    .append("									</a>").append("\n")
                    .append("									<a href='javascript:excluiFiltro(\"").append(link_metrica).append("-").append((index + 1)).append("\", ").append(this.graphCode).append(")'>").append("\n")
                    .append("										<img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>").append("\n")
                    .append("									</a>").append("\n")
                    .append("								</td>").append("\n")
                    .append("							</tr>").append("\n");

            if (index < (filters.getMetricSqlFilter().size() - 1)) {
                tree.append("							<tr>").append("\n")
                        .append("								<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                        .append("									<div style='width: 20px;'></div>").append("\n")
                        .append("								</td>").append("\n")
                        .append("								<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>E</td>").append("\n")
                        .append("								<td class='filtro_").append(this.getColor(treeLevel)).append("'>&nbsp;</td>").append("\n")
                        .append("							</tr>").append("\n");
            }
        }
    }

    private void buildMetricFilters(int treeLevel) throws BIException {
        MetricFilter filtro_metrica;
        String metricLink = "2";

        for (int index = 0; index < filters.getMetricFilters().size(); index++) {
            filtro_metrica = filters.getMetricFilters().get(index);
            filtro_metrica = new MetricTextFilter(filtro_metrica);

            tree.append("									<tr>").append("\n")
                    .append("										<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                    .append("											<div style='width: 20px;'></div>").append("\n")
                    .append("										</td>").append("\n");

            if (index == (filters.getMetricFilters().size() - 1)) {
                tree.append("										<td class='filtro_").append(this.getColor(treeLevel)).append("' style='vertical-align:top;'>").append("\n")
                        .append("											<img src='imagens/line_bottom_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_bottom'>").append("\n")
                        .append("										</td>").append("\n");
            } else {
                tree.append("										<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>").append("\n")
                        .append("											<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>").append("\n")
                        .append("										</td>").append("\n");
            }

            tree.append("										<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                    .append("											<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>")
                    .append(filtro_metrica.getField().getTitle()).append(" ").append(filtro_metrica.getOperator().getDescription()).append(" ").append(filtro_metrica.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                    .append("											<a href='javascript:filter(\"").append(metricLink).append("-").append((index + 1)).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>").append("\n")
                    .append("												<img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>").append("\n")
                    .append("											</a>").append("\n")
                    .append("											<a href='javascript:excluiFiltro(\"").append(metricLink).append("-").append((index + 1)).append("\", ").append(this.graphCode).append(")'>").append("\n")
                    .append("												<img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>").append("\n")
                    .append("											</a>").append("\n")
                    .append("										</td>").append("\n")
                    .append("									</tr>").append("\n");

            if (index < (filters.getMetricFilters().size() - 1)) {
                tree.append("									<tr>").append("\n")
                        .append("										<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                        .append("											<div style='width: 20px;'></div>").append("\n")
                        .append("										</td>").append("\n")
                        .append("                    					<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>E</td>").append("\n")
                        .append("                    					<td class='filtro_").append(this.getColor(treeLevel)).append("'>&nbsp;</td>").append("\n")
                        .append("									</tr>").append("\n");
            }
        }
    }

    private void buildDimensionFilter(DimensionFilter fd, String link, int treeLevel) throws BIException {

        String display = (this.showTree || (this.openNodes != null && this.openNodes.contains(link))) ? "" : "none";

        tree.append("<div id='arvore_filtro").append(link).append("' style='position:relative; width:100%;  z-index:1; display:").append(display).append("'>").append("\n")
                .append("	<table>").append("\n");

        fd = new DimensionTextFilter(fd);
        int cont = 1;
        for (DimensionFilter filtro_dimensao : fd.getFilters()) {
            filtro_dimensao = new DimensionTextFilter(filtro_dimensao);

            buildFilterRow(filtro_dimensao, treeLevel, link, cont);

            buildFilterConditionRow(filtro_dimensao, treeLevel, link, cont);
            cont++;
        }

        tree.append("	</table>").append("\n")
                .append("</div>").append("\n");

    }

    private void buildFilterRow(DimensionFilter filtro_dimensao, int treeLevel, String link, int cont) throws BIException {

        tree.append("		<tr> ").append("\n")
                .append("			<td class='filtro_").append(getColor(treeLevel)).append("'>").append("\n")
                .append("				<div style='width: 20px;'></div>").append("\n")
                .append("			</td>").append("\n");

        if (filtro_dimensao.getCondition() == null && !filtro_dimensao.isMacro()) {
            tree.append("			<td class='filtro_").append(getColor(treeLevel)).append("' align='center' background='imagens/line_").append(getColor(treeLevel)).append(".jpg'>").append("\n")
                    .append(filtro_dimensao.getConnectorDescription()).append("\n")
                    .append("			</td>").append("\n");
        } else {
            tree.append("			<td class='filtro_").append(getColor(treeLevel)).append("' align='center'>&nbsp;</td>").append("\n");
        }

        tree.append("			<td class='filtro_").append(getColor(treeLevel)).append("'> ").append("\n");
        if (filtro_dimensao.getCondition() == null && !filtro_dimensao.isMacro()) {
            buildDimensionFilter(filtro_dimensao, link + "-" + cont, treeLevel + 1);
        } else {
            tree.append("&nbsp;").append("\n");
        }
        tree.append("			</td> ").append("\n");

        tree.append("		</tr>").append("\n");

    }

    private void buildFilterConditionRow(DimensionFilter filtro_dimensao, int treeLevel, String link, int cont) throws BIException {

        tree.append("		<tr> ").append("\n")
                .append("			<td class='filtro_").append(getColor(treeLevel)).append("'>").append("\n")
                .append("				<div style='width: 20px;'></div>").append("\n")
                .append("			</td>").append("\n");

        if (filtro_dimensao.getCondition() != null) {
            tree.append("			<td class='filtro_").append(getColor(treeLevel)).append("'> ").append("\n")
                    .append("				<img src='imagens/minus_top_").append(getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append(filtro_dimensao.getField().getTitle()).append(" ").append(filtro_dimensao.getOperator().getDescription()).append(" ").append(filtro_dimensao.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                    .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("					<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("				</a>&nbsp;")
                    .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>").append("\n")
                    .append("					<img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>").append("\n")
                    .append("				</a>").append("\n")
                    .append("				<a href='javascript:excluiFiltro(\"").append(link).append("-").append(cont).append("\", ").append(this.graphCode).append(")'>").append("\n")
                    .append("					<img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>").append("\n")
                    .append("				</a>").append("\n")
                    .append("			</td>").append("\n");
        } else if (filtro_dimensao.isMacro()) {
            tree.append("			<td class='filtro_").append(getColor(treeLevel)).append("'> ").append("\n")
                    .append("				<img src='imagens/minus_top_").append(getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append(filtro_dimensao.getMacroField().getTitle()).append(" igual a ").append(filtro_dimensao.getMacro().getDescription().toLowerCase()).append("\n")
                    .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("					<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("				</a>&nbsp;")
                    .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>").append("\n")
                    .append("					<img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>").append("\n")
                    .append("				</a>").append("\n")
                    .append("				<a href='javascript:excluiFiltro(\"").append(link).append("-").append(cont).append("\", ").append(this.graphCode).append(")'>").append("\n")
                    .append("					<img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>").append("\n")
                    .append("				</a>").append("\n")
                    .append("			</td>").append("\n");
        }

        tree.append("		</tr>").append("\n");

    }

    private String getColor(int nivel) {
        if (nivel % 2 == 0)
            return "azul";
        else
            return "verde";
    }

    public String prepareTree() throws BIException {
        StringBuilder strHtml = new StringBuilder(this.tree.toString());
        int i, aux;
        try {
            while (((i = strHtml.indexOf("<a")) != -1) || ((i = strHtml.indexOf("<img id='especial' ")) != -1)) {
                aux = strHtml.indexOf(">", i);
                if (aux != -1 && (strHtml.charAt(aux + 1) == '\'' || strHtml.charAt(aux + 1) == '='))
                    aux = strHtml.indexOf(">", aux + 1);
                if (aux != -1)
                    strHtml.replace(i, aux + 1, "&nbsp;");
            }
        } catch (Exception ex) {
            BIException biex = new BIException(ex);
            biex.setAction("preparar tabela.");
            biex.setLocal(this.getClass(), "preparaTabela(String)");
            throw biex;
        }
        return strHtml.toString();
    }
}
