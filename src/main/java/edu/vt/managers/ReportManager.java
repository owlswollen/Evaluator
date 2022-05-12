/*
 * Created by Gokce Onen on 2022.04.$10
 * Copyright © $2022 Gokce Onen. All rights reserved.
 */

package edu.vt.managers;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.vt.EntityBeans.Project;
import edu.vt.globals.Constants;
import edu.vt.pojo.Indicator;
import edu.vt.pojo.Score;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Named(value = "reportManager")
@SessionScoped
public class ReportManager implements Serializable {

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private Project selectedProject;
    private final Color maroon = new Color(128,0,0);
    private final Color blue = new Color(0, 0, 102);
    private final Color green = new Color(0, 51, 0);

    /*
    ================
    Instance Methods
    ================
     */
    public StreamedContent createReport(Project selectedProject) throws IOException, BadElementException, DocumentException {
        this.selectedProject = selectedProject;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document pdf = new Document();
        PdfWriter.getInstance(pdf, byteArrayOutputStream);
        pdf.open();
        pdf.setPageSize(PageSize.A4);

        pdf.add(getTitleParagraph());
        pdf.add(getDescriptionParagraph());
        for (Paragraph paragraph : getIndicatorsHierarchyParagraphs()) {
            pdf.add(paragraph);
        }
        for (Paragraph paragraph : getIndicatorDetailParagraphs()) {
            pdf.add(paragraph);
        }
        pdf.close();

        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        String fileName = "ProjectReport.pdf";
        String directory = Constants.FILES_ABSOLUTE_PATH;
        File file = new File(directory, fileName);
        String contentType = FacesContext.getCurrentInstance().getExternalContext().getMimeType(file.getAbsolutePath());
        return DefaultStreamedContent.builder()
                .name(fileName)
                .contentType(contentType)
                .stream(() -> inputStream)
                .build();
    }

    /*
    ============================================
    Methods for generating report of the project
    ============================================
     */

    /*
     * PROJECT TITLE
     */
    private Paragraph getTitleParagraph() {
        Paragraph titleParagraph = new Paragraph();
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        String title = selectedProject.getTitle() + "\n\nREPORT\n";
        Chunk titleChunk = new Chunk(title, FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD, maroon));
        titleParagraph.add(titleChunk);
        titleParagraph.add(Chunk.NEWLINE);
        titleParagraph.add(Chunk.NEWLINE);

