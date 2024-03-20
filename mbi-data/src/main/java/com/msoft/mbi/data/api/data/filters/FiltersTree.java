package com.msoft.mbi.data.api.data.filters;

import com.msoft.mbi.data.api.data.exception.BIException;
import com.msoft.mbi.data.api.data.util.BIUtil;

import java.util.Iterator;
import java.util.List;

public class FiltersTree {

    private Filters filters;
    private FiltersFunction filtersFunction;
    private StringBuilder tree;
    private List<String> openNodes;
    private String display = "none";
    private boolean showTree;
    private int indicatorIndex;
    private int panelIndex;
    private boolean showEditorLink = true;
    private int graphCode;
    private boolean isGraphFilter = false;


    public FiltersTree(Filters f, FiltersFunction filtrosFuncao, String nos, int indicatorIndex, int panelIndex, boolean exibirLinkEditor, int graphCode) throws BIException {
        this.filters = f;
        this.filtersFunction = filtrosFuncao;
        this.openNodes = BIUtil.stringToList(nos);
        this.indicatorIndex = indicatorIndex;
        this.panelIndex = panelIndex;
        this.showEditorLink = exibirLinkEditor;
        this.graphCode = graphCode;
        this.isGraphFilter = graphCode != 0;
        buildTree();
    }

    public FiltersTree(Filters f, FiltersFunction filtrosFuncao, int indicatorIndex, int panelIndex, int graphCode) throws BIException {
        this(f, filtrosFuncao, "", indicatorIndex, panelIndex, true, graphCode);
    }

    public FiltersTree(Filters f, FiltersFunction filtrosFuncao, boolean mostrar_toda_tree, int indicatorIndex, int panelIndex, boolean exibirLinkEditor, int graphCode) throws BIException {
        this.filters = f;
        this.indicatorIndex = indicatorIndex;
        this.panelIndex = panelIndex;
        this.showTree = mostrar_toda_tree;
        this.showEditorLink = exibirLinkEditor;
        this.graphCode = graphCode;
        this.filtersFunction = filtrosFuncao;
        this.isGraphFilter = graphCode != 0;
        buildTree();
    }

    public FiltersTree(Filters f, boolean mostrar_toda_tree) throws BIException {
        this.filters = f;
        this.showTree = mostrar_toda_tree;
        this.showEditorLink = false;
        buildTree();
    }

    public FiltersTree() throws BIException {
        buildTree();
    }

    public String toString() {
        return this.tree.toString();
    }