        return titleParagraph;
    }

    /*
     * PROJECT DESCRIPTION
     */
    private Paragraph getDescriptionParagraph() throws IOException {
        Paragraph descriptionParagraph = new Paragraph();
        descriptionParagraph.setAlignment(Element.ALIGN_LEFT);
        descriptionParagraph.add(new Chunk("Project Description:\n\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, blue)));
        ArrayList elementList = HTMLWorker.parseToList(new StringReader(selectedProject.getDescription()), null);
        for (Object element : elementList) {
            descriptionParagraph.add(element);
        }
        descriptionParagraph.add(Chunk.NEWLINE);
        descriptionParagraph.add(Chunk.NEWLINE);
        descriptionParagraph.setIndentationLeft(40);
        descriptionParagraph.setFirstLineIndent(-20);

        return descriptionParagraph;
    }

    /*
     * INDICATORS HIERARCHY
     */
    private List<Paragraph> getIndicatorsHierarchyParagraphs() {
        List<Paragraph> paragraphs = new ArrayList<>();
        Paragraph titleParagraph = new Paragraph();
        titleParagraph.add(new Chunk("Indicators Hierarchy:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, blue)));
        titleParagraph.add(Chunk.NEWLINE);
        titleParagraph.setIndentationLeft(40);
        titleParagraph.setFirstLineIndent(-20);
        paragraphs.add(titleParagraph);

        paragraphs = addIndicatorsHierarchyParagraphs(selectedProject.getIndicatorsGraph().getRoot(), paragraphs, 40);

        Paragraph newLineParagraph = new Paragraph();
        newLineParagraph.add(Chunk.NEWLINE);
        newLineParagraph.add(Chunk.NEWLINE);
        paragraphs.add(newLineParagraph);

        return paragraphs;
    }

    private List<Paragraph> addIndicatorsHierarchyParagraphs(Indicator graphRoot, List<Paragraph> paragraphs, int indentation) {
        Paragraph indicatorParagraph = new Paragraph(graphRoot.getName());
        indicatorParagraph.setIndentationLeft(indentation);
        indentation += 10;
        paragraphs.add(indicatorParagraph);
        for (Indicator childNode : graphRoot.getChildIndicators()) {
            if (childNode.isEvaluator()) {
                continue;
            }
            paragraphs = addIndicatorsHierarchyParagraphs(childNode, paragraphs, indentation);
        }
        return paragraphs;
    }

    /*
     * INDIVIDUAL INDICATOR DETAILS
     */
    private List<Paragraph> getIndicatorDetailParagraphs() throws IOException {
        List<Paragraph> paragraphs = new ArrayList<>();
        Paragraph indicatorsParagraph = new Paragraph();
        indicatorsParagraph.add(new Chunk("Indicators:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, blue)));
        indicatorsParagraph.add(Chunk.NEWLINE);
        indicatorsParagraph.setIndentationLeft(20);
        paragraphs.add(indicatorsParagraph);
        DecimalFormat decimalFormat = new DecimalFormat("0.000");

        for (Indicator indicator : selectedProject.getIndicatorsGraph().getIndicatorList()) {
            if (indicator.isEvaluator()) {
                continue;
            }

            Paragraph indicatorDetailParagraph = new Paragraph();

            /*
             * INDICATOR NAME
             */
            indicatorDetailParagraph.add(new Chunk(indicator.getName() + ":\n\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, maroon)));

            /*
             * INDICATOR DESCRIPTION
             */
            if (indicator.getDescription() != null) {
                indicatorDetailParagraph.add(new Chunk("Description:\n\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));indicatorDetailParagraph.add(new Chunk(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14)));
                ArrayList elementList = HTMLWorker.parseToList(new StringReader(indicator.getDescription()), null);
                for (Object element : elementList) {
                    indicatorDetailParagraph.add(element);
                }
                indicatorDetailParagraph.add(Chunk.NEWLINE);
            }

            /*
             * INDICATOR SCORE
             */
            if (selectedProject.getIndicatorsGraph().isSolved() && indicator.getScore() != null) {
                indicatorDetailParagraph.add(new Chunk("Score:\n\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));indicatorDetailParagraph.add(new Chunk(" ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                String scoreText = "";
                if (indicator.getScore() != null) {
                    scoreText = "[" + decimalFormat.format(indicator.getScore().getLow()) + " .. " + decimalFormat.format(indicator.getScore().getHigh()) + "]";
                }
                indicatorDetailParagraph.add(new Chunk(scoreText, FontFactory.getFont(FontFactory.TIMES_ROMAN, 14)));
                indicatorDetailParagraph.add(Chunk.NEWLINE);
                indicatorDetailParagraph.add(Chunk.NEWLINE);
            }

            /*
             * LEAF INDICATOR EVALUATORS, WEIGHTS, AND SCORES
             */
            if (indicator.isLeaf() && !indicator.getChildIndicators().isEmpty()) {
                indicatorDetailParagraph.add(new Chunk("Evaluators' Weights and Scores:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));indicatorDetailParagraph.add(new Chunk("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)));
                PdfPTable table = new PdfPTable(3);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(new Phrase("Evaluator Username", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                table.addCell(new Phrase("Weight", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                table.addCell(new Phrase("Numerical Score", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                for (Indicator evaluator : indicator.getChildIndicators()) {
                    table.addCell(evaluator.getName());
                    table.addCell(decimalFormat.format(indicator.getChildWeights().get(evaluator)));
                    String scoreText = "";
                    Score score = indicator.getEvaluatorScores().get(evaluator.getName());
                    if (score != null) {
                        scoreText = "[" + decimalFormat.format(score.getLow()) + " .. " + decimalFormat.format(score.getHigh()) + "]";
                    }
                    table.addCell(scoreText);
                }
                indicatorDetailParagraph.add(table);
                indicatorDetailParagraph.add(Chunk.NEWLINE);
                indicatorDetailParagraph.add(Chunk.NEWLINE);
            }

            /*
             * BRANCH INDICATOR CHILDREN, SCORES, AND WEIGHTS
             */
            if (!indicator.isLeaf()) {
                indicatorDetailParagraph.add(new Chunk("Child Indicators, Aggregate Scores, and Weights:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));indicatorDetailParagraph.add(new Chunk("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)));
                PdfPTable table = new PdfPTable(3);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(new Phrase("Child Indicator Name", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                table.addCell(new Phrase("Aggregate Score", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                table.addCell(new Phrase("Weight", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                for (Indicator childIndicator : indicator.getChildIndicators()) {
                    table.addCell(childIndicator.getName());
                    table.addCell(decimalFormat.format(indicator.getChildWeights().get(childIndicator)));
                    String scoreText = "";
                    if (childIndicator.getScore() != null) {
                        scoreText = "[" + decimalFormat.format(childIndicator.getScore().getLow()) + " .. " + decimalFormat.format(childIndicator.getScore().getHigh()) + "]";
                    }
                    table.addCell(scoreText);
                }
                indicatorDetailParagraph.add(table);
                indicatorDetailParagraph.add(Chunk.NEWLINE);
                indicatorDetailParagraph.add(Chunk.NEWLINE);
            }

            /*
             * PARENT INDICATORS
             */
            indicatorDetailParagraph.add(new Chunk("Parent Indicators:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));indicatorDetailParagraph.add(new Chunk("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)));
            PdfPTable parentTable = new PdfPTable(1);
            parentTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            parentTable.setWidthPercentage(35);
            parentTable.addCell(new Phrase("Parent Indicators", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
            if (indicator.isRoot()) {
                parentTable.addCell("No Parents");
            } else {
                for (Indicator parentIndicator : indicator.getParentIndicators()) {
                    parentTable.addCell(parentIndicator.getName());
                }
            }
            indicatorDetailParagraph.add(parentTable);
            indicatorDetailParagraph.add(Chunk.NEWLINE);
            indicatorDetailParagraph.add(Chunk.NEWLINE);

            /*
             * EVALUATOR NOTES
             */
            if (indicator.isLeaf() && !indicator.getChildIndicators().isEmpty()) {
                indicatorDetailParagraph.add(new Chunk("Notes:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));indicatorDetailParagraph.add(new Chunk("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)));
                PdfPTable table = new PdfPTable(2);
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(new Phrase("Evaluator Username", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                table.addCell(new Phrase("Notes", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD)));
                for (Indicator childIndicator : indicator.getChildIndicators()) {
                    table.addCell(childIndicator.getName());
                    StringBuilder notes = new StringBuilder();
                    if (indicator.getEvaluatorNotes().containsKey(childIndicator.getName())) {
                        ArrayList elementList = HTMLWorker.parseToList(new StringReader(indicator.getEvaluatorNotes().get(childIndicator.getName())), null);
                        for (Object element : elementList) {
                            notes.append(element);
                        }
                        table.addCell(notes.substring(1, notes.length() - 1));
                    } else {
                        table.addCell("");
                    }
                }
                indicatorDetailParagraph.add(table);
                indicatorDetailParagraph.add(Chunk.NEWLINE);
                indicatorDetailParagraph.add(Chunk.NEWLINE);
            }

//            /*
//             * EVALUATOR WEIGHTS GRAPH
//             */
//            if (indicator.isLeaf() && !indicator.getChildIndicators().isEmpty()) {
//                indicatorDetailParagraph.add(new Chunk("Evaluator Weights Graph:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));
//                indicatorDetailParagraph.add(new Chunk("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)));
//
//                // TABLE
//
//                indicatorDetailParagraph.add(Chunk.NEWLINE);
//                indicatorDetailParagraph.add(Chunk.NEWLINE);
//            }
//
//            /*
//             * CHILD WEIGHTS GRAPH
//             */
//            if (!indicator.isLeaf()) {
//                indicatorDetailParagraph.add(new Chunk("Child Weights Graph:\n", FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, green)));
//                indicatorDetailParagraph.add(new Chunk("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD)));
//
//                // TABLE
//
//                indicatorDetailParagraph.add(Chunk.NEWLINE);
//                indicatorDetailParagraph.add(Chunk.NEWLINE);
//            }

            indicatorDetailParagraph.setIndentationLeft(60);
            indicatorDetailParagraph.setFirstLineIndent(-20);
            paragraphs.add(indicatorDetailParagraph);
        }

        return paragraphs;
    }
}