    private void buildTree() throws BIException {
        tree = new StringBuilder();
        int treeLevel = 0;
        display = shouldDisplay("2") ? "" : "none";

        tree.append("<table id='tabela_arvore_filtro' style='width:90%; margin: auto; padding-top: 25px;'>").append("\n")
                .append("	<tr>").append("\n");
        if (this.showEditorLink) {
            tree.append("	 	<td style='width:94%;' class='filtro'>").append("\n")
                    .append("			<a href='javascript:navegacao(\"1\"); mostra_esconde(\"bloco1\")' title='Abrir a estrutura de filtros nesse nível'>Filtros</a>").append("\n")
                    .append("			<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append("		</td>").append("\n")
                    .append("    	<td style='width:6%;' class='filtro'>").append("\n")
                    .append("			<a href='javascript:alterarFiltros(").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(")'>").append("\n")
                    .append("				<img src='imagens/icone_incluir_pequeno.gif' style='width:15px; height:17px; text-align:baseline' title='Alterar apenas os filtros'>").append("\n")
                    .append("			</a>").append("\n")
                    .append("		</td>").append("\n");
        } else {
            tree.append("	 	<td class='filtro'>").append("\n")
                    .append("			<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append("			<a href='javascript:navegacao(\"1\"); mostra_esconde(\"bloco1\")' title='Abrir a estrutura de filtros nesse nível'>Filtros</a>").append("\n")
                    .append("		</td>").append("\n");
        }
        tree.append("	 </tr>").append("\n")
                .append("  <tr> ").append("\n")
                .append("		<td class='filtro'> ").append("\n")
                .append("			<div id='bloco1' style='position:relative; width:100%; z-index:1;'>").append("\n")
                .append("				<table>").append("\n")
                .append("					<tr> ").append("\n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                .append("							<div style='width: 20px;'></div>").append("\n")
                .append("						</td>").append("\n")
                .append("            			<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>").append("\n")
                .append("							<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>").append("\n")
                .append("						</td>").append("\n");
        if (filters.getDimensionFilter() != null && (filters.getDimensionFilter().hasFilters() > 0 || filters.getDimensionFilter().getCondition() != null || filters.getDimensionFilter().isMacro())) {
            tree.append("						<td class='filtro'>").append("\n")
                    .append("							<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append("							<a href='javascript:navegacao(\"1\"); mostra_esconde(\"arvore_filtro1\")' title='Abrir a estrutura de filtros nesse nível'><span>Dimens&otilde;es</a>&nbsp;").append("\n")
                    .append("							<a href='javascript:filter(\"1\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("							</a>").append("\n")
                    .append("						</td>").append("\n");
        } else {
            tree.append("							<td class='filtro'>").append("\n")
                    .append("							<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'><span>Dimens&otilde;es</span>").append("\n")
                    .append("							<a href='javascript:filter(\"1\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("							</a>").append("\n")
                    .append("						</td>").append("\n");
        }
        tree.append("					</tr>").append("\n")
                .append("					<tr> ").append("\n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                .append("							<div style='width: 20px;'></div>").append("\n")
                .append("						</td>").append("\n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>E</td>").append("\n")
                .append("						<td class='filtro_").append(this.getColor(treeLevel)).append("'> ").append("\n");

        if (filters.getDimensionFilter() != null) buildDimensionFilter(filters.getDimensionFilter(), "1", treeLevel);

        tree.append("						</td>").append("\n")
                .append("					</tr>").append("\n")
                .append("					<tr> ").append("\n")
                .append("						<td class='filtro'>").append("\n")
                .append("							<div style='width: 20px;'></div>").append("\n")
                .append("						</td>").append("\n")
                .append("            			<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>").append("\n")
                .append("							<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>").append("\n")
                .append("						</td>").append("\n");
        if (filters.getMetricFilters() == null || filters.getMetricFilters().size() <= 0) {
            tree.append("						<td class='filtro'> <img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title='&raquo;' id='id_minus_top'><span>Métricas</span>").append("\n")
                    .append("							<a href='javascript:filter(\"2\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("							</a>").append("\n")
                    .append("						</td> ").append("\n")
                    .append("					</tr>").append("\n");
        } else {
            tree.append("						<td class='filtro'> <img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append("							<a href='javascript:navegacao(\"2\"); mostra_esconde(\"bloco2\")' title='Abrir a estrutura de filtros nesse nível'><span>Métricas</span></a>&nbsp;").append("\n")
                    .append("							<a href='javascript:filter(\"2\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                    .append("								<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                    .append("							</a>").append("\n")
                    .append("						</td> ").append("\n")
                    .append("					</tr>").append("\n")
                    .append("          			<tr>").append("\n")
                    .append("			            <td class='filtro'>").append("\n")
                    .append("							<div style='width: 20px;'></div>").append("\n")
                    .append("						</td>").append("\n")
                    .append("            			<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg' >E</td>").append("\n")
                    .append("						<td class='filtro'> ").append("\n")
                    .append("							<div id='bloco2' style='position:relative; width:100%; z-index:1; display:").append(display).append("'> ").append("\n")
                    .append("								<table>").append("\n");

            buildMetricFilter(treeLevel);

            tree.append("								</table>").append("\n")
                    .append("							</div>").append("\n")
                    .append("						</td>").append("\n")
                    .append("					</tr>").append("\n");
        }

        if (!this.isGraphFilter) {
            tree.append("          <tr>").append("\n")
                    .append("            <td class='filtro'>").append("\n")
                    .append("				<div style='width: 20px;'></div>").append("\n")
                    .append("			</td>").append("\n");
            if (filters.getMetricSqlFilter() == null || filters.getMetricSqlFilter().size() == 0) {
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

                display = shouldDisplay("4") ? "" : "none";

                tree.append("					<div id='bloco4' style='position:relative; width:100%; z-index:1; display:").append(display).append("'> ").append("\n")
                        .append("						<table>").append("\n");

                buildMetricSqlFilter(treeLevel);

                tree.append("						</table>").append("\n");
                tree.append("					</div>").append("\n");
                tree.append("				</td>").append("\n");
                tree.append("			</tr>").append("\n");
            }
        }


        tree.append("			<tr>").append("\n")
                .append("				<td class='filtro'>").append("\n")
                .append("					<div style='width: 20px;'></div>").append("\n")
                .append("				</td>").append("\n");
        if (filters.getMetricSqlFilter() == null || filters.getMetricSqlFilter().size() == 0) {
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

        buildFilterFunction(treeLevel);

        tree.append("				</table>").append("\n")
                .append("		</div>").append("\n")
                .append("		</td>").append("\n")
                .append("	</tr>").append("\n")
                .append("</table>").append("\n");
    }


    private void buildFilterFunction(int treeLevel) {
        if (this.filtersFunction != null && this.filtersFunction.getFilters() != null) {
            String display = shouldDisplay("3") ? "" : "none";
            int index = 0;

            for (FilterFunction filterFunction : this.filtersFunction.getFilters()) {
                String color = this.getColor(treeLevel);

                tree.append("        <tr>\n")
                        .append("            <td class='filtro_").append(color).append("'>\n")
                        .append("                <div style='width: 20px;'></div>\n")
                        .append("            </td>\n")
                        .append("            <td class='filtro_").append(color).append("'>\n")
                        .append("                <div style='width: 20px;'></div>\n")
                        .append("            </td>\n")
                        .append("            <td>\n")
                        .append("                <div id='bloco3' style='position:relative; width:100%; z-index:1; display:").append(display).append("'>\n")
                        .append("                    <table>\n")
                        .append("                        <tr>\n")
                        .append("                            <td>&nbsp;</td>\n")
                        .append("                            <td class='filtro' style='vertical-align:top;'>\n")
                        .append("                                <img src='imagens/line_bottom_").append(color).append(".jpg' title='' id='id_line_bottom'>\n")
                        .append("                                <img src='imagens/minus_top_").append(color).append(".jpg' title=' &raquo; ' id='id_minus_top'>\n")
                        .append("                                ").append(filterFunction.getFieldTitle()).append(" ")
                        .append(filterFunction.getOperator().getDescription()).append(" ")
                        .append(filterFunction.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                        .append("                                <a href='javascript:filter(\"func").append(index).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>\n")
                        .append("                                    <img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>\n")
                        .append("                                </a>\n")
                        .append("                                <a href='javascript:excluiFiltro(\"func").append(index).append("\", ").append(this.graphCode).append(")'>\n")
                        .append("                                    <img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>\n")
                        .append("                                </a>\n")
                        .append("                            </td>\n")
                        .append("                        </tr>\n")
                        .append("                    </table>\n")
                        .append("                </div>\n")
                        .append("            </td>\n")
                        .append("        </tr>\n");
                index++;
            }
        }
    }

    private void buildMetricSqlFilter(int treeLevel) throws BIException {
        MetricFilter metricFilter;
        String linkMetric = "4";

        for (int index = 0; index < filters.getMetricSqlFilter().size(); index++) {
            metricFilter = filters.getMetricSqlFilter().get(index);
            metricFilter = new MetricTextFilter(metricFilter);

            String color = this.getColor(treeLevel);
            boolean isLast = (index == filters.getMetricSqlFilter().size() - 1);

            tree.append("    <tr>\n")
                    .append("        <td class='filtro_").append(color).append("'>\n")
                    .append("            <div style='width: 20px;'></div>\n")
                    .append("        </td>\n");

            if (isLast) {
                tree.append("        <td class='filtro_").append(color).append("' style='vertical-align:top;'>\n")
                        .append("            <img src='imagens/line_bottom_").append(color).append(".jpg' title='' id='id_line_bottom'>\n")
                        .append("        </td>\n");
            } else {
                tree.append("        <td class='filtro_").append(color).append("' background='imagens/line_").append(color).append(".jpg'>\n")
                        .append("            <img src='imagens/line_center_").append(color).append(".jpg' title='' id='id_line_center'>\n")
                        .append("        </td>\n");
            }

            tree.append("        <td class='filtro_").append(color).append("'>\n")
                    .append("            <img src='imagens/minus_top_").append(color).append(".jpg' title=' &raquo; ' id='id_minus_top'>\n")
                    .append("            ").append(metricFilter.getField().getTitle()).append(" ")
                    .append(metricFilter.getOperator().getDescription()).append(" ")
                    .append(metricFilter.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                    .append("            <a href='javascript:filter(\"").append(linkMetric).append("-").append((index + 1)).append("\", ")
                    .append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>\n")
                    .append("                <img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>\n")
                    .append("            </a>\n")
                    .append("            <a href='javascript:excluiFiltro(\"").append(linkMetric).append("-").append((index + 1)).append("\", ")
                    .append(this.graphCode).append(")'>\n")
                    .append("                <img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>\n")
                    .append("            </a>\n")
                    .append("        </td>\n")
                    .append("    </tr>\n");

            if (!isLast) {
                tree.append("    <tr>\n")
                        .append("        <td class='filtro_").append(color).append("'>\n")
                        .append("            <div style='width: 20px;'></div>\n")
                        .append("        </td>\n")
                        .append("        <td class='filtro_").append(color).append("' align='center' background='imagens/line_").append(color).append(".jpg'>E</td>\n")
                        .append("        <td class='filtro_").append(color).append("'>&nbsp;</td>\n")
                        .append("    </tr>\n");
            }
        }
    }

    private void buildMetricFilter(int treeLevel) throws BIException {
        MetricFilter metricFilter;
        String linkMetric = "2";

        for (int index = 0; index < filters.getMetricFilters().size(); index++) {
            metricFilter = filters.getMetricFilters().get(index);
            metricFilter = new MetricTextFilter(metricFilter);

            String color = this.getColor(treeLevel);
            boolean isLast = (index == filters.getMetricFilters().size() - 1);

            tree.append("    <tr>\n")
                    .append("        <td class='filtro_").append(color).append("'>\n")
                    .append("            <div style='width: 20px;'></div>\n")
                    .append("        </td>\n");
            if (isLast) {
                tree.append("        <td class='filtro_").append(color).append("' style='vertical-align:top;'>\n")
                        .append("            <img src='imagens/line_bottom_").append(color).append(".jpg' title='' id='id_line_bottom'>\n")
                        .append("        </td>\n");
            } else {
                tree.append("        <td class='filtro_").append(color).append("' background='imagens/line_").append(color).append(".jpg'>\n")
                        .append("            <img src='imagens/line_center_").append(color).append(".jpg' title='' id='id_line_center'>\n")
                        .append("        </td>\n");
            }
            tree.append("        <td class='filtro_").append(color).append("'>\n")
                    .append("            <img src='imagens/minus_top_").append(color).append(".jpg' title=' &raquo; ' id='id_minus_top'>\n")
                    .append("            ").append(metricFilter.getField().getTitle()).append(" ")
                    .append(metricFilter.getOperator().getDescription()).append(" ")
                    .append(metricFilter.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                    .append("            <a href='javascript:filter(\"").append(linkMetric).append("-").append((index + 1)).append("\", ")
                    .append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>\n")
                    .append("                <img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>\n")
                    .append("            </a>\n")
                    .append("            <a href='javascript:excluiFiltro(\"").append(linkMetric).append("-").append((index + 1)).append("\", ")
                    .append(this.graphCode).append(")'>\n")
                    .append("                <img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>\n")
                    .append("            </a>\n")
                    .append("        </td>\n")
                    .append("    </tr>\n");
            if (!isLast) {
                tree.append("    <tr>\n")
                        .append("        <td class='filtro_").append(color).append("'>\n")
                        .append("            <div style='width: 20px;'></div>\n")
                        .append("        </td>\n")
                        .append("        <td class='filtro_").append(color).append("' align='center' background='imagens/line_").append(color).append(".jpg'>E</td>\n")
                        .append("        <td class='filtro_").append(color).append("'>&nbsp;</td>\n")
                        .append("    </tr>\n");
            }
        }
    }

    private void buildDimensionFilter(DimensionFilter parentFilter, String link, int treeLevel) throws BIException {
        display = shouldDisplay(link) ? "" : "none";

        tree.append("<div id='arvore_filtro").append(link).append("' style='position:relative; width:100%;  z-index:1; display:").append(display).append("'>").append("\n")
                .append("	<table>").append("\n");

        parentFilter = new DimensionTextFilter(parentFilter);
        Iterator<DimensionFilter> iterator = parentFilter.getIterator();
        DimensionFilter childFilter;
        int cont = 1;

        if (iterator != null && parentFilter.getCondition() == null && !parentFilter.isMacro()) {
            while (iterator.hasNext()) {
                childFilter = iterator.next();
                childFilter = new DimensionTextFilter(childFilter);

                tree.append("		<tr> ").append("\n")
                        .append("			<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                        .append("				<div style='width: 20px;'></div>").append("\n")
                        .append("			</td>").append("\n");

                if (iterator.hasNext()) {
                    tree.append("			<td class='filtro_").append(this.getColor(treeLevel)).append("' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>").append("\n")
                            .append("				<img src='imagens/line_center_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_center'>").append("\n")
                            .append("			</td>").append("\n");
                } else {
                    tree.append("			<td class='filtro_").append(this.getColor(treeLevel)).append("' style='vertical-align:top;'>").append("\n")
                            .append("				<img src='imagens/line_bottom_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_bottom'>").append("\n")
                            .append("			</td>").append("\n");
                }
                tree.append("			<td class='filtro_").append(this.getColor(treeLevel)).append("'> ").append("\n");
                if (childFilter.getCondition() != null) {
                    tree.append("				<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>")
                            .append(childFilter.getField().getTitle()).append(" ").append(childFilter.getOperator().getDescription()).append(" ").append(childFilter.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
                            .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                            .append("					<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                            .append("				</a>&nbsp;")
                            .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>").append("\n")
                            .append("					<img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>").append("\n")
                            .append("				</a>").append("\n")
                            .append("				<a href='javascript:excluiFiltro(\"").append(link).append("-").append(cont).append("\", ").append(this.graphCode).append(")'>").append("\n")
                            .append("					<img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>").append("\n")
                            .append("				</a>").append("\n");
                } else if (childFilter.isMacro()) {
                    tree.append("				<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>")
                            .append(childFilter.getMacroField().getTitle()).append(" igual a ").append(childFilter.getMacro().getDescription().toLowerCase()).append("\n")
                            .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(this.panelIndex).append(", ").append(this.graphCode).append(", \"add\")'>").append("\n")
                            .append("					<img id='especial' src='imagens/mais.gif' title='Adicionar um filtro nesse nível'>").append("\n")
                            .append("				</a>").append("&nbsp;\n")
                            .append("				<a href='javascript:filter(\"").append(link).append("-").append(cont).append("\", ").append(this.indicatorIndex).append(", ").append(panelIndex).append(", ").append(this.graphCode).append(", \"edit\")'>").append("\n")
                            .append("					<img id='especial' src='imagens/editar_filtro.gif' width='9' height='9' title='Editar esse filtro'>").append("\n")
                            .append("				</a>").append("\n")
                            .append("				<a href='javascript:excluiFiltro(\"").append(link).append("-").append(cont).append("\", ").append(this.graphCode).append(")'>").append("\n")
                            .append("					<img id='especial' src='imagens/excluir.gif' width='9' height='9' title='Excluir este filtro'>").append("\n")
                            .append("				</a>").append("\n");
                } else {
                    tree.append("				<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                            .append("					<a href='javascript:navegacao(\"").append(link).append("-").append(cont).append("\"); mostra_esconde(\"arvore_filtro").append(link).append("-").append(cont).append("\")' title='Abrir a estrutura de filtros nesse nível'>...</a> ").append("\n");
                }
                tree.append("			</td>").append("\n")
                        .append("		</tr>").append("\n");

                if (iterator.hasNext() || (childFilter.getCondition() == null && !childFilter.isMacro())) {
                    tree.append("		<tr> ").append("\n")
                            .append("			<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                            .append("				<div style='width: 20px;'></div>").append("\n")
                            .append("			</td>").append("\n");
                    if (iterator.hasNext()) {
                        tree.append("			<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center' background='imagens/line_").append(this.getColor(treeLevel)).append(".jpg'>").append("\n")
                                .append(parentFilter.getConnectorDescription()).append("\n")
                                .append("			</td>").append("\n");
                    } else {
                        tree.append("			<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center'>&nbsp;</td>").append("\n");
                    }
                    tree.append("			<td class='filtro_").append(this.getColor(treeLevel)).append("'> ").append("\n");
                    if (childFilter.getCondition() == null && !childFilter.isMacro()) {
                        buildDimensionFilter(childFilter, link + "-" + cont, treeLevel + 1);
                    } else {
                        tree.append("			&nbsp;").append("\n");
                    }
                    tree.append("			</td> ").append("\n");
                } else {
                    tree.append("		<tr> ").append("\n")
                            .append("			<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                            .append("				<div style='width: 20px;'></div>").append("\n")
                            .append("			</td>").append("\n")
                            .append("			<td class='filtro_").append(this.getColor(treeLevel)).append("' align='center'>&nbsp;</td>").append("\n")
                            .append("           <td class='filtro_").append(this.getColor(treeLevel)).append("'>&nbsp;</td>").append("\n")
                            .append("		</tr>").append("\n");
                }

                cont++;
            }
        } else {
            this.appendConditionOrMacro(parentFilter, link, cont, treeLevel);
        }
        tree.append("	</table>").append("\n")
                .append("</div>").append("\n");
    }

    private void appendConditionOrMacro(DimensionFilter parentFilter, String link, int cont, int treeLevel) throws BIException {
        tree.append("		<tr> ").append("\n")
                .append("			<td class='filtro_").append(this.getColor(treeLevel)).append("'>").append("\n")
                .append("				<div style='width: 20px;'></div>").append("\n")
                .append("			</td>").append("\n")
                .append("			<td class='filtro_").append(this.getColor(treeLevel)).append("' style='vertical-align:top;'>").append("\n")
                .append("				<img src='imagens/line_bottom_").append(this.getColor(treeLevel)).append(".jpg' title='' id='id_line_bottom'>").append("\n")
                .append("			</td>").append("\n")
                .append("			<td class='filtro_").append(this.getColor(treeLevel)).append("'> ").append("\n");
        if (parentFilter.getCondition() != null) {
            tree.append("				<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append(parentFilter.getField().getTitle()).append(" ").append(parentFilter.getOperator().getDescription()).append(" ").append(parentFilter.getFormattedValue().replace(";", "; ").replace("  ", " ")).append("\n")
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
        } else if (parentFilter.isMacro()) {
            tree.append("				<img src='imagens/minus_top_").append(this.getColor(treeLevel)).append(".jpg' title=' &raquo; ' id='id_minus_top'>").append("\n")
                    .append(parentFilter.getMacroField().getTitle()).append(" igual a ").append(parentFilter.getMacro().getDescription().toLowerCase()).append("\n")
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
    

    private String getColor(int level) {
        if (level % 2 == 0)
            return "azul";
        else
            return "verde";
    }

    private boolean shouldDisplay(String link) {
        return this.showTree || (this.openNodes != null && this.openNodes.contains(link));
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
